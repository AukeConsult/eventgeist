package no.auke.mg.eventapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EventApi {

	@RequestMapping("/doc")
	public ModelAndView restApi() {
		return new ModelAndView("doc");
	}

	@RequestMapping("/event")
	public ModelAndView event() {
		return new ModelAndView("event");
	}

	@RequestMapping("/event/{eventid}/create")
	public ModelAndView createEvent() {
		return new ModelAndView("event");
	}

	@RequestMapping("/event/{eventid}/update")
	public ModelAndView updateEvent() {
		return new ModelAndView("event");
	}

	@RequestMapping("/event/{eventid}/info")
	public ModelAndView infoEvent() {
		return new ModelAndView("event");
	}

	@RequestMapping("/channel/{eventid}/status")
	public ModelAndView status() {
		return new ModelAndView("event");
	}

}