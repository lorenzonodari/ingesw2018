package it.unibs.ingesw.dpn.ui;

/**
 * Semplice classe di test che mima il comportamento di un'interfaccia utente
 * che implementi l'interface {@link UserInterface}.<br>
 * Fornisce un semplice renderer testuale, e un getter impostato in modo da attingere da un buffer.
 * 
 * @author Michele Dusi
 *
 */
public class TestUI implements UserInterface {
	
	private UIRenderer renderer;
	private TestGetter getter;

	public TestUI() {
		this.renderer = new TextRenderer();
		this.getter = new TestGetter(renderer);
	}

	/**
	 * Creo un oggetto TestUI con un getter esterno.<br>
	 * 
	 * Precondizione: getter NON nullo.<br>
	 * 
	 * @param getter Il getter
	 */
	public TestUI(TestGetter getter) {
		// Verifica delle precondizioni
		if (getter == null ) {
			throw new IllegalArgumentException("Impossibile istanziare un oggetto TestUI con parametri nulli");
		}
		
		this.renderer = getter.getRendererReference();
		this.getter = getter;
		
	}
	
	/**
	 * Creo un oggetto TestUI con renderer e getter esterni.<br>
	 * 
	 * Precondizione: argomenti NON nulli.<br>
	 * Precondizione: il getter deve essere stato costruito sul renderer che viene passato come parametro<br>
	 * Precondizione: Il getter deve essere un'istanza di TestGetter (e non dell'interfaccia generica UIGetter).<br>
	 * 
	 * @param renderer Il renderer
	 * @param getter Il getter
	 */
	public TestUI(UIRenderer renderer, TestGetter getter) {
		// Verifica delle precondizioni
		if (renderer == null || getter == null ) {
			throw new IllegalArgumentException("Impossibile istanziare un oggetto TestUI con parametri nulli");
		} else if (!getter.getRendererReference().equals(renderer)) {
			throw new IllegalArgumentException("Non è possibile creare un'interfaccia utente con getter e renderer disaccoppiati");
		}
		
		this.renderer = renderer;
		this.getter = getter;
		
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
