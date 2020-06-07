package no.auke.mg.event.dom;

public class Message {

	private String eventid;
	public String getEventid() {return eventid;}

	private String userid;
	public String getUsertid() {return userid;}

	private int msgid;
	public int getMsgid() {return msgid;}

	private int slotpos;
	public int getSlotpos() {return slotpos;}

	private int delay;
	public int getDelay() {return delay;}

	private String text;
	public String getText() {return text;}

	public Message(String eventid, String userid, int msgid, int slotpos, int delay, String text) {
		this.eventid=eventid;
		this.userid=userid;
		this.msgid=msgid;
		this.slotpos=slotpos;
		this.delay=delay;
		this.text = text;
	}
}
