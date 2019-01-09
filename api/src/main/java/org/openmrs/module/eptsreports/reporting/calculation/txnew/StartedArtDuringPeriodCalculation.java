package org.openmrs.module.eptsreports.reporting.calculation.txnew;

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
 * Patients who started ART during the period of onOrAfter and onOrBefore dates.
 */
@Component
public class StartedArtDuringPeriodCalculation extends AbstractPatientCalculation {
	
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> parameterValues,
	        PatientCalculationContext context) {
		CalculationResultMap artStartDates = calculate(Context.getRegisteredComponents(InitialArtStartDateCalculation.class)
		        .get(0), cohort, context);
		
		// TODO onOrAfter and onOrBefore are mandatory, should throw an error
		Date onOrAfter = (Date) context.getFromCache("startDate");
		Date onOrBefore = context.getNow();
		
		CalculationResultMap calculationResultMap = new CalculationResultMap();
		for (Integer pid : cohort) {
			Date startDate = (Date) artStartDates.get(pid).getValue();
			boolean isBetweenDates = false;
			if (startDate != null) {
				isBetweenDates = onOrAfter.compareTo(startDate) <= 0 && onOrBefore.compareTo(startDate) >= 0;
			}
			calculationResultMap.put(pid, new BooleanResult(isBetweenDates, this));
		}
		return calculationResultMap;
	}
}
