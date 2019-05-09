package it.unibs.ingesw.dpn.model.fields;

/**
 * Classe che permette la creazione di istanze di {@link FieldValue} in maniera "controllata", 
 * secondo il pattern "Factory".
 * Per la creazione di un oggetto {@link FieldValue} è necessario conoscere il campo {@link Field}
 * di riferimento e il valore testuale. 
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
		
		return null;
	}
	
}