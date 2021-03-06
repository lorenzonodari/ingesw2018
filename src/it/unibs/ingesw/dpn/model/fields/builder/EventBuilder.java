package it.unibs.ingesw.dpn.model.fields.builder;


import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.events.ConferenceEvent;
import it.unibs.ingesw.dpn.model.events.SoccerMatchEvent;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.FieldValueUIAcquirer;

/**
 * Classe che permette la creazione di eventi in maniera "controllata", secondo il pattern "Factory" e
 * secondo un preciso processo.
 * 
 * Per la creazione di un evento è necessario chiamare, nell'ordine:
 * - startCreation(..);
 * - acquireFieldValue(..);		<- Quante volte si vuole
 * - finalise(..);
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class EventBuilder extends AbstractBuilder {
	
	/**
	 * Costruttore pubblico.
	 * 
	 * Precondizione: i parametri non devono essere nulli.
	 * 
	 * @param acquirer L'acquisitore di FieldValue
	 */
	public EventBuilder(FieldValueUIAcquirer acquirer) {
		super(acquirer);
	}
	
	/**
	 * Comincia la creazione di un evento, data una precisa categoria e un utente creatore.
	 * 
	 * Precondizione: la factory non deve avere altre creazioni in corso. Una factory puà costruire un solo
	 * evento alla volta, secondo il processo descritto nell'introduzione alla classe.
	 * 
	 * @param creator L'utente che ha creato l'evento
	 * @param category La categoria dell'evento
	 */
	public void startCreation(User creator, Category category) {
		// Verifico che i parametri non siano null
		if (creator == null || category == null) {
			throw new IllegalArgumentException("Impossibile creare un evento con creatore o categoria nulli");
		}
		
		// A seconda della categoria, istanzio una classe differente
		switch (category) {
		
		case CONFERENZA :
			super.startCreation(new ConferenceEvent(creator));
			break;
			
		case PARTITA_DI_CALCIO :
			super.startCreation(new SoccerMatchEvent(creator));
			break;
			
		}
	}
	
}
