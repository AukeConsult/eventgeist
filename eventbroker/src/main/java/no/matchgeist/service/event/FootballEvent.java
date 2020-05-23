package no.matchgeist.service.event;

import no.eventgeist.service.EventServer;
import no.eventgeist.service.EventTimeFrame;
import no.eventgeist.service.TimeSlot;
import no.eventgeist.service.UserSession;

public class FootballEvent extends EventServer {

	public FootballEvent(String eventid) {
		super(eventid);
	}

	protected void executeResponse(UserSession user, TimeSlot slot) {
		for(String response:user.readResponses()) {
	    	slot.resultWork += response + ";";
	    	if(!slot.hits.containsKey(user.getSupport())) {
	    		slot.hits.put(user.getSupport(), 0);
	    	}
	    	slot.hits.put(user.getSupport(), slot.hits.get(user.getSupport()) + 1);
		}
	}
	
	@Override
	protected void executeResult(TimeSlot slot) {
		String hits=""; 
		for(String support:slot.hits.keySet()) {
			slot.resultWork += "#HH:" + support + ":" + slot.hits.get(support).toString();
		}
		if(!hits.equals("")) {
			slot.resultWork +=hits;
		}
		String resp="";
		for(String response:slot.responses) {
			resp += response;
		}
		if(!resp.equals("")) {
			slot.resultWork += "#RR:" + resp;
		}

	}

}
