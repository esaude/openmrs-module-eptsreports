package org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class MohMqInitiatedArtCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    Location location = (Location) context.getFromCache("location");

    CalculationResultMap map = new CalculationResultMap();
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    CalculationResultMap startARTA2Map =
        eptsCalculationService.firstObs(
            hivMetadata.getARVStartDateConcept(),
            null,
            location,
            false,
            null,
            onOrBefore,
            Arrays.asList(hivMetadata.getMasterCardEncounterType()),
            cohort,
            context);
    for (Integer patientId : cohort) {

      Obs startARTA2Obs = EptsCalculationUtils.obsResultForPatient(startARTA2Map, patientId);
      Date requiredDate = null;

      if (startARTA2Obs != null && startARTA2Obs.getValueDatetime() != null) {
        requiredDate = startARTA2Obs.getValueDatetime();
      }
      map.put(patientId, new SimpleResult(requiredDate, this));
    }

    return map;
  }
}
