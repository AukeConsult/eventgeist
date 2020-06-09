package no.auke.mg.channel.models;

import java.util.Map;

public class Team {

	private String id;
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}

	private String teamid;
	private String name;
	private String type;
	private Byte[] pic;

	public Team(

			String teamid,
			String name,
			String type

			) {

		this.teamid=teamid;
		this.name=name;
		this.type=type;

	}

	public Team(String teamid) {
		this.teamid=teamid;
	}

	public String getTeamid() {return teamid;}
	public void setTeamid(String teamid) {this.teamid = teamid;}

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getType() {return type;}
	public void setType(String type) {this.type = type;}

	public Byte[] getPic() {return pic;}
	public void setPic(Byte[] pic) {this.pic = pic;}

	private Map<String, Object> props;
	public Map<String, Object> getProps() {return props;}
	public void setProps(Map<String, Object> props) {this.props = props;}


}
