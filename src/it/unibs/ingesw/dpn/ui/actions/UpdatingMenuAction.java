package it.unibs.ingesw.dpn.ui.actions;

import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Interfaccia dalla struttura molto simile a {@link UpdatingAction}.<br>
 * Quest'ultima, tuttavia, si comportava da incapsulatore generico e permetteva la creazione di una qualunque azione.
 * L'interfaccia funzionale {@link UpdatingMenuAction}, invece, permette l'aggiornamento di un menu (quindi di 
 * una {@link MenuAction}) senza terminarne l'esecuzione, ma ri-costruendo ogni volta tale menu.<br>
 * <br>
 * <em>Nota:</em> In caso non sia necessario l'aggiornamento dell'azione (inteso come la sua "ricostruzione" secondo
 * il metodo dell'interfaccia funzionale "prepareMenuAction") ad ogni selezione dell'utente, si consiglia
 * di usare la (più semplice) interfaccia {@link UpdatingAction}.<br>
 * 
 * @author Michele Dusi
 *
 */
@FunctionalInterface
public interface UpdatingMenuAction extends Action {

	/**
	 * Metodo che racchiude l'intero processo di creazione dell'oggetto "MenuAction" che dovrà essere eseguito. <br>
	 * È il metodo da implementare per l'interfaccia funzionale.<br>
	 * 
	 * Precondizione: Come si può banalmente osservare dal tipo di ritorno di questo metodo, è necessario
	 * che l'azione che viene ricostruita sia un'istanza della classe {@link MenuAction}, e NON una {@link Action}
	 * generica.<br>
	 * 
	 * @return La nuova azione-menu "preparata" appositamente.
	 */
	public MenuAction prepareMenuAction();
	
	/**
	 * Esegue l'azione associata a questo {@link UpdatingMenuAction}, secondo il pattern GoF <em>Composite</em>.<br>
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
