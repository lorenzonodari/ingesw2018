package it.unibs.ingesw.dpn.model.events;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe bacheca, ha la funzione di tenere traccia degli eventi attualmente presenti nel Social Network.<br>
 * Offre la possibilità effettuare ricerche sugli eventi presenti in bacheca secondo differenti criteri.<br>
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
	 * <br>
	 * Precondizione : L'evento non deve essere nullo.<br>
	 * Precondizione : L'evento non deve già essere contenuto in bacheca.<br>
	 * In caso le precondizioni non siano rispettate, questo metodo genera un'eccezione.<br>
	 * <br>
	 * <strong>Nota:</strong> è opportuno essere certi che l'evento possa essere pubblicato, 
	 * altrimenti viene restituito "false".<br>
	 * Per farlo, è possibile utilizzare il metodo "canBePublished" di {@link Event}.<br>
	 * 
	 * @param event L'evento da aggiungere alla bacheca
	 * @return "True" se un evento che rispetta le precondizioni è stato aggiunto alla bacheca ed è stato pubblicato.
	 * "False" se un evento che rispetta le precondizioni NON è stato aggiunto alla bacheca e NON è stato pubblicato
	 * a causa del suo stato incompatibile.
	 */
	public boolean addEvent(Event event) {
		// verifica precondizione
		if (event == null) {
			throw new IllegalArgumentException("Impossibile aggiungere un evento nullo");
		}
		else if (this.events.contains(event)) {
			throw new IllegalArgumentException("Impossibile aggiungere un evento già contenuto in bacheca");
			// Nota: questo errore non dovrebbe potersi mai verificare per come funziona il programma
		}

		if (event.canBePublished()) {
			
			// Aggiungo l'evento alla bacheca
			events.add(event);
			// Procedo con l'operazione di pubblicazione
			event.publish();
			// Restituisco true perché l'operazione è andata a buon fine		
			return true;
		}
		else {
			return false;
		}		
	}
	
	/**
	 * Metodo che rimuove un evento dalla bacheca. Si noti che un evento puo' essere
	 * rimosso dalla bacheca solo se e' rispettato il valore del suo campo "Termine ultimo di 
	 * ritiro iscrizione".<br>
	 * <br>
	 * Precondizione : L'evento non deve essere nullo.<br>
	 * Precondizione : L'evento deve già essere contenuto in bacheca.<br>
	 * In caso le precondizioni non siano rispettate, questo metodo genera un'eccezione.<br>
	 * <br>
	 * <strong>Nota:</strong> è opportuno essere certi che l'evento possa essere ritirato, 
	 * altrimenti viene restituito "false".<br>
	 * Per farlo, è possibile utilizzare il metodo "canBeWithdrawn" di {@link Event}.<br>
	 * 
	 * @param event L'evento da rimuovere dalla bacheca
	 * @return "True" se un evento che rispetta le precondizioni è stato rimosso dalla bacheca ed è stato ritirato.
	 * "False" se un evento che rispetta le precondizioni NON è stato rimosso dalla bacheca e NON è stato ritirato
	 * a causa del suo stato incompatibile.
	 */
	public boolean removeEvent(Event event) {
		// Verifica precondizione
		if (event == null) {
			throw new IllegalArgumentException("Impossibile rimuovere un evento nullo");
		}
		else if (!this.events.contains(event)) {
			throw new IllegalArgumentException("Impossibile rimuovere un evento non contenuto nella bacheca");
			// Nota: questo errore non dovrebbe potersi mai verificare per come funziona il programma
		}
		
		// Ritiro l'evento, se può essere ritirato
		if (event.canBeWithdrawn()) {
			
			// Rimuovo l'evento dalla bacheca
			events.remove(event);
			// Procedo con l'operazione di ritiro
			event.withdraw();
			// Restituisco true perché l'operazione è andata a buon fine		
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Restituisce la lista degli eventi 
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
	
	/**
	 * @return "True" se non sono presenti eventi in bacheca.
	 */
	public boolean isEmpty() {
		return this.events.isEmpty();
	}
	
}