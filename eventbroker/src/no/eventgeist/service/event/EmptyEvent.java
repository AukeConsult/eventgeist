package no.eventgeist.service.event;

import java.util.List;

import no.eventgeist.service.EventRunner;
import no.eventgeist.service.ResultSlot;
import no.eventgeist.service.UserSession;

public class EmptyEvent extends EventRunner {

	public EmptyEvent(String eventid, int timeslot_period) {
		super(eventid, timeslot_period);
	}

	private String makeQ(String value) {
		return "\"" + value + "\"";
	}
	
	protected void executeResponse(UserSession user, ResultSlot slot) {
		List<String> responses = user.readResponses(); 
		for(String response:responses) {

			if (response.startsWith("#C")) {
		    	if(!slot.hits.containsKey(user.getSupport())) {
		    		slot.hits.put(user.getSupport(), 0);
		    	}
		    	slot.hits.put(user.getSupport(), slot.hits.get(user.getSupport()) + 1);
			}
			
//			slot.responses.add("{" + 
//					makeQ("usr") + ":" + makeQ(user.getUserid()) + 
//					"," + makeQ("rsp") + ":" + makeQ(response) + 
//					"," +  makeQ("sup") + ":" + makeQ(user.getSupport()) + 
//					"}") ;
			
		}
		
	}

	@Override
	protected void executeResult(ResultSlot slot) {
		
		String hits="";
		slot.resultString="";
		if(slot.hits.size()>0) {			
			hits= makeQ("hits") + ":" + System.getProperty("line.separator") + "[" + System.getProperty("line.separator"); 
			String list="";
			for(String support:slot.hits.keySet()) {
				list += (list.length()>0?","+ System.getProperty("line.separator"):"") + "{" + 
						makeQ("sup") + ":" + makeQ(support) + "," +  
						makeQ("cnt") + ":" + slot.hits.get(support).toString() + 
						"}";
			}			
			hits += list + System.getProperty("line.separator") + "]" ;
		}
		String resp="";		
		
		/*
		if(slot.responses.size()>0) {
			resp=makeQ("hits") + ": [";
			String list="";
			for(String response:slot.responses) {
				list += (list.length()>0?",":"") + response;
			}
			resp += list + "]";
		}
		*/
		
		if(resp.length()>0 || hits.length()>0) {			
			slot.resultString = "{" + System.getProperty("line.separator") +
					makeQ("evt") + ":" + makeQ(getEventid()) + "," + System.getProperty("line.separator") +
					makeQ("pos") + ":" + String.valueOf(slot.currentpos) +
					(hits.length()>0?","+ System.getProperty("line.separator")+hits:"") + 
					(resp.length()>0?","+ System.getProperty("line.separator")+resp:"") + 
					System.getProperty("line.separator") + "}" ;
		}		
		slot.responses.clear();
	}

	@Override
	protected ResultSlot newResultSlot() {return new ResultSlot();}

}
