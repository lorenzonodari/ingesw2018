package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;

/**
 * Classe utilizzata per contenere i dati relativi ad un singolo utente
 */
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1333193895476185438L;
	
	private String username;
	private Mailbox mailbox;
	
	/**
	 * Crea un nuovo utente con il nome dato. La relativa mailbox e' automaticamente creata, vuota.
	 * 
	 * @param username Il nome dell'utente da creare
	 */
	public User(String username) {
		this.username = username;
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
	 * Restituisce la mailbox dell'utente
	 * 
	 * @return La mailbox dell'utente
	 */
	public Mailbox getMailbox() {
		return this.mailbox;
	}

}
