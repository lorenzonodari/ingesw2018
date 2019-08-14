package it.unibs.ingesw.dpn.ui.actions;

import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Quest'interfaccia rappresenta in maniera standard una qualunque azione
 * realizzabile dall'utente mediante l'interfaccia utente fornita dal programma ({@link UserInterface})
 * dal sistema di menu.<br>
 * Segue il pattern <em>Composite</em>, quindi ogni azione può essere la composizione di più azioni diverse
 * (come un Menu, che permette di scegliere) oppure un'azione semplice.
 * 
 * @author Michele Dusi
 *
 */
public interface Action {
	
	/**
	 * Azione pre-costruita che non esegue nulla.<br>
	 * E' implementata come istanza anonima di {@link SimpleAction}.
	 */
	public static final Action EMPTY_ACTION = new SimpleAction() {
		// Azione vuota, non fa nulla.
		@Override
		public void execute(UserInterface userInterface) {}
	};

	/**
	 * Metodo dell'interfaccia funzionale che esegue l'azione prevista.
	 * 
	 * @param userInterface L'interfaccia utente necessaria per assumere il controllo dell'azione o per espletarla
	 */
	public default void execute(UserInterface userInterface) {
		throw new IllegalStateException("Impossibile utilizzare l'interfaccia \"Action\" come interfaccia funzionale.");
	}
		
}
