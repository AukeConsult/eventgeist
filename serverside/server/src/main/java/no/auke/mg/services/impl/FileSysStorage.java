package no.auke.mg.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.PersistObject;
import no.auke.mg.services.Storage;

public class FileSysStorage extends Storage {

	private String location="";
	protected ObjectMapper objectMapper = new ObjectMapper();

	public FileSysStorage(String location) {
		this.location=location;
		new File(location).mkdir();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	private void saveQueue(Queue<Object> queue, String location, String fileext) {

		while(queue.size()>0) {

			try {

				PersistObject obj = (PersistObject) queue.poll();
				new File(location).mkdir();
				String filename=location + "/" + obj.getPersistName() + fileext;

				objectMapper.writeValue(new File(filename), obj);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	@Override
	public void doSave() {

		try {

			saveQueue(save_channels, location+"/channels",".json");
			saveQueue(save_events, location+"/events",".json");
			saveQueue(save_channelstatuses, location+"/channels",".json");
			saveQueue(save_channelstatuses, location+"/channels/slots","-slot.json");

		} catch (Exception e) {
			e.printStackTrace();
		}


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
