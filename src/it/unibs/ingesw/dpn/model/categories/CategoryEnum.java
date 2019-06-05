package it.unibs.ingesw.dpn.model.categories;

import java.io.Serializable;
import it.unibs.ingesw.dpn.model.events.Event;

/**
 * Enumerazione che contiene un elenco breve e leggero delle categorie attualmente previste dal progetto.
 * Viene utilizzato dalla classe {@link Event} per avere un riferimento veloce alla categoria di appartenenza.
 * In qualunque momento, è sempre possibile accedere all'oggetto complesso di tipo {@link Category} relativo,
 * che contiene tutti i dati e i metodi di utilità, tramite la classe {@link CategoryProvider}.
 * 
 * Nota: l'aggiunta di una nuova istanza a questo enumerator richiede di completare, con i relativi dati,
 * le classi che ne fanno uso in maniera esaustiva (ad esempio, tramite un costrutto <i>switch</i>).
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 *
 */
public enum CategoryEnum implements Serializable {
	
	PARTITA_DI_CALCIO,
	CONFERENZA;
	
	// Altre eventuali categorie da aggiungere qui.
	
	public static final int CATEGORIES_NUMBER = CategoryEnum.values().length;
	
}
