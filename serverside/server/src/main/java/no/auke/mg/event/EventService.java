package no.auke.mg.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import no.auke.mg.event.feedbacks.FeedBackSlot;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.EventStatus;
import no.auke.mg.event.models.Status;
import no.auke.mg.event.models.Team;
import no.auke.mg.services.Monitor;
import no.auke.mg.services.Storage;


public abstract class EventService  {

	private String eventid;
	public String getEventid() {return eventid;}

	private long starttime;
	public long getStarttime() {return starttime;}

	private AtomicLong hits=new AtomicLong();
	public long getHits() {return hits.get();}

	private AtomicInteger currentpos= new AtomicInteger();
	public int getCurrentpos() {return currentpos.get();}

	private int timeslot_period=1000;
	public int getTimeslot_period() {return timeslot_period;}

	protected Map<Integer, ResultSlot> resultslots = new ConcurrentHashMap<Integer, ResultSlot>();
	public List<ResultSlot> getResultSlots() {return new ArrayList<ResultSlot>(resultslots.values());}

	protected Map<Integer, TimeFrame> timeframes = new ConcurrentHashMap<Integer, TimeFrame>();
	public List<TimeFrame> getTimeframes() {return new ArrayList<TimeFrame>(timeframes.values());}


	private EventInfo 		eventinfo;
	public EventInfo 		getEventinfo() {return eventinfo;}
	public void 			setEventinfo(EventInfo eventinfo) {this.eventinfo = eventinfo;}

	// services
	private Monitor 		monitor;
	public Monitor 			getMonitor() {return monitor;}

	private Storage 		storage;
	public Storage 			getStorage() {return storage;}

	protected String persistDir;
	protected String persistDirPos;

	public EventService(EventInfo eventinfo, Monitor monitor, Storage storage) {

		this.eventid=eventinfo.getEventid();

		this.timeslot_period=eventinfo.getTimeslot_period();
		this.eventinfo = eventinfo;
		this.monitor=monitor;
		this.storage=storage;

	}

	public void hit() {
		hits.incrementAndGet();
	}

	public void persist() {

		EventStatus status = new EventStatus();
		status.setEventid(eventid);
		status.setCurrentpos(currentpos.get());
		status.setStarttime(starttime);
		status.setTimeslot_period(timeslot_period);
		status.setTimeframes(timeframes.size());
		status.setHits(hits.get());

		int cnt=0;
		for(TimeFrame timeframe:getTimeframes()) {
			cnt+=timeframe.getUserSessions().size();
		}
		status.setUsersessions(cnt);
		storage.saveEventStatus(status);

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

		// Make hart beat and calculate incoming responses pr. timeslot
		new Thread(new Runnable() {
			@Override
			public void run() {
				// calculating results
				while (!stopthread.get()) {
					try {
						Thread.sleep(timeslot_period);
						calculate();
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
		if(!eventinfo.getTeams().containsKey(session.getTeam())) {
			initTeam(eventinfo.createTeam(session.getTeam()));
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

	private int cnt_empty=0;
	private int send_status=0;
	public void calculate() {

		// hart pulze timer
		currentpos.incrementAndGet();
		//

		for(TimeFrame timeframe:timeframes.values()) {

			int slotpos = currentpos.get() - timeframe.getDelay();

			ResultSlot slot=null;
			if(timeframe.getDelay()==0) {

				slot = newResultSlot();
				slot.currentpos = currentpos.get();

			} else {

				if(!resultslots.containsKey(slotpos)) {

					slot = storage.getSlot(eventid, slotpos);

					if(slot==null) {
						slot = newResultSlot();
						slot.currentpos = currentpos.get();
					}
					resultslots.put(slot.currentpos,slot);
				}

				slot = resultslots.get(slotpos);
			}

			executeSlotStart(slot);

			for(UserSession user:timeframe.getUserSessions()) {
				if(user.isOpen()) {

					executeSlotUser(user, slot);

					// NB only read once for each timeslot
					for(String response:user.readResponses()) {
						if(response.startsWith("C#")) {
							executeSlotResponse(response,user,slot);
						} else if (response.startsWith("M#")) {
							String[] func = response.split("\\#");
							if(func.length>=2) {
								if(func[2]!=null && func[2].length()>0) {
									slot.addMessage(func[1],user.getUserid(), user.getDelay(), func[2]);
								}
							} else if(func.length>=1) {
								if(func[1]!=null && func[1].length()>0) {
									slot.addMessage(null,user.getUserid(), user.getDelay(), func[1]);
								}
							}
						} else if (response.startsWith("ST#")) {
							if(timeframe.getDelay()==0) {
								String[] func = response.split("\\#");
								if(func.length>=3) {
									Status st = slot.addStatus(func[1], user.getUserid(), func[2]);
									timeframe.getStatus().put(st.getT(), st);
								}
							}
						}
					}

				}
			}

			if(slot.msglist.size()>0) {
				slot.isresult=true;
				((FeedBackSlot)slot.feedback).msg=slot.msglist;
			}

			send_status--;
			if(send_status<=0) {
				if(timeframe.getStatus().size()>0) {
					slot.isresult=true;
					((FeedBackSlot)slot.feedback).st=new ArrayList<Status>(timeframe.getStatus().values());
				}
				send_status=9;
			}

			executeSlotEnd(slot, (int) ((System.currentTimeMillis() - starttime)));

			if(!slot.isresult) {
				if(cnt_empty>2) {
					slot.feedback=null;
				} else {
					cnt_empty++;
				}
			} else {
				cnt_empty=0;
			}

			resultslots.put(slot.currentpos, slot);

			// adding slot to send to timeframe
			timeframe.setResultslot(slot);
			// adding to sending monitor
			monitor.sendTimeFrame(timeframe);

		}

	}

	public void emptyResults() {
		for(TimeFrame timeframe:timeframes.values()) {
			timeframe.readResults();
		}
	}

	protected abstract void executeSlotStart(ResultSlot slot);
	protected abstract void executeSlotUser(UserSession user, ResultSlot slot);
	protected abstract void executeSlotResponse(String response, UserSession user, ResultSlot slot);
	protected abstract void executeSlotEnd(ResultSlot slot, int time);

	protected abstract ResultSlot newResultSlot();
	protected abstract void initTeam(Team team);


}
