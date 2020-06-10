// result for each timeslot of channel
package no.auke.mg.channel;

import java.util.ArrayList;
import java.util.List;

import no.auke.mg.channel.feedbacks.FeedBack;
import no.auke.mg.channel.models.Message;
import no.auke.mg.channel.models.Status;

public class ResultSlot {

	public ResultSlot() {}

	public String channelid;
	public String eventid;

	public long pos;
	public long currenttime = System.currentTimeMillis();
	public boolean isresult=false;
	public FeedBack feedback;

	public List<Message> msglist = new ArrayList<Message>();
	public Message addMessage(String type, String userid, int delay, String js) {
		Message msg = new Message(type, userid, msglist.size(), delay, js);
		msglist.add(msg);
		return msg;
	}

	public List<Status> stlist= new ArrayList<Status>();
	public Status addStatus(String type, String userid, String js) {
		Status sts = new Status(type, userid, js);
		stlist.add(sts);
		return sts;
	}

}
