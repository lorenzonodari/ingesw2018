package it.unibs.ingesw.dpn.ui;

/**
 * Interfaccia funzionale utilizzata per rappresentare 
 * un'azione da eseguire in maniera diretta.
 * E' figlia dell'interfaccia generica {@link Action}, 
 * che invece NON è un'interfaccia funzionale e obbliga 
 * le sottoclassi a sovrascrivere il metodo 
 * (che di default restituisce una eccezione).
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi 
 */
@FunctionalInterface
public interface SimpleAction extends Action {

	@Override
	public void execute(UserInterface userInterface);

}
