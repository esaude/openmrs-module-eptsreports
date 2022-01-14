package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This Cohort Query makes several unions of variety queries in {@link
 * IntensiveMonitoringCohortQueries }
 */
@Component
public class ViralLoadIntensiveMonitoringCohortQueries {

  private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  private HivMetadata hivMetadata;

  private CommonCohortQueries commonCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  private final int VIRAL_LOAD_1000_COPIES = 1000;

  @Autowired
  public ViralLoadIntensiveMonitoringCohortQueries(
      IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries,
      HivMetadata hivMetadata,
      CommonCohortQueries commonCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries) {
    this.intensiveMonitoringCohortQueries = intensiveMonitoringCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonCohortQueries = commonCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
  }

  /**
   * <b>Indicator 1 Denominator:</b> <br>
   * Number of patients in the 1st line of ART who had a clinical consultation in the review period
   * (data collection) and who were eligible to a VL request” <br>
   * Select all from the Denominator of MI report categories13.1, 13.6, 13.7, 13.8 (union all
   * specified categories)
   */
  public CohortDefinition getTotalIndicator1Den() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13den1 = intensiveMonitoringCohortQueries.getCat13Den(1, false);
    CohortDefinition mi13den6 = intensiveMonitoringCohortQueries.getCat13Den(6, false);
    CohortDefinition mi13den7 = intensiveMonitoringCohortQueries.getCat13Den(7, false);
    CohortDefinition mi13den8 = intensiveMonitoringCohortQueries.getCat13Den(8, false);

    cd.addSearch(
        "1", EptsReportUtils.map(mi13den1, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "6", EptsReportUtils.map(mi13den6, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "7", EptsReportUtils.map(mi13den7, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "8", EptsReportUtils.map(mi13den8, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("1 OR 6 OR 7 OR 8");
    return cd;
  }

  /**
   * <b>Indicator 1 Numerator:</b> <br>
   * Number of patients in the 1st line of ART who had a clinical consultation during the review
   * period (data collection), were eligible to a VL request and with a record of a VL request made
   * by the clinician <br>
   * Select all from the Numerator of MI report categories13.1, 13.6, 13.7, 13.8 (union all
   * specified categories)
   */
  public CohortDefinition getTotalIndicator1Num() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13num1 = intensiveMonitoringCohortQueries.getCat13Den(1, true);
    CohortDefinition mi13num6 = intensiveMonitoringCohortQueries.getCat13Den(6, true);
    CohortDefinition mi13num7 = intensiveMonitoringCohortQueries.getCat13Den(7, true);
    CohortDefinition mi13num8 = intensiveMonitoringCohortQueries.getCat13Den(8, true);

    cd.addSearch(
        "1", EptsReportUtils.map(mi13num1, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "6", EptsReportUtils.map(mi13num6, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "7", EptsReportUtils.map(mi13num7, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "8", EptsReportUtils.map(mi13num8, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("1 OR 6 OR 7 OR 8");
    return cd;
  }

  /**
   * <b>Indicator 2 Denominator:</b> <br>
   * Number of patients who started 1st-line ART or new 1st-line regimen in the month of evaluation
   * <br>
   * Select all from the Denominator of MI report categories 13.2, 13.9, 13.10, 13.11 (union all
   * specified categories)
   */
  public CohortDefinition getTotalIndicator2Den() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13den2 = intensiveMonitoringCohortQueries.getMI13DEN2(2);
    CohortDefinition mi13den9 = intensiveMonitoringCohortQueries.getMI13DEN9(9);
    CohortDefinition mi13den10 = intensiveMonitoringCohortQueries.getMI13DEN10(10);
    CohortDefinition mi13den11 = intensiveMonitoringCohortQueries.getMI13DEN11(11);

    cd.addSearch(
        "2", EptsReportUtils.map(mi13den2, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "9", EptsReportUtils.map(mi13den9, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "10", EptsReportUtils.map(mi13den10, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "11", EptsReportUtils.map(mi13den11, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("2 OR 9 OR 10 OR 11");
    return cd;
  }

  /**
   * <b>Indicator 2 Numerator:</b> <br>
   * Number of patients in the 1st line of ART who received the VL result between the sixth and
   * ninth month after starting ART <br>
   * Select all from the Numerator of MI report categories 13.2, 13.9, 13.10, 13.11 (union all
   * specified categories
   */
  public CohortDefinition getTotalIndicator2Num() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13num2 = intensiveMonitoringCohortQueries.getMI13NUM2(2);
    CohortDefinition mi13num9 = intensiveMonitoringCohortQueries.getMI13NUM9(9);
    CohortDefinition mi13num10 = intensiveMonitoringCohortQueries.getMI13NUM10(10);
    CohortDefinition mi13num11 = intensiveMonitoringCohortQueries.getMI13NUM11(11);

    cd.addSearch(
        "2", EptsReportUtils.map(mi13num2, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "9", EptsReportUtils.map(mi13num9, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "10", EptsReportUtils.map(mi13num10, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "11", EptsReportUtils.map(mi13num11, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("2 OR 9 OR 10 OR 11");
    return cd;
  }

  /**
   * <b>Indicator 3 Denominator:</b> <br>
   * NNumber of patients in the 1st line of ART with a VL result above 1000 in the month of
   * evaluation <br>
   * Select all from the Denominator of MI report categories 13.3, 13.12 (union all specified
   * categories
   */
  public CohortDefinition getTotalIndicator3Den() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13den3 = intensiveMonitoringCohortQueries.getMICat13Part4(3, false);
    CohortDefinition mi13den12 = intensiveMonitoringCohortQueries.getMICat13Part4(12, false);

    cd.addSearch(
        "3", EptsReportUtils.map(mi13den3, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "12", EptsReportUtils.map(mi13den12, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("3 OR 12");
    return cd;
  }

  /**
   * <b>Indicator 3 Numerator:</b> <br>
   * Number of patients in the 1st line of ART with a record of VL request between the 3rd and 4th
   * month after receiving the last CV result above 1000 and having 3 consecutive sessions of
   * APSS/PP <br>
   * Select all from the Numerator of MI report categories 13.3, 13.12 (union all specified
   * categories3
   */
  public CohortDefinition getTotalIndicator3Num() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13num3 = intensiveMonitoringCohortQueries.getMICat13Part4(3, true);
    CohortDefinition mi13num12 = intensiveMonitoringCohortQueries.getMICat13Part4(12, true);

    cd.addSearch(
        "3", EptsReportUtils.map(mi13num3, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "12", EptsReportUtils.map(mi13num12, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("3 OR 12");
    return cd;
  }

  /**
   * <b>Indicator 4 Denominator:</b> <br>
   * Number of patients on second line ART eligible for VL <br>
   * Select all from the Denominator of MI report categories 13.4, 13.13 (union all specified
   * categories
   */
  public CohortDefinition getTotalIndicator4Den() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13den4 = intensiveMonitoringCohortQueries.getCat13Den(4, false);
    CohortDefinition mi13den13 = intensiveMonitoringCohortQueries.getCat13Den(13, false);

    cd.addSearch(
        "4", EptsReportUtils.map(mi13den4, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "13", EptsReportUtils.map(mi13den13, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("4 OR 13");
    return cd;
  }

  /**
   * <b>Indicator 4 Numerator:</b> <br>
   * Number of patients on second line ART eligible for VL with a VL request made by the clinician
   * <br>
   * Select all from the Numerator of MI report categories 13.4, 13.13 (union all specified
   * categories
   */
  public CohortDefinition getTotalIndicator4Num() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13num4 = intensiveMonitoringCohortQueries.getCat13Den(4, true);
    CohortDefinition mi13num13 = intensiveMonitoringCohortQueries.getCat13Den(13, true);

    cd.addSearch(
        "4", EptsReportUtils.map(mi13num4, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "13", EptsReportUtils.map(mi13num13, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("4 OR 13");
    return cd;
  }

  /**
   * <b>Indicator 5 Denominator:</b> <br>
   * Number of patients with a record of starting the 2nd line of ART in the month of evaluation
   * <br>
   * Select all from the Denominator of MI report categories 13.5, 13.14 (union all specified
   * categories
   */
  public CohortDefinition getTotalIndicator5Den() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13den5 = intensiveMonitoringCohortQueries.getMI13DEN5(5);
    CohortDefinition mi13den14 = intensiveMonitoringCohortQueries.getMI13DEN14(14);

    cd.addSearch(
        "5", EptsReportUtils.map(mi13den5, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "14", EptsReportUtils.map(mi13den14, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("5 OR 14");
    return cd;
  }

  /**
   * <b>Indicator 5 Numerator:</b> <br>
   * Number of patients with a record of starting 2nd line ART in the month of evaluation and who
   * received the VL result between the sixth and ninth month after starting 2nd line ART <br>
   * Select all from the Numerator of MI report categories 13.5, 13.14 (union all specified
   * categories
   */
  public CohortDefinition getTotalIndicator5Num() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mi13num4 = intensiveMonitoringCohortQueries.getMI13NUM5(5);
    CohortDefinition mi13num13 = intensiveMonitoringCohortQueries.getMI13NUM14(14);

    cd.addSearch(
        "5", EptsReportUtils.map(mi13num4, "revisionEndDate=${endDate},location=${location}"));
    cd.addSearch(
        "14", EptsReportUtils.map(mi13num13, "revisionEndDate=${endDate},location=${location}"));

    cd.setCompositionString("5 OR 14");
    return cd;
  }

  /**
   * <b>X</b> - Patients in the 1st line of ART with a First VL result above 1000 copies in the 7
   * months back evaluation period:
   *
   * <p>
   *
   * <ul>
   *   Select all patients with the first VL Numeric Result (concept Id 856) documented in Ficha
   *   Clinica (encounter type 6, encounter_datetime) or Ficha Resumo (encounter type 53,
   *   obs_datetime ) between the evaluation period (<=startDate) and the Result is >= 1000
   *   copias/ml (concept 856 value_numeric >= 1000) except:
   *   <li>Patients marked as pregnant (concept_id 1982) value coded equal to “Yes” (concept_id
   *       1065) or breastfeeding (concept_id 6332) value coded equal to “Yes” (concept_id 1065) on
   *       the same consultation ov VL >=1000
   * </ul>
   *
   * <br>
   * <b>Note:</b> in case there is more than one Viral Load record with a result >= 1000 copies, the
   * first record that occurred during the evaluation period must be considered
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPartOneOfXQuery() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN ( "
            + "SELECT min_vl.patient_id , MIN(min_datetime) final_min_date "
            + "FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "           AND e.location_id = :location"
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "           AND e.location_id = :location "
            + "        GROUP BY p.patient_id "
            + "     )min_vl "
            + "GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id "
            + "WHERE p.patient_id NOT IN ( "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND o.obs_datetime = final_min.final_min_date "
            + "                            UNION "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND e.encounter_datetime = final_min.final_min_date)";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));
    return sqlCohortDefinition;
  }

  /**
   * <b>Filter all patients who are in the first line of ART in the evaluation period as:</b>
   *
   * <p>“LINHA TERAPEUTICA” (Concept Id 21151) equal to “PRIMEIRA LINHA” (concept id 21150) recorded
   * in the Last Clinical Consultation (encounter type 6, encounter_datetime) occurred during the
   * evaluation period.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getFirstLineArt() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Linha Terapeutica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());

    String sql =
        " SELECT p.patient_id  "
            + " FROM patient p INNER  JOIN ("
            + " SELECT p.patient_id , MIN(encounter_datetime) AS last_date "
            + " FROM patient p "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " WHERE p.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND e.encounter_type = ${6} "
            + "  AND o.concept_id = ${21151} "
            + "  AND o.value_coded = ${21150} "
            + "  AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "  AND e.location_id = :location )  ultima ON ultima.patient_id = p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  public CohortDefinition getComposedXQuery() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "1",
        EptsReportUtils.map(
            this.getPartOneOfXQuery(),
            "startDate=${startDate-7m},endDate=${endDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "2",
        EptsReportUtils.map(
            this.getFirstLineArt(),
            "startDate=${startDate-7m},endDate=${endDate-7m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "dead",
        EptsReportUtils.map(this.getDeadPatients(), "endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "(1 AND 2) AND NOT ( transferredIn OR transferredOut OR dead)");

    return compositionCohortDefinition;
  }

  public CohortDefinition getDeadPatients() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "1",
        EptsReportUtils.map(
            this.txCurrCohortQueries
                .getPatientsDeadTransferredOutSuspensionsInProgramStateByReportingEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "2",
        EptsReportUtils.map(
            this.txCurrCohortQueries.getDeadPatientsInDemographiscByReportingEndDate(),
            "onOrBefore=${endDate}"));

    compositionCohortDefinition.addSearch(
        "3",
        EptsReportUtils.map(
            this.txCurrCohortQueries
                .getPatientDeathRegisteredInLastHomeVisitCardByReportingEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "4",
        EptsReportUtils.map(
            this.txCurrCohortQueries
                .getDeadPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "5",
        EptsReportUtils.map(
            this.txCurrCohortQueries
                .getTransferredOutPatientsInFichaResumeAndClinicaOfMasterCardByReportEndDate(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("1 OR 2 OR 3 OR 4 OR 5");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   All patients with 3 APSS/PP consultations (encounter type 35) within 99 days of first VL date
   *   >= 1000 (check Y section: if VL is from encounter type 6 use encounter datetime, if VL is
   *   from encounter type 53 use obs datetime) as VL dateP as follows:
   *   <li>One APSS/PP (encounter type 35) in the same day as VL dateP
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getFirstApss() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                          AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                          AND o.obs_datetime <= :evaluationPeriodEndDate"
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + ") AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${35} "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND e.encounter_datetime = result_vl.vl_dateP "
            + "            GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the First VL
   *   Date>=1000 as VL dateP ( encounter datetime[2nd apss/pp] >= VL dateP + 20 days and <= VL
   *   dateP + 33 days ) and
   *   <li>Note: if there is more than one APSS/PP session in the period between 20 and 33 days
   *       after the “First VL Date>=1000”, consider the first occurrence in this period as the 2nd
   *       “APSS/PP Session Date”.
   * </ul>
   *
   * @return
   */
  public CohortDefinition getSecondApss() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                          AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + " "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                          AND o.obs_datetime <= :evaluationPeriodEndDate"
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + ") AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${35} "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateP, INTERVAL 22 DAY ) "
            + "            AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateP, INTERVAL 33 DAY ) "
            + "            GROUP BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   * Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the 2nd apss/pp (
   * encounter datetime[3rd apss/pp] >= 2nd apss/pp + 20 days and <= 2nd apss/pp + 33 days ) and
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getThirdApss() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN (SELECT p.patient_id, MIN(e.encounter_datetime) AS secund_apss FROM   patient p "
            + "                        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "                            SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                                    FROM   patient p "
            + "                                            INNER JOIN encounter e "
            + "                                                    ON e.patient_id = p.patient_id "
            + "                                            INNER JOIN obs o "
            + "                                                    ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                                    WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND o.voided = 0 "
            + "                                            AND e.encounter_type = ${6} "
            + "                                            AND o.concept_id = ${856} "
            + "                                            AND o.value_numeric >= 1000 "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                                            AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                               AND obs_preg.concept_id = ${1982} "
            + "                                          AND obs_preg.value_coded = ${1065} "
            + "                                         AND NOT EXISTS ("
            + "                                                            SELECT e2.encounter_id FROM encounter e2 "
            + "                                                                                   INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                                                                   WHERE e.encounter_id = e2.encounter_id "
            + "                                                                                    AND obs_brst.concept_id = ${6332} "
            + "                                                                                    AND obs_brst.value_coded = ${1065})     "
            + "                                    UNION "
            + " "
            + "                                    SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                                    FROM   patient p "
            + "                                            INNER JOIN encounter e "
            + "                                                    ON e.patient_id = p.patient_id "
            + "                                            INNER JOIN obs o "
            + "                                                    ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                                    WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND o.voided = 0 "
            + "                                            AND e.encounter_type = ${53} "
            + "                                            AND o.concept_id = ${856} "
            + "                                            AND o.value_numeric >= 1000 "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                                            AND o.obs_datetime <= :evaluationPeriodEndDate "
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + ") AS result  "
            + "                                            GROUP BY result.patient_id))AS result_vl "
            + "                                ON result_vl.patient_id = p.patient_id "
            + "                            WHERE  e.encounter_type = ${35} "
            + "                                AND e.voided = 0 "
            + "                                AND p.voided = 0 "
            + "                                AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateP, INTERVAL 22 DAY ) "
            + "                                AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateP, INTERVAL 33 DAY ) "
            + "                                GROUP BY p.patient_id) AS secund_quey ON secund_quey.patient_id = p.patient_id  "
            + "            WHERE  e.encounter_type = ${35} "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND e.encounter_datetime >=  DATE_ADD(secund_quey.secund_apss, INTERVAL 22 DAY ) "
            + "            AND e.encounter_datetime <= DATE_ADD(secund_quey.secund_apss, INTERVAL 33 DAY ) "
            + "            GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   * All patients with ‘Pedido de carga viral’ (concept id 23722) value coded ‘Carga Viral’ (Concept
   * id 856) registered in Ficha Clinica (encounter type 6) between VL dateP + 80 days and VL dateP
   * + 130 days
   *
   * @return
   */
  public CohortDefinition getViralLoadRequest() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String sql =
        "  SELECT p.patient_id FROM patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                          AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                               INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                          AND o.obs_datetime <= :evaluationPeriodEndDate"
            + "                            AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})     "
            + ") AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "            AND e.voided = 0 "
            + "            AND o.concept_id = ${23722} "
            + "            AND o.value_coded = ${856} "
            + "            AND p.voided = 0 "
            + "            AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateP, INTERVAL 80 DAY ) "
            + "            AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateP, INTERVAL 130 DAY ) "
            + "            GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   * All patient who had a record of 2nd viral load result (concept id 856, value_numeric >=1000,
   * encounter datetime as 2nd VL date) in a clinical consultation (encounter type 6) between VL
   * dateP + 110 days and VL dateP + 160 days
   *
   * @return
   */
  public CohortDefinition getRecordSecondVL() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                      INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                          AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                           AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ("
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065} ) "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                          AND o.obs_datetime <= :evaluationPeriodEndDate"
            + "                         AND obs_preg.concept_id = ${1982} "
            + "                          AND obs_preg.value_coded = ${1065} "
            + "                          AND NOT EXISTS ( "
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})"
            + ") AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND o.concept_id = ${856} "
            + "            AND o.value_numeric >= 1000 "
            + "            AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateP, INTERVAL 110 DAY ) "
            + "            AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateP, INTERVAL 160 DAY ) "
            + "            GROUP BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   * All patients with a viral load result >= 1000 (concept id 856, value_numeric >=1000) in ficha
   * resumo (encounter type 53) between VL dateP + 110 days and VL dateP + 160 days
   *
   * @return
   */
  public CohortDefinition getViralLoadResultMasterCard() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String sql =
        " SELECT p.patient_id FROM patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                      INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime >= :evaluationPeriodStartDate "
            + "                          AND e.encounter_datetime <= :evaluationPeriodEndDate "
            + "                      AND obs_preg.concept_id = ${1982} "
            + "                        AND obs_preg.value_coded = ${1065} "
            + "                        AND NOT EXISTS ( "
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065}) "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                       INNER JOIN obs obs_preg  ON obs_preg.encounter_id = e.encounter_id"
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime >= :evaluationPeriodStartDate "
            + "                          AND o.obs_datetime <= :evaluationPeriodEndDate"
            + "                        AND obs_preg.concept_id = ${1982} "
            + "                        AND obs_preg.value_coded = ${1065} "
            + "                        AND NOT EXISTS ( "
            + "                                           SELECT e2.encounter_id FROM encounter e2 "
            + "                                             INNER JOIN obs obs_brst ON obs_brst.encounter_id = e2.encounter_id "
            + "                                           WHERE e.encounter_id = e2.encounter_id "
            + "                                           AND obs_brst.concept_id = ${6332} "
            + "                                           AND obs_brst.value_coded = ${1065})"
            + ") AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${53} "
            + "            AND e.voided = 0 "
            + "            AND o.concept_id = ${856} "
            + "            AND o.value_numeric >= 1000 "
            + "            AND p.voided = 0 "
            + "            AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateP, INTERVAL 110 DAY ) "
            + "            AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateP, INTERVAL 160 DAY ) "
            + "            GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  /**
   * All patients on 2nd line (concept id 21187, value coded not null) registered on ficha
   * resumo(encounter type 53) and the most recent date (obs_datetime of concept id 21187) between
   * 2nd VL date and EndDate Note: 2nd VL date is the date registered on the denominator. If this
   * date comes from encounter type 6 use encounter datetime, if it comes from encounter type 53 use
   * obs datetime
   *
   * @return
   */
  public CohortDefinition getRecordSecondVlNum15() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());

    String sql =
        " SELECT max_obs_datetime.patient_id  FROM (SELECT p.patient_id, MAX(o.obs_datetime) AS max_obs FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                           WHERE  e.encounter_type =  ${53} "
            + "                             AND e.voided = 0 "
            + "                             AND p.voided = 0 "
            + "                             AND e.location_id = :location "
            + "                             AND o.concept_id =  ${21187} "
            + "                             AND o.value_coded IS NOT NULL "
            + "                             AND o.obs_datetime >=  :evaluationPeriodStartDate "
            + "                             AND o.obs_datetime <= :evaluationPeriodEndDate "
            + "                           GROUP BY p.patient_id "
            + "                       ) AS max_obs_datetime "
            + "                        INNER JOIN ( SELECT p.patient_id, e.encounter_datetime AS second_vl FROM   patient p   "
            + "                   INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                   INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateK  FROM(   "
            + "                       SELECT p.patient_id, e.encounter_datetime AS vl_date   "
            + "                               FROM   patient p   "
            + "                                      INNER JOIN encounter e   "
            + "                                              ON e.patient_id = p.patient_id   "
            + "                                      INNER JOIN obs o   "
            + "                                              ON o.encounter_id = e.encounter_id   "
            + "                               WHERE  p.voided = 0   "
            + "                                      AND e.voided = 0   "
            + "                                      AND o.voided = 0   "
            + "                                      AND e.encounter_type =  ${6}    "
            + "                                      AND o.concept_id =  ${856}    "
            + "                                      AND o.value_numeric >= 1000   "
            + "                                      AND e.location_id = :location   "
            + "                                      AND e.encounter_datetime >= :evaluationPeriodStartDate   "
            + "                                      AND e.encounter_datetime <= :evaluationPeriodEndDate  "
            + "               "
            + "                               UNION   "
            + "               "
            + "                               SELECT p.patient_id, o.obs_datetime AS vl_date   "
            + "                               FROM   patient p   "
            + "                                      INNER JOIN encounter e   "
            + "                                              ON e.patient_id = p.patient_id   "
            + "                                      INNER JOIN obs o   "
            + "                                              ON o.encounter_id = e.encounter_id   "
            + "                               WHERE  p.voided = 0   "
            + "                                      AND e.voided = 0   "
            + "                                      AND o.voided = 0   "
            + "                                      AND e.encounter_type =  ${53}    "
            + "                                      AND o.concept_id =  ${856}    "
            + "                                      AND o.value_numeric >= 1000   "
            + "                                      AND e.location_id = :location   "
            + "                                      AND o.obs_datetime >= :evaluationPeriodStartDate   "
            + "                                      AND o.obs_datetime <= :evaluationPeriodEndDate) AS result    "
            + "                                      GROUP BY result.patient_id))AS result_vl   "
            + "                           ON result_vl.patient_id = p.patient_id   "
            + "                    WHERE  e.encounter_type =  6    "
            + "                        AND e.voided = 0   "
            + "                        AND p.voided = 0  "
            + "                        AND e.location_id = :location  "
            + "                        AND o.concept_id =  ${856}    "
            + "                        AND o.value_numeric >= 1000   "
            + "                        AND e.encounter_datetime >=  DATE_ADD(result_vl.vl_dateK, INTERVAL 110 DAY )   "
            + "                        AND e.encounter_datetime <= DATE_ADD(result_vl.vl_dateK, INTERVAL 160 DAY )  "
            + "                        ) AS second_line ON second_line.patient_id = max_obs_datetime.patient_id "
            + "                            WHERE max_obs_datetime.max_obs >= second_line.second_vl "
            + "                            AND max_obs_datetime.max_obs <= :evaluationPeriodEndDate ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  public CohortDefinition getNumberOfPregnantWomenOnFirstLineDenominator(Boolean den) {

    String MAPPING =
        "evaluationPeriodStartDate=${evaluationPeriodStartDate-5m+1},evaluationPeriodEndDate=${evaluationPeriodEndDate-4m},location=${location}";

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    if (den) {
      compositionCohortDefinition.setName(
          "Number of pregnant women on 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 7 months ago and with a request for a second VL registered");
    } else {
      compositionCohortDefinition.setName(
          "Number of pregnant women in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 7 months ago, with a request registered for a second VL test and with result of second VL above 1000");
    }

    compositionCohortDefinition.addSearch(
        "firstApss", EptsReportUtils.map(getFirstApss(), MAPPING));

    compositionCohortDefinition.addSearch(
        "secondApss", EptsReportUtils.map(getSecondApss(), MAPPING));

    compositionCohortDefinition.addSearch(
        "thirdApss", EptsReportUtils.map(getThirdApss(), MAPPING));

    compositionCohortDefinition.addSearch(
        "viralLoadRequest", EptsReportUtils.map(getViralLoadRequest(), MAPPING));

    compositionCohortDefinition.addSearch(
        "recordSecondVL", EptsReportUtils.map(getRecordSecondVL(), MAPPING));

    compositionCohortDefinition.addSearch(
        "viralLoadResultMasterCard", EptsReportUtils.map(getViralLoadResultMasterCard(), MAPPING));

    compositionCohortDefinition.addSearch(

        "transferredIn",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredInPatients(),
            "onOrBefore=${evaluationPeriodEndDate-4m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${evaluationPeriodEndDate-4m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "dead",
        EptsReportUtils.map(
            this.getDeadPatients(), "endDate=${evaluationPeriodEndDate-4m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "linhaTerapeutica",
        EptsReportUtils.map(
            getFirstLineArt(),
            "startDate=${evaluationPeriodStartDate-5m+1},endDate=${evaluationPeriodEndDate-4m},location=${location}"));

    if (den) {
      compositionCohortDefinition.setCompositionString(
          "((firstApss AND secondApss AND thirdApss AND viralLoadRequest) AND linhaTerapeutica) AND NOT (transferredIn OR transferredOut OR dead)");
    } else {
      compositionCohortDefinition.setCompositionString(
          "firstApss AND secondApss AND thirdApss AND viralLoadRequest AND recordSecondVL AND viralLoadResultMasterCard");
    }

    return compositionCohortDefinition;
  }

  public CohortDefinition getNumberOfPregnantWomenOnFirstLineArtRegimen(Boolean den) {
    String MAPPING =
        "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1},evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}";
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    if (den) {
      compositionCohortDefinition.setName(
          "Number of pregnant women on 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 7 months ago and with a request for a second VL registered");
    } else {
      compositionCohortDefinition.setName(
          "Number of pregnant women in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 7 months ago, with a request registered for a second VL test and with result of second VL above 1000");
    }

    compositionCohortDefinition.addSearch(
        "recordSecondVL", EptsReportUtils.map(getRecordSecondVL(), MAPPING));

    compositionCohortDefinition.addSearch(
        "viralLoadResultMasterCard", EptsReportUtils.map(getViralLoadResultMasterCard(), MAPPING));

    compositionCohortDefinition.addSearch(
        "secondVlNum15", EptsReportUtils.map(getRecordSecondVlNum15(), MAPPING));

    compositionCohortDefinition.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredInPatients(),
            "onOrBefore=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "dead",
        EptsReportUtils.map(
            this.getDeadPatients(), "endDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "linhaTerapeutica",
        EptsReportUtils.map(
            getFirstLineArt(),
            "startDate=${evaluationPeriodStartDate-12m+1},endDate=${evaluationPeriodEndDate-11m},location=${location}"));

    if (den) {
      compositionCohortDefinition.setCompositionString(
          "recordSecondVL AND viralLoadResultMasterCard AND linhaTerapeutica AND NOT (transferredIn OR transferredOut OR dead)");
    } else {
      compositionCohortDefinition.setCompositionString(
          "recordSecondVL AND viralLoadResultMasterCard AND secondVlNum15 AND linhaTerapeutica AND NOT (transferredIn OR transferredOut OR dead)");
    }

    return compositionCohortDefinition;
  }

  /**
   * All patients with 3 APSS/PP consultations (encounter type 35) within 99 days of first VL date
   * >= 1000 (check Y section: if VL is from encounter type 6 use encounter datetime, if VL is from
   * encounter type 53 use obs datetime) as VL DateY as follows:
   *
   * <p>One APSS/PP (encounter type 35) in the same day as VL DateY
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getZpart1() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        " SELECT p.patient_id FROM   patient p  "
            + "    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "    INNER JOIN (SELECT p.patient_id "
            + "  FROM patient p "
            + "  INNER JOIN ( "
            + "  SELECT min_vl.patient_id , MIN(min_datetime) final_min_date "
            + "  FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate  "
            + "           AND e.location_id = :location"
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate "
            + "           AND e.location_id = :location "
            + "        GROUP BY p.patient_id "
            + "     )min_vl "
            + "    GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id "
            + "    WHERE p.patient_id NOT IN ( "
            + "                 SELECT p.patient_id "
            + "                 FROM patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                 WHERE p.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND e.encounter_type = ${53} "
            + "                   AND o.concept_id IN (${1982},${6332}) "
            + "                   AND o.value_coded = ${1065} "
            + "                   AND o.obs_datetime = final_min.final_min_date "
            + "                 UNION "
            + "                 SELECT p.patient_id "
            + "                 FROM patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                 WHERE p.voided = 0 "
            + "                   AND o.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND o.concept_id IN (${1982},${6332}) "
            + "                   AND o.value_coded = ${1065} "
            + "                   AND e.encounter_datetime = final_min.final_min_date))AS vlDateY    "
            + "         ON vlDateY.patient_id = p.patient_id  "
            + "     WHERE  e.encounter_type = ${35} "
            + "         AND e.voided = 0 "
            + "         AND p.voided = 0 "
            + "         AND e.encounter_datetime = vlDateY.final_min_date "
            + "         GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients with 3 APSS/PP consultations (encounter type 35) within 99 days of first VL date
   * >= 1000 (check Y section: if VL is from encounter type 6 use encounter datetime, if VL is from
   * encounter type 53 use obs datetime) as VL DateY as follows:
   *
   * <p>Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the First VL
   * Date>=1000 as VL DateY ( encounter datetime[2nd apss/pp] >= VL DateY + 20 days and <= VL DateY
   * + 33 days ) and
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getZpart2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT patient_id FROM                                                                                                                                                                                                  "
            + "(SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occur FROM   patient p  "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "       INNER JOIN (SELECT p.patient_id, final_min.final_min_date  "
            + "            FROM patient p    "
            + "            INNER JOIN (  "
            + "            SELECT min_vl.patient_id , MIN(min_datetime) final_min_date   "
            + "            FROM (    "
            + "                     SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime   "
            + "                     FROM patient p   "
            + "                              INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                              INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                     WHERE p.voided = 0   "
            + "                       AND o.voided = 0   "
            + "                       AND e.voided = 0   "
            + "                       AND e.encounter_type = ${6}   "
            + "                       AND o.concept_id = ${856}     "
            + "                       AND o.value_numeric >= 1000    "
            + "                       AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate   "
            + "                       AND e.location_id = :location    "
            + "                     GROUP BY p.patient_id    "
            + "                     UNION    "
            + "                     SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime     "
            + "                     FROM patient p   "
            + "                              INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                              INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                     WHERE p.voided = 0   "
            + "                       AND o.voided = 0   "
            + "                       AND e.voided = 0   "
            + "                       AND e.encounter_type = ${53}  "
            + "                       AND o.concept_id = ${856}     "
            + "                       AND o.value_numeric >= 1000    "
            + "                       AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate "
            + "                       AND e.location_id = :location    "
            + "                    GROUP BY p.patient_id     "
            + "                 )min_vl  "
            + "          GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id    "
            + "          WHERE p.patient_id NOT IN (     "
            + "            SELECT p.patient_id   "
            + "            FROM patient p    "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "            WHERE p.voided = 0    "
            + "              AND o.voided = 0    "
            + "              AND e.voided = 0    "
            + "              AND e.encounter_type = ${53}   "
            + "              AND o.concept_id IN (${1982},${6332})     "
            + "              AND o.value_coded = ${1065}    "
            + "              AND o.obs_datetime = final_min.final_min_date   "
            + "            UNION     "
            + "            SELECT p.patient_id   "
            + "            FROM patient p    "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "            WHERE p.voided = 0    "
            + "              AND o.voided = 0    "
            + "              AND e.voided = 0    "
            + "              AND e.encounter_type = ${6}    "
            + "              AND o.concept_id IN (${1982},${6332})     "
            + "              AND o.value_coded = ${1065}    "
            + "              AND e.encounter_datetime = final_min.final_min_date))AS vlDateY "
            + "               ON vlDateY.patient_id = p.patient_id   "
            + "        WHERE  e.encounter_type = ${35}  "
            + "            AND e.voided = 0  "
            + "            AND p.voided = 0  "
            + "            AND e.encounter_datetime >= DATE_ADD(vlDateY.final_min_date, interval 20 DAY)     "
            + "            AND e.encounter_datetime <= DATE_ADD(vlDateY.final_min_date, interval 33 DAY) "
            + "            GROUP BY p.patient_id )secondApss;";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients with 3 APSS/PP consultations (encounter type 35) within 99 days of first VL date
   * >= 1000 (check Y section: if VL is from encounter type 6 use encounter datetime, if VL is from
   * encounter type 53 use obs datetime) as VL DateY as follows:
   *
   * <p>Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the 2nd apss/pp (
   * encounter datetime[3rd apss/pp] >= 2nd apss/pp + 20 days and <= 2nd apss/pp + 33 days ) and
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getZpart3() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        " SELECT p.patient_id FROM   patient p                                                                                                                                                          "
            + "     INNER JOIN encounter e ON p.patient_id = e.patient_id    "
            + "     INNER JOIN (SELECT p.patient_id, MIN(e.encounter_datetime) AS first_occur FROM   patient p   "
            + "     INNER JOIN encounter e ON p.patient_id = e.patient_id    "
            + "     INNER JOIN (SELECT p.patient_id, final_min.final_min_date    "
            + "          FROM patient p  "
            + "          INNER JOIN (    "
            + "          SELECT min_vl.patient_id , MIN(min_datetime) final_min_date     "
            + "          FROM (  "
            + "                   SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime     "
            + "                   FROM patient p     "
            + "                            INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                   WHERE p.voided = 0     "
            + "                     AND o.voided = 0     "
            + "                     AND e.voided = 0     "
            + "                     AND e.encounter_type = ${6}     "
            + "                     AND o.concept_id = ${856}   "
            + "                     AND o.value_numeric >= 1000  "
            + "                     AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate  "
            + "                     AND e.location_id = :location  "
            + "                   GROUP BY p.patient_id  "
            + "                   UNION  "
            + "                   SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime   "
            + "                   FROM patient p     "
            + "                            INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                            INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                   WHERE p.voided = 0     "
            + "                     AND o.voided = 0     "
            + "                     AND e.voided = 0     "
            + "                     AND e.encounter_type = ${53}    "
            + "                     AND o.concept_id = ${856}   "
            + "                     AND o.value_numeric >= 1000  "
            + "                     AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate"
            + "                     AND e.location_id = :location  "
            + "                  GROUP BY p.patient_id   "
            + "               )min_vl    "
            + "        GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id  "
            + "        WHERE p.patient_id NOT IN (   "
            + "          SELECT p.patient_id     "
            + "          FROM patient p  "
            + "                   INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "          WHERE p.voided = 0  "
            + "            AND o.voided = 0  "
            + "            AND e.voided = 0  "
            + "            AND e.encounter_type = ${53}     "
            + "            AND o.concept_id IN (${1982},${6332})   "
            + "            AND o.value_coded = ${1065}  "
            + "            AND o.obs_datetime = final_min.final_min_date     "
            + "          UNION   "
            + "          SELECT p.patient_id     "
            + "          FROM patient p  "
            + "                   INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "          WHERE p.voided = 0  "
            + "            AND o.voided = 0  "
            + "            AND e.voided = 0  "
            + "            AND e.encounter_type = ${6}  "
            + "            AND o.concept_id IN (${1982},${6332})   "
            + "            AND o.value_coded = ${1065}  "
            + "            AND e.encounter_datetime = final_min.final_min_date))AS vlDateY   "
            + "             ON vlDateY.patient_id = p.patient_id "
            + "      WHERE  e.encounter_type = ${35}    "
            + "          AND e.voided = 0    "
            + "          AND p.voided = 0    "
            + "          AND e.encounter_datetime >= DATE_ADD(vlDateY.final_min_date, INTERVAL 20 DAY)   "
            + "          AND e.encounter_datetime <= DATE_ADD(vlDateY.final_min_date, INTERVAL 33 DAY)   "
            + "          GROUP BY p.patient_id ) AS secondApss on secondApss.patient_id = e.patient_id   "
            + "     WHERE    "
            + "     e.voided = 0 "
            + "          AND p.voided = 0 AND  e.encounter_type = ${35} "
            + "          AND e.encounter_datetime >= DATE_ADD(secondApss.first_occur, INTERVAL 20 DAY)   "
            + "          AND e.encounter_datetime <= DATE_ADD(secondApss.first_occur, INTERVAL 33 DAY)   "
            + "          GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients with 3 APSS/PP consultations (encounter type 35) within 99 days of first VL date
   * >= 1000 (check Y section: if VL is from encounter type 6 use encounter datetime, if VL is from
   * encounter type 53 use obs datetime) as VL DateY as follows:
   *
   * <p>Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the 2nd apss/pp (
   * encounter datetime[3rd apss/pp] >= 2nd apss/pp + 20 days and <= 2nd apss/pp + 33 days ) and
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getZpart4() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());

    String query =
        "   SELECT p.patient_id FROM   patient p   "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id    "
            + " INNER JOIN obs o on e.encounter_id = o.encounter_id  "
            + " INNER JOIN (SELECT p.patient_id, final_min.final_min_date as vlDate  "
            + "      FROM patient p  "
            + "      INNER JOIN (    "
            + "      SELECT min_vl.patient_id , MIN(min_datetime) final_min_date     "
            + "      FROM (  "
            + "               SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime     "
            + "               FROM patient p     "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                        INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "               WHERE p.voided = 0     "
            + "                 AND o.voided = 0     "
            + "                 AND e.voided = 0     "
            + "                 AND e.encounter_type = ${6}     "
            + "                 AND o.concept_id = ${856}   "
            + "                 AND o.value_numeric >= 1000  "
            + "                 AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate   "
            + "                 AND e.location_id = :location  "
            + "               GROUP BY p.patient_id  "
            + "               UNION  "
            + "               SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime   "
            + "               FROM patient p     "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                        INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "               WHERE p.voided = 0     "
            + "                 AND o.voided = 0     "
            + "                 AND e.voided = 0     "
            + "                 AND e.encounter_type = ${53}    "
            + "                 AND o.concept_id = ${856}   "
            + "                 AND o.value_numeric >= 1000  "
            + "                 AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate "
            + "                 AND e.location_id = :location  "
            + "              GROUP BY p.patient_id   "
            + "           )min_vl    "
            + "      GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id    "
            + "      WHERE p.patient_id NOT IN (     "
            + "           SELECT p.patient_id    "
            + "           FROM patient p     "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "           WHERE p.voided = 0     "
            + "             AND o.voided = 0     "
            + "             AND e.voided = 0     "
            + "             AND e.encounter_type = ${53}    "
            + "             AND o.concept_id IN (${1982},${6332})  "
            + "             AND o.value_coded = ${1065}     "
            + "             AND o.obs_datetime = final_min.final_min_date    "
            + "           UNION  "
            + "           SELECT p.patient_id    "
            + "           FROM patient p     "
            + "                    INNER JOIN encounter e ON e.patient_id = p.patient_id     "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "           WHERE p.voided = 0     "
            + "             AND o.voided = 0     "
            + "             AND e.voided = 0     "
            + "             AND e.encounter_type = ${6}     "
            + "             AND o.concept_id IN (${1982},${6332})  "
            + "             AND o.value_coded = ${1065}     "
            + "             AND e.encounter_datetime = final_min.final_min_date))AS vlDateY  "
            + "         ON vlDateY.patient_id = p.patient_id "
            + "  WHERE  e.encounter_type = ${6} "
            + "      AND e.voided = 0    "
            + "      AND p.voided = 0    "
            + "      AND o.concept_id = ${23722}    "
            + "      AND o.value_coded = ${856} "
            + "      AND e.encounter_datetime BETWEEN    "
            + "      DATE_ADD(vlDateY.vlDate, INTERVAL 80 DAY)   "
            + "      AND DATE_ADD(vlDateY.vlDate, INTERVAL 130 DAY)  "
            + "      GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  private CohortDefinition getComposedZQuery() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "Z1",
        EptsReportUtils.map(
            getZpart1(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d}, evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "Z2",
        EptsReportUtils.map(
            getZpart2(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d}, evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "Z3",
        EptsReportUtils.map(
            getZpart3(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d}, evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "Z4",
        EptsReportUtils.map(
            getZpart4(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d}, evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.setCompositionString("Z1 AND Z2 AND Z3 AND Z4");

    return compositionCohortDefinition;
  }

  /**
   * <b> Indicator 12: Denominator: “Number of patients in the 1st Line ART Regimen who received 3
   * consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with a
   * request registered for a second VL test and with result of second VL above 1000”
   *
   * <p>All patient who had a record of 2nd viral load result (concept id 856, value_numeric >=1000,
   * encounter datetime as 2nd VL date) in a clinical consultation (encounter type 6) between VL
   * DateY + 110 days and VL DateY + 160 days; and
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getDen12part1() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "   SELECT p.patient_id                                                                                                                     "
            + "   FROM patient p   "
            + "   INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "   INNER JOIN (     "
            + "   SELECT min_vl.patient_id , MIN(min_vl.min_datetime) secondVl     "
            + "   FROM (   "
            + "        SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime  "
            + "        FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                 INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        WHERE p.voided = 0  "
            + "          AND o.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.encounter_type = ${6}  "
            + "          AND o.concept_id = ${856}    "
            + "          AND o.value_numeric >= 1000   "
            + "          AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate  "
            + "          AND e.location_id = :location  "
            + "        GROUP BY p.patient_id   "
            + "        UNION   "
            + "        SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime    "
            + "        FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                 INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        WHERE p.voided = 0  "
            + "          AND o.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.encounter_type = ${53}     "
            + "          AND o.concept_id = ${856}    "
            + "          AND o.value_numeric >= 1000   "
            + "          AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate    "
            + "          AND e.location_id = :location   "
            + "       GROUP BY p.patient_id    "
            + "    )min_vl     "
            + "       GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id  "
            + "       WHERE     "
            + "           e.encounter_type = ${6} and o.concept_id = ${856} and o.value_numeric >=1000    "
            + "           AND e.encounter_datetime BETWEEN DATE_ADD(final_min.secondVl, INTERVAL 110 DAY)   "
            + "       AND DATE_ADD(final_min.secondVl, INTERVAL 160 DAY)    "
            + "           AND p.patient_id NOT IN (     "
            + "            SELECT p.patient_id  "
            + "            FROM patient p   "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "            WHERE p.voided = 0   "
            + "              AND o.voided = 0   "
            + "              AND e.voided = 0   "
            + "              AND e.encounter_type = 53  "
            + "              AND o.concept_id IN (${1982},${6332})    "
            + "              AND o.value_coded = ${1065}   "
            + "              AND o.obs_datetime = final_min.secondVl    "
            + "            UNION    "
            + "            SELECT p.patient_id  "
            + "            FROM patient p   "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "            WHERE p.voided = 0   "
            + "              AND o.voided = 0   "
            + "              AND e.voided = 0   "
            + "              AND e.encounter_type = ${6}   "
            + "              AND o.concept_id IN (${1982},${6332})    "
            + "              AND o.value_coded = ${1065}   "
            + "              AND e.encounter_datetime = final_min.secondVl)";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b> Indicator 12: Denominator: “Number of patients in the 1st Line ART Regimen who received 3
   * consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with a
   * request registered for a second VL test and with result of second VL above 1000” All patients
   * with a viral load result >= 1000 (concept id 856, value_numeric >=1000) in ficha resumo
   * (encounter type 53) between VL DateY + 110 days and VL DateY + 160 days;
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getDen12part2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "   SELECT p.patient_id                                                                                                                    "
            + "   FROM patient p   "
            + "   INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "   INNER JOIN (     "
            + "   SELECT min_vl.patient_id , MIN(min_vl.min_datetime) secondVl     "
            + "   FROM (   "
            + "            SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime  "
            + "            FROM patient p  "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "            WHERE p.voided = 0  "
            + "              AND o.voided = 0  "
            + "              AND e.voided = 0  "
            + "              AND e.encounter_type = ${6}   "
            + "              AND o.concept_id = ${856}     "
            + "              AND o.value_numeric >= 1000   "
            + "              AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate      "
            + "              AND e.location_id = :location "
            + "            GROUP BY p.patient_id   "
            + "            UNION   "
            + "            SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime    "
            + "            FROM patient p  "
            + "                     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "            WHERE p.voided = 0  "
            + "              AND o.voided = 0  "
            + "              AND e.voided = 0  "
            + "              AND e.encounter_type = ${53}  "
            + "              AND o.concept_id = ${856}     "
            + "              AND o.value_numeric >= 1000   "
            + "              AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate    "
            + "              AND e.location_id = :location "
            + "           GROUP BY p.patient_id    "
            + "        )min_vl     "
            + "   GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id     "
            + "   WHERE    "
            + "       e.encounter_type = ${53} and o.concept_id = ${856} and o.value_numeric >=1000    "
            + "       AND o.obs_datetime BETWEEN DATE_ADD(final_min.secondVl, INTERVAL 110 DAY)    "
            + "   AND DATE_ADD(final_min.secondVl, INTERVAL 160 DAY)   "
            + "       AND p.patient_id NOT IN (    "
            + "        SELECT p.patient_id     "
            + "        FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                 INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        WHERE p.voided = 0  "
            + "          AND o.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.encounter_type = ${53}  "
            + "          AND o.concept_id IN (${1982},${6332})     "
            + "          AND o.value_coded = ${1065}   "
            + "          AND o.obs_datetime = final_min.secondVl   "
            + "        UNION   "
            + "        SELECT p.patient_id     "
            + "        FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                 INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        WHERE p.voided = 0  "
            + "          AND o.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.encounter_type = ${6}   "
            + "          AND o.concept_id IN (${1982},${6332})     "
            + "          AND o.value_coded = ${1065}   "
            + "          AND e.encounter_datetime = final_min.secondVl)";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b> Indicator 12: Numerator: : “Number of patients in the 1st Line ART Regimen who received 3
   * consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with
   * result of second VL above 1000 and who changed to 2nd Line ART”
   *
   * <p>All patient who had a record of 2nd viral load result (concept id 856, value_numeric >=1000,
   * encounter datetime as 2nd VL date) in a clinical consultation (encounter type 6) between VL
   * DateY + 110 days and VL DateY + 160 days; and
   *
   * @return sqlCohortDefinition
   */
  public CohortDefinition getNum12() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "    SELECT p.patient_id from patient p INNER JOIN encounter e on e.patient_id = p.patient_id                                          "
            + "    INNER JOIN obs o on o.encounter_id = e.encounter_id  "
            + "    INNER JOIN ( "
            + "        SELECT pp.patient_id, MAX(oo.obs_datetime) as most_recent FROM   "
            + "        patient pp INNER JOIN encounter ee ON ee.patient_id = pp.patient_id  "
            + "        INNER JOIN obs oo ON oo.encounter_id = ee.encounter_id   "
            + "        WHERE ee.voided = 0 and oo.voided=0 and pp.voided = 0  AND ee.location_id = :location   "
            + "        and ee.encounter_type = ${53} and oo.concept_id = ${21187} AND oo.value_coded IS NOT NULL   "
            + "        GROUP BY pp.patient_id   "
            + "    ) as secondLine on secondLine.patient_id = p.patient_id  "
            + "    INNER JOIN   "
            + "    (    "
            + "        SELECT p.patient_id, e.encounter_datetime as second_vl_date  "
            + "                FROM patient p   "
            + "                INNER JOIN encounter e ON e.patient_id = p.patient_id    "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                INNER JOIN (     "
            + "                SELECT min_vl.patient_id , MIN(min_vl.min_datetime) secondVl     "
            + "                FROM (   "
            + "                         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                                  INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "                         WHERE p.voided = 0  "
            + "                   AND o.voided = 0  "
            + "                   AND e.voided = 0  "
            + "                   AND e.encounter_type = ${6}   "
            + "                   AND o.concept_id = ${856}     "
            + "                   AND o.value_numeric >= 1000   "
            + "                   AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate  "
            + "                   AND e.location_id = :location "
            + "                 GROUP BY p.patient_id   "
            + "                 UNION   "
            + "                 SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime    "
            + "                 FROM patient p  "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                          INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "                 WHERE p.voided = 0  "
            + "                   AND o.voided = 0  "
            + "                   AND e.voided = 0  "
            + "                   AND e.encounter_type = ${53}  "
            + "                   AND o.concept_id = ${856}     "
            + "                   AND o.value_numeric >= 1000   "
            + "                   AND o.obs_datetime BETWEEN :evaluationPeriodStartDate AND :evaluationPeriodEndDate    "
            + "                   AND e.location_id = :location "
            + "                GROUP BY p.patient_id    "
            + "             )min_vl     "
            + "        GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id     "
            + "        WHERE    "
            + "            e.encounter_type = ${6} and o.concept_id = ${856} and o.value_numeric >=1000 "
            + "            AND e.encounter_datetime BETWEEN DATE_ADD(final_min.secondVl, INTERVAL 110 DAY)  "
            + "        AND DATE_ADD(final_min.secondVl, INTERVAL 160 DAY)   "
            + "            AND p.patient_id NOT IN (    "
            + "             SELECT p.patient_id     "
            + "             FROM patient p  "
            + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "             WHERE p.voided = 0  "
            + "               AND o.voided = 0  "
            + "               AND e.voided = 0 AND e.location_id = :location "
            + "               AND e.encounter_type = ${53}  "
            + "               AND o.concept_id IN (${1982},${6332})     "
            + "               AND o.value_coded = ${1065}   "
            + "               AND o.obs_datetime = final_min.secondVl   "
            + "             UNION   "
            + "             SELECT p.patient_id     "
            + "             FROM patient p  "
            + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                      INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "             WHERE p.voided = 0  "
            + "               AND o.voided = 0  "
            + "               AND e.voided = 0 AND e.location_id = :location  "
            + "               AND e.encounter_type = ${6}   "
            + "               AND o.concept_id IN (${1982},${6332})     "
            + "               AND o.value_coded = ${1065}   "
            + "               AND e.encounter_datetime = final_min.secondVl)    "
            + "        ) as secondVlDate on secondVlDate.patient_id = p.patient_id  "
            + "        WHERE secondLine.most_recent BETWEEN secondVlDate.second_vl_date AND :evaluationPeriodEndDate GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  public CohortDefinition getNumDen12Indicators(Boolean den) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    if (den) {
      compositionCohortDefinition.setName(
          "Number of patients in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with a request registered for a second VL test and with result of second VL above 1000");
    } else {
      compositionCohortDefinition.setName(
          "Number of patients in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 9 months ago, with result of second VL above 1000 and who changed to 2nd Line ART");
    }

    compositionCohortDefinition.addSearch(
        "secondVlResult",
        EptsReportUtils.map(
            getDen12part1(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d},evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "vlResult",
        EptsReportUtils.map(
            getDen12part2(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d},evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "secondLineResult",
        EptsReportUtils.map(
            getNum12(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-12m+1d},evaluationPeriodEndDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredInPatients(),
            "onOrBefore=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "dead",
        EptsReportUtils.map(
            this.getDeadPatients(), "endDate=${evaluationPeriodEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "linhaTerapeutica",
        EptsReportUtils.map(
            getFirstLineArt(),
            "startDate=${evaluationPeriodStartDate-12m+1},endDate=${evaluationPeriodEndDate-11m},location=${location}"));

    if (den) {
      compositionCohortDefinition.setCompositionString(
          "secondVlResult AND vlResult AND linhaTerapeutica AND NOT (transferredIn OR transferredOut OR dead)");
    } else {
      compositionCohortDefinition.setCompositionString(
          "secondVlResult AND vlResult AND secondLineResult AND linhaTerapeutica AND NOT (transferredIn OR transferredOut OR dead)");
    }

    return compositionCohortDefinition;
  }

  /**
   * All patient who had a record of 2nd viral load result (concept id 856, value_numeric <1000 and
   * concept id 1305, value coded not null) in a clinical consultation (encounter type 6) between VL
   * dateP + 110 days and VL dateP + 160 days
   *
   * @return
   */
  public CohortDefinition getRecordSecondVLNum14() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String sql =
        " SELECT p.patient_id FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id"
            + "       INNER JOIN ((SELECT result.patient_id, MIN(result.vl_date) AS vl_dateP  FROM( "
            + "           SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_datetime BETWEEN :evaluationPeriodStartDate "
            + "                          AND :evaluationPeriodEndDate "
            + " "
            + "                   UNION "
            + " "
            + "                   SELECT p.patient_id, o.obs_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o "
            + "                                  ON o.encounter_id = e.encounter_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.encounter_type = ${53} "
            + "                          AND o.concept_id = ${856} "
            + "                          AND o.value_numeric >= 1000 "
            + "                          AND e.location_id = :location "
            + "                          AND o.obs_datetime BETWEEN :evaluationPeriodStartDate "
            + "                          AND :evaluationPeriodEndDate) AS result  "
            + "                          GROUP BY result.patient_id))AS result_vl "
            + "               ON result_vl.patient_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND (o.concept_id = ${856} AND o.value_numeric < 1000) "
            + "             AND (o2.concept_id = ${1305} AND o2.value_coded IS NOT NULL)"
            + "            AND e.encounter_datetime BETWEEN DATE_ADD(result_vl.vl_dateP, INTERVAL 110 DAY ) "
            + "            AND DATE_ADD(result_vl.vl_dateP, INTERVAL 160 DAY ) "
            + "            GROUP BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));

    return sqlCohortDefinition;
  }

  public CohortDefinition getNum14Indicator() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "Number of pregnant women in the 1st Line ART Regimen who received 3 consecutive sessions of APSS/PP after first VL result above 1000 copies 7 months ago, with a request registered for a second VL test and with result of second VL below 1000");
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodStartDate", "evaluationPeriodStartDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("evaluationPeriodEndDate", "evaluationPeriodEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "secondRecordVlResult",
        EptsReportUtils.map(
            getRecordSecondVLNum14(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-5m+1},evaluationPeriodEndDate=${evaluationPeriodEndDate-4m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "vlResultMaster",
        EptsReportUtils.map(
            getViralLoadResultMasterCard(),
            "evaluationPeriodStartDate=${evaluationPeriodStartDate-5m+1},evaluationPeriodEndDate=${evaluationPeriodEndDate-4m},location=${location}"));

    compositionCohortDefinition.setCompositionString("secondRecordVlResult AND vlResultMaster");

    return compositionCohortDefinition;
  }

  /**
   * Patients with 3 APSS/PP sessions registered within 99 days of CV result above 1000 copies as
   * follows:
   *
   * <p>One APSS/PP (encounter type 35) in the same day as the high VL date "First VL Date>=1000"
   * (check X section: if VL is from encounter type 6 use encounter datetime, if VL is from
   * encounter type 53 use obs datetime)
   *
   * @return
   */
  public CohortDefinition getFirstAPSSInTheSameDayOfXQuery() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String sql =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "    INNER JOIN  (  "
            + " SELECT p.patient_id, final_min.final_min_date "
            + "FROM patient p "
            + "INNER JOIN ( "
            + "SELECT min_vl.patient_id , MIN(min_datetime) final_min_date "
            + "FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "           AND e.location_id = :location"
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "           AND e.location_id = :location "
            + "        GROUP BY p.patient_id "
            + "     )min_vl "
            + "GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id "
            + "WHERE p.patient_id NOT IN ( "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND o.obs_datetime = final_min.final_min_date "
            + "                            UNION "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND e.encounter_datetime = final_min.final_min_date)"
            + " ) AS xquery ON xquery.patient_id=p.patient_id "
            + " WHERE p.voided = 0 "
            + "   AND e.voided = 0"
            + "   AND e.encounter_type = ${35} "
            + "   AND e.encounter_datetime = xquery.final_min_date "
            + "   AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));
    return sqlCohortDefinition;
  }

  /**
   * Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the First VL
   * Date>=1000 as VL date ( encounter datetime[2nd apss/pp] >= VL date + 20 days and <= VL date +
   * 33 days ) and
   *
   * <p>Note: if there is more than one APSS/PP session in the period between 20 and 33 days after
   * the “First VL Date>=1000”, consider the first occurrence in this period as the 2nd “APSS/PP
   * Session Date”.
   *
   * @return
   */
  public CohortDefinition getAPSSInIn20To33DaysAfterXQuery() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String sql =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "    INNER JOIN  (  "
            + " SELECT p.patient_id, final_min.final_min_date "
            + "FROM patient p "
            + "INNER JOIN ( "
            + "SELECT min_vl.patient_id , MIN(min_datetime) final_min_date "
            + "FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "           AND e.location_id = :location"
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "           AND e.location_id = :location "
            + "        GROUP BY p.patient_id "
            + "     )min_vl "
            + "GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id "
            + "WHERE p.patient_id NOT IN ( "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND o.obs_datetime = final_min.final_min_date "
            + "                            UNION "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND e.encounter_datetime = final_min.final_min_date)"
            + " ) AS xquery ON xquery.patient_id=p.patient_id "
            + " WHERE p.voided = 0 "
            + "   AND e.voided = 0"
            + "   AND e.encounter_type = ${35} "
            + "   AND TIMESTAMPDIFF(DAY, xquery.final_min_date, e.encounter_datetime) BETWEEN 20 AND 33 "
            + "   AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));
    return sqlCohortDefinition;
  }

  /**
   * Another APSS/PP (encounter type 35) occurred between 20 to 33 days after the 2nd apss/pp (
   * encounter datetime[3rd apss/pp] >= 2nd apss/pp + 20 days and <= 2nd apss/pp + 33 days ) and
   *
   * @return
   */
  public CohortDefinition getAPSSIn20To33DaysAfter2ndAPSSConsultation() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String sql =
        " SELECT p.patient_id"
            + " FROM patient p "
            + "    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "    INNER JOIN  ( "
            + " SELECT p.patient_id, MIN(e.encounter_datetime) min_apss "
            + " FROM patient p "
            + "    INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "    INNER JOIN  (  "
            + " SELECT p.patient_id, final_min.final_min_date "
            + " FROM patient p "
            + " INNER JOIN ( "
            + " SELECT min_vl.patient_id , MIN(min_datetime) final_min_date "
            + " FROM ( "
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "           AND e.location_id = :location"
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND o.concept_id = ${856} "
            + "           AND o.value_numeric >= 1000 "
            + "           AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "           AND e.location_id = :location "
            + "        GROUP BY p.patient_id "
            + "     )min_vl "
            + " GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id "
            + " WHERE p.patient_id NOT IN ( "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND o.obs_datetime = final_min.final_min_date "
            + "                            UNION "
            + "                            SELECT p.patient_id "
            + "                            FROM patient p "
            + "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE p.voided = 0 "
            + "                              AND o.voided = 0 "
            + "                              AND e.voided = 0 "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND o.concept_id IN (${1982},${6332}) "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND e.encounter_datetime = final_min.final_min_date)"
            + " ) AS xquery ON xquery.patient_id=p.patient_id "
            + " WHERE p.voided = 0 "
            + "   AND e.voided = 0"
            + "   AND e.encounter_type = ${35} "
            + "   AND TIMESTAMPDIFF(DAY, xquery.final_min_date, e.encounter_datetime) BETWEEN 20 AND 33 "
            + "   AND e.location_id = :location "
            + " GROUP BY p.patient_id) AS second_apss on second_apss.patient_id= p.patient_id"
            + "  WHERE p.voided = 0 "
            + "                      AND e.voided = 0"
            + "                       AND e.encounter_type = ${35} "
            + "                        AND TIMESTAMPDIFF(DAY, second_apss.min_apss, e.encounter_datetime) BETWEEN 20 AND 33 "
            + "                        AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));
    return sqlCohortDefinition;
  }

  public CohortDefinition getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
      int lowerBound,
      int upperBound,
      EncounterType encounterType,
      int value,
      Concept concept,
      ValueType valueType,
      boolean additional) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("value", value);
    map.put("encounterType", encounterType.getEncounterTypeId());
    map.put("question", concept.getConceptId());
    map.put("lowerBound", lowerBound);
    map.put("upperBound", upperBound);

    StringBuilder sql = new StringBuilder();
    sql.append(" SELECT p.patient_id ");
    sql.append(" FROM patient p ");
    sql.append("    INNER JOIN encounter e on p.patient_id = e.patient_id ");
    sql.append("    INNER JOIN obs o ON e.encounter_id = o.encounter_id ");
    if (additional) {
      sql.append("    INNER JOIN obs o1 ON e.encounter_id = o1.encounter_id ");
    }
    sql.append("    INNER JOIN  (  ");
    sql.append(" SELECT p.patient_id, final_min.final_min_date ");
    sql.append(" FROM patient p ");
    sql.append(" INNER JOIN ( ");
    sql.append(" SELECT min_vl.patient_id , MIN(min_datetime) final_min_date ");
    sql.append(" FROM ( ");
    sql.append("         SELECT p.patient_id, MIN(e.encounter_datetime) AS min_datetime ");
    sql.append("         FROM patient p ");
    sql.append("                  INNER JOIN encounter e ON e.patient_id = p.patient_id ");
    sql.append("                  INNER JOIN obs o ON e.encounter_id = o.encounter_id ");
    sql.append("         WHERE p.voided = 0 ");
    sql.append("           AND o.voided = 0 ");
    sql.append("           AND e.voided = 0 ");
    sql.append("           AND e.encounter_type = ${6} ");
    sql.append("           AND o.concept_id = ${856} ");
    sql.append("           AND o.value_numeric >= 1000 ");
    sql.append("           AND e.encounter_datetime BETWEEN :startDate AND :endDate  ");
    sql.append("           AND e.location_id = :location");
    sql.append("         GROUP BY p.patient_id ");
    sql.append("         UNION ");
    sql.append("         SELECT p.patient_id, MIN(o.obs_datetime) AS min_datetime ");
    sql.append("         FROM patient p ");
    sql.append("                  INNER JOIN encounter e ON e.patient_id = p.patient_id ");
    sql.append("                  INNER JOIN obs o ON e.encounter_id = o.encounter_id ");
    sql.append("         WHERE p.voided = 0 ");
    sql.append("           AND o.voided = 0 ");
    sql.append("           AND e.voided = 0 ");
    sql.append("           AND e.encounter_type = ${53} ");
    sql.append("           AND o.concept_id = ${856} ");
    sql.append("           AND o.value_numeric >= 1000 ");
    sql.append("           AND o.obs_datetime BETWEEN :startDate AND :endDate ");
    sql.append("           AND e.location_id = :location ");
    sql.append("        GROUP BY p.patient_id ");
    sql.append("     )min_vl ");
    sql.append(" GROUP BY min_vl.patient_id) final_min ON final_min.patient_id = p.patient_id ");
    sql.append(" WHERE p.patient_id NOT IN ( ");
    sql.append("                            SELECT p.patient_id ");
    sql.append("                            FROM patient p ");
    sql.append(
        "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id ");
    sql.append(
        "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id ");
    sql.append("                            WHERE p.voided = 0 ");
    sql.append("                              AND o.voided = 0 ");
    sql.append("                              AND e.voided = 0 ");
    sql.append("                              AND e.encounter_type = ${53} ");
    sql.append("                              AND o.concept_id IN (${1982},${6332}) ");
    sql.append("                              AND o.value_coded = ${1065} ");
    sql.append("                              AND o.obs_datetime = final_min.final_min_date ");
    sql.append("                            UNION ");
    sql.append("                            SELECT p.patient_id ");
    sql.append("                            FROM patient p ");
    sql.append(
        "                                     INNER JOIN encounter e ON e.patient_id = p.patient_id ");
    sql.append(
        "                                     INNER JOIN obs o ON e.encounter_id = o.encounter_id ");
    sql.append("                            WHERE p.voided = 0 ");
    sql.append("                              AND o.voided = 0 ");
    sql.append("                              AND e.voided = 0 ");
    sql.append("                              AND e.encounter_type = ${6} ");
    sql.append("                              AND o.concept_id IN (${1982},${6332}) ");
    sql.append("                              AND o.value_coded = ${1065} ");
    sql.append(
        "                              AND e.encounter_datetime = final_min.final_min_date)");
    sql.append(" ) AS xquery ON xquery.patient_id=p.patient_id ");
    sql.append(" WHERE p.voided = 0 ");
    sql.append("   AND e.voided = 0 ");
    if (additional) {
      sql.append("   AND o1.voided = 0 ");
      sql.append("   AND o1.concept_id = ${1305} ");
      sql.append("   AND o1.value_coded IS NOT NULL ");
    }
    sql.append("   AND o.concept_id =  ${question}   ");
    if (valueType == ValueType.VALUE_CODED) {
      sql.append("   AND o.value_coded =  ${value}   ");
    }
    if (valueType == ValueType.VALUE_NUMERIC) {
      sql.append("   AND o.value_numeric >=  ${value}   ");
    }
    if (encounterType.equals(hivMetadata.getAdultoSeguimentoEncounterType())) {
      sql.append(
          "   AND e.encounter_datetime BETWEEN DATE_ADD(xquery.final_min_date, INTERVAL ${lowerBound} DAY) AND DATE_ADD(xquery.final_min_date, INTERVAL ${upperBound} DAY)");
    }
    if (encounterType.equals(hivMetadata.getMasterCardEncounterType())) {
      sql.append(
          "   AND o.obs_datetime BETWEEN DATE_ADD(xquery.final_min_date, INTERVAL ${lowerBound} DAY) AND DATE_ADD(xquery.final_min_date, INTERVAL ${upperBound} DAY)");
    }
    sql.append("   AND e.encounter_type = ${encounterType} ");
    sql.append("   AND e.location_id = :location ");

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(sql));
    return sqlCohortDefinition;
  }

  public CohortDefinition getDenominator10() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "A", EptsReportUtils.map(this.getFirstAPSSInTheSameDayOfXQuery(), mappings));

    compositionCohortDefinition.addSearch(
        "B", EptsReportUtils.map(this.getAPSSInIn20To33DaysAfterXQuery(), mappings));

    compositionCohortDefinition.addSearch(
        "C", EptsReportUtils.map(this.getAPSSIn20To33DaysAfter2ndAPSSConsultation(), mappings));

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                80,
                130,
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getHivViralLoadConcept().getConceptId(),
                hivMetadata.getApplicationForLaboratoryResearch(),
                ValueType.VALUE_CODED,
                false),
            mappings));
    ;
    compositionCohortDefinition.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "transferredOut",
        EptsReportUtils.map(
            this.commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "dead",
        EptsReportUtils.map(this.getDeadPatients(), "endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "(A AND B AND C AND D) AND NOT (transferredIn OR transferredOut OR dead) ");

    return compositionCohortDefinition;
  }

  public CohortDefinition getNumerator10() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "denominator10",
        EptsReportUtils.map(
            getDenominator10(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                110,
                160,
                hivMetadata.getAdultoSeguimentoEncounterType(),
                VIRAL_LOAD_1000_COPIES,
                hivMetadata.getHivViralLoadConcept(),
                ValueType.VALUE_CODED,
                false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                110,
                160,
                hivMetadata.getMasterCardEncounterType(),
                VIRAL_LOAD_1000_COPIES,
                hivMetadata.getHivViralLoadConcept(),
                ValueType.VALUE_CODED,
                false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("denominator10 AND A AND B");

    return compositionCohortDefinition;
  }

  public CohortDefinition getNumerator11() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                110,
                160,
                hivMetadata.getAdultoSeguimentoEncounterType(),
                VIRAL_LOAD_1000_COPIES,
                hivMetadata.getHivViralLoadConcept(),
                ValueType.VALUE_CODED,
                true),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                110,
                160,
                hivMetadata.getMasterCardEncounterType(),
                VIRAL_LOAD_1000_COPIES,
                hivMetadata.getHivViralLoadConcept(),
                ValueType.VALUE_CODED,
                false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    compositionCohortDefinition.setCompositionString("A AND B ");

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            this.getPedidoCargaViralBetweenLowerBoundAnduppperBoundXQuery(
                80,
                130,
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getHivViralLoadConcept().getConceptId(),
                hivMetadata.getApplicationForLaboratoryResearch(),
                ValueType.VALUE_CODED,
                false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "denominator10",
        EptsReportUtils.map(
            getDenominator10(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("A AND B AND D AND denominator10 ");

    return compositionCohortDefinition;
  }

  public CohortDefinition getDenominator11() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "denominator10",
        EptsReportUtils.map(
            getDenominator10(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("denominator10");

    return compositionCohortDefinition;
  }

  enum ValueType {
    VALUE_NUMERIC,
    VALUE_CODED
  }
}
