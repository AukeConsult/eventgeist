package no.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.auke.mg.channel.ChannelService;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.impl.football.FootballChannel;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.services.impl.FileSysStorage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "no.auke.mg.*")
public class FileSysStorageTest {

	final static Logger log = LoggerFactory.getLogger(FileSysStorageTest.class);

	ChannelService channel;

	TestMonitor monitor = new TestMonitor();
	FileSysStorage storage;

	@Before
	public void start() {

		ChannelInfo info = ChannelInfo.create("test");

		String userDir = System.getProperty("user.dir");

		storage=new FileSysStorage(userDir+"\\test_storage");

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
		storage.doSave();

	}

	@Test
	public void test_create_channels() {

		System.out.println("test_create_channels");

	}

}
