package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.*;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class PoorAdherenceInLastXClinicalCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    Location location = (Location) context.getFromCache("location");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    context.setNow(onOrBefore);

    List<Encounter> resultantList = new ArrayList<>();
    List<Encounter> requiredThreeList = new ArrayList<>();
    List<Encounter> finalListWithNoOption = new ArrayList<>();

    EncounterType adultFollowup = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType childFollowup = hivMetadata.getARVPediatriaSeguimentoEncounterType();

    CalculationResultMap allEncountersMap =
        ePTSCalculationService.allEncounters(
            Arrays.asList(adultFollowup, childFollowup), cohort, location, context);

    for (Integer pId : cohort) {
      boolean hasNoOption = false;
      ListResult patientEncounterList = (ListResult) allEncountersMap.get(pId);
      List<Encounter> encounterList =
          EptsCalculationUtils.extractResultValues(patientEncounterList);

      if (encounterList.size() > 0) {
        for (Encounter encounter : encounterList) {
          Set<Obs> obsSet = encounter.getAllObs();
          for (Obs obs : obsSet) {
            if (obs.getConcept().equals(hivMetadata.getAdherence())) {
              resultantList.add(encounter);
              break;
            }
          }
        }
      }
      if (resultantList.size() >= 3) {
        if (resultantList.size() == 3) {
          requiredThreeList.addAll(resultantList);
        } else {
          requiredThreeList.add(resultantList.get(resultantList.size() - 1));
          requiredThreeList.add(resultantList.get(resultantList.size() - 2));
          requiredThreeList.add(resultantList.get(resultantList.size() - 3));
        }
      }

      for (Encounter encounter : requiredThreeList) {
        Set<Obs> requiredObs = encounter.getAllObs();

        for (Obs obs : requiredObs) {
          if (obs.getConcept().equals(hivMetadata.getAdherence())
              && obs.getValueCoded().equals(hivMetadata.getNoConcept())) {
            finalListWithNoOption.add(encounter);
            break;
          }
        }
      }
      if (finalListWithNoOption.size() == 3) {
        hasNoOption = true;
      }
      map.put(pId, new BooleanResult(hasNoOption, this));
    }

    return map;
  }
}
