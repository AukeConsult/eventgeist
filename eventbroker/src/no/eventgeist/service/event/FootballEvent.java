package no.eventgeist.service.event;

import java.util.List;

import no.eventgeist.service.Event;
import no.eventgeist.service.TimeFrame;
import no.eventgeist.service.TimeSlot;
import no.eventgeist.service.UserSession;

public class FootballEvent extends Event {

	public FootballEvent(String eventid, int timeslot_period) {
		super(eventid,timeslot_period);
	}

	protected void executeResponse(UserSession user, TimeSlot slot) {
		
		List<String> responses = user.readResponses(); 
		for(String response:responses) {
			if (response.startsWith("#C")) {
		    	if(!slot.hits.containsKey(user.getSupport())) {
		    		slot.hits.put(user.getSupport(), 0);
		    	}
		    	slot.hits.put(user.getSupport(), slot.hits.get(user.getSupport()) + 1);
			}
			
//			slot.responses.add("{" + 
//					makeQ("usr") + ":" + makeQ(user.getUserid()) + 
//					"," + makeQ("rsp") + ":" + makeQ(response) + 
//					"," +  makeQ("sup") + ":" + makeQ(user.getSupport()) + 
//					"}") ;
		}
	}
	
	private String makeQ(String value) {
		return "\"" + value + "\"";
	}
	
	@Override
	protected void executeResult(TimeSlot slot) {
		
		String hits="";
		slot.result="";
		
		if(slot.hits.size()>0) {
			
			hits= makeQ("thits") + ": ["; 
			String list="";
			for(String support:slot.hits.keySet()) {
				list += (list.length()>0?",":"") + "{" + 
						makeQ("sup") + ":" + makeQ(support) + "," +  
						makeQ("cnt") + ":" + slot.hits.get(support).toString() + 
						"}";
			}			
			hits += list + "]";
		}

		String resp="";		
		
		/*
		if(slot.responses.size()>0) {
			resp=makeQ("hits") + ": [";
			String list="";
			for(String response:slot.responses) {
				list += (list.length()>0?",":"") + response;
			}
			resp += list + "]";
		}
		*/
		
		if(resp.length()>0 || hits.length()>0) {			
			slot.result = "{ " + 
					makeQ("evt") + ":" + makeQ(getEventid()) + "," +
					makeQ("pos") + ":" + String.valueOf(slot.currentpos) +
					(hits.length()>0?","+hits:"") + 
					(resp.length()>0?","+resp:"") + 
					"}";
		}		
		slot.responses.clear();

	}
	
	@Override
	protected TimeSlot newTimeSlot() {return new TimeSlot();}	

}
