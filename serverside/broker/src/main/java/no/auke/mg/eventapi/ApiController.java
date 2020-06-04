package no.auke.mg.eventapi;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ApiController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping(value="/greeting",method = RequestMethod.GET)
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@RequestMapping("/doc")
	public ModelAndView restApi() {
		return new ModelAndView("restapidoc");
	}

	@RequestMapping("/event/{eventid}/info")
	public ModelAndView info() {
		return new ModelAndView("event");
	}

	@RequestMapping("/event/{eventid}/status")
	public ModelAndView status() {
		return new ModelAndView("event");
	}

}