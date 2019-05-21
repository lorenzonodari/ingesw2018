package it.unibs.ingesw.dpn.model.events;

import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
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
	SoccerMatchEvent(User creator, Map<Field, FieldValue> fieldValues) {
		super(creator, CategoryEnum.PARTITA_DI_CALCIO, fieldValues);
	}

	@Override
	public String toString() {
<<<<<<< HEAD
		// TODO Auto-generated method stub
		return null;
	}
	
=======
		StringBuffer exit = new StringBuffer();;
		exit.append("Evento : Partita di calcio");
		Category cat = CategoryProvider.getProvider().getCategory(CategoryEnum.PARTITA_DI_CALCIO);
		for (Field f : cat.getFields()) {
			exit.append(String.format(" | %-35s : %s",
					f.getName(),
					this.getFieldValue(f).toString()));
		}
		return exit.toString();
	}
>>>>>>> 73c830abb933e0ce9fdfaed1a82911021a262e4d
}
