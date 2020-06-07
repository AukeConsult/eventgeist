package no.auke.mg.event.models;

import java.util.Map;

public class Team {

	private String teamid;
	private String id;
	private String name;
	private String type;
	private Byte[] pic;


	public Team(

			String teamid,
			String name,
			String type,
			String id

			) {

		this.teamid=teamid;
		this.name=name;
		this.type=type;
		this.id=id;

	}

	public String getTeamid() {return teamid;}
	public void setTeamid(String teamid) {this.teamid = teamid;}

	public String getId() {return id;}
	public void setId(String id) {this.id = id;}

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
