package it.unibs.ingesw.dpn.ui;

/**
 * Rappresenta un punto d'accesso all'interfaccia utente
 * del programma.
 * E' semplicemente un modo comodo per accorpare in un'unico riferimento
 * un'istanza di {@link UIRenderer} e una di {@link UIGetter}.
 * Quando un oggetto {@link UserInterface} viene passato come parametro ad un metodo, si sta 
 * virtualmente fornendo a tale metodo la possibilità di "prendere il controllo" dell'interfaccia
 * utente fino al termine della chiamata, in modo tale da acquisire o restituire dati.
 * 
 * @author Michele Dusi
 *
 */
public interface UserInterface {
	
	/**
	 * Restituisce un oggetto {@link UIRenderer} per le operazioni
	 * di visualizzazione dati.
	 * 
	 * @return un riferimento ad un oggetto {@link UIRenderer}.
	 */
	public UIRenderer renderer();

	/**
	 * Restituisce un oggetto {@link UIGetter} per le operazioni
	 * di acquisizione dati.
	 * 
	 * @return un riferimento ad un oggetto {@link UIGetter}.
	 */
	public UIGetter getter();
	
}
