package no.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.auke.mg.channel.TimeFrame;
import no.auke.mg.services.Monitor;

public class TestMonitor extends Monitor {

	ObjectMapper objectMapper = new ObjectMapper();
	public TestMonitor() {Monitor.instance=this;}
	@Override
	public void init() {}

	public void print() {
		while(!send_frames.isEmpty()) {
			TimeFrame frame = send_frames.poll();
			if(frame!=null) {
				try {
					Object feedback = frame.readFeedBack();
					if(feedback!=null) {
						String result = objectMapper.writeValueAsString(feedback);
						System.out.println(result);
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
	}
}