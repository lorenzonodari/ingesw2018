package it.unibs.ingesw.dpn.model.persistence;

/**
 * Interfaccia utilizzata per l'implementazione del pattern Strategy nell'ambito della
 * scelta del metodo di caricamento e salvataggio dei dati del model persistenti
 * 
 * @author Lorenzo Nodari
 *
 */
public interface PersistenceStrategy {

	Model loadModel() throws PersistenceException;
	
	void saveModel(Model model) throws PersistenceException;
	
}
