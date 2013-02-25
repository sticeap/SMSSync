package com.sticeap.sms;

public class Data{
	
	public String response;
	public String message;
	public boolean status;
	public String name;
	
	public Data(){
		this.reset();
	}
	
	public void setData(String response, String message, boolean status, String name){
		
		this.response = response;
		this.message = message;
		this.status = status;
		this.name = name;
		
	}

	public Object getData(){
		
		return this;
		
	}
	
	private void reset(){
		this.response = "";
		this.message = "";
		this.status = false;
		this.name = "";
	}
}