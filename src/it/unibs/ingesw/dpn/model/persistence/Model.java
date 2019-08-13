package it.unibs.ingesw.dpn.model.persistence;

import it.unibs.ingesw.dpn.model.events.EventBoard;
import it.unibs.ingesw.dpn.model.users.UsersRepository;

/**
 * Classe utilizzata per fornire un punto di accesso centralizzato alle strutture
 * dati del model che necessitano di persistenza. Le istanze di questa classe, poiché essenzialmente
 * prive di logica, sono DTO.
 *
 */
public class Model {
	
	private EventBoard events;
	private UsersRepository users;
	
	public Model(EventBoard events, UsersRepository users) {
		
		// Precondizioni
		if (events == null || users == null) {
			throw new IllegalArgumentException();
		}
		
		this.events = events;
		this.users = users;
	}
	
	public EventBoard getEventBoard() {
		return this.events;
	}
	
	public UsersRepository getUsersRepository() {
		return this.users;
	}

}
