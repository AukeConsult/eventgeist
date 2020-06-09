package no.auke.mg.services;

import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.mg.channel.TimeFrame;
import no.auke.mg.channel.UserSession;
import no.auke.mg.channel.feedbacks.FeedBack;

public abstract class JsonMonitor extends Monitor {

	AtomicBoolean stopthread = new AtomicBoolean();
	ObjectMapper objectMapper = new ObjectMapper();

	public JsonMonitor() {}

	@Override
	public void init() {

		// Make hart beat and calculate incoming responses pr. timeslot

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

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
								FeedBack feeback = frame.readResults().feedback;
								if(feeback!=null) {
									String frameresult = objectMapper.writeValueAsString(feeback);
									sendTimeFrame(frame,frameresult);
									for(UserSession usersession:frame.getUserSessions()) {
										sendSession(usersession, frameresult);
									}
								}
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}

						}
					}

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

	public abstract void sendTimeFrame(TimeFrame frame, String result);
	public abstract void sendSession(UserSession usersession, String result);


}
