package it.unibs.ingesw.dpn.model.users;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
/**
 * Classe utilizzata per contenere le informazioni relative ad un insieme di notifiche. In particolare, ad ogni
 * User e' associata una Mailbox contentente tutte le notifiche ricevute da tale utente.
 */
public class Mailbox {

	private LinkedList<Notification> notifications;
	
	/**
	 * Crea una nuova mailbox, vuota
	 */
	public Mailbox() {
		this.notifications = new LinkedList<>();
	}
	
	/**
	 * Restituisce una lista non modificabile delle notifiche. L'immutabilita' di
	 * tale lista, unita all'immutabilita' delle istanze di Notification assicurano che
	 * non sia possibile per classi esterne modificare la mailbox senza utilizzare gli opportuni
	 * metodi
	 * 
	 * @return Una view non modificabile del contenuto della mailbox
	 */
	public List<Notification> getEveryNotification() {
		return Collections.unmodifiableList(this.notifications);
	}
	
	/**
	 * Aggiunge la notifica data alla mailbox
	 * 
	 * Precondizione: la notifica data non deve gia' essere contenuta nella mailbox
	 * Postcondizione: la notifica data non e' piu' presente nella mailbox
	 * 
	 * @param notification La notifica da aggiungere alla mailbox
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
	 * Rimuove la notifica data dalla mailbox
	 * 
	 * Precondizione: la notifica data deve essere contenuta nella mailbox
	 * Postcondizione: la notifica data 
	 * @param notification
	 */
	public void delete(Notification toDelete) {
		
		// Verifica delle precondizioni
		if (!notifications.contains(toDelete)) {
			throw new IllegalArgumentException();
		}
		
		this.notifications.remove(toDelete);
		
		assert !this.notifications.contains(toDelete);
	}
	
	
	
}
