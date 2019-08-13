package it.unibs.ingesw.dpn.ui;

/**
 * Implementazione dell'interfaccia {@link UserInterface} comprendente
 * le implementazioni {@link TextRenderer} e {@link TextGetter} per un'interfaccia utente
 * di tipo testuale.
 * 
 * @author Michele Dusi
 *
 */
public class TextUI implements UserInterface {

	private UIRenderer renderer;
	private UIGetter getter;
	
	/**
	 * Costruisce una nuova istanza della classe {@link TextUI}
	 * creando un riferimento interno ad un nuovo oggetto {@link TextRenderer} e uno ad 
	 * un nuovo oggetto {@link TextGetter}.
	 */
	public TextUI() {
		// Creo un oggetto Renderer
		this.renderer = new TextRenderer();
		// Creo un oggetto Getter
		this.getter = new TextGetter(this.renderer);
	}

	@Override
	public UIRenderer renderer() {
		return this.renderer;
	}

	@Override
	public UIGetter getter() {
		return this.getter;
	}

}
