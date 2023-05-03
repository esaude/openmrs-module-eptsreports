package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.generic.AgeOnObsDatetimeCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.ConsultationUntilEndDateAfterStartingART;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.EncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.SecondFollowingEncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.ThirdFollowingEncounterAfterOldestARTStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovement2020Queries;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.MIMQ;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QualityImprovement2020CohortQueries {

  private GenericCohortQueries genericCohortQueries;

  private HivMetadata hivMetadata;

  private CommonMetadata commonMetadata;

  private GenderCohortQueries genderCohortQueries;

  private AgeCohortQueries ageCohortQueries;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private CommonCohortQueries commonCohortQueries;

  private TbMetadata tbMetadata;

  private TxPvlsCohortQueries txPvls;

  private TxMlCohortQueries txMlCohortQueries;

  private IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries;

  private final String MAPPING = "startDate=${startDate},endDate=${endDate},location=${location}";
  private final String MAPPING1 =
      "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}";
  private final String MAPPING2 =
      "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}";
  private final String MAPPING3 =
      "startDate=${startDate},endDate=${revisionEndDate},location=${location}";
  private String MAPPING4 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}";
  private final String MAPPING5 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}";
  private final String MAPPING6 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate},location=${location}";
  private final String MAPPING7 =
      "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate},location=${location}";
  private final String MAPPING8 =
      "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate-3m},location=${location}";
  private final String MAPPING9 =
      "startDate=${revisionEndDate-4m+1d},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}";
  private final String MAPPING10 =
      "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},revisionEndDate=${revisionEndDate},location=${location}";

  @Autowired
  public QualityImprovement2020CohortQueries(
      GenericCohortQueries genericCohortQueries,
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata,
      GenderCohortQueries genderCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      CommonCohortQueries commonCohortQueries,
      TbMetadata tbMetadata,
      TxPvlsCohortQueries txPvls,
      AgeCohortQueries ageCohortQueries,
      TxMlCohortQueries txMlCohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
    this.genderCohortQueries = genderCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
    this.tbMetadata = tbMetadata;
    this.txPvls = txPvls;
    this.ageCohortQueries = ageCohortQueries;
    this.txMlCohortQueries = txMlCohortQueries;
  }

  public void setIntensiveMonitoringCohortQueries(
      IntensiveMonitoringCohortQueries intensiveMonitoringCohortQueries) {
    this.intensiveMonitoringCohortQueries = intensiveMonitoringCohortQueries;
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

    compositionCohortDefinition.setName("Melhoria de Qualidade Category 3 Denomirator");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.setCompositionString("A AND NOT B");

    return compositionCohortDefinition;
  }
  /**
   * 17 - MOH MQ: Patients who initiated ART during the inclusion period
   *
   * <p>A2-All patients who have the first historical start drugs date (earliest concept ID 1190)
   * set in FICHA RESUMO (Encounter Type 53) earliest “historical start date” Encounter Type Ids =
   * 53 The earliest “Historical Start Date” (Concept Id 1190)And historical start
   * date(Value_datetime) <=EndDate And the earliest date from A1 and A2 (identified as Patient ART
   * Start Date) is >= startDateRevision and <=endDateInclusion
   *
   * @return SqlCohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients who initiated ART during the inclusion
   *         period
   */
  public SqlCohortDefinition getMOHArtStartDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getHistoricalDrugStartDateConcept().getConceptId());

    String query =
        " SELECT patient_id "
            + "        FROM   (SELECT p.patient_id, Min(value_datetime) art_date "
            + "                    FROM patient p "
            + "              INNER JOIN encounter e "
            + "                  ON p.patient_id = e.patient_id "
            + "              INNER JOIN obs o "
            + "                  ON e.encounter_id = o.encounter_id "
            + "          WHERE  p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND e.encounter_type = ${53} "
            + "              AND o.concept_id = ${1190} "
            + "              AND o.value_datetime IS NOT NULL "
            + "              AND o.value_datetime <= :endDate "
            + "              AND e.location_id = :location "
            + "          GROUP  BY p.patient_id  )  "
            + "               union_tbl  "
            + "        WHERE  union_tbl.art_date BETWEEN :startDate AND :endDate";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <h4>Categoria 3 Denominador </h4>
   *
   * <ul>
   *   <li>Select all patients From Categoria 3 Denominador:
   *   <li>C: Filter all adults patients who have the first clinical consultation between Diagnosis
   *       Date and Diagnosis Date+7days as following:
   *       <ul>
   *         <li>all patients who haC: Filter all adults patients who have the first clinical
   *             consultation between Diagnosis Date and Diagnosis Date+7days as following:ve who
   *             have the first consultation registered in “Ficha Clinica” (encounter type 6) after
   *             “Data Diagnostico” with the following conditions:
   *             <ul>
   *               <li>[encounter type 6] “Data da consulta” (encounter datetime) >= [encounter type
   *                   53] “Data Diagnóstico” (concept_id 22772 obs datetime) and <= [encounter type
   *                   53] “Data Diagnóstico” (concept_id 22772 obs datetime) + 7 days
   *             </ul>
   *       </ul>
   *   <li>D: Filter all child patients who have the first clinical consultation between Diagnosis
   *       Date and Diagnosis Date+7days as following:
   *       <ul>
   *         <li>all patients who have the first consultation registered in “Ficha Clinica”
   *             (encounter type 6) after “Data Diagnostico” as following:
   *             <ul>
   *               <li>
   *                   <p>Select the oldest date between “Data Diagnóstico” (concept_id 23807 obs
   *                   datetime, encounter type 53) and “Data Diagnóstico Presuntivo” (concept_id
   *                   22772 obs datetime, encounter type 53)
   *                   <p>(Note: if “Data Diagnóstico” is empty then consider “Data Diagnóstico
   *                   Presuntivo” if it exists. If “Data Diagnostico” is empty then consider “Data
   *                   Diagnostico Presuntivo” if it exists).
   *             </ul>
   *         <li>And the first consultation [encounter type 6] “Data da consulta” (encounter
   *             datetime) >= [encounter type 53] the oldest “Data Diagnóstico” minus the oldest
   *             “Data Diagnóstico” is >=0 and <=7 days
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC3N1() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Melhoria de Qualidade Category 3 Numerator");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "CAT3DEN", EptsReportUtils.map(this.getMQC3D1(), MAPPING));

    compositionCohortDefinition.addSearch(
        "C", EptsReportUtils.map(this.getCFromMQC3N1(), "location=${location}"));
    compositionCohortDefinition.addSearch(
        "D", EptsReportUtils.map(this.getDFromMQC3N1(), "location=${location},endDate=${endDate}"));

    compositionCohortDefinition.setCompositionString("CAT3DEN AND (C OR D)");

    return compositionCohortDefinition;
  }

  /**
   * /**
   * <li>D: Filter all child patients who have the first clinical consultation between Diagnosis
   *     Date and Diagnosis Date+7days as following:
   *
   *     <ul>
   *       <li>all patients who have: Filter all adults patients who have the first clinical
   *           consultation between Diagnosis Date and Diagnosis Date+7days as following:ve who have
   *           the first consultation registered in “Ficha Clinica” (encounter type 6) after “Data
   *           Diagnostico” with the following conditions:
   *           <ul>
   *             <li>
   *                 <p>[encounter type 6] “Data da consulta” (encounter datetime) >= [encounter
   *                 type 53] “Data Diagnóstico” (concept_id 22772 obs datetime) and <= [encounter
   *                 type 53] “Data Diagnóstico” (concept_id 22772 obs datetime) + 7 days
   *           </ul>
   *       <li>And the first consultation [encounter type 6] “Data da consulta” (encounter datetime)
   *           >= [encounter type 53] the oldest “Data Diagnóstico” minus the oldest “Data
   *           Diagnóstico” is >=0 and <=7 days
   *     </ul>
   *
   * @return CohortDefinition
   */
  private CohortDefinition getCFromMQC3N1() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "Melhoria de Qualidade Category 3 Numerator C part composition");

    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "C", EptsReportUtils.map(getCqueryFromCat3(), "location=${location}"));

    compositionCohortDefinition.setCompositionString("C");

    return compositionCohortDefinition;
  }

  /**
   *
   * <li>D: Filter all child patients who have the first clinical consultation between Diagnosis
   *     Date and Diagnosis Date+7days as following:
   *
   *     <ul>
   *       <li>all patients who have the first consultation registered in “Ficha Clinica” (encounter
   *           type 6) after “Data Diagnostico” as following:
   *           <ul>
   *             <li>
   *                 <p>Select the oldest date between “Data Diagnóstico” (concept_id 23807 obs
   *                 datetime, encounter type 53) and “Data Diagnóstico Presuntivo” (concept_id
   *                 22772 obs datetime, encounter type 53)
   *                 <p>(Note: if “Data Diagnóstico” is empty then consider “Data Diagnóstico
   *                 Presuntivo” if it exists. If “Data Diagnostico” is empty then consider “Data
   *                 Diagnostico Presuntivo” if it exists).
   *                 <ul>
   *                   <li>
   *                       <p>And the first consultation [encounter type 6] “Data da consulta”
   *                       (encounter datetime) >= [encounter type 53] the oldest “Data Diagnóstico”
   *                       minus the oldest “Data Diagnóstico” is >=0 and <=7 days
   *                 </ul>
   *           </ul>
   *       <li>
   *     </ul>
   *
   * @return CohortDefinition
   */
  private CohortDefinition getDFromMQC3N1() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "Melhoria de Qualidade Category 3 Numerator C part composition");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    compositionCohortDefinition.addSearch(
        "D", EptsReportUtils.map(getDqueryFromCat3(), "endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("D");

    return compositionCohortDefinition;
  }

  /**
   * /**
   * <li>D: Filter all child patients who have the first clinical consultation between Diagnosis
   *     Date and Diagnosis Date+7days as following:
   *
   *     <ul>
   *       <li>all patients who have: Filter all adults patients who have the first clinical
   *           consultation between Diagnosis Date and Diagnosis Date+7days as following:ve who have
   *           the first consultation registered in “Ficha Clinica” (encounter type 6) after “Data
   *           Diagnostico” with the following conditions:
   *           <ul>
   *             <li>
   *                 <p>[encounter type 6] “Data da consulta” (encounter datetime) >= [encounter
   *                 type 53] “Data Diagnóstico” (concept_id 22772 obs datetime) and <= [encounter
   *                 type 53] “Data Diagnóstico” (concept_id 22772 obs datetime) + 7 days
   *           </ul>
   *     </ul>
   *
   * @return CohortDefinition
   */
  private CohortDefinition getCqueryFromCat3() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("22772", hivMetadata.getTypeTestHIVConcept().getConceptId());

    String query =
        " SELECT p.patient_id   "
            + " FROM patient p "
            + "    INNER JOIN ( "
            + "                    SELECT p.patient_id, MIN(e.encounter_datetime) AS encounterdatetime  "
            + "                    FROM patient p  "
            + "                        INNER JOIN encounter e  "
            + "                            ON e.patient_id = p.patient_id  "
            + "                    WHERE p.voided = 0  "
            + "                        AND e.voided = 0  "
            + "                        AND e.location_id = :location  "
            + "                        AND e.encounter_type = ${6}  "
            + "                    GROUP BY  p.patient_id  "
            + "              )  first_consultation ON  first_consultation.patient_id = p.patient_id "
            + "    INNER JOIN ( "
            + "                    SELECT p_diagnostico.patient_id,  o_diagnostico.obs_datetime AS data_diagnostico  "
            + "                    FROM patient p_diagnostico  "
            + "                        INNER JOIN encounter e_diagnostico  "
            + "                            ON e_diagnostico.patient_id = p_diagnostico.patient_id  "
            + "                        INNER JOIN obs o_diagnostico  "
            + "                            ON o_diagnostico.encounter_id = e_diagnostico.encounter_id  "
            + "                    WHERE p_diagnostico.voided = 0  "
            + "                        AND e_diagnostico.voided = 0  "
            + "                        AND o_diagnostico.voided = 0  "
            + "                        AND e_diagnostico.location_id = :location  "
            + "                        AND e_diagnostico.encounter_type = ${53}  "
            + "                        AND o_diagnostico.concept_id = ${22772} "
            + "                ) diagnostico ON diagnostico.patient_id = p.patient_id  "
            + "                 "
            + " WHERE p.voided = 0 "
            + "    AND  first_consultation.encounterdatetime BETWEEN diagnostico.data_diagnostico "
            + "     AND DATE_ADD(diagnostico.data_diagnostico, INTERVAL 7 DAY) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   * <li>D: Filter all child patients who have the first clinical consultation between Diagnosis
   *     Date and Diagnosis Date+7days as following:
   *
   *     <ul>
   *       <li>all patients who have the first consultation registered in “Ficha Clinica” (encounter
   *           type 6) after “Data Diagnostico” as following:
   *           <ul>
   *             <li>
   *                 <p>Select the oldest date between “Data Diagnóstico” (concept_id 23807 obs
   *                 datetime, encounter type 53) and “Data Diagnóstico Presuntivo” (concept_id
   *                 22772 obs datetime, encounter type 53)
   *                 <p>(Note: if “Data Diagnóstico” is empty then consider “Data Diagnóstico
   *                 Presuntivo” if it exists. If “Data Diagnostico” is empty then consider “Data
   *                 Diagnostico Presuntivo” if it exists).
   *           </ul>
   *       <li>And the first consultation [encounter type 6] “Data da consulta” (encounter datetime)
   *           >= [encounter type 53] the oldest “Data Diagnóstico” minus the oldest “Data
   *           Diagnóstico” is >=0 and <=7 days
   *     </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getDqueryFromCat3() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("22772", hivMetadata.getTypeTestHIVConcept().getConceptId());
    map.put("23807", hivMetadata.getPresumptiveDiagnosisInChildrenConcep().getConceptId());

    String query =
        "SELECT enc_6.patient_id AS patient_id FROM( "
            + "  SELECT pa.patient_id AS patient_id, MIN(ee.encounter_datetime) AS encounter_date FROM patient pa  "
            + "  INNER JOIN encounter ee ON ee.patient_id=pa.patient_id "
            + "  WHERE ee.encounter_type = ${6} "
            + "  AND ee.location_id= :location AND ee.encounter_datetime <=:endDate "
            + " GROUP BY pa.patient_id) enc_6 "
            + " INNER JOIN "
            + "  (SELECT p.patient_id AS patient_id, MIN(o.obs_datetime) AS obs_datetime FROM patient p "
            + "  INNER JOIN encounter e ON e.patient_id=p.patient_id "
            + "  INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "  WHERE e.encounter_type = ${53} AND o.concept_id IN ( ${23807}, ${22772}) "
            + "  AND e.location_id= :location GROUP BY p.patient_id) en_53 "
            + "  ON en_53.patient_id=enc_6.patient_id "
            + "  WHERE enc_6.encounter_date BETWEEN en_53.obs_datetime AND DATE_ADD(en_53.obs_datetime, INTERVAL 7 DAY)";

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
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));
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
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("(A AND NOT B AND NOT C AND NOT D) AND CHILDREN");
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
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));
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
        "D",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("((A AND B) AND NOT (C OR D)) AND FEMALE");
    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>Select all female patients who are pregnant as following:
   *       <ul>
   *         <li>All patients registered in Ficha Clínica (encounter type=53) with
   *             “Gestante”(concept_id 1982) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   *   <li>Select all female patients who are breastfeeding as following:
   *       <ul>
   *         <li>all patients registered in Ficha Clinica (encounter type=53) with
   *             “Lactante”(concept_id 6332) value coded equal to “Yes” (concept_id 1065) and
   *             encounter datetime >= startDateRevision and <=endDateInclusion and sex=Female
   *       </ul>
   * </ul>
   *
   * @param conceptIdQn The Obs quetion concept
   * @param conceptIdAns The value coded answers concept
   * @return CohortDefinition
   */
  // This will bypass the previous implementation and allow reuse of the method with other
  // parameters
  public CohortDefinition getPregnantAndBreastfeedingStates(int conceptIdQn, int conceptIdAns) {

    return getPregnantAndBreastfeedingStates(
        hivMetadata.getMasterCardEncounterType(), conceptIdQn, conceptIdAns);
  }

  public CohortDefinition getPregnantAndBreastfeedingStates(
      EncounterType encounterType, int conceptIdQn, int conceptIdAns) {
    Map<String, Integer> map = new HashMap<>();
    map.put("conceptIdQn", conceptIdQn);
    map.put("conceptIdAns", conceptIdAns);
    map.put("fichaClinicaEncounterType", encounterType.getEncounterTypeId());
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

  /**
   * <b>MQ4NUN1</b>: Melhoria de Qualidade Category 4 Numerador 1 <br>
   * <i> Denominador and E </i> <br>
   * <li>Select all patients from Categoria 4 Denominador
   *
   *     <ul>
   *       <li>E: Filter all patients who on their last clinical consultation registered in “Ficha
   *           Clinica” (encounter type 6) during the revision period (encounter datetime>=
   *           startDateInclusion and <=EndDateRevision), have “CLASSIFICAÇÃO DE DESNUTRIÇÃO”
   *           (concept_id 6336) value coded “Normal/Ligeira/DAM/ DAG”(concept_ids 1115, 6335, 68,
   *           1844).
   *     </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC4N1() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    compositionCohortDefinition.setName("Numerator for Category 4");

    compositionCohortDefinition.addSearch(
        "E",
        EptsReportUtils.map(
            getLastClinicalConsultationClassficacaoDesnutricao(),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch("MQC4D1", EptsReportUtils.map(getMQC4D1(), MAPPING));

    compositionCohortDefinition.setCompositionString("MQC4D1 AND E");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQ4NUN2</b>: Melhoria de Qualidade Category 4 Numerador 2 <br>
   * <i> Denominador and E </i> <br>
   * <li>Select all patients from Categoria 4 Denominador
   *
   *     <ul>
   *       <li>E: Filter all patients who on their last clinical consultation registered in “Ficha
   *           Clinica” (encounter type 6) during the revision period (encounter datetime>=
   *           startDateInclusion and <=EndDateRevision), have “CLASSIFICAÇÃO DE DESNUTRIÇÃO”
   *           (concept_id 6336) value coded “Normal/Ligeira/DAM/ DAG”(concept_ids 1115, 6335, 68,
   *           1844).
   *     </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC4N2() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    compositionCohortDefinition.setName("Numerator for Category 4");

    compositionCohortDefinition.addSearch(
        "E",
        EptsReportUtils.map(
            getLastClinicalConsultationClassficacaoDesnutricao(),
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch("MQC4D2", EptsReportUtils.map(getMQC4D2(), MAPPING));

    compositionCohortDefinition.setCompositionString("MQC4D2 AND E");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>E: Filter all patients who on their last clinical consultation registered in “Ficha
   *       Clinica” (encounter type 6) during the revision period (encounter datetime>=
   *       startDateInclusionRevision and <=EndDateRevisionendDateInclusion), have “CLASSIFICAÇÃO DE
   *       DESNUTRIÇÃO” (concept_id 6336) value coded “Normal/Ligeira/DAM/ DAG”(concept_ids 1115,
   *       6335, 68, 1844).
   * </ul>
   *
   * @return CohortDefinition
   */
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
        "SELECT "
            + " p.patient_id "
            + " FROM "
            + " patient p "
            + " INNER JOIN "
            + " encounter e "
            + " ON e.patient_id = p.patient_id "
            + " INNER JOIN "
            + " obs o "
            + " ON o.encounter_id = e.encounter_id "
            + " INNER JOIN "
            + " ( "
            + " SELECT "
            + " p.patient_id, "
            + " Max(e.encounter_datetime) AS encounter_datetime "
            + " FROM "
            + " patient p "
            + " INNER JOIN "
            + " encounter e "
            + " ON e.patient_id = p.patient_id "
            + " WHERE "
            + " e.encounter_type = ${adultoSeguimentoEncounterType} "
            + " AND p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY "
            + " p.patient_id "
            + " ) filtered "
            + " ON p.patient_id = filtered.patient_id "
            + " WHERE "
            + " e.encounter_datetime = filtered.encounter_datetime "
            + " AND e.location_id = :location "
            + " AND o.concept_id = ${classificationOfMalnutritionConcept} "
            + " AND e.voided = 0 "
            + " AND o.voided = 0 "
            + " AND o.value_coded IN (${normalConcept}, ${malnutritionLightConcept}, ${malnutritionConcept}, ${chronicMalnutritionConcept}) "
            + " AND e.encounter_type = ${adultoSeguimentoEncounterType} ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>F - Filter all patients with “Apoio/Educação Nutricional” equals to “ATPU” or “SOJA” in
   *       the same clinical consultation where“Grau da Avaliação Nutricional” equals to “DAM” or
   *       “DAG” during the revision period, clinical consultation >= startDateRevision and
   *       <=endDateRevision :
   *       <ul>
   *         <li>
   *             <p>All patients registered in Ficha Clinica (encounter type=6) with “Apoio/Educação
   *             Nutricional” (concept_id = 2152) and value_coded equal to “ATPU” (concept_id =
   *             6143) or equal to “SOJA” (concept_id = 2151) during the encounter_datetime >=
   *             startDateRevision and <=endDateInclusion
   *             <p>Note: the clinical consultation with “Apoio/Educação Nutricional” = “ATPU” or
   *             “SOJA” must be the same clinical consultation where “Grau da Avaliação Nutricional”
   *             equals to “DAM” or “DAG”. The first consultation with “Grau da Avaliação
   *             Nutricional” equals to “DAM” or “DAG” should be considered.
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with F criteria
   */
  public CohortDefinition getPatientsWithNutritionalStateAndNutritionalSupport() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Nutritional Classification");
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

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>“PROFILAXIA = INH” (concept_id 23985 value 656) and “Data Fim” (concept_id 165308 value
   * 1267) Encounter_datetime between startDateRevision and endDateRevision and:
   *
   * <p>- Obs_datetime(from the last clinical consultation with “PROFILAXIA = INH” (concept_id 23985
   * value 656) and “Data Fim” (concept_id 165308 value 1267)) MINUS - Obs_datetime (from the
   * clinical consultation with “PROFILAXIA = * INH” (concept_id 23985 value 656) and “Data Fim”
   * (concept_id 165308 value 1267) between 6 months and 9 months
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithProphylaxyDuringRevisionPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Prophylaxy Treatment within Revision Period");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  p.patient_id   "
            + " FROM  patient p   "
            + "    INNER JOIN   "
            + "            ( "
            + "                SELECT p.patient_id, MAX(o2.obs_datetime) AS e_datetime   "
            + "                FROM patient p   "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "                WHERE   "
            + "                    e.location_id = :location   "
            + "                        AND e.encounter_type = ${6}   "
            + "                        AND ((o.concept_id = ${23985}   "
            + "                        AND o.value_coded = ${656})   "
            + "                        AND (o2.concept_id = ${165308}   "
            + "                        AND o2.value_coded = ${1256}))   "
            + "                        AND o2.obs_datetime >= :startDate   "
            + "                        AND o2.obs_datetime <= :endDate   "
            + "                        AND p.voided = 0   "
            + "                        AND e.voided = 0   "
            + "                        AND o.voided = 0   "
            + "                        AND o2.voided = 0   "
            + "                GROUP BY p.patient_id "
            + "            ) AS b4 ON p.patient_id = b4.patient_id   "
            + "    INNER JOIN   "
            + "            ( "
            + "                SELECT  p.patient_id, MAX(o2.obs_datetime) AS e_datetime   "
            + "                FROM  patient p   "
            + "                    INNER JOIN  encounter e ON p.patient_id = e.patient_id   "
            + "                    INNER JOIN  obs o ON e.encounter_id = o.encounter_id   "
            + "                    INNER JOIN  obs o2 ON e.encounter_id = o2.encounter_id   "
            + "                WHERE  e.location_id = :location   "
            + "                    AND e.encounter_type = ${6}  "
            + "                    AND ((o.concept_id = ${23985}   "
            + "                    AND o.value_coded = ${656})   "
            + "                    AND (o2.concept_id = ${165308}   "
            + "                    AND o2.value_coded = ${1267}))   "
            + "                    AND o2.obs_datetime >= :startDate   "
            + "                    AND o2.obs_datetime <= :revisionEndDate   "
            + "                    AND p.voided = 0   "
            + "                    AND e.voided = 0   "
            + "                    AND o.voided = 0 "
            + "                GROUP BY p.patient_id "
            + "            ) AS g ON p.patient_id = g.patient_id "
            + " WHERE p.voided =0 "
            + "    AND TIMESTAMPDIFF(DAY,b4.e_datetime,g.e_datetime) >= 170 "
            + "    AND TIMESTAMPDIFF(DAY,b4.e_datetime,g.e_datetime) <= 297 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>“PROFILAXIA = 3HP” (concept_id 23985 value 23954) and “Data Fim” (concept_id 165308 value
   * 1267) Encounter_datetime between startDateRevision and endDateRevision and:
   *
   * <p>- Obs_datetime(from the last clinical consultation with “PROFILAXIA = 3HP” (concept_id 23985
   * value 23954) and “Data Fim” (concept_id 165308 value 1267)) MINUS - Obs_datetime (from the
   * clinical consultation with “PROFILAXIA = * 3HP” (concept_id 23985 value 23954) and “Data Fim”
   * (concept_id 165308 value 1267) between 6 months and 9 months
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithProphylaxyDuringRevisionPeriod3HP() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients with Prophylaxy Treatment within Revision Period for 3HP");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  p.patient_id   "
            + " FROM  patient p   "
            + "    INNER JOIN   "
            + "            ( "
            + "                SELECT p.patient_id, MAX(o2.obs_datetime) AS e_datetime   "
            + "                FROM patient p   "
            + "                    INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "                    INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "                    INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "                WHERE   "
            + "                    e.location_id = :location   "
            + "                        AND e.encounter_type = ${6}   "
            + "                        AND ((o.concept_id = ${23985}   "
            + "                        AND o.value_coded = ${23954})   "
            + "                        AND (o2.concept_id = ${165308}   "
            + "                        AND o2.value_coded = ${1256}))   "
            + "                        AND o2.obs_datetime >= :startDate   "
            + "                        AND o2.obs_datetime <= :endDate   "
            + "                        AND p.voided = 0   "
            + "                        AND e.voided = 0   "
            + "                        AND o.voided = 0   "
            + "                        AND o2.voided = 0   "
            + "                GROUP BY p.patient_id "
            + "            ) AS b4 ON p.patient_id = b4.patient_id   "
            + "    INNER JOIN   "
            + "            ( "
            + "                SELECT  p.patient_id, MAX(o2.obs_datetime) AS e_datetime   "
            + "                FROM  patient p   "
            + "                    INNER JOIN  encounter e ON p.patient_id = e.patient_id   "
            + "                    INNER JOIN  obs o ON e.encounter_id = o.encounter_id   "
            + "                    INNER JOIN  obs o2 ON e.encounter_id = o2.encounter_id   "
            + "                WHERE  e.location_id = :location   "
            + "                    AND e.encounter_type = ${6}  "
            + "                    AND ((o.concept_id = ${23985}   "
            + "                    AND o.value_coded = ${23954})   "
            + "                    AND (o2.concept_id = ${165308}   "
            + "                    AND o2.value_coded = ${1267}))   "
            + "                    AND o2.obs_datetime >= :startDate   "
            + "                    AND o2.obs_datetime <= :revisionEndDate   "
            + "                    AND p.voided = 0   "
            + "                    AND e.voided = 0   "
            + "                    AND o.voided = 0 "
            + "                GROUP BY p.patient_id "
            + "            ) AS g ON p.patient_id = g.patient_id "
            + " WHERE p.voided =0 "
            + "    AND TIMESTAMPDIFF(DAY,b4.e_datetime,g.e_datetime) >= 170 "
            + "    AND TIMESTAMPDIFF(DAY,b4.e_datetime,g.e_datetime) <= 297 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>- with “Diagnótico TB activo” (concept_id 23761) value coded “SIM”(concept id 1065)
   * Encounter_datetime between:
   *
   * <p>- ( obs_datetime (from the last MASTERCARD - Ficha Resumo (encounter 53) with Ultima
   * Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data Início)” (obs_datetime for
   * concept id 165308 value 1256) AND - obs_datetime (from the last clinical consultation
   * (encounter 6) with Ultima Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data *
   * Início)” (obs_datetime for concept id 165308 value 1256) ) PLUS 9 MONTHS
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB Diagnosis Active
   */
  public CohortDefinition getPatientsWithTBDiagActive() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Diagnosis Active INH");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("23761", tbMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        ""
            + " SELECT p.patient_id "
            + " FROM patient p "
            + "   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "FROM ( "
            + "         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND o2.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${6} "
            + "           AND ( ( o.concept_id = ${23985} "
            + "           AND     o.value_coded = ${656} ) "
            + "           AND   ( o2.concept_id = ${165308} "
            + "           AND     o2.value_coded = ${1256} "
            + "           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND o2.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${53} "
            + "           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "           AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256}  "
            + "           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "         GROUP BY p.patient_id "
            + "     ) AS tpt_start "
            + "GROUP BY tpt_start.patient_id) AS last ON p.patient_id = last.patient_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23761} "
            + "         AND o.value_coded IN (${1065}) "
            + "         AND e.encounter_datetime BETWEEN last.tpt_start_date AND DATE_ADD(last.tpt_start_date, INTERVAL 9 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>- with “Diagnótico TB activo” (concept_id 23761) value coded “SIM”(concept id 1065)
   * Encounter_datetime between:
   *
   * <p>- “Diagnótico TB activo” (concept_id 23761) value coded “SIM”(concept id 1065) and
   * Encounter_datetime between Encounter_datetime(the most recent from B5_1 or B5_2) and
   * Encounter_datetime(the most recent from B5_1 or B5_2) + 6 months
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB Diagnosis Active
   */
  public CohortDefinition getPatientsWithTBDiagActive3hp() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Diagnosis Active 3hp");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("23761", tbMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        ""
            + " SELECT p.patient_id "
            + " FROM patient p "
            + "   INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "FROM ( "
            + "         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND o2.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${6} "
            + "           AND ( ( o.concept_id = ${23985} "
            + "           AND     o.value_coded = ${23954} ) "
            + "           AND   ( o2.concept_id = ${165308} "
            + "           AND     o2.value_coded = ${1256} "
            + "           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "         FROM patient p "
            + "                  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND o.voided = 0 "
            + "           AND o2.voided = 0 "
            + "           AND e.location_id = :location "
            + "           AND e.encounter_type = ${53} "
            + "           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "           AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256}  "
            + "           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "         GROUP BY p.patient_id "
            + "     ) AS tpt_start "
            + "GROUP BY tpt_start.patient_id) AS last ON p.patient_id = last.patient_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23761} "
            + "         AND o.value_coded IN (${1065}) "
            + "         AND e.encounter_datetime BETWEEN last.tpt_start_date AND DATE_ADD(last.tpt_start_date, INTERVAL 6 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>- “TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” (concept_id IN [1065]) and
   * Encounter_datetime between:
   *
   * <p>- ( obs_datetime (from the last MASTERCARD - Ficha Resumo (encounter 53) with Ultima
   * Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data Início)” (obs_datetime for
   * concept id 165308 value 1256) AND - obs_datetime (from the last clinical consultation
   * (encounter 6) with Ultima Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data *
   * Início)” (obs_datetime for concept id 165308 value 1256) ) PLUS 9 MONTHS
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB symptoms
   */
  public CohortDefinition getPatientsWithTBSymtoms() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Symptoms INH");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("23758", tbMetadata.getHasTbSymptomsConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "            FROM ( "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( ( o.concept_id = ${23985} "
            + "                       AND     o.value_coded = ${656}) "
            + "                       AND   ( o2.concept_id = ${165308} "
            + "                       AND     o2.value_coded = ${1256} "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                     UNION "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "                       AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256}  "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                 ) AS tpt_start "
            + "            GROUP BY tpt_start.patient_id ) AS last ON p.patient_id = last.patient_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23758} "
            + "         AND o.value_coded IN (${1065}) "
            + "         AND e.encounter_datetime BETWEEN last.tpt_start_date AND DATE_ADD(last.tpt_start_date, INTERVAL 9 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients ll patients with a clinical consultation(encounter type 6) during the Revision
   * period with the following conditions:
   *
   * <p>- “TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” (concept_id IN [1065]) and
   * Encounter_datetime between:
   *
   * <p>- “TEM SINTOMAS DE TB” (concept_id 23758) value coded “SIM” (concept_id IN [1065]) and
   * Encounter_datetime between Encounter_datetime(from B5_1 or B5_2) and Encounter_datetime(from
   * B5_1 or B5_2) + 6 months
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB symptoms
   */
  public CohortDefinition getPatientsWithTBSymtoms3HP() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Symptoms 3hp");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("23758", tbMetadata.getHasTbSymptomsConcept().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("6128", hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "            FROM ( "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( ( o.concept_id = ${23985} "
            + "                       AND     o.value_coded = ${23954}) "
            + "                       AND   ( o2.concept_id = ${165308} "
            + "                       AND     o2.value_coded = ${1256} "
            + "                       AND o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                     UNION "
            + "                     SELECT p.patient_id, MAX(o2.value_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                       AND   ( o2.concept_id = ${6128} "
            + "                       AND     o2.value_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                 ) AS tpt_start "
            + "            GROUP BY tpt_start.patient_id ) AS last ON p.patient_id = last.patient_id "
            + " WHERE "
            + "     e.location_id = :location "
            + "         AND e.encounter_type = ${6} "
            + "         AND o.concept_id = ${23758} "
            + "         AND o.value_coded IN (${1065}) "
            + "         AND e.encounter_datetime BETWEEN last.tpt_start_date AND DATE_ADD(last.tpt_start_date, INTERVAL 6 MONTH) "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND o.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients with a clinical consultation(encounter type 6) during the Revision period with the
   * following conditions:
   *
   * <p>- “TRATAMENTO DE TUBERCULOSE”(concept_id 1268) value coded “Inicio” or “Continua” or
   * “Fim”(concept_id IN [1256, 1257, 1267]) “Data Tratamento TB” (obs datetime 1268) between:
   *
   * <p>- ( obs_datetime (from the last MASTERCARD - Ficha Resumo (encounter 53) with Ultima
   * Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data Início)” (obs_datetime for
   * concept id 165308 value 1256) AND - obs_datetime (from the last clinical consultation
   * (encounter 6) with Ultima Profilaxia TPT (concept_id 23985) = INH (concept = 656) and (Data *
   * Início)” (obs_datetime for concept id 165308 value 1256) ) PLUS 9 MONTHS
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB treatment
   */
  public CohortDefinition getPatientsWithTBTreatment() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Treatment INH");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1268", tbMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  p.patient_id  "
            + " FROM  patient p  "
            + "     INNER JOIN  encounter e ON p.patient_id = e.patient_id  "
            + "     INNER JOIN  obs o ON e.encounter_id = o.encounter_id  "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "            FROM ( "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( ( o.concept_id = ${23985} "
            + "                       AND     o.value_coded = ${656}) "
            + "                       AND   ( o2.concept_id = ${165308} "
            + "                       AND     o2.value_coded = ${1256} "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                     UNION "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND e.location_id = :location "
            + "                       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} ) "
            + "                       AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256}  "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                 ) AS tpt_start "
            + "            GROUP BY tpt_start.patient_id ) AS last ON last.patient_id = p.patient_id  "
            + " WHERE  "
            + "     e.location_id = :location  "
            + "         AND e.encounter_type = ${6}  "
            + "         AND o.concept_id = ${1268}  "
            + "         AND o.value_coded IN (${1256} , ${1257}, ${1267})  "
            + "         AND DATE(o.obs_datetime) between DATE(last.tpt_start_date) AND DATE(DATE_ADD(last.tpt_start_date, INTERVAL 9 MONTH))  "
            + "         AND p.voided = 0  "
            + "         AND e.voided = 0  "
            + "         AND o.voided = 0";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * All patients with a clinical consultation(encounter type 6) during the Revision period with the
   * following conditions:
   *
   * <p>- “TRATAMENTO DE TUBERCULOSE”(concept_id 1268) value coded “Inicio” or “Continua” or
   * “Fim”(concept_id IN [1256, 1257, 1267]) “Data Tratamento TB” (obs datetime 1268) between:
   *
   * <p>- “TRATAMENTO DE TUBERCULOSE”(concept_id 1268) value coded “Inicio” or “Continua” or
   * “Fim”(concept_id IN [1256, 1257, 1267]) and “Data Tratamento TB” (obs datetime 1268) between
   * Encounter_datetime(from B5_1 or B5_2) and Encounter_datetime(from B5_1 or B5_2) + 6 months
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with TB treatment
   */
  public CohortDefinition getPatientsWithTBTreatment3HP() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with TB Treatment 3hp");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugsConcept().getConceptId());
    map.put("1268", tbMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT  p.patient_id  "
            + " FROM  patient p  "
            + "     INNER JOIN  encounter e ON p.patient_id = e.patient_id  "
            + "     INNER JOIN  obs o ON e.encounter_id = o.encounter_id  "
            + "     INNER JOIN (SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date "
            + "            FROM ( "
            + "                     SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND ( ( o.concept_id = ${23985} "
            + "                       AND     o.value_coded = ${23954}) "
            + "                       AND   ( o2.concept_id = ${165308} "
            + "                       AND     o2.value_coded = ${1256} "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                     UNION "
            + "                     SELECT p.patient_id, MAX(o2.value_datetime) last_encounter "
            + "                     FROM patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "                     WHERE p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o2.voided = 0 "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND e.location_id = :location "
            + "                       AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                       AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                       AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "                     GROUP BY p.patient_id "
            + "                 ) AS tpt_start "
            + "            GROUP BY tpt_start.patient_id ) AS last ON last.patient_id = p.patient_id  "
            + " WHERE  "
            + "     e.location_id = :location  "
            + "         AND e.encounter_type = ${6}  "
            + "         AND o.concept_id = ${1268}  "
            + "         AND o.value_coded IN (${1256} , ${1257}, ${1267})  "
            + "         AND DATE(o.obs_datetime) between DATE(last.tpt_start_date) AND DATE(DATE_ADD(last.tpt_start_date, INTERVAL 6 MONTH))  "
            + "         AND p.voided = 0  "
            + "         AND e.voided = 0  "
            + "         AND o.voided = 0";

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
   * @param den boolean parameter, true indicates denominator ,false indicates numerator
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
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition nutritionalClass = getNutritionalBCat5();

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition nutSupport = getPatientsWithNutritionalStateAndNutritionalSupport();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(nutritionalClass, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(nutSupport, MAPPING));

    if (den) {
      compositionCohortDefinition.setCompositionString("(A AND B) AND NOT (C OR D OR E)");
    } else {
      compositionCohortDefinition.setCompositionString("(A AND B) AND NOT (C OR D OR E) AND F");
    }
    return compositionCohortDefinition;
  }

  /**
   * B - Filter all patients with nutritional state equal to “DAM” or “DAG” registered on a clinical
   * consultation during the period:
   *
   * <p>All patients registered in Ficha Clinica (encounter type=6) with “CLASSIFICAÇÃO DE
   * DESNUTRIÇÃO” (concept_id = 6336) and value_coded equal to “DAG” (concept_id = 1844) or equal to
   * “DAM” (concept_id = 68) during the encounter_datetime >= startDateRevision and
   * <=endDateRevision
   *
   * <p>Note: consider the first clinical consultation with nutritional state equal to “DAM” or
   * “DAG” occurred during the revision period.
   *
   * @return SqlCohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with B criteria
   */
  public SqlCohortDefinition getNutritionalBCat5() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1844", hivMetadata.getChronicMalnutritionConcept().getConceptId());
    map.put("68", hivMetadata.getMalnutritionConcept().getConceptId());
    map.put("6336", commonMetadata.getClassificationOfMalnutritionConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "           INNER JOIN(SELECT p.patient_id, "
            + "                             Min(e.encounter_datetime) AS min_nutritional "
            + "                      FROM   patient p "
            + "                                 INNER JOIN encounter e "
            + "                                            ON e.patient_id = p.patient_id "
            + "                                 INNER JOIN obs o "
            + "                                            ON o.encounter_id = e.encounter_id "
            + "                      WHERE  p.voided = 0 "
            + "                        AND e.voided = 0 "
            + "                        AND o.voided = 0 "
            + "                        AND e.encounter_type = ${6} "
            + "                        AND o.concept_id = ${6336} "
            + "                        AND o.value_coded IN (${1844},${68}) "
            + "                        AND e.location_id = :location "
            + "                        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                      GROUP  BY p.patient_id) AS list "
            + "                     ON list.patient_id = p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
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
   * @param den boolean parameter, true indicates denominator ,false indicates numerator
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
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition nutritionalClass = getNutritionalBCat5();

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition nutSupport = getPatientsWithNutritionalStateAndNutritionalSupport();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(nutritionalClass, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

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
   * @param den indicator number
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
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mapping = "startDate=${startDate},endDate=${revisionEndDate},location=${location}";

    CohortDefinition startedART = getMOHArtStartDate();

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
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(tbActive, mapping));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

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
   * <b>MQ6NUM</b>: Melhoria de Qualidade Category 6 <br>
   * <i> NUMERATOR 1: (A AND F) AND NOT (B OR C OR D OR E)</i> <br>
   * <i> NUMERATOR 2: A AND F NOT (B OR C OR D OR E)</i> <br>
   * <i> NUMERATOR 3: (A AND C AND F) AND NOT (B OR D OR E)</i> <br>
   * <i> NUMERATOR 4: (A AND D AND F) AND NOT (B OR C OR E)</i> <br>
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
   *   <li>
   *   <li>D - Filter all patients with the last Ficha Clinica(encounter type 6) during the revision
   *       period with the following conditions: “TEM SINTOMAS DE TB” (concept id 23758) value coded
   *       “SIM” or “NÃO”(concept id IN [1065, 1066]) and Encounter_datetime between
   *       startDateRevision and endDateRevision (should be the last encounter during the revision
   *       period)
   * </ul>
   *
   * @param num indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ6NUM(Integer num) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (num == 1) {
      compositionCohortDefinition.setName(
          "% de adultos HIV+ em TARV rastreados para TB na última consulta clínica");
    } else if (num == 2) {
      compositionCohortDefinition.setName(
          "% de crianças HIV+ em TARV rastreadas para TB na última consulta clínica");
    } else if (num == 3) {
      compositionCohortDefinition.setName(
          "% de mulheres grávidas HIV+ rastreadas para TB na última consulta clínica");
    } else if (num == 4) {
      compositionCohortDefinition.setName(
          "% de mulheres lactantes HIV+ rastreadas para TB  na última consulta");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mapping = "startDate=${startDate},endDate=${revisionEndDate},location=${location}";

    CohortDefinition startedART = getMOHArtStartDate();

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
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition tbSymptoms =
        QualityImprovement2020Queries.getPatientsWithTBSymptoms(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getTBSymptomsConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId());

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B", EptsReportUtils.map(tbActive, mapping));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(tbSymptoms, MAPPING1));

    if (num == 1 || num == 2) {
      compositionCohortDefinition.setCompositionString("(A AND F) AND NOT (B OR C OR D OR E)");
    } else if (num == 3) {
      compositionCohortDefinition.setCompositionString("(A AND C AND F) AND NOT (B OR D OR E)");
    } else if (num == 4) {
      compositionCohortDefinition.setCompositionString("(A AND D AND F) AND NOT (B OR C OR E)");
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
   * @param den indicator number
   */
  public CohortDefinition getMQ7A(Integer den) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den == 1 || den == 3) {
      compositionCohortDefinition.setName("A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 2 || den == 4) {
      compositionCohortDefinition.setName(
          "(A AND (B41 OR B42 OR B51 OR B52)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    } else if (den == 5) {
      compositionCohortDefinition.setName("(A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (den == 6) {
      compositionCohortDefinition.setName(
          "(A AND (B41 OR B42 OR B51 OR B52) AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition b41 = getB4And1();
    CohortDefinition b42 = getB4And2();
    CohortDefinition b51 = getB5And1();
    CohortDefinition b52 = getB5And2();

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
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition tbDiagOnPeriod = getPatientsWithTBDiagActive();

    CohortDefinition tbDiagOnPeriod3HP = getPatientsWithTBDiagActive3hp();

    CohortDefinition tbSymptomsOnPeriod = getPatientsWithTBSymtoms();

    CohortDefinition tbSymptomsOnPeriod3hp = getPatientsWithTBSymtoms3HP();

    CohortDefinition tbTreatmentOnPeriod = getPatientsWithTBTreatment();

    CohortDefinition tbTreatmentOnPeriod3hp = getPatientsWithTBTreatment3HP();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(tbActive, MAPPING));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(tbSymptoms, MAPPING));

    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(tbTreatment, MAPPING));

    compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(tbProphilaxy, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(tbDiagOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch("H1", EptsReportUtils.map(tbDiagOnPeriod3HP, MAPPING));

    compositionCohortDefinition.addSearch("I", EptsReportUtils.map(tbSymptomsOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch(
        "I1", EptsReportUtils.map(tbSymptomsOnPeriod3hp, MAPPING));

    compositionCohortDefinition.addSearch("J", EptsReportUtils.map(tbTreatmentOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch(
        "J1", EptsReportUtils.map(tbTreatmentOnPeriod3hp, MAPPING));

    compositionCohortDefinition.addSearch("B41", EptsReportUtils.map(b41, MAPPING));

    compositionCohortDefinition.addSearch("B42", EptsReportUtils.map(b42, MAPPING));

    compositionCohortDefinition.addSearch("B51", EptsReportUtils.map(b51, MAPPING));

    compositionCohortDefinition.addSearch("B52", EptsReportUtils.map(b52, MAPPING));

    if (den == 1 || den == 3) {
      compositionCohortDefinition.setCompositionString(
          "A AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (den == 2 || den == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42 OR B51 OR B52)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    } else if (den == 5) {
      compositionCohortDefinition.setCompositionString(
          "(A AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (den == 6) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42 OR B51 OR B52) AND C) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
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
   * @param num indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ7B(Integer num) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setName(
          "(A AND  (B41 OR B42 OR B51 OR B52)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 2 || num == 4) {
      compositionCohortDefinition.setName(
          "(A AND (B41 OR B42 OR B51 OR B52) AND (GNEW OR L)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    } else if (num == 5) {
      compositionCohortDefinition.setName(
          "(A AND C AND (B41 OR B42 OR B51 OR B52) ) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (num == 6) {
      compositionCohortDefinition.setName(
          "(A AND (B41 OR B42 OR B51 OR B52) AND C AND (GNEW OR L)) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

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
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition tbProphylaxyOnPeriod = getPatientsWithProphylaxyDuringRevisionPeriod();

    CohortDefinition tbProphylaxyOnPeriod3hp = getPatientsWithProphylaxyDuringRevisionPeriod3HP();

    CohortDefinition tbDiagOnPeriod = getPatientsWithTBDiagActive();

    CohortDefinition tbDiagOnPeriod3HP = getPatientsWithTBDiagActive3hp();

    CohortDefinition tbSymptomsOnPeriod = getPatientsWithTBSymtoms();

    CohortDefinition tbSymptomsOnPeriod3hp = getPatientsWithTBSymtoms3HP();

    CohortDefinition tbTreatmentOnPeriod = getPatientsWithTBTreatment();

    CohortDefinition tbTreatmentOnPeriod3hp = getPatientsWithTBTreatment3HP();

    CohortDefinition b41 = getB4And1();

    CohortDefinition b42 = getB4And2();

    CohortDefinition b51 = getB5And1();

    CohortDefinition b52 = getB5And2();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(tbActive, MAPPING));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(tbSymptoms, MAPPING));

    compositionCohortDefinition.addSearch("B3", EptsReportUtils.map(tbTreatment, MAPPING));

    compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(tbProphilaxy, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    compositionCohortDefinition.addSearch("G", EptsReportUtils.map(tbProphylaxyOnPeriod, MAPPING1));

    compositionCohortDefinition.addSearch(
        "G1", EptsReportUtils.map(tbProphylaxyOnPeriod3hp, MAPPING1));

    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(tbDiagOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch("H1", EptsReportUtils.map(tbDiagOnPeriod3HP, MAPPING));

    compositionCohortDefinition.addSearch("I", EptsReportUtils.map(tbSymptomsOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch(
        "I1", EptsReportUtils.map(tbSymptomsOnPeriod3hp, MAPPING));

    compositionCohortDefinition.addSearch("J", EptsReportUtils.map(tbTreatmentOnPeriod, MAPPING));

    compositionCohortDefinition.addSearch(
        "J1", EptsReportUtils.map(tbTreatmentOnPeriod3hp, MAPPING));

    compositionCohortDefinition.addSearch("B41", EptsReportUtils.map(b41, MAPPING));

    compositionCohortDefinition.addSearch("B42", EptsReportUtils.map(b42, MAPPING));

    compositionCohortDefinition.addSearch("B51", EptsReportUtils.map(b51, MAPPING));

    compositionCohortDefinition.addSearch("B52", EptsReportUtils.map(b52, MAPPING));

    compositionCohortDefinition.addSearch("GNEW", EptsReportUtils.map(getGNew(), MAPPING1));

    compositionCohortDefinition.addSearch("L", EptsReportUtils.map(getGNew3HP(), MAPPING1));

    if (num == 1 || num == 3) {
      compositionCohortDefinition.setCompositionString(
          "(A AND  (B41 OR B42 OR B51 OR B52)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F)");
    } else if (num == 2 || num == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42 OR B51 OR B52) AND (GNEW OR L)) AND NOT (B1 OR B2 OR B3 OR C OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
    } else if (num == 5) {
      compositionCohortDefinition.setCompositionString(
          "(A AND C AND (B41 OR B42 OR B51 OR B52) ) AND NOT (B1 OR B2 OR B3 OR D OR E OR F)");
    } else if (num == 6) {
      compositionCohortDefinition.setCompositionString(
          "(A AND (B41 OR B42 OR B51 OR B52) AND C AND (GNEW OR L)) AND NOT (B1 OR B2 OR B3 OR D OR E OR F OR H OR H1 OR I OR I1 OR J OR J1)");
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
   * @param indicatorFlag indicator number
   * @param reportSource report Source (MQ or MI)
   * @return CohortDefinition
   * @params indicatorFlag A to G For inicator 11.1 to 11.7 respectively
   */
  public CohortDefinition getMQC11DEN(int indicatorFlag, MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    boolean useE53 = false;

    compositionCohortDefinition.setName(
        "% adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition patientsFromFichaClinicaLinhaTerapeutica =
        getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition patientsFromFichaClinicaCargaViral = getB2_13(useE53);

    CohortDefinition pregnantWithCargaViralHigherThan1000;
    CohortDefinition breastfeedingWithCargaViralHigherThan1000;

    if (indicatorFlag == 4) {
      pregnantWithCargaViralHigherThan1000 =
          QualityImprovement2020Queries.getMQ13DenB4_P4(
              hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
              hivMetadata.getHivViralLoadConcept().getConceptId(),
              hivMetadata.getYesConcept().getConceptId(),
              commonMetadata.getPregnantConcept().getConceptId(),
              50);

      breastfeedingWithCargaViralHigherThan1000 =
          QualityImprovement2020Queries.getMQ13DenB5_P4(
              hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
              hivMetadata.getHivViralLoadConcept().getConceptId(),
              hivMetadata.getYesConcept().getConceptId(),
              commonMetadata.getBreastfeeding().getConceptId(),
              50);

    } else {
      pregnantWithCargaViralHigherThan1000 =
          QualityImprovement2020Queries.getMQ13DenB4_P4(
              hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
              hivMetadata.getHivViralLoadConcept().getConceptId(),
              hivMetadata.getYesConcept().getConceptId(),
              commonMetadata.getPregnantConcept().getConceptId());

      breastfeedingWithCargaViralHigherThan1000 =
          QualityImprovement2020Queries.getMQ13DenB5_P4(
              hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
              hivMetadata.getHivViralLoadConcept().getConceptId(),
              hivMetadata.getYesConcept().getConceptId(),
              commonMetadata.getBreastfeeding().getConceptId());
    }

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transfOut = getTranferredOutPatients();

    if (reportSource.equals(MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));
      compositionCohortDefinition.addSearch(
          "B1", EptsReportUtils.map(patientsFromFichaClinicaLinhaTerapeutica, MAPPING1));

      compositionCohortDefinition.addSearch(
          "B2", EptsReportUtils.map(patientsFromFichaClinicaCargaViral, MAPPING1));

      compositionCohortDefinition.addSearch(
          "B4", EptsReportUtils.map(pregnantWithCargaViralHigherThan1000, MAPPING1));

      compositionCohortDefinition.addSearch(
          "B5", EptsReportUtils.map(breastfeedingWithCargaViralHigherThan1000, MAPPING1));

      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING1));
    } else if (reportSource.equals(MIMQ.MI)) {

      if (indicatorFlag == 1) {
        compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING5));
      }
      if (indicatorFlag == 1 || indicatorFlag == 3 || indicatorFlag == 5 || indicatorFlag == 6) {
        compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING4));
        compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING6));
        compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING6));
        compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING6));
      } else {
        compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));
      }
      if (indicatorFlag == 2 || indicatorFlag == 4 || indicatorFlag == 7) {
        compositionCohortDefinition.addSearch(
            "B1", EptsReportUtils.map(patientsFromFichaClinicaLinhaTerapeutica, MAPPING8));

        compositionCohortDefinition.addSearch(
            "B2", EptsReportUtils.map(patientsFromFichaClinicaCargaViral, MAPPING8));

        compositionCohortDefinition.addSearch(
            "B4", EptsReportUtils.map(pregnantWithCargaViralHigherThan1000, MAPPING8));

        compositionCohortDefinition.addSearch(
            "B5", EptsReportUtils.map(breastfeedingWithCargaViralHigherThan1000, MAPPING8));

        compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
        compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));
        compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING7));
        compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING9));
      }
      if (indicatorFlag == 3 || indicatorFlag == 5 || indicatorFlag == 6) {
        compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transfOut, MAPPING5));
      }
    }

    if (indicatorFlag == 1) {
      compositionCohortDefinition.setCompositionString("((A AND D) AND NOT (C OR E OR F))");
    }
    if (indicatorFlag == 5 || indicatorFlag == 6) {
      compositionCohortDefinition.setCompositionString("A AND NOT (C OR D OR E OR F)");
    }
    if (indicatorFlag == 2) {
      compositionCohortDefinition.setCompositionString("((B1 AND B2 AND B5) AND NOT (B4 OR F))");
    }
    if (indicatorFlag == 7) {
      compositionCohortDefinition.setCompositionString("((B1 AND B2) AND NOT (B4 OR B5 OR F))");
    }
    if (indicatorFlag == 3) {
      compositionCohortDefinition.setCompositionString("(A AND C) AND NOT (D OR E OR F)");
    }
    if (indicatorFlag == 4) {
      compositionCohortDefinition.setCompositionString("((B1 AND B4) AND NOT F)");
    }

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
   * @param indicatorFlag indicator number
   */
  public CohortDefinition getMQC12P2DEN(Integer indicatorFlag) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (indicatorFlag == 3)
      compositionCohortDefinition.setName(
          "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás");
    if (indicatorFlag == 4)
      compositionCohortDefinition.setName(
          "Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás");
    if (indicatorFlag == 7)
      compositionCohortDefinition.setName(
          "Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás");
    if (indicatorFlag == 8)
      compositionCohortDefinition.setName(
          "Crianças (0-14 anos) que iniciaram 2ª linha TARV há 12 meses atrás");
    if (indicatorFlag == 11)
      compositionCohortDefinition.setName(
          "Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition b1 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition b1E = getPatientsFromFichaClinicaDenominatorB1EOrB2E(true);

    CohortDefinition b2 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsSecondLine_B2();

    CohortDefinition b2E = getPatientsFromFichaClinicaDenominatorB1EOrB2E(false);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING2));

    compositionCohortDefinition.addSearch(
        "B1",
        EptsReportUtils.map(
            b1,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location},revisionEndDate=${revisionEndDate}"));

    compositionCohortDefinition.addSearch(
        "B1E", EptsReportUtils.map(b1E, "location=${location},revisionEndDate=${revisionEndDate}"));

    compositionCohortDefinition.addSearch(
        "B2",
        EptsReportUtils.map(
            b2,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location},revisionEndDate=${revisionEndDate}"));

    compositionCohortDefinition.addSearch(
        "B2E", EptsReportUtils.map(b2E, "location=${location},revisionEndDate=${revisionEndDate}"));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            transfOut,
            "startDate=${revisionEndDate-14m},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate-11m},location=${location}"));

    compositionCohortDefinition.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(0, 14, true),
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate-11m},location=${location}"));

    if (indicatorFlag == 3) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B1) AND NOT B1E AND NOT (C OR D OR F) AND ADULT");
    }
    if (indicatorFlag == 4) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B2) AND NOT B2E AND NOT (C OR D OR F) AND ADULT");
    }
    if (indicatorFlag == 7) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B1) AND NOT B1E AND NOT (C OR D OR F) AND CHILDREN");
    }
    if (indicatorFlag == 8) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B2) AND NOT B2E AND NOT (C OR D OR F) AND CHILDREN");
    }
    if (indicatorFlag == 11) {
      compositionCohortDefinition.setCompositionString(
          "(A AND B1 AND C) AND NOT B1E AND NOT (D OR F)");
    }

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
   * @param indicator indicator number
   * @return CohortDefinition
   * @params indicatorFlag A to F For inicator 13.2 to 13.14 accordingly to the specs
   */
  public CohortDefinition getMQC13P3DEN(int indicator) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (indicator == 2)
      compositionCohortDefinition.setName(
          "Adultos (15/+anos) que iniciaram a 1a linha de TARV ou novo regime da 1ª linha há 9 meses atrás");
    if (indicator == 9)
      compositionCohortDefinition.setName(
          "Crianças (0-4 anos de idade) com registo de início da 1a linha de TARV há 9 meses");
    if (indicator == 10)
      compositionCohortDefinition.setName(
          "Crianças  (5-9 anos de idade) com registo de início da 1a linha de TARV ou novo regime de TARV há 9 meses");
    if (indicator == 11)
      compositionCohortDefinition.setName(
          "Crianças  (10-14 anos de idade) com registo de início da 1a linha de TARV ou novo regime da 1ª linha de TARV no mês de avaliação");
    if (indicator == 5)
      compositionCohortDefinition.setName(
          "Adultos (15/+ anos) com registo de início da 2a linha de TARV há 9 meses");
    if (indicator == 14)
      compositionCohortDefinition.setName(
          "Crianças com registo de início da 2a linha de TARV no mês de avaliação");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String mapping2 =
        "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}";

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition b1Patients = getPatientsOnRegimeChangeBI1AndNotB1E_B1();

    CohortDefinition b2NewPatients = getPatientsOnRegimeArvSecondLine();

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transfOut = getTranferredOutPatients();

    CohortDefinition abandonedTarv = getPatientsWhoAbandonedInTheLastSixMonthsFromFirstLineDate();
    CohortDefinition abandonedFirstLine = getPatientsWhoAbandonedTarvOnOnFirstLineDate();
    CohortDefinition abandonedSecondLine = getPatientsWhoAbandonedTarvOnOnSecondLineDate();

    if (indicator == 2) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Adultos (15/+anos) que iniciaram a 1a linha de TARV ou novo regime da 1ª linha há 9 meses atrás",
                  15,
                  null),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 5) {

      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              getAgeOnObsDatetime(15, null),
              "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    } else if (indicator == 9) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças (0-4 anos de idade) com registo de início da 1a linha de TARV há 9 meses",
                  0,
                  4),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 10) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças  (5-9 anos de idade) com registo de início da 1a linha de TARV ou novo regime de TARV há 9 meses",
                  5,
                  9),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 11) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças  (10-14 anos de idade) com registo de início da 1a linha de TARV ou novo regime da 1ª linha de TARV no mês de avaliação",
                  10,
                  14),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 14) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              getAgeOnObsDatetime(2, 14),
              "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    }

    compositionCohortDefinition.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1Patients, mapping2));

    compositionCohortDefinition.addSearch("B2New", EptsReportUtils.map(b2NewPatients, MAPPING));

    compositionCohortDefinition.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    compositionCohortDefinition.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    compositionCohortDefinition.addSearch(
        "DD", EptsReportUtils.map(txMlCohortQueries.getDeadPatientsComposition(), MAPPING3));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch(
        "F",
        EptsReportUtils.map(
            transfOut,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "ABANDONEDTARV", EptsReportUtils.map(abandonedTarv, MAPPING1));

    compositionCohortDefinition.addSearch(
        "ABANDONED1LINE", EptsReportUtils.map(abandonedFirstLine, MAPPING1));

    compositionCohortDefinition.addSearch(
        "ABANDONED2LINE", EptsReportUtils.map(abandonedSecondLine, MAPPING1));

    if (indicator == 2 || indicator == 9 || indicator == 10 || indicator == 11)
      compositionCohortDefinition.setCompositionString(
          "((A AND NOT C AND NOT D AND NOT ABANDONEDTARV) OR (B1 AND NOT ABANDONED1LINE)) AND NOT (F OR E OR DD) AND age");
    if (indicator == 5 || indicator == 14)
      compositionCohortDefinition.setCompositionString(
          "B2New AND NOT (F OR E OR DD OR ABANDONED2LINE) AND age");
    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>B1E- Select all patients from Ficha Clinica (encounter type 6) who have “LINHA
   *       TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA LINHA”(Concept
   *       id 21150) registered in the LAST consultation (encounter type 6) by endDateRevision
   *   <li>B2E- Select all patients from Ficha Clinica (encounter type 6) who have “LINHA
   *       TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “SEGUNDA LINHA”(Concept id
   *       21148) registered in the LAST consultation (encounter type 6) by endDateRevision
   * </ul>
   *
   * @param b1e Boolean parameter, true for b1e and false for b2e
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsFromFichaClinicaDenominatorB1EOrB2E(boolean b1e) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients From Ficha Clinica Denominator B1E Or B2E");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("21148", hivMetadata.getSecondLineConcept().getConceptId());

    StringBuilder query = new StringBuilder();
    query.append(" SELECT p.patient_id ");
    query.append(" FROM patient p ");
    query.append("    INNER JOIN encounter e ");
    query.append("        ON p.patient_id = e.patient_id ");
    query.append("    INNER JOIN obs o ");
    query.append("               ON o.encounter_id = e.encounter_id ");
    query.append("    INNER JOIN  (");
    query.append("                    SELECT p.patient_id, MAX(e.encounter_datetime) max_date ");
    query.append("                    FROM patient p ");
    query.append("                        INNER JOIN encounter e ");
    query.append("                            ON p.patient_id = e.patient_id ");
    query.append("                    WHERE ");
    query.append("                        p.voided = 0 ");
    query.append("                        AND e.voided = 0 ");
    query.append("                        AND e.encounter_type = ${6} ");
    query.append("                        AND e.location_id = :location ");
    query.append("                        AND e.encounter_datetime <= :revisionEndDate ");
    query.append("                     GROUP BY p.patient_id");
    query.append(")  AS last_ficha ON last_ficha.patient_id = p.patient_id ");
    query.append(" WHERE");
    query.append("    p.voided = 0 ");
    query.append("    AND e.voided = 0 ");
    query.append("    AND o.voided = 0 ");
    query.append("    AND e.encounter_type = ${6} ");
    query.append("    AND e.encounter_datetime = last_ficha.max_date ");
    query.append("    AND o.concept_id = ${21151} ");
    if (b1e) {
      query.append("  AND o.value_coded <>  ${21150} ");

    } else {
      query.append("  AND o.value_coded <>  ${21148} ");
    }
    query.append("    AND e.location_id = :location ");
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B2</b>: Melhoria de Qualidade Category 13 Deniminator B2 <br>
   *
   * <ul>
   *   <li>B2- Select all patients from Ficha Clinica or Ficha Resumo(encounter type 6 or 53) with
   *       “Carga Viral” (Concept id 856) registered with numeric value >= 1000 during the Inclusion
   *       period (startDateInclusion and endDateInclusion). Note: if there is more than one record
   *       with value_numeric > 1000 than consider the first occurrence during the inclusion period.
   * </ul>
   *
   * @return CohortDefinition <strong>Should</strong> <strong>Should</strong> Returns empty if there
   *     is no patient who meets the conditions <strong>Should</strong> fetch all patients with B2
   *     criteria
   */
  public CohortDefinition getB2_13(boolean e53) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "All patients from Ficha Clinica with “Carga Viral” registered with numeric value > 1000 during the Inclusion period");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT"
            + "             p.patient_id"
            + "             FROM"
            + "             patient p"
            + "                INNER JOIN"
            + "             (SELECT"
            + "                p.patient_id, MIN(e.encounter_datetime) AS first_encounter"
            + "             FROM"
            + "                patient p"
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id"
            + "             JOIN obs o ON o.encounter_id = e.encounter_id"
            + "             WHERE p.voided = 0"
            + "                    AND e.voided = 0"
            + "                    AND o.voided = 0"
            + "                    AND e.location_id = :location"
            + "                    AND o.location_id = :location"
            + "                    AND o.concept_id = ${856}"
            + "                    AND o.value_numeric >= 1000"
            + "                    AND ("
            + "                         ( e.encounter_type = ${6} "
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate) ";

    if (e53) {
      query +=
          "                         OR (e.encounter_type = ${53} "
              + "                         AND o.obs_datetime BETWEEN :startDate AND :endDate)";
    }
    query +=
        "                   ) "
            + "               GROUP BY p.patient_id) filtered ON p.patient_id = filtered.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B2</b>: Melhoria de Qualidade Category 11 Deniminator B1 <br>
   * <i> A and not B</i> <br>
   *
   * <ul>
   *   <li>B1 – Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   *       TERAPEUTICA”(Concept id 21151) during the Inclusion period (startDateInclusion and
   *       endDateInclusion) and the value coded is “PRIMEIRA LINHA”(Concept id 21150)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients From Ficha Clinica With Last Therapeutic Line Set As First Line");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          JOIN obs o "
            + "                            ON o.encounter_id = e.encounter_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND o.voided = 0 "
            + "                          AND o.concept_id = ${21151} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) filtered "
            + "               ON p.patient_id = filtered.patient_id "
            + "WHERE  e.encounter_datetime = filtered.encounter_datetime "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${21151} "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.value_coded = ${21150} "
            + "       AND e.encounter_type = ${6};  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * B2- Select all patients from Ficha Clinica (encounter type 6) who have THE LAST “LINHA
   * TERAPEUTICA”(Concept id 21151) during the Inclusion period (startDateInclusion =
   * endDateRevision - 14 months and endDateInclusion = endDateRevision - 11 months) and the value
   * coded is “SEGUNDA LINHA”(Concept id 21148)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsSecondLine_B2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients From Ficha Clinica With Last Therapeutic Line Set As Second Line B2");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21148", hivMetadata.getSecondLineConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                          JOIN obs o "
            + "                            ON o.encounter_id = e.encounter_id "
            + "                   WHERE  e.encounter_type = ${6} "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND o.voided = 0 "
            + "                          AND o.concept_id = ${21151} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) filtered "
            + "               ON p.patient_id = filtered.patient_id "
            + "WHERE  e.encounter_datetime = filtered.encounter_datetime "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${21151} "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.value_coded = ${21148} "
            + "       AND e.encounter_type = ${6};  ";

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
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

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
   *   <li>G1 - FIRST consultation (Encounter_datetime (from encounter type 35)) >= “ART Start Date”
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
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
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
        "firstApss",
        EptsReportUtils.map(
            firstApss,
            "onOrAfter=${startDate},onOrBefore=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "secondApss",
        EptsReportUtils.map(
            secondApss,
            "onOrAfter=${startDate},onOrBefore=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "thirdApss",
        EptsReportUtils.map(
            thirdApss,
            "onOrAfter=${startDate},onOrBefore=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("firstApss AND secondApss AND thirdApss");

    return compositionCohortDefinition;
  }

  /**
   * H1 - One Consultation (Encounter_datetime (from encounter type 35)) on the same date when the
   * Viral Load with >=1000 result was recorded (oldest date from B2)
   *
   * @param vlQuantity Quantity of viral load to evaluate
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NH1(int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

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
            + "                        AND o.concept_id = ${856} AND o.value_numeric >= "
            + vlQuantity
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

  /**
   * H2- Another consultation (Encounter_datetime (from encounter type 35)) > “1st consultation”
   * (oldest date from H1)+20 days and <=“1st consultation” (oldest date from H1)+33days
   *
   * @param vlQuantity Quantity of viral load to evaluate
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NH2(int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

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
            + "                                        AND o.concept_id = ${856} AND o.value_numeric > "
            + vlQuantity
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

  /**
   * H3- Another consultation (Encounter_datetime (from encounter type 35)) > “2nd consultation”
   * (oldest date from H2)+20 days and <=“2nd consultation” (oldest date from H2)+33days
   *
   * @param vlQuantity Quantity of viral load to evaluate
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NH3(int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

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
            + "                                                            AND o.concept_id = ${856} AND o.value_numeric > "
            + vlQuantity
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
   *       the Viral Load with >= 1000 result was recorded (oldest date from B2) AND
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
  public CohortDefinition getMQC11NH(boolean numerator4) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.setName("Category 11 Numerator session G");

    CohortDefinition h1;
    CohortDefinition h2;
    CohortDefinition h3;

    if (numerator4) {
      h1 = getMQC11NH1(50);
      h2 = getMQC11NH2(50);
      h3 = getMQC11NH3(50);
    } else {
      h1 = getMQC11NH1(1000);
      h2 = getMQC11NH2(1000);
      h3 = getMQC11NH3(1000);
    }

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch("h1", EptsReportUtils.map(h1, mapping));
    compositionCohortDefinition.addSearch("h2", EptsReportUtils.map(h2, mapping));
    compositionCohortDefinition.addSearch("h3", EptsReportUtils.map(h3, mapping));

    compositionCohortDefinition.setCompositionString("h1 AND h2 AND h3");

    return compositionCohortDefinition;
  }

  public CohortDefinition getMQC11NH() {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "indicator", Mapped.mapStraightThrough(getMQC11NH(false)));

    compositionCohortDefinition.setCompositionString("indicator");

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
        "Category 11 - numerator - Session I - Interval of 99 Days for APSS consultations after ART start date");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

    cd.addCalculationParameter("considerTransferredIn", false);
    cd.addCalculationParameter("considerPharmacyEncounter", true);

    return cd;
  }

  /**
   * 11.1. % de adultos em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de
   * APSS/PP nos primeiros 3 meses após início do TARV (Line 56 in the template) Numerador (Column D
   * in the Template) as following: <code>
   * A and NOT C and NOT D and NOT E and NOT F  AND G and Age > 14*</code>
   *
   * @param reportResource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFandGAdultss(MIMQ reportResource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.1 ");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMOHArtStartDate();

    CohortDefinition c =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition d =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition e =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition f = commonCohortQueries.getTranferredOutPatients();
    CohortDefinition g = getMQC11NG();

    if (reportResource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING1));
    } else if (reportResource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING4));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING6));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING6));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING6));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING5));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING10));
    }

    compositionCohortDefinition.setCompositionString("((A AND D AND G) AND NOT (C OR E OR F))");

    return compositionCohortDefinition;
  }

  /**
   * 11.2. % de pacientes na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de
   * APSS/PP mensais consecutivas para reforço de adesão (Line 57 in the template) Numerador (Column
   * D in the Template) as following: <code>
   * B1 and B2 and NOT C and NOT B5 and NOT E and NOT F AND H and  Age > 14*</code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumB1nB2notCnotDnotEnotEnotFnHandAdultss(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    boolean useE53 = false;

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.2 ");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition b2 = getB2_13(useE53);

    CohortDefinition b4 =
        QualityImprovement2020Queries.getMQ13DenB4_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId());

    CohortDefinition b5 =
        QualityImprovement2020Queries.getMQ13DenB5_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId());

    CohortDefinition f = getTranferredOutPatients();
    CohortDefinition h = getMQC11NH();

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING1));
      compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING1));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));
    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING8));
      compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING8));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING8));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING8));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING9));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING8));
    }

    compositionCohortDefinition.setCompositionString(
        "((B1 AND B2 AND B5 AND H) AND NOT (B4 OR F))");

    return compositionCohortDefinition;
  }

  /**
   * 11.3.% de MG em TARV com o mínimo de 3 consultas de seguimento de adesão na FM-ficha de APSS/PP
   * nos primeiros 3 meses após início do TARV (Line 58 in the template) Numerador (Column D in the
   * Template) as following: <code> A  and  C and NOT D and NOT E and NOT F  AND G </code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumAnB3nCnotDnotEnotEnotFnG(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.3");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMOHArtStartDate();

    CohortDefinition c =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition d =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition e =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());
    CohortDefinition f = getTranferredOutPatients();

    CohortDefinition g = getMQC11NG();

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING1));
    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING4));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING6));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING6));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING6));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING5));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING10));
    }

    compositionCohortDefinition.setCompositionString("A AND C AND NOT D AND NOT E AND NOT F AND G");

    return compositionCohortDefinition;
  }

  /**
   * 11.4. % de MG na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3 consultas de
   * APSS/PP mensais consecutivas para reforço de adesão (Line 59 in the template) Numerador (Column
   * D in the Template) as following: <code>
   *  B1 and B2 and B5 and NOT E and NOT F AND H </code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumB1nB2nB3nCnotDnotEnotEnotFnH(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror  11.4");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition b4 =
        QualityImprovement2020Queries.getMQ13DenB4_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            50);

    CohortDefinition b5 =
        QualityImprovement2020Queries.getMQ13DenB5_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId(),
            50);

    CohortDefinition c =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition f = getTranferredOutPatients();
    CohortDefinition h = getMQC11NH(true);

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING1));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING1));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));
    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING8));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING8));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING8));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING9));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING8));
    }

    compositionCohortDefinition.setCompositionString("((B1 AND B4 AND H) AND NOT F)");

    return compositionCohortDefinition;
  }

  /**
   * 11.5. % de crianças >2 anos de idade em TARV com registo mensal de seguimento da adesão na
   * ficha de APSS/PP nos primeiros 99 dias de TARV (Line 60 in the template) Numerador (Column D in
   * the Template) as following: <code>
   * A and NOT C and NOT D and NOT E and NOT F  AND G and Age BETWEEN 2 AND 14*</code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFnotGnChildren(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.5");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMOHArtStartDate();
    CohortDefinition c =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition d =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition e =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());
    CohortDefinition f = getTranferredOutPatients();
    CohortDefinition g = getMQC11NG();

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING1));
    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING4));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING6));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING6));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING6));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING5));
      compositionCohortDefinition.addSearch("G", EptsReportUtils.map(g, MAPPING10));
    }

    compositionCohortDefinition.setCompositionString("((A AND G) AND NOT (C OR D OR E OR F))");

    return compositionCohortDefinition;
  }
  /**
   * 11.6. % de crianças <2 anos de idade em TARV com registo mensal de seguimento da adesão na
   * ficha de APSS/PP no primeiro ano de TARV (Line 61 in the template) Numerador (Column D in the
   * Template) as following: <code>
   *  A and NOT C and NOT D and NOT E and NOT F AND I  AND Age  <= 9 MONTHS</code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumAnotCnotDnotEnotFnotIlessThan9Month(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.6");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition a = getMOHArtStartDate();
    CohortDefinition c =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition d =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());
    CohortDefinition e =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());
    CohortDefinition f = getTranferredOutPatients();
    CohortDefinition i = getMQC11NI();
    CohortDefinition babies = genericCohortQueries.getAgeInMonths(0, 9);

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch(
          "I",
          EptsReportUtils.map(
              i, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("A", EptsReportUtils.map(a, MAPPING4));
      compositionCohortDefinition.addSearch("C", EptsReportUtils.map(c, MAPPING6));
      compositionCohortDefinition.addSearch("D", EptsReportUtils.map(d, MAPPING6));
      compositionCohortDefinition.addSearch("E", EptsReportUtils.map(e, MAPPING6));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING5));
      compositionCohortDefinition.addSearch(
          "I",
          EptsReportUtils.map(
              i,
              "onOrAfter=${revisionEndDate-5m+1d},onOrBefore=${revisionEndDate-4m},location=${location}"));
    }

    compositionCohortDefinition.addSearch(
        "BABIES", EptsReportUtils.map(babies, "effectiveDate=${effectiveDate}"));

    compositionCohortDefinition.setCompositionString(
        "A and NOT C and NOT D and NOT E and NOT F AND I AND BABIES");

    return compositionCohortDefinition;
  }

  /**
   * 11.7. % de crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram 3
   * consultas mensais consecutivas de APSS/PP para reforço de adesão(Line 62 in the template)
   * Numerador (Column D in the Template) as following: <code>
   *  B1 and B2 and  NOT C and NOT B5 and NOT E and NOT F  And H and  Age < 15**</code>
   *
   * @param reportSource report Resource (MQ or MI)
   * @return CohortDefinition
   */
  public CohortDefinition getMQC11NumB1nB2notCnotDnotEnotFnHChildren(MIMQ reportSource) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    boolean useE53 = false;

    compositionCohortDefinition.setName("Category 11 : Numeraror 11.7");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition b1 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition b2 = getB2_13(useE53);

    CohortDefinition b4 =
        QualityImprovement2020Queries.getMQ13DenB4_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId());

    CohortDefinition b5 =
        QualityImprovement2020Queries.getMQ13DenB5_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId());

    CohortDefinition f = getTranferredOutPatients();
    CohortDefinition h = getMQC11NH();

    if (reportSource.equals(EptsReportConstants.MIMQ.MQ)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING1));
      compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING1));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING1));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING));
    } else if (reportSource.equals(EptsReportConstants.MIMQ.MI)) {
      compositionCohortDefinition.addSearch("B1", EptsReportUtils.map(b1, MAPPING8));
      compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(b2, MAPPING8));
      compositionCohortDefinition.addSearch("B4", EptsReportUtils.map(b4, MAPPING8));
      compositionCohortDefinition.addSearch("B5", EptsReportUtils.map(b5, MAPPING8));
      compositionCohortDefinition.addSearch("F", EptsReportUtils.map(f, MAPPING9));
      compositionCohortDefinition.addSearch("H", EptsReportUtils.map(h, MAPPING8));
    }

    compositionCohortDefinition.setCompositionString(
        "B1 AND B2 AND NOT B5 AND NOT F AND NOT B4 AND H");

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
   * @param den indicator number
   * @return CohortDefinition <strong>Should</strong> Returns empty if there is no patient who meets
   *     the conditions <strong>Should</strong> fetch patients in category 12 MG of the MQ report
   *     denominator
   */
  public CohortDefinition getMQ12DEN(Integer den) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (den) {
      case 1:
        comp.setName(
            "Adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 2:
        comp.setName(
            "Adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 5:
        comp.setName(
            "Crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 6:
        comp.setName(
            "Crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 9:
        comp.setName(
            "Mulheres grávidas HIV+ que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 10:
        comp.setName(
            "Mulheres grávidas HIV+ que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    comp.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    comp.addSearch("A", EptsReportUtils.map(startedART, MAPPING));

    comp.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    comp.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    comp.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    comp.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    comp.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    if (den == 1 || den == 2) {
      comp.setCompositionString("A AND NOT (C OR D OR E OR F) AND ADULT");
    }
    if (den == 5 || den == 6) {
      comp.setCompositionString("A AND NOT (C OR D OR E OR F)");
    } else if (den == 9 || den == 10) {
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
   *       (Concept Id 21190, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any
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
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "Data Final de Avaliacao", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21190", commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(o.obs_datetime) AS regime_date "
            + "        FROM   patient p "
            + "               JOIN encounter e "
            + "                 ON e.patient_id = p.patient_id "
            + "               JOIN obs o "
            + "                 ON o.encounter_id = e.encounter_id "
            + "               JOIN obs o2 "
            + "                 ON o2.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${53} "
            + "               AND o.concept_id = ${21190} "
            + "               AND o.value_coded IS NOT NULL "
            + "               AND e.location_id = :location "
            + "               AND ( "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})"
            + "                       OR "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded IS NULL) "
            + "                       OR "
            + "                      ( "
            + "                       NOT EXISTS ( "
            + "                               SELECT * FROM obs oo "
            + "                               WHERE oo.voided = 0 "
            + "                               AND oo.encounter_id = e.encounter_id "
            + "                               AND oo.concept_id = 1792 "
            + "                           ) "
            + "                     ) "
            + "                    ) "
            + "               AND e.voided = 0 "
            + "               AND p.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o2.voided = 0"
            + "               AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "        GROUP  BY p.patient_id) bI1 "
            + "WHERE  bI1.patient_id NOT IN (SELECT p.patient_id "
            + "                              FROM   patient p "
            + "                                     JOIN encounter e "
            + "                                       ON e.patient_id = p.patient_id "
            + "                                     JOIN obs o "
            + "                                       ON o.encounter_id = e.encounter_id "
            + "                              WHERE  e.encounter_type = ${6} "
            + "                                     AND o.concept_id = ${21151} AND o.value_coded <> ${21150} "
            + "                                     AND e.location_id = :location "
            + "                                     AND e.voided = 0 "
            + "                                     AND p.voided = 0 "
            + "                                     AND e.encounter_datetime > bI1.regime_date "
            + "                                     AND e.encounter_datetime <= :revisionEndDate)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC13Part3B2</b>: B2NEW P1_2 <br>
   *
   * <ul>
   *   <li>B2NEW P1_2- Select all patients who have the REGIME ARV SEGUNDA LINHA (Concept Id 21187,
   *       value coded different NULL) recorded in Ficha Resumo (encounter type 53) and obs_datetime
   *       >= inclusionStartDate and <= revisionEndDate AND at least for 6 months ( “Last Clinical
   *       Consultation” (last encounter_datetime from B1) minus obs_datetime(from B2) >= 6 months)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsOnRegimeArvSecondLineB2NEWP1_2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Clinical Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21148", hivMetadata.getSecondLineConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p"
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN(SELECT p.patient_id, "
            + "                         Max(e.encounter_datetime) last_visit "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e "
            + "                                 ON e.patient_id = p.patient_id "
            + "                  WHERE  p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                         AND e.encounter_type = ${6} "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_datetime BETWEEN "
            + "                             :startDate AND :revisionEndDate "
            + "                  GROUP  BY p.patient_id) AS last_clinical "
            + "               ON last_clinical.patient_id = p.patient_id "
            + " WHERE  e.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.encounter_type = ${53} "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${21187} "
            + "       AND o.value_coded IS NOT NULL "
            + "       AND o.obs_datetime >= :startDate "
            + "       AND o.obs_datetime <= :revisionEndDate "
            + "       AND TIMESTAMPDIFF(MONTH, o.obs_datetime,  last_clinical.last_visit) >= 6";

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
  public CohortDefinition getPatientsOnRegimeArvSecondLine() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients With Clinical Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "  FROM patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "  WHERE e.voided = 0 AND p.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND o2.voided = 0 "
            + "    AND e.encounter_type = ${53} "
            + "    AND e.location_id = :location "
            + "    AND (o.concept_id = ${21187} AND o.value_coded IS NOT NULL) "
            + "               AND ( "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})"
            + "                       OR "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded IS NULL) "
            + "                       OR "
            + "                      ( "
            + "                       NOT EXISTS ( "
            + "                               SELECT * FROM obs oo "
            + "                               WHERE oo.voided = 0 "
            + "                               AND oo.encounter_id = e.encounter_id "
            + "                               AND oo.concept_id = 1792 "
            + "                           ) "
            + "                     ) "
            + "                    ) "
            + "    AND o.obs_datetime >= :startDate "
            + "    AND o.obs_datetime <= :endDate "
            + "  GROUP BY p.patient_id";

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
   * @param flag indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ12NumeratorP2(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 3:
        cd.setName(
            "Adultos (15/+anos) na 1ª linha que iniciaram o TARV há 12 meses atrás sem registo de saídas");
        break;
      case 4:
        cd.setName("Adultos (15/+anos) que iniciaram 2ª linha TARV há 12 meses atrás");
        break;
      case 7:
        cd.setName("Crianças (0-14 anos) na 1ª linha que iniciaram o TARV há 12 meses atrás");
        break;
      case 8:
        cd.setName("Crianças (0-14 anos) que iniciaram 2ª linha TARV há 12 meses atrás");
        break;
      case 11:
        cd.setName("Mulheres grávidas HIV+ 1ª linha que iniciaram o TARV há 12 meses atrás");
        break;
    }
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    // Start adding the definitions based on the requirements
    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition b1 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsFirstLine_B1();

    CohortDefinition b1E = getPatientsFromFichaClinicaDenominatorB1EOrB2E(true);

    CohortDefinition b2 = getPatientsFromFichaClinicaWithLastTherapeuticLineSetAsSecondLine_B2();

    CohortDefinition b2E = getPatientsFromFichaClinicaDenominatorB1EOrB2E(false);

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    cd.addSearch("A", EptsReportUtils.map(startedART, MAPPING2));

    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            b1,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location},revisionEndDate=${revisionEndDate}"));

    cd.addSearch(
        "B1E", EptsReportUtils.map(b1E, "location=${location},revisionEndDate=${revisionEndDate}"));

    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            b2,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location},revisionEndDate=${revisionEndDate}"));

    cd.addSearch(
        "B2E", EptsReportUtils.map(b2E, "location=${location},revisionEndDate=${revisionEndDate}"));

    cd.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));

    cd.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));

    cd.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    cd.addSearch(
        "G",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getActivePatientsInARTByEndOfCurrentMonth(true),
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate-11m},location=${location}"));
    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(0, 14, true),
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate-11m},location=${location}"));
    if (flag == 3) {
      cd.setCompositionString("(A AND B1 AND NOT (B1E OR C OR D OR F)) AND G AND ADULT");
    } else if (flag == 4) {
      cd.setCompositionString("(A AND B2 AND NOT (B2E OR C OR D OR F)) AND G AND ADULT");
    } else if (flag == 7) {
      cd.setCompositionString("(A AND B1 AND NOT (B1E OR C OR D OR F)) AND G AND CHILDREN");
    } else if (flag == 8) {
      cd.setCompositionString("(A AND B2) AND NOT (B2E OR C OR D OR F) AND G AND CHILDREN");
    } else if (flag == 11) {
      cd.setCompositionString("(A AND B1 AND C) AND NOT (B1E OR D OR F) AND G");
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
   * @param den indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ12NUM(Integer den) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (den) {
      case 1:
        comp.setName(
            "Adultos (15/+anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs entre 25 a 33 dias após o início do TARV");
        break;
      case 2:
        comp.setName(
            "Adultos (15/+anos) que iniciaram o TARV no período de inclusão e que tiveram 3 consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 5:
        comp.setName(
            "Crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 6:
        comp.setName(
            "Crianças (0-14 anos) que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
      case 9:
        comp.setName(
            "Mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que retornaram para uma consulta clínica ou levantamento de ARVs dentro de 33 dias após o início do TARV");
        break;
      case 10:
        comp.setName(
            "Mulheres grávidas HIV+  que iniciaram o TARV no período de inclusão e que tiveram consultas clínicas ou levantamentos de ARVs dentro de 99 dias após o início do TARV");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    comp.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition returnedForAnyConsultationOrPickup =
        QualityImprovement2020Queries.getMQ12NumH(
            20,
            33,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId());

    CohortDefinition returnedForAnotherConsultationOrPickup =
        QualityImprovement2020Queries.getMQ12NumI(
            20,
            33,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId());

    CohortDefinition returnedForAnotherConsultationOrPickup3466 =
        QualityImprovement2020Queries.getMQ12NumI(
            34,
            66,
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getHistoricalDrugStartDateConcept().getConceptId(),
            hivMetadata.getArtPickupConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId());

    CohortDefinition returnedForAnotherConsultationOrPickup6799 =
        QualityImprovement2020Queries.getMQ12NumI(
            67,
            99,
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
            transferredIn,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    comp.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    comp.addSearch(
        "H",
        EptsReportUtils.map(
            returnedForAnyConsultationOrPickup,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "I",
        EptsReportUtils.map(
            returnedForAnotherConsultationOrPickup,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "II",
        EptsReportUtils.map(
            returnedForAnotherConsultationOrPickup3466,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "III",
        EptsReportUtils.map(
            returnedForAnotherConsultationOrPickup6799,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    comp.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    if (den == 1) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND H AND ADULT");
    } else if (den == 2) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND I AND II AND III AND ADULT");
    } else if (den == 5) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND H");
    } else if (den == 6) {
      comp.setCompositionString("(A AND NOT (C OR D OR E OR F)) AND I AND II AND III");
    } else if (den == 9) {
      comp.setCompositionString("((A AND C) AND NOT (D OR E OR F)) AND H ");
    } else if (den == 10) {
      comp.setCompositionString("((A AND C) AND NOT (D OR E OR F)) AND I AND II AND III ");
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
   * occurred during the period (encounter_datetime >= endDateInclusion and <= endDateRevision) and
   * the concept: “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value coded “HIV
   * CARGA VIRAL” (Concept Id 856) In this last consultation.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMQ13G() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Last Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());

    String query =
        " SELECT p.patient_id  "
            + " FROM  "
            + "     patient p  "
            + "         INNER JOIN  "
            + "     encounter e ON e.patient_id = p.patient_id  "
            + "         INNER JOIN  "
            + "     obs o ON o.encounter_id = e.encounter_id  "
            + "         INNER JOIN  "
            + "     (SELECT   "
            + "         p.patient_id, MAX(e.encounter_datetime) last_visit  "
            + "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  "
            + "             AND e.encounter_type = ${6}  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "     GROUP BY p.patient_id) b1 ON b1.patient_id = e.patient_id"
            + "     WHERE p.voided = 0 AND e.voided = 0  AND o.voided = 0"
            + "    AND e.encounter_type = ${6}  "
            + "    AND e.location_id = :location  "
            + "    AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "    AND e.encounter_datetime = b1.last_visit"
            + "        AND o.concept_id = ${23722}"
            + "        AND o.value_coded = ${856}";

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
   * @param den boolean parameter, true indicates denominator ,false indicates numerator
   * @param line indicator number
   * @return CohortDefinition <strong>Should</strong> Returns empty if there is no patient who meets
   *     the conditions <strong>Should</strong> fetch patients in category 13 MG of the MQ report
   */
  public CohortDefinition getMQ13(Boolean den, Integer line) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    if (den) {
      compositionCohortDefinition.setName("B AND NOT C AND NOT D");
    } else {
      compositionCohortDefinition.setName("(B AND G) AND NOT (C OR D)");
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition lastClinical = commonCohortQueries.getMOHPatientsLastClinicalConsultation();

    CohortDefinition pregnant =
        commonCohortQueries.getNewMQPregnantORBreastfeeding(
            hivMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition brestfeeding =
        commonCohortQueries.getNewMQPregnantORBreastfeeding(
            hivMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition b2New =
        commonCohortQueries.getPatientsWithFirstTherapeuticLineOnLastClinicalEncounterB2NEW();

    CohortDefinition b2e = getMQC13DEN_B2E();

    CohortDefinition secondLine6Months = getPatientsOnRegimeArvSecondLineB2NEWP1_2();

    CohortDefinition changeRegimen6Months = getMOHPatientsOnTreatmentFor6Months();

    CohortDefinition B3E =
        commonCohortQueries.getMOHPatientsToExcludeFromTreatmentIn6Months(
            true,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getRegimenAlternativeToFirstLineConcept(),
            Arrays.asList(
                commonMetadata.getAlternativeFirstLineConcept(),
                commonMetadata.getRegimeChangeConcept(),
                hivMetadata.getNoConcept()),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()));

    CohortDefinition abandonedExclusionInTheLastSixMonthsFromFirstLineDate =
        getPatientsWhoAbandonedInTheLastSixMonthsFromFirstLineDate();

    CohortDefinition abandonedExclusionByTarvRestartDate =
        getPatientsWhoAbandonedTarvOnArtRestartDate();

    CohortDefinition abandonedExclusionFirstLine = getPatientsWhoAbandonedTarvOnOnFirstLineDate();

    CohortDefinition abandonedExclusionSecondLine = getPatientsWhoAbandonedTarvOnOnSecondLineDate();

    CohortDefinition restartdedExclusion = getPatientsWhoRestartedTarvAtLeastSixMonths();

    CohortDefinition B5E =
        commonCohortQueries.getMOHPatientsWithVLRequestorResultBetweenClinicalConsultations(
            false, true, 12);

    CohortDefinition G = getMQ13G();

    if (line == 1) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(15, null),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    } else if (line == 4) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(15, null),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    } else if (line == 6) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(0, 4),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    } else if (line == 7) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(5, 9),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    } else if (line == 8) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(10, 14),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    } else if (line == 13) {
      compositionCohortDefinition.addSearch(
          "age",
          EptsReportUtils.map(
              commonCohortQueries.getMOHPatientsAgeOnLastClinicalConsultationDate(2, 14),
              "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    }

    compositionCohortDefinition.addSearch(
        "B1",
        EptsReportUtils.map(
            lastClinical,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B2NEW",
        EptsReportUtils.map(
            b2New,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "D",
        EptsReportUtils.map(
            brestfeeding,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "secondLineB2",
        EptsReportUtils.map(
            secondLine6Months,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B3",
        EptsReportUtils.map(
            changeRegimen6Months,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B3E",
        EptsReportUtils.map(
            B3E, "startDate=${endDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B5E",
        EptsReportUtils.map(
            B5E, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "G",
        EptsReportUtils.map(
            G, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B2E",
        EptsReportUtils.map(
            b2e,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "RESTARTED", EptsReportUtils.map(restartdedExclusion, MAPPING));

    compositionCohortDefinition.addSearch(
        "RESTARTEDTARV", EptsReportUtils.map(abandonedExclusionByTarvRestartDate, MAPPING));

    compositionCohortDefinition.addSearch(
        "ABANDONEDTARV",
        EptsReportUtils.map(abandonedExclusionInTheLastSixMonthsFromFirstLineDate, MAPPING1));

    compositionCohortDefinition.addSearch(
        "ABANDONED1LINE", EptsReportUtils.map(abandonedExclusionFirstLine, MAPPING1));

    compositionCohortDefinition.addSearch(
        "ABANDONED2LINE", EptsReportUtils.map(abandonedExclusionSecondLine, MAPPING1));

    if (den) {
      if (line == 1) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR  ( (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV) ) )) AND NOT B5E) AND NOT (C OR D) AND age");
      } else if (line == 6 || line == 7 || line == 8) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR  ( (RESTARTED AND NOT RESTARTEDTARV) OR (B3 AND NOT B3E AND NOT ABANDONED1LINE) )) AND NOT B5E) AND NOT (C OR D) AND age");
      } else if (line == 4 || line == 13) {
        compositionCohortDefinition.setCompositionString(
            "((B1 AND (secondLineB2 AND NOT B2E AND NOT ABANDONED2LINE)) AND NOT B5E) AND NOT (C OR D) AND age");
      }
    } else {
      if (line == 1) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR  ( (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV) ) )) AND NOT B5E) AND NOT (C OR D) AND G AND age");
      } else if (line == 6 || line == 7 || line == 8) {
        compositionCohortDefinition.setCompositionString(
            "(B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR  ( (RESTARTED AND NOT RESTARTEDTARV) OR (B3 AND NOT B3E AND NOT ABANDONED1LINE) )) AND NOT B5E) AND NOT (C OR D) AND G AND age");
      } else if (line == 4 || line == 13) {
        compositionCohortDefinition.setCompositionString(
            "((B1 AND (secondLineB2 AND NOT B2E AND NOT ABANDONED2LINE)) AND NOT B5E) AND NOT (C OR D) AND G AND age");
      }
    }

    return compositionCohortDefinition;
  }

  /**
   * <b>MQC13P3NUM</b>: Melhoria de Qualidade Categoria 13 Numerador - Part 3 <br>
   *
   * <p>Select patients from the corresponding denominator and apply the following categories
   * <i>13.2 - ( (A and NOT E and G) OR (B1 and H)) and NOT (C or D or F) and Age >= 15*</i><br>
   * <i>13.9 - ( (A and NOT E and G) OR (B1 and H))and NOT (C or D or F) and Age >= 0 and <=4
   * years*</i><br>
   * <i>13.10 - ( (A and NOT E and G) OR (B1 and H)) and NOT (C or D or F) and Age >= 5 and <=9
   * years*</i><br>
   * <i>13.11 - ( (A and NOT E and G) OR (B1 and H)) and NOT (C or D or F) and Age >= 10 and Age <=
   * 14 years*</i><br>
   * <i>13.5 - (B2 and I) and NOT (C or D or F) and Age>=15years* </i><br>
   * <i>13.14 - (B2 and I) and NOT (C or D or F) and Age > 2 and Age <15years * </i><br>
   *
   * <p>All age disaggreagtions should be based on the ART start date
   *
   * @param indicator indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P3NUM(int indicator) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    if (indicator == 2)
      cd.setName(
          "Adultos (15/+anos) na 1a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após início do TARV");
    if (indicator == 9)
      cd.setName(
          "Crianças  (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV");
    if (indicator == 10)
      cd.setName(
          "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV");
    if (indicator == 11)
      cd.setName(
          "Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV");
    if (indicator == 5)
      cd.setName(
          "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV");
    if (indicator == 14)
      cd.setName(
          "Crianças na 2a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início da 2a linha de TARV");

    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Data final de Revisao", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    String mapping =
        "startDate=${startDate},endDate=${endDate},less3mDate=${startDate-3m},location=${location}";

    if (indicator == 2) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Adultos (15/+anos) na 2a linha de TARV que receberam o resultado da CV entre o sexto e o nono mês após o início da 2a linha de TARV",
                  15,
                  null),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 5) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              getAgeOnObsDatetime(15, null),
              "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    } else if (indicator == 9) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças (0-4 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
                  0,
                  4),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 10) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças  (5-9 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
                  5,
                  9),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 11) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              ageCohortQueries.createXtoYAgeCohort(
                  "Crianças  (10-14 anos de idade) na 1a linha de TARV que receberam o resultado da Carga Viral entre o sexto e o nono mês após o início do TARV",
                  10,
                  14),
              "effectiveDate=${revisionEndDate}"));
    } else if (indicator == 14) {
      cd.addSearch(
          "age",
          EptsReportUtils.map(
              getAgeOnObsDatetime(2, 14),
              "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    }

    // Start adding the definitions based on the requirements
    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));

    cd.addSearch("B1", EptsReportUtils.map(getPatientsOnRegimeChangeBI1AndNotB1E_B1(), MAPPING1));

    cd.addSearch("B2New", EptsReportUtils.map(getPatientsOnRegimeArvSecondLine(), MAPPING));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            MAPPING));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            MAPPING));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            MAPPING));

    cd.addSearch(
        "ABANDONEDTARV",
        EptsReportUtils.map(
            getPatientsWhoAbandonedInTheLastSixMonthsFromFirstLineDate(), MAPPING1));

    cd.addSearch(
        "ABANDONED1LINE",
        EptsReportUtils.map(getPatientsWhoAbandonedTarvOnOnFirstLineDate(), MAPPING1));

    cd.addSearch(
        "ABANDONED2LINE",
        EptsReportUtils.map(getPatientsWhoAbandonedTarvOnOnSecondLineDate(), MAPPING1));

    cd.addSearch(
        "F",
        EptsReportUtils.map(
            commonCohortQueries.getTranferredOutPatients(),
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch("G", EptsReportUtils.map(getMQC13P3NUM_G(), MAPPING));
    cd.addSearch("H", EptsReportUtils.map(getMQC13P3NUM_H(), MAPPING));
    cd.addSearch("I", EptsReportUtils.map(getMQC13P3NUM_I(), mapping));
    cd.addSearch("J", EptsReportUtils.map(getMQC13P3NUM_J(), MAPPING));
    cd.addSearch("K", EptsReportUtils.map(getMQC13P3NUM_K(), MAPPING1));
    cd.addSearch("L", EptsReportUtils.map(getMQC13P3NUM_L(), MAPPING));
    cd.addSearch(
        "DD", EptsReportUtils.map(txMlCohortQueries.getDeadPatientsComposition(), MAPPING3));

    if (indicator == 2 || indicator == 9 || indicator == 10 || indicator == 11)
      cd.setCompositionString(
          "((A AND NOT C AND NOT D AND NOT ABANDONEDTARV AND (G OR J) AND NOT DD) OR ((B1 AND NOT ABANDONED1LINE) AND (H OR K))) AND NOT (F OR E OR DD) AND age");

    if (indicator == 5)
      cd.setCompositionString(
          "((B2New AND NOT ABANDONED2LINE) AND (I OR L)) AND NOT (F OR E OR DD) AND age");

    if (indicator == 14)
      cd.setCompositionString(
          "((B2New AND NOT ABANDONED2LINE) AND (I OR L)) AND NOT (F OR E OR DD) AND age");

    return cd;
  }

  /**
   * Select all patients with a clinical consultation (encounter type 6) with concept “Carga Viral”
   * (Concept id 856) with value_numeric not null OR concept “Carga Viral Qualitative” (Concept id
   * 1305) with value_coded not null and Encounter_datetime between “Patient ART Start Date” (the
   * oldest date from A)+6months and “Patient ART Start Date” (the oldest date from query
   * A)+9months.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P3NUM_G() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setName("Category 13 Part 3- Numerator - G");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT art_tbl.patient_id "
            + "FROM   (SELECT patient_id, "
            + "               Min(data_inicio) data_inicio "
            + "        FROM   (SELECT p.patient_id, "
            + "                       Min(value_datetime) data_inicio "
            + "                FROM   patient p "
            + "                       inner join encounter e "
            + "                               ON p.patient_id = e.patient_id "
            + "                       inner join obs o "
            + "                               ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND o.concept_id = ${1190} "
            + "                       AND o.value_datetime IS NOT NULL "
            + "                       AND o.value_datetime <= :endDate "
            + "                       AND e.location_id = :location "
            + "                GROUP  BY p.patient_id ) inicio "
            + "        WHERE  data_inicio BETWEEN :startDate AND :endDate "
            + "        GROUP  BY patient_id) art_tbl "
            + "       join (SELECT p.patient_id, "
            + "                    e.encounter_datetime "
            + "             FROM   patient p "
            + "                    join encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "                    join obs o "
            + "                      ON o.encounter_id = e.encounter_id "
            + "             WHERE  e.encounter_type = ${6} "
            + "                    AND e.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND ( ( o.concept_id = ${856} "
            + "                            AND o.value_numeric IS NOT NULL ) "
            + "                           OR ( o.concept_id = ${1305} "
            + "                                AND o.value_coded IS NOT NULL ) ))G_tbl "
            + "         ON G_tbl.patient_id = art_tbl.patient_id "
            + "       WHERE  G_tbl.encounter_datetime BETWEEN Date_add(art_tbl.data_inicio, "
            + "                                        interval 6 month) "
            + "                                        AND "
            + "       Date_add(art_tbl.data_inicio, interval 9 month)  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * H - Select all patients with a clinical consultation (encounter type 6) with concept “Carga
   * Viral” (Concept id 856) with value_numeric not null OR concept “Carga Viral Qualitative”
   * (Concept id 1305) with value_coded not null and Encounter_datetime between “ALTERNATIVA A LINHA
   * - 1a LINHA Date” (the most recent date from B1)+6months and “ALTERNATIVA A LINHA - 1a LINHA
   * Date” (the most recent date from B1)+9months.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P3NUM_H() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setName("Category 13 Part 3- Numerator - H");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("21190", commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        " SELECT B1.patient_id "
            + "FROM   (SELECT patient_id, "
            + "               regime_date "
            + "        FROM   (SELECT p.patient_id, "
            + "                       Max(o.obs_datetime) AS regime_date "
            + "                FROM   patient p "
            + "                       join encounter e "
            + "                         ON e.patient_id = p.patient_id "
            + "                       join obs o "
            + "                         ON o.encounter_id = e.encounter_id "
            + "                WHERE  e.encounter_type = ${53} "
            + "                       AND o.concept_id = ${21190} "
            + "                       AND o.value_coded IS NOT NULL "
            + "                       AND e.location_id = :location "
            + "                       AND e.voided = 0 "
            + "                       AND p.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "                GROUP  BY p.patient_id) bI1 "
            + "        WHERE  bI1.patient_id NOT IN (SELECT p.patient_id "
            + "                                      FROM   patient p "
            + "                                             join encounter e "
            + "                                               ON e.patient_id = p.patient_id "
            + "                                             join obs o "
            + "                                               ON o.encounter_id = "
            + "                                                  e.encounter_id "
            + "                                      WHERE  e.encounter_type = ${6} "
            + "                                             AND o.concept_id = ${21151} "
            + "                                             AND o.value_coded <> ${21150} "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.voided = 0 "
            + "                                             AND p.voided = 0 "
            + "                                             AND e.encounter_datetime > "
            + "                                                 bI1.regime_date "
            + "                                             AND e.encounter_datetime <= "
            + "                                                 :endDate))B1 "
            + "       join (SELECT p.patient_id, "
            + "                    e.encounter_datetime "
            + "             FROM   patient p "
            + "                    join encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "                    join obs o "
            + "                      ON o.encounter_id = e.encounter_id "
            + "             WHERE  e.encounter_type = ${6} "
            + "                    AND e.voided = 0 "
            + "                    AND e.location_id = :location "
            + "                    AND ( ( o.concept_id = ${856} "
            + "                            AND o.value_numeric IS NOT NULL ) "
            + "                           OR ( o.concept_id = ${1305} "
            + "                                AND o.value_coded IS NOT NULL ) )) H_tbl "
            + "         ON H_tbl.patient_id = B1.patient_id "
            + "WHERE  H_tbl.encounter_datetime BETWEEN Date_add(B1.regime_date, "
            + "                                        interval 6 month) AND "
            + "                                               Date_add(B1.regime_date, "
            + "                                               interval 9 month);  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * I - Select all patients with a clinical consultation (encounter type 6) with concept “Carga
   * Viral” (Concept id 856) with value_numeric not null OR concept “Carga Viral Qualitative”
   * (Concept id 1305) with value_coded not null and Encounter_datetime between “Segunda Linha Date”
   * (the most recent date from B2New)+6months and “Segunda Linha Date” (the most recent date from
   * B2New)+9months.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P3NUM_I() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("less3mDate", "Less3months date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setName("Category 13 Part 3- Numerator - I");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        " SELECT B2.patient_id "
            + "         FROM   ( SELECT p.patient_id, MAX(o.obs_datetime) as regime_date "
            + "   FROM "
            + "   patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "   WHERE e.voided = 0 AND p.voided = 0 "
            + "   AND o.voided = 0 "
            + "   AND o2.voided = 0 "
            + "   AND e.encounter_type = ${53} "
            + "   AND e.location_id = :location  "
            + "   AND (o.concept_id = ${21187} AND o.value_coded IS NOT NULL) "
            + "               AND ( "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})"
            + "                       OR "
            + "                      (o2.concept_id = ${1792} AND o2.value_coded IS NULL) "
            + "                       OR "
            + "                      ( "
            + "                       NOT EXISTS ( "
            + "                               SELECT * FROM obs oo "
            + "                               WHERE oo.voided = 0 "
            + "                               AND oo.encounter_id = e.encounter_id "
            + "                               AND oo.concept_id = 1792 "
            + "                           ) "
            + "                     ) "
            + "                    ) "
            + "   AND o.obs_datetime >= :startDate "
            + "   AND o.obs_datetime <= :endDate "
            + "  GROUP BY p.patient_id "
            + "         ) B2  "
            + "                join ( SELECT p.patient_id, e.encounter_datetime  "
            + "                      FROM   patient p  "
            + "                             join encounter e ON e.patient_id = p.patient_id  "
            + "                             join obs o ON o.encounter_id = e.encounter_id  "
            + "                      WHERE  e.encounter_type = ${6}  "
            + "                             AND e.voided = 0  "
            + "                             AND e.location_id = :location  "
            + "                             AND ( ( o.concept_id = ${856}  "
            + "             AND o.value_numeric IS NOT NULL )  "
            + "             OR ( o.concept_id = ${1305}  "
            + "             AND o.value_coded IS NOT NULL ) )) I_tbl  "
            + "                  ON I_tbl.patient_id = B2.patient_id  "
            + "         WHERE  I_tbl.encounter_datetime BETWEEN Date_add(B2.regime_date, interval 6 month) AND Date_add(B2.regime_date, interval 9 month)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ13</b>: Melhoria de Qualidade Category 13 Part 4, H <br>
   *
   * <ul>
   *   <li>Select all patients with clinical consultation (encounter type 6) with concept “PEDIDO DE
   *       INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value coded “HIV CARGA VIRAL”
   *       (Concept Id 856) on Encounter_datetime between “Viral Load Date” (the oldest date from
   *       B2)+80 days and “Viral Load Date” (the oldest date from B2)+130 days.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQ13P4H(int vlQuantity) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    sqlCohortDefinition.setName("Category 13 - Part 4 Denominator - H");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());

    String query =
        " SELECT DISTINCT  "
            + "        p.patient_id  "
            + "    FROM  "
            + "        patient p  "
            + "            INNER JOIN  "
            + "        encounter e ON e.patient_id = p.patient_id  "
            + "            INNER JOIN  "
            + "        obs o ON o.encounter_id = e.encounter_id  "
            + "            INNER JOIN  "
            + "        (SELECT   "
            + "            p.patient_id, MIN(e.encounter_datetime) value_datetime  "
            + "        FROM  "
            + "            patient p  "
            + "        INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "        INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "        WHERE  "
            + "            p.voided = 0 AND e.voided = 0  "
            + "                AND o.voided = 0  "
            + "                AND e.location_id = :location  "
            + "                AND e.encounter_type = ${6}  "
            + "                AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "                AND o.concept_id = ${856}  "
            + "                AND o.value_numeric >=  "
            + vlQuantity
            + "        GROUP BY p.patient_id) vl ON vl.patient_id = p.patient_id  "
            + "    WHERE  "
            + "        p.voided = 0 AND e.voided = 0  "
            + "            AND o.voided = 0  "
            + "            AND e.encounter_type = ${6}  "
            + "            AND e.location_id = :location  "
            + "            AND o.concept_id = ${23722}  "
            + "            AND o.value_coded = ${856}  "
            + "            AND DATE(e.encounter_datetime) BETWEEN DATE_ADD(vl.value_datetime,  "
            + "            INTERVAL 80 DAY) AND DATE_ADD(vl.value_datetime,  "
            + "            INTERVAL 130 DAY);";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ13</b>: Melhoria de Qualidade Category 13 <br>
   * <i> DENOMINATOR 3: (B1 and B2) and NOT (C or D or F) and Age >= 15* </i> <br>
   * <i> DENOMINATOR 12: (B1 and B2) and NOT (C or D or F) and Age > 2 and Age < 15* </i> <br>
   * <i> DENOMINATOR 18: (B1 and B2 and C) and NOT (D or F) and Age > 2 and Age < 15* </i> <br>
   * <i> NUMERATOR 3: (B1 and B2 AND G AND H) and NOT (C or D or F) and Age >= 15* </i> <br>
   * <i> NUMERATOR 12: (B1 and B2 AND G AND H) and NOT (C or D or F) and Age > 2 and Age < 15* </i>
   * <br>
   *
   * <ul>
   *   <li>B1- Select all patients who have the LAST “LINHA TERAPEUTICA” (Concept Id 21151) recorded
   *       in Ficha Clinica (encounter type 6, encounter_datetime) with value coded “PRIMEIRA LINHA”
   *       (concept id 21150) during the inclusion period (startDateInclusion and endDateInclusion).
   *   <li>
   *   <li>B2 - Select all patients from Ficha Clinica (encounter type 6) with FIRST concept “Carga
   *       Viral” (Concept id 856) with value_numeric > 1000 and Encounter_datetime during the
   *       Inclusion period (startDateInclusion and endDateInclusion). Note: if there is more than
   *       one record with value_numeric > 1000 than consider the first occurrence during the
   *       inclusion period.
   *   <li>
   *   <li>
   *   <li>C - All female patients registered as “Pregnant” on a clinical consultation during the
   *       inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>D - All female patients registered as “Breastfeeding” on a clinical consultation during
   *       the inclusion period (startDateInclusion and endDateInclusion)
   *   <li>
   *   <li>F - All Transferred Out patients
   * </ul>
   *
   * @param den boolean parameter, true indicates denominator ,false indicates numerator
   * @param line indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ13P4(Boolean den, Integer line) {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    boolean useE53 = true;

    if (den) {
      if (line == 3) {
        compositionCohortDefinition.setName(
            "# de Adultos (15/+anos) na 1ª linha de TARV com registo resultado de CV acima de 1000");
      } else if (line == 12) {
        compositionCohortDefinition.setName(
            "# de crianças (>2 anos de idade) na 1ª linha de TARV com registo de resultado de CV ≥1000");
      } else if (line == 18) {
        compositionCohortDefinition.setName(
            "# de MG na 1ª linha de TARV com registo de resultado de CV >50");
      }
    } else {
      if (line == 3) {
        compositionCohortDefinition.setName(
            "# de Adultos (15/+anos) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV ≥1000 cps/ml");
      } else if (line == 12) {
        compositionCohortDefinition.setName(
            "# de crianças (>2 anos de idade) na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV ≥1000");
      } else if (line == 18) {
        compositionCohortDefinition.setName(
            "# de MG na 1ª linha de TARV com registo de pedido de CV entre o 3º e o 4º mês após terem recebido o último resultado de CV >50");
      }
    }
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition children = this.ageCohortQueries.createXtoYAgeCohort("children", 2, 14);

    CohortDefinition adult = this.ageCohortQueries.createXtoYAgeCohort("adult", 15, 200);

    CohortDefinition patientsFromFichaClinicaLinhaTerapeutica =
        getPatientsOnArtFirstLineForMoreThanSixMonthsFromArtStartDate();

    CohortDefinition patientsFromFichaClinicaCargaViral = getB2_13(useE53);

    CohortDefinition pregnantWithCargaViralHigherThan1000 =
        QualityImprovement2020Queries.getMQ13DenB4_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId());

    CohortDefinition breastfeedingWithCargaViralHigherThan1000 =
        QualityImprovement2020Queries.getMQ13DenB5_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId());

    CohortDefinition pregnantWithCargaViralHigherThan50 =
        QualityImprovement2020Queries.getMQ13DenB4_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId(),
            50);

    CohortDefinition breastfeedingWithCargaViralHigherThan50 =
        QualityImprovement2020Queries.getMQ13DenB5_P4(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            commonMetadata.getBreastfeeding().getConceptId(),
            50);

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition transferOut = getTranferredOutPatients();

    CohortDefinition H = getMQ13P4H(1000);

    CohortDefinition H50 = getMQ13P4H(50);

    CohortDefinition patientsWithCargaViralonFichaClinicaAndFichaResumo =
        getPatientsWithCargaViralonFichaClinicaAndFichaResumo(1000);

    CohortDefinition patientsWithCargaViralonFichaClinicaAndFichaResumoDen18 =
        getPatientsWithCargaViralonFichaClinicaAndFichaResumo(50);

    CohortDefinition b2New =
        commonCohortQueries.getPatientsWithFirstTherapeuticLineOnLastClinicalEncounterB2NEW();

    CohortDefinition abandonedExclusionInTheLastSixMonthsFromFirstLineDate =
        getPatientsWhoAbandonedInTheLastSixMonthsFromFirstLineDate();

    CohortDefinition restartdedExclusion = getPatientsWhoRestartedTarvAtLeastSixMonths();

    CohortDefinition abandonedExclusionByTarvRestartDate =
        getPatientsWhoAbandonedTarvOnArtRestartDate();

    CohortDefinition changeRegimen6Months = getMOHPatientsOnTreatmentFor6Months();

    CohortDefinition B3E =
        commonCohortQueries.getMOHPatientsToExcludeFromTreatmentIn6Months(
            true,
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getMasterCardEncounterType(),
            commonMetadata.getRegimenAlternativeToFirstLineConcept(),
            Arrays.asList(
                commonMetadata.getAlternativeFirstLineConcept(),
                commonMetadata.getRegimeChangeConcept(),
                hivMetadata.getNoConcept()),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getTherapeuticLineConcept(),
            Collections.singletonList(hivMetadata.getFirstLineConcept()));

    CohortDefinition abandonedExclusionFirstLine = getPatientsWhoAbandonedTarvOnOnFirstLineDate();

    CohortDefinition B5E =
        commonCohortQueries.getMOHPatientsWithVLRequestorResultBetweenClinicalConsultations(
            false, true, 12);

    compositionCohortDefinition.addSearch(
        "children", EptsReportUtils.map(children, "effectiveDate=${revisionEndDate}"));
    compositionCohortDefinition.addSearch(
        "adult", EptsReportUtils.map(adult, "effectiveDate=${revisionEndDate}"));

    compositionCohortDefinition.addSearch(
        "B1", EptsReportUtils.map(patientsWithCargaViralonFichaClinicaAndFichaResumo, MAPPING));

    compositionCohortDefinition.addSearch(
        "B1Den18",
        EptsReportUtils.map(patientsWithCargaViralonFichaClinicaAndFichaResumoDen18, MAPPING));

    compositionCohortDefinition.addSearch(
        "B2", EptsReportUtils.map(patientsFromFichaClinicaCargaViral, MAPPING1));

    compositionCohortDefinition.addSearch(
        "B4", EptsReportUtils.map(pregnantWithCargaViralHigherThan1000, MAPPING1));

    compositionCohortDefinition.addSearch(
        "B5", EptsReportUtils.map(breastfeedingWithCargaViralHigherThan1000, MAPPING1));

    compositionCohortDefinition.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));

    compositionCohortDefinition.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    compositionCohortDefinition.addSearch(
        "DD", EptsReportUtils.map(txMlCohortQueries.getDeadPatientsComposition(), MAPPING3));

    compositionCohortDefinition.addSearch("H", EptsReportUtils.map(H, MAPPING));

    compositionCohortDefinition.addSearch("H50", EptsReportUtils.map(H50, MAPPING));

    compositionCohortDefinition.addSearch(
        "B2NEW",
        EptsReportUtils.map(
            b2New,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "ABANDONEDTARV",
        EptsReportUtils.map(abandonedExclusionInTheLastSixMonthsFromFirstLineDate, MAPPING1));

    compositionCohortDefinition.addSearch(
        "RESTARTED", EptsReportUtils.map(restartdedExclusion, MAPPING));

    compositionCohortDefinition.addSearch(
        "RESTARTEDTARV", EptsReportUtils.map(abandonedExclusionByTarvRestartDate, MAPPING));

    compositionCohortDefinition.addSearch(
        "B3",
        EptsReportUtils.map(
            changeRegimen6Months,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B3E",
        EptsReportUtils.map(
            B3E, "startDate=${endDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "ABANDONED1LINE", EptsReportUtils.map(abandonedExclusionFirstLine, MAPPING1));

    compositionCohortDefinition.addSearch(
        "B5E",
        EptsReportUtils.map(
            B5E, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "B4CV50", EptsReportUtils.map(pregnantWithCargaViralHigherThan50, MAPPING1));

    compositionCohortDefinition.addSearch(
        "B5CV50", EptsReportUtils.map(breastfeedingWithCargaViralHigherThan50, MAPPING1));

    if (den) {
      if (line == 3) {
        compositionCohortDefinition.setCompositionString(
            "((B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) AND NOT (B4 or B5 or E or F or DD)) AND adult");
      } else if (line == 12) {
        compositionCohortDefinition.setCompositionString(
            "((B1 AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) AND NOT (B4 or B5 or E or F)) AND children");
      } else if (line == 18) {
        compositionCohortDefinition.setCompositionString(
            "(((B1Den18 AND B4CV50) AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) ) AND NOT (B5CV50 or E or F)");
      }
    } else {
      if (line == 3) {
        compositionCohortDefinition.setCompositionString(
            "(((B1 AND H) AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) AND NOT (B4 or B5 or E or F or DD)) AND adult");
      } else if (line == 12) {
        compositionCohortDefinition.setCompositionString(
            "(((B1 AND H) AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) AND NOT (B4 or B5 or E or F)) AND children");
      } else if (line == 18) {
        compositionCohortDefinition.setCompositionString(
            "(((B1Den18 AND B4CV50 AND H50) AND ( (B2NEW AND NOT ABANDONEDTARV) OR (RESTARTED AND NOT (RESTARTEDTARV OR ABANDONEDTARV)) OR (B3 AND NOT B3E AND NOT (ABANDONED1LINE OR ABANDONEDTARV)) ) AND NOT B5E ) ) AND NOT (B5CV50 or E or F)");
      }
    }
    return compositionCohortDefinition;
  }

  /**
   * B2 - Select all female patients with first clinical consultation (encounter type 6) that have
   * the concept “GESTANTE” (Concept Id 1982) and value coded “SIM” (Concept Id 1065) registered
   * (1)during the inclusion period (first occurrence, encounter_datetime >= startDateInclusion and
   * <=endDateInclusion) and (2) 3 months after the start of ART (encounter_datetime > “Patient ART
   * Start Date” + 3 months)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2DenB2() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" B2 - categoria 13 - Denominador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());

    String query =
        "       SELECT patient_id "
            + " FROM ("
            + "         SELECT p.patient_id, MIN(e.encounter_datetime) AS first_gestante "
            + "         FROM  patient p "
            + "               INNER JOIN person per on p.patient_id=per.person_id "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided = 0 "
            + "           AND per.voided=0 AND per.gender = 'F' "
            + "           AND e.voided = 0 AND o.voided  = 0 "
            + "           AND e.encounter_type = ${6} "
            + "           AND o.concept_id = ${1982} "
            + "           AND o.value_coded = ${1065} "
            + "           AND e.location_id = :location "
            + "         GROUP BY p.patient_id) gest  "
            + " WHERE gest.first_gestante >= :startDate "
            + "   AND gest.first_gestante <= :endDate "
            + "   AND gest.first_gestante > DATE_ADD((SELECT MIN(o.value_datetime) as art_date "
            + "                                       FROM encounter e "
            + "                                           INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                       WHERE gest.patient_id = e.patient_id "
            + "                                         AND e.voided = 0 AND o.voided = 0 "
            + "                                         AND e.encounter_type = ${53} AND o.concept_id = ${1190} "
            + "                                         AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "                                       LIMIT 1), interval 3 MONTH) ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   * (B3=H from Numerator) - Select all patients with clinical consultation (encounter type 6) with
   * concept “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id 23722) and value coded “HIV CARGA
   * VIRAL” (Concept Id 856) on Encounter_datetime between “Patient ART Start Date” (the oldest from
   * query A)+80days and “Patient ART Start Date” (the oldest from query A)+130days. Note: if more
   * than one encounter exists that satisfies these conditions, select the oldest one.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2DenB3() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" B3 - categoria 13 - Denominador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    StringSubstitutor sb = new StringSubstitutor(map);

    String query =
        " SELECT p.patient_id "
            + " FROM patient p "
            + " INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN ( "
            + "             SELECT patient_id, art_date, encounter_id "
            + "             FROM	( "
            + "                     SELECT p.patient_id, Min(value_datetime) as art_date, e.encounter_id "
            + "                     FROM patient p "
            + "                     INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     WHERE  p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0 "
            + "                           AND e.encounter_type = ${53}   "
            + "                           AND o.concept_id = ${1190}   "
            + "                           AND o.value_datetime IS NOT NULL "
            + "                           AND o.value_datetime <= :endDate   "
            + "                           AND e.location_id = :location  "
            + "                     GROUP  BY p.patient_id   "
            + "                   ) union_tbl  "
            + "             WHERE union_tbl.art_date  "
            + "                 BETWEEN :startDate AND :endDate   "
            + "            ) AS inicio ON inicio.patient_id = p.patient_id   "
            + " INNER JOIN ( "
            + "             SELECT p.patient_id, MIN(e.encounter_datetime) AS first_carga_viral, e.encounter_id "
            + "             FROM patient p  "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "             WHERE p.voided = 0  "
            + "                   AND e.voided = 0  "
            + "                   AND o.voided  = 0  "
            + "                   AND e.encounter_type = ${6}   "
            + "                   AND o.concept_id = ${23722}   "
            + "                   AND o.value_coded = ${856} "
            + "                   AND e.location_id = :location   "
            + "             GROUP BY p.patient_id "
            + "            ) AS carga_viral  ON carga_viral.patient_id = p.patient_id   "
            + " WHERE p.voided = 0  "
            + "       AND e.voided = 0  "
            + "       AND o.voided  = 0  "
            + "       AND e.encounter_type = ${6}  "
            + "       AND o.concept_id = ${23722}  "
            + "       AND o.value_coded = ${856}  "
            + "       AND e.encounter_datetime >= DATE_ADD(inicio.art_date,INTERVAL 80 DAY)  "
            + "       AND e.encounter_datetime <= DATE_ADD(inicio.art_date,INTERVAL 130 DAY)  "
            + "       AND e.location_id = :location";

    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   * filtrando os utentes que têm o registo de “Pedido de Investigações Laboratoriais” igual a
   * “Carga Viral” na primeira consulta clínica com registo de grávida durante o período de inclusão
   * (“Data 1ª Consulta Grávida”).
   *
   * @return CohortDefinition
   */
  public CohortDefinition getgetMQC13P2DenB4() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" B4 - categoria 13 - Denominador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    StringSubstitutor sb = new StringSubstitutor(map);

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e "
            + "ON         p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON         e.encounter_id = o.encounter_id "
            + "INNER JOIN "
            + "           ( "
            + "                  SELECT pregnant.patient_id, "
            + "                         pregnant.first_gestante "
            + "                  FROM   ( "
            + "                                    SELECT     p.patient_id, "
            + "                                               Min(e.encounter_datetime) AS first_gestante "
            + "                                    FROM       patient p "
            + "                                    INNER JOIN encounter e "
            + "                                    ON         e.patient_id = p.patient_id "
            + "                                    INNER JOIN obs o "
            + "                                    ON         e.encounter_id = o.encounter_id "
            + "                                    WHERE      p.voided = 0 "
            + "                                    AND        e.voided = 0 "
            + "                                    AND        o.voided = 0 "
            + "                                    AND        e.encounter_type = ${6} "
            + "                                    AND        o.concept_id = ${1982} "
            + "                                    AND        o.value_coded = ${1065} "
            + "                                    AND        e.encounter_datetime <= :endDate "
            + "                                    AND        e.location_id = :location "
            + "                                    GROUP BY   p.patient_id ) pregnant "
            + "                  WHERE  pregnant.first_gestante BETWEEN :startDate AND    :endDate ) pregnancy "
            + "ON         pregnancy.patient_id = p.patient_id "
            + "WHERE      p.voided = 0 "
            + "AND        e.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.encounter_type = ${6} "
            + "AND        o.concept_id = ${23722} "
            + "AND        o.value_coded = ${856} "
            + "AND        e.encounter_datetime = pregnancy.first_gestante "
            + "AND        e.location_id = :location "
            + "GROUP BY   p.patient_id";

    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   * 13.15. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram
   * TARV na CPN) Denominator: # de MG com registo de início do TARV na CPN dentro do período de
   * inclusão. (Line 90,Column F in the Template) as following: <code>
   * (A and B1) and NOT (D or F) and Age >= 15*</code>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getgetMQC13P2DenMGInIncluisionPeriod() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(" CAT 13 DEN - part 2 - 13.15. % de MG elegíveis ");
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition startedART = getMOHArtStartDate();

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

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

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition pregnantAbandonedDuringPeriod =
        getPatientsWhoAbandonedTarvOnArtStartDateForPregnants();

    cd.addSearch("A", EptsReportUtils.map(startedART, MAPPING));
    cd.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
    cd.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    cd.addSearch("ABANDONED", EptsReportUtils.map(pregnantAbandonedDuringPeriod, MAPPING));

    cd.setCompositionString("((A AND NOT ABANDONED) AND C) AND NOT (E OR F)");

    return cd;
  }

  /**
   * 13.16. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN
   * (MG que entraram em TARV na CPN) Denominator:# de MG que tiveram a primeira CPN no período de
   * inclusão, e que já estavam em TARV há mais de 3 meses (Line 91,Column F in the Template) as
   * following: B2
   *
   * @return CohortDefinition
   */
  public CohortDefinition getgetMQC13P2DenMGInIncluisionPeriod33Month() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("B2", EptsReportUtils.map(getMQC13P2DenB2(), MAPPING));

    CohortDefinition pregnantAbandonedDuringPeriod =
        getPatientsWhoAbandonedTarvBetween3MonthsBeforePregnancyDate();
    cd.addSearch("ABANDONED", EptsReportUtils.map(pregnantAbandonedDuringPeriod, MAPPING));

    cd.setCompositionString("B2 AND NOT ABANDONED");

    return cd;
  }

  /**
   * 13.17. % de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido
   * Denominator: # de MG com registo de pedido de CV no período de revisão (Line 92,Column F in the
   * Template) as following: <code>
   * ((A and B1 and B3) or (B2 and B4)) and NOT (D or E or F) and Age >= 15*</code>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2DenMGInIncluisionPeriod33Days() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

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

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));

    cd.addSearch("B2", EptsReportUtils.map(getMQC13P2DenB2(), MAPPING));
    cd.addSearch("B3", EptsReportUtils.map(getMQC13P2DenB3(), MAPPING));
    cd.addSearch("B4", EptsReportUtils.map(getgetMQC13P2DenB4(), MAPPING));

    cd.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
    cd.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));
    cd.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));
    cd.addSearch("F", EptsReportUtils.map(transfOut, MAPPING1));

    cd.setCompositionString("((A AND C AND B3) AND NOT (D OR E OR F)) OR (B2 AND B4)");

    return cd;
  }
  /**
   * <b>M&Q Report - Cat 10 Indicator 10.3 - Numerator and Denominator<br>
   *
   * <p>Conta para o numerador: Crianças diagnosticadas através do teste de PCR (registados na ficha
   * resumo, no campo Cuidados de HIV-Data-PCR) e que iniciaram o TARV no período de inclusão,
   * dentro de 15 dias após a data do diagnóstico (isto é a diferença entre a data do diagnóstico
   * através do PCR registada na ficha resumo e a data do início do TARV registada na ficha resumo
   * deve ser igual ou inferior a 15 dias). Selecionar todos os que iniciaram TARV (1a Linha) no
   * período de inclusão e Filtrar as Crianças ( 0 a 18m) excluindo Mulheres Grávidas, Lactantes,
   * Criancas>18m, Adultos e Transferidos De, com a diferença entre a Data do Início TARV (Ficha
   * Clínica) e a Data de Diagnóstico (Ficha Resumo - Data do PCR) é entre 0 a 15 dias.
   *
   * <ul>
   *   <li>A - Select all patients who initiated ART during the Inclusion Period (startDateRevision
   *       and endDateInclusion)
   *   <li>B – Filter all patients diagnosed with the PCR test (registado na ficha resumo, no campo
   *       Cuidados de HIV-Data-PCR)
   *   <li>C - Exclude all transferred in patients
   *   <li>D – Filter all patients with “ART Start Date”( the oldest date from A) minus “Diagnosis
   *       Date” >=0 and <=15 days
   * </ul>
   *
   * <p>10.3. % de crianças com PCR positivo para HIV que iniciaram TARV dentro de 2 semanas após o
   * diagnóstico/entrega do resultado ao cuidador
   *
   * <ul>
   *   <li>Denominator: # de crianças com idade compreendida entre 0 - 18 meses, diagnosticadas
   *       através do teste de PCR (registados na ficha resumo, no campo Cuidados de HIV-Data-PCR) e
   *       que iniciaram o TARV no período de inclusão. <b>A and B and NOT C and Age>=0months and
   *       &lt;18months</b>
   *   <li>Numerator: # crianças com idade compreendida entre 0 - 18 meses, diagnosticadas através
   *       do PCR (registado na ficha resumo, no campo Cuidados de HIV-Data-PCR) que tenham iniciado
   *       o TARV dentro de 15 dias após o diagnóstico através do PCR. <b>A and B and D and NOT C
   *       and Age>=0months and <18months</b>
   * </ul>
   *
   * <p>Age should be calculated on Patient ART Start Date
   *
   * @param flag report source
   * @return CohortDefinition
   */
  public CohortDefinition getMQ10NUMDEN103(String flag) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MQ10NUMDEN103 Cohort definition");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addSearch(
        "infant",
        EptsReportUtils.map(
            genericCohortQueries.getAgeInMonthsOnArtStartDate(0, 18),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            commonCohortQueries.getMohMQPatientsOnCondition(
                false,
                true,
                "once",
                hivMetadata.getMasterCardEncounterType(),
                hivMetadata.getTypeTestHIVConcept(),
                Collections.singletonList(hivMetadata.getHivPCRQualitativeConceptUuid()),
                hivMetadata.getTypeTestHIVConcept(),
                Collections.singletonList(hivMetadata.getHivPCRQualitativeConceptUuid())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
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
        "D",
        EptsReportUtils.map(
            genericCohortQueries.getArtDateMinusDiagnosisDate(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    if (flag.equals("num")) {
      cd.setCompositionString("(A AND B AND D AND infant) AND NOT C");
    } else if (flag.equals("den")) {
      cd.setCompositionString("(A AND B AND infant) AND NOT C");
    }
    return cd;
  }

  /**
   * K - Select all patients from Ficha Clinica (encounter type 6) with concept “Carga Viral”
   * (Concept id 856, value_numeric not null) OR concept “Carga Viral Qualitative”(Concept id 1305,
   * value_coded not null) and Encounter_datetime > “Data de Pedido de Carga Viral”(the date from
   * B3) and Encounter_datetime <= “Data de Pedido de Carga Viral”(the date from B3)+33days
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2NumK() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" K - categoria 13 - Numerador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        ""
            + "SELECT p.patient_id   "
            + "FROM patient p  "
            + "    INNER JOIN encounter e  "
            + "        ON e.patient_id = p.patient_id  "
            + "    INNER JOIN obs o  "
            + "        ON o.encounter_id = e.encounter_id  "
            + "    INNER JOIN  "
            + "                (  "
            + "                SELECT p.patient_id, e.encounter_datetime AS b3_datetime  "
            + "                FROM patient p  "
            + "                    INNER JOIN encounter e  "
            + "                        ON e.patient_id = p.patient_id  "
            + "                    INNER JOIN obs o  "
            + "                        ON o.encounter_id = e.encounter_id  "
            + "                    INNER JOIN  "
            + "                                (  "
            + "                                    SELECT inicio1.patient_id, inicio1.data_inicio  "
            + "                                    FROM (  "
            + "                                            SELECT   patient_id,Min(data_inicio) data_inicio  "
            + "                                            FROM (  "
            + "                                                    SELECT  p.patient_id, Min(value_datetime) data_inicio  "
            + "                                                    FROM       patient p  "
            + "                                                        INNER JOIN encounter e  "
            + "                                                            ON  p.patient_id = e.patient_id  "
            + "                                                        INNER JOIN obs o  "
            + "                                                            ON  e.encounter_id = o.encounter_id  "
            + "                                                    WHERE      p.voided = 0  "
            + "                                                        AND e.voided = 0  "
            + "                                                        AND o.voided = 0  "
            + "                                                        AND e.encounter_type = ${53}  "
            + "                                                        AND o.concept_id = ${1190}  "
            + "                                                        AND o.value_datetime IS NOT NULL  "
            + "                                                        AND o.value_datetime <= :endDate  "
            + "                                                        AND e.location_id = :location  "
            + "                                                    GROUP BY   p.patient_id  "
            + "                                                ) AS inicio  "
            + "                                                GROUP BY patient_id  "
            + "                                        ) inicio1  "
            + "                                    WHERE  data_inicio BETWEEN :startDate AND    :endDate  "
            + "                                ) art_start_date ON art_start_date.patient_id = p.patient_id  "
            + "                WHERE   "
            + "                    p.voided = 0  "
            + "                    AND e.voided = 0  "
            + "                    AND o.voided  = 0  "
            + "                    AND e.encounter_type = ${6}  "
            + "                    AND o.concept_id = ${23722}  "
            + "                    AND o.value_coded = ${856}  "
            + "                    AND e.encounter_datetime >= DATE_ADD(art_start_date.data_inicio,INTERVAL 80 DAY)  "
            + "                    AND e.encounter_datetime <= DATE_ADD(art_start_date.data_inicio,INTERVAL 130 DAY)  "
            + "                    AND e.location_id = :location  "
            + "                  "
            + "                            "
            + "                ) b3 ON b3.patient_id = p.patient_id       "
            + "WHERE   "
            + "    p.voided = 0  "
            + "    AND e.voided = 0  "
            + "    AND o.voided  = 0  "
            + "    AND e.encounter_type = ${6}  "
            + "    AND (  "
            + "            (o.concept_id = ${856}   AND o.value_numeric IS NOT NULL)  "
            + "            OR  "
            + "            (o.concept_id = ${1305}   AND o.value_coded IS NOT NULL)  "
            + "        )  "
            + "      "
            + "    AND e.encounter_datetime > b3.b3_datetime  "
            + "    AND e.encounter_datetime <= DATE_ADD(b3.b3_datetime, INTERVAL 33 DAY)  "
            + "    AND e.location_id = :location  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * L - Select all patients from Ficha Clinica (encounter type 6) with concept “Carga Viral”
   * (Concept id 856, value_numeric not null) or concept “Carga Viral Qualitative”(Concept id 1305,
   * value_coded not null) and Encounter_datetime > “Data de Pedido de Carga Viral”(the date from
   * B4) and Encounter_datetime <= “Data de Pedido de Carga Viral”(the date from B4)+33days
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2NumL() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" L - categoria 13 - Numerador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "    INNER JOIN ( "
            + "        SELECT p.patient_id, e.encounter_datetime AS b4_datetime "
            + "        FROM patient p "
            + "            INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "            INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "            INNER JOIN ( "
            + "                SELECT p.patient_id, MIN(first_gestante) AS min_datetime "
            + "                FROM patient p "
            + "                    INNER JOIN ( "
            + "                        SELECT patient_id, art_date, encounter_id "
            + "                        FROM	( "
            + "                            SELECT p.patient_id, Min(value_datetime) as art_date, e.encounter_id  "
            + "                            FROM patient p  "
            + "                                INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                            WHERE  p.voided = 0  "
            + "                              AND e.voided = 0  "
            + "                              AND o.voided = 0  "
            + "                              AND e.encounter_type = ${53} "
            + "                              AND o.concept_id = ${1190} "
            + "                              AND o.value_datetime IS NOT NULL "
            + "                              AND o.value_datetime <= :endDate "
            + "                              AND e.location_id = :location "
            + "                            GROUP  BY p.patient_id "
            + "                        ) union_tbl "
            + "                    ) AS inicio ON inicio.patient_id = p.patient_id "
            + "                    INNER JOIN ( "
            + "                        SELECT patient_id, first_gestante "
            + "                        FROM   ( "
            + "                            SELECT p.patient_id, MIN(e.encounter_datetime) AS first_gestante "
            + "                            FROM patient p "
            + "                                INNER JOIN person per on p.patient_id=per.person_id "
            + "                                INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                            WHERE   p.voided = 0 "
            + "                              AND per.gender = 'F' "
            + "                              AND e.voided = 0 "
            + "                              AND o.voided  = 0 "
            + "                              AND e.encounter_type = ${6} "
            + "                              AND o.concept_id = ${1982} "
            + "                              AND o.value_coded = ${1065} "
            + "                              AND e.location_id = :location "
            + "                            GROUP BY p.patient_id "
            + "                        ) gest "
            + "                        WHERE  gest.first_gestante BETWEEN :startDate AND :endDate "
            + "                    ) AS gestante  ON gestante.patient_id = p.patient_id AND gestante.first_gestante > inicio.art_date "
            + "                GROUP BY p.patient_id "
            + "                ) b2  ON b2.patient_id = p.patient_id "
            + "        WHERE p.voided = 0 "
            + "          AND e.voided = 0 "
            + "          AND o.voided  = 0 "
            + "          AND e.encounter_type = ${6} "
            + "          AND o.concept_id = ${23722} "
            + "          AND o.value_coded = ${856} "
            + "          AND e.encounter_datetime = b2.min_datetime "
            + "          AND e.location_id = :location "
            + "    ) b4 ON b4.patient_id = p.patient_id "
            + "WHERE p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided  = 0 "
            + "  AND e.encounter_type = ${6} "
            + "  AND ( "
            + "      (o.concept_id = ${856} AND o.value_numeric IS NOT NULL) "
            + "          OR "
            + "      (o.concept_id = ${1305} AND o.value_coded IS NOT NULL) "
            + "  ) "
            + "  AND e.encounter_datetime > b4.b4_datetime "
            + "  AND e.encounter_datetime <= DATE_ADD(b4.b4_datetime, INTERVAL 33 DAY) "
            + "  AND e.location_id = :location "
            + "GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * M - Select all patients from Ficha Resumo (encounter type 53) with “HIV Carga” viral”(Concept
   * id 856, value_numeric not null) and obs_datetime >= “Data de Pedido, de Carga Viral”(the date
   * from B3) and obs_datetime <= “Data de Pedido de Carga Viral”(the date from B3)+33days
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2NumM() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" M - categoria 13 - Numerador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT p.patient_id   "
            + "            FROM patient p  "
            + "                INNER JOIN encounter e  "
            + "                    ON e.patient_id = p.patient_id  "
            + "                INNER JOIN obs o  "
            + "                    ON o.encounter_id = e.encounter_id  "
            + "                INNER JOIN  "
            + "                            (  "
            + "                            SELECT p.patient_id, e.encounter_datetime AS b3_datetime  "
            + "                            FROM patient p  "
            + "                                INNER JOIN encounter e  "
            + "                                    ON e.patient_id = p.patient_id  "
            + "                                INNER JOIN obs o  "
            + "                                    ON o.encounter_id = e.encounter_id  "
            + "                                INNER JOIN  "
            + "                                            (  "
            + "                                                SELECT inicio1.patient_id, inicio1.data_inicio  "
            + "                                                FROM (  "
            + "                                                        SELECT   patient_id,Min(data_inicio) data_inicio  "
            + "                                                        FROM (  "
            + "                                                                SELECT  p.patient_id, Min(value_datetime) data_inicio  "
            + "                                                                FROM       patient p  "
            + "                                                                    INNER JOIN encounter e  "
            + "                                                                        ON  p.patient_id = e.patient_id  "
            + "                                                                    INNER JOIN obs o  "
            + "                                                                        ON  e.encounter_id = o.encounter_id  "
            + "                                                                WHERE      p.voided = 0  "
            + "                                                                    AND e.voided = 0  "
            + "                                                                    AND o.voided = 0  "
            + "                                                                    AND e.encounter_type = 53  "
            + "                                                                    AND o.concept_id = 1190  "
            + "                                                                    AND o.value_datetime IS NOT NULL  "
            + "                                                                    AND o.value_datetime <= :endDate  "
            + "                                                                    AND e.location_id = :location  "
            + "                                                                GROUP BY   p.patient_id  "
            + "                                                            ) AS inicio  "
            + "                                                            GROUP BY patient_id  "
            + "                                                    ) inicio1  "
            + "                                                WHERE  data_inicio BETWEEN :startDate AND    :endDate  "
            + "                                            ) art_start_date ON art_start_date.patient_id = p.patient_id  "
            + "                            WHERE   "
            + "                                p.voided = 0  "
            + "                                AND e.voided = 0  "
            + "                                AND o.voided  = 0  "
            + "                                AND e.encounter_type = ${6}  "
            + "                                AND o.concept_id = ${23722}  "
            + "                                AND o.value_coded = ${856}  "
            + "                                AND e.encounter_datetime >= DATE_ADD(art_start_date.data_inicio,INTERVAL 80 DAY)  "
            + "                                AND e.encounter_datetime <= DATE_ADD(art_start_date.data_inicio,INTERVAL 130 DAY)  "
            + "                                AND e.location_id = :location  "
            + "                              "
            + "                                        "
            + "                            ) b3 ON b3.patient_id = p.patient_id       "
            + "            WHERE   "
            + "                p.voided = 0  "
            + "                AND e.voided = 0  "
            + "                AND o.voided  = 0  "
            + "                AND e.encounter_type = ${53}  "
            + "                AND o.concept_id = ${856}   "
            + "                AND o.value_numeric IS NOT NULL "
            + "                AND o.obs_datetime >= b3.b3_datetime  "
            + "                AND o.obs_datetime <= DATE_ADD(b3.b3_datetime, INTERVAL 33 DAY)  "
            + "                AND e.location_id = :location  ;";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * N - Select all patients from Ficha Resumo (encounter type 53) with “HIV Carga viral”(Concept id
   * 856, value_numeric not null) and obs_datetime >= “Data de Pedido de Carga Viral”(the date from
   * B4) and obs_datetime <= “Data de Pedido de Carga Viral”(the date from B4)+33days
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2NumN() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setName(" N - categoria 13 - Numerador - part 2");

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "        SELECT p.patient_id "
            + "            FROM patient p "
            + "                INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                INNER JOIN ( "
            + "                    SELECT p.patient_id, e.encounter_datetime AS b4_datetime "
            + "                    FROM patient p "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN ( "
            + "                            SELECT p.patient_id, MIN(first_gestante) AS min_datetime "
            + "                            FROM patient p "
            + "                                INNER JOIN ( "
            + "                                    SELECT patient_id, art_date, encounter_id "
            + "                                    FROM ( "
            + "                                        SELECT p.patient_id, Min(value_datetime) as art_date, e.encounter_id  "
            + "                                        FROM patient p  "
            + "                                            INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                            INNER JOIN obs o ON e.encounter_id = o.encounter_id  "
            + "                                        WHERE  p.voided = 0  "
            + "                                          AND e.voided = 0  "
            + "                                          AND o.voided = 0  "
            + "                                          AND e.encounter_type = ${53} "
            + "                                          AND o.concept_id = 1190 "
            + "                                          AND o.value_datetime IS NOT NULL "
            + "                                          AND o.value_datetime <= :endDate "
            + "                                          AND e.location_id = :location "
            + "                                        GROUP  BY p.patient_id "
            + "                                    ) union_tbl "
            + "                                ) AS inicio ON inicio.patient_id = p.patient_id "
            + "                                INNER JOIN ( "
            + "                                    SELECT patient_id, first_gestante "
            + "                                    FROM   ( "
            + "                                        SELECT p.patient_id, MIN(e.encounter_datetime) AS first_gestante "
            + "                                        FROM patient p "
            + "                                            INNER JOIN person per on p.patient_id=per.person_id "
            + "                                            INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                            INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                        WHERE   p.voided = 0 "
            + "                                          AND per.gender = 'F' "
            + "                                          AND e.voided = 0 "
            + "                                          AND o.voided  = 0 "
            + "                                          AND e.encounter_type = 6 "
            + "                                          AND o.concept_id = 1982 "
            + "                                          AND o.value_coded = 1065 "
            + "                                          AND e.location_id = :location "
            + "                                        GROUP BY p.patient_id "
            + "                                    ) gest "
            + "                                    WHERE  gest.first_gestante BETWEEN :startDate AND :endDate "
            + "                                ) AS gestante  ON gestante.patient_id = p.patient_id AND gestante.first_gestante > inicio.art_date "
            + "                            GROUP BY p.patient_id "
            + "                            ) b2  ON b2.patient_id = p.patient_id "
            + "                    WHERE p.voided = 0 "
            + "                      AND e.voided = 0 "
            + "                      AND o.voided  = 0 "
            + "                      AND e.encounter_type = ${6} "
            + "                      AND o.concept_id = ${23722} "
            + "                      AND o.value_coded = ${856} "
            + "                      AND e.encounter_datetime = b2.min_datetime "
            + "                      AND e.location_id = :location "
            + "                ) b4 ON b4.patient_id = p.patient_id "
            + "            WHERE p.voided = 0  "
            + "                AND e.voided = 0  "
            + "                AND o.voided  = 0  "
            + "                AND e.encounter_type = ${53}  "
            + "                AND o.concept_id = ${856}   "
            + "                AND o.value_numeric IS NOT NULL"
            + "              AND o.obs_datetime >= b4.b4_datetime "
            + "              AND o.obs_datetime <= DATE_ADD(b4.b4_datetime, INTERVAL 33 DAY) "
            + "              AND e.location_id = :location "
            + "            GROUP BY p.patient_id;";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * 13.15. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram
   * TARV na CPN) (Line 90 in the template) Numerator (Column E in the Template) as following:
   * <code>(A and B1 and H) and NOT (D or F) and Age >= 15*</code>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2Num1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

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

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition pregnantAbandonedDuringPeriod =
        getPatientsWhoAbandonedTarvOnArtStartDateForPregnants();

    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));
    cd.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            transferredIn,
            "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch("F", EptsReportUtils.map(transfOut, MAPPING1));
    cd.addSearch("H", EptsReportUtils.map(getMQC13P2DenB3(), MAPPING));

    cd.addSearch("ABANDONED", EptsReportUtils.map(pregnantAbandonedDuringPeriod, MAPPING));

    cd.setCompositionString("((A AND NOT ABANDONED)AND C AND H) AND NOT (E OR F)");
    return cd;
  }

  /**
   * 13.16. % de MG elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN
   * (MG que entraram em TARV na CPN) (Line 91 in the template) Numerator (Column E in the Template)
   * as following: (B2 and J)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2Num2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch("B2", EptsReportUtils.map(getMQC13P2DenB2(), MAPPING));
    cd.addSearch("J", EptsReportUtils.map(getgetMQC13P2DenB4(), MAPPING));
    CohortDefinition pregnantAbandonedDuringPeriod =
        getPatientsWhoAbandonedTarvOnArtStartDateForPregnants();
    cd.addSearch("ABANDONED", EptsReportUtils.map(pregnantAbandonedDuringPeriod, MAPPING));

    cd.setCompositionString("(B2 AND NOT ABANDONED) AND J");

    return cd;
  }

  /**
   * 13.17. % de MG que receberam o resultado da Carga Viral dentro de 33 dias após pedido (Line 92
   * in the template) Numerator (Column E in the Template) as following: <code>
   * ((A and B1 and B3 and K) or (B2 and B4 and L)) and NOT (D or E or F) and Age >= 15*</code>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13P2Num3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "StartDate", Date.class));
    cd.addParameter(new Parameter("endDate", "EndDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition transfOut = commonCohortQueries.getTranferredOutPatients();

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

    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    cd.addSearch("A", EptsReportUtils.map(getMOHArtStartDate(), MAPPING));

    cd.addSearch("B2", EptsReportUtils.map(getMQC13P2DenB2(), MAPPING));
    cd.addSearch("B3", EptsReportUtils.map(getMQC13P2DenB3(), MAPPING));
    cd.addSearch("B4", EptsReportUtils.map(getgetMQC13P2DenB4(), MAPPING));

    cd.addSearch("C", EptsReportUtils.map(pregnant, MAPPING));
    cd.addSearch("D", EptsReportUtils.map(breastfeeding, MAPPING));
    cd.addSearch("E", EptsReportUtils.map(transferredIn, MAPPING));
    cd.addSearch("F", EptsReportUtils.map(transfOut, MAPPING1));
    cd.addSearch("K", EptsReportUtils.map(getMQC13P2NumK(), MAPPING));
    cd.addSearch("L", EptsReportUtils.map(getMQC13P2NumL(), MAPPING));
    cd.addSearch("M", EptsReportUtils.map(getMQC13P2NumM(), MAPPING));
    cd.addSearch("N", EptsReportUtils.map(getMQC13P2NumN(), MAPPING));

    cd.setCompositionString(
        "((A AND C AND B3 AND (K OR M)) AND NOT (D OR E OR F)) OR (B2 AND B4 AND (L OR N))");

    return cd;
  }

  /**
   * <b>MQ15Den: Melhoria de Qualidade Category 15 Denominator</b><br>
   * <br>
   * <i> DENOMINATOR 1: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 2: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 3: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 4: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 5: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> DENOMINATOR 6: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and
   * 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 7: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> DENOMINATOR 8: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and
   * 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 9: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> DENOMINATOR 10: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and
   * 14 </i> <br>
   * <br>
   * <i> DENOMINATOR 11: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> DENOMINATOR 12: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and
   * 14 </i> <br>
   *
   * @param den indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ15DEN(Integer den) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (den) {
      case 1:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV");
        break;
      case 2:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há pelo menos 12 meses em algum MDS (DT ou GAAC) com pedido de pelo menos uma CV");
        break;
      case 3:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) que receberam pelo menos um resultado de CV");
        break;
      case 4:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) com CV <1000 Cópias");
        break;
      case 5:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV");
        break;
      case 6:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV");
        break;
      case 7:
        comp.setName(
            "% de Crianças (2-9 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV");
        break;
      case 8:
        comp.setName(
            "% de Crianças (10-14 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV");
        break;
      case 9:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV");
        break;
      case 10:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV");
        break;
      case 11:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias");
        break;
      case 12:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    comp.addParameter(new Parameter("location", "location", Location.class));

    List<Integer> dispensationTypes =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states = Arrays.asList(hivMetadata.getStartDrugs().getConceptId());

    CohortDefinition queryA1 =
        QualityImprovement2020Queries.getPatientsWithFollowingMdcDispensationsWithStates(
            dispensationTypes, states);

    List<Integer> quarterlyDispensation =
        Arrays.asList(hivMetadata.getQuarterlyDispensation().getConceptId());
    CohortDefinition withDT =
        QualityImprovement2020Queries.getPatientsWithFollowingMdcDispensationsWithStates(
            quarterlyDispensation, states);

    CohortDefinition queryA3 =
        genericCohortQueries.hasCodedObs(
            hivMetadata.getTypeOfDispensationConcept(),
            BaseObsCohortDefinition.TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            Arrays.asList(
                hivMetadata.getQuarterlyConcept(), hivMetadata.getSemiannualDispensation()));

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();

    CohortDefinition dead = resumoMensalCohortQueries.getPatientsWhoDied(false);

    CohortDefinition nextPickupBetween83And97 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBetween(83, 97);
    CohortDefinition nextPickupBetween173And187 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBetween(173, 187);

    CohortDefinition viralLoad = QualityImprovement2020Queries.getPatientsWithVlGreaterThen1000();
    // Pacientes com pedidos de investigações depois de DT
    List<Integer> concepts = Arrays.asList(hivMetadata.getQuarterlyDispensation().getConceptId());
    CohortDefinition IADT =
        getPatientsWithVLResultLessThan1000Between2VlRequestAfterTheseMDS(concepts);

    // Utentes que tiveram dois pedidos de investigação depois da inscrição ao GACC/DT/APE/DD/FR/DS
    List<Integer> mdsConcepts =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());
    CohortDefinition IAMDS =
        getPatientsWithVLResultLessThan1000Between2VlRequestAfterTheseMDS(mdsConcepts);

    // Utentes que têm o registo de Resultado de Carga Viral na Ficha Laboratório registada entre a
    // data do 2o pedido de CV e Data de Revisao
    CohortDefinition VLFL =
        getPatientsWhoHadVLResultOnLaboratoryFormAfterSecoddVLRequest(mdsConcepts);

    comp.addSearch(
        "A1",
        EptsReportUtils.map(
            queryA1,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},location=${location}"));

    comp.addSearch(
        "NPF83",
        EptsReportUtils.map(
            nextPickupBetween83And97,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},location=${location}"));

    comp.addSearch(
        "NPF173",
        EptsReportUtils.map(
            nextPickupBetween173And187,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},location=${location}"));

    comp.addSearch(
        "DT",
        EptsReportUtils.map(
            withDT,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},location=${location}"));

    comp.addSearch(
        "A3",
        EptsReportUtils.map(
            queryA3,
            "onOrAfter=${revisionEndDate-26m+1d},onOrBefore=${revisionEndDate-24m},locationList=${location}"));

    comp.addSearch(
        "CD",
        EptsReportUtils.map(
            getPregnantOrBreastfeedingWomen(),
            "revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "G2",
        EptsReportUtils.map(
            getCombinedB13ForCat15Indicators(),
            "revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    comp.addSearch(
        "dead",
        EptsReportUtils.map(dead, "onOrBefore=${revisionEndDate},locationList=${location}"));

    comp.addSearch(
        "VL",
        EptsReportUtils.map(
            viralLoad,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "IADT",
        EptsReportUtils.map(
            IADT,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "IAMDS",
        EptsReportUtils.map(
            IAMDS,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "VLFL",
        EptsReportUtils.map(
            VLFL,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    if (den == 1) {
      comp.setCompositionString("(A1 OR A3 OR NPF83 OR NPF173) AND NOT (CD OR F OR dead)");
    } else if (den == 2) {
      comp.setCompositionString("((A1 OR A3 OR NPF83 OR NPF173) AND NOT (CD OR F OR VL)) AND G2");
    } else if (den == 3) {
      comp.setCompositionString(
          "(A1 OR A3 OR NPF83 OR NPF173) AND G2 AND IAMDS AND NOT (CD OR F OR VL)");
    } else if (den == 4) {
      comp.setCompositionString(
          "((A1 OR A3 OR NPF83 OR NPF173) AND G2 AND IAMDS AND VLFL AND NOT (CD OR F OR VL)) ");
    } else if (den == 5 || den == 6) {
      comp.setCompositionString("(DT OR A3 OR NPF83 OR NPF173) AND  NOT (CD OR F OR dead)");
    } else if (den == 7 || den == 8) {
      comp.setCompositionString("((DT OR A3 OR NPF83 OR NPF173) AND  NOT (CD OR F OR VL)) AND G2 ");
    } else if (den == 11 || den == 12) {
      comp.setCompositionString(
          "((DT OR A3 OR NPF83 OR NPF173) AND  NOT (CD OR F OR VL)) AND G2 AND IADT AND VLFL");
    } else if (den == 9 || den == 10) {
      comp.setCompositionString(
          "((DT OR A3 OR NPF83 OR NPF173) AND  NOT (CD OR F OR VL)) AND G2 IADT");
    }
    return comp;
  }

  /**
   * <b>MQ15Num: Melhoria de Qualidade Category 15 Numerator</b><br>
   * <br>
   * <i> NUMERATOR 1: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> NUMERATOR 2: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> NUMERATOR 3: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> NUMERATOR 4: A and NOT B1 and NOT C and NOT D and NOT F and Age > 14 </i> <br>
   * <br>
   * <i> NUMERATOR 5: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> NUMERATOR 6: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and 14
   * </i> <br>
   * <br>
   * <i> NUMERATOR 7: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> NUMERATOR 8: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and 14
   * </i> <br>
   * <br>
   * <i> NUMERATOR 9: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> NUMERATOR 10: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and 14
   * </i> <br>
   * <br>
   * <i> NUMERATOR 11: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 2 and 9
   * </i> <br>
   * <br>
   * <i> NUMERATOR 12: (A2 or A3) and NOT B1 and NOT C and NOT D and NOT F and Age between 10 and 14
   * </i> <br>
   *
   * @param num indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ15NUM(Integer num) {
    CompositionCohortDefinition comp = new CompositionCohortDefinition();

    switch (num) {
      case 1:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS  (DT ou GAAC) que continuam activos em TARV");
        break;
      case 2:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há pelo menos 12 meses em algum MDS (DT ou GAAC) com pedido de pelo menos uma CV");
        break;
      case 3:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) que receberam pelo menos um resultado de CV");
        break;
      case 4:
        comp.setName(
            "% de Adultos (15/+anos) inscritos há 12 meses em algum MDS (DT ou GAAC) com CV <1000 Cópias");
        break;
      case 5:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que continuam activos em TARV");
        break;
      case 6:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 em algum MDS (DT) que continuam activos em TARV");
        break;
      case 7:
        comp.setName(
            "% de Crianças (2-9 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV");
        break;
      case 8:
        comp.setName(
            "% de Crianças (10-14 anos de idade) inscritas há 12 meses em algum MDS (DT) com pedido de pelo menos uma CV");
        break;
      case 9:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV");
        break;
      case 10:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) que receberam pelo menos um resultado de CV");
        break;
      case 11:
        comp.setName(
            "% de Crianças (2-9 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias");
        break;
      case 12:
        comp.setName(
            "% de Crianças (10-14 anos) inscritas há 12 meses em algum MDS (DT) com CV <1000 Cópias");
        break;
    }

    comp.addParameter(new Parameter("startDate", "startDate", Date.class));
    comp.addParameter(new Parameter("endDate", "endDate", Date.class));
    comp.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    comp.addParameter(new Parameter("location", "location", Location.class));

    List<Integer> dispensationTypes =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states = Arrays.asList(hivMetadata.getStartDrugs().getConceptId());

    CohortDefinition queryA1 =
        QualityImprovement2020Queries.getPatientsWithFollowingMdcDispensationsWithStates(
            dispensationTypes, states);

    CohortDefinition queryA2 =
        QualityImprovement2020Queries.getMQ15DenA1orA2(
            "A2",
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId());

    CohortDefinition queryA3 =
        QualityImprovement2020Queries.getMQ15DenA3(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getQuarterlyConcept().getConceptId(),
            hivMetadata.getTypeOfDispensationConcept().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    CohortDefinition pregnant =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getPregnantConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition breastfeeding =
        commonCohortQueries.getMOHPregnantORBreastfeeding(
            commonMetadata.getBreastfeeding().getConceptId(),
            hivMetadata.getYesConcept().getConceptId());

    CohortDefinition h1 =
        QualityImprovement2020Queries.getMQ15NumH(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getQuarterlyConcept().getConceptId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getTypeOfDispensationConcept().getConceptId(),
            hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
            hivMetadata.getHivViralLoadConcept().getConceptId());

    CohortDefinition h2 =
        QualityImprovement2020Queries.getMQ15NumH2(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getQuarterlyConcept().getConceptId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getTypeOfDispensationConcept().getConceptId(),
            hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());

    CohortDefinition i =
        QualityImprovement2020Queries.getMQ15NumI(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getQuarterlyConcept().getConceptId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getTypeOfDispensationConcept().getConceptId(),
            hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());

    CohortDefinition transferOut = commonCohortQueries.getTranferredOutPatients();
    // Utentes que tiveram dois pedidos de investigação depois da inscrição ao GACC/DT/APE/DD/FR/DS
    List<Integer> concepts =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());
    CohortDefinition IAMDS =
        getPatientsWithVLResultLessThan1000Between2VlRequestAfterTheseMDS(concepts);

    // Utentes que têm o registo de Resultado de Carga Viral na Ficha Laboratório registada entre a
    // data do 2o pedido de CV e Data de Revisao
    CohortDefinition VLFL = getPatientsWhoHadVLResultOnLaboratoryFormAfterSecoddVLRequest(concepts);

    CohortDefinition LOWVLFL =
        getPatientsWhoHadVLResultLessThen1000nLaboratoryFormAfterSecudondVLRequest(concepts);

    // Pacientes com pedidos de investigações depois de DT
    List<Integer> dtConcept = Arrays.asList(hivMetadata.getQuarterlyDispensation().getConceptId());
    CohortDefinition IADT =
        getPatientsWithVLResultLessThan1000Between2VlRequestAfterTheseMDS(dtConcept);

    comp.addSearch(
        "A1",
        EptsReportUtils.map(
            queryA1,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}"));

    comp.addSearch(
        "A2",
        EptsReportUtils.map(
            queryA2,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},location=${location}"));

    comp.addSearch(
        "A3",
        EptsReportUtils.map(
            queryA3,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}"));

    comp.addSearch(
        "C",
        EptsReportUtils.map(
            pregnant,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}"));

    comp.addSearch(
        "D",
        EptsReportUtils.map(
            breastfeeding,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},location=${location}"));

    comp.addSearch("F", EptsReportUtils.map(transferOut, MAPPING1));

    comp.addSearch(
        "H1",
        EptsReportUtils.map(
            h1, "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "H2",
        EptsReportUtils.map(
            h2, "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "I",
        EptsReportUtils.map(
            i, "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "Den1",
        EptsReportUtils.map(
            getMQ15DEN(1),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "Den2",
        EptsReportUtils.map(
            getMQ15DEN(2),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "Den3",
        EptsReportUtils.map(
            getMQ15DEN(3),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "Den4",
        EptsReportUtils.map(
            getMQ15DEN(4),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "Den5",
        EptsReportUtils.map(
            getMQ15DEN(5),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "Den7",
        EptsReportUtils.map(
            getMQ15DEN(7),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "Den10",
        EptsReportUtils.map(
            getMQ15DEN(10),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "Den11",
        EptsReportUtils.map(
            getMQ15DEN(11),
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "G2",
        EptsReportUtils.map(
            getCombinedB13ForCat15Indicators(),
            "revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "IAMDS",
        EptsReportUtils.map(
            IAMDS,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));
    comp.addSearch(
        "VLFL",
        EptsReportUtils.map(
            VLFL,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "LOWVLFL",
        EptsReportUtils.map(
            LOWVLFL,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    comp.addSearch(
        "IADT",
        EptsReportUtils.map(
            IADT,
            "startDate=${revisionEndDate-26m+1d},endDate=${revisionEndDate-24m},revisionEndDate=${revisionEndDate},location=${location}"));

    if (num == 1) {
      comp.setCompositionString("Den1 AND G2");
    } else if (num == 2) {
      comp.setCompositionString("Den2 AND IAMDS");
    } else if (num == 3) {
      comp.setCompositionString("Den3  AND VLFL");
    } else if (num == 4) {
      comp.setCompositionString("Den4 AND LOWVLFL");
    } else if (num == 5 || num == 6) {
      comp.setCompositionString("Den5 AND G2");
    } else if (num == 7 || num == 8) {
      comp.setCompositionString("Den7  AND G2 AND IADT");
    } else if (num == 9 || num == 10) {
      comp.setCompositionString("Den10 AND G2 AND VLFL");
    } else if (num == 11) {
      comp.setCompositionString("Den11 AND G2 AND LOWVLFL");
    } else if (num == 12) {
      comp.setCompositionString("Den11  AND G2 LOWVLFL");
    }
    return comp;
  }

  /**
   * Filtrando os pacientesutentes que têm o registo de início do MDS para pacienteutente estável na
   * última consulta decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” –
   * 12 meses+1dia e <= “Data Fim Revisão”), ou seja, registo de um MDC (MDC1 ou MDC2 ou MDC3 ou
   * MDC4 ou MDC5) como:
   *
   * <p>“GA” e o respectivo “Estado” = “Início” ou “DT” e o respectivo “Estado” = “Início” ou “DS” e
   * o respectivo “Estado” = “Início” ou “APE” e o respectivo “Estado” = “Início” ou “FR” e o
   * respectivo “Estado” = “Início” ou “DD” e o respectivo “Estado” = “Início” na última consulta
   * clínica (“Ficha Clínica”, coluna 24) decorrida entre: “Data Início de Avaliação” = “Data Fim de
   * Revisão” menos 12 meses + 1 dia “Data Fim de Avaliação” = “Data Fim de Revisão”
   *
   * <p>os utentes que têm o registo de “Tipo de Dispensa” = “DT” na última consulta (“Ficha
   * Clínica”) decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12
   * meses+1dia e <= “Data Fim Revisão”)
   *
   * <p>os utentes com registo de “Tipo de Dispensa” = “DS” na última consulta (“Ficha Clínica”)
   * decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12 meses+1dia e
   * <= “Data Fim Revisão”)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 12 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim Revisão”) com próximo
   * levantamento agendado para 83 a 97 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 83 dias e <= 97 dias)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 12 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim Revisão”) com próximo
   * levantamento agendado para 173 a 187 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 173 dias e <= 187 dias)
   */
  public CohortDefinition getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36(
      List<Integer> dispensationTypes, List<Integer> states) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "MDS para utentes estáveis que tiveram consulta no período de avaliação");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mdsLastClinical =
        getPatientsWithMdcOnMostRecentClinicalFormWithFollowingDispensationTypesAndState(
            dispensationTypes, states);

    CohortDefinition queryA3 =
        genericCohortQueries.hasCodedObs(
            hivMetadata.getTypeOfDispensationConcept(),
            BaseObsCohortDefinition.TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            Arrays.asList(
                hivMetadata.getQuarterlyConcept(), hivMetadata.getSemiannualDispensation()));

    CohortDefinition nextPickupBetween83And97 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBetween(83, 97);

    CohortDefinition nextPickupBetween173And187 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBetween(173, 187);

    compositionCohortDefinition.addSearch(
        "MDS",
        EptsReportUtils.map(
            mdsLastClinical, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "DSDT",
        EptsReportUtils.map(
            queryA3, "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA83",
        EptsReportUtils.map(
            nextPickupBetween83And97,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA173",
        EptsReportUtils.map(
            nextPickupBetween173And187,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("MDS OR DSDT OR FILA83 OR FILA173");

    return compositionCohortDefinition;
  }

  /**
   * O sistema irá identificar utentes já inscritos em algum MDS selecionado os seguinte utentes:
   * selecionando todos os utentes que têm o último registo de pelo menos um dos seguintes modelos
   * na “Ficha Clínica” (coluna 24) registada antes da “Data Última Consulta”:
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “GAAC” e o respectivo
   * “Estado” = “Iníicioar” ou “Continua”, ou
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “DT” e o respectivo
   * “Estado” = “Início” ou “Continua”, ou
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “DS” e o respectivo
   * “Estado” = “Início” ou “Continua”, ou
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “APE” e o respectivo
   * “Estado” = “Início” ou “Continua”, ou
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “FR” e o respectivo
   * “Estado” = “Início” ou “Continua”, ou
   *
   * <p>Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “DD” e o respectivo
   * “Estado” = “Início” ou “Continua”
   *
   * <p>Nota1: A “Data Última Consulta” é a última “Data de Consulta” do utente ocorrida no período
   * compreendido entre: “Data Início Avaliação” = “Data Fim de Revisão” menos 12 meses + 1 dia
   * “Data Fim Avaliação” = “Data Fim de Revisão”
   *
   * <p>Filtrando os utentes que têm o último registo de “Tipo de Dispensa” = “DT” antes da última
   * consulta do período de revisão ( “Data Última Consulta”)
   *
   * <p>Filtrando os utentes que têm o último registo de “Tipo de Dispensa” = “DS” antes da última
   * consulta do período de revisão ( “Data Última Consulta”)
   *
   * <p>Filtrando os utentes com registo de último levantamento na farmácia (FILA) antes da última
   * consulta do período de revisão (“Data última Consulta) com próximo levantamento agendado para
   * 83 a 97 dias ( “Data Próximo Levantamento” menos “Data Levantamento”>= 83 dias e <= 97 dias,
   * sendo “Data Levantamento” último levantamento registado no FILA < “Data Última Consulta”)
   *
   * <p>Filtrando os utentes com registo de último levantamento na farmácia (FILA) antes da última
   * consulta do período de revisão (“Data última Consulta) com próximo levantamento agendado para
   * 173 a 187 dias ( “Data Próximo Levantamento” menos “Data Levantamento”>= 173 dias e <= 187
   * dias, sendo “Data Levantamento” último levantamento registado no FILA < “Data Última
   * Consulta”).
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsAlreadyEnrolledInTheMdc() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "MDS para utentes estáveis que tiveram consulta no período de avaliação");
    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    List<Integer> mdsConcepts =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states =
        Arrays.asList(
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId());

    CohortDefinition mdsLastClinical =
        getPatientsWithMdcBeforeMostRecentClinicalFormWithFollowingDispensationTypesAndState(
            mdsConcepts, states);

    CohortDefinition dtBeforeClinical =
        getPatientsWithDispensationBeforeLastConsultationDate(hivMetadata.getQuarterlyConcept());
    CohortDefinition dsBeforeClinical =
        getPatientsWithDispensationBeforeLastConsultationDate(
            hivMetadata.getSemiannualDispensation());

    CohortDefinition filaBC83 = getPatientsWhoHadFilaBeforeLastClinicalConsutationBetween(83, 97);

    CohortDefinition filaBC173 =
        getPatientsWhoHadFilaBeforeLastClinicalConsutationBetween(173, 187);

    compositionCohortDefinition.addSearch(
        "MDS",
        EptsReportUtils.map(
            mdsLastClinical, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "DT",
        EptsReportUtils.map(
            dtBeforeClinical, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "DS",
        EptsReportUtils.map(
            dsBeforeClinical, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA83",
        EptsReportUtils.map(
            filaBC83, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA173",
        EptsReportUtils.map(
            filaBC173, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("MDS OR DS OR DT OR FILA83 OR FILA173");

    return compositionCohortDefinition;
  }

  /**
   * <b>MQ14Den: M&Q Report - Categoria 14 Denominador</b><br>
   * <i>A - Select all patientsTX PVLS DENOMINATOR: TX PVLS Denominator</i> <i>A1 - Filter all
   * Pregnant Women from A</i> <i>A2 - Filter all Breastfeeding Women from A</i> <b>The following
   * disaggregations will be outputed</b>
   *
   * <ul>
   *   <li>14.1. % de adultos (15/+anos) em TARV com supressão viral - A and NOT A1 and NOT A2 and
   *       Age > 14
   *   <li>14.2.% de crianças (0-14 anos) em TARV com supressão viral - A and NOT A1 and NOT A2 and
   *       Age <= 14
   *   <li>14.3.% de MG em TARV com supressão viral - A and A1 and NOT A2
   *   <li>14.4. % de ML em TARV com supressão viral - A and NOT A1 and A2
   * </ul>
   *
   * @param preposition composition string and description
   * @return CohortDefinition
   */
  public CohortDefinition getMQ14(MQCat14Preposition preposition) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(preposition.getDescription());

    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            txPvls.getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "A1",
        EptsReportUtils.map(
            txPvls.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter4MQ(
                EptsReportConstants.PregnantOrBreastfeedingWomen.PREGNANTWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "A2",
        EptsReportUtils.map(
            txPvls.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter4MQ(
                EptsReportConstants.PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString(preposition.getCompositionString());

    return cd;
  }

  public enum MQCat14Preposition {
    A {
      @Override
      public String getCompositionString() {
        return "A";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - A ";
      }
    },
    A_AND_A1 {
      @Override
      public String getCompositionString() {
        return "A AND A1";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - A and A1 ";
      }
    },
    A_AND_A2 {
      @Override
      public String getCompositionString() {
        return "A AND A2";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - A and A2";
      }
    },
    B {
      @Override
      public String getCompositionString() {
        return "B";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - B ";
      }
    },
    B_AND_B1 {
      @Override
      public String getCompositionString() {
        return "B AND B1";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - B AND B1";
      }
    },
    B_AND_B2 {
      @Override
      public String getCompositionString() {
        return "B AND B2";
      }

      @Override
      public String getDescription() {
        return "MQ Cat 14 - B AND B2";
      }
    };

    public abstract String getCompositionString();

    public abstract String getDescription();
  }

  /**
   * <b>MQ14Num: M&Q Report - Categoria 14 Numerator</b><br>
   * <i>B - Select all patientsTX PVLS NUMERATOR: TX PVLS NUMERATOR</i> <i>B1 - Filter all Pregnant
   * Women from B</i> <i>B2 - Filter all Breastfeeding Women from B</i> <b>The following
   * disaggregations will be outputed</b>
   *
   * <ul>
   *   <li>14.1. % de adultos (15/+anos) em TARV com supressão viral - B and NOT B1 and NOT B2 and
   *       Age > 14
   *   <li>14.2.% de crianças (0-14 anos) em TARV com supressão viral - B and NOT B1 and NOT B2 and
   *       Age <= 14
   *   <li>14.3.% de MG em TARV com supressão viral - B and B1 and NOT B2
   *   <li>14.4. % de ML em TARV com supressão viral - B and NOT B1 and B2
   * </ul>
   *
   * @param preposition
   * @return CohortDefinition
   */
  public CohortDefinition getMQ14NUM(MQCat14Preposition preposition) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName(preposition.getDescription());
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txPvls.getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            txPvls.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter4MQ(
                EptsReportConstants.PregnantOrBreastfeedingWomen.PREGNANTWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            txPvls.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter4MQ(
                EptsReportConstants.PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString(preposition.getCompositionString());

    return cd;
  }

  /**
   * <b>MQ9Den: M&Q Report - Categoria 9 Denominador</b><br>
   *
   * <ul>
   *   <li>9.1. % de adultos HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4
   *       dentro de 33 dias após a inscrição
   *   <li>9.2. % de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4
   *       dentro de 33 dias após a inscrição
   * </ul>
   *
   * @param flag indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ9Den(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 1:
        cd.setName(
            "% de adultos  HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4 dentro de 33 dias após a inscrição");
        break;
      case 2:
        cd.setName(
            "% de adultos HIV+ ≥ 15 anos que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
      case 3:
        cd.setName(
            "% de crianças HIV+ ≤ 14 anos que teve registo de pedido do primeiro CD4 na data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
      case 4:
        cd.setName(
            "% de crianças HIV+ ≤ 14 anos que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
    }

    if (flag == 1 || flag == 2) {
      cd.addSearch(
          "AGE",
          EptsReportUtils.map(
              genericCohortQueries.getAgeOnFirstClinicalConsultation(15, null),
              "onOrAfter=${revisionEndDate-12m+1d},onOrBefore=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    } else if (flag == 3 || flag == 4) {
      cd.addSearch(
          "AGE",
          EptsReportUtils.map(
              genericCohortQueries.getAgeOnFirstClinicalConsultation(0, 14),
              "onOrAfter=${revisionEndDate-12m+1d},onOrBefore=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    }

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String inclusionPeriodMappings =
        "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},location=${location}";

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getFirstClinicalConsultationDuringInclusionPeriod(),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "pregnantOnPeriod",
        EptsReportUtils.map(
            getMOHPregnantORBreastfeedingOnClinicalConsultation(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));

    cd.addSearch(
        "breastfeedingOnPeriod",
        EptsReportUtils.map(
            getMOHPregnantORBreastfeedingOnClinicalConsultation(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));

    cd.setCompositionString(
        "A AND NOT (C OR D OR E OR pregnantOnPeriod OR breastfeedingOnPeriod) AND AGE");
    return cd;
  }

  /**
   * <b>Categoria 9 Denominador - Pedido e Resultado de CD4 - MG</b>
   * <li>Pedido de CD4 = “% de MG HIV+ que teve registo de pedido do primeiro CD4 na data da
   *     primeira consulta clínica/abertura da Ficha Mestra”
   * <li>Resultado de CD4 = “% de MG HIV+ que teve conhecimento do resultado do primeiro CD4 dentro
   *     de 33 dias após a data da primeira CPN (primeira consulta com registo de Gravidez”
   *
   * @param flag parameter to receive the indicator numbe
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getCd4RequestAndResultForPregnantsCat9Den(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 5:
        cd.setName(
            "Pedido de CD4 = “% de MG HIV+ que teve registo de pedido do primeiro CD4 na data da primeira consulta clínica/abertura da Ficha Mestra”");
        break;
      case 6:
        cd.setName(
            "Resultado de CD4 = “% de MG HIV+ que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira CPN (primeira consulta com registo de Gravidez”");
        break;
    }

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String inclusionPeriodMappings =
        "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}";

    cd.addSearch(
        "pregnantOnPeriod",
        EptsReportUtils.map(
            getFirstPregnancyORBreastfeedingOnClinicalConsultation(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));

    cd.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("pregnantOnPeriod AND NOT transferredIn");

    return cd;
  }

  /**
   * O sistema irá produzir o Numerador para o indicador do pedido de CD4 para MG: “# de MG HIV+ em
   * TARV com registo de pedido de CD4 na primeira CPN (Primeira consulta com registo Gravidez)”
   *
   * @param flag parameter to receive the indicator number
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getCd4RequestAndResultForPregnantsCat9Num(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 5:
        cd.setName(
            "Pedido de CD4 = “% de MG HIV+ que teve registo de pedido do primeiro CD4 na data da primeira consulta clínica/abertura da Ficha Mestra”");
        break;
      case 6:
        cd.setName(
            "Resultado de CD4 = “% de MG HIV+ que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira CPN (primeira consulta com registo de Gravidez”");
        break;
    }

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String inclusionPeriodMappings =
        "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}";

    cd.addSearch(
        "pregnantOnPeriod",
        EptsReportUtils.map(
            getFirstPregnancyORBreastfeedingOnClinicalConsultation(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));

    cd.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "requestCd4ForPregnant",
        EptsReportUtils.map(
            getRequestForCd4OnFirstClinicalConsultationOfPregnancy(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId(),
                hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                hivMetadata.getCD4AbsoluteOBSConcept().getConceptId()),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "resultCd4ForPregnant",
        EptsReportUtils.map(
            getCd4ResultAfterFirstConsultationOfPregnancy(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));

    if (flag == 5) {
      cd.setCompositionString("(pregnantOnPeriod AND requestCd4ForPregnant) AND NOT transferredIn");
    } else if (flag == 6) {
      cd.setCompositionString("(pregnantOnPeriod AND resultCd4ForPregnant) AND NOT transferredIn");
    }

    return cd;
  }

  /**
   * <b>MQ10Den: M&Q Report - Categoria 10 Denominador</b><br>
   *
   * <ul>
   *   <li>10.1. % de adultos (15/+anos) que iniciaram o TARV dentro de 15 dias após diagnóstico
   *   <li>10.3. % de crianças (0-14 anos) HIV+ que iniciaram TARV dentro de 15 dias após
   *       diagnóstico
   * </ul>
   *
   * @param adults indicators flag
   * @return CohortDefinition
   */
  public CohortDefinition getMQ10Den(boolean adults) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    if (adults) {
      cd.setName("Category 10 Denominator adults");
    } else {
      cd.setName("Category 10 Denominator children");
    }

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getMOHArtStartDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            getChildrenCompositionMore19Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    if (adults) {
      cd.setCompositionString("A AND NOT (C OR D OR E) AND ADULT");
    } else {
      cd.setCompositionString("A AND NOT (C OR D OR E) AND CHILDREN");
    }

    return cd;
  }

  private CohortDefinition getChildrenCompositionMore19Months() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(0, 14, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "BABIES",
        EptsReportUtils.map(
            genericCohortQueries.getAgeInMonthsOnArtStartDate(0, 20),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("CHILDREN AND NOT BABIES");

    return cd;
  }

  /**
   *
   * <li>Filtrando os que tiveram registo do “Resultado de CD4” na consulta clínica decorrida em 33
   *     dias após a primeira consulta clínica do período de inclusão (= “Data Fim de Revisão” menos
   *     (-) 12 meses mais (+) 1 dia e “Data fim de Revisão” menos (-) 9 meses), ou seja, “Data
   *     Resultado de CD4” menos a “Data Primeira Consulta” <=33 dias
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getCd4ResultAfterFirstConsultationOnInclusionPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "B: Filter all patients with CD4 within 33 days from the first clinical consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        "SELECT pa.patient_id "
            + "FROM "
            + "    patient pa "
            + "        INNER JOIN encounter enc "
            + "                   ON enc.patient_id =  pa.patient_id "
            + "        INNER JOIN obs "
            + "                   ON obs.encounter_id = enc.encounter_id "
            + "        INNER JOIN obs o2 "
            + "                   ON o2.encounter_id = enc.encounter_id "
            + "        INNER JOIN "
            + "    ( "
            + "        SELECT final.patient_id, final.first_consultation "
            + "                    FROM   ( "
            + "                               SELECT pa.patient_id, "
            + "                                      MIN(enc.encounter_datetime) AS first_consultation "
            + "                               FROM   patient pa "
            + "                                          INNER JOIN encounter enc "
            + "                                                     ON enc.patient_id =  pa.patient_id "
            + "                                          INNER JOIN obs "
            + "                                                     ON obs.encounter_id = enc.encounter_id "
            + "                               WHERE pa.voided = 0 "
            + "                                 AND enc.voided = 0 "
            + "                                 AND obs.voided = 0 "
            + "                                 AND enc.encounter_type = ${6} "
            + "                                 AND enc.encounter_datetime <= :revisionEndDate "
            + "                                 AND enc.location_id = :location "
            + "                               GROUP  BY pa.patient_id "
            + "                           ) final "
            + "                    WHERE  final.first_consultation >= :startDate "
            + "                      AND final.first_consultation <= :endDate "
            + "        GROUP  BY final.patient_id "
            + "    ) consultation_date ON consultation_date.patient_id = pa.patient_id "
            + "WHERE  pa.voided = 0 "
            + "  AND enc.voided = 0 "
            + "  AND obs.voided = 0 "
            + "  AND o2.voided = 0 "
            + "  AND enc.encounter_type = ${6} "
            + "  AND ( "
            + "        (obs.concept_id = ${1695} AND obs.value_numeric IS NOT NULL) "
            + "        OR "
            + "        (o2.concept_id = ${730} AND o2.value_numeric IS NOT NULL) "
            + "      ) "
            + "  AND enc.encounter_datetime > consultation_date.first_consultation "
            + "  AND enc.encounter_datetime <= DATE_ADD(consultation_date.first_consultation, INTERVAL 33 DAY) "
            + "  AND enc.location_id = :location "
            + "GROUP BY pa.patient_id";

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1695", hivMetadata.getCD4AbsoluteOBSConcept().getConceptId());
    map.put("730", hivMetadata.getCD4PercentConcept().getConceptId());

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQ9Num: M&Q Report - Categoria 9 Numerador - Pedido de CD4 Adulto</b><br>
   *
   * <ul>
   *   <li>9.1. % de adultos HIV+ ≥ 15 anos que teve registo de pedido do primeiro CD4 na data da
   *       primeira consulta clínica/abertura da Ficha Mestra”
   *   <li>9.2. % de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro CD4
   *       dentro de 33 dias após a inscrição
   * </ul>
   *
   * @param flag indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ9Num(int flag) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    switch (flag) {
      case 1:
        cd.setName(
            "% de adultos HIV+ ≥ 15 anos que teve registo de pedido do primeiro CD4 na data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
      case 2:
        cd.setName(
            "% de adultos HIV+ ≥ 15 anos que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
      case 3:
        cd.setName(
            "% de crianças HIV+ ≤ 14 anos que teve registo de pedido do primeiro CD4 na data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
      case 4:
        cd.setName(
            "% de crianças HIV+ ≤ 14 anos que teve conhecimento do resultado do primeiro CD4 dentro de 33 dias após a data da primeira consulta clínica/abertura da Ficha Mestra");
        break;
    }

    if (flag == 1 || flag == 2) {
      cd.addSearch(
          "AGE",
          EptsReportUtils.map(
              genericCohortQueries.getAgeOnFirstClinicalConsultation(15, null),
              "onOrAfter=${revisionEndDate-12m+1d},onOrBefore=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    } else if (flag == 3 || flag == 4) {
      cd.addSearch(
          "AGE",
          EptsReportUtils.map(
              genericCohortQueries.getAgeOnFirstClinicalConsultation(0, 14),
              "onOrAfter=${revisionEndDate-12m+1d},onOrBefore=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    }

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String inclusionPeriodMappings =
        "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},location=${location}";

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getFirstClinicalConsultationDuringInclusionPeriod(),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "requestCd4",
        EptsReportUtils.map(
            getRequestForCd4OnFirstClinicalConsultationDuringInclusionPeriod(),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "resultCd4",
        EptsReportUtils.map(
            getCd4ResultAfterFirstConsultationOnInclusionPeriod(),
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate-9m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "pregnantOnPeriod",
        EptsReportUtils.map(
            getMOHPregnantORBreastfeedingOnClinicalConsultation(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));
    cd.addSearch(
        "breastfeedingOnPeriod",
        EptsReportUtils.map(
            getMOHPregnantORBreastfeedingOnClinicalConsultation(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            inclusionPeriodMappings));

    if (flag == 1 || flag == 3) {
      cd.setCompositionString(
          "A AND requestCd4 AND NOT (C OR D OR E OR pregnantOnPeriod OR breastfeedingOnPeriod) AND AGE");
    } else if (flag == 2 || flag == 4) {
      cd.setCompositionString(
          "A AND resultCd4 AND NOT (C OR D OR E OR pregnantOnPeriod OR breastfeedingOnPeriod) AND AGE");
    }

    return cd;
  }

  /**
   * <b>M&Q Categoria 10 - Numerador</b>
   *
   * <ul>
   *   <li>10.1. % de adultos (15/+anos) que iniciaram o TARV dentro de 15 dias após diagnóstico - A
   *       AND F AND NOT (C OR D OR E) AND ADULTS
   *   <li>10.3. % de crianças (0-14 anos) HIV+ que iniciaram TARV dentro de 15 dias após
   *       diagnóstico - A AND F AND NOT (C OR D OR E) AND CHILDREN
   * </ul>
   *
   * @param flag indicator number
   * @return CohortDefinition
   */
  public CohortDefinition getMQ10NUM(int flag) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    switch (flag) {
      case 1:
        cd.setName(
            "% de adultos (15/+anos) que iniciaram o TARV dentro de 15 dias após diagnóstico");
        break;
      case 2:
        cd.setName(
            "% de crianças (0-14 anos) HIV+ que iniciaram TARV dentro de 15 dias após diagnóstico");
        break;
    }
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getMOHArtStartDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getPregnantConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            commonCohortQueries.getMOHPregnantORBreastfeeding(
                commonMetadata.getBreastfeeding().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getTransferredInPatients(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                hivMetadata.getPatientFoundYesConcept().getConceptId(),
                hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                hivMetadata.getArtStatus().getConceptId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            genericCohortQueries.getArtDateMinusDiagnosisDate(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            getChildrenCompositionMore19Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "ADULT",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    if (flag == 1) {
      cd.setCompositionString("A AND F AND NOT (C OR D OR E) AND ADULT");
    } else if (flag == 3) {
      cd.setCompositionString("A AND F AND NOT (C OR D OR E) AND CHILDREN");
    }
    return cd;
  }

  public CohortDefinition getPregnantOrBreastfeedingWomen() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregnant or breastfeeding women");
    cd.addParameter(new Parameter("revisionEndDate", "End revision Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition pregnant =
        genericCohortQueries.hasCodedObs(
            commonMetadata.getPregnantConcept(),
            BaseObsCohortDefinition.TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            Arrays.asList(hivMetadata.getYesConcept()));

    CohortDefinition breastfeeding =
        genericCohortQueries.hasCodedObs(
            commonMetadata.getBreastfeeding(),
            BaseObsCohortDefinition.TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            Arrays.asList(hivMetadata.getYesConcept()));

    CohortDefinition women = genderCohortQueries.femaleCohort();

    cd.addSearch(
        "P",
        EptsReportUtils.map(
            pregnant,
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate},locationList=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            breastfeeding,
            "onOrAfter=${revisionEndDate-14m},onOrBefore=${revisionEndDate},locationList=${location}"));
    cd.addSearch("F", EptsReportUtils.map(women, ""));
    cd.setCompositionString("(P OR B) AND F");
    return cd;
  }

  /**
   * Combined B13 for the CAT15 indicators Active patients excluding suspended, abandoned, dead and
   * transferout by end revision date
   *
   * <p>* @return CohortDefinition
   */
  public CohortDefinition getCombinedB13ForCat15Indicators() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("B13 for the MQ CAT 15 indicators ");
    cd.addParameter(new Parameter("revisionEndDate", "End revision Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "Active",
        EptsReportUtils.map(
            QualityImprovement2020Queries.getPatientsWithAtLeastAdrugPickup(
                hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
                hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
                hivMetadata.getArtDatePickupMasterCard().getConceptId(),
                hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId()),
            "endDate=${revisionEndDate},location{location}"));
    cd.addSearch(
        "suspended",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getPatientsWhoSuspendedTreatmentB6(false),
            "onOrBefore=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "abandoned",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7(),
            "date=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "dead",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getPatientsWhoDied(false),
            "onOrBefore=${revisionEndDate},locationList=${location}"));
    cd.addSearch(
        "TO",
        EptsReportUtils.map(
            resumoMensalCohortQueries.getPatientsTransferredOutB5(true),
            "onOrBefore=${revisionEndDate},location=${location}"));
    cd.setCompositionString("Active AND NOT (suspended OR abandoned OR dead OR TO)");

    return cd;
  }

  /**
   * <b>Select all patients with at least one of the following models registered in Ficha Clinica
   * (encounter type 6, encounter_datetime) during the revision period (start date and end date)</b>
   *
   * <ul>
   *   *
   *   <li>Last record of GAAC (concept id 23724) and the response is “Iniciar” * (value_coded,
   *       concept id 1256) or “Continua” (value_coded, concept id * 1257)
   *   <li>Last record of DT (concept id 23730) and the response is “Iniciar” * (value_coded,
   *       concept id 1256) or “Continua” (value_coded, concept id * 1257) or Type of dispensation
   *       (concept id 23793) value coded DT * (concept id 23888)
   *   <li>Last record of DS (concept id 23888) and the response is “Iniciar” * (value_coded,
   *       concept id 1256) or “Continua” (value_coded, concept id * 1257) or Type of dispensation
   *       (concept id 23793) value coded DT * (concept id 23720)
   *   <li>Last record of FR (concept id 23729) and the response is “ Iniciar” * (value_coded,
   *       concept id 1256) or “Continua” (value_coded, concept id * 1257)
   *   <li>Last record of DC (concept id 23731) and the response is “Iniciar” * (value_coded,
   *       concept id 1256) or “Continua” (value_coded, concept id * 1257)
   *   <li>Last record of Dispensing mode (concept id 165174) and the response is * “Dispensa
   *       Comunitária via APE (DCAPE)” (value_coded, concept id * 165179)
   *   <li>Last record of FARMAC (concept id 165177) and the response is * “Iniciar” (value_coded,
   *       concept id 1256) or “Continua” (value_coded, * concept id 1257)
   * </ul>
   *
   * @return
   */
  public CohortDefinition getMQMdsC() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("C: MDS para pacientes estáveis");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("23724", hivMetadata.getGaac().getConceptId());
    map.put("23730", hivMetadata.getQuarterlyDispensation().getConceptId());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId());
    map.put("23729", hivMetadata.getRapidFlow().getConceptId());
    map.put("23731", hivMetadata.getCommunityDispensation().getConceptId());
    map.put("165177", hivMetadata.getLastRecordOfFarmacConcept().getConceptId());
    map.put("165179", hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, "
            + "                          Max(e.encounter_datetime) AS encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON e.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND e.encounter_datetime BETWEEN "
            + "                              :startDate AND :endDate "
            + "                   GROUP  BY p.patient_id) last_consultation "
            + "               ON last_consultation.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND ( ( o.concept_id = ${23724} "
            + "               AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23730} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23888} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23729} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${165174} "
            + "                   AND (o.value_coded = ${165179})) "
            + "              OR ( o.concept_id = ${165177} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256})) "
            + "              OR ( o.concept_id = ${23731} "
            + "                   AND (o.value_coded = ${1257} OR o.value_coded = ${1256}))) "
            + "       AND e.encounter_datetime < last_consultation.encounter_datetime";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  public CohortDefinition getMQ15DenMDS() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Denominator 15 - Pacientes elegíveis a MDS");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15A = intensiveMonitoringCohortQueries.getMI15A();
    CohortDefinition Mq15B1 = intensiveMonitoringCohortQueries.getMI15B1();
    CohortDefinition E1 = intensiveMonitoringCohortQueries.getMI15E(30, 1);
    CohortDefinition E2 = intensiveMonitoringCohortQueries.getMI15E(60, 31);
    CohortDefinition E3 = intensiveMonitoringCohortQueries.getMI15E(90, 61);
    CohortDefinition Mq15C = getMQ15CPatientsMarkedAsPregnant();
    CohortDefinition Mq15D = getMQ15DPatientsMarkedAsBreastfeeding();
    CohortDefinition Mq15F = intensiveMonitoringCohortQueries.getMI15F();
    CohortDefinition Mq15G = intensiveMonitoringCohortQueries.getMI15G();
    CohortDefinition alreadyMds = getPatientsAlreadyEnrolledInTheMdc();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15A, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            Mq15B1, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "E1",
        EptsReportUtils.map(
            E1, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "E2",
        EptsReportUtils.map(
            E2, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "E3",
        EptsReportUtils.map(
            E3, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            Mq15C,
            "startDate=${revisionEndDate-14m+1d},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            Mq15D,
            "startDate=${revisionEndDate-14m+1d},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            Mq15F, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "G", EptsReportUtils.map(Mq15G, "endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "MDS",
        EptsReportUtils.map(
            alreadyMds,
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A AND B1 AND (E1 AND E2 AND E3) AND NOT (C OR D OR F OR G OR MDS)");

    return cd;
  }

  public CohortDefinition getMI15Den13() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Denominator 15 - Pacientes elegíveis a MDS");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15A = intensiveMonitoringCohortQueries.getMI15A();
    CohortDefinition Mq15B1 = intensiveMonitoringCohortQueries.getMI15B1();
    CohortDefinition E1 = intensiveMonitoringCohortQueries.getMI15E(30, 1);
    CohortDefinition E2 = intensiveMonitoringCohortQueries.getMI15E(60, 31);
    CohortDefinition E3 = intensiveMonitoringCohortQueries.getMI15E(90, 61);
    CohortDefinition Mq15C = getMQ15CPatientsMarkedAsPregnant();
    CohortDefinition Mq15D = getMQ15DPatientsMarkedAsBreastfeeding();
    CohortDefinition Mq15F = intensiveMonitoringCohortQueries.getMI15F();
    CohortDefinition Mq15G = intensiveMonitoringCohortQueries.getMI15G();
    CohortDefinition alreadyMds = getPatientsAlreadyEnrolledInTheMdc();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15A, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "B1",
        EptsReportUtils.map(
            Mq15B1, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "E1",
        EptsReportUtils.map(
            E1, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "E2",
        EptsReportUtils.map(
            E2, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "E3",
        EptsReportUtils.map(
            E3, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            Mq15C,
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            Mq15D,
            "startDate=${revisionEndDate-19m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            Mq15F, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "G", EptsReportUtils.map(Mq15G, "endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "MDS",
        EptsReportUtils.map(
            alreadyMds, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A AND B1 AND (E1 AND E2 AND E3)  AND NOT (C OR D OR F OR G OR MDS)");

    return cd;
  }

  public CohortDefinition getMQ15NumeratorMDS() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator MQ 15 - Pacientes elegíveis a MDS");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15DenMDS = getMQ15DenMDS();
    CohortDefinition MqK = intensiveMonitoringCohortQueries.getMI15K();

    List<Integer> mdsConcepts =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states = Arrays.asList(hivMetadata.getStartDrugs().getConceptId());

    CohortDefinition mds =
        getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36(mdsConcepts, states);

    cd.addSearch(
        "MQ15DenMDS",
        EptsReportUtils.map(
            Mq15DenMDS,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "MDS",
        EptsReportUtils.map(
            mds,
            "startDate=${revisionEndDate-12m+1d},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("MQ15DenMDS AND MDS");
    return cd;
  }

  public CohortDefinition getMI15Nume13() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator MQ 15 - Pacientes elegíveis a MDS");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mi15Den13 = getMI15Den13();

    List<Integer> mdsConcepts =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states = Arrays.asList(hivMetadata.getStartDrugs().getConceptId());

    CohortDefinition mds =
        getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36(mdsConcepts, states);

    cd.addSearch(
        "MQ15Den13",
        EptsReportUtils.map(
            Mi15Den13,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "MDS",
        EptsReportUtils.map(
            mds, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("MQ15Den13 AND MDS");
    return cd;
  }

  public CohortDefinition getMQ15MdsDen14() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.14 - % de inscritos em MDS que receberam CV");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15A = intensiveMonitoringCohortQueries.getMI15A();
    CohortDefinition alreadyMdc = getPatientsAlreadyEnrolledInTheMdc();

    CohortDefinition Mq15H = intensiveMonitoringCohortQueries.getMI15H();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15A, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "MDC",
        EptsReportUtils.map(
            alreadyMdc, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "H",
        EptsReportUtils.map(
            Mq15H, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A AND MDC AND H");
    return cd;
  }

  public CohortDefinition getMQ15MdsNum14() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator 15.14: # de pacientes inscritos em MDS ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15DenMds14 = getMQ15MdsDen14();
    CohortDefinition hadFilaAfterClinical =
        getPatientsWhoHadPickupOnFilaAfterMostRecentVlOnFichaClinica();

    cd.addSearch(
        "Mq15DenMds14",
        EptsReportUtils.map(
            Mq15DenMds14,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "FAC",
        EptsReportUtils.map(
            hadFilaAfterClinical,
            "startDate=${startDate},endDate=${revisionEndDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("Mq15DenMds14 AND FAC");

    return cd;
  }

  public CohortDefinition getMI15MdsNum14() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator 15.14: # de pacientes inscritos em MDS ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    List<Integer> dispensationTypes =
        Arrays.asList(
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
            hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getSemiannualDispensation().getConceptId());

    List<Integer> states =
        Collections.singletonList(hivMetadata.getCompletedConcept().getConceptId());

    CohortDefinition Mq15DenMds14 = getMQ15MdsDen14();

    CohortDefinition MdsFimNum14 =
        getPatientsWithMdcOnMostRecentClinicalFormWithFollowingDispensationTypesAndState(
            dispensationTypes, states);

    CohortDefinition hadFilaAfterClinical =
        getPatientsWhoHadPickupOnFilaAfterMostRecentVlOnFichaClinica();

    cd.addSearch(
        "Mq15DenMds14",
        EptsReportUtils.map(
            Mq15DenMds14,
            "startDate=${startDate},revisionEndDate=${revisionEndDate-1m},location=${location}"));

    cd.addSearch(
        "MdsFimNum14",
        EptsReportUtils.map(
            MdsFimNum14, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "FAC",
        EptsReportUtils.map(
            hadFilaAfterClinical,
            "startDate=${startDate},endDate=${endDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("Mq15DenMds14 AND (MdsFimNum14 OR FAC)");

    return cd;
  }

  public CohortDefinition getMQMI15DEN15WithoutExclusions() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.15 % de pacientes inscritos em MDS em TARV há mais de 21 meses ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15A = intensiveMonitoringCohortQueries.getMI15A();
    CohortDefinition alreadyMdc = getPatientsAlreadyEnrolledInTheMdc();
    CohortDefinition Mq15B2 = intensiveMonitoringCohortQueries.getMI15B2(24);

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15A, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "MDC",
        EptsReportUtils.map(
            alreadyMdc, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "B2",
        EptsReportUtils.map(
            Mq15B2, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A AND MDC AND B2");
    return cd;
  }

  public CohortDefinition getMQ15Den15() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.15 % de pacientes inscritos em MDS em TARV há mais de 21 meses MQ ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15withoutExclusions = getMQMI15DEN15WithoutExclusions();
    CohortDefinition Mq15P = intensiveMonitoringCohortQueries.getMI15P();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15withoutExclusions,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "P",
        EptsReportUtils.map(
            Mq15P, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A NOT P");
    return cd;
  }

  public CohortDefinition getMI15Den15() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.15 % de pacientes inscritos em MDS em TARV há mais de 21 meses MQ ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15withoutExclusions = getMQMI15DEN15WithoutExclusions();
    CohortDefinition Mq15P =
        intensiveMonitoringCohortQueries.getPatientsWhoHadLabInvestigationsRequest();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            Mq15withoutExclusions,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "IR",
        EptsReportUtils.map(
            Mq15P, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("A NOT IR");
    return cd;
  }

  private CohortDefinition getAgeOnObsDatetime(Integer minAge, Integer maxAge) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(AgeOnObsDatetimeCalculation.class).get(0));
    cd.setName("Calculate Age based on ObsDatetime");
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("minAgeOnObsDatetime", minAge);
    cd.addCalculationParameter("maxAgeOnObsDatetime", maxAge);
    return cd;
  }

  public CohortDefinition getMQ15MdsNum15() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.15 % de pacientes inscritos em MDS em TARV há mais de 21 meses ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mq15MdsDen15 = getMQ15Den15();
    CohortDefinition Mq15I = intensiveMonitoringCohortQueries.getMI15I(24, 10, 20);

    cd.addSearch(
        "Mq15MdsDen15",
        EptsReportUtils.map(
            Mq15MdsDen15,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "Mq15I",
        EptsReportUtils.map(
            Mq15I, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("Mq15MdsDen15 AND Mq15I");

    return cd;
  }

  public CohortDefinition getMI15Num15() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("15.15 % de pacientes inscritos em MDS em TARV há mais de 21 meses ");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition Mi15Den = getMI15Den15();
    CohortDefinition Mq15I = intensiveMonitoringCohortQueries.getMI15I(20, 10, 20);

    cd.addSearch(
        "MI15DEN15",
        EptsReportUtils.map(
            Mi15Den,
            "startDate=${startDate},revisionEndDate=${revisionEndDate},location=${location}"));
    cd.addSearch(
        "Mq15I",
        EptsReportUtils.map(
            Mq15I, "startDate=${startDate},endDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("MI15DEN15 AND Mq15I");

    return cd;
  }

  public CohortDefinition getMQDen15Dot16() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "15.16. % de utentes inscritos em MDS (para pacientes estáveis) com supressão viral");

    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addSearch(
        "A",
        EptsReportUtils.map(
            txPvls.getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "MDC",
        EptsReportUtils.map(
            getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36BasedOnLastVl(
                Arrays.asList(
                    hivMetadata.getGaac().getConceptId(),
                    hivMetadata.getQuarterlyDispensation().getConceptId(),
                    hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
                    hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
                    hivMetadata.getRapidFlow().getConceptId(),
                    hivMetadata.getSemiannualDispensation().getConceptId()),
                Arrays.asList(
                    hivMetadata.getStartDrugs().getConceptId(),
                    hivMetadata.getContinueRegimenConcept().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("A AND MDC");
    return cd;
  }

  public CohortDefinition getMQNum15Dot16() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "15.16. % de utentes inscritos em MDS (para pacientes estáveis) com supressão viral");

    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txPvls.getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "MDC",
        EptsReportUtils.map(
            getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36BasedOnLastVl(
                Arrays.asList(
                    hivMetadata.getGaac().getConceptId(),
                    hivMetadata.getQuarterlyDispensation().getConceptId(),
                    hivMetadata.getDispensaComunitariaViaApeConcept().getConceptId(),
                    hivMetadata.getDescentralizedArvDispensationConcept().getConceptId(),
                    hivMetadata.getRapidFlow().getConceptId(),
                    hivMetadata.getSemiannualDispensation().getConceptId()),
                Arrays.asList(
                    hivMetadata.getStartDrugs().getConceptId(),
                    hivMetadata.getContinueRegimenConcept().getConceptId())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("B AND MDC");
    return cd;
  }

  /**
   * <b>C - All female patients registered as “Pregnant” on Ficha Clinica during the revision period
   * (startDateInclusion = endDateRevision - 14 months and endDateRevision):</b>
   *
   * <ul>
   *   <li>all patients registered in Ficha Clínica (encounter type=6) with “Gestante”(concept_id
   *       1982) value_coded equal to “Yes” (concept_id 1065) and sex=Female and encounter_datetime
   *       >= startDateInclusion (endDateRevision - 14 months) and encounter_datetime <=
   *       endDateRevision. <i>NOTE: IF the patient has both states pregnant and breastfeeding, the
   *       system will consider the most recent registry. If the patient has both states on the same
   *       day, the system will consider the patient as pregnant.</i>
   * </ul>
   */
  public CohortDefinition getMQ15CPatientsMarkedAsPregnant() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All female patients registered as pregnant but not breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT pregnant.patient_id "
            + "FROM   (SELECT p.patient_id, MAX(e.encounter_datetime) AS pregnancy_date "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "               AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "               AND e.location_id = :location "
            + "               AND e.voided = 0 "
            + "               AND o.concept_id = ${1982} "
            + "               AND o.value_coded = ${1065} "
            + "               AND o.voided = 0 "
            + "               AND ps.gender = 'F' "
            + "               AND ps.voided = 0 "
            + "        GROUP  BY p.patient_id) pregnant "
            + "       LEFT JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                         INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "                  WHERE  e.encounter_type = ${6} "
            + "                         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                         AND e.location_id = :location "
            + "                         AND e.voided = 0 "
            + "                         AND o.concept_id = ${6332} "
            + "                         AND o.value_coded = ${1065} "
            + "                         AND o.voided = 0 "
            + "                         AND ps.gender = 'F' "
            + "                         AND ps.voided = 0 "
            + "                  GROUP  BY p.patient_id) AS breastfeeding "
            + "              ON breastfeeding.patient_id = pregnant.patient_id "
            + "WHERE  pregnant.pregnancy_date >= breastfeeding.breastfeed_date OR breastfeeding.breastfeed_date IS NULL "
            + "GROUP  BY pregnant.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>D - All female patients registered as “Breastfeeding” on Ficha Clinica during the revision
   * period (startDateInclusion = endDateRevision - 14 months and endDateRevision):</b>
   *
   * <ul>
   *   <li>all patients registered in Ficha Clínica (encounter type=6) with “Lactante”(concept_id
   *       6332) value_coded equal to “Yes” (concept_id 1065) and sex=Female and encounter_datetime
   *       >= startDateInclusion (endDateRevision - 14 months) and encounter_datetime <=
   *       endDateRevision <i>NOTE: IF the patient has both states pregnant and breastfeeding, the
   *       system will consider the most recent registry. If the patient has both states on the same
   *       day, the system will consider the patient as pregnant.</i>
   * </ul>
   */
  public CohortDefinition getMQ15DPatientsMarkedAsBreastfeeding() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All female patients registered as Breastfeeding but not pregnant");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT breastfeeding.patient_id "
            + "FROM  (SELECT p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "       FROM   patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "              INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "       WHERE  e.encounter_type = ${6} "
            + "              AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "              AND e.location_id = :location "
            + "              AND e.voided = 0 "
            + "              AND o.concept_id = ${6332} "
            + "              AND o.value_coded = ${1065} "
            + "              AND o.voided = 0 "
            + "              AND ps.gender = 'F' "
            + "              AND ps.voided = 0 "
            + "       GROUP  BY p.patient_id) breastfeeding "
            + "      LEFT JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) pregnancy_date "
            + "                 FROM   patient p "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "                 WHERE  e.encounter_type = ${6} "
            + "                        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                        AND e.location_id = :location "
            + "                        AND e.voided = 0 "
            + "                        AND o.concept_id = ${1982} "
            + "                        AND o.value_coded = ${1065} "
            + "                        AND o.voided = 0 "
            + "                        AND ps.gender = 'F' "
            + "                        AND ps.voided = 0 "
            + "                 GROUP  BY p.patient_id) pregnant "
            + "             ON pregnant.patient_id = breastfeeding.patient_id "
            + "WHERE  breastfeeding.breastfeed_date > pregnant.pregnancy_date "
            + "        OR pregnant.pregnancy_date IS NULL "
            + "GROUP  BY breastfeeding.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <b>Pacientes que Iniciaram TPT - Isoniazida durante período de inclusão</b>
   *   <li>O sistema irá identificar pacientes que iniciaram TPT – Isoniazida durante o período de
   *       inclusão seleccionando os pacientes:
   *       <p>com registo de “Última Profilaxia TPT” = “INH” e “Última Profilaxia TPT (Data
   *       Início)”, no formulário “Ficha de Resumo”, durante o período de inclusão (“Última
   *       Profilazia TPT (Data Início)” >= “Data Início Inclusão” e <= “Data Fim Inclusão”). Em
   *       caso de existência de mais que uma Ficha Resumo com registo do “Última Profilaxia (Data
   *       Início)”, deve-se considerar o último registo durante o período de inclusão ou.
   *   <li>Nota: sendo a “Data Início TPT - Isonazida” do paciente a data mais recente entre os
   *       critérios acima listados.
   * </ul>
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with B4_1 criteria
   */
  public CohortDefinition getB4And2() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B4_2");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        "SELECT  final.patient_id "
            + "FROM "
            + "( "
            + "   SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "   FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "     AND e.voided = 0 "
            + "     AND o.voided = 0 "
            + "     AND o2.voided = 0 "
            + "     AND e.location_id = :location "
            + "     AND e.encounter_type = ${53} "
            + "     AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "     AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "     AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "   GROUP BY p.patient_id "
            + ") AS  final";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <b>Pacientes que Iniciaram TPT - Isoniazida durante período de inclusão</b>
   *   <li>O sistema irá identificar pacientes que iniciaram TPT – Isoniazida durante o período de
   *       inclusão seleccionando os pacientes:
   *       <p>com o registo de “Profilaxia TPT”=”INH” e “Estado da Profilaxia” =“Inicio” numa
   *       consulta clínica (Ficha Clínica) ocorrida durante o período de inclusão (“Data de
   *       Consulta”>= “Data Início Inclusão” e <= “Data Fim Inclusão”). Em caso de existência de
   *       mais que uma Ficha Clínica com registo do “Início”, deve-se considerar o último registo
   *       durante o período de inclusão.
   *   <li>Nota: sendo a “Data Início TPT - Isonazida” do paciente a data mais recente entre os
   *       critérios acima listados.
   * </ul>
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with B4_2 criteria
   */
  public CohortDefinition getB4And1() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B4_1");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        ""
            + "SELECT  final.patient_id "
            + "FROM "
            + "( "
            + "   SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "   FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "     AND e.voided = 0 "
            + "     AND o.voided = 0 "
            + "     AND o2.voided = 0 "
            + "     AND e.location_id = :location "
            + "     AND e.encounter_type = ${6} "
            + "     AND ( (o.concept_id = ${23985} "
            + "     AND o.value_coded = ${656}) "
            + "     AND   (o2.concept_id = ${165308} "
            + "     AND o2.value_coded = ${1256} "
            + "     AND o2.obs_datetime between :startDate AND :endDate) ) "
            + "   GROUP BY p.patient_id "
            + ") AS  final";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <b>Pacientes que Iniciaram TPT – 3HP durante período de inclusão</b>
   *   <li>O sistema irá identificar pacientes que iniciaram TPT – 3HP durante o período de inclusão
   *       seleccionando os pacientes:
   *       <p>com registo de “Última Profilaxia TPT” = “3HP” e “Última Profilaxia TPT (Data
   *       Início)”, no formulário “Ficha de Resumo”, durante o período de inclusão (“Última
   *       Profilazia TPT (Data Início)” >= “Data Início Inclusão” e <= “Data Fim Inclusão”). Em
   *       caso de existência de mais que uma Ficha Resumo com registo do “Última Profilaxia (Data
   *       Início)”, deve-se considerar o último registo durante o período de inclusão ou
   *   <li>Nota: sendo a “Data Início TPT – 3HP” do paciente a data mais recente entre os critérios
   *       acima listados.
   * </ul>
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with B4_3 criteria
   */
  public CohortDefinition getB5And2() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B5_2");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    String query =
        ""
            + "SELECT  final.patient_id "
            + "FROM "
            + "( "
            + "   SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "   FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "     AND e.voided = 0 "
            + "     AND o.voided = 0 "
            + "     AND o2.voided = 0 "
            + "     AND e.location_id = :location "
            + "     AND e.encounter_type = ${53} "
            + "     AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "     AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "     AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) ) "
            + "   GROUP BY p.patient_id "
            + ") AS  final";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <b>Pacientes que Iniciaram TPT – 3HP durante período de inclusão</b>
   *   <li>O sistema irá identificar pacientes que iniciaram TPT – 3HP durante o período de inclusão
   *       seleccionando os pacientes:
   *       <p>com o registo de “Profilaxia TPT”= ”3HP” e “Estado da Profilaxia” =“Inicio” numa
   *       consulta clínica (Ficha Clínica) ocorrida durante o período de inclusão (“Data de
   *       Consulta”>= “Data Início Inclusão” e <= “Data Fim Inclusão”). Em caso de existência de
   *       mais que uma Ficha Clínica com registo do “Início”, deve-se considerar o último registo
   *       durante o período de inclusão.
   *   <li>Nota: sendo a “Data Início TPT – 3HP” do paciente a data mais recente entre os critérios
   *       acima listados.
   * </ul>
   *
   * @return CohortDefinition
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients with B4_4 criteria
   */
  public CohortDefinition getB5And1() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("B5_1");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        ""
            + "SELECT  final.patient_id "
            + "FROM "
            + "( "
            + "   SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter "
            + "   FROM patient p "
            + "         INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id "
            + "   WHERE p.voided = 0 "
            + "     AND e.voided = 0 "
            + "     AND o.voided = 0 "
            + "     AND o2.voided = 0 "
            + "     AND e.location_id = :location "
            + "     AND e.encounter_type = ${6} "
            + "     AND ( (o.concept_id = ${23985} "
            + "     AND o.value_coded = ${23954}) "
            + "     AND   (o2.concept_id = ${165308} "
            + "     AND o2.value_coded = ${1256} "
            + "     AND o2.obs_datetime between :startDate AND :endDate) ) "
            + "   GROUP BY p.patient_id "
            + ") AS  final";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(query));
    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>G_New: Filter all patients with the most recent date as “TPT end Date” between the
   *       following::
   *       <ul>
   *         <li>
   *             <p>"Profilaxia TPT"(concept id 23985) value coded INH(concept id 656) and Estado da
   *             Profilaxia (concept id 165308) value coded FIM(concept id 1267) durante o período
   *             de revisao (obs_datetime >= startDateRevision and <= endDateRevision) Nota: Em caso
   *             de existência de mais que uma Ficha Clínica com registo do “FIM”, deve-se
   *             considerar o último registo durante o período de revisao.
   *         <li>
   *             <p>" Ultima profilaxia TPT"(concept id 23985) value coded INH(concept id 656) and
   *             value_datetime(concept id 6129) during the revision period (value_datetime >=
   *             startDateRevision and <= endDateRevision) Nota: Em caso de existência de mais que
   *             uma Ficha Resumo com registo do “Última ProfilaxiaIsoniazida (Data FIM)”, deve-se
   *             considerar o último registo durante o período de revisao.
   *             <p>and “TPT Start Date” (the most recent date from B4_1 and B4_2) minus “TPT End
   *             Date” is between 170 days and 297 days
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getGNew() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("G new  INH");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        ""
            + "SELECT p.patient_id  "
            + "FROM patient p  "
            + "    INNER JOIN (  "
            + "                SELECT tpt_end.patient_id, MAX(tpt_end.last_encounter) AS tpt_end_date  "
            + "                FROM (  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.encounter_type = ${6}  "
            + "                           AND e.location_id = :location  "
            + "                           AND ( ( o.concept_id = ${23985}  "
            + "                           AND     o.value_coded = ${656} )  "
            + "                           AND   ( o2.concept_id = ${165308} "
            + "                           AND     o2.value_coded = ${1267} "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :revisionEndDate ) ) "
            + "                         GROUP BY p.patient_id  "
            + "                         UNION  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${53}  "
            + "                           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} )  "
            + "                           AND   ( o2.concept_id = ${165308}  AND o2.value_coded = ${1267} "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :revisionEndDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                     ) AS tpt_end  "
            + "                GROUP BY  tpt_end.patient_id  "
            + "            ) AS tpt_fim ON tpt_fim.patient_id = p.patient_id  "
            + "    INNER JOIN (  "
            + "                SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date  "
            + "                FROM (  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${6}  "
            + "                           AND ( ( o.concept_id = ${23985}  "
            + "                           AND     o.value_coded = ${656} )  "
            + "                           AND   ( o2.concept_id = ${165308}  "
            + "                           AND     o2.value_coded = ${1256}   "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                         UNION  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${53}  "
            + "                           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${656} )  "
            + "                           AND   ( o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                     ) AS tpt_start  "
            + "                GROUP BY tpt_start.patient_id  "
            + "            ) AS tpt_inicio ON tpt_inicio.patient_id = p.patient_id  "
            + "WHERE p.voided = 0  "
            + "    AND TIMESTAMPDIFF(DAY, tpt_inicio.tpt_start_date,tpt_fim.tpt_end_date) BETWEEN  170 AND 297 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>L: Filter all patients with the most recent date as “TPT end Date” between the
   *       following::
   *       <ul>
   *         <li>
   *             <p>the most recent clinical consultation(encounter type 6)during the revision
   *             period(obs_datetime >= startDateRevision and <= endDateRevision) with “Ultima
   *             Profilaxia TPT (concept_id 23985) = 3HP (concept = 23954) and “Data Fim”
   *             (concept_id 165308 value 1267)
   *         <li>
   *             <p>the most recent “Última Profilaxia = 3HP (concept 23985 value 23954) and value
   *             datetime FIM (concept id 6129) registered in Ficha Resumo (encounter type 53)
   *             occurred during the revision period (value_datetime >= startDateRevision and <=
   *             endDateRevision)
   *             <p>and “TPT Start Date” (the most recent date from B5_1 and B5_2) minus “TPT End
   *             Date” is between 80 days and 190 days
   *       </ul>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getGNew3HP() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("G new 3HP");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        ""
            + "SELECT p.patient_id  "
            + "FROM patient p  "
            + "    INNER JOIN (  "
            + "                SELECT tpt_end.patient_id, MAX(tpt_end.last_encounter) AS tpt_end_date  "
            + "                FROM (  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${6}  "
            + "                           AND ( ( o.concept_id = ${23985}  "
            + "                           AND     o.value_coded = ${23954})  "
            + "                           AND   ( o2.concept_id = ${165308}  "
            + "                           AND     o2.value_coded = ${1267}  "
            + "                           AND o2.obs_datetime BETWEEN :startDate AND :revisionEndDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                         UNION  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${53}  "
            + "                           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} ) "
            + "                           AND   ( o2.concept_id = ${165308}  AND o2.value_coded = ${1267} "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :revisionEndDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                     ) AS tpt_end  "
            + "                GROUP BY  tpt_end.patient_id  "
            + "            ) AS tpt_fim ON tpt_fim.patient_id = p.patient_id  "
            + "    INNER JOIN (  "
            + "                SELECT tpt_start.patient_id, MAX(tpt_start.last_encounter) tpt_start_date  "
            + "                FROM (  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${6}  "
            + "                           AND ( ( o.concept_id = ${23985}  "
            + "                           AND     o.value_coded = ${23954})  "
            + "                           AND   ( o2.concept_id = ${165308}  "
            + "                           AND     o2.value_coded = ${1256}  "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                         UNION  "
            + "                         SELECT p.patient_id, MAX(o2.obs_datetime) last_encounter  "
            + "                         FROM patient p  "
            + "                                  INNER JOIN encounter e ON p.patient_id = e.patient_id  "
            + "                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "                                  INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id  "
            + "                         WHERE p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0  "
            + "                           AND e.location_id = :location  "
            + "                           AND e.encounter_type = ${53}  "
            + "                           AND ( ( o.concept_id = ${23985} AND o.value_coded = ${23954} )  "
            + "                           AND   ( o2.concept_id = ${165308}  AND o2.value_coded = ${1256}  "
            + "                           AND     o2.obs_datetime BETWEEN :startDate AND :endDate ) )  "
            + "                         GROUP BY p.patient_id  "
            + "                     ) AS tpt_start  "
            + "                GROUP BY tpt_start.patient_id  "
            + "            ) AS tpt_inicio ON tpt_inicio.patient_id = p.patient_id  "
            + "WHERE p.voided = 0  "
            + "    AND TIMESTAMPDIFF(DAY, tpt_inicio.tpt_start_date,tpt_fim.tpt_end_date) BETWEEN  80 AND 190 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * J - Select all patients from Ficha Resumo (encounter type 53) with “HIV Carga viral”(Concept id
   * 856, value_numeric not null) and obs_datetime between “Patient ART Start Date” (the oldest date
   * from A)+6months and “Patient ART Start Date” (the oldest date from query A)+9months.
   *
   * @return CohortDefinition
   */
  private CohortDefinition getMQC13P3NUM_J() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients with HIV Carga Viral - J");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getHistoricalDrugStartDateConcept().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p "
            + "       INNER JOIN encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       INNER JOIN obs o  "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       INNER JOIN(SELECT patient_id, "
            + "                         art_date  "
            + "                  FROM   (SELECT p.patient_id, "
            + "                                 Min(value_datetime) art_date  "
            + "                          FROM   patient p "
            + "                                 INNER JOIN encounter e  "
            + "                                         ON p.patient_id = e.patient_id  "
            + "                                 INNER JOIN obs o  "
            + "                                         ON e.encounter_id = o.encounter_id  "
            + "                          WHERE  p.voided = 0  "
            + "                                 AND e.voided = 0  "
            + "                                 AND o.voided = 0  "
            + "                                 AND e.encounter_type = ${53} "
            + "                                 AND o.concept_id = ${1190} "
            + "                                 AND o.value_datetime IS NOT NULL  "
            + "                                 AND o.value_datetime <= :endDate  "
            + "                                 AND e.location_id = :location "
            + "                          GROUP  BY p.patient_id) union_tbl  "
            + "                  WHERE  union_tbl.art_date BETWEEN  "
            + "                         :startDate AND :endDate) AS "
            + "                                 tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0  "
            + "       AND e.voided = 0  "
            + "       AND o.voided = 0  "
            + "       AND e.encounter_type = ${53} "
            + "       AND e.location_id = :location "
            + "       AND o.concept_id = ${856}  "
            + "       AND o.value_numeric IS NOT NULL "
            + "       AND o.obs_datetime BETWEEN Date_add(tabela.art_date, INTERVAL 6 MONTH)  "
            + "                                  AND  "
            + "                                      Date_add(tabela.art_date, INTERVAL 9 MONTH "
            + "                                      )  "
            + " GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * K - Select all patients from Ficha Resumo (encounter type 53) with “HIV Carga viral”(Concept id
   * 856, value_numeric not null) and obs_datetimebetween “ALTERNATIVA A LINHA - 1a LINHA Date” (the
   * most recent date from B1)+6months and “ALTERNATIVA A LINHA - 1a LINHA Date” (the most recent
   * date from B1)+9months.
   *
   * @return CohortDefinition
   */
  private CohortDefinition getMQC13P3NUM_K() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients with HIV Carga Viral - K");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21190", commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());

    String query =
        " SELECT   "
            + "    p.patient_id "
            + " FROM "
            + "    patient p  "
            + "        INNER JOIN "
            + "    encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN "
            + "    obs o ON o.encounter_id = e.encounter_id   "
            + "    INNER JOIN(SELECT patient_id, regime_date  "
            + "            FROM   (SELECT p.patient_id,   "
            + "                           Max(o.obs_datetime) AS regime_date  "
            + "                    FROM   patient p   "
            + "                           JOIN encounter e  "
            + "                             ON e.patient_id = p.patient_id  "
            + "                           JOIN obs o  "
            + "                             ON o.encounter_id = e.encounter_id  "
            + "                           JOIN obs o2   "
            + "                             ON o2.encounter_id = e.encounter_id   "
            + "                    WHERE  e.encounter_type = ${53}   "
            + "                           AND o.concept_id = ${21190}  "
            + "                           AND o.value_coded IS NOT NULL   "
            + "                           AND e.location_id = :location   "
            + "                           AND (   "
            + "                                  (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})  "
            + "                                   OR  "
            + "                                  (o2.concept_id = ${1792} AND o2.value_coded IS NULL)  "
            + "                                   OR  "
            + "                                  (  "
            + "                                   NOT EXISTS (  "
            + "                                           SELECT * FROM obs oo  "
            + "                                           WHERE oo.voided = 0   "
            + "                                           AND oo.encounter_id = e.encounter_id  "
            + "                                           AND oo.concept_id = ${1792}  "
            + "                                       )   "
            + "                                 )   "
            + "                                )  "
            + "                           AND e.voided = 0  "
            + "                           AND p.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND o2.voided = 0 "
            + "                           AND o.obs_datetime BETWEEN :startDate AND :endDate  "
            + "                    GROUP  BY p.patient_id) bI1  "
            + "            WHERE  bI1.patient_id NOT IN (SELECT p.patient_id  "
            + "                                          FROM   patient p   "
            + "                                                 JOIN encounter e  "
            + "                                                   ON e.patient_id = p.patient_id  "
            + "                                                 JOIN obs o  "
            + "                                                   ON o.encounter_id = e.encounter_id  "
            + "                                          WHERE  e.encounter_type = ${6}  "
            + "                                                 AND o.concept_id = ${21151} AND o.value_coded <> ${21150}   "
            + "                                                 AND e.location_id = :location   "
            + "                                                 AND e.voided = 0  "
            + "                                                 AND p.voided = 0  "
            + "                                                 AND e.encounter_datetime > bI1.regime_date  "
            + "                                                 AND e.encounter_datetime <= :revisionEndDate))  "
            + "                    AS B1 ON B1.patient_id = p.patient_id  "
            + "                    WHERE  "
            + "                    p.voided = 0   "
            + "                        AND e.voided = 0   "
            + "                        AND o.voided = 0   "
            + "                        AND e.encounter_type = ${53}  "
            + "                        AND e.location_id = :location  "
            + "                        AND o.concept_id = ${856} "
            + "                        AND o.value_numeric IS NOT NULL  "
            + "                        AND o.obs_datetime BETWEEN DATE_ADD(B1.regime_date, INTERVAL 6 MONTH)  "
            + "                        AND DATE_ADD(B1.regime_date, INTERVAL 9 MONTH) "
            + "                        GROUP BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * L - Select all patients from Ficha Resumo (encounter type 53) with “HIV Carga viral”(Concept id
   * 856, value_numeric not null) and obs_datetime between “Segunda Linha Date” (the most recent
   * date from B2New)+6months and “Segunda Linha Date” (the most recent date from B2New)+9months.
   *
   * @return CohortDefinition
   */
  private CohortDefinition getMQC13P3NUM_L() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients with HIV Carga Viral - L");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());

    String query =
        " SELECT   "
            + "    p.patient_id "
            + " FROM "
            + "    patient p  "
            + "        INNER JOIN "
            + "    encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN "
            + "    obs o ON o.encounter_id = e.encounter_id   "
            + "    INNER JOIN(SELECT p.patient_id, Max(e.encounter_datetime) AS last_consultation "
            + "              FROM patient p   "
            + "                   INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "                   INNER JOIN obs o ON o.encounter_id = e.encounter_id   "
            + "                   INNER JOIN obs o2 ON o2.encounter_id = e.encounter_id   "
            + "              WHERE e.voided = 0 AND p.voided = 0  "
            + "                AND o.voided = 0   "
            + "                AND o2.voided = 0  "
            + "                AND e.encounter_type = ${53}  "
            + "                AND e.location_id = :location  "
            + "                AND (o.concept_id = ${21187} AND o.value_coded IS NOT NULL)   "
            + "                           AND (   "
            + "                                  (o2.concept_id = ${1792} AND o2.value_coded <> ${1982})  "
            + "                                   OR  "
            + "                                  (o2.concept_id = ${1792} AND o2.value_coded IS NULL)  "
            + "                                   OR  "
            + "                                  (  "
            + "                                   NOT EXISTS (  "
            + "                                           SELECT * FROM obs oo  "
            + "                                           WHERE oo.voided = 0   "
            + "                                           AND oo.encounter_id = e.encounter_id  "
            + "                                           AND oo.concept_id = ${1792}  "
            + "                                       )   "
            + "                                 )   "
            + "                                )  "
            + "                AND o.obs_datetime >= :startDate   "
            + "                AND o.obs_datetime <= :endDate   "
            + "              GROUP BY p.patient_id)   "
            + "                    AS B2NEW ON B2NEW.patient_id = p.patient_id  "
            + "                    WHERE  "
            + "                    p.voided = 0   "
            + "                        AND e.voided = 0   "
            + "                        AND o.voided = 0   "
            + "                        AND e.encounter_type = ${53}  "
            + "                        AND e.location_id = :location  "
            + "                        AND o.concept_id = ${856} "
            + "                        AND o.value_numeric IS NOT NULL  "
            + "                        AND o.obs_datetime BETWEEN DATE_ADD(B2NEW.last_consultation, INTERVAL 6 MONTH)   "
            + "                        AND DATE_ADD(B2NEW.last_consultation, INTERVAL 9 MONTH)  "
            + "                        GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * B2E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who have
   * “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “SEGUNDA LINHA”(Concept
   * id 21150) and obs_datetime > “REGIME ARV SEGUNDA LINHA” (from B2New) and <= “Last Clinical
   * Consultation” (last encounter_datetime from B1)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getMQC13DEN_B2E() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients with LINHA TERAPEUTICA DIFFERENT THAN SEGUNDA LINHA ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21148", hivMetadata.getSecondLineConcept().getConceptId());

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e "
            + "ON         e.patient_id = p.patient_id "
            + "INNER JOIN obs o "
            + "ON         o.encounter_id = e.encounter_id "
            + "INNER JOIN "
            + "           ( "
            + "                      SELECT     p.patient_id , "
            + "                                 o.obs_datetime           AS linha_terapeutica, "
            + "                                 last_clinical.last_visit AS last_consultation "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o "
            + "                      ON         o.encounter_id = e.encounter_id "
            + "                      INNER JOIN "
            + "                                 ( "
            + "                                            SELECT     p.patient_id, "
            + "                                                       Max(e.encounter_datetime) last_visit "
            + "                                            FROM       patient p "
            + "                                            INNER JOIN encounter e "
            + "                                            ON         e.patient_id = p.patient_id "
            + "                                            WHERE      p.voided = 0 "
            + "                                            AND        e.voided = 0 "
            + "                                            AND        e.encounter_type = ${6} "
            + "                                            AND        e.location_id = :location "
            + "                                            AND        e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
            + "                                            GROUP BY   p.patient_id) AS last_clinical "
            + "                      ON         last_clinical.patient_id = p.patient_id "
            + "                      WHERE      e.voided = 0 "
            + "                      AND        p.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      AND        e.encounter_type = ${53} "
            + "                      AND        e.location_id = :location "
            + "                      AND        o.concept_id = ${21187} "
            + "                      AND        o.value_coded IS NOT NULL "
            + "                      AND        o.obs_datetime >= :startDate "
            + "                      AND        o.obs_datetime <= :revisionEndDate "
            + "                      AND        timestampdiff(month, o.obs_datetime, last_clinical.last_visit) >= 6) second_line "
            + "ON         second_line.patient_id = p.patient_id "
            + "WHERE      e.voided = 0 "
            + "AND        p.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.encounter_type = ${6} "
            + "AND        e.location_id = :location "
            + "AND        o.concept_id = ${21151} "
            + "AND        o.value_coded <> ${21148} "
            + "AND        o.obs_datetime > second_line.linha_terapeutica "
            + "AND        o.obs_datetime <= second_line.last_consultation";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  // ************** ABANDONED ART SECTION *************

  /**
   * <b> RF7.2 EXCLUSION PATIENTS WHO ABANDONED DURING ART START DATE PERIOD</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>1. para exclusão nos utentes que iniciaram a 1ª linha de TARV, a “Data Início Período” será
   *     igual a “Data Início TARV” e “Data Fim do Período” será igual a “Data Início TARV”+6meses.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvOnArtStartDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV On Art Start Date");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvOnArtStartDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId()));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PREGNANT PATIENTS WHO ABANDONED DURING ART START DATE PERIOD</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>5. para exclusão nas mulheres grávidas que iniciaram TARV a “Data Início Período” será
   *     igual a “Data Início TARV” e “Data Fim do Período” será igual a “Data Início TARV”+3meses.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvOnArtStartDateForPregnants() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV on Art Start Date For Pregnants");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvOnArtStartDateForPregnants(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId()));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PREGNANT PATIENTS WHO ABANDONED DURING ART START DATE PERIOD</b>
   *
   * <p>Excepto as utentes abandono em TARV durante o período (seguindo os critérios
   * definidosBetween 1st clina no RF7.2) nos últimos 3 meses (entre “Data 1ª Consulta Grávida” – 3
   * meses e “Data 1ª Consulta Grávida”).<br>
   * <br>
   *
   * <p>Nota 1: “Data 1ª Consulta Grávida” deve ser a primeira consulta de sempre com registo de
   * grávida e essa consulta deve ter ocorrido no período de inclusão. <br>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvBetween3MonthsBeforePregnancyDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV on Art Start Date For Pregnants");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String a =
        "SELECT abandoned.patient_id from (  "
            + "                                                               SELECT p.patient_id, max(e.encounter_datetime) as last_encounter FROM patient p  "
            + "                                                                                                                                         INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                                                                                                                                         INNER JOIN obs o on e.encounter_id = o.encounter_id  "
            + "                                                                                                                                         INNER JOIN (  "
            + QualityImprovement2020Queries.getPregnancyDuringPeriod()
            + "                                                               ) end_period ON end_period.patient_id = p.patient_id  "
            + "                                                               WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "                                                                 AND e.encounter_type = ${6}  "
            + "                                                                 AND o.concept_id = ${6273}  "
            + "                                                                 AND o.value_coded = ${1707}  "
            + "                                                                 AND e.location_id = :location  "
            + "                                                                 AND e.encounter_datetime >= DATE_SUB(end_period.first_gestante, INTERVAL 3 MONTH)  "
            + "                                                                 AND e.encounter_datetime <= end_period.first_gestante  "
            + "                                                               GROUP BY p.patient_id  "
            + "                                                               UNION  "
            + "                                                               SELECT p.patient_id, max(o.obs_datetime) as last_encounter FROM patient p  "
            + "                                                                                                                                   INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "                                                                                                                                   INNER JOIN obs o on e.encounter_id = o.encounter_id  "
            + "                                                                                                                                   INNER JOIN (  "
            + QualityImprovement2020Queries.getPregnancyDuringPeriod()
            + "                                                               ) end_period ON end_period.patient_id = p.patient_id  "
            + "                                                               WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0  "
            + "                                                                 AND e.encounter_type = ${53}  "
            + "                                                                 AND o.concept_id = ${6272}  "
            + "                                                                 AND o.value_coded = ${1707}  "
            + "                                                                 AND e.location_id = :location  "
            + "                                                                 AND o.obs_datetime >= DATE_SUB(end_period.first_gestante, INTERVAL 3 MONTH)  "
            + "                                                                 AND o.obs_datetime <= end_period.first_gestante  "
            + "                                                               GROUP BY p.patient_id  "
            + "                                                           ) abandoned GROUP BY abandoned.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(a));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PATIENTS WHO ABANDONED DURING ART RESTART DATE PERIOD</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>2.para exclusão nos utentes que reiniciaram TARV, a “Data Início Período” será igual a
   *     “Data Consulta Reinício TARV” e “Data Fim do Período” será igual a “Data Consulta Reínicio
   *     TARV”+6meses.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvOnArtRestartDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV on Art Restart Date");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvOnArtRestartDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getRestartConcept().getConceptId()));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PATIENTS WHO ABANDONED DURING FIRST LINE REGIMEN DATE PERIOD</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>3. para exclusão nos utentes que iniciaram novo regime de 1ª Linha, a “Data Início Período”
   *     será igual a “Data última Alternativa 1ª Linha” e a “Data Fim do Período” será “Data última
   *     Alternativa 1ª Linha” + 6meses.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvOnOnFirstLineDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV on On First Line Date");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvOnFirstLineDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getTherapeuticLineConcept().getConceptId(),
            hivMetadata.getFirstLineConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getJustificativeToChangeArvTreatment().getConceptId(),
            commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId()));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PATIENTS WHO ABANDONED IN THE LAST SIX MONTHS FROM FIRST LINE DATE</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>6. para exclusão nos utentes que estão na 1ª linha de TARV, a “Data Início Período” será
   *     igual a “Data 1a Linha” – 6 meses e “Data Fim do Período” será igual a “Data 1a Linha”.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedInTheLastSixMonthsFromFirstLineDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV In The Last Six Months From First Line Date");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvInTheLastSixMonthsFromFirstLineDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getTherapeuticLineConcept().getConceptId(),
            hivMetadata.getFirstLineConcept().getConceptId(),
            hivMetadata.getARVStartDateConcept().getConceptId(),
            hivMetadata.getJustificativeToChangeArvTreatment().getConceptId(),
            commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId(),
            commonMetadata.getPregnantConcept().getConceptId()));

    return cd;
  }

  /**
   * <b> RF7.2 EXCLUSION FOR PATIENTS WHO ABANDONED DURING SECOND LINE REGIMEN DATE PERIOD</b>
   *
   * <p>O sistema irá identificar utentes que abandonaram o tratamento TARV durante o período da
   * seguinte forma:
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Clínica durante o período (“Data Consulta”>=”Data Início Período” e “Data
   * Consulta”<=”Data Fim Período”
   *
   * <p>incluindo os utentes com Último registo de “Mudança de Estado de Permanência” = “Abandono”
   * na Ficha Resumo durante o período (“Data de Mudança de Estado Permanência”>=”Data Início
   * Período” e “Data Consulta”<=”Data Fim Período”
   * <li>4. para exclusão nos utentes que iniciaram 2ª linha de TARV, a “Data Início Período” será
   *     igual a “Data 2ª Linha” a “Data Fim do Período” será “Data 2ª Linha”+ 6 meses.
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAbandonedTarvOnOnSecondLineDate() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who abandoned TARV on On Second Line Date");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        QualityImprovement2020Queries.getMQ13AbandonedTarvOnSecondLineDate(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getAbandonedConcept().getConceptId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getRegArvSecondLine().getConceptId()));

    return cd;
  }

  // **************/ABANDONED ART SECTION *************

  /**
   * <b>RF14</b>: Select all patients who restarted ART for at least 6 months following: all
   * patients who have “Mudança de Estado de Permanência TARV”=”Reinício” na Ficha Clínica durante o
   * período de inclusão (“Data Consulta Reinício TARV” >= “Data Início Inclusão” e <= “Data Fim
   * Inclusão”), where “Data Última Consulta” durante o período de revisão, menos (-) “Data Consulta
   * Reinício TARV” maior ou igual (>=) a 6 meses
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoRestartedTarvAtLeastSixMonths() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All patients who restarted TARV for at least 6 months");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());

    String query = QualityImprovement2020Queries.getRestartedArtQuery();

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * Filtrando os utentes que têm o registo de dois pedidos de CV na Ficha Clínica (“Pedido de
   * Investigações Laboratoriais” igual a “Carga Viral”) entre “Data Inscrição MDS (DT)” e “Data Fim
   * Revisão” e o registo de um resultado de CV <1000 cps/ml entre os dois pedidos (“Data Consulta
   * Resultado CV”> “Data Consulta 1º Pedido de CV” e < “Data Consulta 2º Pedido de CV”)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithVLResultLessThan1000Between2VlRequestAfterTheseMDS(
      List<Integer> dispensationTypes) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId().toString());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId().toString());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId().toString());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId().toString());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId().toString());
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId().toString());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId().toString());
    map.put("lower", "83");
    map.put("upper", "97");

    String query =
        "SELECT two_dispensations.patient_id "
            + "FROM   (SELECT patient_id, MIN(encounter_datetime) first_date, MAX(encounter_datetime) second_date "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o  ON o.encounter_id = e.encounter_id "
            + "                WHERE  e.encounter_type = ${6} "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND o.concept_id = ${23722} "
            + "                       AND o.value_coded = ${856} "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                       AND e.encounter_datetime >= (SELECT dt_date "
            + "                                                    FROM   (SELECT most_recent.patient_id, MAX(most_recent.encounter_datetime) dt_date "
            + "                                                            FROM   (SELECT p2.patient_id, e2.encounter_datetime "
            + "                                                                    FROM   patient p2 "
            + "                                                                    INNER JOIN encounter e2 ON e2.patient_id = p2.patient_id "
            + "                                                                    INNER JOIN obs otype ON otype.encounter_id = e2.encounter_id "
            + "                                                                    INNER JOIN obs ostate ON ostate.encounter_id = e2.encounter_id "
            + "                                                            WHERE  e2.encounter_type = ${6} "
            + "                                                             AND e2.location_id = :location "
            + "                                                             AND otype.concept_id = ${165174} "
            + "                                                             AND otype.value_coded IN( ${dispensationTypes} ) "
            + "                                                             AND ostate.concept_id = ${165322} "
            + "                                                             AND ostate.value_coded = ${1256} "
            + "                                                             AND e2.encounter_datetime >= :startDate "
            + "                                                             AND e2.encounter_datetime <= :endDate "
            + "                                                             AND otype.obs_group_id = ostate.obs_group_id "
            + "                                                             AND e2.voided = 0 "
            + "                                                             AND p2.voided = 0 "
            + "                                                             AND otype.voided = 0 "
            + "                                                             AND ostate.voided = 0 "
            + "                                                             UNION "
            + "                                                             SELECT p.patient_id, e.encounter_datetime "
            + "                                                             FROM   patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                             WHERE  p.voided = 0 "
            + "                                                                    AND o.voided = 0 "
            + "                                                                    AND e.voided = 0 "
            + "                                                                    AND e.location_id = :location "
            + "                                                                    AND e.encounter_type = ${6} "
            + "                                                                    AND o.concept_id = ${23739} "
            + "                                                                    AND o.value_coded IN ( ${23720}, ${23888}) "
            + "                                                                    AND e.encounter_datetime "
            + "                                                                    AND e.encounter_datetime >= :startDate "
            + "                                                                    AND e.encounter_datetime <= :endDate "
            + "                                                             UNION "
            + "                                                             SELECT p.patient_id, e.encounter_datetime "
            + "                                                             FROM   patient p "
            + "                                                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                             INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                                             INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "                                                                         FROM   patient p "
            + "                                                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                         WHERE  e.encounter_type = ${18} "
            + "                                                                         AND e.location_id = :location "
            + "                                                                         AND e.encounter_datetime BETWEEN  :startDate AND :endDate "
            + "                                                                         AND e.voided = 0 "
            + "                                                                         AND p.voided = 0 "
            + "                                                                         GROUP  BY p.patient_id) recent_clinical "
            + "                                                             ON recent_clinical.patient_id = p.patient_id "
            + "                                                             WHERE  e.encounter_datetime = recent_clinical.consultation_date "
            + "                                                             AND e.encounter_type = ${18} "
            + "                                                             AND e.location_id = :location "
            + "                                                             AND o.concept_id = ${5096} "
            + "                                                             AND ( (DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + "                                                             AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper}) OR (DATEDIFF(o.value_datetime, e.encounter_datetime) >= 173 AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= 187) )"
            + "                                                             AND p.voided = 0 "
            + "                                                             AND e.voided = 0 "
            + "                                                             AND o.voided = 0 "
            + "                                                             GROUP  BY p.patient_id) most_recent "
            + "                                                 GROUP  BY most_recent.patient_id) dispensation "
            + "                                                 WHERE  dispensation.patient_id = p.patient_id)) investigations "
            + "               GROUP  BY investigations.patient_id "
            + "               HAVING COUNT(investigations.encounter_datetime) >= 2) two_dispensations "
            + " INNER JOIN (SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "             FROM   patient p "
            + "             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "             INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "             WHERE  e.encounter_type = ${6} "
            + "             AND e.location_id = :location "
            + "             AND ( ( o.concept_id = ${856} AND o.value_numeric < 1000 ) OR ( o.concept_id = ${1305} AND o.value_coded IS NOT NULL ) ) "
            + "             AND p.voided = 0 "
            + "             AND e.voided = 0 "
            + "             AND o.voided = 0) vl_result ON two_dispensations.patient_id = vl_result.patient_id "
            + " WHERE  vl_result.vl_date > two_dispensations.first_date AND vl_result.vl_date < two_dispensations.second_date"
            + " GROUP BY two_dispensations.patient_id      ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * Utentes que têm o registo de início do MDS para utente estável na última consulta decorrida há
   * 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim
   * Revisão”), ou seja, registo de um MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como:
   *
   * <p>“GA” e o respectivo “Estado” = “Início” “DT” e o respectivo “Estado” = “Início” “DS” e o
   * respectivo “Estado” = “Início” “APE” e o respectivo “Estado” = “Início” “FR” e o respectivo
   * “Estado” = “Início” “DD” e o respectivo “Estado” = “Início”
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsWithMdcOnMostRecentClinicalFormWithFollowingDispensationTypesAndState(
          List<Integer> dispensationTypes, List<Integer> states) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));
    map.put("states", getMetadataFrom(states));

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "INNER JOIN ( "
            + "                      SELECT     p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                      WHERE      e.encounter_type = ${6} "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                      AND        p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      GROUP BY   p.patient_id ) consultation ON consultation.patient_id = p.patient_id "
            + "WHERE      e.encounter_type = ${6} "
            + "AND        e.location_id = :location "
            + "AND        otype.concept_id = ${165174} "
            + "AND        otype.value_coded IN (${dispensationTypes}) "
            + "AND        ostate.concept_id = ${165322} "
            + "AND        ostate.value_coded IN (${states}) "
            + "AND        e.encounter_datetime = consultation.consultation_date "
            + "AND        otype.obs_group_id = ostate.obs_group_id "
            + "AND        e.voided = 0 "
            + "AND        p.voided = 0 "
            + "AND        otype.voided = 0 "
            + "AND        ostate.voided = 0 "
            + "GROUP BY   p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * Utentes com marcação de levantamento a seguir a última consulta clínica no período de
   * avaliação/revisão, na qual foi registado o resultado de CV >= 1000, sendo esta marcação de
   * levantamento entre 23 a 37 dias do levantamento, ou seja, “Data Próximo Levantamento” (marcado
   * no FILA com “Data Levantamento” >= “Data última Consulta” e <= “Data Fim Revisão”) >= “Data
   * Levantamento”+23 dias e <= “Data Levantamento + 37 dias)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHadPickupOnFilaAfterMostRecentVlOnFichaClinica() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "Revision endDate", Date.class));

    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "INNER JOIN ( "
            + "                      SELECT     p.patient_id, MAX(e.encounter_datetime) vl_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e ON         e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o  ON  o.encounter_id = e.encounter_id "
            + "                      WHERE      e.encounter_type = ${6} "
            + "                      AND        e.location_id = :location "
            + "                      AND        o.concept_id = ${856} "
            + "                      AND        o.value_numeric >= 1000 "
            + "                      AND        e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                      AND        p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      GROUP BY   p.patient_id ) vl ON vl.patient_id = p.patient_id "
            + "WHERE      e.encounter_type = ${18} "
            + "AND        e.location_id = :location "
            + "AND        o.concept_id = ${5096} "
            + "AND        e.encounter_datetime BETWEEN vl.vl_date AND :revisionEndDate "
            + "AND        o.value_datetime BETWEEN DATE_ADD(e.encounter_datetime, INTERVAL 23 DAY) AND  DATE_ADD(e.encounter_datetime, INTERVAL 37 DAY) "
            + "AND        e.voided = 0 "
            + "AND        p.voided = 0 "
            + "AND        o.voided = 0 "
            + "GROUP BY   p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * Filtrando os utentes que têm o último registo de “Tipo de Dispensa” = @param dispensationType
   * antes da última consulta do período de revisão ( “Data Última Consulta”)
   *
   * @param dispensationType
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithDispensationBeforeLastConsultationDate(
      Concept dispensationType) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes com registo de tipo de dispensa antes da última ficha clinica");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId());
    map.put("dispensation", dispensationType.getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) encounter_datetime "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                          INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) AS encounter_datetime "
            + "                                      FROM   patient p "
            + "                                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                      WHERE  p.voided = 0 "
            + "                                             AND e.voided = 0 "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.encounter_type = ${6} "
            + "                                             AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                      GROUP  BY p.patient_id) last_consultation "
            + "                                  ON last_consultation.patient_id = p.patient_id "
            + "                   WHERE  p.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND e.location_id = :location "
            + "                          AND e.encounter_type = ${6} "
            + "                          AND o.concept_id = ${23739} "
            + "                          AND e.encounter_datetime < last_consultation.encounter_datetime "
            + "                   GROUP  BY p.patient_id) recent_dispensation_type ON recent_dispensation_type.patient_id = p.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND o.concept_id = ${23739} "
            + "       AND o.value_coded = ${dispensation} "
            + "       AND e.encounter_datetime = recent_dispensation_type.encounter_datetime "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * Todos os utentes que têm o último registo de pelo menos um dos seguintes modelos na “Ficha
   * Clínica” (coluna 24) registada antes da “Data Última Consulta”: Último registo de MDC (MDC1 ou
   * MDC2 ou MDC3 ou MDC4 ou MDC5) como “GAAC” e o respectivo “Estado” = “Iníicio” ou “Continua”, ou
   * Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “DT” e o respectivo “Estado”
   * = “Início” ou “Continua”, ou Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como
   * “DS” e o respectivo “Estado” = “Início” ou “Continua”, ou Último registo de MDC (MDC1 ou MDC2
   * ou MDC3 ou MDC4 ou MDC5) como “APE” e o respectivo “Estado” = “Início” ou “Continua”, ou Último
   * registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como “FR” e o respectivo “Estado” =
   * “Início” ou “Continua”, ou Último registo de MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como
   * “DD” e o respectivo “Estado” = “Início” ou “Continua”
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsWithMdcBeforeMostRecentClinicalFormWithFollowingDispensationTypesAndState(
          List<Integer> dispensationTypes, List<Integer> states) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));
    map.put("states", getMetadataFrom(states));

    String query =
        "SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "INNER JOIN ( "
            + "                      SELECT     p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                      WHERE      e.encounter_type = ${6} "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                      AND        p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      GROUP BY   p.patient_id ) consultation ON consultation.patient_id = p.patient_id "
            + "WHERE      e.encounter_type = ${6} "
            + "AND        e.location_id = :location "
            + "AND        otype.concept_id = ${165174} "
            + "AND        otype.value_coded IN (${dispensationTypes}) "
            + "AND        ostate.concept_id = ${165322} "
            + "AND        ostate.value_coded IN (${states}) "
            + "AND        e.encounter_datetime < consultation.consultation_date "
            + "AND        otype.obs_group_id = ostate.obs_group_id "
            + "AND        e.voided = 0 "
            + "AND        p.voided = 0 "
            + "AND        otype.voided = 0 "
            + "AND        ostate.voided = 0 "
            + "GROUP BY   p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  /**
   * Os utentes com registo de último levantamento na farmácia (FILA) antes da última consulta do
   * período de revisão (“Data última Consulta) com próximo levantamento agendado para 83 a 97 dias
   * ( “Data Próximo Levantamento” menos “Data Levantamento”>= 83 dias e <= 97 dias, sendo “Data
   * Levantamento” último levantamento registado no FILA < “Data Última Consulta”)
   */
  public CohortDefinition getPatientsWhoHadFilaBeforeLastClinicalConsutationBetween(
      int lowerBounded, int upperBounded) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients who have pickup registered on FILA Before Last Clinical Consultation)");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("lower", lowerBounded);
    map.put("upper", upperBounded);

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id, Max(e.encounter_datetime) consultation_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN (SELECT p.patient_id, Max(e.encounter_datetime) consultation_date "
            + "                                      FROM   patient p "
            + "                                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                      WHERE  e.encounter_type = ${6} "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                             AND e.voided = 0 "
            + "                                             AND p.voided = 0 "
            + "                                      GROUP  BY p.patient_id) recent_clinical ON recent_clinical.patient_id = p.patient_id "
            + "                   WHERE  e.encounter_datetime < recent_clinical.consultation_date "
            + "                          AND e.encounter_type = ${18} "
            + "                          AND e.location_id = :location "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                   GROUP  BY p.patient_id) recent_fila "
            + "               ON recent_fila.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${18} "
            + "       AND e.encounter_datetime = recent_fila.consultation_date "
            + "       AND o.concept_id = ${5096} "
            + "       AND e.location_id = :location "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + "       AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper} "
            + " GROUP  BY p.patient_id  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * utentes que têm o registo de “Resultado de Carga Viral” < 1000 cópias, na “Ficha de
   * Laboratório” registada entre “Data Consulta 2º Pedido de CV” e “Data Fim Revisão” .e o
   * resultado é < 1000 cps/ml.
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsWhoHadVLResultLessThen1000nLaboratoryFormAfterSecudondVLRequest(
          List<Integer> dispensationTypes) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId().toString());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId().toString());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId().toString());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId().toString());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId().toString());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId().toString());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId().toString());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId().toString());
    map.put("lower", "83");
    map.put("upper", "97");
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));

    String query =
        "SELECT two_dispensations.patient_id "
            + "FROM   (SELECT patient_id, MIN(encounter_datetime) first_date, MAX(encounter_datetime) second_date "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o  ON o.encounter_id = e.encounter_id "
            + "                WHERE  e.encounter_type = ${6} "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND o.concept_id = ${23722} "
            + "                       AND o.value_coded = ${856} "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                       AND e.encounter_datetime >= (SELECT dt_date "
            + "                                                    FROM   (SELECT most_recent.patient_id, MAX(most_recent.encounter_datetime) dt_date "
            + "                                                            FROM   (SELECT p2.patient_id, e2.encounter_datetime "
            + "                                                                    FROM   patient p2 "
            + "                                                                    INNER JOIN encounter e2 ON e2.patient_id = p2.patient_id "
            + "                                                                    INNER JOIN obs otype ON otype.encounter_id = e2.encounter_id "
            + "                                                                    INNER JOIN obs ostate ON ostate.encounter_id = e2.encounter_id "
            + "                                                                    WHERE  e2.encounter_type = ${6} "
            + "                                                                    AND e2.location_id = :location "
            + "                                                                    AND otype.concept_id =  ${165174} "
            + "                                                                    AND otype.value_coded IN( ${dispensationTypes} ) "
            + "                                                                    AND ostate.concept_id = ${165322} "
            + "                                                                    AND ostate.value_coded =  ${1256} "
            + "                                                                    AND e2.encounter_datetime >= :startDate "
            + "                                                                    AND e2.encounter_datetime <= :endDate "
            + "                                                                    AND otype.obs_group_id = ostate.obs_group_id "
            + "                                                                    AND e2.voided = 0 "
            + "                                                                    AND p2.voided = 0 "
            + "                                                                    AND otype.voided = 0 "
            + "                                                                    AND ostate.voided = 0 "
            + "                                                                    UNION "
            + "                                                                    SELECT p.patient_id, e.encounter_datetime "
            + "                                                                    FROM   patient p "
            + "                                                                    INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                                    INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                                    WHERE  p.voided = 0 "
            + "                                                                           AND o.voided = 0 "
            + "                                                                           AND e.voided = 0 "
            + "                                                                           AND e.location_id = :location "
            + "                                                                           AND e.encounter_type = ${6} "
            + "                                                                           AND o.concept_id = ${23739} "
            + "                                                                           AND o.value_coded IN ( ${23720}, ${23888}) "
            + "                                                                           AND e.encounter_datetime "
            + "                                                                           AND e.encounter_datetime >= :startDate "
            + "                                                                           AND e.encounter_datetime <= :endDate "
            + "                                                                           UNION "
            + "                                                                           SELECT p.patient_id, e.encounter_datetime "
            + "                                                                           FROM   patient p "
            + "                                                                           INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                           INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                                                           INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "                                                                                       FROM   patient p "
            + "                                                                                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                                                       WHERE  e.encounter_type = ${18} "
            + "                                                                                       AND e.location_id = :location "
            + "                                                                                       AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                                                                       AND e.voided = 0 "
            + "                                                                                       AND p.voided = 0 "
            + "                                                                                       GROUP  BY p.patient_id) recent_clinical "
            + "                                                                           ON recent_clinical.patient_id = p.patient_id "
            + "                                                                           WHERE  e.encounter_datetime = recent_clinical.consultation_date "
            + "                                                                                 AND e.encounter_type = ${18} "
            + "                                                                                 AND e.location_id = :location "
            + "                                                                                 AND o.concept_id = ${5096} "
            + "                                                                                 AND ( (DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + "                                                                                 AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper}) OR (DATEDIFF(o.value_datetime, e.encounter_datetime) >= 173 AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= 187) )"
            + "                                                                                 AND p.voided = 0 "
            + "                                                                                 AND e.voided = 0 "
            + "                                                                                 AND o.voided = 0 "
            + "                                                                                 GROUP  BY p.patient_id) most_recent "
            + "                                                                   GROUP  BY most_recent.patient_id) dispensation "
            + "                                                         WHERE  dispensation.patient_id = p.patient_id)) investigations "
            + "                                                         GROUP  BY investigations.patient_id "
            + "        HAVING COUNT(investigations.encounter_datetime) >= 2) two_dispensations "
            + "       INNER JOIN (SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                   WHERE  e.encounter_type = ${13} "
            + "                          AND e.location_id = :location AND ( ( o.concept_id = ${856} AND o.value_numeric < 1000 ) "
            + "                                                                OR ( o.concept_id = ${1305} AND o.value_coded IS NOT NULL ) ) "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0) vl_result "
            + "               ON two_dispensations.patient_id = vl_result.patient_id "
            + "WHERE  vl_result.vl_date > two_dispensations.second_date AND vl_result.vl_date < :revisionEndDate";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  public CohortDefinition getPatientsWhoHadVLResultOnLaboratoryFormAfterSecoddVLRequest(
      List<Integer> dispensationTypes) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Laboratório ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId().toString());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId().toString());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId().toString());
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId().toString());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId().toString());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId().toString());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId().toString());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId().toString());
    map.put("lower", "83");
    map.put("upper", "97");
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));

    String query =
        "SELECT two_dispensations.patient_id "
            + "FROM   (SELECT patient_id, MIN(encounter_datetime) first_date, MAX(encounter_datetime) second_date "
            + "        FROM   (SELECT p.patient_id, e.encounter_datetime "
            + "                FROM   patient p "
            + "                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o  ON o.encounter_id = e.encounter_id "
            + "                WHERE  e.encounter_type = ${6} "
            + "                       AND e.voided = 0 "
            + "                       AND o.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND o.concept_id = ${23722} "
            + "                       AND o.value_coded = ${856} "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                       AND e.encounter_datetime >= (SELECT dt_date "
            + "                                                    FROM   (SELECT most_recent.patient_id, MAX(most_recent.encounter_datetime) dt_date "
            + "                                                            FROM   (SELECT p2.patient_id, e2.encounter_datetime "
            + "                                                                    FROM   patient p2 "
            + "                                                                    INNER JOIN encounter e2 ON e2.patient_id = p2.patient_id "
            + "                                                                    INNER JOIN obs otype ON otype.encounter_id = e2.encounter_id "
            + "                                                                    INNER JOIN obs ostate ON ostate.encounter_id = e2.encounter_id "
            + "                                                                    WHERE  e2.encounter_type = ${6} "
            + "                                                                         AND e2.location_id = :location "
            + "                                                                         AND otype.concept_id = ${165174} "
            + "                                                                         AND otype.value_coded IN( ${dispensationTypes} ) "
            + "                                                                         AND ostate.concept_id = ${165322} "
            + "                                                                         AND ostate.value_coded = ${1256} "
            + "                                                                         AND e2.encounter_datetime >= :startDate "
            + "                                                                         AND e2.encounter_datetime <= :endDate "
            + "                                                                         AND otype.obs_group_id = ostate.obs_group_id "
            + "                                                                         AND e2.voided = 0 "
            + "                                                                         AND p2.voided = 0 "
            + "                                                                         AND otype.voided = 0 "
            + "                                                                         AND ostate.voided = 0 "
            + "                                                             UNION "
            + "                                                             SELECT p.patient_id, e.encounter_datetime "
            + "                                                             FROM   patient p "
            + "                                                             INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                             WHERE  p.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.encounter_type = ${6} "
            + "                                                                 AND o.concept_id = ${23739} "
            + "                                                                 AND o.value_coded IN ( ${23720}, ${23888}) "
            + "                                                                 AND e.encounter_datetime "
            + "                                                                 AND e.encounter_datetime >= :startDate "
            + "                                                                 AND e.encounter_datetime <= :endDate "
            + "                                                         UNION "
            + "                                                         SELECT p.patient_id, e.encounter_datetime "
            + "                                                         FROM   patient p "
            + "                                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                                         INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) consultation_date "
            + "                                                         FROM   patient p "
            + "                                                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                         WHERE  e.encounter_type = ${18} "
            + "                                                             AND e.location_id = :location "
            + "                                                             AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "                                                             AND e.voided = 0 "
            + "                                                             AND p.voided = 0 "
            + "                                                         GROUP  BY p.patient_id) recent_clinical "
            + "                                                   ON recent_clinical.patient_id = p.patient_id "
            + "                                                   WHERE  e.encounter_datetime = recent_clinical.consultation_date "
            + "                                                   AND e.encounter_type = ${18} "
            + "                                                   AND e.location_id = :location "
            + "                                                   AND o.concept_id = ${5096} "
            + "                                                   AND ( (DATEDIFF(o.value_datetime, e.encounter_datetime) >= ${lower} "
            + "                                                   AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= ${upper})"
            + "                                                      OR (DATEDIFF(o.value_datetime, e.encounter_datetime) >= 173 AND DATEDIFF(o.value_datetime, e.encounter_datetime) <= 187) )"
            + "                                                   AND p.voided = 0 "
            + "                                                   AND e.voided = 0 "
            + "                                                   AND o.voided = 0 "
            + "                                               GROUP  BY p.patient_id) most_recent "
            + "                                     GROUP  BY most_recent.patient_id) dispensation "
            + "                                     WHERE  dispensation.patient_id = p.patient_id)) investigations "
            + "                               GROUP  BY investigations.patient_id "
            + "                                HAVING COUNT(investigations.encounter_datetime) >= 2) two_dispensations "
            + "       INNER JOIN (SELECT p.patient_id, e.encounter_datetime AS vl_date "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                          INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                   WHERE  e.encounter_type = ${13} "
            + "                          AND e.location_id = :location "
            + "                          AND  ((o.concept_id = ${856} AND o.value_numeric IS NOT NULL)   OR (o.concept_id = ${1305} AND o.value_coded IS NOT NULL)) "
            + "                          AND p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0) vl_result  ON two_dispensations.patient_id = vl_result.patient_id "
            + "WHERE  vl_result.vl_date > two_dispensations.second_date "
            + "       AND vl_result.vl_date < :revisionEndDate";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  private String getMetadataFrom(List<Integer> dispensationTypes) {
    if (dispensationTypes == null || dispensationTypes.isEmpty()) {

      throw new RuntimeException("The list of encounters or concepts might not be empty ");
    }
    return StringUtils.join(dispensationTypes, ",");
  }

  public CohortDefinition getPatientsHavingTypeOfDispensationBasedOnTheirLastVlResults() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients Having Type of Dispensation Based on Last VL");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(ViralLoadQueries.getPatientsHavingTypeOfDispensationBasedOnTheirLastVlResults());
    return sql;
  }

  /**
   * Filtrando os pacientesutentes que têm o registo de início do MDS para pacienteutente estável na
   * última consulta decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” –
   * 12 meses+1dia e <= “Data Fim Revisão”), ou seja, registo de um MDC (MDC1 ou MDC2 ou MDC3 ou
   * MDC4 ou MDC5) como:
   *
   * <p>“GA” e o respectivo “Estado” = “Início” ou “DT” e o respectivo “Estado” = “Início” ou “DS” e
   * o respectivo “Estado” = “Início” ou “APE” e o respectivo “Estado” = “Início” ou “FR” e o
   * respectivo “Estado” = “Início” ou “DD” e o respectivo “Estado” = “Início” na última consulta
   * clínica (“Ficha Clínica”, coluna 24) decorrida entre: “Data Início de Avaliação” = “Data Fim de
   * Revisão” menos 12 meses + 1 dia “Data Fim de Avaliação” = “Data Fim de Revisão”
   *
   * <p>os utentes que têm o registo de “Tipo de Dispensa” = “DT” na última consulta (“Ficha
   * Clínica”) decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12
   * meses+1dia e <= “Data Fim Revisão”)
   *
   * <p>os utentes com registo de “Tipo de Dispensa” = “DS” na última consulta (“Ficha Clínica”)
   * decorrida há 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12 meses+1dia e
   * <= “Data Fim Revisão”)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 12 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim Revisão”) com próximo
   * levantamento agendado para 83 a 97 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 83 dias e <= 97 dias)
   *
   * <p>os utentes com registo de último levantamento na farmácia (FILA) há 12 meses (última “Data
   * Levantamento”>= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim Revisão”) com próximo
   * levantamento agendado para 173 a 187 dias ( “Data Próximo Levantamento” menos “Data
   * Levantamento”>= 173 dias e <= 187 dias)
   */
  public CohortDefinition getPatientsWhoHadMdsOnMostRecentClinicalAndPickupOnFilaFR36BasedOnLastVl(
      List<Integer> dispensationTypes, List<Integer> states) {

    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();

    compositionCohortDefinition.setName(
        "MDS para utentes estáveis que tiveram consulta no período de avaliação based on the last VL results");

    compositionCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));

    compositionCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition mdsLastClinical12Months =
        getPatientsWithMdcOnMostRecentClinicalFormWithFollowingDispensationTypesAndStateBasedOnLastVl12Months(
            dispensationTypes, states);

    CohortDefinition dsd12Months = getPatientsHavingTypeOfDispensationBasedOnTheirLastVlResults();

    CohortDefinition nextPickupBetween83And97 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBasedOnLastVl12Months(83, 97);

    CohortDefinition nextPickupBetween173And187 =
        QualityImprovement2020Queries.getPatientsWithPickupOnFilaBasedOnLastVl12Months(173, 187);

    compositionCohortDefinition.addSearch(
        "MDS",
        EptsReportUtils.map(
            mdsLastClinical12Months,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "DSDT",
        EptsReportUtils.map(
            dsd12Months, "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA83",
        EptsReportUtils.map(
            nextPickupBetween83And97,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.addSearch(
        "FILA173",
        EptsReportUtils.map(
            nextPickupBetween173And187,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    compositionCohortDefinition.setCompositionString("MDS OR DSDT OR FILA83 OR FILA173");

    return compositionCohortDefinition;
  }

  /**
   * Utentes que têm o registo de início do MDS para utente estável na última consulta decorrida há
   * 12 meses (última “Data Consulta Clínica” >= “Data Fim Revisão” – 12 meses+1dia e <= “Data Fim
   * Revisão”), ou seja, registo de um MDC (MDC1 ou MDC2 ou MDC3 ou MDC4 ou MDC5) como:
   *
   * <p>“GA” e o respectivo “Estado” = “Início” “DT” e o respectivo “Estado” = “Início” “DS” e o
   * respectivo “Estado” = “Início” “APE” e o respectivo “Estado” = “Início” “FR” e o respectivo
   * “Estado” = “Início” “DD” e o respectivo “Estado” = “Início”
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsWithMdcOnMostRecentClinicalFormWithFollowingDispensationTypesAndStateBasedOnLastVl12Months(
          List<Integer> dispensationTypes, List<Integer> states) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Utentes que têm o registo de dois pedidos de CV na Ficha Clinica ");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId().toString());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId().toString());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId().toString());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId().toString());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("dispensationTypes", getMetadataFrom(dispensationTypes));
    map.put("states", getMetadataFrom(states));

    String query =
        "SELECT out_p.patient_id "
            + "FROM   patient pp "
            + "       INNER JOIN encounter ep ON pp.patient_id = ep.patient_id "
            + "       INNER JOIN obs otype ON otype.encounter_id = ep.encounter_id "
            + "       INNER JOIN obs ostate ON ostate.encounter_id = ep.encounter_id "
            + "       INNER JOIN (SELECT patient_id, MAX(encounter_datetime) AS max_vl_date_and_max_ficha "
            + "                   FROM   (SELECT pp.patient_id, ee.encounter_datetime "
            + "                           FROM   patient pp "
            + "                                  INNER JOIN encounter ee ON pp.patient_id = ee.patient_id "
            + "                                  INNER JOIN obs oo ON ee.encounter_id = oo.encounter_id "
            + "                                  INNER JOIN (SELECT patient_id, DATE( Max(encounter_date)) AS vl_max_date "
            + "                                              FROM   (SELECT p.patient_id, DATE(e.encounter_datetime) AS encounter_date "
            + "                                                      FROM   patient p "
            + "                                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                                      INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                                      WHERE  p.voided = 0 "
            + "                                                       AND e.voided = 0 "
            + "                                                       AND o.voided = 0 "
            + "                                                       AND e.encounter_type IN ( ${13}, ${6}, ${9}, ${51} ) "
            + "                                                       AND ( ( o.concept_id = ${856} AND o.value_numeric IS NOT  NULL ) "
            + "                                                             OR ( o.concept_id = ${1305}  AND o.value_coded IS NOT NULL ) ) "
            + "                                                       AND DATE(e.encounter_datetime) BETWEEN :startDate AND :endDate "
            + "                                                       AND e.location_id = :location "
            + "                                               UNION "
            + "                                               SELECT p.patient_id, DATE(o.obs_datetime) AS encounter_date "
            + "                                               FROM   patient p "
            + "                                               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                               WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND o.voided = 0 "
            + "                                                 AND e.encounter_type IN ( ${53} ) "
            + "                                                 AND o.concept_id = ${856} "
            + "                                                 AND o.value_numeric IS NOT NULL "
            + "                                                 AND DATE(o.obs_datetime) BETWEEN :startDate AND :endDate "
            + "                                                 AND e.location_id = :location) max_vl_date "
            + "                                                 GROUP  BY patient_id "
            + "                   ) vl_date_tbl ON pp.patient_id = vl_date_tbl.patient_id "
            + "                 WHERE  ee.encounter_datetime BETWEEN Date_add( vl_date_tbl.vl_max_date, INTERVAL - 12 MONTH) AND  DATE_ADD( vl_date_tbl.vl_max_date,INTERVAL - 1 DAY) "
            + "                 AND oo.concept_id = ${165174} "
            + "                 AND oo.voided = 0 "
            + "                 AND ee.voided = 0 "
            + "                 AND ee.location_id = :location "
            + "                 AND ee.encounter_type = ${6}) fin_tbl "
            + "                 GROUP  BY patient_id) out_p ON pp.patient_id = out_p.patient_id "
            + "WHERE  ep.encounter_type = ${6} "
            + "	AND        ep.location_id = :location "
            + "	AND        otype.concept_id = ${165174} "
            + "	AND        otype.value_coded IN (${dispensationTypes}) "
            + "	AND        ostate.concept_id = ${165322} "
            + "	AND        ostate.value_coded IN (${states}) "
            + "	AND        ep.encounter_datetime = out_p.max_vl_date_and_max_ficha  "
            + "	AND        otype.obs_group_id = ostate.obs_group_id "
            + "	AND        ep.voided = 0 "
            + "	AND        pp.voided = 0 "
            + "	AND        otype.voided = 0 "
            + "	AND        ostate.voided = 0 "
            + "	GROUP BY   pp.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    cd.setQuery(sb.replace(query));

    return cd;
  }

  public CohortDefinition getPatientsOnMQCat18Denominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Cat 18 Denominator");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition startedArt = getMOHArtStartDate();
    CohortDefinition inTarv = resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfMonthB13();
    CohortDefinition transferredIn =
        QualityImprovement2020Queries.getTransferredInPatients(
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            commonMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
            hivMetadata.getPatientFoundYesConcept().getConceptId(),
            hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
            hivMetadata.getArtStatus().getConceptId());

    cd.addSearch(
        "startedArt",
        EptsReportUtils.map(
            startedArt, "startDate=${endDate-14m},endDate=${endDate-11m},location=${location}"));

    cd.addSearch("inTarv", EptsReportUtils.map(inTarv, "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "transferredIn",
        EptsReportUtils.map(
            transferredIn, "startDate=${endDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(startedArt AND inTarv) AND NOT transferredIn");

    return cd;
  }

  public CohortDefinition getPatientsOnMQCat18Numerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Cat 18 Numerator");
    cd.addParameter(new Parameter("revisionEndDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition denominator = getPatientsOnMQCat18Denominator();
    CohortDefinition diagnose =
        QualityImprovement2020Queries.getDisclosureOfHIVDiagnosisToChildrenAdolescents();

    cd.addSearch(
        "denominator",
        EptsReportUtils.map(denominator, "endDate=${revisionEndDate},location=${location}"));

    cd.addSearch(
        "diagnose",
        EptsReportUtils.map(
            diagnose,
            "startDate=${revisionEndDate-14m},endDate=${revisionEndDate-11m},revisionEndDate=${revisionEndDate},location=${location}"));

    cd.setCompositionString("denominator AND diagnose");

    return cd;
  }

  /**
   * <b>Description:</b> MQ-MOH Query For pregnant or Breastfeeding patients
   *
   * <p><b>Technical Specs</b>
   * <li>A - Select all female patients who are pregnant as following: all patients registered in
   *     Ficha Clinica (encounter type=6) with “Gestante”(concept_id 1982) value coded equal to
   *     “Yes” (concept_id 1065) and sex=Female
   * <li>B - Select all female patients who are breastfeeding as following: all patients registered
   *     in Ficha Clinica (encounter type=6) with “Lactante”(concept_id 6332) value coded equal to
   *     “Yes” (concept_id 1065) and sex=Female
   *
   * @param question The question Concept Id
   * @param answer The value coded Concept Id
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPregnantORBreastfeedingOnClinicalConsultation(
      int question, int answer) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant Or Breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("question", question);
    map.put("answer", answer);

    String query =
        "SELECT p.person_id  "
            + "FROM   person p  "
            + "       JOIN encounter e  "
            + "         ON e.patient_id = p.person_id  "
            + "       JOIN obs o  "
            + "         ON o.encounter_id = e.encounter_id  "
            + "            AND encounter_type = ${6}  "
            + "            AND o.concept_id = ${question}  "
            + "            AND o.value_coded = ${answer}  "
            + "            AND e.location_id = :location  "
            + "            AND e.encounter_datetime >= :startDate  "
            + "            AND e.encounter_datetime <= :endDate  "
            + "            AND p.gender = 'F'  "
            + "            AND e.voided = 0  "
            + "            AND o.voided = 0  "
            + "            AND p.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Registo da primeira consulta clínica durante o período de inclusão (>= “Data Fim de Revisão”
   * menos (-) 12 meses mais (+) 1 dia e <= “Data fim de Revisão” menos (-) 9 meses).
   *
   * <p>Nota: é a primeira consulta clínica de sempre do utente que decorreu no período de inclusão.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getFirstClinicalConsultationDuringInclusionPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" First Clinical Consultation During Inclusion Period ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT final.patient_id  "
            + "FROM   (  "
            + "           SELECT pa.patient_id,  "
            + "                  MIN(enc.encounter_datetime) AS first_consultation  "
            + "           FROM   patient pa  "
            + "                      INNER JOIN encounter enc  "
            + "                                 ON enc.patient_id =  pa.patient_id  "
            + "                      INNER JOIN obs  "
            + "                                 ON obs.encounter_id = enc.encounter_id  "
            + "           WHERE pa.voided = 0  "
            + "             AND enc.voided = 0  "
            + "             AND obs.voided = 0  "
            + "             AND enc.encounter_type = ${6}  "
            + "             AND enc.encounter_datetime <= :revisionEndDate "
            + "             AND enc.location_id = :location  "
            + "           GROUP  BY pa.patient_id  "
            + "       ) final  "
            + "WHERE  final.first_consultation >= :startDate  "
            + "  AND final.first_consultation <= :endDate";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Filtrando os que tiveram registo do “Pedido de CD4” na primeira consulta clínica do período de
   * inclusão (>= “Data Fim de Revisão” menos (-) 12 meses mais (+) 1 dia e <= “Data fim de Revisão”
   * menos (-) 9 meses).
   *
   * <p>Nota: é a primeira consulta clínica de sempre do utente que decorreu no período de inclusão.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getRequestForCd4OnFirstClinicalConsultationDuringInclusionPeriod() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" First Clinical Consultation During Inclusion Period ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("1695", hivMetadata.getCD4AbsoluteOBSConcept().getConceptId());

    String query =
        "SELECT pa.patient_id "
            + "FROM "
            + "    patient pa "
            + "        INNER JOIN encounter enc "
            + "                   ON enc.patient_id =  pa.patient_id "
            + "        INNER JOIN obs o2 "
            + "                   ON o2.encounter_id = enc.encounter_id "
            + "        INNER JOIN "
            + "    ( "
            + "        SELECT pa.patient_id, "
            + "               MIN(enc.encounter_datetime)  first_consultation "
            + "        FROM   patient pa "
            + "                   INNER JOIN encounter enc "
            + "                              ON enc.patient_id =  pa.patient_id "
            + "                   INNER JOIN obs "
            + "                              ON obs.encounter_id = enc.encounter_id "
            + "        WHERE  pa.voided = 0 "
            + "          AND enc.voided = 0 "
            + "          AND obs.voided = 0 "
            + "          AND enc.encounter_type = ${6} "
            + "          AND enc.encounter_datetime <= :revisionEndDate "
            + "          AND enc.location_id = :location "
            + "        GROUP  BY pa.patient_id "
            + "    ) final ON final.patient_id = pa.patient_id "
            + "        AND final.first_consultation >= :startDate "
            + "        AND final.first_consultation <= :endDate "
            + "WHERE pa.voided = 0 "
            + "  AND enc.voided = 0 "
            + "  AND o2.voided = 0 "
            + "  AND enc.encounter_type = ${6} "
            + "  AND enc.encounter_datetime = final.first_consultation "
            + "  AND o2.concept_id = ${23722} "
            + "  AND o2.value_coded = ${1695} "
            + "  AND enc.location_id = :location "
            + "GROUP BY pa.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MQ-MOH Query For pregnant or Breastfeeding patients
   *
   * <p><b>Technical Specs</b>
   * <li>O sistema irá identificar mulheres que tiveram primeiro registo de gravidez durante o
   *     período de inclusão, ou seja, as que iniciaram a gravidez durante o período de inclusão,
   *     seleccionado:
   * <li>todos os utentes do sexo feminino, independentemente da idade, e registados como
   *     “Grávida=Sim” numa consulta clínica decorrida durante o período de inclusão (“Data Consulta
   *     Clínica Gravida” >= “Data Fim Revisão” menos (-) 12 meses mais (+) 1 dia e <= “Data Fim
   *     Revisão” menos (-) 9 meses.
   * <li>excluindo todos os utentes registados como “Grávida=Sim” numa consulta clínica decorrida
   *     nos últimos 9 meses antes do período de inclusão (“Data Consulta Clínica Gravida” < “Data
   *     Fim Revisão” menos (-) 12 meses mais (+) 1 dia e >= “Data Fim Revisão” menos (-) 12 meses
   *     mais (+) 1 dia menos (-) 9 meses).
   *
   * @param question The question Concept Id
   * @param answer The value coded Concept Id
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getFirstPregnancyORBreastfeedingOnClinicalConsultation(
      int question, int answer) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Mulheres com registo de primeira gravidez no período de inclusão");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("question", question);
    map.put("answer", answer);

    String query =
        "SELECT   pregnant.person_id "
            + "FROM     ( "
            + "                  SELECT   p.person_id, "
            + "                           Min(e.encounter_datetime) AS first_pregnancy "
            + "                  FROM     person p "
            + "                  JOIN     encounter e "
            + "                  ON       e.patient_id = p.person_id "
            + "                  JOIN     obs o "
            + "                  ON       o.encounter_id = e.encounter_id "
            + "                  AND      encounter_type = ${6} "
            + "                  AND      o.concept_id = ${question} "
            + "                  AND      o.value_coded = ${answer} "
            + "                  AND      e.location_id = :location "
            + "                  AND      e.encounter_datetime <= :revisionEndDate "
            + "                  AND      p.gender = 'F' "
            + "                  AND      e.voided = 0 "
            + "                  AND      o.voided = 0 "
            + "                  AND      p.voided = 0 "
            + "                  GROUP BY p.person_id) pregnant "
            + "WHERE    pregnant.first_pregnancy >= :startDate "
            + "AND      pregnant.first_pregnancy <= :endDate "
            + "AND      pregnant.person_id NOT IN "
            + "         ( "
            + "                SELECT p.person_id "
            + "                FROM   person p "
            + "                JOIN   encounter e "
            + "                ON     e.patient_id = p.person_id "
            + "                JOIN   obs o "
            + "                ON     o.encounter_id = e.encounter_id "
            + "                AND    encounter_type = ${6} "
            + "                AND    o.concept_id = ${question} "
            + "                AND    o.value_coded = ${answer} "
            + "                AND    e.location_id = :location "
            + "                AND    e.encounter_datetime >= date_sub(:startDate, interval 9 month ) "
            + "                AND    e.encounter_datetime < :startDate "
            + "                AND    p.gender = 'F' "
            + "                AND    e.voided = 0 "
            + "                AND    o.voided = 0 "
            + "                AND    p.voided = 0 ) "
            + "GROUP BY pregnant.person_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * Filtrando as que tiveram registo do “Pedido de CD4” na mesma consulta clínica na qual tiveram o
   * primeiro registo de Gravidez durante o período de inclusão (>= “Data Fim de Revisão” menos (-)
   * 12 meses mais (+) 1 dia e “Data fim de Revisão” menos (-) 9 meses).
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getRequestForCd4OnFirstClinicalConsultationOfPregnancy(
      int pregnantConcept, int yesConcept, int labResearchConcept, int cd4) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        " “Pedido de CD4” na mesma consulta clínica na qual tiveram o primeiro registo de Gravidez");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("pregnantConcept", pregnantConcept);
    map.put("yesConcept", yesConcept);
    map.put("labResearchConcept", labResearchConcept);
    map.put("cd4", cd4);

    String query =
        "SELECT pa.patient_id "
            + "             FROM "
            + "                 patient pa "
            + "                     INNER JOIN encounter enc "
            + "                                ON enc.patient_id =  pa.patient_id "
            + "                     INNER JOIN obs o2 "
            + "                                ON o2.encounter_id = enc.encounter_id "
            + "                     INNER JOIN "
            + "                 ( "
            + "                     SELECT pregnant.person_id, pregnant.first_pregnancy as first_consultation FROM ( "
            + "                                                                                                        SELECT p.person_id, MIN(e.encounter_datetime) as first_pregnancy "
            + "                                                                                                        FROM   person p "
            + "                                                                                                                   JOIN encounter e "
            + "                                                                                                                        ON e.patient_id = p.person_id "
            + "                                                                                                                   JOIN obs o "
            + "                                                                                                                        ON o.encounter_id = e.encounter_id "
            + "                                                                                                                            AND encounter_type = ${6} "
            + "                                                                                                                            AND o.concept_id = ${pregnantConcept} "
            + "                                                                                                                            AND o.value_coded = ${yesConcept} "
            + "                                                                                                                            AND e.location_id = :location "
            + "                                                                                                                            AND e.encounter_datetime <= :revisionEndDate "
            + "                                                                                                                            AND p.gender = 'F' "
            + "                                                                                                                            AND e.voided = 0 "
            + "                                                                                                                            AND o.voided = 0 "
            + "                                                                                                                            AND p.voided = 0 "
            + "                                                                                                        GROUP BY p.person_id) pregnant "
            + "                     WHERE "
            + "                             pregnant.first_pregnancy >=  :startDate "
            + "                       AND pregnant.first_pregnancy <= :endDate "
            + "                       AND   pregnant.person_id NOT IN ( "
            + "                             SELECT p.person_id "
            + "                             FROM   person p "
            + "                                        JOIN encounter e "
            + "                                             ON e.patient_id = p.person_id "
            + "                                        JOIN obs o "
            + "                                             ON o.encounter_id = e.encounter_id "
            + "                                                 AND encounter_type = ${6} "
            + "                                                 AND o.concept_id = ${pregnantConcept} "
            + "                                                 AND o.value_coded = ${yesConcept} "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_datetime >= date_sub(:startDate, interval 9 month )  "
            + "                                                 AND e.encounter_datetime < :startDate "
            + "                                                 AND p.gender = 'F' "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND o.voided = 0 "
            + "                                                 AND p.voided = 0 "
            + "                         ) "
            + "                     GROUP BY pregnant.person_id "
            + "                 ) final ON final.person_id = pa.patient_id "
            + "             WHERE pa.voided = 0 "
            + "               AND enc.voided = 0 "
            + "               AND o2.voided = 0 "
            + "               AND enc.encounter_type = ${6} "
            + "               AND enc.encounter_datetime = final.first_consultation "
            + "               AND o2.concept_id = ${labResearchConcept} "
            + "               AND o2.value_coded = ${cd4} "
            + "               AND enc.location_id = :location "
            + "             GROUP BY pa.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   * <li>Filtrando os que tiveram registo do “Resultado de CD4” na consulta clínica decorrida em 33
   *     dias após a consulta clínica com o primeiro registo de Gravidez no período de inclusão
   *     (“Data Consulta Grávida” >= “Data Fim de Revisão” menos (-) 12 meses mais (+) 1 dia e “Data
   *     fim de Revisão” menos (-) 9 meses), ou seja, “Data Resultado de CD4” menos a “Data Primeira
   *     Gravidez” <=33 dias.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getCd4ResultAfterFirstConsultationOfPregnancy(
      int pregnantConcept, int yesConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Filter all patients with CD4 within 33 days from the first pregnancy consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1695", hivMetadata.getCD4AbsoluteOBSConcept().getConceptId());
    map.put("730", hivMetadata.getCD4PercentConcept().getConceptId());
    map.put("pregnantConcept", pregnantConcept);
    map.put("yesConcept", yesConcept);

    String query =
        "SELECT pa.patient_id "
            + "FROM "
            + "    patient pa "
            + "        INNER JOIN encounter enc "
            + "                   ON enc.patient_id =  pa.patient_id "
            + "        INNER JOIN obs "
            + "                   ON obs.encounter_id = enc.encounter_id "
            + "        INNER JOIN obs o2 "
            + "                   ON o2.encounter_id = enc.encounter_id "
            + "        INNER JOIN "
            + "    ( "
            + "                     SELECT pregnant.person_id, pregnant.first_pregnancy as first_consultation FROM ( "
            + "                                                                                                        SELECT p.person_id, MIN(e.encounter_datetime) as first_pregnancy "
            + "                                                                                                        FROM   person p "
            + "                                                                                                                   JOIN encounter e "
            + "                                                                                                                        ON e.patient_id = p.person_id "
            + "                                                                                                                   JOIN obs o "
            + "                                                                                                                        ON o.encounter_id = e.encounter_id "
            + "                                                                                                                            AND encounter_type = ${6} "
            + "                                                                                                                            AND o.concept_id = ${pregnantConcept} "
            + "                                                                                                                            AND o.value_coded = ${yesConcept} "
            + "                                                                                                                            AND e.location_id = :location "
            + "                                                                                                                            AND e.encounter_datetime <= :revisionEndDate "
            + "                                                                                                                            AND p.gender = 'F' "
            + "                                                                                                                            AND e.voided = 0 "
            + "                                                                                                                            AND o.voided = 0 "
            + "                                                                                                                            AND p.voided = 0 "
            + "                                                                                                        GROUP BY p.person_id) pregnant "
            + "                     WHERE "
            + "                             pregnant.first_pregnancy >=  :startDate "
            + "                       AND pregnant.first_pregnancy <= :endDate "
            + "                       AND   pregnant.person_id NOT IN ( "
            + "                             SELECT p.person_id "
            + "                             FROM   person p "
            + "                                        JOIN encounter e "
            + "                                             ON e.patient_id = p.person_id "
            + "                                        JOIN obs o "
            + "                                             ON o.encounter_id = e.encounter_id "
            + "                                                 AND encounter_type = ${6} "
            + "                                                 AND o.concept_id = ${pregnantConcept} "
            + "                                                 AND o.value_coded = ${yesConcept} "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_datetime >= date_sub(:startDate, interval 9 month )  "
            + "                                                 AND e.encounter_datetime < :startDate "
            + "                                                 AND p.gender = 'F' "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND o.voided = 0 "
            + "                                                 AND p.voided = 0 "
            + "                         ) "
            + "                     GROUP BY pregnant.person_id "
            + "    ) consultation_date ON consultation_date.person_id = pa.patient_id "
            + "WHERE  pa.voided = 0 "
            + "  AND enc.voided = 0 "
            + "  AND obs.voided = 0 "
            + "  AND o2.voided = 0 "
            + "  AND enc.encounter_type = ${6} "
            + "  AND ( "
            + "        (obs.concept_id = ${1695} AND obs.value_numeric IS NOT NULL) "
            + "        OR "
            + "        (o2.concept_id = ${730} AND o2.value_numeric IS NOT NULL) "
            + "      ) "
            + "  AND enc.encounter_datetime > consultation_date.first_consultation "
            + "  AND enc.encounter_datetime <= DATE_ADD(consultation_date.first_consultation, INTERVAL 33 DAY) "
            + "  AND enc.location_id = :location "
            + "GROUP BY pa.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Transferred Out Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * O sistema irá identificar utentes “Transferido Para” outras US em TARV durante o período de
   * revisão seleccionando os utentes registados como:
   *
   * <ul>
   *   <li>[“Mudança Estado Permanência TARV” (Coluna 21) = “T” (Transferido Para) na “Ficha
   *       Clínica” com “Data da Consulta Actual” (Coluna 1, durante a qual se fez o registo da
   *       mudança do estado de permanência TARV) dentro do período de revisão ou
   *   <li>>registados como “Mudança Estado Permanência TARV” = “Transferido Para”, último estado
   *       registado na “Ficha Resumo” com “Data da Transferência” dentro do período de revisão;
   * </ul>
   *
   * <p>Excluindo os utentes que tenham tido uma consulta clínica (Ficha Clínica) ou levantamento de
   * ARV (FILA) após a “Data de Transferência” (a data mais recente entre os critérios acima
   * identificados) e até “Data Fim Revisão”.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   *     <li><strong>Should</strong> Returns empty if there is no patient who meets the conditions
   *     <li><strong>Should</strong> fetch all patients transfer out other facility
   */
  public CohortDefinition getTranferredOutPatients() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients registered in encounter “Ficha Resumo-MasterCard”");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());

    String query =
        " SELECT patient_id "
            + "FROM   (SELECT transferout.patient_id, "
            + "               Max(transferout.last_date) transferout_date "
            + "        FROM   ( SELECT p.patient_id, "
            + "                        last_clinical.last_date AS last_date "
            + "                 FROM   patient p "
            + "                            JOIN encounter e "
            + "                                 ON p.patient_id = e.patient_id "
            + "                            JOIN obs o "
            + "                                 ON e.encounter_id = o.encounter_id "
            + "                            JOIN (SELECT p.patient_id, "
            + "                                         Max(e.encounter_datetime) AS last_date "
            + "                                  FROM   patient p "
            + "                                             JOIN encounter e "
            + "                                                  ON p.patient_id = e.patient_id "
            + "                                  WHERE  p.voided = 0 "
            + "                                    AND e.voided = 0 "
            + "                                    AND e.location_id = :location "
            + "                                    AND e.encounter_type = ${6} "
            + "                                    AND e.encounter_datetime <= :revisionEndDate "
            + "                                  GROUP BY p.patient_id) last_clinical "
            + "                                ON last_clinical.patient_id = p.patient_id "
            + "                 WHERE  p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND e.encounter_datetime = last_clinical.last_date "
            + "                   AND o.voided = 0 "
            + "                   AND o.concept_id = ${6273} "
            + "                   AND o.value_coded = ${1706} "
            + "                 GROUP  BY p.patient_id "
            + "                UNION "
            + "                SELECT p.patient_id, "
            + "                       Max(o.obs_datetime) AS last_date "
            + "                FROM   patient p "
            + "                       JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                       JOIN obs o "
            + "                         ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND o.obs_datetime <= :revisionEndDate "
            + "                       AND o.voided = 0 "
            + "                       AND o.concept_id = ${6272} "
            + "                       AND o.value_coded = ${1706} "
            + "                GROUP  BY p.patient_id) transferout "
            + "        GROUP  BY transferout.patient_id) max_transferout "
            + "WHERE  max_transferout.patient_id NOT IN (SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                 JOIN encounter e "
            + "                                                   ON p.patient_id = "
            + "                                                      e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND e.encounter_type = ${6} "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND "
            + "                                                 e.encounter_datetime > transferout_date "
            + "                                                 AND "
            + "                                                 e.encounter_datetime <= :revisionEndDate "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                 JOIN encounter e "
            + "                                                   ON p.patient_id = "
            + "                                                      e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND e.encounter_type = ${18} "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND e.encounter_datetime > transferout_date "
            + "                                                 AND "
            + "                                                 e.encounter_datetime <= :revisionEndDate)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B2</b>: Utentes em 1ª Linha elegíveis ao pedido de CV <br>
   *
   * <ul>
   *   <li>incluindo os utentes há pelo menos 6 meses na 1ª Linha de TARV, ou seja, incluindo todos
   *       os utentes que têm o último registo da “Linha Terapêutica” na Ficha Clínica durante o
   *       período de revisão igual a “1ª Linha” (última consulta, “Data 1ª Linha”>= “Data Início
   *       Revisão” e <= “Data Fim Revisão”), sendo a “Data 1ª Linha” menos (-) “Data do Início
   *       TARV” registada na Ficha Resumo maior ou igual (>=) a 6 meses
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsOnArtFirstLineForMoreThanSixMonthsFromArtStartDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("1ª Linha de TARV há mais de 6 meses do Início TARV");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());

    String query =
        " SELECT p.patient_id  "
            + "FROM   patient p  "
            + "           INNER JOIN encounter e  "
            + "                      ON e.patient_id = p.patient_id  "
            + "           INNER JOIN obs o  "
            + "                      ON o.encounter_id = e.encounter_id  "
            + "           INNER JOIN (SELECT p.patient_id,  "
            + "                              Max(e.encounter_datetime) AS encounter_datetime  "
            + "                       FROM   patient p  "
            + "                                  INNER JOIN encounter e  "
            + "                                             ON e.patient_id = p.patient_id  "
            + "                                  JOIN obs o  "
            + "                                       ON o.encounter_id = e.encounter_id  "
            + "                       WHERE  e.encounter_type = ${6}  "
            + "                         AND p.voided = 0  "
            + "                         AND e.voided = 0  "
            + "                         AND e.location_id = :location  "
            + "                         AND o.voided = 0  "
            + "                         AND e.encounter_datetime BETWEEN  "
            + "                           :startDate AND :revisionEndDate  "
            + "                       GROUP  BY p.patient_id) last_consultation  "
            + "                      ON p.patient_id = last_consultation.patient_id  "
            + "           INNER JOIN (  "
            + "    SELECT p.patient_id, Min(o.value_datetime) art_date  "
            + "            FROM patient p  "
            + "                     INNER JOIN encounter e  "
            + "                                ON p.patient_id = e.patient_id  "
            + "                     INNER JOIN obs o  "
            + "                                ON e.encounter_id = o.encounter_id  "
            + "            WHERE  p.voided = 0  "
            + "              AND e.voided = 0  "
            + "              AND o.voided = 0  "
            + "              AND e.encounter_type = ${53}  "
            + "              AND o.concept_id = ${1190}  "
            + "              AND o.value_datetime IS NOT NULL  "
            + "              AND o.value_datetime <= :revisionEndDate  "
            + "              AND e.location_id = :location  "
            + "            GROUP  BY p.patient_id ) art_start on art_start.patient_id = p.patient_id  "
            + "WHERE e.encounter_type = ${6}  "
            + "  AND o.concept_id = ${21151}  "
            + "  AND o.value_coded = ${21150}  "
            + "  AND e.location_id = :location  "
            + "  AND e.voided = 0  "
            + "  AND o.voided = 0  "
            + "  AND e.encounter_datetime = last_consultation.encounter_datetime  "
            + "  AND TIMESTAMPDIFF(MONTH, art_start.art_date, e.encounter_datetime) >= 6 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B2</b>: Utentes em 1ª Linha elegíveis ao pedido de CV <br>
   *
   * <ul>
   *   Todos utentes que Mudaram de Regime na 1ª Linha de TARV há pelo menos 6 meses, ou seja,
   *   incluindo todos os utentes que têm o último registo da “Alternativa a Linha – 1ª Linha” na
   *   Ficha Resumo, sendo a “Data Última Alternativa 1ª Linha” menos (-) “Data Última Consulta”
   *   maior ou igual (>=) a 6 meses
   * </ul>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsOnTreatmentFor6Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "utentes que Mudaram de Regime na 1ª Linha de TARV há pelo menos 6 meses");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("21190", commonMetadata.getRegimenAlternativeToFirstLineConcept().getConceptId());

    String query =
        "SELECT p.patient_id  "
            + "FROM   patient p  "
            + "           INNER JOIN encounter e  "
            + "                      ON e.patient_id = p.patient_id  "
            + "           INNER JOIN obs o  "
            + "                      ON o.encounter_id = e.encounter_id  "
            + "           INNER JOIN (SELECT p.patient_id,  "
            + "                              Max(e.encounter_datetime) AS encounter_datetime  "
            + "                       FROM   patient p  "
            + "                                  INNER JOIN encounter e  "
            + "                                             ON e.patient_id = p.patient_id  "
            + "                                  JOIN obs o  "
            + "                                       ON o.encounter_id = e.encounter_id  "
            + "                       WHERE  e.encounter_type = ${6}  "
            + "                         AND p.voided = 0  "
            + "                         AND e.voided = 0  "
            + "                         AND e.location_id = :location  "
            + "                         AND o.voided = 0  "
            + "                         AND e.encounter_datetime BETWEEN  "
            + "                           :startDate AND :revisionEndDate  "
            + "                       GROUP  BY p.patient_id) last_consultation  "
            + "                      ON p.patient_id = last_consultation.patient_id  "
            + "           INNER JOIN (  "
            + "    SELECT p.patient_id, MAX(o.obs_datetime) first_line_date  "
            + "    FROM patient p  "
            + "             INNER JOIN encounter e  "
            + "                        ON p.patient_id = e.patient_id  "
            + "             INNER JOIN obs o  "
            + "                        ON e.encounter_id = o.encounter_id  "
            + "    WHERE  p.voided = 0  "
            + "      AND e.voided = 0  "
            + "      AND o.voided = 0  "
            + "      AND e.encounter_type = ${53}  "
            + "      AND o.concept_id = ${21190}  "
            + "      AND o.value_coded IS NOT NULL  "
            + "      AND o.obs_datetime <= :revisionEndDate  "
            + "      AND e.location_id = :location  "
            + "    GROUP  BY p.patient_id  "
            + ") regimen_change on regimen_change.patient_id = p.patient_id  "
            + "WHERE e.encounter_type = ${53} "
            + "  AND o.concept_id = ${21190}  "
            + "  AND o.value_coded IS NOT NULL  "
            + "  AND o.obs_datetime = regimen_change.first_line_date  "
            + "  AND e.location_id = :location  "
            + "  AND e.voided = 0  "
            + "  AND o.voided = 0  "
            + "  AND TIMESTAMPDIFF(MONTH, o.obs_datetime, last_consultation.encounter_datetime) >= 6";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>MQC11B2</b>: Utentes em 1ª Linha elegíveis ao pedido de CV <br>
   *
   * <ul>
   *   <li>incluindo todos os utentes com idade >= 15 anos (seguindo o critério definido no RF13) e
   *       com registo de uma Carga Viral na Ficha Clínica ou Ficha Resumo com resultado >= 1000
   *       cópias durante o período de inclusão (“Data da CV>=1000” >= “Data Início Inclusão” e <=
   *       “Data Fim Inclusão”)
   * </ul>
   *
   * @param vlQuantity Quantity of viral load to evaluate
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithCargaViralonFichaClinicaAndFichaResumo(int vlQuantity) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Carga Viral na Ficha Clínica ou Ficha Resumo com resultado >= 1000");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "  SELECT vl.patient_id "
            + " FROM   (SELECT p.patient_id, "
            + "         CASE "
            + "           WHEN e.encounter_type = ${6} THEN MIN(e.encounter_datetime) "
            + "           WHEN e.encounter_type = ${53} THEN MIN(o.obs_datetime) "
            + "         END AS first_encounter "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "         WHERE "
            + "             ( "
            + "               (  (e.encounter_type = ${6} "
            + "                     AND e.encounter_datetime BETWEEN :startDate AND :endDate)"
            + "               OR (e.encounter_type = ${53} "
            + "                     AND o.obs_datetime BETWEEN :startDate AND :endDate) ) "
            + "             ) "
            + "         AND e.location_id = :location "
            + "         AND o.concept_id = ${856} "
            + "         AND o.value_numeric >= "
            + vlQuantity
            + "        AND p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + " GROUP BY p.patient_id "
            + "        ) vl "
            + " GROUP BY vl.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
