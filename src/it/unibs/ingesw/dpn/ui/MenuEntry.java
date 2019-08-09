package it.unibs.ingesw.dpn.ui;

/**
 * Classe rappresentante una delle possibili scelte presentate in un menu dell'interfaccia utente.
 * Si noti che le istanze di questa classe sono immutabili.
 */
public class MenuEntry {
	
	private String name;
	private MenuAction entryAction;
	
	/**
	 * Crea una nuova voce di menu, dato il nome e l'azione corrispondente.
	 * Non sono ammessi titoli o azioni nulle.
	 * 
	 * Precondizione: name != null && name != ""
	 * Precondizione: entryAction != null
	 * 
	 * @param name Il nome della voce del menu
	 * @param entryAction L'azione corrispondente alla voce
	 */
	public MenuEntry(String name, MenuAction entryAction) {
		
		// Verifica delle precondizioni
		if (name == null || entryAction == null) {
			throw new NullPointerException("Impossibile istanziare un'oggetto MenuEntry con componenti nulle");
		}
		
		this.name = name;
		this.entryAction = entryAction;
	}
	
	public String getName() {
		return this.name;
	}
	
	public MenuAction getAction() {
		return this.entryAction;
	}
	
}
