package it.unibs.ingesw.dpn.model.categories;

/**
 * Interfaccia che segue il pattern Strategy.
 * L'obiettivo delle classi che la implementano è inizializzare le istanze uniche
 * di oggetti {@link Category} all'avvio del programma, secondo la modalità prescelta
 * dal programmatore.
 * Attualmente si prevede che l'inizializzazione venga effettuata grazie a metodi appositi, 
 * ma in futuro si potrebbe voler leggere i dati dei campi da file o in altro modo.
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 *
 */
public interface ICategoryInitializer {
	
	/**
	 * Restituisce una nuova istanza di ICategoryInitializer.
	 * Naturalmente, ciascuna classe che implementa questa interfaccia dovrà prevedere 
	 * l'implementazione del pattern "singleton", poiché non è logicamente necessario che siano 
	 * presenti più istanze all'interno del programma.
	 * 
	 * @return L'istanza unica di ICategoryInitializer.
	 */
	static ICategoryInitializer getInstance() {
		return SimpleCategoryInitializer.getInstance();
	}
	
	/**
	 * Metodo che inizializza e restituisce la lista di Categorie,
	 * come oggetti {@link Category}.
	 * 
	 * Nota: poiché l'unica classe a chiamare un'istanza di ICategoryInitializer sarà
	 * {@link CategoryProvider}, il modificatore del metodo non è "public", bensì "friendly".
	 * 
	 * @return la lista di categorie
	 */
	Category [] initCategories();

}
