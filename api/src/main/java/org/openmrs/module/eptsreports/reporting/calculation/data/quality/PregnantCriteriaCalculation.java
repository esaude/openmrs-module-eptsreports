package org.openmrs.module.eptsreports.reporting.calculation.data.quality;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.PatientProgram;
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
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class PregnantCriteriaCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Concept pregnant = hivMetadata.getPregnantConcept();
    Concept pregnantBasedOnWeeks = hivMetadata.getNumberOfWeeksPregnant();
    Concept pregnancyDueDate = hivMetadata.getPregnancyDueDate();
    Program ptv = hivMetadata.getPtvEtvProgram();
    Concept gestation = hivMetadata.getGestationConcept();

    Location location = (Location) context.getFromCache("location");

    CalculationResultMap pregnantMap =
        ePTSCalculationService.getObs(
            pregnant,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(gestation),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantByWeeksMap =
        ePTSCalculationService.getObs(
            pregnantBasedOnWeeks,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantDueDateMap =
        ePTSCalculationService.getObs(
            pregnancyDueDate,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap markedPregnantInProgramMap =
        ePTSCalculationService.allProgramEnrollment(ptv, cohort, context);

    for (Integer ptId : cohort) {
      Map<Date, String> values = new HashMap<Date, String>();

      // have the list results
      ListResult pregnantResult = (ListResult) pregnantMap.get(ptId);
      ListResult pregnantByWeeksResullt = (ListResult) markedPregnantByWeeksMap.get(ptId);
      ListResult pregnantDueDateResult = (ListResult) markedPregnantDueDateMap.get(ptId);
      ListResult pregnantInProgramResults = (ListResult) markedPregnantInProgramMap.get(ptId);

      // have the respective list items of the ListResults above
      List<Obs> pregnantObsList = EptsCalculationUtils.extractResultValues(pregnantResult);
      List<Obs> pregnantByWeeksObsList =
          EptsCalculationUtils.extractResultValues(pregnantByWeeksResullt);
      List<Obs> pregnantDueDateObsList =
          EptsCalculationUtils.extractResultValues(pregnantDueDateResult);
      List<PatientProgram> patientPrograms =
          EptsCalculationUtils.extractResultValues(pregnantInProgramResults);

      // loop through the pregnantObsList
      for (Obs obs : pregnantObsList) {
        if (obs.getObsDatetime() != null) {
          values.put(obs.getObsDatetime(), "PC1");
          break;
        }
      }
      // loop through the pregnantByWeeksObsList
      for (Obs obs : pregnantByWeeksObsList) {
        if (obs.getObsDatetime() != null && obs.getValueNumeric() != null) {
          values.put(obs.getObsDatetime(), "PC2");
          break;
        }
      }
      // loop through the pregnantDueDateObsList
      for (Obs obs : pregnantDueDateObsList) {
        if (obs.getValueDatetime() != null) {
          values.put(obs.getValueDatetime(), "PC3");
          break;
        }
      }

      // loop through the patientPrograms
      for (PatientProgram patientProgram : patientPrograms) {
        if (patientProgram.getDateEnrolled() != null) {
          values.put(patientProgram.getDateEnrolled(), "PC4");
          break;
        }
      }
      Map<Date, String> sortedMap = new TreeMap<Date, String>(values);
      map.put(ptId, new SimpleResult(sortedMap, this));
    }

    return map;
  }
}
