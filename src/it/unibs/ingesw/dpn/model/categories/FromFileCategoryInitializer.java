package it.unibs.ingesw.dpn.model.categories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import it.unibs.ingesw.dpn.model.fields.Field;

/**
 * Classe che si occupa di inizializzare la lista di categorie all'avvio del programma leggendo i dati
 * relativi dagli opportuni file.
 * E' un'implementazione dell'interfaccia {@link CategoryInitializer}, che permette 
 * di seguire il pattern "Strategy" per risolvere il problema dell'inizializzazione.
 * In futuro potranno essere implementate nuove metodologie di inizializzazione semplicemente
 * creando nuove implementazioni di {@link CategoryInitializer} che seguano logiche diverse.
 * 
 * Nota: ogni implementazione di {@link CategoryInitializer} segue anche il pattern "Singleton".
 * 
 * @author Michele Dusi, Lorenzo Nodari, Emanuele Poggi
 *
 */
public class FromFileCategoryInitializer implements CategoryInitializer {
	
	/** Cartella di riferimento contenente tutti i file. */
	private static final String DIRECTORY = "category_descriptions/";
	private static final String COMMON_FIELDS_FILE = "common_fields";
	private static final String FILE_EXTENSION = ".xml";
	
	private static final String TAG_FIELD = "field";
	private static final String TAG_CATEGORY = "category";

	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_DESCRIPTION = "description";
	private static final String ATTRIBUTE_MANDATORY = "mandatory";
	private static final String ATTRIBUTE_TYPE = "type";

	private static final String UNKNOWN_ATTRIBUTE_EXCEPTION = "Cannot recognize the attribute \"%s\" of tag \"%s\"";
	private static final String MISSING_ATTRIBUTE_VALUES_EXCEPTION = "Missing values in one or more attributes {%s} in tag \"%s\"";
	
	/**
	 * Istanza unica della classe, secondo il pattern Singleton.
	 */
	private static FromFileCategoryInitializer singleton = null;

	/**
	 * Costruttore privato per permettere l'esistenza di un'unica istanza di classe.
	 */
	private FromFileCategoryInitializer() {}

	/**
	 * Restituisce una nuova istanza di FromFileCategoryInitializer, secondo il pattern "Singleton".
	 * L'istanza non viene creata finché il metodo non viene invocato per la prima volta, in modo
	 * da non istanziare oggetti inutili ai fini del programma (poiché è possibile che, in caso di 
	 * utilizzo di strategie diverse -secondo il pattern Strategy- questa classe non venga mai usata).
	 * 
	 * @return L'istanza unica di SimpleCategoryInitializer.
	 */
	static CategoryInitializer getInstance() {
		
		// Verifico che il singleton sia già stato istanziato
		if (FromFileCategoryInitializer.singleton == null) {
			FromFileCategoryInitializer.singleton = new FromFileCategoryInitializer();
		}
		
		// Restituisco l'istanza unica della classe
		return FromFileCategoryInitializer.singleton;
	}

	/**
	 * Metodo che inizializza e restituisce la lista di Categorie, come oggetti {@link Category}.
	 * Per ciascuna categoria legge il relativo file, che deve essere chiamato come la categoria stessa 
	 * (il nome di riferimento è quello dell'Enumerator {@link CategoryEnum}, ma in minuscolo e con formato
	 * ".xml") e posizionato nell'apposita directory.
	 * 
	 * @return la lista di categorie
	 */
	@Override
	public Category [] initCategories() {
		// Leggo i campi comuni a tutte le categorie
		Field[] common_fields = getCommonFields();
		
		// Controllo che la lettura dei campi sia andata a buon fine
		if (common_fields == null) {
			System.err.println("Non è stato possibile completare la lettura dei campi comuni");
			return null;
		}
		
		// Preparo la lista di categorie
		List<Category> category_list = new LinkedList<>();
		
		// Per ciascuna categoria
		for (CategoryEnum cat : CategoryEnum.values()) {
			Category category = parseXMLCategory(cat, common_fields);
			if (category == null) {
				System.err.println("Non è stata aggiunta alcuna categoria di tipo " + cat.name());
			} else {
				category_list.add(category);
			}
		}
		
		// Restituisco le categorie lette correttamente
		return category_list.toArray(new Category[category_list.size()]);
	}
	

	/**
	 * Metodo che inizializza i campi comuni a tutte le categorie.
	 * I dati relativi sono contenuti all'interno del file in formato XML denominato
	 * "common_fields.xml", a sua volta contenuto nella directory prestabilita.
	 * 
	 * @return L'array di campi comuni a tutte le categorie
	 */
	private Field [] getCommonFields() {
		
		XMLEventReader reader = prepareXMLReader(DIRECTORY, COMMON_FIELDS_FILE);
		// Controllo che l'inizializzazione del lettore sia andata a buon fine
		if (reader == null) {
			// Non è stato possibile inizializzare il lettore:
			// Termino qui l'esecuzione e restituisco null
			return null;
		}
		
		// Preparazione della lista che accoglierà i vari campi letti da file
		List<Field> field_list = new LinkedList<>();
		
		// Lettura del file
		while (reader.hasNext()) {
			
			try {
				// Creo un riferimento all'evento corrente
				XMLEvent event = reader.nextEvent();
				
				// Se incontro un nuovo elemento "field" come tag d'apertura
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equalsIgnoreCase(TAG_FIELD)) {
					field_list.add(parseXMLField(event.asStartElement()));
				}
				
			} catch (XMLStreamException e) {
				// TODO In attesa del logging, stampo tutto a schermo
				e.printStackTrace();
			}
		}
		
		// Restituisco la lista di tutti i "Field" letti da file
		return field_list.toArray(new Field[field_list.size()]);
	}
	
	/**
	 * Metodo che si occupa di tutta la preparazione di un lettore XML dato il nome del file.
	 * Dato che questa operazione viene ripetuta per ciascun file di ciascuna categoria e per il file
	 * dei campi comuni, è utile avere un unico metodo che gestisce in maniera standard il processo.
	 * In questo modo è possibile uniformare anche il comportamento in caso di eccezioni.
	 * 
	 * Nota: il nome del file è da passare senza estensione, poichè si suppone sia in XML.
	 * 
	 * @param file_path La directory in cui cercare il file
	 * @param file_name Il nome del file (senza estensione)
	 * @return Il lettore XML, se tutto va a buon fine. Altrimenti null.
	 */
	private XMLEventReader prepareXMLReader(String file_path, String file_name) {
		// Preparo il file
		File file = new File(file_path + file_name + FILE_EXTENSION);
		
		// Preparazione del lettore XML
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		try {
			XMLEventReader reader = factory.createXMLEventReader(new FileReader(file));
			return reader;
			
		} catch (FileNotFoundException e) {
			// TODO In attesa del logging, stampo tutto a schermo
			e.printStackTrace();
			
		} catch (XMLStreamException e) {
			// TODO In attesa del logging, stampo tutto a schermo
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Metodo che si occupa della lettura di un intero file di descrizione di una categoria.
	 * Il metodo cerca automaticamente il file con lo stesso nome della categoria (in minuscolo e in formato XML)
	 * all'interno della directory prevista dalla costante di classe DIRECTORY.
	 * 
	 * Questo metodo restituisce un'eccezione in caso il file sia mancante o illeggibile.
	 * 
	 * @param cat La categoria che si vuole parsare
	 * @param common_fields I campi comuni da aggiungere ad ogni categoria. Si suppone che siano contenuti in un altro file, per questo motivo sono passati dall'esterno.
	 * @return l'oggetto {@link Category} richiesto se è possibile parsarlo correttamente da file, altrimenti null.
	 */
	private Category parseXMLCategory(CategoryEnum cat, Field [] common_fields) {
		
		XMLEventReader reader = prepareXMLReader(DIRECTORY, cat.name().toLowerCase());
		// Controllo che l'inizializzazione del lettore sia andata a buon fine
		if (reader == null) {
			// Non è stato possibile inizializzare il lettore:
			// Termino qui l'esecuzione e restituisco null
			return null;
		}
		
		// Preparo delle variabili locali per contenere i dati che leggo
		String name = null, description = null;
		List<Field> exclusive_fields = new LinkedList<>();
		
		// Creo un array di boolean di controllo
		boolean [] setup_flags = {false, false};
		/* Ogni volta che viene inizializzato un valore di un attributo di Category, 
		 * il relativo flag viene settato a "true".
		 * Attualmente i due flag presenti si riferiscono a "name" e "description". Non è presente nessun
		 * flag per i campi esclusivi della categoria.
		 * E' possibile completare la creazione dell'oggetto Category solo se tutti i flag sono "true".
		 */
		
			
		// Lettura del file
		while (reader.hasNext()) {
			XMLEvent event = null;
			try {
				event = reader.nextEvent();
			} catch (XMLStreamException e) {
				// TODO In attesa del logging, stampo tutto a schermo
				e.printStackTrace();
			}
			
			// Se non incontro un tag d'apertura, passo al prossimo evento
			if (!event.isStartElement()) {
				continue;
			}
			
			// Se invece incontro un tag d'apertura, controllo quale ho parsato
			switch (event.asStartElement().getName().getLocalPart()) {

			case TAG_CATEGORY :
				// Devo ciclare sugli attributi
				Iterator<?> iterator = event.asStartElement().getAttributes();
				
				while (iterator.hasNext()) {
					Attribute attribute = (Attribute) iterator.next();
					String attribute_id = attribute.getName().getLocalPart();
					
					switch(attribute_id) {
					// Il nome della categoria
					case ATTRIBUTE_NAME :
						name = attribute.getValue().trim();
						setup_flags[0] = true;
						break;
					// La descrizione della categoria
					case ATTRIBUTE_DESCRIPTION :
						description = attribute.getValue().trim();
						setup_flags[1] = true;
						break;
					// Un attributo non riconosciuto
					default :
						throw new IllegalStateException(String.format(
								UNKNOWN_ATTRIBUTE_EXCEPTION,
								attribute_id,
								TAG_FIELD
								));
					}
				}
				break;
				
			case TAG_FIELD :
				exclusive_fields.add(parseXMLField(event.asStartElement()));
				break;
					
			}
		}
		
		// A questo punto ho tutti i dati pronti per la creazione di un oggetto Category
		if (setup_flags[0] && setup_flags[1]) {
			Category new_category = new Category(name, description);
			new_category.addAllFields(common_fields);
			for (Field field : exclusive_fields) {
				new_category.addField(field);
			}
			return new_category;
		} else {
			// TODO Convertirlo in un print su ERR
			throw new IllegalStateException(String.format(
					MISSING_ATTRIBUTE_VALUES_EXCEPTION,
					(ATTRIBUTE_NAME + ", " + ATTRIBUTE_DESCRIPTION),
					TAG_CATEGORY
					));
		}
	}
	
	/**
	 * Metodo che si occupa della lettura di oggetti "Field" da file.
	 * E' necessario passare come parametro l'oggetto di tipo {@link StartElement} ottenuto
	 * dall'evento XML letto dal parser.
	 * 
	 * @param field_start_element L'evento "Field" incontrato dal parser XML
	 * @return L'oggetto {@link Field} creato secondo i dati letti
	 */
	private Field parseXMLField(StartElement field_start_element) {
		String name = null, description = null;
		boolean mandatory = true;
		Class<?> type = null;
		
		// Creo un array di boolean di controllo
		boolean [] setup_flags = {false, false, false, false};
		// Ogni volta che viene inizializzato un valore di un attributo di field, il relativo flag viene settato
		// a "true". E' possibile completare la creazione dell'oggetto field solo se tutti i flag sono "true".
		
		Iterator<?> iterator = field_start_element.getAttributes();
		
		// Scorro su tutti gli attributi del tag Field
		while (iterator.hasNext()) {
			Attribute attribute = (Attribute) iterator.next();
			String attribute_id = attribute.getName().getLocalPart();
			
			switch(attribute_id) {
			// Il nome del campo
			case ATTRIBUTE_NAME :
				name = attribute.getValue().trim();
				setup_flags[0] = true;
				break;
			// La descrizione del campo	
			case ATTRIBUTE_DESCRIPTION :
				description = attribute.getValue().trim();
				setup_flags[1] = true;
				break;
			// L'obbligatorietà del campo
			case ATTRIBUTE_MANDATORY :
				mandatory = Boolean.parseBoolean(attribute.getValue().trim());
				setup_flags[2] = true;
				break;
			// Il tipo del valore del campo	
			case ATTRIBUTE_TYPE :
				try {
					type = Class.forName(attribute.getValue().trim());
					setup_flags[3] = true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
			// Un qualunque attributo non riconosciuto
			default :
				throw new IllegalStateException(String.format(
						UNKNOWN_ATTRIBUTE_EXCEPTION,
						attribute_id,
						TAG_FIELD
						));
			}
		}
		
		// Controllo se tutti gli attributi previsti per un oggetto Field sono stati inizializzati
		if (setup_flags[0] && setup_flags[1] && setup_flags[2] && setup_flags[3]) {
			return new Field(name, description, mandatory, type);
		} else {
			// TODO Convertirlo in un print su ERR
			throw new IllegalStateException(String.format(
					MISSING_ATTRIBUTE_VALUES_EXCEPTION,
					(ATTRIBUTE_NAME + ", " + ATTRIBUTE_DESCRIPTION + ", " + ATTRIBUTE_MANDATORY + ", " + ATTRIBUTE_TYPE),
					TAG_FIELD
					));
		}
	}
}
		