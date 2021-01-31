package org.openmrs.module.eptsreports.reporting.calculation.rtt;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.library.dimensions.PLHIVDays;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ReturnedDateIITDateDaysCalculation extends AbstractPatientCalculation {

  private final int YEAR_DAYS = 365;
  private final String ON_OR_AFTER = "onOrAfter";
  private final String ON_OR_BEFORE = "onOrBefore";
  private final String LOCATION = "location";
  private final String PERIOD = "period";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap calculationResultMap = new CalculationResultMap();

    Date onOrAfter = (Date) context.getFromCache(ON_OR_AFTER);
    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);
    Date previousPeriod = DateUtils.addDays(onOrAfter, -1);

    Location location = (Location) context.getFromCache(LOCATION);

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

    CalculationResultMap mostRecent =
        getMostRecentDateFromPatientWhoExperincedITT(
            cohort, context, hivMetadata, commonMetadata, previousPeriod, location);

    CalculationResultMap oldestDate =
        getOldestDateForPatientWhoReturned(
            cohort, context, hivMetadata, location, onOrAfter, onOrBefore);

    PLHIVDays period = (PLHIVDays) parameterValues.get(PERIOD);

    for (Integer pId : cohort) {

      Date a = EptsCalculationUtils.resultForPatient(mostRecent, pId);
      Date b = EptsCalculationUtils.resultForPatient(oldestDate, pId);

      if (a != null && b != null) {
        int days = Days.daysIn(new Interval(a.getTime(), b.getTime())).getDays();
        if (period == PLHIVDays.LESS_THAN_365) {
          if (days < YEAR_DAYS) {
            calculationResultMap.put(pId, new SimpleResult(days, this));
          }
        } else if (period == PLHIVDays.MORE_THAN_365) {
          if (days >= YEAR_DAYS) {
            calculationResultMap.put(pId, new SimpleResult(days, this));
          }
        }
      } else if ((a == null || b == null) && period == PLHIVDays.UNKNOWN) {
        calculationResultMap.put(pId, new SimpleResult(null, this));
      }
    }

    return calculationResultMap;
  }

  /**
   *
   *
   * <h4>Denominated A</h4>
   *
   * <ul>
   *   <li>A: Select the most recent date (encounter datetime for encounter 6, 9, 18 and value
   *       datetime for 52) of the following conditions:
   *       <ul>
   *         <li>patients who experienced IIT by end of previous reporting period (startDate -1 day)
   *             following the criterias defined in the common queries:
   *       </ul>
   * </ul>
   *
   * <p>https://docs.google.com/document/d/1EtpeIn-6seD5skZJteCdANhxkKXQye9RckGV2eoYj6c/edit?pli=1#
   * ( 5. LTFU patients by reporting endDate)
   */
  private CalculationResultMap getMostRecentDateFromPatientWhoExperincedITT(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata,
      Date previousPeriod,
      Location location) {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String endDate = format.format(previousPeriod);

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();

    sqlPatientDataDefinition.addParameter(new Parameter(LOCATION, LOCATION, Location.class));

    String query1 = getQuery1FromLTFU(hivMetadata, commonMetadata, endDate);
    String query2 = getQuery2FromLTFU(hivMetadata, commonMetadata, endDate);
    String finalQuery = query1 + " UNION " + query2;

    sqlPatientDataDefinition.setSql(finalQuery);

    Map<String, Object> param = new HashMap<>();
    param.put(LOCATION, location);

    CalculationResultMap calculationResultMap =
        EptsCalculationUtils.evaluateWithReporting(
            sqlPatientDataDefinition, cohort, param, null, context);

    return calculationResultMap;
  }

  /**
   *
   *
   * <h4>Denominated B</h4>
   *
   * <ul>
   *   <li>B: Select the oldest date (encounter datetime for encounter 6, 9, 18 and value datetime
   *       for 52) of the following conditions:
   *       <ul>
   *         <li>patients who returned to the treatment during the reporting period following the
   *             criterias below:
   *             <ul>
   *               <li>At least one Ficha Clinica registered during the reporting period (Encounter
   *                   Type 6 or 9, and encounter_datetime>= startDate and <=endDate) OR
   *               <li>At least one Drugs Pick up registered in FILA during the reporting period
   *                   (Encounter Type 18, and encounter_datetime>= startDate and <=endDate) OR
   *               <li>At least one Drugs Pick up registered in MasterCard-Recepção/Levantoy ARV,
   *                   during the reporting period (Encounter Type 52, and “Levantou ARV”- concept
   *                   ID 23865”= “Yes” (concept id 1065) and “Data de Levantamento” (concept Id
   *                   23866 value_datetime>= startDate and <=endDate)
   *             </ul>
   *       </ul>
   * </ul>
   *
   * @return
   */
  private CalculationResultMap getOldestDateForPatientWhoReturned(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      HivMetadata hivMetadata,
      Location location,
      Date... dates) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_AFTER, "On Or After", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_BEFORE, "On Or Before", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(LOCATION, "Location", Location.class));

    String query = getReturnedInPeriodQuery(hivMetadata);

    sqlPatientDataDefinition.setSql(query);

    Map<String, Object> param = new HashMap<>();
    param.put(ON_OR_AFTER, dates[0]);
    param.put(ON_OR_BEFORE, dates[1]);
    param.put(LOCATION, location);

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, param, null, context);
  }

  private String getQuery1FromLTFU(
      HivMetadata hivMetadata, CommonMetadata commonMetadata, String endDate) {
    Map<String, Integer> map1 = new HashMap<>();
    map1.put(
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map1.put(
        "ARVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map1.put("returnVisitDateConcept", commonMetadata.getReturnVisitDateConcept().getConceptId());
    map1.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map1.put(
        "aRVPediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map1.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map1.put(
        "msterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map1.put("numDays", 28);

    String query1 =
        " SELECT most_recent.patient_id, Date_add(Max(most_recent.value_datetime), interval ${numDays} day) final_encounter_date "
            + "                FROM   (SELECT fila.patient_id, o.value_datetime from ( "
            + "                            SELECT pa.patient_id, "
            + "                                Max(enc.encounter_datetime)  encounter_datetime "
            + "                            FROM   patient pa "
            + "                                inner join encounter enc "
            + "                                    ON enc.patient_id =  pa.patient_id "
            + "                            WHERE  pa.voided = 0 "
            + "                                AND enc.voided = 0 "
            + "								AND enc.encounter_type = ${ARVPharmaciaEncounterType} "
            + "                                AND enc.location_id = :location "
            + "                                AND enc.encounter_datetime <= '"
            + endDate
            + "' "
            + "                            GROUP  BY pa.patient_id) fila "
            + "                        INNER JOIN encounter e on "
            + "                            e.patient_id = fila.patient_id and "
            + "                            e.encounter_datetime = fila.encounter_datetime and "
            + "                            e.encounter_type =  ${ARVPharmaciaEncounterType} and "
            + "                            e.location_id = :location and "
            + "                            e.voided = 0 and "
            + "                            e.encounter_datetime <= '"
            + endDate
            + "' "
            + "					INNER JOIN obs o on "
            + "                            o.encounter_id = e.encounter_id and "
            + "                            o.concept_id = ${returnVisitDateForArvDrugConcept} and "
            + "                            o.voided = 0 "
            + "                        UNION "
            + "                        SELECT ficha.patient_id, o.value_datetime FROM ( "
            + "                            SELECT pa.patient_id, "
            + "                                Max(enc.encounter_datetime) encounter_datetime "
            + "                            FROM   patient pa "
            + "                                inner join encounter enc "
            + "                                    ON enc.patient_id = pa.patient_id "
            + "								WHERE  pa.voided = 0 "
            + "                                AND enc.voided = 0 "
            + "                                AND enc.encounter_type IN ( ${adultoSeguimentoEncounterType},${aRVPediatriaSeguimentoEncounterType} ) "
            + "                                AND enc.location_id = :location "
            + "                                AND enc.encounter_datetime <= '"
            + endDate
            + "' "
            + "							GROUP  BY pa.patient_id) ficha "
            + "                        INNER JOIN encounter e on "
            + "                            e.patient_id = ficha.patient_id and "
            + "                            e.encounter_datetime = ficha.encounter_datetime and "
            + "                            e.encounter_type IN (${adultoSeguimentoEncounterType},${aRVPediatriaSeguimentoEncounterType}) and "
            + "                            e.location_id = :location and "
            + "                            e.voided = 0 "
            + "                        INNER JOIN obs o on "
            + "                            o.encounter_id = e.encounter_id and "
            + "                            o.concept_id = ${returnVisitDateConcept} and "
            + "                            o.voided = 0 "
            + "                        UNION "
            + "                        SELECT pa.patient_id, "
            + "                            Date_add(Max(obs.value_datetime), interval 30 day) value_datetime "
            + "                        FROM   patient pa "
            + "                            inner join encounter enc "
            + "                                ON enc.patient_id = pa.patient_id "
            + "                            inner join obs obs "
            + "                                ON obs.encounter_id = enc.encounter_id "
            + "                        WHERE  pa.voided = 0 "
            + "                            AND enc.voided = 0 "
            + "                            AND obs.voided = 0 "
            + "                            AND obs.concept_id = ${artDatePickup} "
            + "                            AND obs.value_datetime IS NOT NULL "
            + "                            AND enc.encounter_type = ${msterCardDrugPickupEncounterType}  "
            + "                            AND enc.location_id = :location "
            + "                            AND obs.value_datetime <= '"
            + endDate
            + "' "
            + "                       GROUP  BY pa.patient_id "
            + "                   ) most_recent "
            + "               GROUP BY most_recent.patient_id "
            + "               HAVING final_encounter_date < '"
            + endDate
            + "' ";

    StringSubstitutor stringSubstitutor1 = new StringSubstitutor(map1);
    return stringSubstitutor1.replace(query1);
  }

  private String getQuery2FromLTFU(
      HivMetadata hivMetadata, CommonMetadata commonMetadata, String endDate) {

    Map<String, Integer> map2 = new HashMap<>();
    map2.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map2.put(
        "ARVPediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map2.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map2.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map2.put("returnVisitDateConcept", commonMetadata.getReturnVisitDateConcept().getConceptId());
    map2.put(
        "returnVisitDateForArvDrugConcept",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map2.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query2 =
        " SELECT pat.patient_id,  enc.encounter_datetime   "
            + " FROM   patient pat "
            + " INNER JOIN encounter enc ON enc.encounter_id = pat.patient_id "
            + " WHERE  pat.voided=0 "
            + " AND enc.voided = 0 "
            + " AND enc.location_id = :location "
            + " AND enc.encounter_datetime <= '"
            + endDate
            + "'"
            + " AND    pat.patient_id NOT IN "
            + " ( "
            + " SELECT patient_id "
            + " FROM  ( "
            + " SELECT     qa.patient_id, qa.encounter_datetime AS encounterdatetime"
            + " FROM      ( "
            + " SELECT     pat.patient_id, "
            + " Max(e.encounter_datetime) AS encounter_datetime "
            + " FROM       patient pat "
            + " INNER JOIN encounter e "
            + " ON         pat.patient_id=e.patient_id "
            + " WHERE      e.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        pat.voided=0 "
            + " AND        e.voided=0 "
            + " AND        e.location_id=:location "
            + " AND        e.encounter_type IN(${adultoSeguimentoEncounterType}) "
            + " GROUP BY   pat.patient_id)qa "
            + " INNER JOIN encounter e1 "
            + " ON         qa.patient_id=e1.patient_id "
            + " INNER JOIN obs o1 "
            + " ON         e1.encounter_id=o1.encounter_id "
            + " WHERE      qa.encounter_datetime=e1.encounter_datetime "
            + " AND        e1.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        e1.voided=0 "
            + " AND        e1.encounter_type IN(${adultoSeguimentoEncounterType}) "
            + " AND        e1.location_id=:location "
            + " AND        o1.value_datetime IS NOT NULL "
            + " AND        o1.voided=0 "
            + " AND        o1.concept_id IN(${returnVisitDateForArvDrugConcept}, "
            + " ${returnVisitDateConcept}) "
            + " AND        o1.location_id =:location "
            + "  UNION "
            + " SELECT     qb.patient_id, qb.encounter_datetime AS encounterdatetime "
            + " FROM      ( "
            + " SELECT     pat.patient_id, "
            + " max(e.encounter_datetime) AS encounter_datetime "
            + " FROM       patient pat "
            + " INNER JOIN encounter e "
            + " ON         pat.patient_id=e.patient_id "
            + " WHERE      e.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        pat.voided=0 "
            + " AND        e.voided=0 "
            + " AND        e.location_id=:location "
            + " AND        e.encounter_type IN(${ARVPediatriaSeguimentoEncounterType}) "
            + " GROUP BY   pat.patient_id)qb "
            + " INNER JOIN encounter e1 "
            + " ON         qb.patient_id=e1.patient_id "
            + " INNER JOIN obs o1 "
            + " ON         e1.encounter_id=o1.encounter_id "
            + " WHERE      qb.encounter_datetime=e1.encounter_datetime "
            + " AND        e1.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        e1.voided=0 "
            + " AND        e1.encounter_type IN(${ARVPediatriaSeguimentoEncounterType}) "
            + " AND        e1.location_id=:location "
            + " AND        o1.value_datetime IS NOT NULL "
            + " AND        o1.voided=0 "
            + " AND        o1.concept_id IN(${returnVisitDateForArvDrugConcept}, "
            + " ${returnVisitDateConcept}) "
            + " AND        o1.location_id = :location "
            + " UNION "
            + " SELECT     qc.patient_id, qc.encounter_datetime AS encounterdatetime "
            + " FROM      ( "
            + " SELECT     pat.patient_id, "
            + " max(e.encounter_datetime) AS encounter_datetime "
            + " FROM       patient pat "
            + " INNER JOIN encounter e "
            + " ON         pat.patient_id=e.patient_id "
            + " WHERE      e.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        pat.voided=0 "
            + " AND        e.voided=0 "
            + " AND        e.location_id=:location "
            + " AND        e.encounter_type IN(${aRVPharmaciaEncounterType}) "
            + " GROUP BY   pat.patient_id)qc "
            + " INNER JOIN encounter e1 "
            + " ON         qc.patient_id=e1.patient_id "
            + " INNER JOIN obs o1 "
            + " ON         e1.encounter_id=o1.encounter_id "
            + " WHERE      qc.encounter_datetime=e1.encounter_datetime "
            + " AND        e1.encounter_datetime<= '"
            + endDate
            + "' "
            + " AND        e1.voided=0 "
            + " AND        e1.encounter_type IN(${aRVPharmaciaEncounterType}) "
            + " AND        e1.location_id=:location "
            + " AND        o1.value_datetime IS NOT NULL "
            + " AND        o1.voided=0 "
            + " AND        o1.concept_id IN(${returnVisitDateForArvDrugConcept}, "
            + " ${returnVisitDateConcept}) "
            + " AND        o1.location_id = :location "
            + " UNION "
            + " SELECT     pa.patient_id,ob.value_datetime AS encounterdatetime "
            + " FROM       patient pa "
            + " INNER JOIN encounter en "
            + " ON         pa.patient_id=en.patient_id "
            + " INNER JOIN obs ob "
            + " ON         en.encounter_id=ob.encounter_id "
            + " WHERE      pa.voided=0 "
            + " AND        en.voided=0 "
            + " AND        ob.voided=0 "
            + " AND        en.location_id=:location "
            + " AND        ob.location_id=:location "
            + " AND        en.encounter_type IN(${masterCardDrugPickupEncounterType}) "
            + " AND        ob.concept_id     IN(${artDatePickup}) "
            + " AND        ob.value_datetime IS NOT NULL "
            + " AND        ob.value_datetime<= '"
            + endDate
            + "' ) fn)  "
            + "GROUP BY pat.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map2);
    return stringSubstitutor.replace(query2);
  }

  private String getReturnedInPeriodQuery(HivMetadata hivMetadata) {
    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "ARVPediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artPickupConcept", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("yesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        " SELECT outter.patient_id, MIN(outter.encounterdatetime) AS  encounterdatetime"
            + " FROM ( "
            + " SELECT p.patient_id, MIN(encounter_datetime) AS encounterdatetime "
            + " FROM patient p  "
            + "    INNER JOIN encounter e   "
            + "        ON e.patient_id = p.patient_id      "
            + " WHERE   "
            + "    p.voided = 0   "
            + "    AND   e.voided = 0   "
            + "    AND e.encounter_type IN (${adultoSeguimentoEncounterType},${ARVPediatriaSeguimentoEncounterType})  "
            + "    AND e.encounter_datetime   "
            + "        BETWEEN :onOrAfter AND :onOrBefore  "
            + "    AND e.location_id = :location  "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MIN(encounter_datetime) AS encounterdatetime "
            + " FROM patient p  "
            + "    INNER JOIN encounter e   "
            + "        ON e.patient_id = p.patient_id      "
            + " WHERE   "
            + "    p.voided = 0   "
            + "    AND   e.voided = 0   "
            + "    AND e.encounter_type = ${aRVPharmaciaEncounterType}  "
            + "    AND e.encounter_datetime   "
            + "        BETWEEN :onOrAfter AND :onOrBefore  "
            + "    AND e.location_id = :location  "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, MIN(o2.value_datetime) "
            + " FROM patient p  "
            + "    INNER JOIN encounter e   "
            + "        ON e.patient_id = p.patient_id  "
            + "    INNER JOIN obs o1 "
            + "        ON e.encounter_id = o1.encounter_id  "
            + "    INNER JOIN obs o2  "
            + "        ON e.encounter_id = o2.encounter_id  "
            + " WHERE  p.voided = 0   "
            + "    AND e.voided = 0   "
            + "    AND e.encounter_type = ${masterCardDrugPickupEncounterType}  "
            + "    AND o1.voided= 0  "
            + "    AND o2.voided= 0  "
            + "    AND (o1.concept_id = ${artPickupConcept} AND o1.value_coded = ${yesConcept})  "
            + "    AND (o2.concept_id = ${artDatePickup} AND o2.value_datetime BETWEEN :onOrAfter AND :onOrBefore )  "
            + "    AND e.location_id = :location  "
            + " GROUP BY p.patient_id) outter "
            + " GROUP BY outter.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    return stringSubstitutor.replace(query);
  }
}
