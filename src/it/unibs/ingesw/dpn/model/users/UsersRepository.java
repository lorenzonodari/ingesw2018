package it.unibs.ingesw.dpn.model.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.CategoryListFieldValue;
/**
 * Classe adibita alla gestione dei dati relativi agli utenti e dei login/logout
 */
public class UsersRepository implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5696141226031129287L;
	private LinkedList<User> users; 		// Lista degli utenti registrati
	
	/**
	 * Istanzia un nuovo gestore degli utenti. Alla creazione, tale gestore non avra' alcun utente associato.
	 */
	public UsersRepository() {
		
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
	
	public List<User> getUserByCategoryOfInterest(Category category){
		
		Predicate<User> filterPredicate = (user) -> {
			
			CategoryListFieldValue interests = (CategoryListFieldValue) user.getFieldValue(UserField.CATEGORIE_DI_INTERESSE);
			if (interests == null) {
				return false;
			}
			return interests.contains(category);
			
		};
		
		return users.stream().filter(filterPredicate)
							 .collect(Collectors.toCollection(ArrayList::new));			
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
			if (u.getFieldValue(UserField.NICKNAME).toString().equals(nickname)) {
				return true;
			}
		}
		return false;
	}

}