package it.unibs.ingesw.dpn.model.users;

import java.util.List;
import java.util.LinkedList;

/**
 * Classe adibita alla gestione dei dati relativi agli utenti e dei login/logout
 */
public class UsersManager {
	
	private User currentUser;		// Utente correntemente loggato
	private LinkedList<User> users; // Lista degli utenti registrati
	
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
		
		User[] results = (User[]) this.users.stream()
								   .filter(user -> user.getUsername() == username)
								   .toArray();
		
		return (results.length == 0) ? null : results[0];
		
	}
	
	/**
	 * Registra l'utente dato come correntemente connesso al sistema.
	 * 
	 * Precondizione: user deve essere un utente esistente
	 * Precondizione: nessun altro utente deve essere attualmente connesso al sistema
	 * 
	 * @param user L'utente da connettere al sistema
	 */
	public void login(User user) {
		
		// Verifica delle precondizioni
		if (!this.users.contains(user)) {
			throw new IllegalArgumentException();
		}
		
		if (this.currentUser != null) {
			throw new IllegalStateException();
		}
		
		this.currentUser = user;
		
	}
	
	/**
	 * Disconnette l'utente dato dal sistema.
	 * 
	 * Precondizione: l'utente dato deve essere attualmente connesso al sistema
	 * 
	 * @param user L'utente da disconnettere dal sistema
	 */
	public void logout(User user) {
		
		// Verifica delle precondizioni
		if (this.currentUser != user) {
			throw new IllegalArgumentException();
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
