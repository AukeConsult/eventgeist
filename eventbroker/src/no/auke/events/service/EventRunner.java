package no.auke.events.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.events.persistdom.EventStatus;


//
// Calculating each event
//

public abstract class EventRunner  {

	private String eventid;
	public String getEventid() {return eventid;}

	private long starttime;
	public long getStarttime() {return starttime;}

	private AtomicLong hits=new AtomicLong();
	public long getHits() {return hits.get();}

	private AtomicInteger currentpos= new AtomicInteger();
	public int getCurrentpos() {return currentpos.get();}

	private int timeslot_period=5000;
	public int getTimeslot_period() {return timeslot_period;}

	protected Map<Integer, ResultSlot> resultslots = new ConcurrentHashMap<Integer, ResultSlot>();
	public List<ResultSlot> getResultSlots() {return new ArrayList<ResultSlot>(resultslots.values());}

	protected Map<Integer, TimeFrame> timeframes = new ConcurrentHashMap<Integer, TimeFrame>();
	public List<TimeFrame> getTimeframes() {return new ArrayList<TimeFrame>(timeframes.values());}

	protected Queue<ResultSlot> calculated_slots = new ConcurrentLinkedQueue<ResultSlot>();
	public Queue<ResultSlot> getCalculated_slots() {return calculated_slots;}

	protected ObjectMapper objectMapper = new ObjectMapper();

	protected String persistDir;
	protected String persistDirPos;

	public void hit() {
		hits.incrementAndGet();
	}


	public void saveSlots() {

		if(calculated_slots.size()>0) {

			while(calculated_slots.peek()!=null) {
				try {
					ResultSlot slot = calculated_slots.poll();
					if(slot!=null) {
						objectMapper.writeValue(new File(persistDirPos + "/pos-" + String.valueOf(slot.currentpos) + ".json"), slot);
					}
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void persist() {

		try {

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
			objectMapper.writeValue(new File(persistDir + "/status.json"), status);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public EventRunner(String eventid, int timeslot_period) {
		this.eventid=eventid;
		this.timeslot_period=timeslot_period;
	}

	// init and read up even informations
	public void init() {

		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		starttime=System.currentTimeMillis();

		persistDir = EventBroker.reportDir + eventid;
		new File(persistDir).mkdir();
		persistDirPos=persistDir + "/pos";
		new File(persistDirPos).mkdir();

		// persist status

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


		// save slots
		new Thread(new Runnable() {
			@Override
			public void run() {
				// persist status
				while (!stopthread.get()) {
					saveSlots();
					try {
						Thread.sleep(1000*10);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();

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

			ResultSlot slot=null;
			if(!resultslots.containsKey(slotpos)) {
				slot = newResultSlot();
				slot.currentpos = currentpos.get();
			} else {
				slot = resultslots.get(slotpos);
			}
			for(UserSession user:timeframe.getUserSessions()) {
				if(user.isOpen()) {
					executeResponse(user, slot);
				}
			}
			executeResult(slot);
			if(slot.isresult) {
				resultslots.put(slot.currentpos, slot);
				timeframe.setResultslot(slot);
			}

		}

	}

	/*
	public List<ResultSlot> readResultslots() {
		List<ResultSlot> ret = new ArrayList<ResultSlot>();
		for(TimeFrame timeframe:timeframes.values()) {
			ret.add(timeframe.readResultslot());
        }
		return ret;
	}
	 */

	protected abstract void executeResponse(UserSession usersession,ResultSlot slot);
	protected abstract void executeResult(ResultSlot slot);
	protected abstract ResultSlot newResultSlot();

}
