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
import no.auke.mg.channel.Message;
import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.Status;
import no.auke.mg.channel.TimeFrame;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.impl.football.FootballChannel;
import no.auke.mg.channel.impl.football.FootballFeedback;
import no.auke.mg.channel.models.ChannelInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class FootballTest {

	final static Logger log = LoggerFactory.getLogger(FootballTest.class);

	ChannelService channel;

	TestMonitor monitor = new TestMonitor();
	TestStorage storage = new TestStorage();

	@Before
	public void start() {

		ChannelInfo info = ChannelInfo.create("test");
		storage.saveChannelInfo(info);

		channel = new FootballChannel(info);
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
	public void test_read_feedback() throws JsonProcessingException {

		System.out.println("test_read_feedback");

		List<UserSession> usersessions = channel.getUserSessions();
		for(int i=0;i<100;i++) {

			for(UserSession session:usersessions){
				session.addResponse("C#btn1");
				session.addResponse("C#btn2");
			}

			Assert.assertTrue(monitor.getSend_frames().size()==0);

			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==1);

			TimeFrame frame = monitor.getSend_frames().poll();
			Assert.assertTrue(monitor.getSend_frames().size()==0);

			Assert.assertNotNull(frame.readFeedBack());
			Assert.assertNull(frame.readFeedBack());

			monitor.clear();

		}

	}

	@Test
	public void test_empty() throws JsonProcessingException {

		System.out.println("test_empty");

		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("C#btn1");
			session.addResponse("C#btn2");
		}

		channel.collect();
		channel.calculate();
		Assert.assertTrue(monitor.getSend_frames().size()>0);
		monitor.clear();

		for(int i=0;i<channel.getAvg2period()+2;i++) {
			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()>0);
			monitor.clear();
		}

		for(int i=0;i<1000;i++) {
			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==0);
			monitor.clear();
		}

	}


	@Test
	public void test_storage() throws JsonProcessingException {

		System.out.println("test_storage");

		List<UserSession> usersessions = channel.getUserSessions();
		for(int i=0;i<100;i++) {

			for(UserSession session:usersessions){
				session.addResponse("C#btn1");
				session.addResponse("C#btn2");
			}
			channel.collect();
			channel.calculate();
			Assert.assertTrue(storage.getResultSlots("test").size()==i+1);
			monitor.clear();

		}

		for(ResultSlot slot:storage.getResultSlots("test")) {
			Assert.assertNotNull(slot.feedback);
		}

	}


	@Test
	public void test_calculate_one_btn() throws JsonProcessingException {

		System.out.println("test_calculate_one_btn");

		Assert.assertTrue(storage.getResultSlots("test").size()==0);

		List<UserSession> usersessions = channel.getUserSessions();
		for(int i=0;i<5;i++) {
			for(UserSession session:usersessions){
				session.addResponse("C#btn1");
			}
			channel.collect();
		}

		Assert.assertEquals(5,channel.getTimeframes().get(0).responses.size());
		channel.calculate();
		Assert.assertEquals(0,channel.getTimeframes().get(0).responses.size());


		Assert.assertTrue(storage.getResultSlots("test").size()==1);
		FootballFeedback result = (FootballFeedback) storage.getResultSlots("test").get(0).feedback;

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(1,result.teamwork.get("team1").btnwork.size());
		Assert.assertEquals(1,result.teamwork.get("team2").btnwork.size());

		Assert.assertEquals(50.0,result.teamwork.get("team1").btnwork.get("btn1").val,0);
		Assert.assertEquals(75.0,result.teamwork.get("team2").btnwork.get("btn1").val,0);

		monitor.clear();

	}

	@Test
	public void test_calculate_more_btn() throws JsonProcessingException {

		System.out.println("test_calculate_more_btn");

		List<UserSession> usersessions = channel.getUserSessions();
		Assert.assertEquals(0,channel.getTimeframes().get(0).responses.size());

		for(UserSession session:usersessions){
			for(int i=0;i<5;i++) {
				session.addResponse("C#btn" + i);
			}
		}
		Assert.assertEquals(0,channel.getTimeframes().get(0).responses.size());

		channel.collect();
		Assert.assertEquals(1,channel.getTimeframes().get(0).responses.size());

		channel.calculate();
		Assert.assertEquals(0,channel.getTimeframes().get(0).responses.size());

		Assert.assertTrue(monitor.getSend_frames().size()==1);
		Assert.assertTrue(storage.getResultSlots("test").size()==1);

		FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(5,result.teamwork.get("team1").btnwork.size());
		Assert.assertEquals(5,result.teamwork.get("team2").btnwork.size());

		for(int i=0;i<5;i++) {
			Assert.assertEquals(10.0,result.teamwork.get("team1").btnwork.get("btn"+i).val,0);
			Assert.assertEquals(15.0,result.teamwork.get("team2").btnwork.get("btn"+i).val,0);
		}

		monitor.clear();


	}

	@Test
	public void test_message() throws JsonProcessingException {

		System.out.println("test_message");
		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("M#M1#test1 M1 type");
			session.addResponse("M#test2 blanktype");
			session.addResponse("M#");
		}

		channel.collect();
		channel.calculate();

		Assert.assertTrue(monitor.getSend_frames().size()==1);

		FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.msg);

		Assert.assertEquals(usersessions.size()*2,result.msg.size());

		int cnt_m1=0;
		int cnt_blank=0;

		for(Message msg:result.msg) {
			cnt_m1 += (msg.getT()!=null && msg.getT().equals("M1")?1:0);
			cnt_blank += (msg.getT()==null ?1:0);
		}
		Assert.assertEquals(usersessions.size(),cnt_m1);
		Assert.assertEquals(usersessions.size(),cnt_blank);

		for(UserSession session:usersessions){


			int cnt_m1_u=0;
			int cnt_blank_u=0;

			for(Message msg:result.msg) {
				if(msg.getUid().equals(session.getUserid())) {
					cnt_m1_u += (msg.getT()!=null && msg.getT().equals("M1")?1:0);
					cnt_blank_u += (msg.getT()==null ?1:0);
				}
			}

			Assert.assertEquals(1,cnt_m1_u);
			Assert.assertEquals(1,cnt_blank_u);

		}


		monitor.clear();

	}

	@Test
	public void test_status() throws JsonProcessingException {

		System.out.println("test_status");

		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("ST#status1#test1");
			session.addResponse("ST#status2#test2");
		}

		channel.collect();
		channel.calculate();

		Assert.assertTrue(monitor.getSend_frames().size()==1);
		FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertEquals(2,result.st.size());

		Assert.assertEquals("status1",result.st.get(0).getT());
		Assert.assertEquals("status2",result.st.get(1).getT());

		monitor.clear();

	}

	@Test
	public void test_status_frequence() throws JsonProcessingException {

		System.out.println("test_status_frequence");

		List<UserSession> usersessions = channel.getUserSessions();
		for(UserSession session:usersessions){
			session.addResponse("ST#status1#test1");
			session.addResponse("ST#status2#test2");
		}

		channel.collect();
		channel.calculate();

		FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

		Assert.assertEquals(2,result.st.size());
		Status status1 = result.st.get(0);
		Status status2 = result.st.get(1);

		Assert.assertEquals("status1",status1.getT());
		Assert.assertEquals("status2",status2.getT());

		Assert.assertEquals("test1",status1.getJs());
		Assert.assertEquals("test2",status2.getJs());

		monitor.clear();

		for(int i=0;i<3;i++) {
			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()>0);
			monitor.clear();
		}

		for(int i=0;i<1000;i++) {

			channel.collect();
			channel.calculate();

			if(channel.getCurrentpos() % 5 == 0) {

				//System.out.println(channel.getCurrentpos());

				result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();
				Assert.assertNotNull(result);

				//Assert.assertEquals(2,result.st.size());
				//Assert.assertEquals("status1",result.st.get(0).getT());
				//Assert.assertEquals("status2",result.st.get(1).getT());

			} else {
				Assert.assertTrue(monitor.getSend_frames().size()==0);
			}

			monitor.clear();

		}

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
			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==1);
			monitor.print();
		}

	}

	@Test
	public void test_calculate_average() throws JsonProcessingException {

		System.out.println("test_calculate_average");

		channel.setAvg1period(5);
		channel.setAvg2period(30);

		Assert.assertEquals(5,channel.getAvg1period());
		Assert.assertEquals(30,channel.getAvg2period());

		for(int x=0;x<channel.getAvg2period()*2;x++) {
			for(UserSession session:channel.getUserSessions()){
				session.addResponse("C#btn1");
				session.addResponse("C#btn2");
			}
			channel.collect();
			channel.calculate();
			Assert.assertTrue(monitor.getSend_frames().size()==1);
			monitor.clear();

		}

		for(int x=0;x<channel.getAvg2period() - 1 ;x++) {

			channel.collect();
			channel.calculate();

			FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

			Assert.assertEquals(0,result.teamwork.get("team1").btnwork.get("btn1").val,0);
			Assert.assertEquals(0,result.teamwork.get("team1").btnwork.get("btn2").val,0);
			Assert.assertEquals(0,result.teamwork.get("team2").btnwork.get("btn1").val,0);
			Assert.assertEquals(0,result.teamwork.get("team2").btnwork.get("btn2").val,0);

			Assert.assertEquals(0,result.teamwork.get("team1").totwork.val,0);
			Assert.assertEquals(0,result.teamwork.get("team2").totwork.val,0);

			if(x>=channel.getAvg1period() - 1) {

				Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").avg1==0);
				Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn2").avg1==0);

				Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").avg1==0);
				Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn2").avg1==0);


			} else {

				Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").avg1>0);
				Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn2").avg1>0);

				Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").avg1>0);
				Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn2").avg1>0);

			}

			Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").avg2>0);
			Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn2").avg2>0);

			Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").avg2>0);
			Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn2").avg2>0);

		}

		channel.collect();
		channel.calculate();

		FootballFeedback result = (FootballFeedback) monitor.getSend_frames().poll().readFeedBack();

		Assert.assertNotNull(result.teamwork.get("team1"));
		Assert.assertNotNull(result.teamwork.get("team2"));

		Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").avg2==0);
		Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn2").avg2==0);

		Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").avg2==0);
		Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn2").avg2==0);

		Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn1").avg1==0);
		Assert.assertTrue(result.teamwork.get("team1").btnwork.get("btn2").avg1==0);

		Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn1").avg1==0);
		Assert.assertTrue(result.teamwork.get("team2").btnwork.get("btn2").avg1==0);

		Assert.assertTrue(result.teamwork.get("team1").totwork.val==0);
		Assert.assertTrue(result.teamwork.get("team2").totwork.val==0);


	}



}
