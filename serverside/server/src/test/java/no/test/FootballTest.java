package no.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Random;

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
import no.auke.mg.event.ResultSlot;
import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.eventimpl.football.FootballEvent;
import no.auke.mg.eventimpl.football.FootballFeedback;
import no.auke.mg.services.Monitor;
import no.auke.mg.services.Storage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class FootballTest {

	final static Logger log = LoggerFactory.getLogger(FootballTest.class);

	public class TestStorage extends Storage {

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

	public class TestMonitor extends Monitor {

		@Override
		public void init() {}

		public void print() {
			while(!send_frames.isEmpty()) {
				TimeFrame frame = send_frames.poll();
				if(frame!=null) {
					try {
						Object feedback = frame.readResults().feedback;
						if(feedback!=null) {
							System.out.println(objectMapper.writeValueAsString(feedback));
						}
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	EventService event;

	TestMonitor monitor;
	TestStorage storage;

	ObjectMapper objectMapper = new ObjectMapper();

	@Before
	public void start() {

		monitor = new TestMonitor();
		storage = new TestStorage();

		event = new FootballEvent(new EventInfo("test"), monitor, storage);
		event.init();
		event.stop();

		for(int i=0;i<10;i++){
			event.addUser(new UserSession(String.valueOf(i), event, "leifx" + i, "team1", "", 0));
		}
		for(int i=10;i<25;i++){
			event.addUser(new UserSession(String.valueOf(i), event, "leify" + i, "team2", "", 0));
		}
		assertEquals(1, event.getTimeframes().size());

		assertEquals("error users", 25, event.getTimeframes().get(0).getUserSessions().size());
		storage.doSave();

	}

	@Test
	public void test_calculate_one_btn() throws JsonProcessingException {

		System.out.println("test_calculate_one_btn");

		List<UserSession> usersessions = event.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("C#btn1");
		}
		event.calculate();

		Assert.assertTrue(event.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) event.getResultSlots().get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(1,result.teamwork.get("team1").btnwork.size());
		Assert.assertEquals(1,result.teamwork.get("team2").btnwork.size());

		Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").val==10.0);
		Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").val==15.0);

		monitor.print();

	}

	@Test
	public void test_calculate_more_btn() throws JsonProcessingException {

		System.out.println("test_calculate_more_btn");

		List<UserSession> usersessions = event.getUserSessions();
		for(UserSession session:usersessions){
			for(int i=0;i<5;i++) {
				session.addResponse("C#btn" + i);
			}
		}
		event.calculate();
		Assert.assertTrue(monitor.getSend_frames().size()==1);

		Assert.assertTrue(event.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) event.getResultSlots().get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(5,result.teamwork.get("team1").btnwork.size());
		Assert.assertEquals(5,result.teamwork.get("team2").btnwork.size());

		monitor.print();


	}

	@Test
	public void test_message() throws JsonProcessingException {

		System.out.println("test_message");
		List<UserSession> usersessions = event.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("M#M1#test " + session.getUserid());
		}
		event.calculate();
		Assert.assertTrue(monitor.getSend_frames().size()==1);

		Assert.assertTrue(event.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) event.getResultSlots().get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(25,result.msg.size());

		monitor.print();


	}

	@Test
	public void test_status() throws JsonProcessingException {

		System.out.println("test_status");
		List<UserSession> usersessions = event.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("ST#status1#test1");
			session.addResponse("ST#status2#test2");
		}
		event.calculate();

		Assert.assertTrue(event.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) event.getResultSlots().get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(2,result.st.size());
		Assert.assertEquals("status1",result.st.get(0).getT());
		Assert.assertEquals("status2",result.st.get(1).getT());

		monitor.print();


	}

	@Test
	public void test_calculate_1000() throws JsonProcessingException {

		System.out.println("test_calculate_1000");
		Random rnd = new Random();

		for(int i=0;i<1000;i++){
			event.addUser(new UserSession(String.valueOf("x"+i), event, "leifww" + i, "team1", "", 0));
			event.addUser(new UserSession(String.valueOf("z"+i), event, "leifxx" + i, "team2", "", 0));
		}

		List<UserSession> usersessions = event.getUserSessions();

		for(int x=0;x<10;x++) {
			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("C#btn1");
				}
				if(rnd.nextInt(7)==0) {
					session.addResponse("C#btn2");
				}
			}
			event.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==1);
			monitor.print();
		}

		Assert.assertTrue(event.getResultSlots().size()==10);
		for(ResultSlot slot:event.getResultSlots()) {
			Assert.assertNotNull(slot.feedback);
		}

	}


	@Test
	public void test_calculate_json() throws JsonProcessingException {

		System.out.println("test_calculate_json");
		Random rnd = new Random();

		List<UserSession> usersessions = event.getUserSessions();
		for(int i=0;i<100;i++) {

			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("C#btn1");
				}
				if(rnd.nextInt(7)==0) {
					//session.addResponse("C#btn2");
				}
			}
			event.calculate();
			Assert.assertTrue(event.getResultSlots().size()>0);
			monitor.print();

		}
		for(ResultSlot slot:event.getResultSlots()) {
			Assert.assertNotNull(slot.feedback);
		}
		for(int i=0;i<30;i++) {
			event.calculate();
			monitor.print();
		}
	}

}
