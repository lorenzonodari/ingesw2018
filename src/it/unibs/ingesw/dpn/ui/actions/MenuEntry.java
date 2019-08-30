package it.unibs.ingesw.dpn.ui.actions;

/**
 * Classe rappresentante una delle possibili scelte presentate in un menu dell'interfaccia utente.
 * 
 * @author Lorenzo Nodari, Michele Dusi
 */
public class MenuEntry {
	
	private static final boolean DEFAULT_TERMINATION = false;
	
	/**
	 * Il testo da associare all'entry.
	 */
	private String name;
	
	/**
	 * L'azione da associare all'entry.
	 */
	private Action entryAction;
	
	/**
	 * Indica se la selezione di questa entry provoca la terminazione o no del menu
	 * di cui l'entry fa parte.
	 */
	private boolean termination;
	
	/**
	 * Crea una nuova voce di menu, dato il nome e l'azione corrispondenti; inoltre richiede
	 * di impostare se l'azione termina o meno l'esecuzione del menu.
	 * Non sono ammessi titoli o azioni nulle.
	 * 
	 * Precondizione: name != null && name != ""
	 * Precondizione: entryAction != null
	 * 
	 * @param name Il nome della voce del menu
	 * @param entryAction L'azione corrispondente alla voce
	 * @param isTerminatingEntry Indica se la selezione di questa entry provoca o meno la terminazione del menu
	 */
	public MenuEntry(String name, Action entryAction, boolean isTerminatingEntry) {
		
		// Verifica delle precondizioni
		if (name == null || entryAction == null) {
			throw new NullPointerException("Impossibile istanziare un'oggetto MenuEntry con componenti nulle");
		}
		
		this.name = name;
		this.entryAction = entryAction;
		this.termination = isTerminatingEntry;
	}
	
	/**
	 * Crea una nuova voce di menu, dato il nome e l'azione corrispondente.
	 * Non sono ammessi titoli o azioni nulle.<br>
	 * Di default, l'azione è impostata come NON terminante; questo significa che la sua 
	 * selezione all'interno del menu non provocherà la terminazione dello stesso.
	 * 
	 * Precondizione: name != null && name != ""
	 * Precondizione: entryAction != null
	 * 
	 * @param name Il nome della voce del menu
	 * @param entryAction L'azione corrispondente alla voce
	 */
	public MenuEntry(String name, Action entryAction) {
		this(name, entryAction, DEFAULT_TERMINATION);
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
	
	/**
	 * Restituisce <emph>TRUE</emph> se la selezione di questa entry all'interno del menu ({@link MenuAction})
	 * provoca la terminazione del menu stesso.<br>
	 * Al contrario, restituisce <emph>FALSE</emph> se la selezione di questa entry non provoca la terminazione del 
	 * menu, ma anzi causa la sua visualizzazione al termine dell'esecuzione dell'action associata a questa entry.
	 * 
	 * @return TRUE se la entry è un'opzione di terminazione per il menu
	 */
	boolean isTerminatingAction() {
		return this.termination;
	}
	
}
