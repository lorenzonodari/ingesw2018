package it.unibs.ingesw.dpn.model.persistence;

/**
 * Classe che si occupa del dei dati di dominio che necessitano di persistenza
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 */
public class PersistenceManager {

	private PersistenceStrategy strategy;
	private Model model;
	
	/**
	 * Istanzia un ModelManager, creando i riferimenti alle classi del che vengono utilizzate
	 * per fornire informazioni sul modello di dominio al resto del programma.
	 * 
	 * Precondizione: strategy != null
	 * 
	 * @param strategy La strategia da utilizzare per il caricamento dei dati di dominio
	 */
	public PersistenceManager(PersistenceStrategy strategy)  {
		
		// Verifica delle precondizioni
		if (strategy == null)  {
			throw new IllegalArgumentException();
		}
		
		this.strategy = strategy;
		this.model = null;
		
	}
	
	/**
	 * Effettua il caricamento dei dati di dominio mediante la strategia specificata durante la costruzione di questo oggetto.
	 * 
	 * @throws PersistenceException 
	 */
	public void load() throws PersistenceException {

			this.model = this.strategy.loadModel();
		
	}
	
	/**
	 * Effettua il salvataggio dei dati di dominio mediante la strategia specificata durante la costruzione di questo oggetto.
	 * 
	 * Precondizione: i dati di dominio devono essere stati precedentemente caricati mediante una chiamata
	 *                al metodo load()
	 * @throws PersistenceException 
	 */
	public void save() throws PersistenceException {
		
		if (this.model == null) {
			throw new IllegalStateException();
		}
		
		this.strategy.saveModel(this.model);
		
	}
	
	/**
	 * Restituisce un oggetto che permette l'accesso ai dati di dominio persistenti
	 * 
	 * Precondizione: i dati di dominio devono essere stati precedentemente caricati mediante una chiamata
	 *                al metodo load()
	 * 
	 * @return Un'istanza di ModelRepository contente i dati di dominio persistenti
	 */
	public Model getModel() {
		
		if (this.model == null) {
			throw new IllegalStateException();
		}
		
		return this.model;
	}
}
