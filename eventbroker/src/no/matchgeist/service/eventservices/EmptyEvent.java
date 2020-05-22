package no.matchgeist.service.eventservices;

import no.eventgeist.service.EventService;
import no.eventgeist.service.UserSession;

public class EmptyEvent extends EventService {

	@Override
	protected String execute() {
		
		String resultWork="";
    	for(UserSession user:usersessions.values()) {
            for(String response:user.getResponses()) {
            	resultWork += response + ";";
            }
    	}
    	return resultWork;
		
	}

}
