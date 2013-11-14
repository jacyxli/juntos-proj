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

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread-safe model for the Google calendars.
 * 
 * @author Yaniv Inbar
 */
class CalendarModel {

	private final Map<String, CalendarInfo> events = new HashMap<String, CalendarInfo>();

	int size(){
		synchronized (events) {
			return events.size();
		}
	}

	void remove(String id) {
		synchronized (events) {
			events.remove(id);
		}
	}

	CalendarInfo get(String id) {
		synchronized (events) {
			return events.get(id);
		}
	}

	void add(Event event2add) {
		synchronized (events) {
			CalendarInfo found = get(event2add.getId());
			if (found == null) {
				events.put(event2add.getId(), new CalendarInfo(event2add));
			} else {
				found.update(event2add);
			}
		}
	}

	void reset(List<Event> events2add) {
		synchronized (events) {
			events.clear();
			Date d = new Date();
			EventDateTime end;
			Date endTime;

			for (Event event2add : events2add) {
				end = event2add.getEnd();
				if(end.getDateTime() != null) {
					endTime = new Date(end.getDateTime().getValue());
				} else {
					endTime = new Date(end.getDate().getValue());
				}
				if(endTime.after(d)){
					add(event2add);
				}
			}
		}
	}

	public CalendarInfo[] toSortedArray() {
		synchronized (events) {
			List<CalendarInfo> result = new ArrayList<CalendarInfo>();
			for (CalendarInfo calendar : events.values()) {
				result.add(calendar.clone());
			}
			Collections.sort(result);
			return result.toArray(new CalendarInfo[0]);
		}
	}  
}
