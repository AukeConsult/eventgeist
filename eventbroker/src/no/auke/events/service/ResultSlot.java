// result for each timeslot of event
package no.auke.events.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSlot {

	public int currentpos;
	public long currenttime = System.currentTimeMillis();
	public boolean isresult=false;
	public String resultString;
	
	public Map<String, Integer> hits = new HashMap<String, Integer>();	
	public List<String> responses = new ArrayList<String>();	
	public ResultSlot() {}
	
	public Object resultObject;

}
