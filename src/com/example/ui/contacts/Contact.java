package com.example.ui.contacts;


public class Contact{
	private int id;
	private String sip;
	private String name;
	private String image;
	private String email;
	
	public Contact(){}
	public Contact(String name, String n,String im,String e) {
		this.sip = n;
		this.name = name;
		this.image=im;
		this.email=e;
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
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String e) {
		this.email = e;
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
		return "ID : "+id+"\nName : "+name+"\nSip : "+sip+"\nImage : "+image+"\nEmail : "+email;
	}
	
		}
	