package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsWithPositiveTbScreeningQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB (Condicoes medicas
   * importantes) – Ficha Resumo Mastercard during reporting period
   *
   * <p>• Encounter Type ID = 53<br>
   * • Concept ID for Pulmonary TB = 421406 <br>
   * • Answer = Yes (value_coded 106542) <br>
   * • Obs_datetime >= :startDate and <= :endDate
   *
   * @return {@link String}
   */
  public String getPatientWithPulmonaryTbdDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1406", hivMetadata.getOtherDiagnosis().getConceptId());
    map.put("42", tbMetadata.getPulmonaryTB().getConceptId());
    String query =
        "SELECT p.patient_id,o.obs_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${53} "
            + "       AND e.location_id = :location "
            + "       AND o.obs_datetime >= :startDate AND o.obs_datetime <= :endDate "
            + "       AND o.concept_id = ${1406} "
            + "       AND o.value_coded = ${42} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients marked as “Tratamento TB= Inicio (I) ” in Ficha Clinica Master Card
   *
   * <p>• Encounter Type ID = 6 <br>
   * • TB Treament Plan ID = 1268 <br>
   * • Answer = START (value_coded 1256) <br>
   * • obs_datetime >= startDate and <=endDate <br>
   *
   * @return {@link String}
   */
  public String getPatientMarkedAsTbTreatmentStartAndDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1268", hivMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    String query =
        "SELECT p.patient_id,o.obs_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND o.obs_datetime >= :startDate AND o.obs_datetime <= :endDate "
            + "       AND o.concept_id = ${1268} "
            + "       AND o.value_coded = ${1256} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * “TB Program Enrollment Date” (Data de Admissão) In SESP – Program Enrollment within the
   * reporting period
   *
   * @return {@link String}
   */
  public String getPatientWithTbProgramEnrollmentAndDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("5", tbMetadata.getTBProgram().getProgramId());
    String query =
        "SELECT pg.patient_id,pg.date_enrolled AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pg "
            + "               ON pg.patient_id = p.patient_id "
            + "WHERE  pg.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND pg.program_id = ${5} "
            + "       AND pg.date_enrolled  >= :startDate AND pg.date_enrolled <= :endDate "
            + "       AND pg.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with at least one response to the following questions in Ficha Clinica Master Card
   * during the reporting period
   *
   * <p>• Encounter Type ID = 6 <br>
   * • TUBERCULOSIS SYMPTOMS (concept_id = 23758) Answers YES (id = 1065)
   *
   * @return {@link String}
   */
  public String getPatientWithTuberculosisSymptomsAndDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23758", hivMetadata.getTBSymptomsConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND  o.concept_id = ${23758} "
            + "       AND o.value_coded IN( ${1065})  "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with at least one response to the following questions in Ficha Clinica Master Card
   * during the reporting period
   *
   * <p>• Encounter Type ID = 6 <br>
   * ACTIVE TUBERCULOSIS (obs concept_id = 23761) Answer YES (id = 1065)
   *
   * @return {@link String}
   */
  public String getPatientsActiveTuberculosisDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23761", hivMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND  o.concept_id = ${23761} "
            + "       AND o.value_coded IN( ${1065} )  "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with at least one response to the following questions in Ficha Clinica Master Card
   * during the reporting period
   *
   * <p>• TB OBSERVATIONS (obs concept id = 1766) Answers: <br>
   * a. FEVER LASTING MORE THAN 3 WEEKS (id = 1763) or <br>
   * b. WEIGHT LOSS OF MORE THAN 3 KG IN LAST MONTH (id = 1764) or <br>
   * c. NIGHTSWEATS LASTING MORE THAN 3 WEEKS ( id = 1762) or <br>
   * d. COUGH LASTING MORE THAN 3 WEEKS ( id = 1760) or <br>
   * e. ASTHENIA ( id =23760) or <br>
   * f. COHABITANT BEING TREATED FOR TB (id = 1765) or <br>
   * g. LYMPHADENOPATHY (id = 161)
   *
   * @return {@link String}
   */
  public String getPatientsWithTbObservationsAndDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1766", tbMetadata.getObservationTB().getConceptId());
    map.put("1763", tbMetadata.getFeverLastingMoraThan3Weeks().getConceptId());
    map.put("1764", tbMetadata.getWeightLossOfMoreThan3KgInLastMonth().getConceptId());
    map.put("1762", tbMetadata.getNightsWeatsLastingMoraThan3Weeks().getConceptId());
    map.put("1760", tbMetadata.getCoughLastingMoraThan3Weeks().getConceptId());
    map.put("23760", tbMetadata.getAsthenia().getConceptId());
    map.put("1765", tbMetadata.getCohabitantBeingTreatedForTB().getConceptId());
    map.put("161", tbMetadata.getLymphadenopathy().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND ( o.concept_id = ${1766} "
            + "             AND o.value_coded IN( ${1763}, ${1764}, ${1762}, ${1760},${23760}, ${1765}, ${161} ) ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * ○If Investigações - Pedidos Laboratoriais requests are marked for ‘TB LAM’, ‘GeneXpert’,
   * ‘Cultura’, ‘BK’ or ‘Raio-X’; or
   *
   * <p>• (GENEXPERT TEST (id =23723) or CULTURE TEST (id = 23774) or TB LAM TEST (id = 23951) or
   * EXAME BACILOSCOPIA (id=307) or Raio-X Torax(id=12)) for concept_id=23722 <br>
   *
   * @return {@link String}
   */
  public String getPatientsWithApplicationsForLabResearch() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("23723", tbMetadata.getTBGenexpertTestConcept().getConceptId());
    map.put("23774", tbMetadata.getCultureTest().getConceptId());
    map.put("23951", tbMetadata.getTestTBLAM().getConceptId());
    map.put("307", hivMetadata.getResultForBasiloscopia().getConceptId());
    map.put("12", tbMetadata.getXRayChest().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND o.concept_id = ${23722} "
            + "       AND o.value_coded IN( ${23723}, ${23774}, ${23951}, ${307}, ${12} ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with at least one response to the following questions in Ficha Clinica Master Card
   * during the reporting period
   *
   * <p>• TB GENEXPERT TEST (id =23723) Answer Positive (id = 703) or Negative (id = 664) <br>
   * OR • CULTURE TEST (id = 23774) Answer Positive (id = 703) or Negative (id = 664) <br>
   * OR • Test TB LAM (id = 23951) Answer Positive (id = 703) or Negative (id = 664) <br>
   * • BK Test (id = 307) Answer Positive (id = 703) or Negative (id = 664) <br>
   * • Raio X Torax (id = 12) Answer Positive (id = 703) or Negative (id = 664) or Indeterminado (id
   * = 1138) <br>
   *
   * @return {@link String}
   */
  public String getPatientsWithTbGenexpertAndDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23723", tbMetadata.getTBGenexpertTestConcept().getConceptId());
    map.put("23774", tbMetadata.getCultureTest().getConceptId());
    map.put("23951", tbMetadata.getTestTBLAM().getConceptId());
    map.put("307", hivMetadata.getResultForBasiloscopia().getConceptId());
    map.put("12", tbMetadata.getXRayChest().getConceptId());
    map.put("703", tbMetadata.getPositiveConcept().getConceptId());
    map.put("664", tbMetadata.getNegativeConcept().getConceptId());
    map.put("1138", tbMetadata.getIndeterminate().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND ( ( o.concept_id IN ( ${23723}, ${23774}, ${23951}, ${307}, ${12} ) "
            + "               AND o.value_coded IN( ${703}, ${664} ) ) OR (o.concept_id = ${12} AND o.value_coded = ${1138}) ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * • Encounter Type ID = 13 <br>
   * • EXAME BASILOSCOPIA (id=307) Answers Value_coded (664 – Negative or 703 – Positive or ???-Not
   * Found) or <br>
   * • Teste TB GENEXPERT (id=23723) Answers Value_coded (664 – Negative, 703 – Positive) or <br>
   * • CULTURE TEST (id = 23774) Answer Positive (id = 703) or Negative (id = 664) ) or <br>
   * • TEST TB LAM (id = 23951) Answer Positive (id = 703) or Negative (id = 664)
   *
   * @return {@link String}
   */
  public String getPatientsWithBaciloscopiaOrGenexpertOrCultureTestOrTestTbLamDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("23723", tbMetadata.getTBGenexpertTestConcept().getConceptId());
    map.put("23774", tbMetadata.getCultureTest().getConceptId());
    map.put("23951", tbMetadata.getTestTBLAM().getConceptId());
    map.put("703", tbMetadata.getPositiveConcept().getConceptId());
    map.put("664", tbMetadata.getNegativeConcept().getConceptId());
    map.put("307", hivMetadata.getResultForBasiloscopia().getConceptId());

    String query =
        "SELECT p.patient_id,e.encounter_datetime AS recent_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${13} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "       AND ( o.concept_id IN ( ${307}, ${23723}, ${23774}, ${23951} ) "
            + "             AND o.value_coded IN( ${703}, ${664} ) ) "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients marked as “Tratamento TB= Inicio (I) ” in Ficha Clinica Master Card between the Last
   * Positive TB Screening Date (Value of Column H) and report generation date
   *
   * <p>Encounter Type ID = 6 <br>
   * TB Treament Plan ID = 1268 <br>
   * Answer = START (value_coded 1256) <br>
   * obs_datetime >= Last Positive TB Screening Date and <= report generation date <br>
   *
   * @return {@link String}
   */
  public String getTbTreatmentStartDateFichaClinica() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1268", hivMetadata.getTBTreatmentPlanConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    String query =
        " SELECT p.patient_id, o.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN ( "
            + " SELECT positive.patient_id, MAX(positive.recent_date) as recent_date FROM ( "
            + getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND o.obs_datetime >= positiveScreening.recent_date AND o.obs_datetime <= :generationDate "
            + "       AND o.concept_id = ${1268} "
            + "       AND o.value_coded = ${1256} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB (Condicoes medicas
   * importantes) – Ficha Resumo Mastercard between the Last Positive TB Screening Date (Value of
   * Column H) and report generation date
   *
   * <p>• Encounter Type ID = 53<br>
   * Concept ID for Pulmonary TB = 421406 <br>
   * Answer = Yes (value_coded 106542) <br>
   * Obs_datetime >= Last Positive TB Screening Date and <= report generation date
   *
   * @return {@link String}
   */
  public String getPulmonaryTbDateFichaResumo() {
    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1406", hivMetadata.getOtherDiagnosis().getConceptId());
    map.put("42", tbMetadata.getPulmonaryTB().getConceptId());
    String query =
        " SELECT p.patient_id,o.obs_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN ( "
            + " SELECT positive.patient_id, MAX(positive.recent_date) as recent_date FROM ( "
            + getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${53} "
            + "       AND e.location_id = :location "
            + "       AND o.obs_datetime >= positiveScreening.recent_date AND o.obs_datetime <= :generationDate "
            + "       AND o.concept_id = ${1406} "
            + "       AND o.value_coded = ${42} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * “TB Program Enrollment Date” (Data de Admissão) In SESP – Program Enrollment between the Last
   * Positive TB Screening Date (Value of Column H) and report generation date
   *
   * @return {@link String}
   */
  public String getTbProgramEnrollmentStartDate() {
    Map<String, Integer> map = new HashMap<>();
    map.put("5", tbMetadata.getTBProgram().getProgramId());
    String query =
        " SELECT pg.patient_id,pg.date_enrolled AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN patient_program pg "
            + "               ON pg.patient_id = p.patient_id "
            + "       INNER JOIN ( "
            + " SELECT positive.patient_id, MAX(positive.recent_date) as recent_date FROM ( "
            + getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = pg.patient_id "
            + "WHERE  pg.voided = 0 "
            + "       AND p.voided = 0 "
            + "       AND pg.program_id = ${5} "
            + "       AND pg.date_enrolled  >= positiveScreening.recent_date AND pg.date_enrolled <= :generationDate "
            + "       AND pg.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * Patients with at least one response to the following questions in Ficha Clinica Master Card
   * between the Last Positive TB Screening Date (Value of Column H) and report generation date
   *
   * <p>Encounter Type ID = 6 <br>
   * TUBERCULOSIS SYMPTOMS (concept_id = 23761) Answers YES (id = 1065)
   *
   * @return {@link String}
   */
  public String getActiveTuberculosisDateFichaClinica() {
    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23761", hivMetadata.getActiveTBConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        " SELECT p.patient_id,e.encounter_datetime AS start_date "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN ( "
            + " SELECT positive.patient_id, MAX(positive.recent_date) as recent_date FROM ( "
            + getTbPositiveScreeningFromSourcesQuery()
            + " ) positive GROUP BY positive.patient_id ) positiveScreening ON positiveScreening.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = ${6} "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_datetime >= positiveScreening.recent_date AND e.encounter_datetime <= :generationDate "
            + "       AND  o.concept_id = ${23761} "
            + "       AND o.value_coded IN( ${1065} )  "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);
    return sb.replace(query);
  }

  /**
   * @see #getTbPositiveScreeningFromSourcesQuery()
   * @return {@link String}
   */
  public String getAntiTbTreatmentFromSourcesQuery() {
    return new EptsQueriesUtil()
        .unionBuilder(getTbTreatmentStartDateFichaClinica())
        .union(getTbProgramEnrollmentStartDate())
        .union(getPulmonaryTbDateFichaResumo())
        .union(getActiveTuberculosisDateFichaClinica())
        .buildQuery();
  }

  /**
   * <b>Generate one union separeted query based on the given queries</b>
   *
   * @return {@link String}
   */
  public String getTbPositiveScreeningFromSourcesQuery() {
    return new EptsQueriesUtil()
        .unionBuilder(getPatientWithTbProgramEnrollmentAndDate())
        .union(getPatientWithPulmonaryTbdDate())
        .union(getPatientMarkedAsTbTreatmentStartAndDate())
        .union(getPatientWithTuberculosisSymptomsAndDate())
        .union(getPatientsActiveTuberculosisDate())
        .union(getPatientsWithTbObservationsAndDate())
        .union(getPatientsWithApplicationsForLabResearch())
        .union(getPatientsWithTbGenexpertAndDate())
        .union(getPatientsWithBaciloscopiaOrGenexpertOrCultureTestOrTestTbLamDate())
        .buildQuery();
  }
}
