package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.MohMQInitiatedARTDuringTheInclusionPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class ArtDateMinusDiagnosisDateCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CalculationResultMap map = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");

    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(
                    MohMQInitiatedARTDuringTheInclusionPeriodCalculation.class)
                .get(0),
            cohort,
            context);
    CalculationResultMap diagnosisDateMap =
        eptsCalculationService.getObs(
            hivMetadata.getTypeTestHIVConcept(),
            Arrays.asList(hivMetadata.getMasterCardDrugPickupEncounterType()),
            cohort,
            Arrays.asList(location),
            Arrays.asList(hivMetadata.getHivPCRQualitativeConceptUuid()),
            TimeQualifier.LAST,
            null,
            null,
            context);
    for (Integer patientId : cohort) {
      boolean pass = false;
      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      Obs obs = EptsCalculationUtils.obsResultForPatient(diagnosisDateMap, patientId);
      if (artStartDate != null
          && obs != null
          && obs.getObsDatetime() != null
          && EptsCalculationUtils.daysSince(artStartDate, obs.getObsDatetime()) >= 0
          && EptsCalculationUtils.daysSince(artStartDate, obs.getObsDatetime()) <= 15) {
        pass = true;
      }
      map.put(patientId, new BooleanResult(pass, this));
    }
    return map;
  }
}
