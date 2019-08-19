package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe bacheca, ha la funzione di tenere traccia degli eventi e degli utenti
 * che ad essi aderiscono
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
	
	private List<Event> events = new ArrayList<Event>();
	
	/**
	 * Aggiunge un evento alla lista della bacheca, pubblicandolo e rendendolo visibile a tutti.
	 * Inoltre, iscrive all'evento lo stesso creatore in maniera automatica.
	 * 
	 * Precondizione : L' evento non deve essere nullo. 
	 * 
	 * @param event L'evento da aggiungere alla bacheca
	 * @param creator Il creatore dell'evento
	 */
	public void addEvent(Event event, User creator) {
		// verifica precondizione
		if (event == null) {
			throw new IllegalStateException();
		}

		events.add(event);
		
		// Comunica all'evento che è stato pubblicato
		event.publish();
		
	}
	
	/**
	 * Metodo che rimuove un evento dalla bacheca. Si noti che un evento puo' essere
	 * rimosso dalla bacheca solo se e' rispettato il valore del suo campo "Termine ultimo di 
	 * ritiro iscrizione".
	 * 
	 * Precondizione: l'evento deve essere già nella bacheca
	 * 
	 * @param evento da rimuovere dalla bacheca
	 */
	public boolean removeEvent(Event event) {

		// Verifica precondizione
		if (event == null) {
			throw new IllegalStateException();
		}
		
		if (event.withdraw()) {
			events.remove(event);
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/**
	 * Resistuisce la lista degli eventi 
	 */
	public List<Event> getEvents(){
		return events
				.stream()
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * restituisce la lista degli eventi filtrata su un particolare stato 
	 * 
	 *  @param Nome dello stato su cui si vuole fare la ricerca
	 */
	public List<Event> getEventsByState(String stateName){
		
		return events
				.stream()
				.filter(event -> event.getState().equals(stateName))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	/**
	 * Restituisce la lista degli eventi attualmente aperti proposti da un utente.
	 *  
	 * 
	 * @param author : utente su cui si effettua la ricerca
	 */
	public List<Event> getEventsByAuthor(User author) {
		return events
				.stream()
				.filter(event -> event.getState().equals(EventState.OPEN))
				.filter(event -> event.getCreator() == author)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Restituisce la lista degli eventi aperti a cui l'utente è iscritto.
	 * 
	 * @param user L'utente su cui avviene la ricerca 
	 */
	public List<Event> getOpenSubscriptionsByUser(User user) {
		return events
				.stream()
				.filter(event -> event.hasSubscriber(user))
				.filter(event -> event.getState().equals(EventState.OPEN))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * Restituisce la lista degli eventi aperti a cui l'utente è iscritto e di cui l'utente NON è autore.
	 * 
	 * @param user L'utente su cui avviene la ricerca 
	 */
	public List<Event> getOpenSubscriptionsNotProposedByUser(User user) {
		return events
				.stream()
				.filter(event -> event.hasSubscriber(user))
				.filter(event -> !event.getCreator().equals(user))
				.filter(event -> event.getState().equals(EventState.OPEN))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	
	/**
	 * Funzione che prende in ingresso un utente e restituisce una lista di 
	 * tutti gli utenti che hanno partecipato a eventi precedentemente creati
	 * da lui e attualmente nello stato "evento concluso"
	 * 
	 * @param user Utente sul quale si conduce la ricerca	 
	 */
	public List<User> getListOfOldSubscribersFromPastEvents(User user) {
		ArrayList<User> subscribers = new ArrayList<>();
		for(Event e : events
				.stream()
				.filter(event -> event.getState().equals(EventState.ENDED))
				.filter(event -> event.getCreator() == user)
				.collect(Collectors.toCollection(ArrayList::new))) {
			for(User u : e.getSubscribers()) {
				if(!subscribers.contains(u) && u != e.getCreator())
					subscribers.add(u);
			}			
		}
		return subscribers;
	}
	
	/**
	 * Reimposta lo stato degli eventi contenuti nell bacheca. Tale metodo deve essere chiamato dopo aver caricato
	 * la event board da disco in modo che gli eventi in essa contenuti siano posti in uno stato consistente con quello
	 * che avevano precedentemente al salvataggio persistente
	 */
	public void resetEventStates() {
		
		for (Event e : this.events) {
			e.resetState();
		}
	}
	
}