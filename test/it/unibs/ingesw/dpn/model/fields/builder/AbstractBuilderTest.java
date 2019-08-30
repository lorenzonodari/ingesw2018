package it.unibs.ingesw.dpn.model.fields.builder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import it.unibs.ingesw.dpn.model.fields.Field;
import it.unibs.ingesw.dpn.model.fields.Fieldable;
import it.unibs.ingesw.dpn.model.fields.UserField;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;
import it.unibs.ingesw.dpn.ui.FieldValueUIAcquirer;

public class AbstractBuilderTest {
	
	private static class TestBuilder extends AbstractBuilder {
		
		static final FieldValueUIAcquirer ACQUIRER = mock(FieldValueUIAcquirer.class);
		
		TestBuilder() {
			super(ACQUIRER);
		}
		
	}
	
	@After
	public void resetAcuirerMock() {
		
		reset(TestBuilder.ACQUIRER);
	}
	
	@Test
	public void setStateTest_whenNullState() {
		
		AbstractBuilder builder = new TestBuilder();
		
		try {
			builder.setState(null);
		}
		catch (IllegalArgumentException ex) {
			
			assertEquals(ex.getMessage(), "Impossibile transizionare in uno stato nullo");
			return;
		}
		
		fail("Questo test dovrebbe lanciare un'eccezione");
		
	}
	
	@Test
	public void setStateTest_whenValidState() throws Exception {
		
		// Mock configurations
		BuilderState state = mock(BuilderState.class);
		
		// Test code
		AbstractBuilder builder = new TestBuilder();
		builder.setState(state);
		java.lang.reflect.Field stateField = AbstractBuilder.class.getDeclaredField("state");
		stateField.setAccessible(true);
		
		assertEquals(stateField.get(builder), state);
	}

	@Test
	public void startCreation_ofNull() {
		
		// Mock configuration
		BuilderState state = mock(BuilderState.class);
		
		// Test code
		AbstractBuilder builder = new TestBuilder();
		builder.setState(state);
		
		try {
			builder.startCreation(null);
		}
		catch (IllegalArgumentException ex) {
			
			assertEquals(ex.getMessage(), "Impossibile proseguire con la costruzione di un oggetto null");
			return;
			
		}
		
		fail("Questo test dovrebbe lanciare un'eccezione");
		
	}
	
	@Test
	public void startEditing_ofNull() {
		
		// Mock configuration
		BuilderState state = mock(BuilderState.class);
		
		// Test code
		AbstractBuilder builder = new TestBuilder();
		builder.setState(state);
		
		try {
			builder.startEditing(null);
		}
		catch (IllegalArgumentException ex) {
			
			assertEquals(ex.getMessage(), "Impossibile modificare un utente nullo");
			return;
			
		}
		
		fail("Questo test dovrebbe lanciare un'eccezione");
		
	}
	
	@Test
	public void startEditingBackupTest() throws Exception {
		
		// Mock configuration
		BuilderState state = mock(BuilderState.class);
		Fieldable editedObject = mock(Fieldable.class);
		Map<Field, FieldValue> values = new HashMap<>();
		values.put(UserField.NICKNAME, new StringFieldValue("Test nickname"));
		values.put(UserField.CATEGORIE_DI_INTERESSE, null);
		when(editedObject.getAllFieldValues()).thenReturn(values);
		
		// Test code
		AbstractBuilder builder = new TestBuilder();
		builder.setState(state);
		builder.startEditing(editedObject);
		
		java.lang.reflect.Field backupValuesField = AbstractBuilder.class.getDeclaredField("backupValuesMap");
		backupValuesField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<Field, FieldValue> backupValues = (Map<Field, FieldValue>) backupValuesField.get(builder);
		
		assertEquals(backupValues, values);
		
	}
	
	@Test
	public void cancelWhenEditingTest() throws Exception {
		
		// Mock configuration
		BuilderState state = mock(BuilderState.class);
		Fieldable targetObject = mock(Fieldable.class);
		
		// Test code
		AbstractBuilder builder = new TestBuilder();
		builder.setState(state);
		
		// Creo la mappa di backup
		Map<Field, FieldValue> values = new HashMap<>();
		StringFieldValue nickname = new StringFieldValue("Test nickname");
		values.put(UserField.NICKNAME, nickname);
		values.put(UserField.CATEGORIE_DI_INTERESSE, null);
		
		// Ottengo tramite reflection i campi privati che mi interessano
		java.lang.reflect.Field backupValuesField = AbstractBuilder.class.getDeclaredField("backupValuesMap");
		backupValuesField.setAccessible(true);
		java.lang.reflect.Field fieldableField = AbstractBuilder.class.getDeclaredField("provisionalFieldable");
		fieldableField.setAccessible(true);
		
		// Imposto i riferimenti correttamente e invoco cancel()
		backupValuesField.set(builder, values);
		fieldableField.set(builder, targetObject);
		builder.cancel();
		
		// Verifico che il ripristino sia avvenuto correttamente
		verify(targetObject, times(1)).setFieldValue(UserField.NICKNAME, nickname);
		verify(targetObject, times(1)).setFieldValue(UserField.CATEGORIE_DI_INTERESSE, null);
		verifyNoMoreInteractions(targetObject);
		
		// Ottengo i valori dei campi di interesse
		@SuppressWarnings("unchecked")
		Map<Field, FieldValue> backupMap = (Map<Field, FieldValue>) backupValuesField.get(builder);
		Fieldable provisionalFieldable = (Fieldable) fieldableField.get(builder);
		
		// Verifico che siano entrambi nulli dopo l'esecuzione di cancel()
		assertNull(backupMap);
		assertNull(provisionalFieldable);
		
	}
	
}
