package no.auke.mg.eventimpl.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import no.auke.mg.event.EventService;
import no.auke.mg.event.Monitor;
import no.auke.mg.event.Storage;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.ResultSlot;
import no.auke.mg.event.models.Team;

public class FootballEvent extends EventService {

	public FootballEvent(EventInfo eventinfo, Monitor monitor, Storage storage) {
		super(eventinfo, monitor,storage);
	}

	@Override
	protected void executeSlotStart(TimeFrame timeframe) {}

	class Measure {
		public double 	val=0;
		public double	avg1=0;
		public double	avg2=0;
	}

	class Teamres {
		public int num=0;
		public Measure hits = new Measure();
		public Map<String,Measure> btn = new HashMap<String,Measure>();
	}

	@Override
	protected void executeResponse(UserSession user, ResultSlot slot, int time) {

		List<String> responses = user.readResponses();

		slot.isresult=false;
		FootballFeedback res = (FootballFeedback)slot.feedback;
		if(slot.feedback==null) {
			res = new FootballFeedback();
			res.sp=slot.currentpos;
			res.evid=getEventid();
			slot.feedback=res;
		}

		if(!res.teams.containsKey(user.getTeam())) {
			res.teams.put(user.getTeam(), new Teamres());
		}
		Teamres teamres = (Teamres) res.teams.get(user.getTeam());
		teamres.num++;

		if(responses.size()>0) {

			for(String response:responses) {

				if(response.startsWith("C#")) {
					slot.isresult=true;
					String btnkey = response.substring(2);
					teamres.hits.val++;
					if(btnkey!=null&&btnkey.length()>0) {
						if(!teamres.btn.containsKey(btnkey)){
							teamres.btn.put(btnkey,new Measure());
						}
						teamres.btn.get(btnkey).val++;
					}
				} else if (response.startsWith("M#")) {
					this.getMessages().addMessage(user.getId(), slot.currentpos, user.getDelay(), response.substring(2));
					res.messages.add(user.getUserid() + ":" + response.substring(2));
					slot.isresult=true;
				}
			}
			res.tm=time;
		}

	}


	private Queue<ResultSlot> history = new LinkedList<ResultSlot>();

	@Override
	protected void executeSlotEnd(ResultSlot slot) {

		// make average etc
		history.add(slot);
		if(history.size()>10) {
			history.poll();
		}

		int num_pos=0;

		System.out.println("calc history " + history.size());

		FootballFeedback total_res=null;
		for(ResultSlot slothist:new ArrayList<ResultSlot>(history)) {

			num_pos++;
			if(slothist.feedback!=null) {

				System.out.println(slothist.feedback.getClass());

				FootballFeedback res = (FootballFeedback) slothist.feedback;
				if(res.teams!=null) {

					if(total_res==null) {
						total_res = new FootballFeedback();
					}

					for(String keyteaam:res.teams.keySet()) {

						if(!total_res.teams.containsKey(keyteaam)) {
							total_res.teams.put(keyteaam, new Teamres());
						}

						Teamres totalres = (Teamres) total_res.teams.get(keyteaam);
						Teamres slotres = (Teamres) res.teams.get(keyteaam);
						totalres.num += slotres.num;
						totalres.hits.val += slotres.hits.val;

						for(String keybtn:slotres.btn.keySet()){
							if(!totalres.btn.containsKey(keybtn)){
								totalres.btn.put(keybtn, new Measure());
							}
							totalres.btn.get(keybtn).val += slotres.btn.get(keybtn).val;
						}
					}
				}
			}
		}

		if(total_res!=null) {

			FootballFeedback current_res=(FootballFeedback) slot.feedback;
			if(current_res==null) {
				current_res = new FootballFeedback();
				current_res.sp=slot.currentpos;
				current_res.evid=getEventid();
				slot.feedback=current_res;
			}

			for(String keyteaam:total_res.teams.keySet()) {

				if(!current_res.teams.containsKey(keyteaam)) {
					current_res.teams.put(keyteaam, new Teamres());
				}

				Teamres total = (Teamres) total_res.teams.get(keyteaam);
				Teamres res = (Teamres) current_res.teams.get(keyteaam);
				res.hits.avg1 = total.hits.val / num_pos;
				if(total.hits.val>0) {
					slot.isresult=true;
				}

				for(String keybtn:total.btn.keySet()){

					if(!res.btn.containsKey(keybtn)){
						res.btn.put(keybtn, new Measure());
					}

					total.btn.get(keybtn);
					res.btn.get(keybtn).avg1 = total.btn.get(keybtn).val/num_pos;

					if(total.btn.get(keybtn).val>0) {
						slot.isresult=true;
					}

				}

			}

		}

		if(!slot.isresult) {
			slot.feedback=null;
		}

	}

	// convert to result string
	@Override
	protected void executeResult(ResultSlot slot) {}

	@Override
	protected ResultSlot newResultSlot() {
		ResultSlot slot = new ResultSlot();
		slot.feedback = new FootballFeedback();
		return slot;
	}

	@Override
	protected void initTeam(Team team) {}


}
