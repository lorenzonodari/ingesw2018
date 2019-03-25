package it.unibs.ingesw.dpn.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe astratta che rappresenta in maniera concettuale un evento generico gestito dal programma.
 * Questa classe viene poi specificata in differenti classi a seconda delle categorie previste.
 * (Si veda, ad esempio, la classe {@link SoccerMatch}.)
 * 
 * Nota: Le classi figlie non contengono direttamente i dati, bensì è la classe padre che mantiene in memoria 
 * tutti i valori dei campi. Le classi figlie, tuttavia, sono necessarie per l'implementazione dei differenti
 * comportamenti basati sui differenti campi.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public abstract class Event {
	
	private final Category category;
	private final Map<Field, Object> values_map;
	
	/**
	 * Crea un nuovo evento con la relativa categoria.
	 * 
	 * @param category
	 */
	public Event(Category category) {
		this.category = category;
		this.values_map = new HashMap<>();
	}

	/**
	 * @return la categoria a cui appartiene l'evento.
	 */
	public Category getCategory() {
		return category;
	}

}
