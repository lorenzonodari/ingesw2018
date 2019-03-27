package it.unibs.ingesw.dpn.model;

/**
 * Classe che centralizza l'accesso ai dati di dominio. Tramite questa classe
 * e' quindi possibile accedere alla lista delle categorie, la lista degli eventi registrati, etc...
 */
public class ModelManager {

	private CategoryProvider categoryProvider;
	
	
	/**
	 * Crea un nuovo gestore dei dati di dominio, dato il provider delle categorie da utilizzare
	 * 
	 * Precondizione: categoryProvider != null
	 * 
	 * @param categoryProvider Il provider delle categorie da utilizzare
	 */
	public ModelManager(CategoryProvider categoryProvider) {
		
		// Verifica della precondizione
		if (categoryProvider == null) {
			throw new NullPointerException();
		}
		
		this.categoryProvider = categoryProvider;
	}
	
	/**
	 * Restituisce l'array delle categorie di eventi registrate
	 * 
	 * @return Un array contenente le categorie registrate
	 */
	public Category[] getCategories() {
		return categoryProvider.getAllCategories();
	}
}
