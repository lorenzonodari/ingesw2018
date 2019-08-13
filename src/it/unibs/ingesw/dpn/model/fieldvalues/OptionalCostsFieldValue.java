package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;

import it.unibs.ingesw.dpn.model.users.User;

public class OptionalCostsFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1605590568155502574L;
	
	private LinkedHashMap<String, Float> costs;
	private HashMap<String, List<User>> userChoices;
	
	public OptionalCostsFieldValue() {
		
		this.costs = new LinkedHashMap<>();
		this.userChoices = new HashMap<>();
		
	}
	
	/**
	 * Aggiunge una voce di spesa aggiuntiva
	 * 
	 * Precondizione: il campo non deve gia' comprendere una voce di spesa con lo stesso nome di quella che si aggiunge
	 * Precondizione: name != null
	 * 
	 * @param name Il nome della voce di spesa
	 * @param amount L'ammontare della spesa 
	 */
	public void addEntry(String name, float amount) {
		
		// Verifica delle precondizioni
		if (name == null) {
			throw new NullPointerException();
		}
		
		if (costs.containsKey(name)) {
			throw new IllegalArgumentException();
		}
		
		costs.put(name, amount);
		userChoices.put(name, new LinkedList<User>());
		
	}
	
	/**
	 * Restituisce una view non modificabile del valore del campo
	 * 
	 * @return L'elenco delle coppie "voce di spesa" + "ammontare"
	 */
	public Map<String, Float> getValue() {
		
		return Collections.unmodifiableMap(costs);
	}
	
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		for (String name : costs.keySet()) {
			buffer.append(name);
			buffer.append(": ");
			buffer.append(String.format("%.2f €", costs.get(name)));
			buffer.append("; ");
		}
		
		return buffer.toString();
		
	}
	
	/**
	 * Registra l'utente dato tra gli utenti che desiderano sostenere la spesa aggiuntiva data
	 * 
	 * Precondizione: user != null
	 * Precondizione: cost != null
	 * Precondizione: La spesa data e' presente tra i valori di questo campo
	 * 
	 * @param user L'utente che desidera sostenere la spesa aggiuntiva
	 * @param cost La spesa che desidera sostenere
	 * @return true se il processo va a buon fine
	 */
	public boolean registerUserToCost(User user, String cost) {
		
		// Verifica delle precondizioni
		if (user == null || cost == null) {
			throw new NullPointerException();
		}
		
		if (!costs.containsKey(cost)) {
			throw new IllegalArgumentException();
		}
		
		userChoices.get(cost).add(user);
		return true;
		
	}
	
	/**
	 * Rimuove l'utente dato dalla lista degli utenti che desiderano sostenere la spesa aggiuntiva data
	 * 
	 * Precondizione: user != null
	 * Precondizione: cost != null
	 * Precondizione: La spesa data e' presente tra i valori di questo campo
	 * 
	 * @param user L'utente che desidera sostenere la spesa aggiuntiva
	 * @param cost La spesa che desidera sostenere
	 * @return true se il processo va a buon fine
	 */
	public boolean removeUserFromCost(User user, String cost) {
		
		// Verifica delle precondizioni
		if (user == null || cost == null) {
			throw new IllegalArgumentException("Impossibile rimuovere un utente o una spesa nulla");
		}
		
		if (!costs.containsKey(cost)) {
			throw new IllegalArgumentException("Impossibile rimuovere un utente da una spesa che non sta sostenendo");
		}
		
		userChoices.get(cost).remove(user);
		return true;
		
	}
	
	/**
	 * Rimuove l'utente da tutte le spese aggiuntive che sta sostenendo.
	 * 
	 * Precondizione: user != null
	 * 
	 * @param user L'utente da rimuovere da tutte le spese
	 */
	public void removeUserFromAllCosts(User user) {
		// Verifica delle precondizioni
		if (user == null) {
			throw new IllegalArgumentException("Impossibile rimuovere un utente nullo");
		}
		
		for (String cost : this.userChoices.keySet()) {
			if (this.userChoices.get(cost).contains(user)) {
				this.userChoices.get(cost).remove(user);
			}
		}
	}
	
	/**
	 * Restituisce true se l'utente dato ha deciso di sostenere la spesa data
	 * 
	 * Precondizione: user != null && cost != null
	 * Precondizione: il costo dato deve essere uno dei costi esistenti
	 * 
	 * @param user L'utente in questione
	 * @param cost Il costo in questione
	 * @return true se l'utente dato ha deciso di sostenere la spesa data
	 */
	public boolean userHasCost(User user, String cost) {
		
		// Verifica delle precondizioni
		if (user == null || cost == null) {
			throw new NullPointerException();
		}
		
		if (!costs.containsKey(cost)) {
			throw new IllegalArgumentException();
		}
		
		return userChoices.get(cost).contains(user);
		
	}
	
	/**
	 * Restituisce le spese opzionali complessive sostenute dall'utente dato
	 * 
	 * Precondizione: user != null
	 * Precondizione: L'utente deve essere gia' registrato tra quelli che sostengono spese aggiuntive
	 * 
	 * @param user L'utente per il quale calcolare le spese aggiuntive
	 * @return Le spese aggiuntive sostenute dall'utente
	 */
	public float getExpensesForUser(User user) {
		
		float amount = 0.0f;
		
		for (String cost : userChoices.keySet()) {
			
			if (userChoices.get(cost).contains(user)) {
				amount += costs.get(cost);
			}
		}
		
		return amount;
	}
	

}
