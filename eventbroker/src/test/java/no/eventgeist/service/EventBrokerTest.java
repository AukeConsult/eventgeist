package no.eventgeist.service;

import static org.junit.Assert.*;

import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import no.matchgeist.service.event.FootballEvent;

import static org.mockito.Mockito.*;

import java.util.List;

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
		assertEquals(10, event.getTimeframes().get(0).getUsersessions().size());
    }
	
	@Test
	public void test_init() {

		for(int i=0;i<10;i++){
			event.addUser(new UserSession(null, event, "per" + i, "", "", 10));
		}
		assertEquals(2, event.getTimeframes().size());
		assertEquals(10, event.getTimeframes().get(1).getUsersessions().size());
		
	}
	
	@Test
	public void test_calculate() {
		
		List<UserSession> usersessions = event.getUserSessions();

		for(int i=0;i<10;i++){
			event.addUser(new UserSession(null, event, "per" + i, "", "", 10));
		}
		assertEquals(2, event.getTimeframes().size());
		assertEquals(10, event.getTimeframes().get(1).getUsersessions().size());
		
	}	

}
