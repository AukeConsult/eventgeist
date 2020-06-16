package no.auke.mg.channel.models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChannelInfo extends PersistObject {

	private String id;
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}

	private String channelid;
	public String getChannelid() {return channelid;}
	public void setChannelid(String channelid) {this.channelid = channelid;}

	private String type;
	public String getType() {return type;}
	public void setType(String type) {this.type=type;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	private String name;
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	private Date start;
	public Date getStart() {return start;}
	public void setStart(Date start) {this.start = start;}

	private Date stop;
	public Date getStop() {return stop;}
	public void setStop(Date stop) {this.stop = stop;}

	private int slotTime = 2000;
	public int getSlotTime() {return slotTime;}
	public void setSlotTime(int slotTime) {this.slotTime=slotTime;}

	public int avg1Time = 1000*15;
	public int getAvg1Time() {return avg1Time;}
	public void setAvg1Time(int avg1Time) {this.avg1Time=avg1Time;}

	public int avg2Time = 1000*60;
	public int getAvg2Time() {return avg2Time;}
	public void setAvg2Time(int avg2Time) {this.avg2Time=avg2Time;}

	// Process information
	private String server;
	public String getServer() {return server;}
	public void setServer(String server) {this.server = server;}

	private List<String> brokers;
	public List<String> getBrokers() {return brokers;}
	public void setBrokers(List<String> brokers) {this.brokers = brokers;}

	// constr
	public ChannelInfo() {}

	public static ChannelInfo create(String channelid) {

		ChannelInfo info = new ChannelInfo();

		info.setChannelid(channelid);
		info.setType("standard");

		info.setSlotTime(2000);
		info.setAvg1Time(1000*15);
		info.setAvg2Time(1000*60);

		info.setName("Navnet er " + channelid);

		Calendar cal = Calendar.getInstance();
		info.setStart(cal.getTime());
		cal.add(Calendar.HOUR, 3);
		info.setStop(cal.getTime());

		return info;

	}

	@Override
	public String getPersistName() {
		return getChannelid();
	}
	@Override
	public String getPersistDir() {
		return getType();
	}

}
