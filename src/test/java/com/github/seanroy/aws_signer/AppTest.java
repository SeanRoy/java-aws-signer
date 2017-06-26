package com.github.seanroy.aws_signer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.amazonaws.auth.BasicAWSCredentials;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIDEXAMPLE", "wJalrXUtnFEMI/K7MDENG+bPxRfiCYEXAMPLEKEY");
        String method = "GET";
        String timeStamp = "20150830T123600Z";
        String url = "https://example.amazonaws.com//example1/example2/../..";
        String data = "";
        String [] headers = null;
        try {
            String authHeader = TaskFour.getAuthorizationHeader(credentials, method, timeStamp, url, data, headers, "us-east-1", "service");
            
            assertEquals(authHeader, "AWS4-HMAC-SHA256 Credential=AKIDEXAMPLE/20150830/us-east-1/service/aws4_request, SignedHeaders=host;x-amz-date, Signature=5fa00fa31553b73ebf1942676e86291e8372ff2a2260956d9b8aae1d763fbf31");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void testSigner() {
        try {
            String url = "https://apigateway.us-east-1.amazonaws.com";

            String [] headers = null;
            
            HttpRequestBase request = AWSV4RequestSigner.signAWSRequest(new HttpGet(url), "us-east-1", "apigateway", "", headers);
           
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
        
        assertTrue(true);
    }
}
