package no.auke.mg.event.dao;

import no.auke.mg.event.models.EventInfo;
public class EventDao {
	public void storeEvent(EventInfo event) {}
	public EventInfo readEvent(String eventid) {
		return new EventInfo("test");
	}
}
