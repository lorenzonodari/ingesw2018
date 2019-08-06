package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.model.categories.Category;

/**
 * Classe che rappresenta il contenuto di un campo di tipo "lista di categorie".
 * Permette l'aggiunta e la rimozione di categorie, così come la loro visualizzazione in forma testuale.
 * 
 * @author Michele Dusi
 *
 */
public class CategoryListFieldValue implements FieldValue, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3704161654157735866L;
	private List<Category> categoryList;
	
	/**
	 * Costruisce un nuovo oggetto {@link CategoryListFieldValue} inizializzando a vuota la lista interna.
	 */
	public CategoryListFieldValue() {
		this.categoryList = new ArrayList<>();
	}
	
	/**
	 * Metodo che aggiunge una categoria alla lista SOLO Se questa non è già presente.
	 * In caso sia già presente restituisce false.
	 * 
	 * Precondizione: il parametro non deve essere "null". In caso sia nullo, il metodo genera un'eccezione.
	 * 
	 * Postcondizione: la lista contiene la categoria di cui è stata richiesta l'aggiunta.
	 * 
	 * @param categoryToAdd La categoria da aggiungere.
	 * @return "true" se l'aggiunta è andata a buon fine, "false" altrimenti. In entrambi i casi, al termine dell'esecuzione del metodo, la postcondizione è rispettata.
	 */
	public boolean addCategory(Category categoryToAdd) {
		if (categoryToAdd == null) {
			throw new IllegalArgumentException("Impossibile aggiungere una categoria nulla: parametro non inizializzato");
		}
		if (this.categoryList.contains(categoryToAdd)) {
			return false;
		} else {
			this.categoryList.add(categoryToAdd);
			return true;
		}
	}
	
	/**
	 * Metodo che permette la rimozione di una categoria dalla lista.
	 * In caso la categoria non sia presente nella lista, il metodo restituisce false.
	 * 
	 * Precondizione: il parametro non deve essere "null". In caso sia nullo, il metodo genera un'eccezione.
	 * 
	 * Postcondizione: la lista non contiene la categoria di cui è stata richiesta la rimozione.
	 * 
	 * @param categoryToRemove La categoria da rimuovere
	 * @return "true" se la rimozione è andata a buon fine, "false" altrimenti. In entrambi i casi, al termine dell'esecuzione del metodo, la postcondizione è rispettata.
	 */
	public boolean removeCategory(Category categoryToRemove) {
		if (categoryToRemove == null) {
			throw new IllegalArgumentException("Impossibile rimuovere una categoria nulla: parametro non inizializzato");
		}
		if (!this.categoryList.contains(categoryToRemove)) {
			return false;
		} else {
			this.categoryList.remove(categoryToRemove);
			return true;
		}
	}

	/**
	 * Metodo che controlla l'appartenenza di una categoria alla lista.
	 * 
	 * Precondizione: il parametro non deve essere "null". In caso sia nullo, il metodo genera un'eccezione.
	 * 
	 * @param categoryToCheck La categoria di cui si desidera verificare l'appartenenza
	 * @return "True" se la categoria appartiene alla lista, false altrimenti.
	 */
	public boolean contains(Category categoryToCheck) {
		// Verifica della precondizione
		if (categoryToCheck == null) {
			throw new IllegalArgumentException("Impossibile controllare una categoria nulla: parametro non inizializzato");
		}
		return this.categoryList.contains(categoryToCheck);
	}
	
	/**
	 * Restituisce un array di stringhe, ciascuna delle quali è il nome di una categoria contenuta nella lista.
	 * 
	 * Postcondizione: In caso la lista sia vuota, il metodo restituirà un "null".
	 * 
	 * @return Una lista di categorie come array di stringhe.
	 */
	public String [] toStringList() {
		if (this.categoryList.isEmpty()) {
			return null;
		}
		String [] stringList = new String[this.categoryList.size()];
		// Aggiungo i nomi delle categorie alla lista
		int i = 0;
		for (Category c : this.categoryList) {
			stringList[i++] = c.getName();
		}
		return stringList;
		
	}
	
	/**
	 * Restituisce una stringa contenente la lista di tutte le categorie contenute all'interno.
	 * 
	 * Postcondizione: La stringa non conterrà caratteri "newline", ma sarà costituita da elementi separati da una virgola.
	 * Postcondizione: In caso la lista sia vuota, il metodo restituirà una stringa vuota (e NON "null").
	 * 
	 * @return Una lista di categorie come stringa.
	 */
	@Override
	public String toString() {
		if (this.categoryList.isEmpty()) {
			return "";
		}
		StringBuffer s = new StringBuffer();
		// Aggiungo i nomi delle categorie alla lista
		for (Category c : this.categoryList) {
			s.append(c.getName());
			s.append(", ");
		}
		return s.toString().substring(0, s.length() - 2);
	}
}
