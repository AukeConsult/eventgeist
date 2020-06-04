package no.auke.mg.event;

import java.util.ArrayList;
import java.util.List;

import no.auke.mg.event.dom.Note;

public class NoteService {

	private EventService event;
	private List<Note> notelist = new ArrayList<Note>();
	public NoteService(EventService event) {
		this.event=event;
	}
	public int lastNoteid(int delay) {return notelist.size() - 1;}

}
