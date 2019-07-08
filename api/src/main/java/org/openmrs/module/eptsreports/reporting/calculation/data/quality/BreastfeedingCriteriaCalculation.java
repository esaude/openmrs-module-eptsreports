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
import org.openmrs.PatientState;
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
public class BreastfeedingCriteriaCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Location location = (Location) context.getFromCache("location");

    Concept breastfeedingConcept = hivMetadata.getBreastfeeding();
    Concept yes = hivMetadata.getYesConcept();
    Concept criteriaForHivStart = hivMetadata.getCriteriaForArtStart();
    Concept priorDeliveryDate = hivMetadata.getPriorDeliveryDateConcept();

    CalculationResultMap lactatingMap =
        ePTSCalculationService.getObs(
            breastfeedingConcept,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(yes),
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap criteriaHivStartMap =
        ePTSCalculationService.getObs(
            criteriaForHivStart,
            null,
            cohort,
            Arrays.asList(location),
            Arrays.asList(breastfeedingConcept),
            TimeQualifier.FIRST,
            null,
            context);

    CalculationResultMap deliveryDateMap =
        ePTSCalculationService.getObs(
            priorDeliveryDate,
            null,
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    CalculationResultMap patientStateMap =
        ePTSCalculationService.allPatientStates(
            cohort, location, hivMetadata.getPatientIsBreastfeedingWorkflowState(), context);

    for (Integer ptId : cohort) {
      Map<Date, String> values = new HashMap<Date, String>();

      // have the list results
      ListResult lactatingResult = (ListResult) lactatingMap.get(ptId);
      ListResult artStartResult = (ListResult) criteriaHivStartMap.get(ptId);
      ListResult deliveryDateResult = (ListResult) deliveryDateMap.get(ptId);
      ListResult patientStatesResult = (ListResult) patientStateMap.get(ptId);

      // have the respective list items of the ListResults above
      List<Obs> lactatingObsList = EptsCalculationUtils.extractResultValues(lactatingResult);
      List<Obs> artStartObsList = EptsCalculationUtils.extractResultValues(artStartResult);
      List<Obs> deliveryDateObsList = EptsCalculationUtils.extractResultValues(deliveryDateResult);
      List<PatientState> patientStateList =
          EptsCalculationUtils.extractResultValues(patientStatesResult);
      // start looping through the list of obs
      for (Obs obs : lactatingObsList) {
        if (obs.getObsDatetime() != null) {
          values.put(obs.getObsDatetime(), "BC1");
          break;
        }
      }
      // loop through the pregnantByWeeksObsList
      for (Obs obs : artStartObsList) {
        if (obs.getObsDatetime() != null) {
          values.put(obs.getObsDatetime(), "BC2");
          break;
        }
      }
      // loop through the pregnantDueDateObsList
      for (Obs obs : deliveryDateObsList) {
        if (obs.getValueDatetime() != null) {
          values.put(obs.getValueDatetime(), "BC3");
          break;
        }
      }

      // loop through the patientPrograms states
      for (PatientState patientState : patientStateList) {
        if (patientState.getStartDate() != null) {
          values.put(patientState.getStartDate(), "BC4");
          break;
        }
      }
      Map<Date, String> sortedMap = new TreeMap<Date, String>(values);
      map.put(ptId, new SimpleResult(sortedMap, this));
    }

    return map;
  }
}
