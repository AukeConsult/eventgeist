package no.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.auke.mg.event.EventService;
import no.auke.mg.event.ResultSlot;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.eventimpl.football.FootballEvent;
import no.auke.mg.services.Storage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class MassProdTest {

	final static Logger log = LoggerFactory.getLogger(MassProdTest.class);

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

		int num_usersessions=1000;

		final int calctime =2000;
		Long time = System.currentTimeMillis();
		new Thread(new Runnable() {

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
