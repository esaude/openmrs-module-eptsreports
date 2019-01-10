package org.openmrs.module.eptsreports.reporting.calculation.txnew;

import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.reporting.common.Age;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Patients who are aged between minAge and maxAge on ART start date.
 */
@Component
public class StartedArtAtXtoYAgeCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		PatientService patientService = Context.getPatientService();
		CalculationResultMap artStartDates = calculate(Context.getRegisteredComponents(InitialArtStartDateCalculation.class)
		        .get(0), cohort, context);
		
		// TODO minAge and maxAge are mandatory, should throw an error
		Integer minAge = (Integer) parameterValues.get("minAge");
		Integer maxAge = (Integer) parameterValues.get("maxAge");
		
		CalculationResultMap calculationResultMap = new CalculationResultMap();
		for (Integer pid : cohort) {
			Date onDate = (Date) artStartDates.get(pid).getValue();
			boolean isBetweenAges = false;
			if (onDate != null) {
				Integer age = patientService.getPatient(pid).getAge(onDate);
				isBetweenAges = age != null && minAge <= age && age <= maxAge;
			}
			calculationResultMap.put(pid, new BooleanResult(isBetweenAges, this));
		}
		
		return calculationResultMap;
	}

	private Integer ageInYearsAtDate(Date birthDate, Date artInitiationDate) {
		Age age = new Age(birthDate, artInitiationDate);

		return age.getFullYears();
	}
}
