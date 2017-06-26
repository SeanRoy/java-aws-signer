package com.github.seanroy.aws_signer;

import java.net.URI;
import java.util.Date;

import org.apache.http.client.methods.HttpRequestBase;

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
                                                 String [] headers) throws Exception {
        AWSCredentialsProviderChain defaultChain = DefaultAWSCredentialsProviderChain.getInstance();
        URI uri = request.getURI();
        String timeStamp = SigningTask.dateFormat.format(new Date());
        String authHeader = TaskFour.getAuthorizationHeader(defaultChain.getCredentials(), request.getMethod(), 
                timeStamp, uri.toURL().toString(), data, headers, region, service);
        request.addHeader("host", uri.getHost() );
        request.addHeader("x-amz-date", timeStamp);
        request.addHeader("Authorization", authHeader);
        
        return request;
    }
}
