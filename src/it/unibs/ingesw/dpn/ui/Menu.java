package it.unibs.ingesw.dpn.ui;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Classe rappresentante le informazioni contenute in un menu dell'interfaccia utente.
 * Si noti che le istanze di questa classe sono immutabili.
 * 
 */
public class Menu {
	
	private String title;
	private String description;
	private LinkedList<MenuEntry> entries;
	private MenuEntry quitEntry;
	
	/**
	 * Crea un nuovo menu vuoto. Il titolo non puo' essere vuoto.
	 * Ogni menu deve inoltre essere obbligatoriamente accompagnato da una voce che permetta all'utente
	 * di uscire dal menu.
	 * 
	 * Precondizione: title != null
	 * Precondizione: quitEntry != null
	 * 
	 * @param title Il titolo del menu
	 * @param description La descrizione del menu
	 * @param quitAction L'opzione di uscita dal menu
	 */
	public Menu(String title, String description, MenuEntry quitEntry) {
		
		// Verifica delle precondizioni
		if (title == null || quitEntry == null) {
			throw new NullPointerException();
		}
		
		this.title = title;
		this.description = description != null ? description : "";
		this.quitEntry = quitEntry;
		this.entries = new LinkedList<MenuEntry>();
	}
	
	/**
	 * Aggiunge una nuova voce al menu
	 * 
	 * Precondizione: entry != null
	 * Postcondizione: this.entries.contains(entry) == true
	 * 
	 * @param entry La voce da aggiungere al menu
	 */
	public void addEntry(MenuEntry entry) {
		
		// Verifica della precondizione
		if (entry == null) {
			throw new IllegalArgumentException();
		}
		
		this.entries.add(entry);
		
		
		// Verifica della postcondizione
		assert this.entries.contains(entry);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDescription() {
		return this.description;
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
