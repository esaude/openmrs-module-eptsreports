package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Calculates whether patients are (alive and) in the HIV program
 */
public class InHivProgramEnrollmentCalculation extends AbstractPatientCalculation {
	
	@Autowired
	private HivMetadata hivMetadata;
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculation#evaluate(java.util.Collection,
	 *      java.util.Map, org.openmrs.calculation.patient.PatientCalculationContext)
	 */
	@Override
	public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
		
		CalculationResultMap map = new CalculationResultMap();
		
		ProgramWorkflowService service = Context.getProgramWorkflowService();
		
		for (Integer pId : cohort) {
			PatientProgram inHivProgram = null;
			List<PatientProgram> programs = service.getPatientPrograms(Context.getPatientService().getPatient(pId),
			    hivMetadata.getARTProgram(), null, null, null, null, true);
			if (programs != null && programs.size() > 0) {
				inHivProgram = programs.get(0);
			}
			map.put(pId, new SimpleResult(inHivProgram, this));
		}
		return map;
	}
	
}
