package no.auke.mg.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.ChannelStatus;
import no.auke.mg.channel.models.EventInfo;

public abstract class Storage {

	public static Storage instance;

	protected Map<String,EventInfo> events = new ConcurrentHashMap<String,EventInfo>();
	protected Map<String,ChannelInfo> channels = new ConcurrentHashMap<String,ChannelInfo>();
	protected Map<String,Map<Long,ResultSlot>> slots = new HashMap<String,Map<Long,ResultSlot>>();

	protected Queue<Object> save_events = new ConcurrentLinkedQueue<Object>();
	protected Queue<Object> save_channels = new ConcurrentLinkedQueue<Object>();
	protected Queue<Object> save_channelstatuses = new ConcurrentLinkedQueue<Object>();
	protected Queue<Object> save_slots = new ConcurrentLinkedQueue<Object>();

	public ChannelInfo getChannel(String channelid) {
		if(!channels.containsKey(channelid)) {
			ChannelInfo info = readChannelInfo(channelid);
			if(info==null) {
				info = ChannelInfo.create(channelid);
			}
			channels.put(info.getChannelid(), info);
			saveChannelInfo(info);
		}
		return channels.get(channelid);
	}

	public void saveChannelInfo(ChannelInfo channelinfo) {
		channels.put(channelinfo.getChannelid(), channelinfo);
		save_channels.add(channelinfo);
		doSave();
	}

	public void saveChannelStatus(ChannelStatus status) {
		save_channelstatuses.add(status);
		doSave();
	}

	public boolean hasEventInfo(String eventid) {
		return events.containsKey(eventid);
	}
	public EventInfo getEventInfo(String eventid) {
		if(!events.containsKey(eventid)) {
			EventInfo info = readEventInfo(eventid);
			if(info == null) {
				info = EventInfo.create(eventid);
				events.put(info.getEventid(), info);
				saveEventInfo(info);
			}
		}
		return events.get(eventid);
	}

	public EventInfo saveEventInfo(EventInfo event) {
		events.put(event.getEventid(), event);
		save_events.add(event);
		doSave();
		return event;
	}

	public void saveSlot(ResultSlot slot) {
		if(!slots.containsKey(slot.channelid)) {
			slots.put(slot.channelid, new HashMap<Long,ResultSlot>());
		}
		slots.get(slot.channelid).put(slot.pos, slot);
		save_slots.add(slot);
		if(save_slots.size()>1000) {
			save_slots.poll();
		}
	}
	public ResultSlot getSlot(String channelid, long slotpos) {
		if(!slots.containsKey(channelid)) {
			slots.put(channelid, new HashMap<Long,ResultSlot>());
		}
		return slots.get(channelid).get(slotpos);
	}

	public List<ResultSlot> getResultSlots(String channelid) {
		if(slots.containsKey(channelid)) {
			return new ArrayList<ResultSlot>(slots.get(channelid).values());
		} else {
			return new ArrayList<ResultSlot>();
		}
	}

	public abstract void init();
	public abstract void doSave();
	public abstract void readAll();

	public abstract ChannelStatus readChannelStatus(String channelid);
	public abstract ChannelInfo readChannelInfo(String channelid);
	public abstract EventInfo readEventInfo(String eventid);
	public abstract List<ResultSlot> readSlots(String channelid, int slotpos_from, int slotpos_to);

}