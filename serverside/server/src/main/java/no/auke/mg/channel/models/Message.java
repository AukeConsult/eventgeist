package no.auke.mg.channel.models;

public class Message {

	private String t;
	public String getT() {return t;}

	private String uid;
	public String getUid() {return uid;}

	private int id;
	public int getId() {return id;}

	private int dl;
	public int getDl() {return dl;}

	private String js;
	public String getJs() {return js;}

	public Message(String type, String userid, int id, int delay, String js) {
		this.t=type;
		this.uid=userid;
		this.id=id;
		this.dl=delay;
		this.js = js;
	}

}
