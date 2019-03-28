package it.unibs.ingesw.dpn.model;

/**
 * Enumerator che contiene tutti i dati delle categorie attualmente presenti nel progetto.
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 *
 */
public enum CategoryEnum {
	
	PARTITA_DI_CALCIO(
			// Titolo
			"Partita di calcio",
			// Descrizione
			"Evento sportivo che prevede una partita di calcio fra due squadre di giocatori",
			
			// Campo "genere"
			new Field(
					"Genere",
					"Il genere dei giocatori che partecipano alla partita",
					true,
					it.unibs.ingesw.dpn.model.fieldtypes.GenderEnumFieldValue.class
					),
			
			// Campo "fascia d'età"
			new Field(
					"Fascia di età",
					"L'intervallo in cui sono comprese le età accettate dei giocatori",
					true,
					it.unibs.ingesw.dpn.model.fieldtypes.IntegerIntervalFieldValue.class)
			);
	
	
	// Altre eventuali categorie da aggiungere qui.
	
	private String name;
	private String description;
	private Field [] exclusiveFields;
	
	public static final int CATEGORIES_NUMBER = CategoryEnum.values().length;

	static final Field [] COMMON_FIELDS = {
			
			// Campo "titolo"
			new Field(
					"Titolo",
					"Nome di fantasia attribuito all'evento",
					false,
					String.class
					),
			
			// Campo "numero di partecipanti"
			new Field(
					"Numero di partecipanti",
					"Numero di persone da coinvolgere nell'evento",
					true,
					Integer.class
					),
			
			// Campo "termine ultimo di iscrizione"
			new Field(
					"Termine ultimo di iscrizione",
					"Ultimo giorno utile per iscriversi all'evento",
					true,
					java.util.Date.class
					),
			
			// Campo "luogo"
			new Field(
					"Luogo",
					"Il luogo di svolgimento o di ritrovo dell'evento",
					true,
					String.class
					),

			// Campo "data"
			new Field(
					"Data",
					"Il giorno in cui si svolgerà o avrà inizio l'evento",
					true,
					java.util.Date.class
					),

			// Campo "ora"
			new Field(
					"Ora",
					"L'orario in cui si svolgerà o avrà inizio l'evento",
					true,
					java.util.Date.class
					),
			
			// Campo "durata"
			new Field(
					"Durata",
					"La durata approssimata, in ore e minuti o in giorni, dell'evento",
					false,
					Integer.class
					),

			// Campo "quota individuale"
			new Field(
					"Quota individuale",
					"La spesa che ogni partecipante dovrà sostenere per l'evento",
					true,
					Float.class
					),

			// Campo "compreso nella quota"
			new Field(
					"Compreso nella quota",
					"Lista delle voci di spesa comprese nella quota di partecipazione",
					false,
					String.class
					),

			// Campo "data conclusiva"
			new Field(
					"Data conclusiva",
					"Il giorno di conclusione dell'evento",
					false,
					java.util.Date.class
					),
			
			// Campo "ora conclusiva"
			new Field(
					"Ora conclusiva",
					"L'orario di conclusione dell'evento",
					false,
					java.util.Date.class
					),

			// Campo "note"
			new Field(
					"Note",
					"Note aggiuntive sull'evento",
					false,
					String.class
					),
	};
	
	/**
	 * Costruttore privato di una categoria.
	 * 
	 * @param name Il nome di una categoria
	 * @param description La descrizione di una categoria
	 * @param exclusiveFields I campi esclusivi e caratterizzanti di una categoria
	 */
	private CategoryEnum(String name, String description, Field ... exclusiveFields) {
		this.name = name;
		this.description = description;
		this.exclusiveFields = exclusiveFields;
	}

	/**
	 * @return Il nome della categoria
	 */
	String getName() {
		return this.name;
	}

	/**
	 * @return La descrizione della categoria
	 */
	String getDescription() {
		return this.description;
	}
	
	/**
	 * @return I campi esclusivi della categoria
	 */
	Field [] getExclusiveFields() {
		return this.exclusiveFields;
	}
	
}
