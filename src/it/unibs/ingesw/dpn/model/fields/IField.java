package it.unibs.ingesw.dpn.model.fields;

import it.unibs.ingesw.dpn.ui.InputGetter;
import it.unibs.ingesw.dpn.ui.UIRenderer;

public interface IField {	
	
	public interface FieldValueAcquirer {
		public Object acquireFieldValue(UIRenderer renderer, InputGetter getter);
	};

	public String getName();
	
	public String getDescription();
	
	public boolean isMandatory();
	
	public Object acquireFieldValue(UIRenderer renderer, InputGetter getter);
	
}
