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

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channelimpl.football.FootballChannel;
import no.auke.mg.channelimpl.football.FootballFeedback;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class FootballTest {

	final static Logger log = LoggerFactory.getLogger(FootballTest.class);


	ChannelService channel;

	TestMonitor monitor = new TestMonitor();
	TestStorage storage = new TestStorage();

	@Before
	public void start() {

		channel = new FootballChannel(new ChannelInfo("test"), monitor, storage);
		channel.init();
		channel.stop();

		for(int i=0;i<10;i++){
			channel.addUser(new UserSession(String.valueOf(i), channel, "leifx" + i, "team1", "", 0));
		}
		for(int i=10;i<25;i++){
			channel.addUser(new UserSession(String.valueOf(i), channel, "leify" + i, "team2", "", 0));
		}
		assertEquals(1, channel.getTimeframes().size());

		assertEquals("error users", 25, channel.getTimeframes().get(0).getUserSessions().size());
		storage.doSave();

	}

	@Test
	public void test_calculate_one_btn() throws JsonProcessingException {

		System.out.println("test_calculate_one_btn");

		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("C#btn1");
		}
		channel.calculate();

		Assert.assertTrue(storage.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) storage.getResultSlots().get(0).feedback;

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

		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			for(int i=0;i<5;i++) {
				session.addResponse("C#btn" + i);
			}
		}
		channel.calculate();
		Assert.assertTrue(monitor.getSend_frames().size()==1);

		Assert.assertTrue(storage.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) storage.getResultSlots().get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(5,result.teamwork.get("team1").btnwork.size());
		Assert.assertEquals(5,result.teamwork.get("team2").btnwork.size());

		monitor.print();


	}

	@Test
	public void test_message() throws JsonProcessingException {

		System.out.println("test_message");
		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("M#M1#test " + session.getUserid());
			session.addResponse("M#test2" + session.getUserid());
			session.addResponse("M#" + session.getUserid());
		}
		channel.calculate();
		Assert.assertTrue(monitor.getSend_frames().size()==1);
		Assert.assertTrue(storage.getResultSlots().size()==1);

		FootballFeedback result = (FootballFeedback) storage.getResultSlots().get(0).feedback;
		Assert.assertNotNull(result);
		monitor.print();


	}

	@Test
	public void test_status() throws JsonProcessingException {

		System.out.println("test_status");
		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("ST#status1#test1");
			session.addResponse("ST#status2#test2");
		}
		channel.calculate();

		Assert.assertTrue(storage.getResultSlots().size()==1);
		FootballFeedback result = (FootballFeedback) storage.getResultSlots().get(0).feedback;
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
			channel.addUser(new UserSession(String.valueOf("x"+i), channel, "leifww" + i, "team1", "", 0));
			channel.addUser(new UserSession(String.valueOf("z"+i), channel, "leifxx" + i, "team2", "", 0));
		}

		List<UserSession> usersessions = channel.getUserSessions();

		for(int x=0;x<10;x++) {
			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("C#btn1");
				}
				if(rnd.nextInt(7)==0) {
					session.addResponse("C#btn2");
				}
			}
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==1);
			monitor.print();
		}


	}

	@Test
	public void test_calculate_json() throws JsonProcessingException {

		System.out.println("test_calculate_json");
		Random rnd = new Random();

		List<UserSession> usersessions = channel.getUserSessions();
		for(int i=0;i<100;i++) {

			for(UserSession session:usersessions){
				if(rnd.nextInt(3)==0) {
					session.addResponse("C#btn1");
				}
				if(rnd.nextInt(7)==0) {
					//session.addResponse("C#btn2");
				}
			}
			channel.calculate();
			Assert.assertTrue(storage.getResultSlots().size()>0);
			monitor.print();

		}
		for(ResultSlot slot:storage.getResultSlots()) {
			Assert.assertNotNull(slot.feedback);
		}
		for(int i=0;i<30;i++) {
			channel.calculate();
			monitor.print();
		}
	}

}
