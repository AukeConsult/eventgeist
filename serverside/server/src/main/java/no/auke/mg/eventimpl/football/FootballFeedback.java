package no.auke.mg.eventimpl.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.event.feedbacks.FeedBackSlot;

public class FootballFeedback extends FeedBackSlot {

	public FootballFeedback() {super("S");}
	public Map<String, Object> teams = new HashMap<String, Object>();
	public List<Object> messages = new ArrayList<Object>();

}