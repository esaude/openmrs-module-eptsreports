package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.ConsultationUntilEndDateAfterStartingART;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.EncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.SecondFollowingEncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.ThirdFollowingEncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovement2020Queries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QualityImprovement2020CohortQueries {

  private GenericCohortQueries genericCohortQueries;

  private HivMetadata hivMetadata;

  private CommonMetadata commonMetadata;

  private GenderCohortQueries genderCohortQueries;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private CommonCohortQueries commonCohortQueries;

  private TbMetadata tbMetadata;

  private QualityImprovement2020Queries qualityImprovement2020Queries;

  private final String MAPPING = "startDate=${startDate},endDate=${endDate},location=${location}";

  @Autowired
  public QualityImprovement2020CohortQueries(
      GenericCohortQueries genericCohortQueries,
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata,
      GenderCohortQueries genderCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      CommonCohortQueries commonCohortQueries,
      TbMetadata tbMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
    this.genderCohortQueries = genderCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
    this.tbMetadata = tbMetadata;
  }

  /**
   * <b>MQC3D1</b>: Melhoria de Qualidade Category 3 Deniminator 1 <br>
   * <i> A and not B</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion Period (startDateRevision
   *       and endDateInclusion)
   *   <li>B - Exclude all transferred in patients as following:
   *       <ul>
   *         <li>All patients registered in Ficha Resumo (Encounter Type Id= 53) and marked as
   *             Transferred-in (“Transfer from other facility” concept Id 1369 = “Yes” concept id
   *             1065) in TARV (“Type of Patient Transferred from” concept id 6300 = “ART” concept
   *             id 6276)
   *             <p>< Note: the both concepts 1369 and 6300 should be recorded in the same encounter
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC3D1() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Patients Who Initiated ART During The Inclusion Period");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = this.genericCohortQueries.getStartedArtOnPeriod(false, true);

    CohortDefinition transferredIn = this.getTransferredInPatients();

    compositionCohortDefinition.addSearch(
        "A",
        EptsReportUtils.map(
            startedART, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.setCompositionString("A AND NOT B");

    return compositionCohortDefinition;
  }

  private CohortDefinition getTransferredInPatients() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("transferred in patients");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "transferFromOtherFacilityConcept",
        commonMetadata.getTransferFromOtherFacilityConcept().getConceptId());
    map.put("patientFoundYesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put(
        "typeOfPatientTransferredFrom",
        hivMetadata.getTypeOfPatientTransferredFrom().getConceptId());
    map.put("artStatus", hivMetadata.getArtStatus().getConceptId());

    String query =
        "SELECT  p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id=p.patient_id "
            + "    INNER JOIN obs obs1 "
            + "        ON obs1.encounter_id=e.encounter_id "
            + "    INNER JOIN obs obs2 "
            + "        ON obs2.encounter_id=e.encounter_id "
            + "WHERE p.voided =0  "
            + "    AND e.voided = 0 "
            + "    AND obs1.voided =0 "
            + "    AND obs2.voided =0 "
            + "    AND e.encounter_type = ${masterCardEncounterType}  "
            + "    AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "    AND e.location_id = :location "
            + "    AND obs1.concept_id = ${transferFromOtherFacilityConcept} AND obs1.value_coded = ${patientFoundYesConcept} "
            + "    AND obs2.concept_id = ${typeOfPatientTransferredFrom} AND obs2.value_coded = ${artStatus} ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC4D1</b>: Melhoria de Qualidade Category 4 Deniminator 1 <br>
   * <i> A and NOT B and NOT C and Age < 15 years</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion Period (startDateRevision
   *       and endDateInclusion)
   *   <li>B - Select all female patients who are pregnant as following:
   *       <ul>
   *         <li>All patients registered in Ficha Clínica (encounter type=53) with
   *             “Gestante”(concept_id 1982) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   *   <li>C- Select all female patients who are breastfeeding as following:
   *       <ul>
   *         <li>all patients registered in Ficha Clinica (encounter type=53) with
   *             “Lactante”(concept_id 6332) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC4D1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MCC4D1 Patients");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingStates(
                hivMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingStates(
                hivMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("FEMALE", EptsReportUtils.map(genderCohortQueries.femaleCohort(), ""));
    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("(A AND FEMALE AND CHILDREN) AND NOT (B OR C)");
    return cd;
  }

  /**
   * <b>MQC4D2</b>: Melhoria de Qualidade Category 4 Deniminator 2 <br>
   * <i> A and B and NOT C</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion Period (startDateRevision
   *       and endDateInclusion)
   *   <li>B - Select all female patients who are pregnant as following:
   *       <ul>
   *         <li>All patients registered in Ficha Clínica (encounter type=53) with
   *             “Gestante”(concept_id 1982) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   *   <li>C- Select all female patients who are breastfeeding as following:
   *       <ul>
   *         <li>all patients registered in Ficha Clinica (encounter type=53) with
   *             “Lactante”(concept_id 6332) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC4D2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MCC4D2 Patients");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingStates(
                hivMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingStates(
                hivMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("FEMALE", EptsReportUtils.map(genderCohortQueries.femaleCohort(), ""));

    cd.setCompositionString("(A AND FEMALE) AND NOT (B OR C)");
    return cd;
  }

  private CohortDefinition getPregnantAndBreastfeedingStates(int conceptIdQn, int conceptIdAns) {
    Map<String, Integer> map = new HashMap<>();
    map.put("conceptIdQn", conceptIdQn);
    map.put("conceptIdAns", conceptIdAns);
    map.put(
        "fichaClinicaEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String query =
        "SELECT "
            + "   p.patient_id "
            + "   FROM "
            + "   patient p "
            + "   INNER JOIN "
            + "      encounter e "
            + "      ON p.patient_id = e.patient_id "
            + "   INNER JOIN "
            + "      obs o "
            + "      ON o.encounter_id = e.encounter_id "
            + "  WHERE "
            + "   p.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND e.encounter_type = ${fichaClinicaEncounterType} "
            + "   AND o.concept_id = ${conceptIdQn} "
            + "   AND o.value_coded = ${conceptIdAns} "
            + "   AND e.encounter_datetime BETWEEN :startDate AND :endDate";

    return genericCohortQueries.generalSql(
        "Pregnant or breastfeeding females", stringSubstitutor.replace(query));
  }

  public CohortDefinition getMQC4N1() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.setName("Numerator for Category 4");

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getLastClinicalConsultationClassficacaoDesnutricao(), MAPPING));

    compositionCohortDefinition.addSearch("MQC4D1", EptsReportUtils.map(getMQC4D1(), MAPPING));

    compositionCohortDefinition.setCompositionString("MQC4D1 AND E");

    return compositionCohortDefinition;
  }

  public CohortDefinition getMQC4N2() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.setName("Numerator for Category 4");

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(getLastClinicalConsultationClassficacaoDesnutricao(), MAPPING));

    compositionCohortDefinition.addSearch("MQC4D2", EptsReportUtils.map(getMQC4D2(), MAPPING));

    compositionCohortDefinition.setCompositionString("MQC4D2 AND E");

    return compositionCohortDefinition;
  }

  private CohortDefinition getLastClinicalConsultationClassficacaoDesnutricao() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "last clinical consultation registered CLASSIFICAÇÃO DE DESNUTRIÇÃO");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "classificationOfMalnutritionConcept",
        commonMetadata.getClassificationOfMalnutritionConcept().getConceptId());
    map.put("normalConcept", hivMetadata.getNormalConcept().getConceptId());
    map.put(
        "malnutritionLightConcept", commonMetadata.getMalnutritionLightConcept().getConceptId());
    map.put("malnutritionConcept", hivMetadata.getMalnutritionConcept().getConceptId());
    map.put(
        "chronicMalnutritionConcept", hivMetadata.getChronicMalnutritionConcept().getConceptId());

    String query =
        "SELECT max_date.patient_id "
            + "FROM "
            + "   ( "
            + "    SELECT p.patient_id, MAX(encounter_datetime) AS max_encounter_date "
            + "    FROM patient p "
            + "        INNER JOIN encounter e "
            + "            ON e.patient_id = p.patient_id "
            + "        INNER JOIN obs o "
            + "            ON o.encounter_id = e.encounter_id "
            + "    WHERE "
            + "        p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType} "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_datetime  "
            + "            BETWEEN :startDate AND :endDate "
            + "        AND o.concept_id = ${classificationOfMalnutritionConcept} "
            + "        AND o.value_coded IN (${normalConcept}, ${malnutritionLightConcept}, ${malnutritionConcept}, ${chronicMalnutritionConcept}) "
            + "    GROUP BY patient_id "
            + "    ) max_date";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /*
   *
   * All  patients the first clinical consultation with nutricional state equal
   * to “DAM” or “DAG” occurred during the revision period and
   * “Apoio/Educação Nutricional” = “ATPU” or “SOJA” in
   * the same clinical consultation
   *
   */

  public CohortDefinition getPatientsWithNutritionalStateAndNutritionalSupport() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Nutritional Calssification");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6336", commonMetadata.getClassificationOfMalnutritionConcept().getConceptId());
    map.put("1844", hivMetadata.getChronicMalnutritionConcept().getConceptId());
    map.put("68", hivMetadata.getMalnutritionConcept().getConceptId());
    map.put("2152", commonMetadata.getNutritionalSupplememtConcept().getConceptId());
    map.put("6143", commonMetadata.getATPUSupplememtConcept().getConceptId());
    map.put("2151", commonMetadata.getSojaSupplememtConcept().getConceptId());

    String query =
        " SELECT "
            + " p.patient_id "
            + " FROM "
            + " patient p "
            + "     INNER JOIN "
            + " (SELECT  "
            + "     p.patient_id, MIN(e.encounter_datetime) "
            + " FROM "
            + "     patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN obs o1 ON o1.encounter_id = o.encounter_id "
            + " WHERE "
            + "     p.voided = 0 AND e.voided = 0 "
            + "         AND o.voided = 0 "
            + "         AND o1.voided = 0 "
            + "         AND e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${6336} "
            + "         AND o.value_coded IN (${1844} , ${68}) "
            + "         AND o1.concept_id = ${2152} "
            + "         AND o1.value_coded IN (${6143} , ${2151}) "
            + "         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY p.patient_id) nut ON p.patient_id = nut.patient_id; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /*
   *
   * All  patients ll patients with a clinical consultation(encounter type 6) during
   * the Revision period with the following conditions:
   *
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Fim” (concept_id 1267)
   * Encounter_datetime between startDateRevision and endDateRevision and:
   *
   *  - Encounter_datetime(from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Fim” (concept_id 1267))
   * MINUS
   * - Encounter_datetime (from the colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Fim” (concept_id 1267))
   * between 6 months and 9 months
   *
   */

  public CohortDefinition getPatientsWithProphylaxyDuringRevisionPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Prophylaxy Treatment within Revision Period");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  "
            + " p.person_id  "
            + " FROM  "
            + " person p  "
            + "     INNER JOIN  "
            + " encounter e ON p.person_id = e.patient_id  "
            + "     INNER JOIN  "
            + " obs o ON e.encounter_id = o.encounter_id  "
            + "     INNER JOIN  "
            + " (SELECT   "
            + "     p.person_id, MAX(e.encounter_datetime) AS encounter  "
            + " FROM  "
            + "     person p  "
            + " INNER JOIN encounter e ON p.person_id = e.patient_id  "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + " WHERE  "
            + "     e.location_id = :location  "
            + "         AND e.encounter_type = ${6}  "
            + "         AND o.concept_id = ${6122}  "
            + "         AND o.value_coded IN (${1256})  "
            + "         AND e.encounter_datetime >= :startDate  "
            + "         AND e.encounter_datetime <= :endDate  "
            + "         AND p.voided = 0  "
            + "         AND e.voided = 0  "
            + "         AND o.voided = 0  "
            + " GROUP BY p.person_id) AS last ON p.person_id = last.person_id  "
            + " WHERE  "
            + " e.location_id = :location  "
            + " AND e.encounter_type = ${6}  "
            + " AND o.concept_id = ${6122}  "
            + " AND o.value_coded IN (${1267})  "
            + " AND e.encounter_datetime >= :startDate  "
            + " AND e.encounter_datetime <= :endDate  "
            + " AND DATEDIFF(DATE(last.encounter),DATE(e.encounter_datetime)) between 180 and 270  "
            + " AND p.voided = 0  "
            + " AND e.voided = 0  "
            + " AND o.voided = 0; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /*
   *
   * All  patients ll patients with a clinical consultation(encounter type 6) during
   * the Revision period with the following conditions:
   *
   * - with “Diagnótico TB activo” (concept_id 23761) value coded “SIM”(concept id 1065)
   * Encounter_datetime between:
   *
   *  - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * AND
   * - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * PLUS
   * 9 MONTHS
   *
   */

  public CohortDefinition getPatientsWithTBDiagActive() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Diagnosis Active");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("23761", tbMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        "  SELECT "
            + "     p.person_id "
            + " FROM "
            + "     person p "
            + "         INNER JOIN "
            + "     encounter e ON p.person_id = e.patient_id "
            + "         INNER JOIN "
            + "     obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT  "
            + "         p.person_id, MAX(e.encounter_datetime) AS encounter "
            + "     FROM "
            + "         person p "
            + "     INNER JOIN encounter e ON p.person_id = e.patient_id "
            + "     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     WHERE "
            + "         e.location_id = :location "
            + "             AND e.encounter_type = ${6} "
            + "             AND o.concept_id = ${6122} "
            + "             AND o.value_coded IN (${1256}) "
            + "             AND e.encounter_datetime >= :startDate "
            + "             AND e.encounter_datetime <= :endDate "
            + "             AND p.voided = 0 "
            + "             AND e.voided = 0 "
            + "             AND o.voided = 0 "
            + "     GROUP BY p.person_id) AS last ON p.person_id = last.person_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23761} "
            + "         AND o.value_coded IN (${1065}) "
            + "         AND e.encounter_datetime >= :startDate "
            + "         AND e.encounter_datetime <= :endDate "
            + "         AND e.encounter_datetime BETWEEN last.encounter AND DATE_ADD(last.encounter, INTERVAL 9 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0; ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /*
   *
   * All  patients ll patients with a clinical consultation(encounter type 6) during
   * the Revision period with the following conditions:
   *
   * -  “TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” or “NÃO”(concept_id IN [1065, 1066]) and
   * Encounter_datetime between:
   *
   *  - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * AND
   * - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * PLUS
   * 9 MONTHS
   *
   */

  public CohortDefinition getPatientsWithTBSymtoms() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Diagnosis Active");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("23758", tbMetadata.getHasTbSymptomsConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("1066", hivMetadata.getNoConcept().getConceptId());

    String query =
        "  SELECT "
            + "     p.person_id "
            + " FROM "
            + "     person p "
            + "         INNER JOIN "
            + "     encounter e ON p.person_id = e.patient_id "
            + "         INNER JOIN "
            + "     obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT  "
            + "         p.person_id, MAX(e.encounter_datetime) AS encounter "
            + "     FROM "
            + "         person p "
            + "     INNER JOIN encounter e ON p.person_id = e.patient_id "
            + "     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     WHERE "
            + "         e.location_id = :location "
            + "             AND e.encounter_type = ${6} "
            + "             AND o.concept_id = ${6122} "
            + "             AND o.value_coded IN (${1256}) "
            + "             AND e.encounter_datetime >= :startDate "
            + "             AND e.encounter_datetime <= :endDate "
            + "             AND p.voided = 0 "
            + "             AND e.voided = 0 "
            + "             AND o.voided = 0 "
            + "     GROUP BY p.person_id) AS last ON p.person_id = last.person_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23758} "
            + "         AND o.value_coded IN (${1065},${1066}) "
            + "         AND e.encounter_datetime >= :startDate "
            + "         AND e.encounter_datetime <= :endDate "
            + "         AND e.encounter_datetime BETWEEN last.encounter AND DATE_ADD(last.encounter, INTERVAL 9 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /*
   *
   * All  patients ll patients with a clinical consultation(encounter type 6) during
   * the Revision period with the following conditions:
   *
   * -  “TRATAMENTO DE TUBERCULOSE”(concept_id 1268) value coded “Inicio” or “Continua” or
   * “Fim”(concept_id IN [1256, 1257, 1267]) “Data Tratamento TB” (obs datetime 1268) between:
   *
   *  - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * AND
   * - Encounter_datetime (from the last colinical consultation with
   * “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256))
   * PLUS
   * 9 MONTHS
   *
   */
  public CohortDefinition getPatientsWithTBTreatment() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Diagnosis Active");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6122", hivMetadata.getIsoniazidUsageConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1268", tbMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  "
            + "     p.person_id  "
            + " FROM  "
            + "     person p  "
            + "         INNER JOIN  "
            + "     encounter e ON p.person_id = e.patient_id  "
            + "         INNER JOIN  "
            + "     obs o ON e.encounter_id = o.encounter_id  "
            + "     INNER JOIN (SELECT   "
            + "         p.person_id, MAX(e.encounter_datetime) AS encounter  "
            + "     FROM  "
            + "         person p  "
            + "     INNER JOIN encounter e ON p.person_id = e.patient_id  "
            + "     INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "     WHERE  "
            + "         e.location_id = :location  "
            + "             AND e.encounter_type = ${6}  "
            + "             AND o.concept_id = ${6122}  "
            + "             AND o.value_coded IN (${1256})  "
            + "             AND e.encounter_datetime >= :startDate  "
            + "             AND e.encounter_datetime <= :endDate  "
            + "             AND p.voided = 0  "
            + "             AND e.voided = 0  "
            + "             AND o.voided = 0  "
            + "     GROUP BY p.person_id) AS last ON last.person_id = p.person_id  "
            + " WHERE  "
            + "     e.location_id = :location  "
            + "         AND e.encounter_type = ${6}  "
            + "         AND o.concept_id = ${1268}  "
            + "         AND o.value_coded IN (${1256} , ${1257}, ${1267})  "
            + "         AND e.encounter_datetime >= :startDate  "
            + "         AND e.encounter_datetime <= :endDate  "
            + "         AND DATE(o.obs_datetime) between DATE(last.encounter) AND DATE(DATE_ADD(last.encounter, INTERVAL 9 MONTH))  "
            + "         AND p.voided = 0  "
            + "         AND e.voided = 0  "
            + "         AND o.voided = 0;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ5A</b>: Melhoria de Qualidade Category 5 Criancas <br>
   * <i> DENOMINATOR: (A AND B) AND NOT (C OR D OR E)</i> <br>
   * <i> NOMINATOR: (A AND B) AND NOT (C OR D OR E) AND F</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B - Filter all patients with nutritional state equal to “DAM” or “DAG” registered on a
   *       clinical consultation during the period
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients
   *   <li>
   *   <li>F - F - Filter all patients with “Apoio/Educação Nutricional” equals to “ATPU” or “SOJA”
   *       in the same clinical consultation where“Grau da Avaliação Nutricional” equals to “DAM” or
   *       “DAG” during the revision period, clinical consultation >= startDateRevision and
   *       <=endDateRevision
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ5A(Boolean den) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den) {
      compositionCohortDefinition.setName("% de crianças em TARV com desnutrição (DAM ou DAG)");
    } else {
      compositionCohortDefinition.setName(
          "% de crianças em TARV com desnutrição (DAM ou DAG) e com registo de prescrição de suplementação ou tratamento nutricional");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition nutritionalClass =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "first",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            commonMetadata.getClassificationOfMalnutritionConcept(),
            Arrays.asList(
                hivMetadata.getChronicMalnutritionConcept(), hivMetadata.getMalnutritionConcept()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition nutSupport = getPatientsWithNutritionalStateAndNutritionalSupport();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(nutritionalClass, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(nutSupport, MAPPING));

    if (den) {
      compositionCohortDefinition.setCompositionString("(A AND B) AND NOT (C OR D OR E)");
    } else {
      compositionCohortDefinition.setCompositionString("(A AND B) AND NOT (C OR D OR E) AND F");
    }
    return compositionCohortDefinition;
  }

  /**
   * <b>MQ5B</b>: Melhoria de Qualidade Category 5 MG <br>
   * <i> DENOMINATOR: (A AND B AND C) AND NOT (D OR E)</i> <br>
   * <i> NOMINATOR: (A AND B AND C) AND NOT (D OR E) AND F</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B - Filter all patients with nutritional state equal to “DAM” or “DAG” registered on a
   *       clinical consultation during the period
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients
   *   <li>
   *   <li>F - F - Filter all patients with “Apoio/Educação Nutricional” equals to “ATPU” or “SOJA”
   *       in the same clinical consultation where“Grau da Avaliação Nutricional” equals to “DAM” or
   *       “DAG” during the revision period, clinical consultation >= startDateRevision and
   *       <=endDateRevision
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ5B(Boolean den) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den) {
      compositionCohortDefinition.setName(
          "% de mulheres gravidas em TARV com desnutrição (DAM ou DAG)");
    } else {
      compositionCohortDefinition.setName(
          "% de MG em TARV com desnutrição (DAM ou DAG) e com registo de prescrição de suplementação ou tratamento nutricional");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition nutritionalClass =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "first",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            commonMetadata.getClassificationOfMalnutritionConcept(),
            Arrays.asList(
                hivMetadata.getChronicMalnutritionConcept(), hivMetadata.getMalnutritionConcept()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition nutSupport = getPatientsWithNutritionalStateAndNutritionalSupport();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(nutritionalClass, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(nutSupport, MAPPING));

    if (den) {
      compositionCohortDefinition.setCompositionString("(A AND B AND C) AND NOT (D OR E)");
    } else {
      compositionCohortDefinition.setCompositionString("(A AND B AND C) AND NOT (D OR E) AND F");
    }

    return compositionCohortDefinition;
  }

  /**
   * <b>MQ6</b>: Melhoria de Qualidade Category 6 <br>
   * <i> DENOMINATOR 1: A AND NOT (B OR C OR D OR E)</i> <br>
   * <i> DENOMINATOR 2: A AND NOT (B OR C OR D OR E)</i> <br>
   * <i> DENOMINATOR 3: (A AND C) AND NOT (B OR D OR E)</i> <br>
   * <i> DENOMINATOR 4: A AND D AND NOT (B OR C OR E)</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B - B - Filter all patients with the last clinical consultation(encounter type 6) with
   *       “Diagnótico TB activo” (concept id 23761) and value coded “SIM”(concept id 1065) and
   *       Encounter_datetime between startDateInclusion and endDateRevision
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients during the inclusion period
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ6A(Integer den) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den == 1) {
      compositionCohortDefinition.setName(
          "% de adultos HIV+ em TARV rastreados para TB na última consulta clínica");
    } else if (den == 2) {
      compositionCohortDefinition.setName(
          "% de crianças HIV+ em TARV rastreadas para TB na última consulta clínica");
    } else if (den == 3) {
      compositionCohortDefinition.setName(
          "% de mulheres grávidas HIV+ rastreadas para TB na última consulta clínica");
    } else if (den == 4) {
      compositionCohortDefinition.setName(
          "% de mulheres lactantes HIV+ rastreadas para TB  na última consulta");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "last",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(tbActive, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    if (den == 1 || den == 2) {
      compositionCohortDefinition.setCompositionString("A AND NOT (B OR C OR D OR E)");
    } else if (den == 3) {
      compositionCohortDefinition.setCompositionString("(A AND C) AND NOT (B OR D OR E)");
    } else if (den == 4) {
      compositionCohortDefinition.setCompositionString("A AND D AND NOT (B OR C OR E)");
    }
    return compositionCohortDefinition;
  }

  /**
   * <b>MQ7</b>: Melhoria de Qualidade Category 7 <br>
   * <i> DENOMINATOR 1: A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 2: (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 3: A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 4: (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 5: (A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 6: (A AND B4 AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B1 - Filter all patients with a clinical consultation(encounter type 6) with “Diagnótico
   *       TB activo” (concept id 23761) and value coded “SIM”(concept id 1065) and
   *       Encounter_datetime between startDateInclusion and endDateRevision
   *   <li>
   *   <li>
   *   <li>B2 - Filter all patients with a clinical consultation(encounter type 6) with “TEM
   *       SINTOMAS DE TB” (concept_id 23758) value coded “SIM” (concept_id 1065) and
   *       Encounter_datetime between startDateInclusion and endDateInclusion
   *   <li>
   *   <li>
   *   <li>B3 - Filter all patients with a clinical consultation(encounter type 6) with “TRATAMENTO
   *       DE TUBERCULOSE”(concept_id 1268) value coded “Inicio” or “Continua” or “Fim” (concept_id
   *       IN [1256, 1257, 1267]) Encounter_datetime between startDateInclusion and endDateInclusion
   *   <li>
   *   <li>
   *   <li>B4 - Filter all patients with a clinical consultation(encounter type 6) with “PROFILAXIA
   *       COM ISONIAZIDA”(concept_id 6122) value coded “Inicio” (concept_id 1256)
   *       Encounter_datetime between startDateInclusion and endDateInclusion
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients during the inclusion period
   *   <li>
   *   <li>F - Filter all patients with the last clinical consultation(encounter type 6) with
   *       “Diagnótico TB activo” (concept id 23761) and value coded “SIM”(concept id 1065) and
   *       Encounter_datetime between startDateInclusion and endDateRevision
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ7A(Integer den) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den == 1 || den == 3) {
      compositionCohortDefinition.setName("A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 2 || den == 4) {
      compositionCohortDefinition.setName(
          "(A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 5) {
      compositionCohortDefinition.setName("(A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (den == 6) {
      compositionCohortDefinition.setName(
          "(A AND B4 AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTBTreatmentPlanConcept(),
            Arrays.asList(
                tbMetadata.getStartDrugsConcept(),
                hivMetadata.getContinueRegimenConcept(),
                hivMetadata.getCompletedConcept()),
            null,
            null);

    CohortDefinition tbProphilaxy =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getIsoniazidUsageConcept(),
            Collections.singletonList(hivMetadata.getStartDrugsConcept()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(tbActive, MAPPING));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(tbSymptoms, MAPPING));

    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(tbTreatment, MAPPING));

    compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(tbProphilaxy, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transferOut, MAPPING));

    if (den == 1 || den == 3) {
      compositionCohortDefinition.setCompositionString(
          "A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 2 || den == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 5) {
      compositionCohortDefinition.setCompositionString(
          "(A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (den == 6) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B4 AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    }
    return compositionCohortDefinition;
  }

  /**
   * <b>MQ7</b>: Melhoria de Qualidade Category 7 <br>
   * <i> DENOMINATOR 1: A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 2: (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 3: A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 4: (A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 5: (A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)</i> <br>
   * <i> DENOMINATOR 6: (A AND B4 AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)</i> <br>
   *
   * <ul>
   *   <li>G - Filter all patients with the last clinical consultation(encounter type 6) with
   *       “PROFILAXIA COM ISONIAZIDA”(concept_id 6122) value coded “Fim” (concept_id 1267)
   *       Encounter_datetime between startDateRevision and endDateRevision and
   *       Encounter_datetime(the most recent from B4) minus Encounter_datetime(the most recent from
   *       G) between 6 months and 9 months
   *   <li>
   *   <li>
   *   <li>H - Filter all patients with a clinical consultation(encounter type 6) with “Diagnótico
   *       TB activo” (concept_id 23761) value coded “SIM”(concept id 1065) during the treatment
   *       period: Encounter_datetime between Encounter_datetime(the most recent from B4) and
   *       Encounter_datetime(the most recent from B4) + 9 months
   *   <li>
   *   <li>
   *   <li>I - Filter all patients with a clinical consultation(encounter type 6) during the
   *       Inclusion period with the following conditions: “TEM SINTOMAS DE TB” (concept_id 23758)
   *       value coded “SIM” or “NÃO”(concept_id IN [1065, 1066]) and Encounter_datetime between
   *       Encounter_datetime(the most recent from B4) and Encounter_datetime(the most recent from
   *       B4) + 9 months
   *   <li>
   *   <li>
   *   <li>J - Filter all patients with a clinical consultation(encounter type 6) during the
   *       Inclusion period with the following conditions: “TRATAMENTO DE TUBERCULOSE”(concept_id
   *       1268) value coded “Inicio” or “Continua” or “Fim”(concept_id IN [1256, 1257, 1267]) “Data
   *       Tratamento TB” (obs datetime 1268) between Encounter_datetime(the most recent from B4)
   *       and Encounter_datetime(the most recent from B4) + 9 months
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ7B(Integer num) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setName(
          "(A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 2 || num == 4) {
      compositionCohortDefinition.setName(
          "(A AND B4 AND G) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR I OR J)");
    } else if (num == 5) {
      compositionCohortDefinition.setName(
          "(A AND C AND B4) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (num == 6) {
      compositionCohortDefinition.setName(
          "(A AND B4 AND C AND G) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition tbActive =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getActiveTBConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbSymptoms =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getHasTbSymptomsConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition tbTreatment =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTBTreatmentPlanConcept(),
            Arrays.asList(
                tbMetadata.getStartDrugsConcept(),
                hivMetadata.getContinueRegimenConcept(),
                hivMetadata.getCompletedConcept()),
            null,
            null);

    CohortDefinition tbProphilaxy =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            false,
            "once",
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getIsoniazidUsageConcept(),
            Collections.singletonList(hivMetadata.getStartDrugs()),
            null,
            null);

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition tbProphylaxyOnPeriod = getPatientsWithProphylaxyDuringRevisionPeriod();

    CohortDefinition tbDiagOnPeriod = getPatientsWithTBDiagActive();

    CohortDefinition tbSymptomsOnPeriod = getPatientsWithTBSymtoms();

    CohortDefinition tbTreatmentOnPeriod = getPatientsWithTBTreatment();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(tbActive, MAPPING));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(tbSymptoms, MAPPING));

    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(tbTreatment, MAPPING));

    compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(tbProphilaxy, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transferOut, MAPPING));

    compositionCohortDefinition.addSearch("G", EptsReportUtils.map(tbProphylaxyOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(tbDiagOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch("I", EptsReportUtils.map(tbSymptomsOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch("J", EptsReportUtils.map(tbTreatmentOnPeriod, MAPPING));

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B4) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 2 || num == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B4 AND G) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR I OR J)");
    } else if (num == 5) {
      compositionCohortDefinition.setCompositionString(
          "(A AND C AND B4) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (num == 6) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B4 AND C AND G) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR I OR J)");
    }
    return compositionCohortDefinition;
  }

  /**
   * <b>MQC11</b>: Melhoria de Qualidade Category 11 Denominator <br>
   * <i> DENOMINATORS: A,B1,B2,B3,C,D and E</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B1 - Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   *       TERAPEUTICA
   *   <li>
   *   <li>B2-Select all patients from Ficha Clinica (encounter type 6) with “Carga Viral”
   *       registered with numeric value > 1000
   *   <li>
   *   <li>B3-Filter all patients with clinical consultation (encounter type 6) with concept
   *       “GESTANTE” and value coded “SIM”
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients
   *   <li>
   *   <li>F - All Transferred Out patients
   * </ul>
   *
   * @return CohortDefinition
   * @params indicatorFlag A to G For inicator 11.1 to 11.7 respectively
   */
  public CohortDefinition getMQC11DEN(String indicatorFlag) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "% adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMQC3D1(); // A

    CohortDefinition patientsFromFichaClinicaLinhaTerapeutica =
        getPatientsFromFichaClinicaDenominatorB("B1");

    CohortDefinition patientsFromFichaClinicaCargaViral =
        getPatientsFromFichaClinicaDenominatorB("B2_11");

    CohortDefinition patientsWithClinicalConsultation = getPatientsWithClinicalConsultationB3();

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch(
        "B1", EptsReportUtils.map(patientsFromFichaClinicaLinhaTerapeutica, MAPPING));

    compositionCohortDefinition.addSearch(
        "B2", EptsReportUtils.map(patientsFromFichaClinicaCargaViral, MAPPING));

    compositionCohortDefinition.addSearch(
        "B3", EptsReportUtils.map(patientsWithClinicalConsultation, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING));

    if (indicatorFlag.equals("A") || indicatorFlag == "E" || indicatorFlag.equals("F"))
      compositionCohortDefinition.setCompositionString("A AND NOT (C OR D OR E OR F)");
    if (indicatorFlag.equals("B") || indicatorFlag.equals("G"))
      compositionCohortDefinition.setCompositionString("(B1 AND B2) AND NOT (C OR D OR E OR F)");
    if (indicatorFlag.equals("C"))
      compositionCohortDefinition.setCompositionString("(A AND B3 AND C) AND NOT (D OR E OR F)");
    if (indicatorFlag.equals("D"))
      compositionCohortDefinition.setCompositionString("(B1 AND B3 AND C) AND NOT (D OR E OR F)");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQ12</b>: Melhoria de Qualidade Category 12 Denominator Part 2 <br>
   * <i> DENOMINATORS: A,B1,B1E,B2,B2E,C,D and E</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the period (startDateInclusion =
   *       endDateRevision - 14 months and endDateInclusion = endDateRevision - 11 months)
   *   <li>
   *   <li>B1- Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   *       TERAPEUTICA”(Concept id 21151) during the Inclusion period (startDateInclusion =
   *       endDateRevision - 14 months and endDateInclusion = endDateRevision - 11 months) and the
   *       value coded is “PRIMEIRA LINHA”(Concept id 21150)
   *   <li>
   *   <li>B1E- Select all patients from Ficha Clinica (encounter type 6) who have “LINHA
   *       TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA LINHA”(Concept
   *       id 21150) during the period (startDate = endDateRevision - 14 months and endDateRevision)
   *   <li>
   *   <li>B2- Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   *       TERAPEUTICA”(Concept id 21151) during the Inclusion period (startDateInclusion =
   *       endDateRevision - 14 months and endDateInclusion = endDateRevision - 11 months) and the
   *       value coded is “SEGUNDA LINHA”(Concept id 21148)
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients
   *   <li>
   *   <li>F - All Transferred Out patients
   * </ul>
   *
   * @return CohortDefinition
   * @params indicatorFlag A to E For inicator 12.3 to 12.12 respectively
   */
  public CohortDefinition getMQC12P2DEN(String indicatorFlag) {
    String mapping1 = "startDate=${endDate-14m},endDate=${endDate},location=${location}";
    String mapping2 = "startDate=${endDate-14m},endDate=${endDate-11m},location=${location}";
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (indicatorFlag.equals("A"))
      compositionCohortDefinition.setName(
          "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás sem registo de saidas");
    if (indicatorFlag.equals("B"))
      compositionCohortDefinition.setName(
          "Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrá");
    if (indicatorFlag.equals("C"))
      compositionCohortDefinition.setName(
          "Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás");
    if (indicatorFlag.equals("D"))
      compositionCohortDefinition.setName(
          "Crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás");
    if (indicatorFlag.equals("E"))
      compositionCohortDefinition.setName(
          "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition b1 = getPatientsFromFichaClinicaDenominatorB("B1");

    CohortDefinition b1E = getPatientsFromFichaClinicaDenominatorB("B1E");

    CohortDefinition b2 = getPatientsFromFichaClinicaDenominatorB("B2_12");

    CohortDefinition b2E = getPatientsFromFichaClinicaDenominatorB("B2E");

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transfOut = commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, mapping2));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, mapping2));

    compositionCohortDefinition.addSearch("B1E", EptsReportUtils.map(b1E, mapping1));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, mapping2));

    compositionCohortDefinition.addSearch("B2E", EptsReportUtils.map(b2E, mapping1));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING));

    if (indicatorFlag.equals("A") || indicatorFlag.equals("C"))
      compositionCohortDefinition.setCompositionString("(A AND B1) NOT (C OR D OR E)");
    if (indicatorFlag.equals("B") || indicatorFlag.equals("D"))
      compositionCohortDefinition.setCompositionString("(A AND B2) AND NOT (C OR D OR E)");
    if (indicatorFlag.equals("E"))
      compositionCohortDefinition.setCompositionString("(A AND B1 AND C) AND NOT (D OR E)");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQC11B1B2</b>: Melhoria de Qualidade Category 11 Deniminator B1 and B2 <br>
   * /** <b>MQC13</b>: Melhoria de Qualidade Category 13 Part 3 Denominator <br>
   * <i> DENOMINATORS: A,B1,B2,B3,C,D and E</i> <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>
   *   <li>B1 - B1= (BI1 and not B1E) : MUDANCA DE REGIME
   *   <li>
   *   <li>B2 = (BI2 and not B2E) - PACIENTES 2a LINHA
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>E - All transferred IN patients
   *   <li>
   *   <li>F - All Transferred Out patients
   * </ul>
   *
   * @return CohortDefinition
   * @params indicatorFlag A to F For inicator 13.2 to 13.14 accordingly to the specs
   */
  public CohortDefinition getMQC13P3DEN(String indicatorFlag) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (indicatorFlag.equals("A"))
      compositionCohortDefinition.setName(
          "Crianças  (0-4 anos de idade) que iniciaram TARV no período de inclusão");
    if (indicatorFlag.equals("B"))
      compositionCohortDefinition.setName(
          "Crianças  (5-9 anos de idade) que iniciaram TARV no período de inclusão");
    if (indicatorFlag.equals("C"))
      compositionCohortDefinition.setName(
          "Crianças  (5-9 anos de idade) que iniciaram TARV no período de inclusão");
    if (indicatorFlag.equals("D"))
      compositionCohortDefinition.setName(
          "crianças  (10-14 anos de idade) que iniciaram TARV no período de inclusão");
    if (indicatorFlag.equals("E"))
      compositionCohortDefinition.setName(
          "Adultos (15/+ anos) que iniciaram a 2a linha do TARV no período de inclusão ");
    if (indicatorFlag.equals("F"))
      compositionCohortDefinition.setName(
          "Crianças  > 2 anos que iniciaram a 2a linha do TARV no período de inclusão");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    String mapping =
        "startDate=${startDate},endDate=${endDate},less3mDate=${startDate-3m},location=${location}";

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition b1Patients = getPatientsOnRegimeChangeBI1AndNotB1E_B1();

    CohortDefinition b2Patients = getPatientsOnSecondLineBI2AndNotB2E_B2();

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1Patients, MAPPING));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2Patients, mapping));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING));

    if (indicatorFlag.equals("A")
        || indicatorFlag.equals("B")
        || indicatorFlag.equals("C")
        || indicatorFlag.equals("D"))
      compositionCohortDefinition.setCompositionString(
          "((A AND NOT E) OR B1) AND NOT (C OR D OR F))");
    if (indicatorFlag.equals("E") || indicatorFlag.equals("F"))
      compositionCohortDefinition.setCompositionString("B2 AND NOT (C OR D OR F)");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQC11B1B2</b>: Melhoria de Qualidade Category 11 Deniminator B1 and B2 <br>
   * <i> A and not B</i> <br>
   *
   * <ul>
   *   <li>B1 – Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   *       TERAPEUTICA”(Concept id 21151) during the Inclusion period (startDateInclusion and
   *       endDateInclusion) and the value coded is “PRIMEIRA LINHA”(Concept id 21150)
   *   <li>B2 – Select all patients from Ficha Clinica (encounter type 6) with “Carga Viral”
   *       (Concept id 856) registered with numeric value > 1000 during the Inclusion period
   *       (startDateInclusion and endDateInclusion)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsFromFichaClinicaDenominatorB(String key) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients From Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("encounterType", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("therapeuticLineConcept", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("firstLineConcept", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("secondLineConcept", hivMetadata.getSecondLineConcept().getConceptId());
    map.put("viralLoadConcept", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime), e.encounter_id ";
    String queryTermination =
        " GROUP BY p.patient_id) filtered ON p.patient_id = filtered.patient_id ";
    String valueQuery = "";

    if (key == "B1") {
      valueQuery =
          " AND o.concept_id = ${therapeuticLineConcept} AND o.value_coded = ${firstLineConcept} ";
    }
    if (key == "B1E") {
      valueQuery =
          " AND o.concept_id = ${therapeuticLineConcept} AND o.value_coded <> ${firstLineConcept} ";
    }
    if (key == "B2_11") {
      query = "SELECT p.patient_id ";
      valueQuery = " AND o.concept_id = ${viralLoadConcept} AND o.value_numeric > 1000 ";
      queryTermination = "";
    }
    if (key == "B2_12") {

      valueQuery =
          " AND o.concept_id = ${therapeuticLineConcept} AND o.value_coded = ${secondLineConcept} ";
    }

    if (key == "B2E") {
      valueQuery =
          " AND o.concept_id = ${therapeuticLineConcept} AND o.value_coded <> ${secondLineConcept} ";
    }

    query +=
        "FROM   patient p  "
            + "              INNER JOIN encounter e  "
            + "                        ON e.patient_id = p.patient_id  "
            + "                    JOIN obs o  "
            + "                        ON o.encounter_id = e.encounter_id  "
            + "                   WHERE  e.encounter_type = ${encounterType}  "
            + "                          AND p.voided = 0 AND e.voided = 0 "
            + "                          AND e.location_id = :location AND o.location_id = :location "
            + valueQuery
            + "                          AND e.encounter_datetime BETWEEN  :startDate AND :endDate  "
            + queryTermination;

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B3</b>: Melhoria de Qualidade Category 11 Deniminator B3 <br>
   * <i> A and not B</i> <br>
   *
   * <ul>
   *   <li>B3 – Filter all patients with clinical consultation (encounter type 6) with concept
   *       “GESTANTE” (Concept Id 1982) and value coded “SIM” (Concept Id 1065) with the
   *       Encounter_datetime = ART Start Date (from query A) during the Inclusion period
   *       (startDateInclusion and endDateInclusion)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithClinicalConsultationB3() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Clinical Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("artPickupDateConcept", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("artPickupConcept", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("yesConcept", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("arvStartDateConcept", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("pregnantConcept", commonMetadata.getPregnantConcept().getConceptId());

    String query =
        " SELECT art_tbl.patient_id  "
            + "FROM   (SELECT patient_id,  "
            + "               art_date  "
            + "        FROM   (SELECT patient_id,  "
            + "                       Min(pickup_date) AS art_date  "
            + "                FROM   (SELECT p.patient_id,  "
            + "                               pickup_date  "
            + "                        FROM   patient p  "
            + "                               INNER JOIN (SELECT p.patient_id,  "
            + "                                                  Min(ob_date.value_datetime) AS  "
            + "                                                  pickup_date  "
            + "                                           FROM   patient p  "
            + "                                                  INNER JOIN encounter e  "
            + "                                                          ON e.patient_id =  "
            + "                                                             p.patient_id  "
            + "                               INNER JOIN obs ob_date  "
            + "                                       ON ob_date.encounter_id =  "
            + "                                          e.encounter_id  "
            + "                               INNER JOIN obs ob_pickup  "
            + "                                       ON ob_pickup.encounter_id =  "
            + "                                          e.encounter_id  "
            + "                                           WHERE  e.encounter_type = ${masterCardDrugPickupEncounterType}  "
            + "                                                  AND ob_date.voided = 0  "
            + "                                                  AND ob_pickup.voided = 0  "
            + "                                                  AND e.voided = 0  "
            + "                                                  AND p.voided = 0  "
            + "                                                  AND e.location_id = :location  "
            + "                                                  AND ob_date.location_id = :location  "
            + "                                                  AND ob_pickup.location_id = :location "
            + "                                                  AND ob_date.concept_id = ${artPickupDateConcept}  "
            + "                                                  AND ob_date.value_datetime <= :endDate "
            + "                                                  AND ob_pickup.concept_id = ${artPickupConcept} "
            + "                                                  AND  ob_pickup.value_coded = ${yesConcept} "
            + "                                           GROUP  BY p.patient_id) filtered_tbl  "
            + "                                       ON filtered_tbl.patient_id = p.patient_id) first_tbl "
            + "                UNION  "
            + "                (SELECT p.patient_id,  "
            + "                        pickup_date  "
            + "                 FROM   patient p  "
            + "                        INNER JOIN (SELECT p.patient_id,  "
            + "                                           Min(o.value_datetime) AS pickup_date  "
            + "                                    FROM   patient p  "
            + "                                           INNER JOIN encounter e  "
            + "                                                   ON p.patient_id =  e.patient_id "
            + "                                           INNER JOIN obs o  "
            + "                                                   ON o.encounter_id = e.encounter_id "
            + "                                    WHERE  e.encounter_type = ${masterCardEncounterType}  "
            + "                                           AND e.voided = 0  "
            + "                                           AND e.location_id = :location AND o.location_id = :location "
            + "                                           AND o.voided = 0 AND p.voided = 0  "
            + "                                           AND o.concept_id = ${arvStartDateConcept}  "
            + "                                           AND o.value_datetime <= :endDate  "
            + "                                    GROUP  BY p.patient_id) filtered_tbl  "
            + "                                ON filtered_tbl.patient_id = p.patient_id))  "
            + "               union_tbl  "
            + "        WHERE  union_tbl.art_date BETWEEN :startDate AND :endDate) art_tbl  "
            + "       INNER JOIN (SELECT p.patient_id,  "
            + "                          e.encounter_datetime  "
            + "                   FROM   patient p  "
            + "                          INNER JOIN encounter e  "
            + "                                  ON e.patient_id = p.patient_id  "
            + "                          JOIN obs o  "
            + "                            ON o.encounter_id = e.encounter_id  "
            + "                   WHERE  e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "                          AND o.concept_id = ${pregnantConcept}  "
            + "                          AND p.voided = 0  "
            + "                          AND e.voided = 0  "
            + "                          AND e.location_id = :location AND o.location_id = :location   "
            + "                          AND o.value_coded = ${yesConcept}  "
            + "                          AND e.encounter_datetime BETWEEN :startDate AND :endDate) gest_tbl "
            + "               ON art_tbl.patient_id = gest_tbl.patient_id  "
            + "WHERE  gest_tbl.encounter_datetime = art_tbl.art_date;  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  private <T extends AbstractPatientCalculation>
      CohortDefinition getApssConsultationAfterARTstartDateOrAfterApssConsultation(
          int lowerBoundary, int upperBoundary, Class<T> clazz) {

    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(Context.getRegisteredComponents(clazz).get(0));
    cd.setName("APSS consultation after ART start date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addCalculationParameter("considerTransferredIn", false);
    cd.addCalculationParameter("considerPharmacyEncounter", true);
    cd.addCalculationParameter("lowerBoundary", lowerBoundary);
    cd.addCalculationParameter("upperBoundary", upperBoundary);

    return cd;
  }

  /**
   * G: Select all patients who have 3 APSS&PP(encounter type 35) consultation in 99 days after
   * Starting ART(The oldest date from A) as following the conditions:
   *
   * <ul>
   *   <li>G1 - FIRST consultation (Encounter_datetime (from encounter type 35)) > “ART Start Date”
   *       (oldest date from A)+20days and <= “ART Start Date” (oldest date from A)+33days AND
   *   <li>G2 - At least one consultation (Encounter_datetime (from encounter type 35)) registered
   *       during the period between “1st Consultation Date(from G1)+20days” and “1st Consultation
   *       Date(from G1)+33days” AND
   *   <li>G3 - At least one consultation (Encounter_datetime (from encounter type 35)) registered
   *       during the period between “2nd Consultation Date(from G2, the oldest)+20days” and “2nd
   *       Consultation Date(from G2, the oldest)+33days” AND
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NG() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 Numerator session G");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition firstApss =
        getApssConsultationAfterARTstartDateOrAfterApssConsultation(
            20, 33, EncounterAfterOldestARTStartDateCalculation.class);

    CohortDefinition secondApss =
        getApssConsultationAfterARTstartDateOrAfterApssConsultation(
            20, 33, SecondFollowingEncounterAfterOldestARTStartDateCalculation.class);

    CohortDefinition thirdApss =
        getApssConsultationAfterARTstartDateOrAfterApssConsultation(
            20, 33, ThirdFollowingEncounterAfterOldestARTStartDateCalculation.class);

    compositionCohortDefinition.addSearch(
        "firstApss", EptsReportUtils.map(firstApss, "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "secondApss",
        EptsReportUtils.map(secondApss, "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "thirdApss", EptsReportUtils.map(thirdApss, "onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("firstApss AND secondApss AND thirdApss");

    return compositionCohortDefinition;
  }

  public CohortDefinition getMQC11NH1() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    sqlCohortDefinition.setName("Category 11 - Numerator - H1");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN ( "
            + "                    SELECT p.patient_id, MIN(e.encounter_datetime) as  encounter_date "
            + "                    FROM patient p   "
            + "                        INNER JOIN encounter e "
            + "                            ON p.patient_id = e.patient_id "
            + "                        INNER JOIN obs o "
            + "                            ON o.encounter_id = e.encounter_id "
            + "                    WHERE p.voided = 0  "
            + "                        AND e.voided = 0 "
            + "                        AND o.voided = 0 "
            + "                        AND e.encounter_type = ${6} "
            + "                        AND e.encounter_datetime "
            + "                            BETWEEN :startDate AND :endDate "
            + "                        AND e.location_id = :location "
            + "                        AND o.concept_id = ${856} AND o.value_numeric >  1000 "
            + "                    GROUP BY p.patient_id "
            + "                ) viral_load ON viral_load.patient_id = p.patient_id "
            + " WHERE p.voided = 0  "
            + "    AND e.voided = 0 "
            + "    AND e.encounter_type = ${35} "
            + "    AND e.encounter_datetime = viral_load.encounter_date "
            + "    AND e.location_id = :location ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public CohortDefinition getMQC11NH2() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    sqlCohortDefinition.setName("Category 11 - Numerator - H2");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN ( "
            + "                 SELECT p.patient_id, e.encounter_datetime"
            + "                 FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON p.patient_id = e.patient_id "
            + "                    INNER JOIN ( "
            + "                                    SELECT p.patient_id, MIN(e.encounter_datetime) as  encounter_date "
            + "                                    FROM patient p   "
            + "                                        INNER JOIN encounter e "
            + "                                            ON p.patient_id = e.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                            ON o.encounter_id = e.encounter_id "
            + "                                    WHERE p.voided = 0  "
            + "                                        AND e.voided = 0 "
            + "                                        AND o.voided = 0 "
            + "                                        AND e.encounter_type = ${6} "
            + "                                        AND e.encounter_datetime "
            + "                                            BETWEEN :startDate AND :endDate "
            + "                                        AND e.location_id = :location "
            + "                                        AND o.concept_id = ${856} AND o.value_numeric >  1000 "
            + "                                    GROUP BY p.patient_id "
            + "                                ) viral_load ON viral_load.patient_id = p.patient_id "
            + "                 WHERE p.voided = 0  "
            + "                     AND e.voided = 0 "
            + "                    AND e.encounter_type = ${35} "
            + "                    AND e.encounter_datetime = viral_load.encounter_date "
            + "                    AND e.location_id = :location "
            + "                ) h1 ON h1.patient_id = p.patient_id "
            + " WHERE p.voided = 0  "
            + "    AND e.voided = 0 "
            + "    AND e.encounter_type = ${35} "
            + "    AND e.encounter_datetime > DATE_ADD(h1.encounter_datetime, INTERVAL 20 DAY)  "
            + "         AND e.encounter_datetime <= DATE_ADD(h1.encounter_datetime, INTERVAL 33 DAY) "
            + "    AND e.location_id = :location ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  public CohortDefinition getMQC11NH3() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    sqlCohortDefinition.setName("Category 11 - Numerator - H3");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON p.patient_id = e.patient_id "
            + "    INNER JOIN ( "
            + "                     SELECT p.patient_id, e.encounter_datetime "
            + "                    FROM patient p "
            + "                        INNER JOIN encounter e "
            + "                            ON p.patient_id = e.patient_id "
            + "                        INNER JOIN ( "
            + "                                     SELECT p.patient_id, e.encounter_datetime"
            + "                                     FROM patient p "
            + "                                        INNER JOIN encounter e "
            + "                                            ON p.patient_id = e.patient_id "
            + "                                        INNER JOIN ( "
            + "                                                        SELECT p.patient_id, MIN(e.encounter_datetime) as  encounter_date "
            + "                                                        FROM patient p   "
            + "                                                            INNER JOIN encounter e "
            + "                                                                ON p.patient_id = e.patient_id "
            + "                                                            INNER JOIN obs o "
            + "                                                                ON o.encounter_id = e.encounter_id "
            + "                                                        WHERE p.voided = 0  "
            + "                                                            AND e.voided = 0 "
            + "                                                            AND o.voided = 0 "
            + "                                                            AND e.encounter_type = ${6} "
            + "                                                            AND e.encounter_datetime "
            + "                                                                BETWEEN :startDate AND :endDate "
            + "                                                            AND e.location_id = :location "
            + "                                                            AND o.concept_id = ${856} AND o.value_numeric >  1000 "
            + "                                                        GROUP BY p.patient_id "
            + "                                                    ) viral_load ON viral_load.patient_id = p.patient_id "
            + "                                     WHERE p.voided = 0  "
            + "                                         AND e.voided = 0 "
            + "                                        AND e.encounter_type = ${35} "
            + "                                        AND e.encounter_datetime = viral_load.encounter_date "
            + "                                        AND e.location_id = :location "
            + "                                    ) h1 ON h1.patient_id = p.patient_id "
            + "                     WHERE p.voided = 0  "
            + "                        AND e.voided = 0 "
            + "                        AND e.encounter_type = ${35} "
            + "                        AND e.encounter_datetime > DATE_ADD(h1.encounter_datetime, INTERVAL 20 DAY)  "
            + "                             AND e.encounter_datetime <= DATE_ADD(h1.encounter_datetime, INTERVAL 33 DAY) "
            + "                        AND e.location_id = :location "
            + "                ) h2 ON h2.patient_id = p.patient_id "
            + " WHERE p.voided = 0  "
            + "    AND e.voided = 0 "
            + "    AND e.encounter_type = ${35} "
            + "    AND e.encounter_datetime > DATE_ADD(h2.encounter_datetime, INTERVAL 20 DAY)  "
            + "         AND e.encounter_datetime <= DATE_ADD(h2.encounter_datetime, INTERVAL 33 DAY) "
            + "    AND e.location_id = :location ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * H: Select all patients who have 3 APSS&PP (encounter type 35) consultation in 66 days after
   * Viral Load result (the oldest date from B2) following the conditions:
   *
   * <ul>
   *   <li>H1 - One Consultation (Encounter_datetime (from encounter type 35)) on the same date when
   *       the Viral Load with >1000 result was recorded (oldest date from B2) AND
   *   <li>H2- Another consultation (Encounter_datetime (from encounter type 35)) > “1st
   *       consultation” (oldest date from H1)+20 days and <=“1st consultation” (oldest date from
   *       H1)+33days AND
   *   <li>H3- Another consultation (Encounter_datetime (from encounter type 35)) > “2nd
   *       consultation” (oldest date from H2)+20 days and <=“2nd consultation” (oldest date from
   *       H2)+33days AND
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NH() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Date.class));

    compositionCohortDefinition.setName("Category 11 Numerator session G");

    CohortDefinition h1 = getMQC11NH1();
    CohortDefinition h2 = getMQC11NH2();
    CohortDefinition h3 = getMQC11NH3();

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch("h1", EptsReportUtils.map(h1, mapping));
    compositionCohortDefinition.addSearch("h2", EptsReportUtils.map(h2, mapping));
    compositionCohortDefinition.addSearch("h3", EptsReportUtils.map(h3, mapping));

    compositionCohortDefinition.setCompositionString("h1 AND h2 AND h3");

    return compositionCohortDefinition;
  }

  /**
   * I: Select all patients who have monthly APSS&PP(encounter type 35) consultation (until the end
   * of the revision period) after Starting ART(The oldest date from A) as following pseudo-code:
   *
   * <p>Start pseudo-code:
   *
   * <ul>
   *   <li>For ( i=0; i<(days between “ART Start Date” and endDateRevision; i++)
   *       <ul>
   *         <li>Existence of consultation (Encounter_datetime (from encounter type 35)) > [“ART
   *             Start Date” (oldest date from A)+i] and <= “ART Start Date” (oldest date from
   *             A)+i+33days
   *         <li>i= i+33days
   *       </ul>
   *   <li>End pseudo-code.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NI() {

    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(ConsultationUntilEndDateAfterStartingART.class).get(0));
    cd.setName(
        "Categoru 11 - numerator - Session I - Interval of 33 Daus for APSS consultations after ART start date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addCalculationParameter("considerTransferredIn", false);
    cd.addCalculationParameter("considerPharmacyEncounter", true);

    return cd;
  }

  /**
   * 11.1. % de adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de
   * APSS/PP nos primeiros 3 meses após início do TARV (Line 56 in the template) Numerador (Column D
   * in the Template) as following: <code>
   * A and NOT C and NOT D and NOT E and NOT F  AND G and Age > 14*</code>
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFandGAdultss() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.1 ");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMQC3D1();
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition g = getMQC11NG();
    CohortDefinition adults = genericCohortQueries.getAgeOnArtStartDate(15, 200, true);

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch(
        "G", EptsReportUtils.map(g, "endDate=${endDate},location=${location}"));
    compositionCohortDefinition.addSearch(
        "ADULTS",
        EptsReportUtils.map(
            adults, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "A AND NOT C AND NOT D AND NOT E AND NOT F  AND G AND ADULTS");

    return compositionCohortDefinition;
  }

  /**
   * 11.2. % de pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de
   * APSS/PP mensais consecutivas para reforço de adesão (Line 57 in the template) Numerador (Column
   * D in the Template) as following: <code>
   * B1 and B2 and NOT C and NOT D and NOT E and NOT F AND H and  Age > 14*</code>
   */
  public CohortDefinition getMQC11NumB1nB2notCnotDnotEnotEnotFnHandAdultss() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.2 ");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaDenominatorB("B1");
    CohortDefinition b2 = getPatientsFromFichaClinicaDenominatorB("B2_11");
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));
    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition h = getMQC11NH();
    CohortDefinition adults = genericCohortQueries.getAgeOnArtStartDate(15, 200, true);

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING));
    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));

    compositionCohortDefinition.addSearch(
        "ADULTS",
        EptsReportUtils.map(
            adults, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "B1 AND B2 AND NOT C AND NOT D AND NOT E AND NOT F AND H AND  ADULTS");

    return compositionCohortDefinition;
  }

  /**
   * 11.3.% de MG em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP
   * nos primeiros 3 meses após início do TARV (Line 58 in the template) Numerador (Column D in the
   * Template) as following: <code> A and B3 and  C and NOT D and NOT E and NOT F  AND G </code>
   */
  public CohortDefinition getMQC11NumAnB3nCnotDnotEnotEnotFnG() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.3");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMQC3D1();
    CohortDefinition b3 = getPatientsWithClinicalConsultationB3();
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    ;
    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));
    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition g = getMQC11NG();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(b3, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch(
        "G", EptsReportUtils.map(g, "endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "A AND B3 AND  C AND NOT D AND NOT E AND NOT F  AND G");

    return compositionCohortDefinition;
  }

  /**
   * 11.4. % de MG na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de
   * APSS/PP mensais consecutivas para reforço de adesão (Line 59 in the template) Numerador (Column
   * D in the Template) as following: <code>
   *  B1 and B2 and B3 and C and NOT D and NOT E and NOT F AND H </code>
   */
  public CohortDefinition getMQC11NumB1nB2nB3nCnotDnotEnotEnotFnH() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror  11.4");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaDenominatorB("B1");
    CohortDefinition b2 = getPatientsFromFichaClinicaDenominatorB("B2_11");
    ;
    CohortDefinition b3 = getPatientsWithClinicalConsultationB3();
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition h = getMQC11NH();

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING));
    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING));
    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(b3, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));

    compositionCohortDefinition.setCompositionString(
        "B1 AND B2 AND B3 AND C AND NOT D AND NOT E AND NOT F AND H");

    return compositionCohortDefinition;
  }

  /**
   * 11.5. % de crianças >2 anos de idade em TARV com registo mensal de seguimento da adesão na
   * ficha de APSS/PP nos primeiros 99 dias de TARV (Line 60 in the template) Numerador (Column D in
   * the Template) as following: <code>
   * A and NOT C and NOT D and NOT E and NOT F  AND G and Age BETWEEN 2 AND 14*</code>
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFnotGnChildren() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.5");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMQC3D1();
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));
    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition g = getMQC11NG();
    CohortDefinition children = genericCohortQueries.getAgeOnArtStartDate(2, 14, true);

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch(
        "G", EptsReportUtils.map(g, "endDate=${endDate},location=${location}"));
    compositionCohortDefinition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            children, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "A AND NOT C AND NOT D AND NOT E AND NOT F  AND G AND CHILDREN");

    return compositionCohortDefinition;
  }
  /**
   * 11.6. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na
   * ficha de APSS/PP no primeiro ano de TARV (Line 61 in the template) Numerador (Column D in the
   * Template) as following: <code>
   *  A and NOT C and NOT D and NOT E and NOT F AND I  AND Age  <= 9 MONTHS</code>
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFnotIlessThan9Month() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.6");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMQC3D1();
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);
    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));
    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition i = getMQC11NI();
    CohortDefinition babies = genericCohortQueries.getAgeInMonths(0, 8);

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch(
        "I", EptsReportUtils.map(i, "onOrBefore=${endDate},location=${location}"));
    compositionCohortDefinition.addSearch(
        "BABIES", EptsReportUtils.map(babies, "effectiveDate=${effectiveDate}"));

    compositionCohortDefinition.setCompositionString(
        "A and NOT C and NOT D and NOT E and NOT F AND I  AND BABIES");

    return compositionCohortDefinition;
  }

  /**
   * 11.7. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3
   * consultas mensais consecutivas de APSS/PP para reforço de adesão(Line 62 in the template)
   * Numerador (Column D in the Template) as following: <code>
   *  B1 and B2 and  NOT C and NOT D and NOT E and NOT F  And H and  Age < 15**</code>
   */
  public CohortDefinition getMQC11NumB1nB2notCnotDnotEnotFnHChildren() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.6");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaDenominatorB("B1");
    CohortDefinition b2 = getPatientsFromFichaClinicaDenominatorB("B2_11");
    CohortDefinition c =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition d =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition e =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition h = getMQC11NH();
    CohortDefinition children = genericCohortQueries.getAgeOnArtStartDate(0, 14, true);

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING));
    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING));
    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING));
    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));
    compositionCohortDefinition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            children, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString(
        "B1 AND B2 AND  NOT C AND NOT D AND NOT E AND NOT F  AND H AND CHILDREN ");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQ12DenP1: Melhoria de Qualidade Category 12 Denominator - Part 1</b><br>
   * <br>
   * <i> DENOMINATOR 1: A AND NOT (C OR D OR E OR F) AND AGE > 15 </i> <br>
   * <br>
   * <i> DENOMINATOR 2: A AND NOT (C OR D OR E OR F) AND AGE > 15 </i> <br>
   * <br>
   * <i> DENOMINATOR 6: A AND NOT (C OR D OR E OR F) AND AGE <= 15 </i> <br>
   * <br>
   * <i> DENOMINATOR 7: A AND NOT (C OR D OR E OR F) AND AGE <= 15 </i> <br>
   * <br>
   * <i> DENOMINATOR 10: (A AND C) AND NOT (D OR E OR F) </i> <br>
   * <br>
   * <i> DENOMINATOR 11: (A AND C) AND NOT (D OR E OR F) </i> <br>
   * <br>
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion period (startDateInclusion
   *       and endDateInclusion)
   *   <li>C - All female patients registered as “Pregnant” on MasterCard during the inclusion
   *       period (startDateInclusion and endDateInclusion)
   *   <li>D - All female patients registered as “Breastfeeding” on MasterCard during the inclusion
   *       period (startDateInclusion and endDateInclusion)
   *   <li>E - All transferred IN patients within the revision period
   *   <li>F - All transferred OUT patients within the revision period
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ12DEN(Integer den) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (den) {
      case 1:
        comp.setName(
            "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 2:
        comp.setName(
            "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 6:
        comp.setName(
            "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 7:
        comp.setName(
            "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 10:
        comp.setName(
            "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 11:
        comp.setName(
            "# de mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    comp.addParameter(new Parameter("location", "location", Date.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    comp.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    comp.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    comp.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    comp.addSearch(
        "E",
        EptsReportUtils.map(
            transferIn,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    comp.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    if (den == 1 || den == 2) {
      comp.setCompositionString("A AND NOT (C OR D OR E OR F) AND ADULT");
    } else if (den == 6 || den == 7) {
      comp.setCompositionString("A AND NOT (C OR D OR E OR F) AND CHILDREN");
    } else if (den == 10 || den == 11) {
      comp.setCompositionString("(A AND C) AND NOT (D OR E OR F)");
    }
    return comp;
  }

  /**
   * <b>MQC13Part3B1</b>: Melhoria de Qualidade Category 13 Deniminator B1 <br>
   * <i> BI1 and not B1E</i> <br>
   *
   * <ul>
   *   <li>BI1 - Select all patients who have the most recent “ALTERNATIVA A LINHA - 1a LINHA”
   *       (Concept Id 23898, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any
   *       value coded (not null) during the inclusion period (startDateInclusion and
   *       endDateInclusion) AND
   *   <li>B1E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who
   *       have “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA
   *       LINHA”(Concept id 21150) and encounter_datetime > the most recent “ALTERNATIVA A LINHA -
   *       1a LINHA” (from B1) and <= endDateInclusion
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsOnRegimeChangeBI1AndNotB1E_B1() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Clinical Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23898", commonMetadata.getAlternativeLineConcept().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(o.obs_datetime) AS regime_date "
            + "        FROM   patient p "
            + "               JOIN encounter e "
            + "                 ON e.patient_id = p.patient_id "
            + "               JOIN obs o "
            + "                 ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${53} "
            + "               AND o.concept_id = ${23898} "
            + "               AND o.value_coded IS NOT NULL "
            + "               AND e.location_id = :location "
            + "               AND e.voided = 0 "
            + "               AND p.voided = 0 "
            + "               AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id) bI1 "
            + "WHERE  bI1.patient_id NOT IN (SELECT p.patient_id "
            + "                              FROM   patient p "
            + "                                     JOIN encounter e "
            + "                                       ON e.patient_id = p.patient_id "
            + "                                     JOIN obs o "
            + "                                       ON o.encounter_id = e.encounter_id "
            + "                              WHERE  e.encounter_type = ${6} "
            + "                                     AND o.concept_id = ${21151} "
            + "                                     AND o.value_coded <> ${21150} "
            + "                                     AND e.location_id = :location "
            + "                                     AND e.voided = 0 "
            + "                                     AND p.voided = 0 "
            + "                                     AND e.encounter_datetime > bI1.regime_date "
            + "                                     AND e.encounter_datetime <= :endDate)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC13Part3B2</b>: Melhoria de Qualidade Category 13 Deniminator B2 <br>
   * <i> BI2 and not B2E</i> <br>
   *
   * <ul>
   *   <li>BI2- Select all patients who have the LAST “LINHA TERAPEUTICA” (Concept Id 21151)
   *       recorded in Ficha Clinica (encounter type 6, encounter_datetime) with value coded
   *       “SEGUNDA LINHA” (concept id 21148) during the inclusion period (startDateInclusion and
   *       endDateInclusion) AND
   *   <li>B1E - Exclude all patients with clinical consultation (encounter type 6) with concept
   *       “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value coded “HIV CARGA
   *       VIRAL” (Concept Id 856) on Encounter_datetime startDateInclusion-3months and
   *       startDateInclusion.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsOnSecondLineBI2AndNotB2E_B2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Clinical Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("less3mDate", "less3mDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21148", hivMetadata.getSecondLineConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(e.encounter_datetime) AS regime_date "
            + "        FROM   patient p "
            + "               JOIN encounter e "
            + "                 ON e.patient_id = p.patient_id "
            + "               JOIN obs o "
            + "                 ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "               AND o.concept_id = ${21151} "
            + "               AND o.value_coded IS NOT NULL "
            + "               AND e.location_id = :location "
            + "               AND e.voided = 0 "
            + "               AND p.voided = 0 "
            + "               AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id) bI2 "
            + "WHERE  bI2.patient_id NOT IN (SELECT p.patient_id "
            + "                              FROM   patient p "
            + "                                     JOIN encounter e "
            + "                                       ON e.patient_id = p.patient_id "
            + "                                     JOIN obs o "
            + "                                       ON o.encounter_id = e.encounter_id "
            + "                              WHERE  e.encounter_type = ${6} "
            + "               AND bI2.patient_id = p.patient_id "
            + "                                     AND o.concept_id = ${23722} "
            + "                                     AND o.value_coded = ${856} "
            + "                                     AND e.location_id = :location "
            + "                                     AND e.voided = 0 "
            + "                                     AND p.voided = 0 "
            + "                                     AND e.encounter_datetime BETWEEN "
            + "                                         :less3mDate AND :endDate "
            + "                              GROUP  BY p.patient_id)  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ12</b>: Melhoria de Qualidade Categoria 12 Numerador - P2 <br>
   *
   * <p>Select patients from the corresponding denominator and all active patients as done in resumo
   * mensal report - B13 and apply the following categories <i>12.3 - (A and B1 and NOT (B1E or C or
   * D or E)) AND NOT G and Age >= 15*</i><br>
   * <i>12.4 - (A and B2 and NOT (B2E or C or D or E)) AND NOT G and Age > =15*</i><br>
   * <i>12.8 - (A and B1 and NOT (B1E or C or D or E)) AND NOT G and Age < 15*</i><br>
   * <i>12.9 - (A and B2) and NOT (B2E or C or D or E) AND NOT G and Age < 15*</i><br>
   * <i>12.12 - (A and B1 and C) and NOT (B1E or D or E) AND NOT G </i><br>
   *
   * <p>All age disaggreagtions should be based on the ART start date
   *
   * @param flag
   * @return
   */
  public CohortDefinition getMQ12NumeratorP2(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 3:
        cd.setName(
            "No de adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás sem registo de saidas");
        break;
      case 4:
        cd.setName("No de adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás");
        break;
      case 8:
        cd.setName("No de crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás");
        break;
      case 9:
        cd.setName("No de crianças (0-14 anos)  que iniciaram 2ª linha TARV há 12 meses atrás");
        break;
      case 12:
        cd.setName("No de mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás");
        break;
    }
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    // Start adding the definitions based on the requirements
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "G",
        EptsReportUtils.map(
            resumoMensalCohortQueries
                .getNumberOfActivePatientsInArtAtEndOfCurrentMonthWithVlPerformed(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            getPatientsFromFichaClinicaDenominatorB("B1"),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1E",
        EptsReportUtils.map(
            getPatientsFromFichaClinicaDenominatorB("B1E"),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                true,
                false,
                "once",
                hivMetadata.getMasterCardEncounterType(),
                commonMetadata.getPregnantConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                true,
                false,
                "once",
                hivMetadata.getMasterCardEncounterType(),
                commonMetadata.getBreastfeeding(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                null,
                null),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                true,
                "once",
                hivMetadata.getMasterCardEncounterType(),
                commonMetadata.getTransferFromOtherFacilityConcept(),
                Collections.singletonList(hivMetadata.getYesConcept()),
                hivMetadata.getTypeOfPatientTransferredFrom(),
                Collections.singletonList(hivMetadata.getArtStatus())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            getPatientsFromFichaClinicaDenominatorB("B2_12"),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B2E",
        EptsReportUtils.map(
            getPatientsFromFichaClinicaDenominatorB("B2E"),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    if (flag == 3) {
      cd.setCompositionString("(A AND B1 AND NOT (B1E OR C OR D OR E)) AND NOT G");
    } else if (flag == 4) {
      cd.setCompositionString("(A AND B2 AND NOT (B2E OR C OR D OR E)) AND NOT G");
    } else if (flag == 8) {
      cd.setCompositionString("(A AND B1 AND NOT (B1E OR C OR D OR E)) AND NOT G");
    } else if (flag == 9) {
      cd.setCompositionString("(A AND B2) AND NOT (B2E OR C OR D OR E) AND NOT G");
    } else if (flag == 12) {
      cd.setCompositionString("(A AND B1 AND C) AND NOT (B1E OR D OR E) AND NOT G ");
    }

    return cd;
  }

  /**
   * <b>MQ12NumP1: Melhoria de Qualidade Category 12 Numerator - Part 1</b><br>
   * <br>
   * <i> NUMERATOR 1: (A and NOT (C or D or E or F)) and H and Age >= 15 </i> <br>
   * <br>
   * <i> NUMERATOR 2: (A and NOT (C or D or E or F)) and I and Age >= 15 </i> <br>
   * <br>
   * <i> NUMERATOR 6: (A and NOT (C or D or E or F)) and H Age < 15 </i> <br>
   * <br>
   * <i> NUMERATOR 7: (A and NOT (C or D or E or F)) and I and Age < 15 </i> <br>
   * <br>
   * <i> NUMERATOR 10: ((A and C) and NOT (D or E or F)) and H Age < 15 </i> <br>
   * <br>
   * <i> NUMERATOR 11: ((A and C) and NOT (D or E or F)) and I </i> <br>
   * <br>
   *
   * <ul>
   *   <li>H - Filter all patients that returned for another clinical consultation (encounter type
   *       6, encounter_datetime) or ARV pickup (encounter type 52, concept ID 23866 value_datetime,
   *       Levantou ARV (concept id 23865) = Sim (1065)) between 25 and 33 days after ART start
   *       date(Oldest date From A)
   *   <li>I1 - FIRST consultation (Encounter_datetime (from encounter type 6)) >= “ART Start Date”
   *       (oldest date from A)+20days and <= “ART Start Date” (oldest date from A)+33days
   *   <li>AND
   *   <li>I2 - At least one consultation (Encounter_datetime (from encounter type 6)) >= “First
   *       Consultation” (oldest date from I1)+20days and <=“First Consultation” (oldest date from
   *       I1)+33days
   *   <li>I3 - At least one consultation (Encounter_datetime (from encounter type 6)) > “Second
   *       Consultation” (oldest date from I2)+20days and <= “Second Consultation” (oldest date from
   *       I2)+33days
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ12NUM(Integer den) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (den) {
      case 1:
        comp.setName(
            "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs entre 25 a 33 dias após o início do TARV");
        break;
      case 2:
        comp.setName(
            "# de adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram 3 consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 6:
        comp.setName(
            "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 7:
        comp.setName(
            "# de crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 10:
        comp.setName(
            "# de mulheres grávidas HIV+ que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 11:
        comp.setName(
            "# de mulheres grávidas HIV+ que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));
    comp.addParameter(new Parameter("location", "location", Date.class));

    CohortDefinition startedART = getMQC3D1();

    CohortDefinition pregnant =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getPregnantConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition breastfeeding =
        commonCohortQueries.getMohMQPatientsOnCondition(
            true,
            false,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getBreastfeeding(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            null,
            null);

    CohortDefinition transferIn =
        commonCohortQueries.getMohMQPatientsOnCondition(
            false,
            true,
            "once",
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getTransferFromOtherFacilityConcept(),
            Collections.singletonList(hivMetadata.getYesConcept()),
            hivMetadata.getTypeOfPatientTransferredFrom(),
            Collections.singletonList(hivMetadata.getArtStatus()));

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition returnedForAnyConsultationOrPickup =
        qualityImprovement2020Queries.getMQ12NumH(
            25,
            33,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId());

    CohortDefinition returnedForAnotherConsultationOrPickup =
        qualityImprovement2020Queries.getMQ12NumI(
            20,
            33,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId());

    comp.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    comp.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    comp.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    comp.addSearch(
        "E",
        EptsReportUtils.map(
            transferIn,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "F",
        EptsReportUtils.map(
            transferOut,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "H",
        EptsReportUtils.map(
            returnedForAnyConsultationOrPickup,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "I",
        EptsReportUtils.map(
            returnedForAnotherConsultationOrPickup,
            "startDate=${startDate},endDate=${dataFinalAvaliacao},location=${location}"));

    comp.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    comp.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    if (den == 1) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND H AND ADULT");
    } else if (den == 2) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND I AND ADULT");
    } else if (den == 6) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND H AND CHILDREN");
    } else if (den == 7) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND I AND CHILDREN");
    } else if (den == 10) {
      comp.setCompositionString("((A AND C) AND NOT (D OR E OR F)) AND H AND CHILDREN");
    } else if (den == 11) {
      comp.setCompositionString("((A AND C) AND NOT (D OR E OR F)) AND I");
    }
    return comp;
  }

  /**
   * <b>Description:</b> MQ Categoria 13 C query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * C - Select all patients with Last Clinical Consultation (encounter type 6, encounter_datetime)
   * occurred during the period (encounter_datetime > endDateInclusion and <= endDateRevision) and
   * the concept: “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value coded “HIV
   * CARGA VIRAL” (Concept Id 856) In this last consultation.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMQ13C() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Last Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());

    String query =
        " SELECT  "
            + "     patient_id  "
            + " FROM  "
            + "     (SELECT   "
            + "         p.patient_id, MAX(e.encounter_datetime) last_clinical  "
            + "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     INNER JOIN obs o ON o.person_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  AND o.voided = 0"
            + "             AND e.encounter_type = ${6}  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "             AND o.concept_id = ${23722}  "
            + "             AND o.value_coded = ${856}  "
            + "     GROUP BY p.patient_id) AS list";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ13</b>: Melhoria de Qualidade Category 13 <br>
   * <i></i><br>
   * <i> <b>DENOMINATOR (1,6,7,8):</b> B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR
   * B5E)</i> <br>
   * <i></i><br>
   * <i> <b>NUMERATOR (1,6,7,8):</b> B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR
   * B5E) AND C </i> <br>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ13(Boolean den, Integer line) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den) {
      compositionCohortDefinition.setName(
          "B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E)");
    } else {
      compositionCohortDefinition.setName("B and C");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    CohortDefinition lastClinical = commonCohortQueries.getMOHPatientsLastClinicalConsultation();

    CohortDefinition firstLine6Months =
        commonCohortQueries.getMOHPatientsOnTreatmentFor6Months(
            false,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()));

    CohortDefinition secondLine6Months =
        commonCohortQueries.getMOHPatientsOnTreatmentFor6Months(
            false,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getSecondLineConcept()));

    CohortDefinition changeRegimen6Months =
        commonCohortQueries.getMOHPatientsOnTreatmentFor6Months(
            true,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getAlternativeLineConcept(),
            Arrays.asList(
                commonMetadata.getAlternativeFirstLineConcept(),
                commonMetadata.getRegimeChangeConcept(),
                hivMetadata.getNoConcept()));

    CohortDefinition B2E =
        commonCohortQueries.getMOHPatientsToExcludeFromTreatmentIn6Months(
            false,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()));

    CohortDefinition secondLineB2E =
        commonCohortQueries.getMOHPatientsToExcludeFromTreatmentIn6Months(
            false,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getSecondLineConcept()),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getSecondLineConcept()));

    CohortDefinition B3E =
        commonCohortQueries.getMOHPatientsToExcludeFromTreatmentIn6Months(
            true,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getAlternativeLineConcept(),
            Arrays.asList(
                commonMetadata.getAlternativeFirstLineConcept(),
                commonMetadata.getRegimeChangeConcept(),
                hivMetadata.getNoConcept()),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()));

    CohortDefinition B4E =
        commonCohortQueries.getMOHPatientsWithVLRequestorResultBetweenClinicalConsultations(
            true, false, 12);

    CohortDefinition B5E =
        commonCohortQueries.getMOHPatientsWithVLRequestorResultBetweenClinicalConsultations(
            false, true, 3);

    CohortDefinition C = getMQ13C();

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(lastClinical, MAPPING));

    if (line == 1) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(15, null),
              MAPPING));
    } else if (line == 4) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(15, null),
              MAPPING));
    } else if (line == 6) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(0, 4), MAPPING));
    } else if (line == 7) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(5, 9), MAPPING));
    } else if (line == 8) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(10, 14),
              MAPPING));
    } else if (line == 13) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(2, null),
              MAPPING));
    }

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(firstLine6Months, MAPPING));

    compositionCohortDefinition.addSearch(
        "secondLineB2", EptsReportUtils.map(secondLine6Months, MAPPING));

    compositionCohortDefinition.addSearch("B2E", EptsReportUtils.map(B2E, MAPPING));

    compositionCohortDefinition.addSearch(
        "secondLineB2E", EptsReportUtils.map(secondLineB2E, MAPPING));

    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(changeRegimen6Months, MAPPING));

    compositionCohortDefinition.addSearch("B3E", EptsReportUtils.map(B3E, MAPPING));

    compositionCohortDefinition.addSearch("B4E", EptsReportUtils.map(B4E, MAPPING));

    compositionCohortDefinition.addSearch("B5E", EptsReportUtils.map(B5E, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(C, MAPPING));

    if (den) {
      if (line == 1) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E) AND age");
      } else if (line == 4) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND (secondLineB2 AND NOT secondLineB2E) AND NOT (B4E OR B5E) AND age");
      } else if (line == 6) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E) AND age");
      } else if (line == 7) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E) AND age");
      } else if (line == 8) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E) AND age");
      } else if (line == 13) {
        compositionCohortDefinition.setCompositionString(
            "B1 AND (secondLineB2 AND NOT secondLineB2E) AND NOT (B4E OR B5E) AND age");
      }
    } else {
      if (line == 1) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E)) AND C AND age");
      } else if (line == 6) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E)) AND C AND age");
      } else if (line == 7) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E)) AND C AND age");
      } else if (line == 8) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ((B2 AND NOT B2E) OR (B3 AND NOT B3E)) AND NOT (B4E OR B5E)) AND C AND age");
      }
    }

    return compositionCohortDefinition;
  }
}
