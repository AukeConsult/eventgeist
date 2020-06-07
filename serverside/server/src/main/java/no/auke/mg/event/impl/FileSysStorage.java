package no.auke.mg.event.impl;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import no.auke.mg.event.Storage;
import no.auke.mg.event.models.EventInfo;
import no.auke.mg.event.models.ResultSlot;

public class FileSysStorage extends Storage {

	private String location="";
	protected ObjectMapper objectMapper = new ObjectMapper();

	public FileSysStorage(String location) {
		this.location=location;
		new File(location).mkdir();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	@Override
	public void doSave() {

		try {

			//String persistloc=location + "/" + eventinfo.getEventid();
			//new File(persistloc).mkdir();
			//objectMapper.writeValue(new File(location + "/info.json"), eventinfo);

			//String persistloc=location + "/" + eventinfo.getEventid();
			//new File(persistloc).mkdir();
			//events.put(eventinfo.getEventid(), eventinfo);
			//objectMapper.writeValue(new File(location + "/info.json"), eventinfo);

			//String persistloc=location + "/" + status.getEventid();
			//new File(persistloc).mkdir();
			objectMapper.writeValue(new File(location + "/status.json"), "");


		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void readAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public EventInfo readEvent(String eventid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResultSlot> readSlots(String eventid, int slotpos) {
		// TODO Auto-generated method stub
		return null;
	}

}
