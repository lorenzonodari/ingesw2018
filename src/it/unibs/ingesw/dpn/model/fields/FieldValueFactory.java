package it.unibs.ingesw.dpn.model.fields;

import java.lang.reflect.InvocationTargetException;

import it.unibs.ingesw.dpn.ui.Menu;
import it.unibs.ingesw.dpn.ui.MenuAction;
import it.unibs.ingesw.dpn.ui.MenuEntry;

/**
 * Classe che permette la creazione di istanze di {@link FieldValue} in maniera "controllata", 
 * secondo il pattern "Factory".
 * Per la creazione di un oggetto {@link FieldValue} è necessario conoscere il campo {@link Field}
 * di riferimento. 
 * 
 * @author Michele Dusi
 *
 */
public class FieldValueFactory {
	
	private static FieldValueFactory singletonFactory = null;

	/**
	 * Costruttore privato, secondo il pattern Singleton.
	 */
	private FieldValueFactory() {
				
	}
	
	/**
	 * Restituisce l'istanza unica della factory.
	 * 
	 * @return L'unica istanza di FieldValueFactory
	 */
	public static FieldValueFactory getFactory() {
		if (FieldValueFactory.singletonFactory == null) {
			FieldValueFactory.singletonFactory = new FieldValueFactory();
		}
		return FieldValueFactory.singletonFactory;
	}
	
	public FieldValue createFieldValue(Field field, String value) {
		java.lang.Class<?> type = field.getType();
		
		if (type.isEnum()) {
			// L'oggetto FieldValue è un ENUM			
			
		} else {
			// L'oggetto FieldValue NON è un ENUM
			
		}
		
		return null;
	}
	
}