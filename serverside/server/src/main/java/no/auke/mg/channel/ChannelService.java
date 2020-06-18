package no.auke.mg.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import no.auke.mg.channel.feedbacks.FeedBackSlot;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.ChannelStatus;
import no.auke.mg.channel.models.Team;
import no.auke.mg.services.Monitor;
import no.auke.mg.services.Storage;

public abstract class ChannelService  {

	private String channelid;
	public String getChannelid() {return channelid;}

	private String eventid;
	public String getEventid() {return eventid;}
	public void setEventid(String eventid) {this.eventid = eventid;}

	private long starttime;
	public long getStarttime() {return starttime;}

	private AtomicLong hits=new AtomicLong();
	public long getHits() {return hits.get();}

	private AtomicLong currentpos= new AtomicLong();
	public long getCurrentpos() {return currentpos.get();}

	private AtomicInteger slotTime = new AtomicInteger(2000);
	public int getSlotTime() {return slotTime.get();}
	public void setSlotTime(int slotTime) {this.slotTime.set(slotTime);}

	public AtomicInteger avg1Period = new AtomicInteger(15);
	public int getAvg1period() {return avg1Period.get();}
	public void setAvg1period(int avg1Period) {this.avg1Period.set(avg1Period);}

	public AtomicInteger avg2Period = new AtomicInteger(60);
	public int getAvg2period() {return avg2Period.get();}
	public void setAvg2period(int avg2Period) {this.avg2Period.set(avg2Period);}

	protected Map<Integer, TimeFrame> timeframes = new ConcurrentHashMap<Integer, TimeFrame>();
	public List<TimeFrame> getTimeframes() {return new ArrayList<TimeFrame>(timeframes.values());}

	private ChannelInfo 	channelinfo;
	public ChannelInfo 		getChannelinfo() {return channelinfo;}
	public void 			setChannelinfo(ChannelInfo channelinfo) {this.channelinfo = channelinfo;}

	// services
	private Monitor 		monitor;
	public void 			setMonitor(Monitor monitor) {this.monitor = monitor;}
	public Monitor 			getMonitor() {return monitor;}

	private Storage 		storage;
	public void 			setStorage(Storage storage) {this.storage = storage;}
	public Storage 			getStorage() {return storage;}

	protected String persistDir;
	protected String persistDirPos;

	public ChannelService(ChannelInfo channelinfo) {

		this.channelinfo = channelinfo;

		this.channelid=channelinfo.getChannelid();
		this.eventid=channelinfo.getChannelid();

		this.slotTime.set(channelinfo.getSlotTime());

		this.avg1Period.set(channelinfo.getAvg1Time() / channelinfo.getSlotTime());
		this.avg2Period.set(channelinfo.getAvg2Time() / channelinfo.getSlotTime());

		this.channelinfo = channelinfo;

		this.monitor=Monitor.instance;
		//TODO: make a separate storage object for each channel
		this.storage=Storage.instance;

	}

	public void hit() {
		hits.incrementAndGet();
	}

	public void persist() {

		ChannelStatus status = new ChannelStatus();
		status.setChannelid(channelid);
		status.setEventid(channelid);
		status.setCurrentpos(currentpos.get());
		status.setStarttime(starttime);
		status.setSlotTime(slotTime.get());
		status.setTimeframes(timeframes.size());
		status.setHits(hits.get());
		int cnt=0;
		for(TimeFrame timeframe:getTimeframes()) {
			cnt+=timeframe.getUserSessions().size();
		}
		status.setUsersessions(cnt);
		storage.saveChannelStatus(status);

	}

	private AtomicBoolean stopthread = new AtomicBoolean(false);
	public void stop() {
		stopthread.set(true);
	}

	// init and read up even informations
	public void init() {

		starttime=System.currentTimeMillis();

		persist();
		storage.doSave();

		// persist status

		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				// persist status
				while (!stopthread.get()) {
					persist();
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
		 */

		// collect responses
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("start collect");
				while (!stopthread.get()) {
					try {
						//System.out.println("collect");
						collect();
						Thread.sleep(slotTime.get() / 5);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}).start();

		// Make hart beat and calculate incoming responses pr. timeslot
		new Thread(new Runnable() {
			@Override
			public void run() {
				// calculating results
				System.out.println("start calculate");
				long start=System.currentTimeMillis();
				while (!stopthread.get()) {
					try {

						System.out.println("calculate " + slotTime.get());
						calculate();
						start = start + slotTime.get();
						long wait = start - System.currentTimeMillis();
						System.out.println("wait " + wait);

						if(wait>0) {
							Thread.sleep(wait);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	public void addUser(UserSession session) {

		if(!timeframes.containsKey(session.getDelay())) {
			timeframes.put(session.getDelay(), new TimeFrame(this,session.getDelay()));
		}
		timeframes.get(session.getDelay()).addUser(session);

	}

	public List<UserSession> getUserSessions() {
		List<UserSession> ret = new ArrayList<UserSession>();
		for(TimeFrame timeframe:getTimeframes()) {
			ret.addAll(timeframe.getUserSessions());
		}
		return ret;
	}

	public void collect() {

		for(TimeFrame timeframe:timeframes.values()) {

			Response reponse_collect = new Response();
			for(UserSession user:timeframe.getUserSessions()) {

				if(user.isOpen() && user.hasResult()) {

					if(!reponse_collect.teams.containsKey(user.getTeam())) {
						reponse_collect.teams.put(user.getTeam(), new ResponseTeam());
					}
					ResponseTeam res = reponse_collect.teams.get(user.getTeam());
					res.num++;

					for(String response:user.readResponses()) {
						if(response.startsWith("C#")) {
							reponse_collect.hasresult=true;
							String btnkey = response.substring(2);
							if(btnkey!=null&&btnkey.length()>0) {
								if(!res.hits.containsKey(btnkey)){
									res.hits.put(btnkey,new ResponseHits());
								}
								res.hits.get(btnkey).num++;
								res.hits.get(btnkey).val++;
							}
						} else if (response.startsWith("M#")) {
							if(response.substring(2).length()>0) {
								reponse_collect.msglist.add(user.getUserid() + "#" + response.substring(2));
								reponse_collect.hasresult=true;
							}
						} else if (response.startsWith("ST#")) {
							reponse_collect.statuslist.add(user.getUserid() + "#" + response.substring(3));
							reponse_collect.hasresult=true;
						}
					}
				}
			}
			if(reponse_collect.hasresult) {
				timeframe.setResponse(reponse_collect);
			}
		}

	}

	private int cnt_empty=100;
	public void calculate() {

		// hart pulse timer
		currentpos.incrementAndGet();
		//

		for(TimeFrame timeframe:timeframes.values()) {

			long slotpos = (currentpos.get() - timeframe.getDelay());

			ResultSlot slot=null;
			slot = storage.getSlot(channelid, slotpos - timeframe.getDelay());
			if(slot==null) {
				slot = newResultSlot();
				slot.channelid=getChannelid();
				slot.eventid=getEventid();
				slot.pos = currentpos.get() - timeframe.getDelay();
			}

			executeSlotStart(slot);

			for(Response response:timeframe.readResponses()) {

				// Summarize hits
				for(String team:response.teams.keySet()) {

					if(!slot.teams.containsKey(team)) {
						slot.teams.put(team, new ResponseTeam());
					}

					ResponseTeam teamres = slot.teams.get(team);
					teamres.num += response.teams.get(team).num;

					for(String btn:response.teams.get(team).hits.keySet()) {
						if(!teamres.hits.containsKey(btn)) {
							teamres.hits.put(btn, new ResponseHits());
						}
						teamres.hits.get(btn).val += response.teams.get(team).hits.get(btn).val;
						teamres.hits.get(btn).num += response.teams.get(team).hits.get(btn).num;
					}
					slot.isresult=true;

				}

				// add messages
				for(String message:response.msglist) {

					String[] func = message.split("\\#");
					if(func.length==3) {
						if(func[2]!=null && func[2].length()>0) {
							slot.isresult=true;
							slot.addMessage(func[1],func[0], timeframe.getDelay(), func[2]);
						}
					} else if(func.length==2) {
						if(func[1]!=null && func[1].length()>0) {
							slot.isresult=true;
							slot.addMessage(null,func[0], timeframe.getDelay(), func[1]);
						}
					}

				}

				// add statuses
				for(String status:response.statuslist) {
					if(timeframe.getDelay()==0) {
						String[] func = status.split("\\#");
						if(func.length>=3) {
							Status st = slot.addStatus(func[1], func[0], func[2]);
							timeframe.getStatus().put(st.getT(), st);
						}
						slot.isresult=true;
						slot.statuslist = new ArrayList<Status>(timeframe.getStatus().values());
						((FeedBackSlot)slot.feedback).st=new ArrayList<Status>(timeframe.getStatus().values());
					}
				}

			}

			executeSlotEnd(timeframe, slot, (int) ((System.currentTimeMillis() - starttime)));

			if(!slot.isresult) {
				if(cnt_empty<=2) {
					slot.isresult=true;
					cnt_empty++;
				}
			} else {
				cnt_empty=0;
			}

			if(timeframe.getStatus().size()>0 && getCurrentpos() % 5 == 0) {

				slot.isresult=true;
				slot.statuslist = new ArrayList<Status>(timeframe.getStatus().values());
				((FeedBackSlot)slot.feedback).st=new ArrayList<Status>(timeframe.getStatus().values());
			}

			if(slot.isresult) {

				// adding slot to send to timeframe
				timeframe.setResultslot(slot);
				// adding to sending monitor
				monitor.sendTimeFrame(timeframe);
				storage.saveSlot(slot);

			}

		}

	}

	public void emptyResults() {
		for(TimeFrame timeframe:timeframes.values()) {
			timeframe.readFeedBack();
		}
	}

	protected abstract void executeSlotStart(ResultSlot slot);
	protected abstract void executeSlotEnd(TimeFrame frame, ResultSlot slot, int time);

	protected abstract ResultSlot newResultSlot();
	protected abstract void initTeam(Team team);


}
