package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
/**
 * Classe utilizzata per contenere le informazioni relative ad un insieme di notifiche. In particolare, ad ogni
 * User e' associata una Mailbox contentente tutte le notifiche ricevute da tale utente.
 */
public class Mailbox implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4612332277435632397L;
	
	private LinkedList<Notification> notifications;
	private LinkedList<Invite> invitations;
	
	/**
	 * Crea una nuova mailbox, vuota
	 */
	public Mailbox() {
		this.notifications = new LinkedList<>();
		this.invitations = new LinkedList<>();
	}
	
	/**
	 * Restituisce una lista non modificabile delle notifiche. L'immutabilita' di
	 * tale lista, unita all'immutabilita' delle istanze di Notification assicurano che
	 * non sia possibile per classi esterne modificare la mailbox senza utilizzare gli opportuni
	 * metodi
	 * 
	 * @return Una view non modificabile delle notifiche contenute nella mailbox
	 */
	public List<Notification> getEveryNotification() {
		return Collections.unmodifiableList(this.notifications);
	}
	
	/**
	 * Restituisce una lista non modificabile degli inviti. L'immutabilita' di tale lista, unita
	 * all'immutabilita' delle istanze di Invite assicurano che non sia possibile per classi esterne
	 * modificare la mialbox senza utilizzare gli opportuni metodi.
	 * 
	 * 
	 * @return Una view non modificabile degli inviti contenuti nella mailbox
	 */
	public List<Invite> getEveryInvite() {
		return Collections.unmodifiableList(this.invitations);
	}
	
	/**
	 * Aggiunge la notifica data alla mailbox
	 * 
	 * Precondizione: la notifica data non deve gia' essere contenuta nella mailbox
	 * Postcondizione: la notifica data non e' piu' presente nella mailbox
	 * 
	 * @param toAdd La notifica da aggiungere alla mailbox
	 */
	public void deliver(Notification toAdd) {
		
		// Verifica delle precondizioni
		if (notifications.contains(toAdd)) {
			throw new IllegalArgumentException();
		}
		
		this.notifications.add(toAdd);
		
		assert this.notifications.contains(toAdd);
		
	}
	
	/**
	 * Aggiunge l'invito dato alla mailbox
	 * 
	 * Precondizione: l' invito dato non deve gia' essere contenuta nella mailbox
	 * Postcondizione: l' invito dato non e' piu' presente nella mailbox
	 * 
	 * @param toAdd L'invito da aggiungere alla mailbox
	 */
	public void deliver(Invite toAdd) {
		
		// Verifica delle precondizioni
		if (invitations.contains(toAdd)) {
			throw new IllegalArgumentException();
		}
		
		this.invitations.add(toAdd);
		
		assert this.invitations.contains(toAdd);
		
	}
	
	/**
	 * Rimuove la notifica/invito data dalla mailbox
	 * 
	 * Precondizione: la notifica data deve essere contenuta nella mailbox
	 * Postcondizione: la notifica data non e' piu' contenuta nella mailbox
	 * 
	 * @param toDelete La notifica da eliminare dalla mailbox
	 */
	public void delete(Notification toDelete) {
		
		// Verifica delle precondizioni
		if (!notifications.contains(toDelete)) {
			throw new IllegalArgumentException();
		}
		
		this.notifications.remove(toDelete);
		
		assert !this.notifications.contains(toDelete);
	}
	
	/**
	 * Rimuove l'invito data dalla mailbox
	 * 
	 * Precondizione: l'invito dato deve essere contenuto nella mailbox
	 * Postcondizione: l'invito dato non e' piu' contenuto nella mailbox 
	 * 
	 * @param toDelete L'invito da eliminare dalla mailbox
	 */
	public void delete(Invite toDelete) {
		
		// Verifica delle precondizioni
		if (!invitations.contains(toDelete)) {
			throw new IllegalArgumentException();
		}
		
		this.invitations.remove(toDelete);
		
		assert !this.invitations.contains(toDelete);
	}
	
	/**
	 * Restituisc true se la mailbox dell'utente contiene almeno una notifica.
	 * 
	 * @return true se la mailbox contiene almeno una notifica
	 */
	public boolean containsNotifications() {
		return !this.notifications.isEmpty();
	}
	
	/**
	 * Restituisce true se la mailbox dell'utente contiene almeno un invito.
	 * 
	 * @return true se la mailbox contiene almeno un invito
	 */
	public boolean containsInvites() {
		return !this.invitations.isEmpty();
	}
	
	
	
}
