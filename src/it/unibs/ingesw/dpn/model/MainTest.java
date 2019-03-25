package it.unibs.ingesw.dpn.model;

import java.sql.Date;

public class MainTest {

	public static void main(String[] args) {
		Category cat1 = new Category("Concerto", "Evento musicale pubblico con esibizione di un cantante, di una band o di un gruppo musicale");
		Category cat2 = new Category("Partita di calcetto", "Evento sportivo amatoriale");

		Field campoData = new Field("Data", "La data dell'evento", true, Date.class);
		cat1.addField(campoData);
		cat2.addField(campoData);
		
		Field campoOra = new Field("Orario", "L'orario dell'evento", true, Date.class);
		cat1.addField(campoOra);
		cat2.addField(campoOra);
		
		Field campoCantante = new Field("Star", "Il gruppo musicale che suona al concerto", true, String.class);
		cat1.addField(campoCantante);

		Field campoNumeroPartecipanti = new Field("Numero di giocatori", "Quanti giocatori sono richiesti per l'evento", true, Integer.class);
		cat2.addField(campoNumeroPartecipanti);

		System.out.println(cat1.toString());
		System.out.println(cat2.toString());
	}

}
