package org.openmrs.module.eptsreports.reporting.calculation.txnew;

import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.reporting.calculation.PatientDataCalculation;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
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
		CalculationResultMap artStartDates = calculate(Context.getRegisteredComponents(InitialArtStartDateCalculation.class)
		        .get(0), cohort, context);
		
		BirthdateDataDefinition birthdateDataDefinition = new BirthdateDataDefinition("birthdate");
		PatientDataCalculation birthdateDataCalculation = new PatientDataCalculation();
		birthdateDataCalculation.setDataDefinition(birthdateDataDefinition);
		CalculationResultMap birthdates = calculate(birthdateDataCalculation, cohort, context);
		
		// TODO minAge and maxAge are mandatory, should throw an error
		Integer minAge = (Integer) parameterValues.get("minAge");
		Integer maxAge = (Integer) parameterValues.get("maxAge");
		
		CalculationResultMap calculationResultMap = new CalculationResultMap();
		for (Integer pid : cohort) {
			Date artStartDate = (Date) artStartDates.get(pid).getValue();
			Birthdate birthdate = (Birthdate) birthdates.get(pid).getValue();
			boolean isBetweenAges = false;
			if (artStartDate != null && birthdate != null) {
				Integer age = ageInYearsAtDate(birthdate.getBirthdate(), artStartDate);
				isBetweenAges = minAge <= age && age <= maxAge;
			}
			calculationResultMap.put(pid, new BooleanResult(isBetweenAges, this));
		}
		
		return calculationResultMap;
	}
	
	// TODO move to utility
	private Integer ageInYearsAtDate(Date birthDate, Date currentDate) {
		Age age = new Age(birthDate, currentDate);
		return age.getFullYears();
	}
}
