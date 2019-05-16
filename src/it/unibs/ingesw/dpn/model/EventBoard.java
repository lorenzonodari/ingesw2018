package it.unibs.ingesw.dpn.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe bacheca, ha la funzione di tenere traccia degli eventi e degli utenti
 *  che ad essi aderiscono
 * Offre la possibilità di modificare le iscrizioni e di fare ricerche tramite stato
 * sugli eventi
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 */
public class EventBoard implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8389735292299317677L;
	
	private HashMap<Event, List<User>> eventMap = new HashMap<Event, List<User>>();
	
	/**
	 * Aggiunge un evento alla lista della bacheca 
	 * 
	 * Precondizione : evento non deve essere un null 
	 * @param Evento da aggiungere alla bacheca
	 */
	public void addEvent(Event event) {
		// verifica precondizione
		if (event == null) {
			throw new IllegalStateException();
		}
		eventMap.put(event, new ArrayList<User>());
	}
	/**
	 * Metodo che rimuove un evento dalla bacheca
	 * 
	 * Precondizione: l'evento deve essere già nella bacheca 
	 * 
	 * @param evento da rimuovere dalla bacheca
	 */
	public void removeEvent(Event event) {

		// verifica precondizione
		if (event == null) {
			throw new IllegalStateException();
		}
		eventMap.remove(event);
	}
	/**
	 * Resistuisce la lista degli eventi 
	 */
	public List<Event> getEvents(){
		return eventMap
				.keySet()
				.stream()
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * restituisce la lista degli eventi filtrata su un particolare stato 
	 * 
	 *  @param Nome dello stato su cui si vuole fare la ricerca
	 */
	public List<Event> getEventsByState(String stateName){
		
		return eventMap
				.keySet()
				.stream()
				.filter(event -> event.getEventState() == stateName)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * restituisce la scritta degli iscritti a un evento 
	 * 
	 * Precondizione: l'evento deve essere esistente
	 * 
	 * @param un evento specifico della bacheca, esistente
	 */
	public List<User> getSubscriptions(Event event){

		// verifica precondizione
		if (event == null) {
			throw new IllegalStateException();
		}
		return eventMap.get(event);
	}
	/**
	 * Metodo che aggiunge un iscrizione di un utente ad un evento nella bacheca
	 * 
	 * Precondizioni: l'evento deve essere un evento esistente e aperto in bacheca 
	 * 				  l'utente deve non essere già iscritto all'evento stesso 
	 * 				  
	 * @param Evento in cui inscrivere un utente 
	 * @param L'utente da inscrivere
	 */
	public int addSubscription(Event event, User subscription) {
		// verifica precondizione
		if (event == null || subscription == null ) {
			throw new IllegalStateException();
		}
		if(!eventMap.get(event).contains(subscription)){
			eventMap.get(event).add(subscription);
			return 0;
		}
		return 1;
		
	}
	
	/**
	 * Metodo che rimuove una inscrizione di un utente ad un evento nella bacheca 
	 * 
	 * Precondizioni: l'evento deve esistere ed essere aperto in bacheca 
	 * 				  l'utente deve essere iscritto all'evento
	 * 
	 * @param evento a cui si vuole disiscrivere l'utente
	 * @param Utente da rimuovere dalla lista degli iscritti 
	 */
	public void removeSubscription(Event event, User toRemove) {

		// verifica precondizione
		if (event == null || toRemove == null) {
			throw new IllegalStateException();
		}
		eventMap.get(event).remove(toRemove);
	}
	
}