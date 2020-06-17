package no.auke.mg.channel.models;

public class ChannelStatus extends PersistObject {

	private String channelid;
	private long starttime;
	private long currenttime = System.currentTimeMillis();
	private long hits=0;
	private long currentpos=0;
	private int slotTime;
	private int timeframes;
	private int usersessions;

	public String getChannelid() {return channelid;}
	public long getStarttime() {return starttime;}
	public long getCurrenttime() {return currenttime;}
	public long getHits() {return hits;}
	public long getCurrentpos() {return currentpos;}
	public int getSlotTime() {return slotTime;}
	public int getTimeframes() {return timeframes;}
	public int getUsersessions() {return usersessions;}

	public void setChannelid(String channelid) {this.channelid = channelid;}
	public void setStarttime(long starttime) {this.starttime = starttime;}
	public void setCurrentpos(long currentpos) {this.currentpos = currentpos;}
	public void setSlotTime(int slotTime) {this.slotTime = slotTime;}
	public void setTimeframes(int timeframes) {this.timeframes = timeframes;}
	public void setUsersessions(int usersessions) {this.usersessions = usersessions;}
	public void setCurrenttime(long currenttime) {this.currenttime = currenttime;}
	public void setHits(long hits) {this.hits = hits;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	@Override
	public String getPersistName() {return getChannelid();}
	@Override
	public String getPersistDir() {return "";}

}
