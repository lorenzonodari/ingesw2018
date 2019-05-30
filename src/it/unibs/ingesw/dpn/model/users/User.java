package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe utilizzata per contenere i dati relativi ad un singolo utente
 */
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333193895476185438L;

	/**
	 * La casella di posta a cui recapitare i messaggi dell'utente.
	 */
	private Mailbox mailbox;
	
	/** Il nickname dell'utente */
	private final String username;
	/** La data di  nascita dell'utente */
	private LocalDate birthday;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * Nota: il nickname di un utente non può essere modificato successivamente.
	 * Gli altri campi, invece, sì. Tuttavia è richiesto almeno un valore valido al momento della creazione.
	 * 
	 * Precondizione: lo username non può essere nullo. Tuttavia, è possibile che la data di nascita lo sia.
	 * 
	 * @param username Il nome dell'utente da creare
	 */
	public User(String username, LocalDate birthday) {
		if (username == null) {
			throw new IllegalArgumentException("Parametri nulli: impossibile creare un nuovo utente");
		}
		// Caratteristiche dell'utente
		this.username = username;
		this.birthday = birthday;
		
		this.mailbox = new Mailbox();
	}
	
	/**
	 * Restituisce lo username dell'utente
	 * 
	 * @return Lo username dell'utente
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Restituisce la data di nascita dell'utente.
	 * 
	 * Precondizione: la data di nascita non deve essere nulla. Se così fosse, il metodo lancia un'eccezione
	 * segnalando al chiamante che è necessario impostare un valore per questa data di nascita.
	 * 
	 * @return La data di nascita dell'utente.
	 */
	public LocalDate getBirthday() {
		if (this.birthday == null) {
			throw new IllegalStateException("Questo utente non possiede una data di nascita inizializzata");
		} else {
			return this.birthday;
		}
	}
	
	/**
	 * Restituisce la mailbox dell'utente
	 * 
	 * @return La mailbox dell'utente
	 */
	public Mailbox getMailbox() {
		return this.mailbox;
	}

}
