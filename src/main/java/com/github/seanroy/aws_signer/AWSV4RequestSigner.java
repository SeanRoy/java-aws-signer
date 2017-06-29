package com.github.seanroy.aws_signer;

import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

/**
 * Utility method that will sign an AWS request, setting the authorization header with the proper signature as well as the
 * x-amz-date and host headers.
 *
 */
public class AWSV4RequestSigner 
{
    public static HttpRequestBase signAWSRequest(HttpRequestBase request, String region, String service, String data, 
                                                 List<String> headers) throws Exception {
        AWSCredentialsProviderChain defaultChain = DefaultAWSCredentialsProviderChain.getInstance();
        URI uri = request.getURI();
        String timeStamp = SigningTask.dateFormat.format(new Date());
        
        headers = ofNullable(headers).orElse(new ArrayList<String>());
        
        if ( data != null && !data.isEmpty() ) {
            headers.add("Content-Type:application/json");
        }
        
        String authHeader = TaskFour.getAuthorizationHeader(defaultChain.getCredentials(), request.getMethod(), 
                timeStamp, uri.toURL().toString(), data, headers.toArray(new String[headers.size()]), region, service);
        request.addHeader("host", uri.getHost() );
        request.addHeader("x-amz-date", timeStamp);
        request.addHeader("Authorization", authHeader);
        headers.stream().forEach(hdr -> {
            String [] kAndV = hdr.split(":"); 
            request.addHeader(kAndV[0], kAndV.length > 1 ? kAndV[1] : "");
        });
        
        return request;
    }
    
    public static HttpURLConnection signAWSRequest(String urlString, String method, String region, String service, 
                                                   String data, List<String> headers) throws Exception {
        AWSCredentialsProviderChain defaultChain = DefaultAWSCredentialsProviderChain.getInstance();
        URI uri = new URI(urlString);
        String timeStamp = SigningTask.dateFormat.format(new Date());
        
        headers = ofNullable(headers).orElse(new ArrayList<String>());
        
        if ( data != null && !data.isEmpty() ) {
            headers.add("Content-Type:application/json");
        }
               
        String authHeader = TaskFour.getAuthorizationHeader(defaultChain.getCredentials(), method, 
                timeStamp, uri.toURL().toString(), data, headers.toArray(new String [headers.size()]), region, service);
        
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("host", uri.getHost());
        connection.setRequestProperty("x-amz-date", timeStamp);
        connection.setRequestProperty("Authorization", authHeader);
        headers.stream().forEach(hdr -> {
           String [] kAndV = hdr.split(":"); 
           connection.setRequestProperty(kAndV[0], kAndV.length > 1 ? kAndV[1] : "");
        });
        
        if ( data != null && !data.isEmpty()) {
            connection.setDoOutput(true);
           
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(data);
            bw.flush();
            bw.close();
        }
        
        return connection;
    }
    
    public static void main(String [] args) {
        javaStyle();
    }
    public static void apacheStyle() {
        try {
            String url = "https://apigateway.us-east-1.amazonaws.com/restapis";
            String body = 
                    "{" + 
                    "  'name': 'SEAN_TEST', " +
                    "  'description': 'JUST A TEST' " +
                    "}";


            
            HttpRequestBase request = AWSV4RequestSigner.signAWSRequest(new HttpPost(url), "us-east-1", "apigateway", body, null);
           
            CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpResponse response = httpclient.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                        String x = null;
                        while(( x = br.readLine() ) != null ) {
                            System.out.println(x);
                        }
                    } finally {
                        instream.close();
                    }
                }
            } finally {
                response.close();
            }
            
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    public static void javaStyle() {    
        String url = "https://apigateway.us-east-1.amazonaws.com/restapis";
        String body = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", "my-test-api", "This is a description");

        String [] headers = {
                "content-type:x-amz-json-1.0",
                "content-length:" + body.length()
        };
        
                
        try {
            HttpURLConnection conn = AWSV4RequestSigner.signAWSRequest(url, "POST", "us-east-1", "apigateway", body, null);
            System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
            

            BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
