package no.auke.mg.event.dom;

public class Note {

	private String eventid;
	public String getEventid() {return eventid;}

	private int noteid;
	public int getNoteid() {return noteid;}

	private int delay;
	public int getDelay() {return delay;}

	public Note(String eventid, int noteid, int delay) {
		this.eventid=eventid;
		this.noteid=noteid;
		this.delay=delay;
	}
}
