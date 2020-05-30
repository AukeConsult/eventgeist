package no.auke.events.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.auke.events.service.event.EmptyEvent;
import no.auke.events.service.event.FootballEvent;

public class EventBroker implements Runnable {
    
    private static EventBroker instance;

    private static Map<String, EventRunner> events = new ConcurrentHashMap<String, EventRunner>();
    private static Map<String, UserSession> sessions = new ConcurrentHashMap<String, UserSession>();
    private static int report_period_default=5000;
    
    public static String reportDir=""; 
    
    private EventBroker() {}
    
    public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {
    	
    	if(!events.containsKey(eventid)) {
    		// check what type of event
    		if(eventtype.equals("football")) {
        		events.put(eventid, new FootballEvent(eventid, report_period_default));
    		} else {
    			events.put(eventid, new EmptyEvent(eventid, report_period_default));
    		}
    		// read event info

    		// initialize and start event
    		events.get(eventid).init();
    	}
    	
    	EventRunner event = events.get(eventid);    	
    	UserSession usersession = new UserSession(session, event, userid, support, position,0);
        event.addUser(usersession);
        sessions.put(session.getId(), usersession);

    }

    public static void addResponse(Session session, String response) {
    	if(sessions.containsKey(session.getId())) {
        	sessions.get(session.getId()).addResponse(response);
    	}
    }
    
    public static void initialize() {
    	
    	if (instance == null) {
    		
    		// read parameters
    		reportDir = System.getProperty("user.dir") + "/test/events/";    		
            instance = new EventBroker();
            new Thread(instance).start();
        
    	}
    
    }
    
    @Override
    public void run() {
        
        while (true) {
            
                try {

                	Thread.sleep(1000);
	                System.out.println("do push size clients " + sessions.size() + " " + this.hashCode()); 
	                
	                for(EventRunner event:events.values()) {
	                	
	                	System.out.println("push event");
	                	for(TimeFrame frame:event.getTimeframes()) {
	                		
		                	String response = frame.readResults();
		                	if(response!=null) {
		                		
			                	for(UserSession session:frame.getUserSessions()) {                		

			                		System.out.println("push " + session.getSession().getId()); 
			                		
			                        if (session.getSession().isOpen()) {
			                        	session.getSession().getBasicRemote().sendText(response);
			                        } else {
			                        	//TODO add logging
			                        	System.out.println("close " + session.getSession().getId()); 
			                        	frame.removeSession(session);	
			                            sessions.remove(session);
			                        }
			                	}
		                	}	                		
	                	}	                	
	                }				
                
                } catch (InterruptedException e) {
                } catch (Exception e) {
					e.printStackTrace();
				}
        	
        }
        
    }
}