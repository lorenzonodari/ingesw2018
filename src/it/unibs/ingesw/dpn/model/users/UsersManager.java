package it.unibs.ingesw.dpn.model.users;

<<<<<<< HEAD
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.Serializable;
=======
>>>>>>> refs/remotes/origin/field_value_problem
import java.util.LinkedList;

import it.unibs.ingesw.dpn.Main;

/**
 * Classe adibita alla gestione dei dati relativi agli utenti e dei login/logout
 */
public class UsersManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5696141226031129287L;
	
	private transient User currentUser;		// Utente correntemente loggato
	private LinkedList<User> users; 		// Lista degli utenti registrati
	
	/**
	 * Istanzia un nuovo gestore degli utenti. Alla creazione, tale gestore non avra' alcun utente associato.
	 */
	public UsersManager() {
		
		this.currentUser = null;
		this.users = new LinkedList<>();
		
	}
	
	/**
	 * Restituisce l'utente al quale e' associato lo username dato, se esistente. In caso
	 * contrario, restituisce null.
	 * 
	 * @param username Lo username dell'utente desiderato
	 * @return L'utente cercato o null se questo non esiste
	 */
	public User getUser(String username) {
		
		User result = null;
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				result = user;
				break;
			}
		}
		
		return result;
		
	}
	
	/**
	 * Effettua il login con lo username dato. Se un utente con tale username non esiste
	 * esso viene creato, in caso contrario viene utilizzato l'utente gia' esistente.
	 * 
	 * Precondizione: nessun altro utente deve essere attualmente connesso al sistema
	 * 
	 * @param user L'utente da connettere al sistema
	 */
	public void login(String username) {
		
		// Verifica delle precondizioni
		if (this.currentUser != null) {
			throw new IllegalStateException();
		}
		
		User user = getUser(username);
		if (user == null) {
			
			user = new User(username);
			this.users.add(user);
			
		}
		
		this.currentUser = user;
		
	}
	
	/**
	 * Disconnette l'utente attualmente connesso al sistema.
	 * 
	 * Precondizione: un utente deve essere attualmente connesso al sistema
	 * 
	 */
	public void logout() {
		
		// Verifica delle precondizioni
		if (this.currentUser == null) {
			throw new IllegalStateException();
		}
		
		this.currentUser = null;
		
	}
	
	/**
	 * Restituisce l'utente attualmente connesso al sistema.
	 * 
	 * Precondizione: un utente deve essere attualmente connesso al sistema
	 * 
	 * @return L'utente attualmente connesso al sistema
	 */
	public User getCurrentUser() {
		
		// Verifica delle precondizioni
		if (this.currentUser == null) {
			throw new IllegalStateException();
		}
		
		return this.currentUser;
	}

}