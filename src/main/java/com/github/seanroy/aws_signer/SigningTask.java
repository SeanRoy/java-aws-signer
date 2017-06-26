package com.github.seanroy.aws_signer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;


public abstract class SigningTask {
    public static final String HashAlgorithm = "SHA-256";
	public static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	static {
	    dateFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTIC"));
	}

	protected String date;
	protected String service;
	
	public SigningTask() {}
	
	public SigningTask(String date, String service) {
    	setDate(date);
    	this.service = service.toLowerCase();
    }
    public SigningTask(Date date, String service) {
    	setDate(date);
    	this.service = service.toLowerCase();
    }
    
    private void setDate(String dateStr) {
    	date = dateStr;
    }
    private void setDate(Date date) {
    	this.date = dateFormat.format(date);
    }
    
    public static byte [] hash(String payload) {
        try {
            return MessageDigest.getInstance(HashAlgorithm).digest(payload.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return payload.getBytes();
    }
    
    public static String toHex(byte [] bytes) {
        try {
            return String.format("%040x", new BigInteger(1, bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new String(bytes);
    }
}
