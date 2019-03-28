package it.unibs.ingesw.dpn.model;

/**
 * Classe che rappresenta concettualmente il tipo di categoria "Partita di calcio".
 * 
 * @author Michele Dusi, Emanuele Poggi, Lorenzo Nodari
 * 
 */
public class SoccerMatchEvent extends Event {

	/**
	 * Costruttore della classe SoccerMatch, che verrà invocato da una classe
	 * apposita la cui responsabilità principale sarà creare eventi.
	 * 
	 * Precondizione: i valori dei campi devono essere uguali come numero e come tipo ai campi
	 * previsti dalla categoria. Questo viene garantito dalla classe adibita alla creazione degli eventi.
	 * 
	 * @param fieldValues i valori dei campi dell'evento di tipo "Partita di calcio"
	 */
	SoccerMatchEvent(Object [] fieldValues) {
		super(CategoryEnum.PARTITA_DI_CALCIO, fieldValues);
	}
	
	/* 
	 * NOTA:
	 * Attualmente la classe non contiene nulla, è presente per puro scopo semantico
	 * in quanto anche nei requisiti della versione 1 si nomina la categoria "partita di calcio".
	 * Dalle successive versioni conterrà metodi che gestiranno il comportamento dell'evento di questa
	 * specifica categoria.
	 */

}
