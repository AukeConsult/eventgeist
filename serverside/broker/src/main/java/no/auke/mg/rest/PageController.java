package no.auke.mg.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/page")
public class PageController {



	@GetMapping(value = "/{name}")
	public @ResponseBody Object getPage(@PathVariable String name) {
		return "hello page " + name;
	}

}
