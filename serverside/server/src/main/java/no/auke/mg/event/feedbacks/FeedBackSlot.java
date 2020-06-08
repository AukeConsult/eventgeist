package no.auke.mg.event.feedbacks;

import java.util.List;

import no.auke.mg.event.models.Message;
import no.auke.mg.event.models.Status;

public class FeedBackSlot extends FeedBack {

	public FeedBackSlot(String t) {this.t=t;}
	public int sp=0;
	public int tm=0;
	public List<Status> st;
	public List<Message> msg;

}