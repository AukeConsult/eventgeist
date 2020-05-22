package no.eventgeist.service;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public abstract class EventService implements Runnable {
	
    
	private int max_hits=0;

	protected Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();
	public Map<String, UserSession> getUsersessions() {return usersessions;}

	public int getCnt() {return max_hits;}	
	public void addUser(UserSession session) {
		if(usersessions.containsKey(session.getSession().getId())) {
			usersessions.put(session.getSession().getId(),session);
		}
	}

	private ReentrantLock lock = new ReentrantLock(); 
	
	private AtomicBoolean hasresults = new AtomicBoolean();
	public boolean hasResults() {
		return hasresults.get();
	}
	
	public String readResult() {
		try {
			lock.lock();
			String result=resultWork;
			return result;
		} finally {
			resultWork="";
			hasresults.set(false);
			lock.unlock();			
		}
	}

	protected String resultWork="";
	@Override
	public void run() {
				
		// calculating results
        while (true) {
        	try {
                Thread.sleep(100);
                try {
                	lock.lock();
                	resultWork = execute();
                	hasresults.set(!resultWork.equals(""));
                } finally {
                	lock.unlock();			
                }
            } catch (Exception e) {
                e.printStackTrace();
            }        	
        }
	}
	protected abstract String execute();

}
