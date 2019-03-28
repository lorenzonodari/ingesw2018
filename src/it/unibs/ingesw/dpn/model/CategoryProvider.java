package it.unibs.ingesw.dpn.model;

/**
 * Classe che fornisce le implementazioni corrette degli oggetti {@link Category}
 * all'interno del programma. Tali istanze sono uniche per ciascuna categoria esistente, e ognuna
 * di esse contiene campi con valori specifici.
 * Questa classe di occupa di creare le suddette categorie, inizializzando i rispettivi campi, e fornisce
 * l'istanza desiderata di {@link Category} su richiesta, passando come parametro un riferimento generico 
 * di tipo {@link CategoryEnum}.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public final class CategoryProvider {
	
	private static final CategoryProvider singletonProvider = new CategoryProvider();
	
	private final Category [] categories = new Category[CategoryEnum.CATEGORIES_NUMBER];
	
	/**
	 * Costruttore privato.
	 */
	private CategoryProvider() {
		for (CategoryEnum cat : CategoryEnum.values()) {
			categories[cat.ordinal()] = createCategory(cat);
		}
	}
	
	/**
	 * Metodo che restituisce l'unico oggetto di tipo {@link CategoryProvider}.
	 * 
	 * @return l'unica istanza di CategoryProvider
	 */
	public static CategoryProvider getProvider() {
		return CategoryProvider.singletonProvider;
	}

	/**
	 * Metodo unico del provider che permette di ottenere la categoria desiderata
	 * come oggetto {@link Category}, data un'istanza dell'enum {@link CategoryEnum}.
	 * Questo permette di mantenere istanze uniche all'interno di tutto il software.
	 * 
	 * @param category La categoria prescelta di cui si cerca l'oggetto {@link Category} corrispondente
	 * @return L'oggetto {@link Category}
	 */
	public Category getCategory(CategoryEnum category) {
		return categories[category.ordinal()];
	}
	
	/**
	 * Restituisce la lista di tutte le istanze di {@link Category}
	 * previste dal progetto.
	 * 
	 * @return L'array di categorie
	 */
	public Category [] getAllCategories() {
		return this.categories;
	}
	
	/**
	 * Crea una categoria come istanza di {@link Category}.
	 * 
	 * @param catEnum La categoria richiesta
	 * @return l'oggetto {@link Category} corrispondente
	 */
	private Category createCategory(CategoryEnum catEnum) {
		// Creo la nuova istanza di Category
		Category c = new Category(catEnum.getName(), catEnum.getDescription());
		// Aggiungo i campi comuni
		for (Field cf : CategoryEnum.COMMON_FIELDS) {
			c.addField(cf);
		}
		// Aggiungo i campi esclusivi
		for (Field ef : catEnum.getExclusiveFields()) {
			c.addField(ef);
		}
		// Restituisco la categoria inizializzata con tutti i campi richiesti.
		return c;
	}
	
	
}
