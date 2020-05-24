package no.eventgeist.service.event;

import no.eventgeist.service.Event;
import no.eventgeist.service.TimeSlot;
import no.eventgeist.service.UserSession;

public class EmptyEvent extends Event {

	public EmptyEvent(String eventid, int timeslot_period) {
		super(eventid, timeslot_period);
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

	@Override
	protected TimeSlot newTimeSlot() {return new TimeSlot();}

}
