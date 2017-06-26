package com.github.seanroy.aws_signer;


import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

public class TaskThree extends SigningTask {
	
	
	public TaskThree(String date, String service) {
		super(date, service);
	}
	
	public TaskThree(Date date, String service)  {
		super(date, service);
	}
	
	static byte[] HMAC(byte[] key, String data) throws Exception {
	    String algorithm="HmacSHA256";
	    Mac mac = Mac.getInstance(algorithm);
	    mac.init(new SecretKeySpec(key, algorithm));
	    return mac.doFinal(data.getBytes("UTF8"));
	}

	private static byte[] getSigningKey(String secretKey, String dateStamp, String regionName, String serviceName) throws Exception {
	    byte [] kDate = HMAC(("AWS4" + secretKey).getBytes(), dateStamp.substring(0, 8));
	    byte [] kRegion = HMAC(kDate, regionName);
	    byte [] kService = HMAC(kRegion, serviceName);
	    byte [] kSigning = HMAC(kService, "aws4_request");
	    return kSigning;
	}
	
	static String getSignature(String stringToSign, String secretKey, String dateStamp, String regionName, String serviceName) throws Exception {
	    byte [] signingKey = getSigningKey(secretKey, dateStamp, regionName, serviceName);
	    
	    return toHex(HMAC(signingKey, stringToSign));
	}
}
