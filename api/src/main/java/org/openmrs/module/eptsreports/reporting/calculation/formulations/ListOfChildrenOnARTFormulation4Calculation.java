package org.openmrs.module.eptsreports.reporting.calculation.formulations;

import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
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

import java.util.*;

@Component
public class ListOfChildrenOnARTFormulation4Calculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    Location location = (Location) context.getFromCache("location");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");

    CalculationResultMap lastPickupFila =
        ePTSCalculationService.getEncounter(
            Collections.singletonList(hivMetadata.getARVPharmaciaEncounterType()),
            TimeQualifier.LAST,
            cohort,
            location,
            onOrBefore,
            context);

    CalculationResultMap formulation4 =
        ePTSCalculationService.getObs(
            hivMetadata.getArtDrugFormulationConcept(),
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            onOrBefore,
            context);

    for (Integer patientId : cohort) {
      ListResult listResult = (ListResult) formulation4.get(patientId);
      List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
      Encounter lastEncounterFila =
          EptsCalculationUtils.resultForPatient(lastPickupFila, patientId);
      Drug drug = null;

      if (obsList == null || obsList.size() == 0 || lastEncounterFila == null) {
        continue;
      }

      List<Obs> onLastEncounter = new ArrayList<>();

      for (Obs o : obsList) {
        if (o.getEncounter().getEncounterId() == lastEncounterFila.getEncounterId()) {
          onLastEncounter.add(o);
        }
      }

      if (onLastEncounter.size() >= 4) {
        Obs fourthFormulation = onLastEncounter.get(3);
        if (fourthFormulation.getValueDrug() != null) {
          drug = fourthFormulation.getValueDrug();
          map.put(patientId, new SimpleResult(drug.getDisplayName(), this));
        }
      }
    }
    return map;
  }
}
