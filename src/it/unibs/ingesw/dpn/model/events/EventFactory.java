package it.unibs.ingesw.dpn.model.events;

/**
 * Classe che permette la creazione di eventi in maniera "controllata", secondo il pattern "Factory".
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class EventFactory {
	
	private static EventFactory singletonFactory = null;

	/**
	 * Costruttore privato, secondo il pattern Singleton.
	 */
	private EventFactory() {
				
	}
	
	public EventFactory getFactory() {
		if (EventFactory.singletonFactory == null) {
			EventFactory.singletonFactory = new EventFactory();
		}
		return EventFactory.singletonFactory;
	}
	
}
