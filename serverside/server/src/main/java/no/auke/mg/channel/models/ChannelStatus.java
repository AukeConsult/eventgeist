package no.auke.mg.channel.models;

public class ChannelStatus {

	private String channelid;
	private long starttime;
	private long currenttime = System.currentTimeMillis();
	private long hits=0;
	private int currentpos=0;
	private int timeslot_period=5000;
	private int timeframes;
	private int usersessions;

	public String getChannelid() {return channelid;}
	public long getStarttime() {return starttime;}
	public long getCurrenttime() {return currenttime;}
	public long getHits() {return hits;}
	public int getCurrentpos() {return currentpos;}
	public int getTimeslot_period() {return timeslot_period;}
	public int getTimeframes() {return timeframes;}
	public int getUsersessions() {return usersessions;}

	public void setChannelid(String eventid) {this.channelid = eventid;}
	public void setStarttime(long starttime) {this.starttime = starttime;}
	public void setCurrentpos(int currentpos) {this.currentpos = currentpos;}
	public void setTimeslot_period(int timeslot_period) {this.timeslot_period = timeslot_period;}
	public void setTimeframes(int timeframes) {this.timeframes = timeframes;}
	public void setUsersessions(int usersessions) {this.usersessions = usersessions;}
	public void setCurrenttime(long currenttime) {this.currenttime = currenttime;}
	public void setHits(long hits) {this.hits = hits;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

}
