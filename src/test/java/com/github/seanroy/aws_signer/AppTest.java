package com.github.seanroy.aws_signer;

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
}
