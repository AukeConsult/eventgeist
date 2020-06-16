package no.auke.mg.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {
	public boolean hasresult=false;
	public Map<String, ResponseTeam> teams = new HashMap<String, ResponseTeam>();
	public List<String> statuslist= new ArrayList<String>();
	public List<String> msglist = new ArrayList<String>();
}
