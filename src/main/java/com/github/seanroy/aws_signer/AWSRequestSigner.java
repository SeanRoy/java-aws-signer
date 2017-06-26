package com.github.seanroy.aws_signer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

/**
 * Hello world!
 *
 */
public class AWSRequestSigner 
{
    
    

    public static void main( String[] args )
    {
        AWSCredentialsProviderChain defaultChain = DefaultAWSCredentialsProviderChain.getInstance();
        try {
            String url = "https://apigateway.us-east-1.amazonaws.com";
            URI uri = new URI(url);
            String timeStamp = SigningTask.dateFormat.format(new Date());
            
            String [] headers = null;
            
            HttpRequestBase request = new HttpGet(url);
            String authHeader = TaskFour.getAuthorizationHeader(defaultChain.getCredentials(), request.getMethod(), timeStamp, url, "", headers, "us-east-1", "apigateway");
            request.addHeader("host", uri.getHost() );
            request.addHeader("x-amz-date", timeStamp);
            request.addHeader("Authorization", authHeader);
           
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
}
