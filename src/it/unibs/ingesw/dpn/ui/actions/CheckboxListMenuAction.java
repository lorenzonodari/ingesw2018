package it.unibs.ingesw.dpn.ui.actions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe che estende la classe {@link MenuAction} e rappresenta un menu fatto (almeno in parte) 
 * di checkboxes.<br>
 * Una checkbox è un'opzione del menu che, se selezionata, inverte (quindi seleziona/deseleziona) il valore
 * booleano ad essa associato.<br>
 * Inoltre, ogni checkbox nasce da uno specifico oggetto della classe <strong>T</strong>. Il metodo "getSelectedObjects" permette
 * in ogni momento di ottenere gli oggetti che sono stati selezionati mediante checkbox.<br>
 * 
 * Nota: è importante ridefinire l'azione di uscita in modo che utilizzi i valori, altrimenti non vengono
 * utilizzati e vanno "persi".
 * 
 * @author Michele Dusi
 *
 * @param <T> Il tipo degli oggetti da selezionare/deselezionare
 */
public class CheckboxListMenuAction<T> extends MenuAction {

	private Map<T, Boolean> checkboxSelections;
	
	private static final boolean DEFAULT_SELECTION = false;
	private static final String CHECKBOX_ENTRY_FORMAT = "[%c]\t%s";
	
	/**
	 * Costruttore.
	 * 
	 * @param title Il titolo del menu
	 * @param text Il contenuto del menu
	 * @param checkboxes La mappa di oggetti da selezionare/deselezionare, con la rispettiva descrizione che comparirà come opzione
	 */
	public CheckboxListMenuAction(String title, String text, Map<T, String> checkboxes) {
		super(title, text);
		
		// Inizializzo gli attributi
		this.checkboxSelections = new LinkedHashMap<T, Boolean>();
		
		// Scorro su tutti gli oggetti delle checkboxes
		for (T object : checkboxes.keySet()) {
			
			String objectString = checkboxes.get(object);
			
			// Inizializzo la selezione con il valore di default
			this.checkboxSelections.put(object, DEFAULT_SELECTION);
			
			// Creo un riferimento alla entry relativa all'oggetto
			MenuEntry entry = new MenuEntry(
					prepareEntryText(objectString, DEFAULT_SELECTION),
					Action.EMPTY_ACTION);

			// Cambio l'azione con quella corretta
			entry.setAction(getCheckboxAction(object, objectString, entry));
					/*
					 * Questo è necessario poiché l'azione deve 
					 * contenere un riferimento alla entry 
					 * (per poterne cambiare il testo), e 
					 * la entry ovviamente deve contenere l'azione.
					 */
			
			// Aggiungo la entry
			this.addEntry(entry);
		}
	}
	
	/**
	 * Restituisce l'azione che seleziona o deseleziona l'oggetto nella mappa corrispondente.
	 * 
	 * @param checkboxObject L'oggetto da selezionare/deselezionare
	 * @return L'azione di checkbox
	 */
	private Action getCheckboxAction(T checkboxObject, String checkboxObjectString, MenuEntry entryReference) {
		// Creo l'azione della checkbox
		SimpleAction checkboxAction = (userInterface) -> {
			// Ottengo il valore precedente
			boolean previous = checkboxSelections.get(checkboxObject);
			// Nego il valore precedente
			checkboxSelections.put(checkboxObject, !previous);
			// Modifico il testo della entry
			entryReference.setName(prepareEntryText(checkboxObjectString, !previous));
		};
		
		return checkboxAction;
	}
	
	/**
	 * Restituisce una stringa da usare come testo della entry.
	 * 
	 * @param objectString La stringa che caratterizza la checkbox
	 * @param selection La selezione della checkbox
	 * @return La stringa della entry
	 */
	private static String prepareEntryText(String objectString, boolean selection) {
		return String.format(CHECKBOX_ENTRY_FORMAT, (selection ? 'X' : ' '), objectString);
	}
	
	/**
	 * Restituisce la lista di oggetti selezionati mediante checkboxes.
	 * 
	 * @return La lista di oggetti selezionati
	 */
	public List<T> getSelectedObjects() {
		List<T> selectedObjects = new ArrayList<T>();
		
		for (T object : this.checkboxSelections.keySet()) {
			if (this.checkboxSelections.get(object)) {
				selectedObjects.add(object);
			}
		}
		
		return selectedObjects;
	}

}
