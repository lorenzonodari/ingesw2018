package it.unibs.ingesw.dpn.model.events;

import java.util.List;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.SoccerMatchField;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe che rappresenta concettualmente il tipo di evento "Partita di calcio".
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 * 
 */
public class SoccerMatchEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4342916685556543088L;
	
	/**
	 * Costruttore della classe SoccerMatchEvent.
	 * 
	 * @param creator Il creatore dell'evento
	 */
	public SoccerMatchEvent(User creator) {
		super(creator, Category.PARTITA_DI_CALCIO);
		
		for (Field f : SoccerMatchField.values()) {
			this.addField(f);
		}
	}
	
}
