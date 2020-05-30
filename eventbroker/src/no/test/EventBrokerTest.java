package no.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.auke.events.service.EventRunner;
import no.auke.events.service.UserSession;
import no.auke.events.service.event.FootballEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.events.service.*")

public class EventBrokerTest {
	
	final static Logger log = LoggerFactory.getLogger(EventBrokerTest.class);

	EventRunner event;
	
    @Before
    public void start() {
    	
    	event = new FootballEvent("test",5000);
    	event.init();
    	
    	event.stopThreads();    	
    	for(int i=0;i<5;i++){
			event.addUser(new UserSession(null, event, "leifx" + i, "team1", "", 0));
		}
    	for(int i=0;i<5;i++){
			event.addUser(new UserSession(null, event, "leify" + i, "team2", "", 0));
		}
    	assertEquals(1, event.getTimeframes().size());
		assertEquals(10, event.getTimeframes().get(0).getUserSessions().size());
    }
	
	@Test
	public void test_init() {

		for(int i=0;i<10;i++){
			event.addUser(new UserSession(null, event, "per" + i, "", "", 10));
		}
		assertEquals(2, event.getTimeframes().size());
		assertEquals(10, event.getTimeframes().get(1).getUserSessions().size());
		
	}
	
	@Test
	public void test_calculate() {

		Random rnd = new Random();
		
			List<UserSession> usersessions = event.getUserSessions();
			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("#C");
				}
			}						
			event.calculate();
			Assert.assertTrue(event.getResultSlots().size()>0);
			event.readResultslots();
			Assert.assertTrue(event.getCalculated_slots().size()>0);
			event.saveSlots();
			
			System.out.println(event.getResultSlots().get(event.getResultSlots().size()-1).resultString);
		
	}
	
	class userThread implements Runnable {

		UserSession usersession;
		Random rnd = new Random();

		AtomicInteger cnt= new AtomicInteger(0);
		AtomicBoolean closed = new AtomicBoolean(false);
		
		public userThread(EventRunner event, String userid) {
			usersession = new UserSession(null, event, userid, (rnd.nextInt()>0?"team1":"team2"), "", 0);
			event.addUser(usersession);
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			
			while(cnt.get()<100) {
				try {
					Thread.sleep(50 + rnd.nextInt(2000));
					if(rnd.nextInt(3)==0) {
						usersession.addResponse("#C0");
						cnt.incrementAndGet();
					}
					if(rnd.nextInt(10)==0) {
						usersession.addResponse("#C1");
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
						event.readResultslots();
						event.saveSlots();
						
						nextwait = (int) (calctime - (System.currentTimeMillis() - startcalc)); 

						//System.out.println("calculate finish");
						if(event.getResultSlots().size()>0){
							System.out.println(event.getResultSlots().get(event.getResultSlots().size()-1).resultString);
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
				event.saveSlots();

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
