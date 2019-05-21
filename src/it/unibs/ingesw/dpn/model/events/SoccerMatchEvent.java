package it.unibs.ingesw.dpn.model.events;

import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.users.User;
import it.unibs.ingesw.dpn.ui.UIRenderer;

/**
 * Classe che rappresenta concettualmente il tipo di categoria "Partita di calcio".
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
	 * Costruttore della classe SoccerMatch, che verrà invocato da una classe
	 * apposita la cui responsabilità principale sarà creare eventi.
	 * 
	 * Precondizione: i valori dei campi devono essere uguali come numero e come tipo ai campi
	 * previsti dalla categoria. Questo viene garantito dalla classe adibita alla creazione degli eventi.
	 * 
	 * @param fieldValues i valori dei campi dell'evento di tipo "Partita di calcio"
	 */
	SoccerMatchEvent(User creator, Map<Field, FieldValue> fieldValues) {
		super(creator, CategoryEnum.PARTITA_DI_CALCIO, fieldValues);
	}
	
}
