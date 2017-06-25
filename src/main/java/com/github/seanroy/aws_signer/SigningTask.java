package com.github.seanroy.aws_signer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class SigningTask {
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmSS'Z'");
	protected String date;
	
	public SigningTask() {}
	
	public SigningTask(String date) {
    	setDate(date);
    }
    public SigningTask(Date date) {
    	setDate(date);
    }
    
    public void setDate(String dateStr) {
    	date = dateStr;
    }
    public SigningTask withDate(String dateStr) {
    	setDate(dateStr);
    	return this;
    }
    public void setDate(Date date) {
    	this.date = dateFormat.format(date);
    }
    public SigningTask withDate(Date date) {
    	setDate(date);
    	return this;
    }
}
