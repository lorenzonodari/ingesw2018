package it.unibs.ingesw.dpn.model.categories;

import it.unibs.ingesw.dpn.model.fields.CommonField;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fields.SoccerMatchField;

public class FromEnumCategoryInitializer implements CategoryInitializer {
	
	/**
	 * Istanza unica della classe, secondo il pattern Singleton.
	 */
	private static FromEnumCategoryInitializer singleton = null;

	/**
	 * Costruttore privato per permettere l'esistenza di un'unica istanza di classe.
	 */
	private FromEnumCategoryInitializer() {}

	/**
	 * Restituisce una nuova istanza di FromEnumCategoryInitializer, secondo il pattern "Singleton".
	 * L'istanza non viene creata finché il metodo non viene invocato per la prima volta, in modo
	 * da non istanziare oggetti inutili ai fini del programma (poiché è possibile che, in caso di 
	 * utilizzo di strategie diverse -secondo il pattern Strategy- questa classe non venga mai usata).
	 * 
	 * @return L'istanza unica di FromEnumCategoryInitializer.
	 */
	static CategoryInitializer getInstance() {
		
		// Verifico che il singleton sia già stato istanziato
		if (FromEnumCategoryInitializer.singleton == null) {
			FromEnumCategoryInitializer.singleton = new FromEnumCategoryInitializer();
		}
		
		// Restituisco l'istanza unica della classe
		return FromEnumCategoryInitializer.singleton;
	}

	/**
	 * Metodo che inizializza e restituisce la lista di Categorie, come oggetti {@link Category}.
	 * 
	 * @return la lista di categorie
	 */
	@Override
	public Category [] initCategories() {
		
		// Creo l'array delle categorie
		Category [] categories = new Category[CategoryEnum.CATEGORIES_NUMBER];
		
		// Per ciascuna categoria inizializzo nome, descrizione, campi comuni ed esclusivi
		for (int c = 0; c < CategoryEnum.CATEGORIES_NUMBER; c++) {
			
			/*
			 * Nota: la combinazione fra il costrutto "for" e il costrutto "switch" è fortemente ridondante
			 * (poiché non esiste del codice ripetuto all'interno del for, dato che ad ogni iterazione il costrutto
			 * switch seleziona solo il frammento relativo alla corretta categoria).
			 * Tuttavia, in questo modo l'aggiunta di una nuova categoria all'enumerator scatena un warning
			 * -a livello di IDE- all'interno del costrutto switch finché non viene implementato il codice relativo 
			 * all'ultima opzione aggiunta. In questo modo il programmatore che in futuro volesse aggiungere 
			 * nuove categorie sarebbe facilitato nell'aggiunta.
			 * Inoltre, questa scelta non è in alcun modo limitante per quanto riguarda l'efficienza. Infatti,
			 * questa classe è attiva solamente all'avvio del programma e pure in singola istanza.
			 * Infine, questo permette di non appesantire la classe CategoryEnum, mentre la classe SimpleCategoryInitializer
			 * potrà in qualunque momento essere sostituita da un'altra implementazione di ICategoryInitializer, a seconda della
			 * logica che si vorrà utilizzare in futuro per inizializzare le categorie all'avvio.
			 */
			switch(CategoryEnum.values()[c]) {
			
			// Partita di calcio
			case PARTITA_DI_CALCIO:
				categories[c] = new Category(
						"Partita di calcio",
						"Evento sportivo che prevede una partita di calcio fra due squadre di giocatori");
				
				// Aggiungo i campi comuni
				categories[c].addAllFields(CommonField.values());
				
				// Aggiungo i campi esclusivi
				categories[c].addAllFields(SoccerMatchField.values());
				
				break;
				
			// Conferenza
			case CONFERENZA :
				categories[c] = new Category(
						"Conferenza",
						"Evento di divulgazione che prevede un relatore e più ascoltatori, ognuno dei quali può personalizzare la propria formula di partecipazione");

				// Aggiungo i campi comuni
				categories[c].addAllFields(CommonField.values());
				
				// Aggiungo i campi esclusivi
				categories[c].addAllFields(ConferenceField.values());
				
				break;
				
			}
		}
		
		return categories;
	}
	
}