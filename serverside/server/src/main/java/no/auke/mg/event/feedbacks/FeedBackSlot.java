package no.auke.mg.event.feedbacks;

import java.util.Map;

import no.auke.mg.event.models.Status;

public class FeedBackSlot extends FeedBack {

	public FeedBackSlot(String t) {this.t=t;}
	public int sp=0;
	public int tm=0;

	public Map<String, Status> st;

}