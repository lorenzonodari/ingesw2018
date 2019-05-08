package it.unibs.ingesw.dpn.model.categories;

import it.unibs.ingesw.dpn.model.fields.Field;

/**
 * Classe che si occupa di inizializzare la lista di categorie all'avvio del programma
 * in maniera semplice, eseguendo il codice sorgente contenuto all'interno di questa classe.
 * E' un'implementazione dell'interfaccia {@link ICategoryInitializer}, che permette 
 * di seguire il pattern "Strategy" per risolvere il problema dell'inizializzazione.
 * In futuro potranno essere implementate nuove metodologie di inizializzazione semplicemente
 * creando nuove implementazioni di {@link ICategoryInitializer} che seguano logiche diverse.
 * 
 * Nota: ogni implementazione di {@link ICategoryInitializer} segue anche il pattern "Singleton".
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class SimpleCategoryInitializer implements ICategoryInitializer {
	
	/**
	 * Istanza unica della classe, secondo il pattern Singleton.
	 */
	private static SimpleCategoryInitializer singleton = null;

	/**
	 * Costruttore privato per permettere l'esistenza di un'unica istanza di classe.
	 */
	private SimpleCategoryInitializer() {}

	/**
	 * Restituisce una nuova istanza di SimpleCategoryInitializer, secondo il pattern "Singleton".
	 * L'istanza non viene creata finché il metodo non viene invocato per la prima volta, in modo
	 * da non istanziare oggetti inutili ai fini del programma (poiché è possibile che, in caso di 
	 * utilizzo di strategie diverse -secondo il pattern Strategy- questa classe non venga mai usata).
	 * 
	 * @return L'istanza unica di SimpleCategoryInitializer.
	 */
	static ICategoryInitializer getInstance() {
		
		// Verifico che il singleton sia già stato istanziato
		if (SimpleCategoryInitializer.singleton == null) {
			SimpleCategoryInitializer.singleton = new SimpleCategoryInitializer();
		}
		
		// Restituisco l'istanza unica della classe
		return SimpleCategoryInitializer.singleton;
	}

	/**
	 * Metodo che inizializza e restituisce la lista di Categorie, come oggetti {@link Category}.
	 * 
	 * @return la lista di categorie
	 */
	@Override
	public Category [] initCategories() {
		
		// Creo e inizializzo l'array di campi comuni (una sola volta per tutte le categorie)
		Field [] commonFields = getCommonFields();
		
		// Creo l'array delle categorie
		Category [] categories = new Category[CategoryEnum.CATEGORIES_NUMBER];
		
		// Per ciascuna categoria inizializzo nome, descrizione, campi comuni ed esclusivi
		for (int c = 0; c < CategoryEnum.CATEGORIES_NUMBER; c++) {
			
			/*
			 * Nota: la combinazione fra il costrutto "for" e il costrutto "switch" è fortemente ridondante
			 * (poiché non esiste del codice ripetuto all'interno del for, dato che ad ogni iterazione il costrutto
			 * switch seleziona solo il frammento relativo alla corretta categoria).
			 * Tuttavia, in questo modo l'aggiunta di una nuova categoria all'enumerator scatena un warning
			 * -a livello di IDE- all'interno del switch, finché non viene implementato il codice relativo 
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
				categories[c].addAllFields(commonFields);
				
				// Aggiungo i campi esclusivi
				categories[c].addAllFields(	
					// Campo "genere"
					new Field(
							"Genere",
							"Il genere dei giocatori che partecipano alla partita",
							true,
							it.unibs.ingesw.dpn.model.fields.GenderEnumFieldValue.class
							),
					// Campo "fascia d'età"
					new Field(
							"Fascia di età",
							"L'intervallo in cui sono comprese le età accettate dei giocatori",
							true,
							it.unibs.ingesw.dpn.model.fields.IntegerIntervalFieldValue.class
							)
					);
				break;
			}
		}
		
		return categories;
	}

	/**
	 * Metodo che inizializza i campi comuni a tutte le categorie.
	 * I campi comuni vengono "salvati" all'interno del codice sorgente di un metodo, non della classe,
	 * in modo che i campi non vengano istanziati non appena viene caricata la classe ma soltanto
	 * quando è invocato il metodo.
	 * 
	 * @return L'array di campi comuni a tutte le categorie
	 */
	private Field [] getCommonFields() {
		Field [] commonFields = {
				
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

				// Campo "data e ora"
				new Field(
						"Data e ora",
						"Il giorno e l'orario in cui si svolgerà o avrà inizio l'evento",
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

				// Campo "data e ora conclusive"
				new Field(
						"Data e ora conclusive",
						"Il giorno e l'orario di conclusione dell'evento",
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
		return commonFields;
	}
	
}
