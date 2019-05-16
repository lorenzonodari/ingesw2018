package it.unibs.ingesw.dpn.model.categories;

import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.model.fields.DateFieldValue;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.FieldValue;
import it.unibs.ingesw.dpn.model.fields.GenderEnumFieldValue;
import it.unibs.ingesw.dpn.model.fields.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fields.IntegerIntervalFieldValue;
import it.unibs.ingesw.dpn.model.fields.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fields.StringFieldValue;

/**
 * Classe che si occupa di inizializzare la lista di categorie all'avvio del programma
 * in maniera semplice, eseguendo il codice sorgente contenuto all'interno di questa classe.
 * E' un'implementazione dell'interfaccia {@link CategoryInitializer}, che permette 
 * di seguire il pattern "Strategy" per risolvere il problema dell'inizializzazione.
 * In futuro potranno essere implementate nuove metodologie di inizializzazione semplicemente
 * creando nuove implementazioni di {@link CategoryInitializer} che seguano logiche diverse.
 * 
 * Nota: ogni implementazione di {@link CategoryInitializer} segue anche il pattern "Singleton".
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class SimpleCategoryInitializer implements CategoryInitializer {
	
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
	static CategoryInitializer getInstance() {
		
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
		List<Field<? extends FieldValue>> commonFields = getCommonFields();
		
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
				categories[c].addField(
						
					// Campo "genere"
					new Field<GenderEnumFieldValue>(
							"Genere",
							"Il genere dei giocatori che partecipano alla partita",
							true,
							(renderer, getter) -> {
								GenderEnumFieldValue [] values = GenderEnumFieldValue.values();
								int i = 1;
								for (GenderEnumFieldValue gender : values) {
									renderer.renderText(String.format("%3d)\t%s", 
											i++, gender.toString()));
								}
								int input = getter.getInteger(1, values.length);
								return values[input - 1];
							})
						);
					

				categories[c].addField(				
					// Campo "fascia d'età"
					new Field<IntegerIntervalFieldValue>(
							"Fascia di età",
							"L'intervallo in cui sono comprese le età accettate dei giocatori",
							true,
							(renderer, getter) -> {

								IntegerIntervalFieldValue value = null;
								boolean check = false;
								do {
									renderer.renderText("Inserisci il valore minimo");
									int min = getter.getInteger(0, Integer.MAX_VALUE);
									renderer.renderText("Inserisci il valore massimo");
									int max = getter.getInteger(0, Integer.MAX_VALUE);
									
									if (min <= max) {
										value = new IntegerIntervalFieldValue(min, max);
										check = true;
									} else {
										renderer.renderError("Inserire un valore minimo inferiore al valore massimo");
									}
								} while (!check);
								return value;
							}
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
	private List<Field<? extends FieldValue>> getCommonFields() {
		
		List<Field<? extends FieldValue>> commonFields = new ArrayList<>();
		commonFields.add(
				// Campo "titolo"
				new Field<StringFieldValue>(
						"Titolo",
						"Nome di fantasia attribuito all'evento",
						false,
						StringFieldValue::acquireValue
						));

		commonFields.add(
				// Campo "numero di partecipanti"
				new Field<IntegerFieldValue>(
						"Numero di partecipanti",
						"Numero di persone da coinvolgere nell'evento",
						true,
						(renderer, getter) -> {
							return new IntegerFieldValue(getter.getInteger(0, Integer.MAX_VALUE));
						}
						));

		commonFields.add(
				// Campo "termine ultimo di iscrizione"
				new Field<DateFieldValue>(
						"Termine ultimo di iscrizione",
						"Ultimo giorno utile per iscriversi all'evento",
						true,
						DateFieldValue::acquireValue
						));
				
		commonFields.add(
				// Campo "luogo"
				new Field<StringFieldValue>(
						"Luogo",
						"Il luogo di svolgimento o di ritrovo dell'evento",
						true,
						StringFieldValue::acquireValue
						));

		commonFields.add(
				// Campo "data e ora"
				new Field<DateFieldValue>(
						"Data e ora",
						"Il giorno e l'orario in cui si svolgerà o avrà inizio l'evento",
						true,
						DateFieldValue::acquireValue
						));

		commonFields.add(
				// Campo "durata"
				new Field<IntegerFieldValue>(
						"Durata",
						"La durata approssimata, in ore e minuti o in giorni, dell'evento",
						false,
						(renderer, getter) -> {
							renderer.renderText("Inserisci il valore numerico della durata");
							return new IntegerFieldValue(getter.getInteger(0, Integer.MAX_VALUE));
						}
						));

		commonFields.add(
				// Campo "quota individuale"
				new Field<MoneyAmountFieldValue>(
						"Quota individuale",
						"La spesa che ogni partecipante dovrà sostenere per l'evento",
						true,
						(renderer, getter) -> {
							renderer.renderText("Inserisci il costo di partecipazione");
							return new MoneyAmountFieldValue(getter.getFloat(0, Float.MAX_VALUE));
						}
						));

		commonFields.add(
				// Campo "compreso nella quota"
				new Field<StringFieldValue>(
						"Compreso nella quota",
						"Lista delle voci di spesa comprese nella quota di partecipazione",
						false,
						StringFieldValue::acquireValue
						));

		commonFields.add(
				// Campo "data e ora conclusive"
				new Field<DateFieldValue>(
						"Data e ora conclusive",
						"Il giorno e l'orario di conclusione dell'evento",
						false,
						DateFieldValue::acquireValue
						));

		commonFields.add(
				// Campo "note"
				new Field<StringFieldValue>(
						"Note",
						"Note aggiuntive sull'evento",
						false,
						StringFieldValue::acquireValue
						));
		
		return commonFields;
	}
	
}
