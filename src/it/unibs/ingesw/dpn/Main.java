package it.unibs.ingesw.dpn;

import it.unibs.ingesw.dpn.ui.UIManager;
import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.ConsoleInputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;
import it.unibs.ingesw.dpn.ui.TextRenderer;
import it.unibs.ingesw.dpn.model.ModelManager;

public class Main {

	public static void main(String[] args) {
		
		UIRenderer renderer = new TextRenderer();
		InputGetter input = new ConsoleInputGetter();
		ModelManager model = new ModelManager();
		UIManager manager = new UIManager(renderer, input, model);
		manager.uiLoop();

	}

}
