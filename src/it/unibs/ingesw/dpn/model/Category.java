package it.unibs.ingesw.dpn.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
	
	public static final String EXCEPTION_FIELD_ALREADY_PRESENT = "Il campo \"%s\" è già presente all'interno della categoria \"%s\".";
	public static final String TO_STRING = "-- Caratteristiche della categoria --\n\n"
			+ "Nome:        %s\n"
			+ "Descrizione: %s\n"
			+ "\n"
			+ "-- Elenco dei campi --\n";
	
	private String name;
	private String description;
	private List<Field> fields;
	
	public Category(String name, String description) {
		this.name = name;
		this.description = description;
		this.fields = new ArrayList<>();
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
	 * Aggiunge un campo alla categoria.
	 * 
	 * Precondizione: il campo da aggiungere non deve avere lo stesso nome 
	 * di un altro campo all'interno di questa stessa categoria. Per fare ciò, 
	 * viene effettuato un controllo sull'appartenenza del nuovo campo alla 
	 * lista dei campi, mediante il metodo "equals" della classe {@link Field}
	 * che confronta due campi in funzione dei loro nomi.
	 * 
	 * @param newField il nuovo campo da aggiungere.
	 */
	void addField(Field newField) {
		if (!this.fields.contains(newField)) {
			this.fields.add(newField);
		} else {
			String exceptionMessage = String.format(EXCEPTION_FIELD_ALREADY_PRESENT, newField.getName(), this.name);
			throw new IllegalArgumentException(exceptionMessage);
		}
	}
	
	/**
	 * Restituisce una descrizione testuale completa dell'intera categoria e dei suoi campi.
	 * 
	 * @return la descrizione testuale della categoria e dei suoi campi.
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(String.format(TO_STRING,
				this.name,
				this.description));
		// Per ciascun campo aggiungo la relativa descrizione alla descrizione della categoria.
		for (Field f : this.fields) {
			str.append("\n" + f.toString());
		}
		return str.toString();
	}

}
