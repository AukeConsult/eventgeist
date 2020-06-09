package no.auke.mg.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.ChannelStatus;
import no.auke.mg.channel.models.Team;

public abstract class Storage {

	protected Map<String,ChannelInfo> channel = new ConcurrentHashMap<String,ChannelInfo>();
	protected Map<String,ChannelStatus> channelstatuses = new ConcurrentHashMap<String,ChannelStatus>();

	protected Queue<ChannelInfo> save_channels = new ConcurrentLinkedQueue<ChannelInfo>();
	protected Queue<ChannelStatus> save_channelstatuses = new ConcurrentLinkedQueue<ChannelStatus>();
	protected Queue<ResultSlot> save_slots = new ConcurrentLinkedQueue<ResultSlot>();
	protected Map<String,Map<Long,ResultSlot>> slots = new HashMap<String,Map<Long,ResultSlot>>();

	public ChannelInfo getChannel(String channelid) {

		if(!channel.containsKey(channelid)) {

			ChannelInfo info = readhannel(channelid);
			if(info==null) {

				info = new ChannelInfo(channelid);

				info.setType("football");

				info.setTimeslot_period(2000);
				info.setAvg1time(1000*15);
				info.setName("Navnet er " + channelid);

				Calendar cal = Calendar.getInstance();
				info.setStart(cal.getTime());
				cal.add(Calendar.HOUR, 3);
				info.setStop(cal.getTime());

				info.getTeams().put("team1", new Team("team1","dette er team 1",""));
				info.getTeams().put("team2", new Team("team2","dette er team 2",""));
				info.getProps().put("bilde", null);
				info.getProps().put("kampfakta", "sasfasdasd");
				info.getProps().put("osv1", "osv");
				info.getProps().put("osv2", "osv");

			}

			channel.put(info.getChannelid(), info);
			saveChannel(info);
		}
		return channel.get(channelid);
	}

	public void saveChannel(ChannelInfo channelinfo) {
		channel.put(channelinfo.getChannelid(), channelinfo);
		save_channels.add(channelinfo);
	}

	public void saveChannelStatus(ChannelStatus status) {
		channelstatuses.put(status.getChannelid(), status);
		save_channelstatuses.add(status);
	}

	public void saveSlot(ResultSlot slot) {
		if(!slots.containsValue(slot.channelid)) {
			slots.put(slot.channelid, new HashMap<Long,ResultSlot>());
		}
		slots.get(slot.channelid).put(slot.pos, slot);
		save_slots.add(slot);
		if(save_slots.size()>1000) {
			save_slots.poll();
		}
	}
	public ResultSlot getSlot(String channelid, long slotpos) {
		if(!slots.containsValue(channelid)) {
			slots.put(channelid, new HashMap<Long,ResultSlot>());
		}
		return slots.get(channelid).get(slotpos);
	}

	public List<ResultSlot> getResultSlots() {return new ArrayList<ResultSlot>(save_slots);}

	public abstract void doSave();
	public abstract void readAll();
	public abstract ChannelInfo readhannel(String channelid);
	public abstract List<ResultSlot> readSlots(String channelid, int slotpos);

	public void init() {
		// TODO Auto-generated method stub
	}


}