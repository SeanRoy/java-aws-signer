package com.github.seanroy.aws_signer;

import java.util.Date;

public class TaskTwo extends SigningTask {
	private String region;
	private String service;
	private String hashedCanonicalRequest;
	private static final String ALGORITHM = "AWS4-HMAC-SHA256";
	
	public TaskTwo(String date, String region, String service, String hashedCanonicalRequest) {
		super(date);
	}
	public TaskTwo(Date date, String region, String service, String hashedCanonicalRequest) {
		super(date);
	}
	
	public static String getStringToSign(String date, String region, String service, String hashedCanonicalRequest) {
		return String.format("%s\n%s\n%s\n%s",
				ALGORITHM,
				date,
				String.format("%s/%s/%s/aws4_request", date.substring(0,8), region.toLowerCase(), service.toLowerCase()),
				hashedCanonicalRequest);
	}
	public static String getStringToSign(Date date, String region, String service, String hashedCanonicalRequest) {
		return getStringToSign(dateFormat.format(date), region, service, hashedCanonicalRequest);
	}
}
