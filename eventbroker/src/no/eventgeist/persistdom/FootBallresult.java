package no.eventgeist.persistdom;

import java.util.HashMap;
import java.util.Map;

public class FootBallresult {
	public String eventid;
	public int pos=0; 
	public int team1=0;
	public int team2=0;
	public Map<String, Map<String,Integer>> hits = new HashMap<String, Map<String,Integer>>();
}