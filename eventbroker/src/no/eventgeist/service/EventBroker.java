package no.eventgeist.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import no.matchgeist.service.eventservices.EmptyEvent;
import no.matchgeist.service.eventservices.FootballEvent;

public class EventBroker implements Runnable {
    
    private static EventBroker instance;

    private static Map<String, EventService> events = new ConcurrentHashMap<String, EventService>();
    private static Map<String, UserSession> sessions = new ConcurrentHashMap<String, UserSession>();
    
    private EventBroker() {}
    
    public static void addSession(Session session, String eventtype, String eventid, String userid, String support, String position) {
    	
    	if(!events.containsKey(eventid)) {
    		// check what type of event
    		if(eventtype.equals("football")) {
        		events.put(eventid, new FootballEvent());
    		} else {
    			events.put(eventid, new EmptyEvent());
    		}
    	
    	}
    	
    	EventService event = events.get(eventid);
    	
    	UserSession usersession = new UserSession(session, event, userid, support, position);
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
        	System.out.println("initpush");
            instance = new EventBroker();
        	System.out.println("initpush " + instance.hashCode());
            new Thread(instance).start();
        }
    }
    
    @Override
    public void run() {
        
        while (true) {
            
        	try {
            	
                Thread.sleep(10*1000);
                System.out.println("do push size clients " + sessions.size() + " " + this.hashCode()); 
                
                for(EventService eventHandler:events.values()) {
                	
                	System.out.println("push event");
                	String response = eventHandler.readResult();
                	
                	for(UserSession session:eventHandler.getUsersessions().values()) {                		

                		System.out.println("push " + session.getSession().getId()); 
                        if (session.getSession().isOpen()) {
                        	Date d = new Date(System.currentTimeMillis());
                        	session.getSession().getBasicRemote().sendText(response);
                        } else {
                        	System.out.println("close " + session.getSession().getId()); 
                            sessions.remove(session);
                        }
                	}
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        	
        }
        
    }
}