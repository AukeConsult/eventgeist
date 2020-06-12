package no.test;

import java.util.List;

import no.auke.mg.channel.ResultSlot;
import no.auke.mg.channel.models.ChannelInfo;
import no.auke.mg.channel.models.ChannelStatus;
import no.auke.mg.channel.models.EventInfo;
import no.auke.mg.services.Storage;

public class TestStorage extends Storage {
	public TestStorage() {Storage.instance=this;}
	@Override
	public void doSave() {}
	@Override
	public void readAll() {}
	@Override
	public void init() {}
	@Override
	public ChannelStatus readChannelStatus(String channelid) {return null;}
	@Override
	public ChannelInfo readChannelInfo(String channelid) {return null;}
	@Override
	public EventInfo readEventInfo(String eventid) {return null;}
	@Override
	public List<ResultSlot> readSlots(String channelid, int slotpos_from, int slotpos_to) {return null;}
}