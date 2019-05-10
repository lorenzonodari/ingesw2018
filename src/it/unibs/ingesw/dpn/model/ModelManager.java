package it.unibs.ingesw.dpn.model;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.users.UsersManager;

/**
 * Classe che centralizza l'accesso ai dati di dominio. Tramite questa classe
 * e' quindi possibile accedere alla lista delle categorie, agli eventi registrati, etc...
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 */
public class ModelManager {
	
	private CategoryProvider categoryProvider;
	private UsersManager usersManager;
	
	/**
	 * Istanzia un ModelManager, creando i riferimenti alle classi del che vengono utilizzate
	 * per fornire informazioni sul modello di dominio al resto del programma.
	 * 
	 * Precondizione: usersManager != null
	 * 
	 * @param usersManager Il gestore degli utenti da utilizzare
	 */
	public ModelManager(UsersManager usersManager)  {
		
		// Verifica delle precondizioni
		if (usersManager == null) {
			throw new IllegalArgumentException();
		}
		
		this.categoryProvider = CategoryProvider.getProvider();
		this.usersManager = usersManager;
		
	}
	
	/**
	 * Restituisce l'array delle categorie di eventi previste dal programma.
	 * Le categorie sono "fisse" e non possono essere modificate, create o distrutte durante l'esecuzione
	 * del programma.
	 * Questo metodo, pertanto, restituirà sempre lo stesso array.
	 * 
	 * @return L'array contenente le categorie registrate
	 */
	public Category [] getAllCategories() {
		return this.categoryProvider.getAllCategories();
	}
	
	/**
	 * Restituisce il gestore degli utenti associato al model manager.
	 * 
	 * @return Il gestore degli utenti in uso
	 */
	public UsersManager getUsersManager() {
		return this.usersManager;
	}
}
