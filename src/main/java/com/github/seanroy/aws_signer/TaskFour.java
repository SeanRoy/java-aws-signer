package com.github.seanroy.aws_signer;

import java.util.Date;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public class TaskFour extends SigningTask  {
    public static String getAuthorizationHeader(AWSCredentials credentials, String method, Date timeNow, String url, String payload, String [] headers, String region, String serviceName) throws Exception{
        String date = dateFormat.format(timeNow);
        return getAuthorizationHeader(credentials, method, date, url, payload, headers, region, serviceName);
    }
    public static String getAuthorizationHeader(AWSCredentials credentials, String method, String date, String url, String payload, String [] headers, String region, String serviceName) throws Exception{
        TaskOne t1 = new TaskOne(date, serviceName)
            .withHTTPRequestMethod(method.toUpperCase())
            .withURL(url)
            .withRequestPayload(payload)
            .withHeaders(headers);
        
        String stringToSign = TaskTwo.getStringToSign(date, region, serviceName, t1.getHashedCanonicalRequest());

        String signature = TaskThree.getSignature(
                stringToSign,
                credentials.getAWSSecretKey(),
                date, 
                region, 
                serviceName);
        
        String scope = String.format("%s/%s/%s/aws4_request", date.substring(0,8), region.toLowerCase(), serviceName.toLowerCase());
        
        return String.format("AWS4-HMAC-SHA256 Credential=%s/%s, SignedHeaders=%s, Signature=%s",
                credentials.getAWSAccessKeyId(),
                scope,
                t1.getSignedHeaders().trim(),
                signature);
    }
}
