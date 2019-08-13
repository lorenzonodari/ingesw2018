package it.unibs.ingesw.dpn.ui;

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
	 * Metodo dell'interfaccia funzionale che esegue l'azione prevista.
	 * 
	 * @param userInterface L'interfaccia utente necessaria per assumere il controllo dell'azione o per espletarla
	 */
	public default void execute(UserInterface userInterface) {
		throw new IllegalStateException("Impossibile utilizzare l'interfaccia \"Action\" come interfaccia funzionale.");
	}
		
}
