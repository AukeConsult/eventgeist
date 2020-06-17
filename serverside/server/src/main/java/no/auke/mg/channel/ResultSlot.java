// result for each timeslot of channel
package no.auke.mg.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.channel.feedbacks.FeedBack;
import no.auke.mg.channel.models.PersistObject;

public class ResultSlot extends PersistObject {

	public String channelid;
	public String eventid;

	public long pos;
	public long time = System.currentTimeMillis();

	public boolean isresult=false;

	public FeedBack feedback;

	public Map<String, ResponseTeam> teams = new HashMap<String, ResponseTeam>();

	public List<Message> msglist = new ArrayList<Message>();
	public Message addMessage(String type, String userid, int delay, String js) {
		Message msg = new Message(type, userid, msglist.size(), delay, js);
		msglist.add(msg);
		return msg;
	}

	public List<Status> statuslist= new ArrayList<Status>();
	public Status addStatus(String type, String userid, String js) {
		Status sts = new Status(type, userid, js);
		statuslist.add(sts);
		return sts;
	}

	@Override
	public String getPersistName() {
		return String.valueOf(pos);
	}

	@Override
	public String getPersistDir() {
		return channelid + "/" + String.valueOf(pos / 1000);
	}

}
