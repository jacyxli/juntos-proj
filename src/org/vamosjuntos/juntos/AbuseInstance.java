package org.vamosjuntos.juntos;

import java.util.Date;

public class AbuseInstance {

	private Date date; 
	private String location, description;
	private String person, type;
	
	public AbuseInstance(Date _date, String _name, String _type, String _location, String _description) {
		if (_location == null) location = "Unknown Location";
		else location = _location;
		description = _description;
		date = _date;
		if (_name == null) person = "Anonymous";
		else person = _name;
		type = _type;
	}
	
	public Date getDate() { return date; }
	public String getLocation() { return location; }
	public String getDescription() { return description; }

	public String getPerson() { return person; }

	public String getType() { return type; }

	
}