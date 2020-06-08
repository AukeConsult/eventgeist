package no.auke.mg.services;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.auke.mg.event.ResultSlot;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.EventStatus;
import no.auke.mg.event.models.Team;

public abstract class Storage {

	protected Map<String,EventInfo> events = new ConcurrentHashMap<String,EventInfo>();
	protected Map<String,EventStatus> eventstatuses = new ConcurrentHashMap<String,EventStatus>();

	protected Queue<EventInfo> save_events = new ConcurrentLinkedQueue<EventInfo>();
	protected Queue<EventStatus> save_eventstatuses = new ConcurrentLinkedQueue<EventStatus>();
	protected Queue<ResultSlot> save_slots = new ConcurrentLinkedQueue<ResultSlot>();

	public EventInfo getEvent(String eventid) {

		if(!events.containsKey(eventid)) {

			EventInfo info = readEvent(eventid);
			if(info==null) {

				info = new EventInfo(eventid);

				info.setType("football");

				info.setTimeslot_period(2000);
				info.setAvg1time(1000*15);
				info.setEventname("Navnet er " + eventid);

				Calendar cal = Calendar.getInstance();
				info.setEventstart(cal.getTime());
				cal.add(Calendar.HOUR, 3);
				info.setEventstop(cal.getTime());

				info.getTeams().put("team1", new Team("team1","dette er team 1",""));
				info.getTeams().put("team2", new Team("team2","dette er team 2",""));
				info.getProps().put("bilde", null);
				info.getProps().put("kampfakta", "sasfasdasd");
				info.getProps().put("osv1", "osv");
				info.getProps().put("osv2", "osv");

			}

			events.put(info.getEventid(), info);
			saveEvent(info);
		}
		return events.get(eventid);
	}

	public void saveEvent(EventInfo eventinfo) {
		events.put(eventinfo.getEventid(), eventinfo);
		save_events.add(eventinfo);
	}

	public void saveEventStatus(EventStatus status) {
		eventstatuses.put(status.getEventid(), status);
		save_eventstatuses.add(status);
	}

	public void saveResultSlot(ResultSlot slot) {
		save_slots.add(slot);
		if(save_slots.size()>1000) {
			save_slots.poll();
		}
	}
	public ResultSlot getSlot(String eventid, int slotpos) {
		List<ResultSlot> list = readSlots(eventid,slotpos);
		return list!= null?list.get(0):null;
	}

	public abstract void doSave();
	public abstract void readAll();
	public abstract EventInfo readEvent(String eventid);
	public abstract List<ResultSlot> readSlots(String eventid, int slotpos);


}