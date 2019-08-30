package it.unibs.ingesw.dpn.model.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

/**
 * Strategia di persistenza dei dati di dominio basata sul salvataggio su disco dei dati
 * mediante la serializzazione degli oggetti. Nel caso in cui 
 * 
 * @author Lorenzo Nodari
 *
 */
public class DiskSerializationStrategy implements PersistenceStrategy {
	
	private File databaseFile;
	
	/**
	 * Inizializza la strategia di persistenza basata sul caricamento da disco dei dati salvati
	 * mediante serializzazione. Nel caso in cui il file di database non esista, la strategia assume
	 * che non siano presenti dati di dominio da caricare e provvede ad inizializzare un model vuoto.
	 * 
	 * Precondizione: databaseFile != null
 	 * 
	 * @param databaseFile Il file di database da utilizzare per il caricamento e il salvataggio dei dati
	 */
	public DiskSerializationStrategy(File databaseFile) {
		
		if (databaseFile == null) {
			throw new IllegalArgumentException();
		}
		
		this.databaseFile = databaseFile;
	}

	@Override
	public Model loadModel() throws PersistenceException {
		
		EventBoard events = null;
		UsersRepository users = null;
		
		if (!databaseFile.exists()) {
			events = new EventBoard();
			users = new UsersRepository();
		}
		else {
							
			try (ObjectInputStream objInput = new ObjectInputStream(
												new FileInputStream(databaseFile))) {
				
				events = (EventBoard) objInput.readObject();
				users = (UsersRepository) objInput.readObject();
						
			}
			catch (Exception ex) {
				
				throw new PersistenceException("Errore durante la lettura del database", ex);
				
			}
			
		}
			
		events.resetEventStates();
		
		return new Model(events, users);
	}

	@Override
	public void saveModel(Model model) throws PersistenceException {
		
		try (ObjectOutputStream objOutput = new ObjectOutputStream(
												new FileOutputStream(databaseFile))) {
												
			objOutput.writeObject(model.getEventBoard());
			objOutput.writeObject(model.getUsersRepository());
			
		}
		catch (Exception ex) {
			
			throw new PersistenceException("Errore durante la scrittura del database", ex);
			
		}
		
	}

}
