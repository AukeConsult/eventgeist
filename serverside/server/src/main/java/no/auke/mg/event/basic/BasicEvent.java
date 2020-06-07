package no.auke.mg.event.basic;

import no.auke.mg.event.EventMonitor;
import no.auke.mg.event.EventService;
import no.auke.mg.event.ResultSlot;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;

public class BasicEvent extends EventService {

	public BasicEvent(EventInfo eventinfo, EventMonitor monitor) {
		super(eventinfo, monitor);
	}

	private String makeQ(String value) {
		return "\"" + value + "\"";
	}

	@Override
	protected void executeResponse(UserSession user, ResultSlot slot, int time) {
	}

	@Override
	protected void executeResult(ResultSlot slot) {
	}

	@Override
	protected ResultSlot newResultSlot() {return new ResultSlot();}

	@Override
	protected void executeSlotEnd(ResultSlot slot) {

	}

	@Override
	protected void executeSlotStart(TimeFrame timeframe) {
		// TODO Auto-generated method stub

	}

}
