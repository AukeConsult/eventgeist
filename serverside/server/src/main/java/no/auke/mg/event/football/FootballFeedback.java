package no.auke.mg.event.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.event.dom.FeedBack;

public class FootballFeedback extends FeedBack {

	public List<String> messages = new ArrayList<String>();
	public Map<String, Map<String,Object>> teams = new HashMap<String, Map<String,Object>>();

}