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
import no.auke.mg.event.models.ResultSlot;
import no.auke.mg.event.models.Status;
import no.auke.mg.event.models.Team;


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

	private Messages 		messages;
	public Messages 		getMessages() {return messages;}

	// services
	private Monitor 		monitor;
	public Monitor 			getMonitor() {return monitor;}

	private Storage 		storage;
	public Storage 			getStorage() {return storage;}

	protected String persistDir;
	protected String persistDirPos;

	public EventService(EventInfo eventinfo, Monitor monitor, Storage storage) {

		this.eventid=eventinfo.getEventid();
		this.messages = new Messages(this);

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

	AtomicInteger send_status = new AtomicInteger();
	public void setStatus(String type, String json, int delay) {
		if(timeframes.containsKey(delay)) {
			if(!timeframes.get(delay).getStatus().containsKey(type)) {
				timeframes.get(delay).getStatus().put(type,new Status());
			}
			timeframes.get(delay).getStatus().get(type).setVal(json);
			send_status.set(0);
		}
	}


	public List<UserSession> getUserSessions() {
		List<UserSession> ret = new ArrayList<UserSession>();
		for(TimeFrame timeframe:getTimeframes()) {
			ret.addAll(timeframe.getUserSessions());
		}
		return ret;
	}


	public void calculate() {

		// hart pulze timer
		currentpos.incrementAndGet();
		//

		for(TimeFrame timeframe:timeframes.values()) {

			executeSlotStart(timeframe);

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
			for(UserSession user:timeframe.getUserSessions()) {
				if(user.isOpen()) {
					executeResponse(user, slot, (int) ((System.currentTimeMillis() - starttime)));
				}
			}

			executeSlotEnd(slot);
			if(send_status.decrementAndGet()<=0) {

				if(slot.feedback!=null) {
					((FeedBackSlot)slot.feedback).st=timeframe.getStatus();
				}
				send_status.set(10);
			}

			executeResult(slot);
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

	protected abstract void executeSlotStart(TimeFrame timeframe);
	protected abstract void executeResponse(UserSession usersession,ResultSlot slot, int time);
	protected abstract void executeSlotEnd(ResultSlot slot);
	protected abstract void executeResult(ResultSlot slot);
	protected abstract ResultSlot newResultSlot();
	protected abstract void initTeam(Team team);


}
