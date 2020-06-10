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

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channelimpl.football.FootballChannel;
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
		public ChannelInfo readhannel(String eventid) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<ResultSlot> readSlots(String eventid, int slotpos) {
			// TODO Auto-generated method stub
			return null;
		}

	}


	ChannelService channel;

	TestJsonMonitor monitor;
	TestStorage storage;

	@Before
	public void start() {

		monitor = new TestJsonMonitor();
		storage = new TestStorage();

		channel = new FootballChannel(new ChannelInfo("test"), monitor, storage);
		channel.init();

	}

	class userThread implements Runnable {

		UserSession usersession;
		Random rnd = new Random();

		AtomicInteger cnt= new AtomicInteger(0);
		AtomicBoolean closed = new AtomicBoolean(false);

		public userThread(ChannelService channel, String userid) {
			usersession = new UserSession(userid, channel, userid, (rnd.nextInt()>0?"t1":"t2"), "", 0);
			channel.addUser(usersession);
			new Thread(this).start();
		}

		@Override
		public void run() {

			while(cnt.get()<100) {
				try {
					Thread.sleep(50 + rnd.nextInt(2000));
					if(rnd.nextInt(3)==0) {
						usersession.addResponse("C#b1");
						cnt.incrementAndGet();
					}
					if(rnd.nextInt(10)==0) {
						usersession.addResponse("C#b2");
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

		int num_usersessions=10000;

		channel.getMonitor().init();

		List<userThread> workers = new ArrayList<userThread>();

		for(int i=0;i<num_usersessions;i++) {
			workers.add(new userThread(channel,"user"+i));
		}

		while(!closed.get()) {
			try {
				Thread.sleep(2000);
				channel.persist();
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
