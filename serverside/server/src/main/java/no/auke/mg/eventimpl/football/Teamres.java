package no.auke.mg.eventimpl.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Teamres {

	public Teamres(String id) {this.id=id;}
	public String id;
	public int num=0;

	@JsonIgnore
	public Measure totwork = new Measure("tot");

	@JsonIgnore
	public Map<String,Measure> btnwork = new HashMap<String,Measure>();

	public List<Measure> getHits() {
		List<Measure> list = new ArrayList<Measure>();
		if(totwork.val>0.0) {
			list.add(totwork);
		}
		for(Measure m:btnwork.values()) {
			if(m.val>0.0 || m.avg1>0.0 || m.avg2>0.0) {
				list.add(m);
			}
		}
		return list;
	};

}