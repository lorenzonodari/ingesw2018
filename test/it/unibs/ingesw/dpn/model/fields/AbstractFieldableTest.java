package it.unibs.ingesw.dpn.model.fields;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import it.unibs.ingesw.dpn.model.fieldvalues.DateFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.FieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.IntegerFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.MoneyAmountFieldValue;
import it.unibs.ingesw.dpn.model.fieldvalues.StringFieldValue;

public class AbstractFieldableTest {
	
	private static class TestFieldable extends AbstractFieldable {
		
		TestFieldable() {
			super(Arrays.asList(CommonField.values()));
		}
		
		@Override
		public void setDefaultFieldValues() {
			// Do nothing
		}
		
	}

	@Test
	public void hasFieldTest_whenNullArgument() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		try {
			
			fieldable.hasField(null);
			
		}
		catch (IllegalArgumentException ex) {
			
			return;
			
		}
		
		fail("Questo test dorebbe sollevare un'eccezione");
		
	}
	
	@Test
	public void hasFieldTest() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		assertTrue(fieldable.hasField(CommonField.TITOLO));
		assertFalse(fieldable.hasField(UserField.NICKNAME));

	}
	
	@Test
	public void hasFieldValueTest_whenFieldNotPresent() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		try {
			
			fieldable.hasFieldValue(UserField.NICKNAME);
			
		}
		catch (IllegalArgumentException ex) {
			
			return;
			
		}
		
		fail("Questo test dorebbe sollevare un'eccezione");

	}
	
	@Test
	public void hasFieldValueTest() throws Exception {
		
		AbstractFieldable fieldable = new TestFieldable();
		fieldable.setFieldValue(CommonField.TITOLO, new StringFieldValue("test"));
		
		assertTrue(fieldable.hasFieldValue(CommonField.TITOLO));
		assertFalse(fieldable.hasFieldValue(CommonField.DATA_E_ORA));
		
	}
	
	@Test
	public void setFieldValueTest_whenFieldNotPresent() {
		
		// Mock configuration
		FieldValue fieldValue = mock(FieldValue.class);
		
		// Test code
		AbstractFieldable fieldable = new TestFieldable();
		boolean success = fieldable.setFieldValue(UserField.NICKNAME, fieldValue);
		
		assertFalse(success);
		
	}
	
	@Test
	public void setFieldValueTest_whenTypeNotCompatible() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		try {
			
			fieldable.setFieldValue(CommonField.TITOLO, new IntegerFieldValue(0));
			
		}
		catch (IllegalArgumentException ex) {
			
			return;
			
		}
		
		fail("Questo test dorebbe sollevare un'eccezione");
	}
	
	@Test
	public void setFieldValueTest() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		assertFalse(fieldable.hasFieldValue(CommonField.TITOLO));
		
		fieldable.setFieldValue(CommonField.TITOLO, new StringFieldValue("test"));
		
		assertTrue(fieldable.hasFieldValue(CommonField.TITOLO));
		
	}
	
	@Test
	public void getFieldValue_whenFieldNotPresent() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		try {
			
			fieldable.getFieldValue(UserField.NICKNAME);
			
		}
		catch (IllegalArgumentException ex) {
			
			return;
			
		}
		
		fail("Questo test dovrebbe sollevare un'eccezione");
		
	}
	
	@Test
	public void getFieldValueTest() {
		
		AbstractFieldable fieldable = new TestFieldable();
		StringFieldValue fieldValue = new StringFieldValue("test");
		fieldable.setFieldValue(CommonField.TITOLO, fieldValue);
		
		assertEquals(fieldable.getFieldValue(CommonField.TITOLO), fieldValue);
		
	}
	
	@Test
	public void hasAllMandatoryFieldTest_whenItDoes() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		fieldable.setFieldValue(CommonField.LUOGO, new StringFieldValue());
		fieldable.setFieldValue(CommonField.DATA_E_ORA, new DateFieldValue());
		fieldable.setFieldValue(CommonField.TERMINE_ULTIMO_DI_ISCRIZIONE, new DateFieldValue());
		fieldable.setFieldValue(CommonField.NUMERO_DI_PARTECIPANTI, new IntegerFieldValue());
		fieldable.setFieldValue(CommonField.QUOTA_INDIVIDUALE, new MoneyAmountFieldValue());
		
		assertTrue(fieldable.hasAllMandatoryField());
		
	}
	
	@Test
	public void hasAllMandatoryFieldTest_whenItHasNone() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		assertFalse(fieldable.hasAllMandatoryField());
		
	}
	
	@Test
	public void hasAllMandatoryFieldTest_whenItHasSome() {
		
		AbstractFieldable fieldable = new TestFieldable();
		
		fieldable.setFieldValue(CommonField.LUOGO, new StringFieldValue());
		fieldable.setFieldValue(CommonField.NUMERO_DI_PARTECIPANTI, new IntegerFieldValue());
		
		assertFalse(fieldable.hasAllMandatoryField());
		
	}

}
