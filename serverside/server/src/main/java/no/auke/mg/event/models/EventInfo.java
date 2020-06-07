package no.auke.mg.event.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventInfo {

	private String id;
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	private String type;
	public String getType() {return type;}
	public void setType(String type) {this.type=type;}

	private String eventname;
	public String getEventname() {return eventname;}
	public void setEventname(String eventname) {this.eventname = eventname;}

	private String eventtags;
	public String getEventtags() {return eventtags;}
	public void setEventtags(String eventtags) {this.eventtags = eventtags;}

	private Date eventstart;
	public Date getEventstart() {return eventstart;}
	public void setEventstart(Date eventstart) {this.eventstart = eventstart;}

	private Date eventstop;
	public Date getEventstop() {return eventstop;}
	public void setEventstop(Date eventstop) {this.eventstop = eventstop;}

	private int timeslot_period=2000;
	public int getTimeslot_period() {return timeslot_period;}
	public void setTimeslot_period(int timeslot_period) {this.timeslot_period = timeslot_period;}

	public int avg1time=1000*15;
	public int getAvg1time() {return avg1time;}
	public void setAvg1time(int avg1time) {this.avg1time = avg1time;}

	private Map<String, Team> teams = new HashMap<String,Team>();
	public Map<String, Team> getTeams() {return teams;}
	public void setTeams(Map<String, Team> teams) {this.teams = teams;}

	// Process information
	private String server;
	public String getServer() {return server;}
	public void setServer(String server) {this.server = server;}

	private List<String> brokers;
	public List<String> getBrokers() {return brokers;}
	public void setBrokers(List<String> brokers) {this.brokers = brokers;}

	private Map<String, Object> props;
	public Map<String, Object> getProps() {return props;}
	public void setProps(Map<String, Object> props) {this.props = props;}

	// constr
	public EventInfo(String eventid) {
		this.eventid=eventid;
	}
	public Team createTeam(String teamid) {
		Team team = new Team(teamid);
		teams.put(teamid, team);
		return team;
	}

}
