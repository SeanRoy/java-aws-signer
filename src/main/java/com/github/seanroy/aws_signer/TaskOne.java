package com.github.seanroy.aws_signer;

import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Optional.ofNullable;


public class TaskOne extends SigningTask {
    private URI uri;
    private String HTTPRequestMethod;
    private String CanonicalURI;
    private String CanonicalQueryString = "";
    private String CanonicalHeaders;
    private String SignedHeaders;
    private String RequestPayload;
    
    public TaskOne(String date, String service) {
    	super(date, service);
    }
    
    public TaskOne(Date date, String service) {
    	super(date, service);
    }
    
    public String getSignedHeaders() {
        return SignedHeaders;
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
            // Do not normalize URI's for S3.
            uri = service == "s3" ? new URI(url) : new URI(url).normalize();
            CanonicalURI = uri.getPath().isEmpty() ? "/" : uri.getPath();
            
            ofNullable(uri.getQuery()).ifPresent( queryString -> {
                Map<String,String> queryPieces = new HashMap<String,String>();
                            
                Stream.of(queryString.split("&")).forEach(seg -> {
                    String [] keyAndValue = seg.split("=");
                    queryPieces.put(keyAndValue[0], keyAndValue.length == 2 ? keyAndValue[1] : (""));
                });
                
                CanonicalQueryString = queryPieces.keySet().stream().sorted().map(x -> {
                    return String.format("%s=%s",x, queryPieces.get(x));
                }).collect(Collectors.joining("&"));           
            });
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public TaskOne withURL(String url) {
        setURL(url);  
        return this;
    }
    
    public void setHeaders(String ... hdrs) {
        Map<String,String> kAndV = new HashMap<String,String>();
        kAndV.put("host", uri.getHost().trim().toLowerCase());
        kAndV.put("x-amz-date", date);
        
        ofNullable(hdrs).ifPresent( headers -> {
            Stream.of(headers).forEach(header -> {
                String [] pieces = header.trim().replaceAll("\\s+", " ").split(":");
                kAndV.put(pieces[0].trim().toLowerCase(), pieces[1].trim());
            });
        });
        
        CanonicalHeaders = kAndV.keySet().stream().sorted().map(key -> {
           return String.format("%s:%s", key, kAndV.get(key)); 
        }).reduce("", (a,b) -> a + b + "\n");
        
        SignedHeaders = kAndV.keySet().stream().sorted().collect(Collectors.joining(";")) + "\n";
    }
    public TaskOne withHeaders(String ... headers) {
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
        System.err.println(getCanonicalRequest());
        return toHex(hash(getCanonicalRequest())).toLowerCase();
    }
    
    public static void main(String [] args) {
        String url = "https://apigateway.us-east-1.amazonaws.com";
        
        String [] headers = {};
        
        TaskOne t = new TaskOne("20170626T212030Z", "apigateway")
            .withHTTPRequestMethod("GET")
            .withURL(url)
            .withRequestPayload("")
            .withHeaders(headers);
        
        System.out.println(t.getCanonicalRequest().equals("GET\n/\n\nhost:apigateway.us-east-1.amazonaws.com\nx-amz-date:20170626T212030Z\n\nhost;x-amz-date\ne3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"));
        //System.out.println(t.getHashedCanonicalRequest());
        
        
        /**
         * The request signature we calculated does not match the signature you provided. Check your AWS Secret Access Key and signing method. Consult the service documentation for details.\n\n
         * The Canonical String for this request should have been\n'
         * GET\n/\n\nhost:apigateway.us-east-1.amazonaws.com\nx-amz-date:20170626T212030Z\n\nhost;x-amz-date\ne3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855'\n\n
         * The String-to-Sign should have been\n'AWS4-HMAC-SHA256\n20170626T212030Z\n20170626/us-east-1/apigateway/aws4_request\nb6dbbab3f5be0bc4aa1b9088799b6167e9440575667f31f32a8e0844b6eac6b8'\n"}
         */
        
        String stringToSign = TaskTwo.getStringToSign("20170626T212030Z", "us-east-1", "apigateway", t.getHashedCanonicalRequest());
        
        System.out.println(stringToSign.equals("AWS4-HMAC-SHA256\n20170626T212030Z\n20170626/us-east-1/apigateway/aws4_request\nb6dbbab3f5be0bc4aa1b9088799b6167e9440575667f31f32a8e0844b6eac6b8"));
        System.out.println("---");
        System.out.println("AWS4-HMAC-SHA256\n20170626T210432Z\n20170626/us-east-1/apigateway/aws4_request\n92aa4fcf6983fd270cfaf26b21ee3d74affa6dd097681b8bf6499867a9e01e8d");
    }
}
