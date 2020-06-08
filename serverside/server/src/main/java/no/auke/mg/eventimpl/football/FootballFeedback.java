package no.auke.mg.eventimpl.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.auke.mg.event.feedbacks.FeedBackSlot;

public class FootballFeedback extends FeedBackSlot {

	public FootballFeedback() {super("S");}
	@JsonIgnore
	public Map<String, Teamres> teamwork = new HashMap<String, Teamres>();
	public List<Teamres> getTeams(){return new ArrayList<Teamres>(teamwork.values());};

}