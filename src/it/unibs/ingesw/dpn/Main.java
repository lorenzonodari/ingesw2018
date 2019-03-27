package it.unibs.ingesw.dpn;

import it.unibs.ingesw.dpn.ui.UIManager;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.ConsoleInputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;
import it.unibs.ingesw.dpn.ui.TextRenderer;
import it.unibs.ingesw.dpn.model.ModelManager;
import it.unibs.ingesw.dpn.model.CategoryProvider;

public class Main {

	public static void main(String[] args) {
		
		UIRenderer renderer = new TextRenderer();
		InputGetter input = new ConsoleInputGetter();
		ModelManager model = new ModelManager(CategoryProvider.getProvider());
		UIManager manager = new UIManager(renderer, input, model);
		manager.uiLoop();

	}

}
