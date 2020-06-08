package no.auke.mg.broker;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.mg.event.TimeFrame;
import no.auke.mg.event.UserSession;
import no.auke.mg.event.feedbacks.FeedBack;
import no.auke.mg.services.Monitor;

public class WsMonitor extends Monitor {

	AtomicBoolean stopthread = new AtomicBoolean();

	private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	ObjectMapper objectMapper = new ObjectMapper();


	public WsMonitor() {}

	//public void addSession(Session session) {
	//	sessions.put(session.getId(),session);
	//}

	@Override
	public void init() {

		// Make hart beat and calculate incoming responses pr. timeslot

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		new Thread(new Runnable() {

			void sendSession(UserSession usersession, String result) {

				System.out.println("push user:" + usersession.getUserid() + ":" + usersession.getUserid() + ":" + result);
				if (usersession.isOpen()) {
					try {
						sessions.get(usersession.getId()).getBasicRemote().sendText(result);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					//TODO add logging
					System.out.println("close " + usersession.getId());
					sessions.remove(usersession.getId());
					usersession.close();
				}

			}

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
