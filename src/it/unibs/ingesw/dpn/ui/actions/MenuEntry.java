package it.unibs.ingesw.dpn.ui.actions;

/**
 * Classe rappresentante una delle possibili scelte presentate in un menu dell'interfaccia utente.
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
	 * Imposta il nome, ossia il messaggio, contenuto in questa entry.<br>
	 * Se già presente, il nome precedente viene sovrascritto.<br>
	 * Questo metodo è utilizzabile solo all'interno di questo package, per ragioni di sicurezza.
	 * 
	 * @param Il nuovo nome dell'entry.
	 */
	void setName(String name) {
		this.name = name;
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
	
	/**
	 * Imposta l'azione contenuta in questa entry, ossia un oggetto {@link Action}.<br>
	 * Questo metodo è utilizzabile solo all'interno di questo package, per ragioni di sicurezza.
	 * 
	 * @param La nuova azione dell'entry.
	 */
	void setAction(Action action) {
		this.entryAction = action;
	}
	
}
