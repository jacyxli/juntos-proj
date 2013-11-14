package org.vamosjuntos.juntos;

public class Contact {
	private String name;
	private String title;
	private String email;

	Contact(String name, String title, String email){
		this.name = name;
		this.title = title;
		this.email = email;
	}
	
	public String getName() {return name;}
	
	public String getTitle() {return name;}

	public String getEmail() {return email;}
	
	@Override
	public String toString(){
		return name;
	}
}
