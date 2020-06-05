package no.auke.mg.event.football;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.auke.mg.event.EventService;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.dom.ResultSlot;

public class FootballEvent extends EventService {

	public FootballEvent(String eventid, int timeslot_period) {
		super(eventid,timeslot_period);
	}

	private Map<String, Integer> supporters = new HashMap<String,Integer>();

	@Override
	protected void executeSlotStart(TimeFrame timeframe) {
		supporters.clear();
	}
	@Override
	protected void executeSlotEnd(ResultSlot slot) {}


	@Override
	protected void executeResponse(UserSession user, ResultSlot slot, int time) {

		if(!supporters.containsKey(user.getSupport())) {
			supporters.put(user.getSupport(), 0);
		}

		int suppcnt = supporters.get(user.getSupport());
		suppcnt +=1;
		supporters.put(user.getSupport(),suppcnt);

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
			if(!res.teams.containsKey(user.getSupport())) {
				res.teams.put(user.getSupport(), new HashMap<String,Object>());
			}
			Map<String,Object> teamres = res.teams.get(user.getSupport());

			if(!teamres.containsKey("numsupp")){
				teamres.put("numsupp",supporters.get(user.getSupport()));
			}

			for(String response:responses) {

				if(response.startsWith("C#")) {

					String btn = response.substring(2);

					if(!teamres.containsKey("hits")){
						teamres.put("hits",new HashMap<String,Map<String,Object>>());
					}
					Map<String,Object> hits = (Map<String, Object>) teamres.get("hits");

					if(!hits.containsKey(btn)) {
						Map<String,Integer> val = new HashMap<String,Integer>();
						val.put("hit",0);
						val.put("avg",0);
						hits.put(btn, val);
					}
					Map<String,Integer> value = (Map<String, Integer>) hits.get(btn);

					int hit_cnt = value.get("hit");
					hit_cnt += 1;
					value.put("hit",hit_cnt);

					int avg_cnt = value.get("avg");
					avg_cnt += 1;
					value.put("avg",avg_cnt);

				} else if (response.startsWith("M#")) {

					this.getMessageService().addMessage(user.getId(), slot.currentpos, user.getDelay(), response.substring(2));
					res.messages.add(user.getUserid() + ":" + response.substring(2));

				}

			}
			res.time=time;
			//res.lastmsgid=this.getMessageService().lastMsgid(user.getDelay());
			//res.lastnoteid=this.getNoteService().lastNoteid(user.getDelay());

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
