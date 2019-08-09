package it.unibs.ingesw.dpn.model;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import it.unibs.ingesw.dpn.Main;
import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

/**
 * Classe che centralizza l'accesso ai dati di dominio. Tramite questa classe
 * e' quindi possibile accedere alla lista delle categorie, agli eventi registrati, etc...
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 */
public class ModelManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4340634112104559263L;
	
	private UsersRepository usersManager;
	private EventBoard eventBoard;
	
	/**
	 * Istanzia un ModelManager, creando i riferimenti alle classi del che vengono utilizzate
	 * per fornire informazioni sul modello di dominio al resto del programma.
	 */
	public ModelManager()  {
		
		this.usersManager = new UsersRepository();
		this.eventBoard = new EventBoard();
		
	}
	
	/**
	 * Carica un ModelManager e i dati da esso gestiti dal file dato, contenente la serializzazione
	 * di un ModelManager
	 * 
	 * Precondizione: database != null
	 * Precondizione: database.exists()
	 * Precondizione: database.canRead()
	 * 
	 * @param database Il file dal quale caricare il ModelManager
	 */
	public static ModelManager loadFromDisk(File database) {
		
		// Verifica delle precondizioni
		if (!database.exists() || !database.canRead() || database == null)  {
			throw new IllegalArgumentException();
		}

		ModelManager modelManager = null;
				
		try (ObjectInputStream objInput = new ObjectInputStream(
											new FileInputStream(database))) {
			
			modelManager = (ModelManager) objInput.readObject();
					
		}
		catch (Exception ex) {
			
			System.err.println("Errore di lettura del database utenti: " + ex.toString());
			System.exit(Main.DB_LOAD_ERROR_EXIT_CODE);
			
		}
		
		// Ripristino lo stato corretto degli eventi in bacheca
		for (Event e : modelManager.getEventBoard().getEvents()) {
			
			e.resetState();
			
		}
		
		return modelManager;
		
	}
	
	/**
	 * Crea un file di salvataggio, utilizzabile per ripristinare lo stato del model in un secondo momento,
	 * mediante {@link ModelManager.loadFromDisk()}
	 * 
	 * Precondizione: database != null
	 * 
	 */
	public void saveToDisk(File database) {
		
		// Verifica delle precondizioni
		if (database == null) {
			throw new IllegalArgumentException();
		}
		
		try (ObjectOutputStream objOutput = new ObjectOutputStream(
												new FileOutputStream(database))) {
												
			objOutput.writeObject(this);
			
		}
		catch (Exception ex) {
			
			System.err.println("Errore durante la serializzazione del model: " + ex.toString());
			System.exit(Main.DB_SAVE_ERROR_EXIT_CODE);
			
		}
		
	}
	
	/**
	 * Restituisce la bacheca gestita da questo ModelManager
	 * 
	 * @return La bacheca degli eventi
	 */
	public EventBoard getEventBoard() {
		return this.eventBoard;
	}
	
	/**
	 * Restituisce l'array delle categorie di eventi previste dal programma.
	 * Le categorie sono "fisse" e non possono essere modificate, create o distrutte durante l'esecuzione
	 * del programma.
	 * Questo metodo, pertanto, restituirà sempre lo stesso array.
	 * 
	 * @return L'array contenente le categorie registrate
	 */
	public Category [] getAllCategories() {
		return Category.values();
	}
	
	/**
	 * Restituisce il gestore degli utenti associato al model manager.
	 * 
	 * @return Il gestore degli utenti in uso
	 */
	public UsersRepository getUsersManager() {
		return this.usersManager;
	}
}
