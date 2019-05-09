package it.unibs.ingesw.dpn.model.events;

import java.util.Date;

import it.unibs.ingesw.dpn.model.categories.Category;
import it.unibs.ingesw.dpn.model.categories.CategoryEnum;
import it.unibs.ingesw.dpn.model.categories.CategoryProvider;

public class MainEventTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Object [] valori = new Object[4];
		valori[0] = new String("Evento di prova");
		valori[1] = new Integer(3);
		valori[2] = new Date(2019, 5, 9, 00, 10);
		valori[3] = new Date(2019, 5, 9, 00, 20);
		
		Category partita = CategoryProvider.getProvider().getCategory(CategoryEnum.PARTITA_DI_CALCIO);
		// QUESTO TEST E' DA FINIRE
		// TODO
		
		// TODO
		
		// TODO

	}

}
