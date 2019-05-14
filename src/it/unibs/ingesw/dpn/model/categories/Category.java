package it.unibs.ingesw.dpn.model.categories;

import java.util.ArrayList;
import java.util.List;

import it.unibs.ingesw.dpn.model.events.Event;
import it.unibs.ingesw.dpn.model.fields.Field;

/**
 * Classe che rappresenta una categoria di eventi all'interno del programma.
 * Una categoria è caratterizzata da un nome, una descrizione e una lista di campi.
 * Per la creazione di una categoria è necessario utilizzare la classe {@link CategoryProvider}.
 * In quanto categoria astratta e concettuale, non contiene alcun riferimento alla classe {@link Event}.
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 *
 */
public class Category {
	
	private static final String EXCEPTION_FIELD_ALREADY_PRESENT = "Il campo \"%s\" è già presente all'interno della categoria \"%s\".";
	private static final String TO_STRING = "-- Caratteristiche della categoria --\n\n"
			+ "Nome:        %s\n"
			+ "Descrizione: %s\n"
			+ "\n"
			+ "-- Elenco dei campi --\n";
	
	private String name;
	private String description;
	private List<Field> fields;
	
	/**
	 * Costruttore con modificatore di accesso "friendly" della classe Category.
	 * L'unica classe adibita alla creazione di un'istanza di {@link Category}
	 * è la classe {@link CategoryProvider}.
	 * 
	 * @param name Il nome della categoria
	 * @param description La descrizione della categoria
	 */
	Category(String name, String description) {
		this.name = name;
		this.description = description;
		this.fields = new ArrayList<>();
	}

	/**
	 * @return Il nome della categoria
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return La descrizione della categoria
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Restituisce un campo della categoria con il nome corrispondente.
	 * Se assente, restituisce "null".
	 * Ovviamente viene dato per scontato che esista al più un campo con un dato nome all'interno della categoria.
	 * 
	 * @return Il campo col nome richiesto, come oggetto {@link Field}
	 */
	public Field getFieldByName(String fieldName) {
		// Scorro attraverso tutti i campi
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {
				return f;
			}
		}
		return null;
	}
	
	/**
	 * Restituisce l'array con tutti i campi della categoria.
	 * 
	 * @return i campi della categoria
	 */
	public List<Field> getFields() {
		return this.fields;
	}

	/**
	 * Aggiunge un campo alla categoria.
	 * 
	 * Nota: questo metodo può essere utilizzato solamente all'interno di questo package.
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
	 * Aggiunge un'intera lista di campi alla categoria.
	 * 
	 * Nota: questo metodo può essere utilizzato solamente all'interno di questo package.
	 * 
	 * Precondizione: non devono esserci campi con lo stesso nome o con nomi già presenti
	 * all'interno della categoria. Questo metodo non fa altro che richiamare il metodo 
	 * di questa classe "addField" su tutti i campi passati come parametro.
	 * 
	 * @param newFields i nuovi campi da aggiungere.
	 */
	void addAllFields(Field ... newFields) {
		for (Field f : newFields) {
			this.addField(f);
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
