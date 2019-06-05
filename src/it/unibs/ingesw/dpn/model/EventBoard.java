package it.unibs.ingesw.dpn.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.events.EventState;
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
	
	private HashMap<Event, List<User>> eventMap = new HashMap<Event, List<User>>();
	
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
		// Prepara la lista di iscritti per il nuovo evento
		ArrayList<User> subscribers = new ArrayList<>();
		eventMap.put(event, subscribers);
		
		// Comunica all'evento che è stato pubblicato
		event.publish();
		
		// Iscrive il creatore dell'evento
		this.addSubscription(event, creator);
		
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
			eventMap.remove(event);
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
		return eventMap
				.keySet()
				.stream()
				.filter(event -> event.getState().equals(EventState.OPEN))
				.filter(event -> event.getCreator() == author)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	/**
	 * Restituisce la lista degli aperti a cui l'utente è iscritto
	 * 
	 * @param e : utente su cui avviene la ricerca 
	 */
	public List<Event> getUserSubscriptions(User e) {
		return eventMap
				.keySet()
				.stream()
				.filter(event -> eventMap.get(event).contains(e))
				.collect(Collectors.toCollection(ArrayList::new));
	}
	/**
	 * Funzione che prende in ingresso un utente e restituisce una lista di 
	 * tutti gli utenti che hanno partecipato a eventi precedentemente creati
	 * da lui e attualmente nello stato "evento chiuso"
	 * 
	 * @param user Utente sul quale si conduce la ricerca	 
	 */
	public List<User> getListOfOldSubscribersFromPastEvents(User user) {
		ArrayList<User> subscribers = new ArrayList<>();
		for(Event e : eventMap
				.keySet()
				.stream()
				.filter(event -> event.getState().equals(EventState.CLOSED))
				.filter(event -> event.getCreator() == user)
				.collect(Collectors.toCollection(ArrayList::new))) {
			for(User u : eventMap.get(e)) {
				if(!subscribers.contains(u))
					subscribers.add(u);
			}			
		}
		return subscribers;
	}
	/**
	 * restituisce la list degli iscritti a un evento 
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
	 * Prima l'iscrizione avviene sulla bacheca cioè tramite l'associazione dell'utente alla lista dei suoi iscritti
	 * poi la mailbox dell'utente viene aggiunta alla lista delle mailbox in ascolto dell'evento
	 * 
	 * Precondizione: l'evento deve essere un evento esistente e aperto in bacheca 
	 * Precondizione: l'utente deve non essere già iscritto all'evento stesso 
	 * 				  
	 * @param event -> Evento in cui iscrivere un utente 
	 * @param subscription -> L'utente da iscrivere
	 *
	 * @return restituisce true se l'operazione è andata buon fine, altrimenti false
	 */
	public boolean addSubscription(Event event, User subscription) {
		// verifica precondizione
		if (event == null || subscription == null ) {
			throw new IllegalStateException();
		}

		if (event.subscribe(subscription)) {		
			eventMap.get(event).add(subscription);
			return true;
		} else {
			return false;
		}

	}
	
	/**
	 * Metodo che verifica se in un determinato evento si è già iscritto un utente
	 * 
	 * @return true se l'utente non è già iscritto all'evento, false altrimenti 
	 */
	public boolean verifySubscription(Event event, User subscriber) {
		// verifica precondizione
		if (event == null || subscriber == null ) {
			throw new IllegalStateException();
		}
		return !eventMap.get(event).contains(subscriber);
	}
	
	/**
	 * Metodo che rimuove una inscrizione di un utente ad un evento nella bacheca 
	 * 
	 * 
	 * Precondizioni: l'evento deve esistere ed essere aperto in bacheca 
	 * 				  l'utente deve essere iscritto all'evento
	 * 
	 * @param evento a cui si vuole disiscrivere l'utente
	 * @param Utente da rimuovere dalla lista degli iscritti 
	 * @return restituisce true se l'operazione è andata buon fine, altrimenti false
	 */
	public boolean removeSubscription(Event event, User toRemove) {

		// verifica precondizione
		if (event == null || toRemove == null) {
			throw new IllegalStateException();
		}
		if(eventMap.get(event).remove(toRemove)){
			event.unsubscribe(toRemove);
			return true;
		} else {
			return false;
		}
		
	}
	
	
}