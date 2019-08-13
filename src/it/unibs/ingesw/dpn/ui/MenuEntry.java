package it.unibs.ingesw.dpn.ui;

/**
 * Classe rappresentante una delle possibili scelte presentate in un menu dell'interfaccia utente.
 * Si noti che le istanze di questa classe sono immutabili.
 */
public class MenuEntry {
	
	private String name;
	private Action entryAction;
	
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
	public MenuEntry(String name, Action entryAction) {
		
		// Verifica delle precondizioni
		if (name == null || entryAction == null) {
			throw new NullPointerException("Impossibile istanziare un'oggetto MenuEntry con componenti nulle");
		}
		
		this.name = name;
		this.entryAction = entryAction;
	}
	
	/**
	 * Restituisce il nome, ossia il messaggio, contenuto in questa entry.
	 * 
	 * @return Il nome dell'entry.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Restituisce l'azione contenuta in questa entry, ossia un oggetto {@link Action}
	 * che poi può essere eseguita.
	 * 
	 * @return L'azione associata a questa entry.
	 */
	public Action getAction() {
		return this.entryAction;
	}
	
}
