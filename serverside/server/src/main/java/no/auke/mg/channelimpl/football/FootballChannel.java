package no.auke.mg.channelimpl.football;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.Team;
import no.auke.mg.services.Monitor;
import no.auke.mg.services.Storage;

public class FootballChannel extends ChannelService {

	public FootballChannel(ChannelInfo channelid, Monitor monitor, Storage storage) {
		super(channelid, monitor,storage);
	}

	@Override
	protected void executeSlotStart(ResultSlot slot) {

		FootballFeedback res = new FootballFeedback();
		res.sp=slot.pos;
		res.chid=getChannelid();
		res.evid=getEventid();
		slot.feedback=res;

	}

	@Override
	protected void executeSlotUser(UserSession user, ResultSlot slot) {

		FootballFeedback res = (FootballFeedback)slot.feedback;
		if(!res.teamwork.containsKey(user.getTeam())) {
			res.teamwork.put(user.getTeam(), new Teamres(user.getTeam()));
		}
		Teamres teamres = res.teamwork.get(user.getTeam());
		teamres.num++;

	}

	@Override
	protected void executeSlotResponse(String response, UserSession user, ResultSlot slot) {

		if(response.startsWith("C#")) {

			FootballFeedback res = (FootballFeedback)slot.feedback;
			Teamres teamres = res.teamwork.get(user.getTeam());

			slot.isresult=true;
			String btnkey = response.substring(2);
			teamres.totwork.val++;
			if(btnkey!=null&&btnkey.length()>0) {
				if(!teamres.btnwork.containsKey(btnkey)){
					teamres.btnwork.put(btnkey,new Measure(btnkey));
				}
				teamres.btnwork.get(btnkey).val++;
			}

		}

	}

	private Queue<ResultSlot> history1 = new LinkedList<ResultSlot>();
	private void calc_average1 (ResultSlot slot) {

		// make average etc
		history1.add(slot);
		if(history1.size()>5) {
			history1.poll();
		}

		int num_pos=0;
		FootballFeedback total_res=null;
		for(ResultSlot slothist:new ArrayList<ResultSlot>(history1)) {

			num_pos++;
			if(slothist.feedback!=null) {

				FootballFeedback res = (FootballFeedback) slothist.feedback;
				if(res.teamwork!=null) {

					if(total_res==null) {
						total_res = new FootballFeedback();
					}

					for(String keyteaam:res.teamwork.keySet()) {

						if(!total_res.teamwork.containsKey(keyteaam)) {
							total_res.teamwork.put(keyteaam, new Teamres(keyteaam));
						}

						Teamres totalres = total_res.teamwork.get(keyteaam);
						Teamres slotres = res.teamwork.get(keyteaam);
						totalres.num += slotres.num;
						totalres.totwork.val += slotres.totwork.val;

						for(String keybtn:slotres.btnwork.keySet()){
							if(!totalres.btnwork.containsKey(keybtn)){
								totalres.btnwork.put(keybtn, new Measure(keybtn));
							}
							totalres.btnwork.get(keybtn).val += slotres.btnwork.get(keybtn).val;
						}
					}
				}
			}
		}

		if(total_res!=null) {

			FootballFeedback current_res=(FootballFeedback) slot.feedback;
			if(current_res==null) {
				current_res = new FootballFeedback();
				current_res.sp=slot.pos;
				current_res.chid=getChannelid();
				slot.feedback=current_res;
			}

			for(String keyteaam:total_res.teamwork.keySet()) {

				if(!current_res.teamwork.containsKey(keyteaam)) {
					current_res.teamwork.put(keyteaam, new Teamres(keyteaam));
				}

				Teamres total = total_res.teamwork.get(keyteaam);
				Teamres res = current_res.teamwork.get(keyteaam);
				res.totwork.avg1 = total.totwork.val / num_pos;
				if(res.totwork.avg1>0) {
					slot.isresult=true;
				}

				for(String keybtn:total.btnwork.keySet()){

					if(!res.btnwork.containsKey(keybtn)){
						res.btnwork.put(keybtn, new Measure(keybtn));
					}

					total.btnwork.get(keybtn);
					res.btnwork.get(keybtn).avg1 = total.btnwork.get(keybtn).val/num_pos;

					if(total.btnwork.get(keybtn).val>0) {
						slot.isresult=true;
					}
				}
			}
		}

	}

	private Queue<ResultSlot> history2 = new LinkedList<ResultSlot>();
	private void calc_average2 (ResultSlot slot) {

		//		// make average etc
		//		history2.add(slot);
		//		if(history2.size()>5) {
		//			history2.poll();
		//		}
		//
		//		int num_pos=0;
		//		FootballFeedback total_res=null;
		//		for(ResultSlot slothist:new ArrayList<ResultSlot>(history2)) {
		//
		//			num_pos++;
		//			if(slothist.feedback!=null) {
		//
		//				FootballFeedback res = (FootballFeedback) slothist.feedback;
		//				if(res.teams!=null) {
		//
		//					if(total_res==null) {
		//						total_res = new FootballFeedback();
		//					}
		//
		//					for(String keyteaam:res.teams.keySet()) {
		//
		//						if(!total_res.teams.containsKey(keyteaam)) {
		//							total_res.teams.put(keyteaam, new Teamres());
		//						}
		//
		//						Teamres totalres = (Teamres) total_res.teams.get(keyteaam);
		//						Teamres slotres = (Teamres) res.teams.get(keyteaam);
		//						totalres.num += slotres.num;
		//						totalres.hits.val += slotres.hits.val;
		//
		//						for(String keybtn:slotres.btn.keySet()){
		//							if(!totalres.btn.containsKey(keybtn)){
		//								totalres.btn.put(keybtn, new Measure());
		//							}
		//							totalres.btn.get(keybtn).val += slotres.btn.get(keybtn).val;
		//						}
		//					}
		//				}
		//			}
		//		}
		//
		//		if(total_res!=null) {
		//
		//			FootballFeedback current_res=(FootballFeedback) slot.feedback;
		//			if(current_res==null) {
		//				current_res = new FootballFeedback();
		//				current_res.sp=slot.currentpos;
		//				current_res.evid=getEventid();
		//				slot.feedback=current_res;
		//			}
		//
		//			for(String keyteaam:total_res.teams.keySet()) {
		//
		//				if(!current_res.teams.containsKey(keyteaam)) {
		//					current_res.teams.put(keyteaam, new Teamres());
		//				}
		//
		//				Teamres total = (Teamres) total_res.teams.get(keyteaam);
		//				Teamres res = (Teamres) current_res.teams.get(keyteaam);
		//				res.totwork.avg2 = total.hits.val / num_pos;
		//				if(total.hits.val>0) {
		//					slot.isresult=true;
		//				}
		//
		//				for(String keybtn:total.btn.keySet()){
		//
		//					if(!res.btn.containsKey(keybtn)){
		//						res.btn.put(keybtn, new Measure());
		//					}
		//
		//					total.btn.get(keybtn);
		//					res.btn.get(keybtn).avg2 = total.btn.get(keybtn).val/num_pos;
		//
		//					if(total.btn.get(keybtn).val>0) {
		//						slot.isresult=true;
		//					}
		//				}
		//			}
		//		}

	}



	@Override
	protected void executeSlotEnd(ResultSlot slot, int time) {

		calc_average1(slot);
		calc_average2(slot);

		FootballFeedback res = (FootballFeedback) slot.feedback;
		res.tm=time;


	}

	@Override
	protected ResultSlot newResultSlot() {
		ResultSlot slot = new ResultSlot();
		slot.feedback = new FootballFeedback();
		return slot;
	}

	@Override
	protected void initTeam(Team team) {}



}
