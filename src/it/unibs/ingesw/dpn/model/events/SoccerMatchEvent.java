package it.unibs.ingesw.dpn.model.events;

import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.FieldValue;
import it.unibs.ingesw.dpn.model.users.User;

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
	@Deprecated
	SoccerMatchEvent(Map<Field, FieldValue> fieldValues) {
		super(CategoryEnum.PARTITA_DI_CALCIO, fieldValues);
	}
	
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
	
	/* 
	 * NOTA:
	 * Attualmente la classe non contiene nulla, è presente per puro scopo semantico
	 * in quanto anche nei requisiti della versione 1 si nomina la categoria "partita di calcio".
	 * Dalle successive versioni conterrà metodi che gestiranno il comportamento dell'evento di questa
	 * specifica categoria.
	 */

}
