package no.auke.mg.event.dom;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Event {

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	private String type;
	public String getType() {return type;}
	public void setType(String type) {this.type=type;}

	private String eventname;
	public String getEventname() {return eventname;}
	public void setEventname(String eventname) {this.eventname = eventname;}

	private Date eventstart;
	public Date getEventstart() {return eventstart;}
	public void setEventstart(Date eventstart) {this.eventstart = eventstart;}

	private Date eventstop;
	public Date getEventstop() {return eventstop;}
	public void setEventstop(Date eventstop) {this.eventstop = eventstop;}

	private Map<String, Object> teams;
	public Map<String, Object> getTeams() {return teams;}
	public void setTeams(Map<String, Object> teams) {this.teams = teams;}

	private Map<String, Object> status;
	public Map<String, Object> getStatus() {return status;}
	public void setStatus(Map<String, Object> status) {this.status = status;}

	private List<String> brokers;
	public List<String> getBrokers() {return brokers;}
	public void setBrokers(List<String> brokers) {this.brokers = brokers;}




}
