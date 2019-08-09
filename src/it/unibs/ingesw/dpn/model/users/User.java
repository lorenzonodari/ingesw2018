package it.unibs.ingesw.dpn.model.users;

import java.util.List;

import it.unibs.ingesw.dpn.model.fields.AbstractFieldable;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.UserField;

/**
 * Classe utilizzata per contenere i dati relativi ad un singolo utente
 */
public class User extends AbstractFieldable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333193895476185438L;

	/** La casella di posta a cui recapitare i messaggi dell'utente */
	private Mailbox mailbox;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * Precondizione: la lista di coppie (campo, valore) devono essere istanziate correttamente.
	 * Questo significa che tutti i campi obbligatori devono già essere stati inizializzati. 
	 * L'unica classe abilitata a fare ciò è la classe {@link UserBuilder}.
	 * 
	 * @param fieldValues La lista di campi previsti per un oggetto User
	 */
	public User(List<Field> fieldsList) {
		super(fieldsList);
		
		// Inizializzo una nuova mailbox
		this.mailbox = new Mailbox();
	}

	/**
	 * Imposta il valore id default di alcuni campi.
	 * Questo metodo viene chiamato dal costruttore e racchiude tutte le procedure che impostano
	 * i valori dei campi facoltativi utilizzati nel programma.
	 */
	@Override
	public void setDefaultFieldValues() {
		// Al momento non esistono campi da impostare in maniera automatica con valori di default
	}
	
	/**
	 * Restituisce una stringa contenente la descrizione completa ma compatta delle caratteristiche
	 * dell'utente.
	 *  
	 * @return Una descrizione testuale dell'utente
	 */
	public String toString() {
		StringBuffer description = new StringBuffer();
		// Valori dei campi
		description.append("Descrizione dell'utente\n");
		for (Field f : UserField.values()) {
			if(!(this.getFieldValue(f) == null)) {
			description.append(String.format(" | %-50s : %s\n",
					f.getName(),
					this.getFieldValue(f).toString()));
			}
		}
		return description.toString();
	}
	
	/**
	 * Aggiunge la notifica data alle notifiche ricevute dall'utente
	 * 
	 * @param n La notifica da ricevere
	 */
	public void receive(Notification n) {
		this.mailbox.deliver(n);
	}
	
	/**
	 * Aggiunge l'invito dato agli inviti ricevuti dall'utente
	 * @param i
	 */
	public void receive(Invite i) {
		this.mailbox.deliver(i);
	}
	
	/**
	 * Elimina la notifica data dalle notifiche ricevute dall'utente
	 * 
	 * @param n La notifica da eliminare
	 */
	public void delete(Notification n) {
		this.mailbox.delete(n);
	}
	
	/**
	 * Elimina l'invito dato dagli inviti ricevuti dall'utente
	 * 
	 * @param n L'invito da eliminare
	 */
	public void delete(Invite i) {
		this.mailbox.delete(i);
	}
	
	/**
	 * Restituisce true se l'utente ha notifiche
	 * 
	 * @return true se l'utente ha notifiche
	 */
	public boolean hasNotifications() {
		return this.mailbox.containsNotifications();
	}
	
	/**
	 * Restituisce true se l'utente ha inviti
	 * 
	 * @return true se l'utente ha inviti
	 */
	public boolean hasInvites() {
		return this.mailbox.containsInvites();
	}
	
	/**
	 * Restituisce la lista delle notifiche ricevute dall'utente
	 * 
	 * @return La lista delle notifiche ricevute dall'utente
	 */
	public List<Notification> getNotifications() {
		return this.mailbox.getEveryNotification();
	}
	
	/**
	 * Restituisce la lista degli inviti ricevuti dall'utente
	 * 
	 * @return La lista degli inviti ricevuti dall'utente
	 */
	public List<Invite> getInvites() {
		return this.mailbox.getEveryInvite();
	}

}
