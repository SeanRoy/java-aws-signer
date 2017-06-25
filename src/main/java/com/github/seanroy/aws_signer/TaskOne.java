package com.github.seanroy.aws_signer;

import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TaskOne extends SigningTask {
    public static final String HashAlgorithm = "SHA-256";
    private URI uri;
    private String HTTPRequestMethod;
    private String CanonicalURI;
    private String CanonicalQueryString;
    private String CanonicalHeaders;
    private String SignedHeaders;
    private String RequestPayload;
    
    public TaskOne(String date) {
    	super(date);
    }
    
    public TaskOne(Date date) {
    	super(date);
    }
    
    public String getHTTPRequestMethod() {
        return HTTPRequestMethod;
    }

    public void setHTTPRequestMethod(String hTTPRequestMethod) {
        HTTPRequestMethod = hTTPRequestMethod;
    }
    
    public TaskOne withHTTPRequestMethod(String hTTPRequestMethod) {
        setHTTPRequestMethod(hTTPRequestMethod);
        return this;
    }  
    
    public void setURL(String url) {
        try {
            uri = new URI(url);
            CanonicalURI = uri.getPath();
            
            String queryString = uri.getQuery();
            
            Map<String,String> queryPieces = new HashMap<String,String>();
            
            Stream.of(queryString.split("&")).forEach(seg -> {
                String [] keyAndValue = seg.split("=");
                queryPieces.put(keyAndValue[0], keyAndValue.length == 2 ? keyAndValue[1] : (""));
            });
            
            CanonicalQueryString = queryPieces.keySet().stream().sorted().map(x -> {
                return String.format("%s=%s",x, queryPieces.get(x));
            }).collect(Collectors.joining("&"));           
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public TaskOne withURL(String url) {
        setURL(url);  
        return this;
    }
    
    public void setHeaders(String [] headers) {
        Map<String,String> kAndV = new HashMap<String,String>();
        kAndV.put("host", uri.getHost().trim().toLowerCase());
        kAndV.put("x-amz-date", date);
        
        Stream.of(headers).forEach(header -> {
            String [] pieces = header.trim().replaceAll("\\s+", " ").split(":");
            kAndV.put(pieces[0].trim().toLowerCase(), pieces[1].trim());
        });
        
        CanonicalHeaders = kAndV.keySet().stream().sorted().map(key -> {
           return String.format("%s:%s", key, kAndV.get(key)); 
        }).reduce("", (a,b) -> a + b + "\n");
        
        SignedHeaders = kAndV.keySet().stream().sorted().collect(Collectors.joining(";")) + "\n";
    }
    public TaskOne withHeaders(String [] headers) {
        setHeaders(headers);
        return this;
    }

    public void setRequestPayload(String requestPayload) {
        RequestPayload = requestPayload;
    }
    
    public TaskOne withRequestPayload(String requestPayload) {
        setRequestPayload(requestPayload);
        return this;
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
    
    public String getCanonicalRequest() {
        return
                HTTPRequestMethod + '\n' +
                CanonicalURI + '\n' +
                CanonicalQueryString + '\n' +
                CanonicalHeaders + '\n' +
                SignedHeaders +
                toHex(hash(RequestPayload));
    }
    
    public String getHashedCanonicalRequest() {
        return toHex(hash(getCanonicalRequest())).toLowerCase();
    }
    
    public static void main(String [] args) {
        String url = "https://iam.amazonaws.com/?Action=ListUsers&Version=2010-05-08";
        String [] headers = {
                "Content-Type: application/x-www-form-urlencoded; charset=utf-8"};
        
        TaskOne t = new TaskOne("20150830T123600Z")
            .withHTTPRequestMethod("GET")
            .withURL(url)
            .withRequestPayload("")
            .withHeaders(headers);
        
        //System.out.println(t.getCanonicalRequest());
        //System.out.println(t.getHashedCanonicalRequest());
        
        String stringToSign = TaskTwo.getStringToSign("20150830T123600Z", "us-east-1", "iam", t.getHashedCanonicalRequest());
        
        System.out.println(stringToSign);
    }
}
