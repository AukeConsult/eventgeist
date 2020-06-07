package no.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.auke.mg.event.EventService;
import no.auke.mg.event.Storage;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.ResultSlot;
import no.auke.mg.eventimpl.football.FootballEvent;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class EventBrokerTest {

	final static Logger log = LoggerFactory.getLogger(EventBrokerTest.class);

	class TestStorage extends Storage {

		@Override
		public void doSave() {
			// TODO Auto-generated method stub

		}

		@Override
		public void readAll() {
			// TODO Auto-generated method stub

		}

		@Override
		public EventInfo readEvent(String eventid) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<ResultSlot> readSlots(String eventid, int slotpos) {
			// TODO Auto-generated method stub
			return null;
		}

	}


	EventService event;

	TestJsonMonitor monitor;
	TestStorage storage;

	@Before
	public void start() {

		monitor = new TestJsonMonitor();
		storage = new TestStorage();

		event = new FootballEvent(new EventInfo("test"), monitor, storage);
		event.init();
		event.stop();

		for(int i=0;i<5;i++){
			event.addUser(new UserSession(String.valueOf(i), event, "leifx" + i, "team1", "", 0));
		}
		for(int i=5;i<10;i++){
			event.addUser(new UserSession(String.valueOf(i), event, "leify" + i, "team2", "", 0));
		}
		assertEquals(1, event.getTimeframes().size());

		assertEquals("error users", 10, event.getTimeframes().get(0).getUserSessions().size());
		storage.doSave();

	}

	@Test
	public void test_init() {

		log.info("start init");
		System.out.println("test_init");

		for(int i=0;i<10;i++){
			event.addUser(new UserSession(null, event, "per" + i, "", "", 10));
		}
		assertEquals(2, event.getTimeframes().size());
		assertEquals(10, event.getTimeframes().get(1).getUserSessions().size());

		event.stop();
		log.info("end init");


	}

	@Test
	public void test_calculate_json() throws JsonProcessingException {

		System.out.println("test_calculate_json");

		ObjectMapper objectMapper = new ObjectMapper();

		Random rnd = new Random();

		List<UserSession> usersessions = event.getUserSessions();

		for(int i=0;i<100;i++) {

			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("C#btn1");
					//session.addResponse("SETST#status1#Melding status dit eller datt");
				}
				if(rnd.nextInt(7)==0) {
					//session.addResponse("SETST#status2#Melding status dit eller datt");
					session.addResponse("C#btn2");
				}
			}

			event.calculate();
			Assert.assertTrue(event.getResultSlots().size()>0);

		}
		for(ResultSlot slot:event.getResultSlots()) {
			Assert.assertNotNull(slot.feedback);
		}
		for(int i=0;i<100;i++) {
			event.calculate();
		}
		for(ResultSlot slot:event.getResultSlots()) {
			if(slot.feedback!=null) {
				System.out.println(objectMapper.writeValueAsString(slot.feedback));
			}
		}

	}


	class userThread implements Runnable {

		UserSession usersession;
		Random rnd = new Random();

		AtomicInteger cnt= new AtomicInteger(0);
		AtomicBoolean closed = new AtomicBoolean(false);

		public userThread(EventService event, String userid) {
			usersession = new UserSession(userid, event, userid, (rnd.nextInt()>0?"team1":"team2"), "", 0);
			event.addUser(usersession);
			new Thread(this).start();
		}

		@Override
		public void run() {

			while(cnt.get()<100) {
				try {
					Thread.sleep(50 + rnd.nextInt(2000));
					if(rnd.nextInt(3)==0) {
						usersession.addResponse("C#0");
						cnt.incrementAndGet();
					}
					if(rnd.nextInt(10)==0) {
						usersession.addResponse("C#1");
						cnt.incrementAndGet();
					}
				} catch (InterruptedException e) {
				}
			}
			closed.set(true);

		}

	}

	@Test
	public void test_calculate_random() {

		System.out.println("calculate random");
		AtomicBoolean closed = new AtomicBoolean(false);

		int num_usersessions=100;

		final int calctime = 1000;
		Long time = System.currentTimeMillis();
		new Thread(new Runnable(){

			int cnt=0;
			@Override
			public void run() {
				int nextwait = calctime;
				while(!closed.get()) {
					try {

						if(nextwait>0) {
							Thread.sleep(nextwait);
						}

						System.out.println("calculate " + String.valueOf(System.currentTimeMillis() - time));
						Long startcalc = System.currentTimeMillis();

						event.calculate();
						event.emptyResults();

						nextwait = (int) (calctime - (System.currentTimeMillis() - startcalc));

						//System.out.println("calculate finish");
						if(event.getResultSlots().size()>0){
							//System.out.println(event.getResultSlots().get(event.getResultSlots().size()-1).resultString);
						}
						System.out.println("calculate time " + String.valueOf(System.currentTimeMillis() - startcalc));

						cnt++;
					} catch (InterruptedException e) {
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}

			}}).start();


		List<userThread> workers = new ArrayList<userThread>();

		for(int i=0;i<num_usersessions;i++) {
			workers.add(new userThread(event,"user"+i));
		}

		while(!closed.get()) {
			try {
				Thread.sleep(2000);
				event.persist();
				int cnt=0;
				for(userThread worker:workers) {
					if(!worker.closed.get()) {
						cnt++;
					}
				}
				if(cnt==0) {
					closed.set(true);
				}
				System.out.println("alive " + cnt);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("calculate random finish");

	}
}
