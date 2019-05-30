package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.util.LinkedList;

import it.unibs.ingesw.dpn.model.fields.UserField;
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
	 * Restituisce l'utente al quale e' associato lo nickname dato, se esistente. In caso
	 * contrario, restituisce null.
	 * 
	 * @param nickname Lo nickname dell'utente desiderato
	 * @return L'utente cercato o null se questo non esiste
	 */
	public User getUser(String nickname) {
		
		User result = null;
		for (User user : this.users) {
			if (user.getFieldValue(UserField.NICKNAME).toString().equals(nickname)) {
				result = user;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Aggiunge un nuovo utente al sistema.
	 * 
	 * Precondizione: l'utente non deve essere "null".
	 * Precondizione: l'utente deve essere effettivamente "nuovo", ossia non deve essere già appartenente al sistema.
	 * 
	 * @param newUser Il nuovo utente da aggiungere
	 */
	public void addUser(User newUser) {
		if (newUser == null) {
			throw new IllegalArgumentException("Impossibile aggiungere un utente nullo");
		} else if (this.users.contains(newUser)) {
			throw new IllegalArgumentException("Impossibile aggiungere un utente già presente nel sistema");
		}
		this.users.add(newUser);
	}
	
	/**
	 * Effettua il login con lo nickname dato.
	 * Se lo nickname non è presente all'interno del sistema, il metodo restituisce "false".
	 * Se invece il login va a buon fine, il metodo restituisce "true".
	 * 
	 * Precondizione: nessun altro utente deve essere attualmente connesso al sistema.
	 * 
	 * @param user L'utente da connettere al sistema
	 * 
	 * @return Un valore booleano che indica se l'utente si è connesso correttamente.
	 */
	public boolean login(String nickname) {
		
		// Verifica delle precondizioni
		if (this.currentUser != null) {
			throw new IllegalStateException();
		}
		
		User user = getUser(nickname);
		
		// Verifico se l'utente non esiste
		if (user == null) {
			return false;			
		}
		
		// Se invece esiste
		this.currentUser = user;
		return true;		
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
	
	/**
	 * Verifica se un utente con il nome passato come parametro è già registrato 
	 * all'interno del sistema.
	 * 
	 * @param nickname Il nickname da cercare.
	 * @return "True" se l'utente esiste, "False" altrimenti.
	 */
	public boolean isNicknameExisting(String nickname) {
		for (User u : this.users) {
			if (u.getFieldValue(UserField.NICKNAME).equals(nickname)) {
				return true;
			}
		}
		return false;
	}

}