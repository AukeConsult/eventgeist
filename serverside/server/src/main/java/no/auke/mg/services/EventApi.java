package no.auke.mg.services;

import no.auke.mg.channel.models.EventInfo;

public class EventApi {

	public Storage storage = Storage.instance;

	public EventInfo create(String eventid) {

		if(!storage.hasEventInfo(eventid)) {
			EventInfo info = EventInfo.create(eventid);
			storage.saveEventInfo(info);
			storage.doSave();
			return info;
		} else {
			return null;
		}

	}

	public EventInfo info(String eventid) {
		return storage.getEventInfo(eventid);
	}
	public EventInfo update(EventInfo event) {
		return storage.saveEventInfo(event);
	}

}
