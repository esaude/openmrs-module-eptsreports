package org.openmrs.module.eptsreports.reporting.calculation.data.quality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class PregnantEnrollmentStatusCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    Program ptv = hivMetadata.getPtvEtvProgram();

    CalculationResultMap markedPregnantInProgramMap =
        ePTSCalculationService.allProgramEnrollment(ptv, cohort, context);
    for (Integer ptId : cohort) {
      PatientState patientState = null;

      ListResult result = (ListResult) markedPregnantInProgramMap.get(ptId);
      if (result != null) {
        List<PatientProgram> patientPrograms = EptsCalculationUtils.extractResultValues(result);
        if (patientPrograms.size() > 0) {
          // get the last enrollment
          PatientProgram patientProgram = patientPrograms.get(patientPrograms.size() - 1);
          // get the patient states by the above program enrollment
          if (patientProgram != null) {
            Set<PatientState> patientStates = patientProgram.getCurrentStates();
            if (patientStates != null && patientStates.size() > 0) {
              // convert the states into a list
              List<PatientState> patientStateList = new ArrayList<>(patientStates);
              patientState = patientStateList.get(0);
            }
          }
        }
      }
      map.put(ptId, new SimpleResult(patientState, this));
    }
    return map;
  }
}
