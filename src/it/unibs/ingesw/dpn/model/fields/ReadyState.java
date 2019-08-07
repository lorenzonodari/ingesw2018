package it.unibs.ingesw.dpn.model.fields;

public class ReadyState implements BuilderState {

	@Override
	public String getStateName() {
		return BuilderState.READY;
	}
	
	/**
	 * Inizia il processo di creazione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	@Override
	public void onStartingCreating(AbstractBuilder b) {
		b.setState(new CreatingState());
	}
	
	/**
	 * Metodo di default.
	 * Nel caso si tenti di chiamarlo per stati in cui non è permesso, viene lanciata un'eccezione.
	 * 
	 * @param b Il Builder a cui si fa riferimento
	 */
	@Override
	public void onStartingEditing(AbstractBuilder b) {
		b.setState(new EditingState());
	}

}
