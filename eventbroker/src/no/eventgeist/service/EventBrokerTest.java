package no.eventgeist.service;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.matchgeist.service.event.FootballEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.eventgeist.service.*")

public class EventBrokerTest {
	
	final static Logger log = LoggerFactory.getLogger(EventBrokerTest.class);

	EventServer event;
	
    @Before
    public void start() {
    	
    	
    	event = new FootballEvent("test");
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
		
		for(int i=0;i<5;i++) {
			List<UserSession> usersessions = event.getUserSessions();
			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("#C");
				}
			}
			event.calculate();
			System.out.println(event.getTimeSlots().get(event.getTimeSlots().size()-1).result);
		}
		
	}
	
	class userThread implements Runnable {

		UserSession usersession;
		Random rnd = new Random();

		AtomicInteger cnt= new AtomicInteger(0);
		AtomicBoolean closed = new AtomicBoolean(false);
		
		public userThread(EventServer event, String userid) {
			usersession = new UserSession(null, event, userid, (rnd.nextInt()>0?"team1":"team2"), "", 0);
			event.addUser(usersession);
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			
			while(cnt.get()<10) {
				try {
					
					Thread.sleep(50 + rnd.nextInt(2000));
					if(rnd.nextInt(4)==0) {
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

		final int calctime = 1000;
		Long time = System.currentTimeMillis();
		new Thread(new Runnable(){

			int cnt=0;
			@Override
			public void run() {
				int nextwait = calctime;
				while(!closed.get()) {
					try {
						Thread.sleep(nextwait);
						System.out.println("calculate " + String.valueOf(System.currentTimeMillis() - time));
						Long startcalc = System.currentTimeMillis();
						event.calculate();
						nextwait = (int) (calctime - (System.currentTimeMillis() - startcalc)); 
						System.out.println("calculate finish " + String.valueOf(System.currentTimeMillis() - startcalc));

						//System.out.println("calculate finish");
						if(event.getTimeSlots().size()>0){
							System.out.println(event.getTimeSlots().get(event.getTimeSlots().size()-1).result);
						}						
						cnt++;	
					} catch (InterruptedException e) {
					}
				}
				
			}}).start();
		
		List<userThread> workers = new ArrayList<userThread>();	
		
		for(int i=0;i<5000;i++) {
			workers.add(new userThread(event,"user"+i));
		}
		
		while(!closed.get()) {
			try {
				Thread.sleep(2000);
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
