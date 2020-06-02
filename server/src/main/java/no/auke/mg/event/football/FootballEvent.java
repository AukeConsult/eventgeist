package no.auke.mg.event.football;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.event.EventService;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.dom.ResultSlot;

public class FootballEvent extends EventService {

	public FootballEvent(String eventid, int timeslot_period) {
		super(eventid,timeslot_period);
	}

	@Override
	protected void executeResponse(UserSession user, ResultSlot slot) {

		List<String> responses = user.readResponses();
		if(responses.size()>0) {

			slot.isresult=true;
			if(slot.resultObject==null) {

				FootballFeedback res = new FootballFeedback();
				res.slotpos=slot.currentpos;
				res.eventid=getEventid();
				slot.resultObject=res;
			}

			FootballFeedback res = (FootballFeedback)slot.resultObject;
			if(user.getSupport().equals("team1")) {
				res.team1 += responses.size();
			} else if(user.getSupport().equals("team2")) {
				res.team2 += responses.size();
			}

			for(String response:responses) {

				if(response.startsWith("C##")) {
					if(!res.hits.containsKey(user.getSupport())){
						res.hits.put(user.getSupport(),new HashMap<String, Integer>());
					}
					Map<String,Integer> hitres = res.hits.get(user.getSupport());
					if(!hitres.containsKey(response.substring(3))) {
						hitres.put(response.substring(3), 0);
					}
					int cnt = hitres.get(response.substring(3));
					cnt += 1;
					hitres.put(response.substring(3),cnt);

				} else if (response.startsWith("M##")) {
					this.getMessageService().addMessage(user.getId(), slot.currentpos, user.getDelay(), response.substring(3));
				}
			}
			res.lastmsgid=this.getMessageService().lastMsgid(user.getDelay());
			res.lastnoteid=this.getNoteService().lastNoteid(user.getDelay());

		}
	}


	@Override
	protected void executeResult(ResultSlot slot) {

		if(slot.resultObject!=null) {
			FootballFeedback res = (FootballFeedback)slot.resultObject;
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