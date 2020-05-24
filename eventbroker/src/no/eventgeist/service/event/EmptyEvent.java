package no.eventgeist.service.event;

import no.eventgeist.service.EventServer;
import no.eventgeist.service.EventTimeFrame;
import no.eventgeist.service.TimeSlot;
import no.eventgeist.service.UserSession;

public class EmptyEvent extends EventServer {

	public EmptyEvent(String eventid) {
		super(eventid);
	}

	protected void executeResponse(UserSession user, TimeSlot slot) {
		for(String response:user.readResponses()) {
	    	slot.result += response + ";";
		}
	}

	@Override
	protected void executeResult(TimeSlot slot) {
		String resp="";
		for(String response:slot.responses) {
			resp += response;
		}
		if(!resp.equals("")) {
			slot.result += "#RR:" + resp;
		}
	}

}
