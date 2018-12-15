package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Calculates whether patients are (alive and) in the HIV program
 */
public class InHivProgramEnrollmentCalculation extends AbstractPatientCalculation {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params,
	        PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		ProgramWorkflowService service = Context.getProgramWorkflowService();
		Program hivProgram = service.getProgramByUuid("efe2481f-9e75-4515-8d5a-86bfde2b5ad3");
		
		CalculationResultMap programMap = EptsCalculations.firstProgramEnrollment(hivProgram, cohort, context);
		
		for (Integer pId : cohort) {
			Date enrollmentDate = null;
			PatientProgram patientProgram = EptsCalculationUtils.resultForPatient(programMap, pId);
			
			if (patientProgram != null) {
				enrollmentDate = patientProgram.getDateEnrolled();
			}
			map.put(pId, new SimpleResult(enrollmentDate, this));
		}
		return map;
	}
	
}
