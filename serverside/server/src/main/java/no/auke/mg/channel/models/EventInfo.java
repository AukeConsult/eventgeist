package no.auke.mg.channel.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventInfo {

	private String id;
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}

	private String channelid;
	public String getChannelid() {return channelid;}
	public void setChannelid(String channelid) {this.channelid = channelid;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	private String type;
	public String getType() {return type;}
	public void setType(String type) {this.type=type;}

	private String name;
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}

	private String tags;
	public String getTags() {return tags;}
	public void setTags(String eventtags) {this.tags = tags;}

	private Date start;
	public Date getStart() {return start;}
	public void setStart(Date start) {this.start = start;}

	private Date stop;
	public Date getStop() {return stop;}
	public void setStop(Date stop) {this.stop = stop;}

	private Map<String, Team> teams = new HashMap<String,Team>();
	public Map<String, Team> getTeams() {return teams;}
	public void setTeams(Map<String, Team> teams) {this.teams = teams;}

	private Map<String, Object> props;
	public Map<String, Object> getProps() {return props;}
	public void setProps(Map<String, Object> props) {this.props = props;}

	// constr
	public EventInfo(String channelid) {
		this.channelid=channelid;
	}
	public Team createTeam(String teamid) {
		Team team = new Team(teamid);
		teams.put(teamid, team);
		return team;
	}

}
