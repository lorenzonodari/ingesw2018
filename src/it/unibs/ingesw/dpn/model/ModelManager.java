package it.unibs.ingesw.dpn.model;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;

/**
 * Classe che centralizza l'accesso ai dati di dominio. Tramite questa classe
 * e' quindi possibile accedere alla lista delle categorie, la lista degli eventi registrati, etc...
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 */
public class ModelManager {
	
	private CategoryProvider category_provider;
	
	/**
	 * Costruttore della classe {@link CategoryProvider}.
	 * Si occupa di creare i riferimenti alle classi del Model che vengono utilizzate
	 * per fornire informazioni al resto del programma.
	 */
	public ModelManager()  {
		this.category_provider = CategoryProvider.getProvider();
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
		return this.category_provider.getAllCategories();
	}
}
