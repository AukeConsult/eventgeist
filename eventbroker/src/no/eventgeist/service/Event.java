package no.eventgeist.service;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


//
// Calculating each event
// 

public abstract class Event implements Runnable {
	    
	private String eventid;
	public String getEventid() {return eventid;}	

	private long starttime;	
	public long getStarttime() {return starttime;}
	
	private AtomicLong max_hits=new AtomicLong();
	public long getCnt() {return max_hits.get();}	

	private AtomicInteger currentpos= new AtomicInteger();
	public int getCurrentpos() {return currentpos.get();}

	private int timeslot_period=5000;

	protected Map<Integer, TimeSlot> timeslots = new ConcurrentHashMap<Integer, TimeSlot>();
	public List<TimeSlot> getTimeSlots() {return new ArrayList<TimeSlot>(timeslots.values());}
	
	protected Map<Integer, TimeFrame> timeframes = new ConcurrentHashMap<Integer, TimeFrame>();		
	public List<TimeFrame> getTimeframes() {return new ArrayList<TimeFrame>(timeframes.values());}

	public Event(String eventid, int timeslot_period) {
		this.eventid=eventid;
		this.timeslot_period=timeslot_period;	
	}

	// init and read up even informations
	public void init() {
		starttime=System.currentTimeMillis();
		new Thread(this).start();
	}
	
	public void addUser(UserSession session) {
		if(!timeframes.containsKey(session.getDelay())) {
			timeframes.put(session.getDelay(), new TimeFrame(session.getDelay()));
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
	
	private AtomicBoolean stopthread = new AtomicBoolean(false);
	public void stopThreads() {
		stopthread.set(true);
	}

	public void calculate() {
		
		// hart pulze timer
		currentpos.incrementAndGet();
		// 
        
		for(TimeFrame timeframe:timeframes.values()) {
        	
        	int slotpos = currentpos.get() - timeframe.getDelay();
        	
        	TimeSlot slot=null;
        	if(!timeslots.containsKey(slotpos)) {
        		slot = newTimeSlot();
        		slot.currentpos = currentpos.get();
        	} else {
        		slot = timeslots.get(slotpos);
        	}        	
        	for(UserSession user:timeframe.getUserSessions()) {
        		executeResponse(user, slot);
        	}
        	executeResult(slot);
        	if(!slot.result.equals("")) {
        		timeslots.put(slot.currentpos, slot);
        		timeframe.setResultslot(slot);
        	}      
        	
        }

	}
		
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
	protected abstract void executeResponse(UserSession usersession,TimeSlot slot);
	protected abstract void executeResult(TimeSlot slot);
	protected abstract TimeSlot newTimeSlot();


}
