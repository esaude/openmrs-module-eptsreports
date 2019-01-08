package org.openmrs.module.eptsreports.reporting.calculation.txnew;

import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Patients who are aged bellow maxAge on ART start date.
 */
@Component
public class StartedArtBellowXAgeCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		PatientService patientService = Context.getPatientService();
		CalculationResultMap artStartDates = calculate(Context.getRegisteredComponents(InitialArtStartDateCalculation.class)
		        .get(0), cohort, context);
		
		// TODO maxAge is mandatory, should throw an error
		Integer maxAge = (Integer) parameterValues.get("maxAge");
		
		CalculationResultMap calculationResultMap = new CalculationResultMap();
		for (Integer pid : cohort) {
			Date onDate = (Date) artStartDates.get(pid).getValue();
			boolean isBellowAge = false;
			if (onDate != null) {
				Integer age = patientService.getPatient(pid).getAge(onDate);
				isBellowAge = age != null && age < maxAge;
			}
			calculationResultMap.put(pid, new BooleanResult(isBellowAge, this));
		}
		return calculationResultMap;
	}
}
