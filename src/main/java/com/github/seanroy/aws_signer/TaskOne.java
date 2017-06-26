package com.github.seanroy.aws_signer;

import static java.util.Optional.ofNullable;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
        return toHex(hash(getCanonicalRequest())).toLowerCase();
    }
}
