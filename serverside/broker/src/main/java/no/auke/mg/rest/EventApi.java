package no.auke.mg.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EventApi {

	@RequestMapping("/doc")
	public ModelAndView restApi() {
		return new ModelAndView("doc");
	}

	@RequestMapping("/event/{eventid}")
	public @ResponseBody String event(@PathVariable String eventid) {
		return "event " + eventid;
	}

	@RequestMapping("/event/{eventid}/create")
	public @ResponseBody String createEvent(@PathVariable String eventid) {
		return "event create" + eventid;
	}

	@RequestMapping("/event/{eventid}/update")
	public @ResponseBody String updateEvent(@PathVariable String eventid) {
		return "event update" + eventid;
	}

	@RequestMapping("/event/{eventid}/info")
	public @ResponseBody String infoEvent(@PathVariable String eventid) {
		return "event info" + eventid;
	}

	@RequestMapping("/channel/{eventid}/status")
	public @ResponseBody String status(@PathVariable String eventid) {
		return "event status" + eventid;
	}

}