package it.unibs.ingesw.dpn.model.events;

import java.util.Map;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;
import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
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
	 * Costruttore della classe SoccerMatch, che verrà invocato da una classe
	 * apposita la cui responsabilità principale sarà creare eventi.
	 * Precondizione: i valori dei campi devono essere uguali come numero e come tipo ai campi
	 * previsti dalla categoria. Questo viene garantito dalla classe adibita alla creazione degli eventi.
	 * 
	 * @param fieldValues i valori dei campi dell'evento di tipo "Partita di calcio"
	 */
	public SoccerMatchEvent(User creator, Map<Field, FieldValue> fieldValues) {
		super(creator, CategoryEnum.PARTITA_DI_CALCIO, fieldValues);
	}

	@Override
	public String toString() {
		StringBuffer description = new StringBuffer();
		description.append("Evento    : Partita di calcio")
		.append("\n");
		description.append(String.format("Creato da : %s\n", this.getCreator().getUsername()));
		Category cat = CategoryProvider.getProvider().getCategory(CategoryEnum.PARTITA_DI_CALCIO);
		for (Field f : cat.getFields()) {
			if(!(this.getFieldValue(f) == null)) {
			description.append(String.format(" | %-50s : %s",
					f.getName(),
					this.getFieldValue(f).toString()))
				.append("\n");
			}
		}
		return description.toString();
	}
}
