package org.openmrs.module.eptsreports.reporting.calculation.formulations;

import java.util.*;
import org.openmrs.Drug;
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

@Component
public class ListOfChildrenOnARTFormulation1Calculation extends AbstractPatientCalculation {

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

    CalculationResultMap calculationResultMap =
        ePTSCalculationService.getObs(
            hivMetadata.getArtDrugFormulationConcept(),
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            null,
            context);

    for (Integer patientId : cohort) {
      ListResult listResult = (ListResult) calculationResultMap.get(patientId);
      List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
      Drug drug = null;

      if (obsList.size() > 0) {
        Obs requiredOb = obsList.get(0);
        if (requiredOb.getValueDrug() != null) {
          drug = requiredOb.getValueDrug();
          map.put(patientId, new SimpleResult(drug.getDisplayName(), this));
        }
      }
    }
    return map;
  }
}
