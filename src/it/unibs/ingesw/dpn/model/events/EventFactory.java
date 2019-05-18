package it.unibs.ingesw.dpn.model.events;

import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.users.User;

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

	/**
	 * Restituisce l'istanza unica della factory.
	 * 
	 * @return L'unica istanza di EventFactory
	 */
	public static EventFactory getFactory() {
		if (EventFactory.singletonFactory == null) {
			EventFactory.singletonFactory = new EventFactory();
		}
		return EventFactory.singletonFactory;
	}
	
	/**
	 * Metodo che si occupa della creazione di un evento di una precisa categoria indicata.
	 * 
	 * @param creator L'utente che ha creato l'evento
	 * @param category La categoria dell'evento
	 * @param fieldValues I campi dell'evento
	 * @return l'istanza di {@link Event} creata
	 */
	public Event createEvent(User creator, CategoryEnum category, Map<Field, FieldValue> fieldValues) {
		
		if (category == null) {
			throw new IllegalArgumentException("Impossibile creare un Evento con categoria \"null\"");
		}
		
		// Creo l'evento secondo l'apposita categoria
		switch (category) {
		
		case PARTITA_DI_CALCIO:
			return new SoccerMatchEvent(creator, fieldValues);
				
		}
		
		return null;
	}
	
}
