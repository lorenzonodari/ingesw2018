package it.unibs.ingesw.dpn.ui.actions;

import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Interfaccia dalla struttura molto simile a {@link UpdatingAction}.<br>
 * Quest'ultima, tuttavia, si comportava da incapsulatore generico e permetteva la creazione di una qualunque azione.
 * L'interfaccia funzionale {@link UpdatingMenuAction}, invece, permette l'aggiornamento di un menu (quindi di 
 * una {@link MenuAction}) senza terminarne l'esecuzione, ma ri-costruendo ogni volta tale menu.
 * 
 * @author Michele Dusi
 *
 */
@FunctionalInterface
public interface UpdatingMenuAction extends Action {

	/**
	 * Metodo che racchiude l'intero processo di creazione dell'oggetto "MenuAction" che dovrà essere eseguito. <br>
	 * È il metodo da implementare per l'interfaccia funzionale.
	 * 
	 * @return La nuova azione-menu "preparata" appositamente.
	 */
	public MenuAction prepareMenuAction();
	
	/**
	 * Esegue l'azione associata a questo {@link UpdatingMenuAction}.
	 * Questo comprende:
	 * <ul>
	 * 	<li>Costruire da zero il Menu secondo il processo indicato in "prepareMenuAction"</li>
	 * 	<li>Visualizzare il Menu</li>
	 * 	<li>Richiedere all'utente la selezione di un'opzione mediante l'interfaccia utente</li>
	 * 	<li>Eseguire ricorsivamente l'azione selezionata</li>
	 * </ul>
	 * 
	 * @param userInterface L'interfaccia utente
	 */
	@Override
	public default void execute(UserInterface userInterface) {
		MenuEntry selectedEntry;
		do {
			selectedEntry = userInterface.getter().getMenuChoice(this.prepareMenuAction());
			selectedEntry.getAction().execute(userInterface);
		} while (!selectedEntry.isTerminatingAction());
	}

}
