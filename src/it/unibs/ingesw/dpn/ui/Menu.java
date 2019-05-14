package it.unibs.ingesw.dpn.ui;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Classe rappresentante le informazioni contenute in un menu dell'interfaccia utente.
 * Si noti che le istanze di questa classe sono immutabili.
 * 
 * Ogni menu e' caratterizzato da un titolo, un contenuto testuale opzionale, delle 
 * opzioni selezionabili ({@link MenuEntry}) e da una opzione speciale per l'uscita dal menu.
 * 
 */
public class Menu {
	
	public static final String BACK_ENTRY_TITLE = "Indietro";
	
	private String title;
	private String text;
	private LinkedList<MenuEntry> entries;
	private MenuEntry quitEntry;
	
	/**
	 * Crea un nuovo menu. Il titolo non puo' essere vuoto.
	 * Se il menu non prevede alcun testo al di fuori delle opzioni, il parametro text puo'
	 * essere null.
	 * 
	 * Precondizione: title != null
	 * Precondizione: backTitle != null
	 * Precondizione: backAction != null
	 * 
	 * @param title Il titolo del menu
	 * @param text Il testo del menu, o null se non e' necessario
	 * @param backTitle Il nome dell'opzione di uscita
	 * @param backAction La callback di uscita dal menu
	 */
	public Menu(String title, String text, String backTitle, MenuAction backAction) {
		
		// Verifica delle precondizioni
		if (title == null || backTitle == null || backAction == null) {
			throw new NullPointerException();
		}
		
		this.title = title;
		this.text = text != null ? text : "";
		this.quitEntry = new MenuEntry(backTitle, backAction);
		this.entries = new LinkedList<MenuEntry>();
	}
	
	/**
	 * Crea un nuovo menu dal titolo dato, privo di contenuto testuale. Il nome dell'opzione di uscita
	 * dal menu e' assegnato di default.
	 * 
	 * Precondizione: title != null
	 * Precondizione: backAction != null
	 * 
	 * @param title Il titolo del menu
	 * @param backAction La callback di uscita dal menu
	 */
	public Menu(String title, MenuAction backAction) {
		this(title, null, BACK_ENTRY_TITLE, backAction);
	}
	
	/**
	 * Aggiunge una nuova voce al menu, dato il nome e l'azione associata
	 * 
	 * Precondizione: title != null
	 * Precondizione: action != null
	 * Postcondizione: this.entries.contains(entry) == true
	 * 
	 * @param entry La voce da aggiungere al menu
	 */
	public void addEntry(String title, MenuAction action) {
		
		// Verifica delle precondizioni
		if (title == null || action == null) {
			throw new NullPointerException();
		}
		
		MenuEntry entry = new MenuEntry(title, action);
		this.entries.add(entry);
		
		
		// Verifica della postcondizione
		assert this.entries.contains(entry);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.text;
	}
	
	public MenuEntry getQuitEntry() {
		return this.quitEntry;
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

}
