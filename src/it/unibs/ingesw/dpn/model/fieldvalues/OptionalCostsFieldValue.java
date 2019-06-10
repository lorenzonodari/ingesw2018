package it.unibs.ingesw.dpn.model.fieldvalues;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

public class OptionalCostsFieldValue implements FieldValue, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1605590568155502574L;
	
	private LinkedHashMap<String, Float> costs;
	
	public OptionalCostsFieldValue() {
		
		this.costs = new LinkedHashMap<>();
		
	}
	
	/**
	 * Aggiunge una voce di spesa aggiuntiva
	 * 
	 * Precondizione: il campo non deve gia' comprendere una voce di spesa con lo stesso nome di quella che si aggiunge
	 * Precondizione: name != null
	 * 
	 * @param name Il nome della voce di spesa
	 * @param amount L'ammontare della spesa 
	 */
	public void addEntry(String name, float amount) {
		
		// Verifica delle precondizioni
		if (name == null) {
			throw new NullPointerException();
		}
		
		if (costs.containsKey(name)) {
			throw new IllegalArgumentException();
		}
		
		costs.put(name, amount);
	}
	
	/**
	 * Restituisce una view non modificabile del valore del campo
	 * 
	 * @return L'elenco delle coppie "voce di spesa" + "ammontare"
	 */
	public Map<String, Float> getValue() {
		
		return Collections.unmodifiableMap(costs);
	}
	
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		for (String name : costs.keySet()) {
			buffer.append(name);
			buffer.append(": ");
			buffer.append(String.format("%.2f €", costs.get(name)));
			buffer.append("; ");
		}
		
		return buffer.toString();
		
	}
	

}
