package no.test;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.auke.mg.channel.TimeFrame;
import no.auke.mg.channel.UserSession;
import no.auke.mg.services.JsonMonitor;

public class TestJsonMonitor extends JsonMonitor {

	ObjectMapper objectMapper = new ObjectMapper();
	AtomicBoolean stopthread = new AtomicBoolean();

	public TestJsonMonitor() {}

	@Override
	public final void sendSession(UserSession usersession, String result) {}

	@Override
	public void sendTimeFrame(TimeFrame frame, String result) {
		System.out.println(result);
	};


}
