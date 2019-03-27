package it.unibs.ingesw.dpn.model;

/**
 * La classe "Field" rappresenta un campo di una categoria all'interno del modello concettuale del progetto.
 * Un campo è caratterizzato da un nome, da una descrizione, dall'essere o meno obbligatorio e dal tipo di valore
 * atteso. 
 * 
 * Nota: ogni attributo viene presentato come "final" poichè non vi è mai la necessità che il suo valore cambi runtime.
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 * 
 */
public class Field {
	
	private static final String TO_STRING =
			  "Nome:           %s\n"
			+ "Descrizione:    %s\n"
			+ "%s\n"
			+ "Tipo:           %s\n";
	
	private static final String MANDATORY_TAG = "Obbligatorio";
	private static final String OPTIONAL_TAG = "Facoltativo";

	private final String name;
	private final String description;
	private final boolean mandatory;
	private Class<?> type = Object.class;
	
	public Field(String name, String description, boolean mandatory, Class<?> type) {
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	
	/**
	 * Casta un oggetto al tipo previsto.
	 * Nel caso in cui l'oggetto non sia un oggetto di tale istanza, viene lanciata un'eccezione.
	 * 
	 * Precondizione: l'oggetto deve essere del tipo previsto.
	 * 
	 * @param value il valore da castare.
	 * @return il valore 
	 */
	@SuppressWarnings("unchecked")
	public <T> T castToType(Object value) throws ClassCastException {
		if (this.type.isInstance(value)) {
			return (T) this.type.cast(value);
		} else {
			throw new ClassCastException();
		}
	}
	
	/**
	 * Verifica se questo campo è uguale ad un altro.
	 * E' possibile passare all'interno del metodo un qualunque oggetto. Tale metodo 
	 * restituisce TRUE se e solo se due campi hanno lo stesso nome.
	 * Per questo motivo, questo metodo può essere utilizzato nella classe {@link Category} per capire quando
	 * due campi hanno lo stesso nome (e quindi, a livello di categoria, sono uguali).
	 * 
	 * @param otherField un oggetto con cui operare il confronto.
	 * @return l'uguaglianza fra i due campi.
	 */
	@Override
	public boolean equals(Object otherField) {
		if (otherField != null && Field.class.isInstance(otherField)) {
			return this.name.equals(((Field) otherField).name);
		} else {
			return false;
		}
	}
	
	/**
	 * Restituisce la stringa per la rappresentazione testuale dell'intero campo.
	 * 
	 * Nota: il tipo del campo non viene visualizzato, poiché l'utente riceve tutte 
	 * le informazioni di cui ha bisogno dal campo descrizione.
	 * 
	 * @return la rappresentazione testuale dell'intero campo.
	 */
	@Override
	public String toString() {
		String str = String.format(TO_STRING, 
				this.name,
				this.description,
				this.mandatory ? 
						MANDATORY_TAG :
						OPTIONAL_TAG,
				this.type.getSimpleName()
				);
		return str;
	}
}
