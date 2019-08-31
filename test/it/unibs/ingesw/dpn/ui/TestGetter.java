package it.unibs.ingesw.dpn.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import it.unibs.ingesw.dpn.ui.actions.ConfirmAction;
import it.unibs.ingesw.dpn.ui.actions.DialogAction;
import it.unibs.ingesw.dpn.ui.actions.MenuAction;
import it.unibs.ingesw.dpn.ui.actions.MenuEntry;

/**
 * Classe che implementa l'interfaccia {@link UIGetter} per semplificare il lavoro 
 * di testing riguardante l'aspetto dell'interfaccia utente.<br>
 * E' necessario che l'utilizzatore di questi metodi sappia esattamente come si comporteranno, poiché
 * non esitano a lanciare eccezioni in caso l'argomento non rispetti le condizioni che ci si aspetta.
 * 
 * @author Michele Dusi
 *
 */
public class TestGetter implements UIGetter {
	
	private static final float FLOAT_DELTA = 0.000001f;
	
	private UIRenderer renderer;
	private Queue<String> buffer;
	
	/**
	 * Costruttore.<br>
	 * Richiede un riferimento ad un renderer per visualizzare eventuali messaggi di errore.
	 * 
	 * @param renderer L'oggetto {@link UIRenderer} da utilizzare
	 */
	public TestGetter(UIRenderer renderer) {
		// Verifica delle precondizioni
		if (renderer == null) {
			throw new IllegalArgumentException("Impossibile istanziare la classe TestGetter senza un riferimento valido ad un oggetto UIRenderer");
		}
		
		this.renderer = renderer;
		this.buffer = new LinkedList<>();
	}
	
	/**
	 * Restituisce il riferimento al renderer utilizzato.
	 * 
	 * @return Il renderer utilizzato
	 */
	UIRenderer getRendererReference() {
		return this.renderer;
	}
	
	/**
	 * Fornisce un certo numero di argomenti al buffer interno di questo TestGetter.<br>
	 * Per ogni richiesta successiva che il Getter subirà, esso prenderà gli argomenti da 
	 * questo buffer.
	 * 
	 * @param arguments Gli argomenti da inserire in coda al buffer
	 */
	public void feedArguments(String ... arguments) {
		// Verifico che esista almeno un elemento
		if (arguments.length == 0) {
			throw new IllegalArgumentException("Il metodo richiede almeno un argomento come parametro");
		}
		// Aggiungo gli elementi in coda al buffer
		for (String arg : arguments) {
			if (arg.contains("\n")) {
				this.renderer.renderError(String.format(
						"Non è stato possibile aggiungere l'argomento %s al buffer poiché contiene caratteri speciali",
						arg));
			} else {
				this.buffer.add(arg);
			}
		}
	}
	
	private String pollArgument() {
		// Verifico che il buffer sia ancora pieno
		if (this.buffer.isEmpty()) {
			throw new IllegalStateException("Buffer vuoto: non esistono argomenti da acquisire");
		}
		// Avviso che è stata effettuata un'estrazione
		this.renderer.renderTextInFrame(String.format(
				"Estrazione dal buffer del valore: %s",
				this.buffer.peek()));
		// Restituisco il valore dell'estrazione
		return this.buffer.poll();
	}

	@Override
	public int getInteger(int min, int max) {
		Integer value = Integer.parseInt(this.pollArgument());
		if (value < min || value > max) {
			throw new IllegalArgumentException("L'argomento non soddisfa i vincoli di upperbound e lowerbound");
		}
		return value;
	}

	@Override
	public float getFloat(float min, float max) {
		Float value = Float.parseFloat(this.pollArgument());
		if (value < min - FLOAT_DELTA || value > max + FLOAT_DELTA) {
			throw new IllegalArgumentException("L'argomento non soddisfa i vincoli di upperbound e lowerbound");
		}
		return value;
	}

	@Override
	public String getString() {
		return this.pollArgument();
	}

	@Override
	public String getMatchingString(String regex) {
		String value = this.pollArgument();
		if (value.matches(regex)) {
			return value;
		} else {
			throw new IllegalArgumentException(String.format(
					"La stringa \"%s\" non corrisponde al formato atteso: %s",
					value,
					regex));
		}
	}

	@Override
	public boolean getBoolean() {
		return Boolean.parseBoolean(this.pollArgument());
	}

	@Override
	public MenuEntry getMenuChoice(MenuAction menu) {
		List<MenuEntry> entries = menu.getEntries();
		int choice = this.getInteger(0, entries.size());
		
		if (choice == 0) {
			return menu.getBackEntry();
		} else {
			return entries.get(choice - 1);
		}
	}

	/**
	 * <strong>Nota di debug:</strong> La scelta viene effettuata leggendo come argomento uno fra due possibili valori:
	 * <ul>
	 * 	<li> "1" =  true | conferma | sì </li>
	 * 	<li> "0" = false |  annulla | no </li>
	 * </ul>
	 */
	@Override
	public boolean getConfirmChoice(ConfirmAction confirm) {
		return (this.getInteger(0, 1) == 1);
	}

	/**
	 * <strong>Nota di debug:</strong> Questo metodo non effettua alcuna operazione,
	 * poiché nella realtà si limiterebbe a rappresentare una finestra di dialogo attendendo che l'utente
	 * prema "invio".<br>
	 * Per questo motivo, questo metodo NON consuma argomenti del buffer.
	 */
	@Override
	public void getDialogInteraction(DialogAction dialogAction) {
		// DO NOTHING
	}

}
