/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vamosjuntos.juntos;

import java.util.Date;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

/**
 * Class that holds information about a calendar.
 * 
 * @author Yaniv Inbar
 */
class CalendarInfo implements Comparable<CalendarInfo>, Cloneable {

	static final String FIELDS = "id, summary, description, location, start, end";
	static final String FEED_FIELDS = "items(" + FIELDS + ")";

	String id;
	String title;
	String description;
	String location;
	EventDateTime startTime;
	EventDateTime endTime;

	CalendarInfo(String id, String summary, String description, String location, EventDateTime startTime, EventDateTime endTime){
		this.id = id;
		this.title = summary;
		this.description = description;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	CalendarInfo(Event e){
		update(e);
	}

	void update(Event e) {
		id = e.getId();
		title = e.getSummary();
		description = e.getDescription();
		location = e.getLocation();
		startTime = e.getStart();
		endTime = e.getEnd();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:\t"+id+"\n");
		sb.append("description:\t"+description+"\n");
		sb.append("location:\t"+location+"\n");
		sb.append("start:\t"+startTime+"\n");
		sb.append("end:\t"+endTime+"\n");
		return sb.toString();
	}

	public int compareTo(CalendarInfo other) {
		if(startTime != null && endTime != null){
			Date thisStart, otherStart;
			boolean thisAllDayEvent = startTime.getDateTime()== null;
			boolean otherAllDayEvent = other.startTime.getDateTime() == null;
			if(thisAllDayEvent){
				thisStart = new Date(startTime.getDate().getValue());
			} else {
				thisStart = new Date(startTime.getDateTime().getValue());
			}

			if(otherAllDayEvent){
				otherStart = new Date(other.startTime.getDate().getValue());
			} else {
				otherStart = new Date(other.startTime.getDateTime().getValue());
			}

			if(thisStart.before(otherStart)) return -1;
			else if(thisStart.after(otherStart)) return 1;
			else return 0;
		} else {
			return id.compareTo(other.id);
		}
	}

	@Override
	public CalendarInfo clone() {
		try {
			return (CalendarInfo) super.clone();
		} catch (CloneNotSupportedException exception) {
			// should not happen
			throw new RuntimeException(exception);
		}
	}
}
