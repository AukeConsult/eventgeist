package no.auke.mg.event;

import java.util.ArrayList;
import java.util.List;

import no.auke.mg.event.dom.Message;

public class MessageService {

	private EventService event;
	private List<Message> msglist = new ArrayList<Message>();
	public MessageService(EventService event) {
		this.event=event;
	}
	public int lastMsgid(int delay) {return msglist.size() - 1;}
	public synchronized int addMessage(String userid, int slotpos, int delay, String message) {
		msglist.add(new Message(event.getEventid(), userid, msglist.size(), slotpos, delay, message));
		return msglist.size() - 1;
	}

}
