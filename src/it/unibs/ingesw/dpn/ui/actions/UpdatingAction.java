package it.unibs.ingesw.dpn.ui.actions;

import it.unibs.ingesw.dpn.ui.UserInterface;

/**
 * Interfaccia che estende l'interfaccia {@link Action} secondo il pattern GoF <emph>Composite</emph>.<br>
 * Il metodo "execute" del pattern diventa un metodo di default, e il cardine di questa interfaccia funzionale si 
 * sposta sul metodo "prepareAction".<br>
 * Più in particolare: ogni volta che si chiama il metodo "execute" di questa interfaccia, viene richiamato il 
 * metodo che ri-costruisce da zero l'azione da eseguire, e che potrebbe -se utilizzato in modo opportuno-
 * costruire un'azione diversa a seconda di eventuali parametri o contesti. L'azione ricostruita viene quindi eseguita
 * come una normale azione, e dimenticata alla fine dell'esecuzione.<br>
 * Questa interfaccia, in pratica, introduce un livello aggiuntivo di "indirezione" nell'albero strutturato del
 * pattern Composite, come fosse (ad esempio) un cuscinetto fra l'azione di un menu e l'azione di una sua opzione.
 * Questo cuscinetto entra in gioco ogni volta che si cerca di attraversare il ramo che congiunge il nodo padre al 
 * nodo figlio, costringendo il nodo figlio a "rigenerarsi" (tramite il metodo "prepareAction") prima di essere eseguito.
 * <br><br>
 * Questa interfaccia è utilizzata spesso per le azioni/menu che presentano delle "liste" che possono essere
 * modificate durante l'esecuzione del programma. In questo modo non viene passata come azione il menu contenente
 * la lista, ma il *processo* di creazione del menu contenente la lista.<br>
 * Essendo un processo qualcosa che può dipendere da parametri o dal contesto in cui è eseguito, è naturale che il risultato
 * ottenuto (ossia -per l'ennesima volta- l'azione da eseguire) può cambiare di volta in volta.
 * <br>
 * 
 * @author Michele Dusi
 *
 */
@FunctionalInterface
public interface UpdatingAction extends Action {
	
	/**
	 * Metodo fulcro di questa implementazione dell'interfaccia {@link Action}.<br>
	 * Permette di racchiudere l'intero processo di creazione dell'oggetto "Azione" che dovrà essere eseguito.
	 * Questo processo di creazione, poi, verrà chiamato ogni volta che si tenterà di eseguire questa azione,
	 * permettendo una modifica della stessa.
	 * 
	 * @return La nuova azione "preparata" appositamente.
	 */
	public Action prepareAction();

	/**
	 * Metodo ereditato da {@link Action} che segue lo schema del pattern GoF <emph>Composite</emph>.<br>
	 * Si limita a richiamare il metodo "prepareAction" per ri-costruire l'oggetto {@link Action}
	 * di interesse, e immediatamente dopo lo esegue.<br>
	 * Come indicato nella descrizione dell'interfaccia, questo livello aggiuntivo di indirezione
	 * permette di modificare ad ogni esecuzione l'azione da eseguire.
	 * 
	 * @param userInterface L'interfaccia utente necessaria per espletare l'azione
	 */
	@Override
	public default void execute(UserInterface userInterface) {
		this.prepareAction().execute(userInterface);
	}

}
