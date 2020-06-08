// result for each timeslot of event
package no.auke.mg.event;

import java.util.ArrayList;
import java.util.List;

import no.auke.mg.event.feedbacks.FeedBack;
import no.auke.mg.event.models.Message;
import no.auke.mg.event.models.Status;

public class ResultSlot {

	public ResultSlot() {}
	public int currentpos;
	public long currenttime = System.currentTimeMillis();
	public boolean isresult=false;

	//public Map<String, Integer> hits = new HashMap<String, Integer>();

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
