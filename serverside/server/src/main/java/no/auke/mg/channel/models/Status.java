package no.auke.mg.channel.models;

public class Status {

	private String t;
	public String getT() {return t;}

	private String uid;
	public String getUid() {return uid;}

	private String js;
	public String getJs() {return js;}

	public Status(String type, String userid, String js) {
		this.t=type;
		this.uid=userid;
		this.js = js;
	}

}
