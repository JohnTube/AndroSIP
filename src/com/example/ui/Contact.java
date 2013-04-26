package com.example.ui;


public class Contact{
	private int id;
	private String sip;
	private String name;
	private String image;
	
	public Contact(){}
	public Contact(String n, String name,String im) {
		this.sip = n;
		this.name = name;
		this.image=im;
		}
	public int getId() {
		return id;
	}
 
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSip() {
		return sip;
	}
 
	public void setSip(String s) {
		this.sip = s;
	}
	
	public String getName() {
		return name;
	}
 
	public void setName(String n) {
		this.name =n ;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String im) {
		this.image =im ;
	}
	
	public String toString(){
		return "ID : "+id+"\nName : "+name+"\nSip : "+sip+"\nImage : "+image;
	}
	
		}
	