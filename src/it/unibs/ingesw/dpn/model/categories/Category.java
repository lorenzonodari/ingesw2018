package it.unibs.ingesw.dpn.model.categories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.*;

/**
 * Enumerazione che contiene un elenco delle categorie attualmente previste dal progetto.
 * Viene utilizzato dalla classe {@link Event} per avere un riferimento alla categoria di appartenenza.
 * 
 * Nota: l'aggiunta di una nuova istanza a questo enumerator richiede di completare, con i relativi dati,
 * le classi che ne fanno uso in maniera esaustiva.
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 *
 */
public enum Category implements Serializable {
	
	PARTITA_DI_CALCIO (
			"Partita di calcio",
			"Evento sportivo che prevede una partita di calcio fra due squadre di giocatori",
			SoccerMatchField.class),
	
	CONFERENZA (
			"Conferenza",
			"Evento di divulgazione che prevede un relatore e più ascoltatori, ognuno dei quali può personalizzare la propria formula di partecipazione",
			ConferenceField.class);
	// Altre eventuali categorie da aggiungere qui.
	
	private static final String TO_STRING = "-- Caratteristiche della categoria --\n\n"
			+ "Nome:        %s\n"
			+ "Descrizione: %s\n"
			+ "\n"
			+ "-- Elenco dei campi --\n";
	
	private String name;
	private String description;
	private List<Field> fields;
	
	public static final int CATEGORIES_NUMBER = Category.values().length;
	
	/**
	 * Costruttore con modificatore di accesso "private" della classe Category.
	 * 
	 * @param name Il nome della categoria
	 * @param description La descrizione della categoria
	 * @param exclusiveFieldsEnum L'enumerazione contenente i campi esclusivi della categoria
	 */
	private Category(String name, String description, Class<? extends Field> exclusiveFieldsEnum) {
		this.name = name;
		this.description = description;
		this.fields = new ArrayList<Field>();
		
		// Inizializzo i campi della categoria
		for (Field f : CommonField.values()) {						// Tutti i campi comuni
			this.fields.add(f);
		}
		for (Field f : exclusiveFieldsEnum.getEnumConstants()) {	// Tutti i campi esclusivi
			this.fields.add(f);
		}
	}

	/**
	 * @return Il nome della categoria
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return La descrizione della categoria
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Restituisce l'array con tutti i campi della categoria.
	 * 
	 * @return i campi della categoria
	 */
	public List<Field> getFields() {
		return this.fields;
	}
	
	/**
	 * Restituisce una descrizione testuale completa dell'intera categoria e dei suoi campi.
	 * 
	 * @return la descrizione testuale della categoria e dei suoi campi.
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(String.format(TO_STRING,
				this.name,
				this.description));
		// Per ciascun campo aggiungo la relativa descrizione alla descrizione della categoria.
		for (Field f : this.fields) {
			str.append("\n" + f.fieldToString());
		}
		return str.toString();
	}
	
}
