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

/**
 * <b>MOH MQ:</b> Patients who initiated ART during the inclusion period
 *
 * <ul>
 *   <li>A1- All patients with first drugs pick up date (earliest concept ID 23866 value_datetime)
 *       set in mastercard pharmacy form “Recepção/Levantou ARV”(Encounter Type ID 52) with Levantou
 *       ARV (concept id 23865) = Yes (concept id 1065)
 *       <ul>
 *         <li>earliest “Date of Pick up”
 *         <li>Encounter Type Ids = 52
 *         <li>The earliest “Data de Levantamento” (Concept Id 23866 value_datetime) <= endDate
 *         <li>Levantou ARV (concept id 23865) = SIm (1065)
 *       </ul>
 *       OR
 *   <li>A2-All patients who have the first historical start drugs date (earliest concept ID 1190)
 *       set in FICHA RESUMO (Encounter Type 53)
 *       <ul>
 *         <li>earliest “historical start date”
 *         <li>Encounter Type Ids = 53
 *         <li>The earliest “Historical Start Date” (Concept Id 1190)
 *         <li>And historical start date(Value_datetime) <=EndDate
 *         <li>And the earliest date from A1 and A2 (identified as Patient ART Start Date) is >=
 *             startDateRevision and <=endDateInclusion
 *       </ul>
 *       AND
 *   <li>And the earliest date from A1 and A2 (identified as Patient ART Start Date) is >=
 *       startDateRevision and <=endDateInclusion
 * </ul>
 */
@Component
public class MohMQInitiatedARTDuringTheInclusionPeriodCalculation
    extends AbstractPatientCalculation {

  private final String ON_OR_AFTER = "onOrAfter";

  private final String ON_OR_BEFORE = "onOrBefore";

  private final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap calculationResultMap = new CalculationResultMap();

    Date onOrAfter = (Date) context.getFromCache(ON_OR_AFTER);
    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);
    Location location = (Location) context.getFromCache(LOCATION);

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

      if (startARTA2Obs != null) {
        // checking if the value datetime of ART start from A2 Obs is  during the inclusion period
        if (startARTA2Obs.getValueDatetime() != null) {
          if (startARTA2Obs.getValueDatetime().compareTo(onOrAfter) >= 0
              && startARTA2Obs.getValueDatetime().compareTo(onOrBefore) <= 0) {
            calculationResultMap.put(
                patientId, new SimpleResult(startARTA2Obs.getValueDatetime(), this));
          }
        }
      }
    }

    return calculationResultMap;
  }

  boolean isObs1AndObs2inTheSameEncounter(Obs o1, Obs o2) {

    if (o1.getEncounter().getEncounterId().equals(o2.getEncounter().getEncounterId())) {

      return true;
    }
    return false;
  }

  private Date getTheEarliastDate(Date dateA, Date dateB) {
    if (dateA.compareTo(dateB) > 0) {
      return dateB;
    }
    if (dateA.compareTo(dateB) < 0) {
      return dateA;
    }
    if (dateA.compareTo(dateB) == 0) {
      return dateA;
    }
    throw new IllegalArgumentException("Unable to compare dates");
  }
}
