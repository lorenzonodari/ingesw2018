package it.unibs.ingesw.dpn.ui.actions;

import java.util.List;

import it.unibs.ingesw.dpn.ui.UserInterface;

import java.util.LinkedList;
import java.util.Collections;

/**
 * Classe rappresentante le informazioni contenute in un menu dell'interfaccia utente.
 * <br>
 * Questa classe, inoltre, partecipa all'implementazione del pattern <em>Composite</em>, 
 * implementando l'interfaccia {@link Action}.
 * <br>
 * Ogni menu e' caratterizzato da:
 * <ul>
 * 	<li> un titolo </li>
 * 	<li> un contenuto testuale opzionale </li>
 * 	<li> delle opzioni selezionabili come oggetti ({@link MenuEntry}), ciascuna delle quali è associata 
 * ad un'{@link Action} e può essere terminante o no. </li>
 * 	<li> un'opzione speciale per l'uscita dal menu, impostata automaticamente come terminante e associata ad un'azione nulla. </li>
 * </ul>
 * Questa particolare implementazione permette una maggiore flessibilità sulla permanenza o sull'uscita 
 * dal menu. Inoltre, l'opzione di uscita assicura che il menu possa terminare in almeno un modo.
 * 
 * @author Lorenzo Nodari, Michele Dusi
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
		// Imposto l'opzione di uscita come azione vuota, ma terminante
		this.backEntry = new MenuEntry(BACK_ENTRY_TITLE, SimpleAction.EMPTY_ACTION, true);
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
		
		// Imposto una nuova entry *terminante*
		MenuEntry entry = new MenuEntry(backEntryText, backEntryAction, true);
		this.backEntry = entry;
	}
	
	/**
	 * Aggiunge una nuova voce al menu, dato il nome, l'azione associata e 
	 * se l'entry è terminante o no (vedi {@link MenuEntry} per maggiori informazioni).
	 * 
	 * Precondizione: title != null
	 * Precondizione: action != null
	 * Precondizione: Il testo dell'entry NON deve essere già presente nel menu
	 * 
	 * Postcondizione: this.entries.contains(entry) == true
	 * 
	 * @param entryText Il testo della nuova voce del menu
	 * @param entryAction L'azione associata alla nuova voce del menu
	 * @param isTerminatingEntry Indica se l'entry provoca l'uscita del menu al termine della sua esecuzione
	 */
	public void addEntry(String entryText, Action entryAction, boolean isTerminatingEntry) {
		
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
		
		MenuEntry entry = new MenuEntry(entryText, entryAction, isTerminatingEntry);
		this.entries.add(entry);
		
		// Verifica della postcondizione
		assert this.entries.contains(entry);
	}
	
	/**
	 * Aggiunge una nuova voce al menu, dato il nome e l'azione associata.
	 * Di default, l'entry creata NON termina l'esecuzione di questo menu.
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
		
		// Aggiungo un'entry di default NON TERMINANTE.
		this.addEntry(entryText, entryAction, false);
		
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
		MenuEntry selectedEntry;
		do {
			selectedEntry = userInterface.getter().getMenuChoice(this);
			selectedEntry.getAction().execute(userInterface);
		} while (!selectedEntry.isTerminatingAction());
	}

}
