package no.auke.mg.channel.impl.football;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.EventInfo;
import no.auke.mg.channel.models.Team;

public class FootballChannel extends ChannelService {

	//TODO: remove calculator (this) from channel service
	public FootballChannel(ChannelInfo channelinfo) {
		super(channelinfo);

		if(channelinfo.getEventid()==null) {
			channelinfo.setEventid(channelinfo.getChannelid());
		}
		EventInfo eventinfo = getStorage().getEventInfo(channelinfo.getEventid());
		if(eventinfo==null) {
			eventinfo = EventInfo.create(channelinfo.getEventid());
			eventinfo.setType("football");
		}
		getStorage().saveEventInfo(eventinfo);

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
	private Queue<ResultSlot> history2 = new LinkedList<ResultSlot>();

	private void calc_average (ResultSlot slot) {

		// sum average 1
		history1.add(slot);
		if(history1.size()>5) {
			history1.poll();
		}

		history2.add(slot);
		if(history2.size()>30) {
			history2.poll();
		}

		FootballFeedback total_hist=null;

		double num_hist1=0;
		for(ResultSlot hist_slot:new ArrayList<ResultSlot>(history1)) {

			num_hist1++;
			if(hist_slot.feedback!=null) {

				FootballFeedback res_hist = (FootballFeedback) hist_slot.feedback;
				if(res_hist.teamwork!=null) {

					// total pr team
					for(String key_team:res_hist.teamwork.keySet()) {

						if(total_hist==null) {
							total_hist=new FootballFeedback();
						}

						if(!total_hist.teamwork.containsKey(key_team)) {
							total_hist.teamwork.put(key_team, new Teamres(key_team));
						}

						Teamres hist_res = res_hist.teamwork.get(key_team);

						Teamres hist1_total = total_hist.teamwork.get(key_team);
						hist1_total.totwork.avg1 += hist_res.totwork.val;

						for(String keybtn:hist_res.btnwork.keySet()){
							if(!hist1_total.btnwork.containsKey(keybtn)){
								hist1_total.btnwork.put(keybtn, new Measure(keybtn));
							}
							hist1_total.btnwork.get(keybtn).avg1 += hist_res.btnwork.get(keybtn).val;
						}
					}
				}
			}
		}

		double num_hist2=0;
		for(ResultSlot hist_slot:new ArrayList<ResultSlot>(history2)) {

			num_hist2++;
			if(hist_slot.feedback!=null) {

				FootballFeedback res_hist = (FootballFeedback) hist_slot.feedback;
				if(res_hist.teamwork!=null) {

					// total pr team
					for(String key_team:res_hist.teamwork.keySet()) {

						if(total_hist==null) {
							total_hist=new FootballFeedback();
						}

						if(!total_hist.teamwork.containsKey(key_team)) {
							total_hist.teamwork.put(key_team, new Teamres(key_team));
						}

						Teamres hist_res = res_hist.teamwork.get(key_team);

						Teamres hist_total = total_hist.teamwork.get(key_team);
						hist_total.totwork.avg2 += hist_res.totwork.val;

						for(String keybtn:hist_res.btnwork.keySet()){
							if(!hist_total.btnwork.containsKey(keybtn)){
								hist_total.btnwork.put(keybtn, new Measure(keybtn));
							}
							hist_total.btnwork.get(keybtn).avg2 += hist_res.btnwork.get(keybtn).val;
						}
					}
				}
			}
		}

		if(total_hist!=null) {

			FootballFeedback current=(FootballFeedback) slot.feedback;
			if(current==null) {
				current = new FootballFeedback();
				current.sp=slot.pos;
				current.chid=getChannelid();
				slot.feedback=current;
			}

			for(String key_team:total_hist.teamwork.keySet()) {

				if(!current.teamwork.containsKey(key_team)) {
					current.teamwork.put(key_team, new Teamres(key_team));
				}

				Teamres hist_total = total_hist.teamwork.get(key_team);
				Teamres current_res = current.teamwork.get(key_team);

				current_res.totwork.avg1 = Math.round(hist_total.totwork.avg1 / num_hist1 * 10000.0) / 10000.0;
				current_res.totwork.avg2 = Math.round(hist_total.totwork.avg2 / num_hist2 * 10000.0) / 10000.0;

				if(current_res.totwork.avg1>0 || current_res.totwork.avg2>0) {
					slot.isresult=true;
				}

				for(String keybtn:hist_total.btnwork.keySet()){
					if(!current_res.btnwork.containsKey(keybtn)){
						current_res.btnwork.put(keybtn, new Measure(keybtn));
					}
					current_res.btnwork.get(keybtn).avg1 = Math.round(hist_total.btnwork.get(keybtn).avg1 / num_hist1 * 10000.0) / 10000.0;
					current_res.btnwork.get(keybtn).avg2 = Math.round(hist_total.btnwork.get(keybtn).avg2 / num_hist2 * 10000.0) / 10000.0;
					if(current_res.btnwork.get(keybtn).avg1>0 || current_res.btnwork.get(keybtn).avg2>0) {
						slot.isresult=true;
					}
				}
			}
		}

	}


	@Override
	protected void executeSlotEnd(ResultSlot slot, int time) {

		calc_average(slot);

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
