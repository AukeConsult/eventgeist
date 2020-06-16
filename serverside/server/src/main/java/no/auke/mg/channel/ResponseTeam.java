package no.auke.mg.channel;

import java.util.HashMap;
import java.util.Map;

public class ResponseTeam {

	public String team;
	public int num;
	public Map<String, ResponseHits> hits = new HashMap<String, ResponseHits>();

}
