package no.test;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.feedbacks.FeedBack;
import no.auke.mg.services.Monitor;

public class TestJsonMonitor extends Monitor {

	AtomicBoolean stopthread = new AtomicBoolean();

	public TestJsonMonitor() {}

	ObjectMapper objectMapper = new ObjectMapper();

	public final void sendSession(UserSession usersession, String result) {
		if (usersession.isOpen()) {
			System.out.println(result);
		}
	}

	@Override
	public void init() {

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		// Make hart beat and calculate incoming responses pr. timeslot
		new Thread(new Runnable() {

			@Override
			public void run() {

				// calculating results
				while (!stopthread.get()) {

					// send to all users connected to this timeframe
					while(!send_frames.isEmpty()) {
						TimeFrame frame = send_frames.poll();
						if(frame!=null) {
							try {
								String frameresult = objectMapper.writeValueAsString(frame.readResults());
								if(frameresult!=null) {
									for(UserSession usersession:frame.getUserSessions()) {
										sendSession(usersession, frameresult);
									}
								}
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						}
					}

					// send to user after a command
					// TODO: not finish
					while(!send_users.isEmpty()) {
						UserSession usersession = send_users.poll();
						if(usersession!=null && usersession.readResults() !=null && usersession.readResults().size()>0) {
							try {
								for(FeedBack result:usersession.readResults()) {
									sendSession(usersession, objectMapper.writeValueAsString(result));
								}
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

		}).start();

	};


}
