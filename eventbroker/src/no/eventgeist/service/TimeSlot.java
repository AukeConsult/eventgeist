// result for each timeslot of event
package no.eventgeist.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSlot {

	public int timepos;
	public String result;
	
	public Map<String, Integer> hits = new HashMap<String, Integer>();	
	public List<String> responses = new ArrayList<String>();	

	public TimeSlot() {}
	public TimeSlot(int timepos) {
		this.timepos=timepos;
	}

}
