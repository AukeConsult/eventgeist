package no.auke.mg.event.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.event.dom.FeedBack;

public class FootballFeedback extends FeedBack {

	public int team1=0;
	public int team2=0;
	public Map<String, Map<String,Integer>> hits = new HashMap<String, Map<String,Integer>>();
	public List<String> messages = new ArrayList<String>();


}