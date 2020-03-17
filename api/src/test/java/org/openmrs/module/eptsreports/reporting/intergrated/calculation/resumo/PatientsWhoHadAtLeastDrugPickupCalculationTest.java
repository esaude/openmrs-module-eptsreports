package org.openmrs.module.eptsreports.reporting.intergrated.calculation.resumo;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.resumo.PatientsWhoHadAtLeastDrugPickupCalculation;
import org.openmrs.module.eptsreports.reporting.intergrated.calculation.BasePatientCalculationTest;

public class PatientsWhoHadAtLeastDrugPickupCalculationTest extends BasePatientCalculationTest{

	@Override
	public PatientCalculation getCalculation() {
		
		return Context.getRegisteredComponents(
				PatientsWhoHadAtLeastDrugPickupCalculation.class).get(0);
		  
	}

	@Override
	public Collection<Integer> getCohort() {
		return Arrays.asList(new Integer[] {1001, 1002});
	}

	@Override
	public CalculationResultMap getResult() {
		PatientCalculation calculation = getCalculation();
		CalculationResultMap map = new CalculationResultMap();
		
		//map.put(key, value)
		    
		return map;
	}
	
	 @Before
	  public void setup() throws Exception {
	    executeDataSet("ResumoMensalTest.xml");
	  }

}
