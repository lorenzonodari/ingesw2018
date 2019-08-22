package it.unibs.ingesw.dpn.model.events;

import java.util.List;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.fields.ConferenceField;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.OptionalCostsFieldValue;
import it.unibs.ingesw.dpn.model.users.User;

/**
 * Classe che rappresenta concettualmente il tipo di evento "Conferenza".
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 * 
 */
public class ConferenceEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9171544783647521694L;

	/**
	 * Costruttore della classe ConferenceEvent.
	 * 
	 * @param creator Il creatore dell'evento
	 * @param fieldsList La lista di campi previsti per un evento "Conferenza"
	 */
	public ConferenceEvent(User creator) {
		super(creator, Category.CONFERENZA);
	}
	
	@Override
	public float getExpensesForUser(User user) {
		
		float baseAmount = super.getExpensesForUser(user);
		float extras = ((OptionalCostsFieldValue) this.getFieldValue(ConferenceField.SPESE_OPZIONALI)).getExpensesForUser(user);
		
		return baseAmount + extras;
	}

}
