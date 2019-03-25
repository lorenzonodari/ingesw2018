package it.unibs.ingesw.dpn.model;

/**
 * Classe che fornisce le implementazioni corrette degli oggetti {@link Category}
 * all'interno del programma. Tali istanze sono uniche per ciascuna categoria esistente, e ognuna
 * di esse contiene campi con valori specifici.
 * Questa classe di occupa di creare le suddette categorie, inizializzando i rispettivi campi, e fornisce
 * l'istanza desiderata di {@link Category} su richiesta, passando come parametro un riferimento generico 
 * di tipo {@link CategoryEnum}.
 * 
 * @author Michele Dusi
 *
 */
public final class CategoryProvider {
	
	private static final CategoryProvider singletonProvider = new CategoryProvider();
	
	private static final Category [] categories = new Category[CategoryEnum.CATEGORIES_NUMBER];
	
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
	public CategoryProvider getProvider() {
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
	
	private Category createCategory(CategoryEnum catEnum) {
		Category c = null;
		// Aggiungo i campi richiesti dalla categoria specifica, più tutti quelli comuni.
		switch (catEnum) {
		
		case PARTITA_DI_CALCIO :
			c = new Category(
					"Partita di calcio", 
					"Evento sportivo che prevede una partita di calcio fra due squadre di giocatori");
			addCommonFields(c);
			addSoccerMatchFields(c);
			break;
			
		default :
			throw new IllegalStateException("Non è stato possibile creare un'istanza di tale categoria.");
		}
		
		// Restituisce la categoria inizializzata con tutti i campi richiesti.
		return c;
	}
	
	/**
	 * Metodo che aggiunge ad una categoria vuota tutti i campi comuni a tutte le categorie.
	 * In particolare, vengono aggiunti i seguenti campi:
	 * - Titolo dell'evento
	 * - Numero di partecipanti
	 * 
	 * Questa categoria può essere poi ampliata con nuovi campi per essere resa più specifica.
	 */
	private void addCommonFields(Category category) {
		// Aggiungo il campo "titolo"
		Field titleField = new Field(
				"Titolo",
				"Nome di fantasia attribuito all'evento",
				false,
				String.class);
		category.addField(titleField);
		
		// Aggiungo il campo "numero di partecipanti"
		Field participantsNumberField = new Field(
				"Numero di partecipanti",
				"Numero di persone da coinvolgere nell'evento",
				true,
				Integer.class);
		category.addField(participantsNumberField);
		
		// TODO Tutti gli altri campi
		
	}
	
	/**
	 * Metodo che aggiunge alla categoria tutti i campi esclusivi di un evento "Partita di Calcio".
	 * In particolare, vengono aggiunti:
	 * - Genere dei giocatori
	 * - Fascia d'età dei giocatori
	 */
	private void addSoccerMatchFields(Category category) {
		// Aggiungo il campo "genere dei giocatori"
		Field genderField = new Field(
				"Genere",
				"Il genere dei giocatori",
				true,
				it.unibs.ingesw.dpn.model.fieldtypes.GenderEnum.class);
		category.addField(genderField);
		
		// Aggiungo il campo "fascia d'età"
		Field ageRangeField = new Field(
				"Fascia di età",
				"L'intervallo in cui sono comprese le età accettate dei giocatori",
				true,
				it.unibs.ingesw.dpn.model.fieldtypes.IntegerInterval.class);
		category.addField(ageRangeField);
	}
	
	
}
