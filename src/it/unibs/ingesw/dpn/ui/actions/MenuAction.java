package it.unibs.ingesw.dpn.ui.actions;

import java.util.List;

import it.unibs.ingesw.dpn.ui.UserInterface;

import java.util.LinkedList;
import java.util.Collections;

/**
 * Classe rappresentante le informazioni contenute in un menu dell'interfaccia utente.
 * Si noti che le istanze di questa classe sono immutabili.
 * <br>
 * Questa classe, inoltre, partecipa all'implementazione del pattern <em>Composite</em>, 
 * implementando l'interfaccia {@link Action}.
 * <br>
 * Ogni menu e' caratterizzato da un titolo, un contenuto testuale opzionale, delle 
 * opzioni selezionabili ({@link MenuEntry}) e da una opzione speciale per l'uscita dal menu,
 * assegnata di default ad un'opzione nulla.
 * 
 */
public class MenuAction implements Action {
	
	public static final String BACK_ENTRY_TITLE = "Indietro";
	
	private String title;
	private String text;
	private LinkedList<MenuEntry> entries;
	private MenuEntry backEntry;
	
	/**
	 * Crea un nuovo menu. Il titolo non puo' essere vuoto.
	 * Se il menu non prevede alcun testo al di fuori delle opzioni, il parametro text puo'
	 * essere null.
	 * 
	 * Precondizione: title != null
	 * 
	 * @param title Il titolo del menu
	 * @param text Il testo del menu, o null se non e' necessario
	 */
	public MenuAction(String title, String text) {
		
		// Verifica delle precondizioni
		if (title == null) {
			throw new IllegalArgumentException("Impossibile istanziare un oggetto MenuAction con titolo nullo");
		}
		
		this.title = title;
		this.text = text != null ? text : "";
		this.entries = new LinkedList<MenuEntry>();
		this.backEntry = new MenuEntry(BACK_ENTRY_TITLE, SimpleAction.EMPTY_ACTION);
	}
	
	/**
	 * Imposta l'opzione di uscita del menu in maniera differente da quella standard.
	 * 
	 * Precondizione: backEntryText != null
	 * Precondizione: backEntryAction != null
	 * 
	 * @param backEntryText Il testo dell'opzione di ritorno/uscita
	 * @param backEntryAction L'azione associata all'opzione di ritorno
	 */
	public void setBackEntry(String backEntryText, Action backEntryAction) {
		// Verifica delle precondizioni
		if (backEntryText == null || backEntryAction == null) {
			throw new NullPointerException("Impossibile aggiungere una entry con componenti nulle");
		}
		
		MenuEntry entry = new MenuEntry(backEntryText, backEntryAction);
		this.backEntry = entry;
	}
	
	/**
	 * Aggiunge una nuova voce al menu, dato il nome e l'azione associata.
	 * 
	 * Precondizione: title != null
	 * Precondizione: action != null
	 * Precondizione: Il testo dell'entry NON deve essere già presente nel menu
	 * 
	 * Postcondizione: this.entries.contains(entry) == true
	 * 
	 * @param entryText Il testo della nuova voce del menu
	 * @param entryAction L'azione associata alla nuova voce del menu
	 */
	public void addEntry(String entryText, Action entryAction) {
		
		// Verifica delle precondizioni
		if (entryText == null || entryAction == null) {
			throw new IllegalArgumentException("Impossibile aggiungere una entry con componenti nulle");
		} else {
			// Verifico che non esista già una entry con testo uguale
			for (MenuEntry me : this.entries) {
				if (me.getName().equals(entryText)) {
					throw new IllegalArgumentException("Impossibile aggiungere una entry con testo duplicato");
				}
			}
		}
		
		MenuEntry entry = new MenuEntry(entryText, entryAction);
		this.entries.add(entry);
		
		// Verifica della postcondizione
		assert this.entries.contains(entry);
	}
	
	/**
	 * Aggiungo una entry alla lista delle entry del menu.
	 * Questo metodo dipende da {@link MenuEntry}, quindi per ragioni di sicurezza è 
	 * utilizzabile solo all'interno di questo package.
	 * 
	 * Precondizione: entry != null<br>
	 * Precondizione: Non esistono altre entry con lo stesso testo<br>
	 * 
	 * Postcondizione: Questo menu contiene la entry passata come parametro<br>
	 * 
	 * @param entry L'entry da aggiungere
	 */
	void addEntry(MenuEntry entry) {
		
		// Verifica delle precondizioni
		if (entry == null) {
			throw new IllegalArgumentException("Impossibile aggiungere una entry nulla");
		} else {
			// Verifico che non esista già una entry con testo uguale
			for (MenuEntry me : this.entries) {
				if (me.getName().equals(entry.getName())) {
					throw new IllegalArgumentException("Impossibile aggiungere una entry con testo duplicato");
				}
			}
		}
		
		// Aggiungo la entry
		this.entries.add(entry);
		
		// Verifica della postcondizione
		assert this.entries.contains(entry);
	}
	
	/**
	 * Aggiunge un'intera lista di MenuEntry alle entires del menu.
	 * 
	 * Precondizione: la lista di entries non deve essere vuota o nulla.
	 * Precondizione: ogni entry non deve essere composta da elementi null e non deve essere null.
	 * 
	 * Postcondizione: al termine dell'esecuzione del metodo, tutte le entry sono state aggiunte.
	 * 
	 * @param menuEntries la lista di MenuEntry da aggiungere al menu.
	 */
	public void addAllEntry(List<MenuEntry> menuEntries) {
		
		// Verifica delle precondizioni
		if (menuEntries == null || menuEntries.isEmpty()) {
			throw new IllegalArgumentException("Impossibile aggiungere al menu una lista nulla o vuota di entry");
		} else if (menuEntries.contains(null)) {
			throw new IllegalArgumentException("Impossibile aggiungere una entry nulla al menu");
		}
		
		for (MenuEntry entry : menuEntries) {
			this.entries.add(entry);
			
			// Verifica della poscondizione
			assert this.entries.contains(entry);
		}
		
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.text;
	}
	
	public MenuEntry getBackEntry() {
		return this.backEntry;
	}
	
	/**
	 * Restituisce una lista non modificabile delle voci del menu. L'immutabilita' di
	 * tale lista, unita all'immutabilita' delle istanze di MenuEntry assicurano che
	 * non sia possibile per classi esterne modificare i menu.
	 * 
	 * @return Una view non modificabile della lista delle voci del menu
	 */
	public List<MenuEntry> getEntries() {
		return Collections.unmodifiableList(this.entries);
	}
	
	/**
	 * Esegue l'azione associata a questo {@link MenuAction}.
	 * Questo comprende:
	 * <ul>
	 * 	<li>Visualizzare il Menu</li>
	 * 	<li>Richiedere all'utente la selezione di un'opzione mediante l'interfaccia utente</li>
	 * 	<li>Eseguire ricorsivamente l'azione selezionata</li>
	 * </ul>
	 * 
	 * @param userInterface L'interfaccia utente
	 */
	@Override
	public void execute(UserInterface userInterface) {
		Action selectedAction;
		do {
			selectedAction = userInterface.getter().getMenuChoice(this);
			selectedAction.execute(userInterface);
		} while (selectedAction != this.backEntry.getAction());
	}

}
