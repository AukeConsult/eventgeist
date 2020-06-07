package no.auke.mg.eventimpl.basic;

import no.auke.mg.event.EventService;
import no.auke.mg.event.Monitor;
import no.auke.mg.event.Storage;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.ResultSlot;
import no.auke.mg.event.models.Team;

public class BasicEvent extends EventService {

	public BasicEvent(EventInfo eventinfo, Monitor monitor,Storage storage) {
		super(eventinfo, monitor,storage);
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

	@Override
	protected void initTeam(Team team) {
		// TODO Auto-generated method stub

	}

}
