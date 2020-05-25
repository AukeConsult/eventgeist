package no.eventgeist.service.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.eventgeist.persistdom.FootBallresult;
import no.eventgeist.service.EventRunner;
import no.eventgeist.service.ResultSlot;
import no.eventgeist.service.UserSession;

public class FootballEvent extends EventRunner {

	public FootballEvent(String eventid, int timeslot_period) {
		super(eventid,timeslot_period);
	}
	
	protected void executeResponse(UserSession user, ResultSlot slot) {
				
		List<String> responses = user.readResponses(); 
		if(responses.size()>0) {

			slot.isresult=true;
			if(slot.resultObject==null) {
				FootBallresult res = new FootBallresult();
				res.pos=slot.currentpos;
				res.eventid=getEventid();
				slot.resultObject=res;
			}
			FootBallresult res = (FootBallresult)slot.resultObject;
			if(user.getSupport().equals("team1")) {
				res.team1 += responses.size();
			} else if(user.getSupport().equals("team2")) {
				res.team2 += responses.size();
			}						
			for(String response:responses) {
				if(!res.hits.containsKey(user.getSupport())){
					res.hits.put(user.getSupport(),new HashMap<String, Integer>());
				}
				Map<String,Integer> hitres = res.hits.get(user.getSupport());
				if(!hitres.containsKey(response)) {
					hitres.put(response, 0);
				}
				int cnt = hitres.get(response);
				cnt += 1;
				hitres.put(response,cnt);	
			}
			
		}
			
	}
	
	
	@Override
	protected void executeResult(ResultSlot slot) {
		
		if(slot.resultObject!=null) {
			FootBallresult res = (FootBallresult)slot.resultObject;
			try {
				slot.resultString = objectMapper.writeValueAsString(res);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		slot.responses.clear();

	}
	
	@Override
	protected ResultSlot newResultSlot() {return new ResultSlot();}	

}
