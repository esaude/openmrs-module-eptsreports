package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IMER1NumeratorCohortQueries {

  private HivMetadata hivMetadata;

  private IMER1DenominatorCohortQueries imer1DenominatorCohortQueries;

  @Autowired
  public IMER1NumeratorCohortQueries(
      HivMetadata hivMetadata, IMER1DenominatorCohortQueries imer1DenominatorCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.imer1DenominatorCohortQueries = imer1DenominatorCohortQueries;
  }

  /**
   * <b>E: Select all patients who had the first clinical consultation within 7 days of community
   * HIV diagnosis date as following conditions:</b>
   *
   * <ul>
   *   <li>All patients with “HIV Testing Site”(concept_id 23884) value_coded “ATSC”(concept_id
   *       6245) recorded in Ficha Resumo (encounter type 53) and “Data Diagnóstico” (concept_id
   *       22772 obs datetime) <= CohortEndDate recorded in the same Ficha Resumo (encounter type
   *       53)
   *   <li>Filter all patients who had the first clinical consultation (enconter_type 6,
   *       encounter_datetime) by reporting end date within 7 days after “Data Diagnóstico” (first
   *       encounter_datetime >= concept_id 22772 obs datetime (ET 53) and <= concept_id 22772 obs
   *       datetime (ET 53) + 7 days). This is the same as “First Clinical Consultation Date” minus
   *       “HIV Diagnosis Date” <= 7days
   * </ul>
   *
   * @return CohortDefinition
   */
  private CohortDefinition getE() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("E Part from numerator");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("hivTestingSiteConcept", hivMetadata.getHivTestingSiteConcept().getConceptId());
    map.put("typeTestHIVConcept", hivMetadata.getTypeTestHIVConcept().getConceptId());
    map.put(
        "voluntaryCouncelingTestingCommunityConcept",
        hivMetadata.getVoluntaryCouncelingTestingCommunityConcept().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        " SELECT final_outter.patient_id "
            + "FROM"
            + "( "
            + " SELECT outter.patient_id, MIN(outter.min_date)  AS m_date "
            + "    FROM "
            + "    ( "
            + "        SELECT p.patient_id, MIN(data_diag.obs_datetime) AS min_date "
            + "        FROM patient p "
            + "            INNER JOIN encounter e "
            + "                ON e.patient_id = p.patient_id "
            + "            INNER JOIN obs testing_site "
            + "                ON testing_site.encounter_id = e.encounter_id "
            + "            INNER JOIN obs data_diag "
            + "                ON data_diag.encounter_id = e.encounter_id "
            + "        WHERE  "
            + "            p.voided =0 "
            + "            AND e.voided = 0 "
            + "            AND testing_site.voided = 0 "
            + "            AND data_diag.voided = 0 "
            + "            AND e.encounter_type = 53 "
            + "            AND e.location_id = :location "
            + "            AND (testing_site.concept_id = ${hivTestingSiteConcept} AND testing_site.value_coded = ${voluntaryCouncelingTestingCommunityConcept}) "
            + "            AND (data_diag.concept_id = ${typeTestHIVConcept} AND data_diag.obs_datetime <= :endDate) "
            + "        GROUP BY p.patient_id "
            + "        UNION                                     "
            + "        SELECT p.patient_id, MIN(e.encounter_datetime) AS min_date "
            + "        FROM patient p "
            + "            INNER JOIN encounter e "
            + "                ON e.patient_id = p.patient_id "
            + "            INNER JOIN ( "
            + "                        SELECT p.patient_id, MIN(o.obs_datetime) AS et_date "
            + "                        FROM patient p "
            + "                            INNER JOIN encounter e "
            + "                                ON e.patient_id = p.patient_id "
            + "                            INNER JOIN obs o "
            + "                                ON o.encounter_id = e.encounter_id "
            + "                        WHERE  "
            + "                            p.voided =0 "
            + "                            AND e.voided = 0 "
            + "                            AND o.voided = 0 "
            + "                            AND e.encounter_type = 53 "
            + "                            AND e.location_id = :location "
            + "                            AND (o.concept_id = ${typeTestHIVConcept} AND o.obs_datetime <= :endDate) "
            + "                        GROUP BY p.patient_id "
            + "                        ) init_date "
            + "                    ON init_date.patient_id = p.patient_id "
            + "            INNER JOIN ( "
            + "                        SELECT p.patient_id, DATE_ADD(MIN(o.obs_datetime), INTERVAL 7 DAY ) AS et_date "
            + "                        FROM patient p "
            + "                            INNER JOIN encounter e "
            + "                                ON e.patient_id = p.patient_id "
            + "                            INNER JOIN obs o "
            + "                                ON o.encounter_id = e.encounter_id "
            + "                        WHERE  "
            + "                            p.voided =0 "
            + "                            AND e.voided = 0 "
            + "                            AND o.voided = 0 "
            + "                            AND e.encounter_type = 53 "
            + "                            AND e.location_id = :location "
            + "                            AND (o.concept_id = ${typeTestHIVConcept} AND o.obs_datetime <= :endDate) "
            + "                        GROUP BY p.patient_id "
            + "                        ) final_date "
            + "                    ON final_date.patient_id = p.patient_id "
            + "        WHERE  "
            + "            p.voided =0 "
            + "            AND e.voided =0 "
            + "            AND e.encounter_type = 6 "
            + "            AND e.location_id = :location "
            + "            AND e.encounter_datetime >= init_date.et_date "
            + "            AND e.encounter_datetime <= final_date.et_date "
            + "        GROUP BY p.patient_id "
            + "    ) AS outter "
            + " GROUP BY outter.patient_id"
            + " ) AS final_outter ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>F: Select all patients who had the first clinical consultation on the same day of Health
   * Facility HIV diagnosis date as following conditions:</b>
   *
   * <ul>
   *   <li>All patients with “TYPE OF HIV TEST” (concept_id 22772) value_coded “TR” or “PCR”
   *       (concept_id in [1040, 1030]) recorded in Ficha Resumo (encounter type 53) and “Data
   *       Diagnóstico” (concept_id 22772 obs datetime) <= CohortEndDate recorded in the same Ficha
   *       Resumo (encounter type 53)
   *   <li>Filter all patients who had the first clinical consultation (enconter_type 6,
   *       encounter_datetime) by reporting end date on the same day as “Data Diagnóstico” (first
   *       encounter_datetime= concept_id 22772 obs datetime (ET 53).
   * </ul>
   *
   * @return CohortDefinition
   */
  private CohortDefinition getF() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("F from Numerator");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "hivRapidTest1QualitativeConcept",
        hivMetadata.getHivRapidTest1QualitativeConcept().getConceptId());
    map.put("typeTestHIVConcept", hivMetadata.getTypeTestHIVConcept().getConceptId());
    map.put(
        "hivPCRQualitativeConceptUuid",
        hivMetadata.getHivPCRQualitativeConceptUuid().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT final.patient_id  "
            + "FROM "
            + "( "
            + "    SELECT p.patient_id, MIN(e.encounter_datetime) AS min_date "
            + "    FROM patient p "
            + "        INNER JOIN encounter e "
            + "            ON e.patient_id = p.patient_id "
            + " "
            + "        INNER JOIN ( "
            + "            SELECT p.patient_id, data_diag.obs_datetime AS dd_date "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON e.patient_id = p.patient_id "
            + "                    INNER JOIN obs testing_site "
            + "                        ON testing_site.encounter_id = e.encounter_id "
            + "                    INNER JOIN obs data_diag "
            + "                        ON data_diag.encounter_id = e.encounter_id "
            + "                WHERE  "
            + "                    p.voided =0 "
            + "                    AND e.voided = 0 "
            + "                    AND testing_site.voided = 0 "
            + "                    AND data_diag.voided = 0 "
            + "                    AND e.encounter_type = ${masterCardEncounterType} "
            + "                    AND e.location_id = :location "
            + "                    AND (testing_site.concept_id = ${typeTestHIVConcept} AND testing_site.value_coded IN (${hivRapidTest1QualitativeConcept},${hivPCRQualitativeConceptUuid})) "
            + "                    AND (data_diag.concept_id = ${typeTestHIVConcept} AND data_diag.obs_datetime <= :cohortEndDate) "
            + "                GROUP BY p.patient_id "
            + "                )  AS data_diagnostico ON  data_diagnostico.patient_id = p.patient_id "
            + "    WHERE  "
            + "        p.voided =0 "
            + "        AND e.voided =0 "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_datetime = data_diagnostico.dd_date "
            + "        AND e.encounter_datetime <= :endDate "
            + "    GROUP BY p.patient_id "
            + ") AS final";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public CohortDefinition getAllPatients() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Numerator - IMER1 - All");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "ALL",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getAllPatients(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getE(), "endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getF(), "cohortEndDate=${endDate-1m},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("ALL AND (E OR F)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>Pregnant Women: A and B and NOT C and NOT D and (E or F)
   * </ul>
   *
   * @return
   */
  public CohortDefinition getPatientWhoArePregnant() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Numerator - IMER1 - pregnants");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "pregnant",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getPregnantWomen(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getE(), "endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getF(), "cohortEndDate=${endDate-1m},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("pregnant AND (E OR F)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>Breastfeeding Women A and NOT B and C and NOT D and (E or F)
   * </ul>
   *
   * @return
   */
  public CohortDefinition getPatientWhoAreBreastFeeding() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Numerator - IMER1 - BreastFeeding");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getBreastfeedingWoman(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getE(), "endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getF(), "cohortEndDate=${endDate-1m},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("breastfeeding AND (E OR F)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>Children (0-14 years) A and NOT B and NOT C NOT D and (E or F) and Age < 15 years*
   * </ul>
   *
   * @return
   */
  public CohortDefinition getChildrenPatients() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Numerator - IMER1 - BreastFeeding");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "children",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getChildreen(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getE(), "endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getF(), "cohortEndDate=${endDate-1m},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("children AND (E OR F)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>Adults (15+ years) A and NOT B and NOT C NOT D and (E or F) and Age>= 15 years*
   * </ul>
   *
   * @return
   */
  public CohortDefinition getAdultsPatients() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("Numerator - IMER1 - BreastFeeding");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.addSearch(
        "adults",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getAdults(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getE(), "endDate=${endDate-1m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            getF(), "cohortEndDate=${endDate-1m},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("adults AND (E OR F)");

    return compositionCohortDefinition;
  }
}
