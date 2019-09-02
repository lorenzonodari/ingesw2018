package it.unibs.ingesw.dpn.model.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.unibs.ingesw.dpn.model.users.Invite;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe adibita alla gestione del processo di invio di inviti agli utenti
 * 
 * @author Emanuele Poggi
 *
 */
public class Inviter {
	
	private Event target;
	private HashMap<User, Boolean> invited = new HashMap<>();
		
	public Inviter(Event target, EventBoard board) {
		
		this.target = target;
		for (User u : board.getListOfOldSubscribersFromPastEvents(target.getCreator())) {
			invited.put(u, false);
		}
		
	}
	
	/**
	 * Imposta l'utente dato come utente da invitare
	 * 
	 * Precondizione: l'utente dato deve essere uno dei possibili canditati all'invito. Cio' significa
	 *                che egli deve aver partecipato in passato ad almeno un evento tra quelli proposti
	 *                dall'attuale creatore dell'evento, della stessa categoria dell'evento attuale
	 * 
	 * @param user L'utente da invitare
	 * @return false se il processo non è andato a buon fine
	 */
	public boolean addInvitation(User user) {
		
		// Verifica delle precondizioni
		if (!invited.containsKey(user)) {
			return false;
		}
		
		invited.put(user, true);
		return true;
		
	}
	
	/**
	 * Imposta tutti gli utenti passati come parametro come utenti da invitare.<br>
	 * 
	 * Precondizione: tutti devono essere contenuti nella lista di utenti invitabili.
	 * Se la precondizione non viene soddisfatta, il metodo NON termina, ma si limita a non invitare il dato utente.
	 * Dopodiché procede con i successivi utenti della lista.
	 * Solamente alla fine, se almeno un utente era fra i non invitabili, viene restituito "false".
	 * 
	 * @param users La lista di utenti da invitare.
	 * @return "true" se tutti gli utenti sono stati invitati
	 */
	public boolean addAllInvitations(Set<User> users) {
		boolean check = true;
		
		for (User u : users) {
			// Check verifica che ogni utente sia invitabile
			check &= addInvitation(u);
		}
		
		return check;
	}
	
	/**
	 * Imposta l'utente dato come utente da non invitare
	 * 
	 * Precondizione: l'utente dato deve essere uno dei possibili candidati all'invito. Cio' significa
	 *                che egli deve aver partecipato in passato ad almeno un evento tra quelli propost
	 *                dall'attuale creatre dell'evento, della stessa categoria dell'evento attuale
	 * 
	 * @param user L'utente da rimuovere dalla lista degli utenti da invitare
	 * @return false se il processo non è andato a buon fine
	 */
	public boolean removeInvitation(User user) {
		
		// Verifica delle precondizioni
		if (!invited.containsKey(user)) {
			return false;
		}
				
		invited.put(user, false);
		return true;
		
	}
	
	/**
	 * Restituisce l'insieme degli utenti candidati all'invito all'evento.
	 * 
	 * @return L'insieme dei candidati all'invito
	 */
	public Set<User> getCandidates() {
		
		return invited.keySet();

	}
	
	/**
	 * Metodo che restituisce la lista degli eventi da invitare
	 * 
	 * @return La lista degli utenti selezionati per l'invio dell'invito
	 */
	public List<User> getInvited(){
		
		ArrayList<User> retVal = new ArrayList<>();
		for (User u : invited.keySet()) {
			if (invited.get(u)) {
				retVal.add(u);
			}
		}
		return retVal;
				      
	}
	
	/**
	 * Metodo che restituisce la lista degli utenti da non invitare
	 * 
	 * @return La lista degli utenti non selezionati per l'invio dell'invito
	 */
	public List<User> getNotInvited(){
		
		ArrayList<User> retVal = new ArrayList<>();
		for (User u : invited.keySet()) {
			if (!invited.get(u)) {
				retVal.add(u);
			}
		}
		return retVal;
	}
	
	/**
	 * Restituisce true se l'utente dato e' attualmente selezionato come utente da invitare
	 * 
	 * @param u L'utente del quale si desiderano informazioni
	 * @return true se l'utente dato e' attualmente selezionato per l'invito
	 */
	public boolean isInvited(User u) {
		
		// Verifica delle precondizioni
		if (!invited.containsKey(u)) {
			throw new IllegalArgumentException();
		}
		
		return invited.get(u);
		
	}
	
	/**
	 * Invia gli inviti agli utenti selezionati
	 */
	public void sendInvites() {

		for(User p : invited.keySet()) {
			
			if (invited.get(p)) {
				p.receive(new Invite(this.target));
			}
		}
	}
	
}
