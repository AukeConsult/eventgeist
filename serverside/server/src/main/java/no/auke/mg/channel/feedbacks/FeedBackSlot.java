package no.auke.mg.channel.feedbacks;

import java.util.List;

import no.auke.mg.channel.Message;
import no.auke.mg.channel.Status;

public class FeedBackSlot extends FeedBack {

	public FeedBackSlot(String t) {this.t=t;}
	public long sp=0;
	public long tm=0;
	public List<Status> st;
	public List<Message> msg;

}