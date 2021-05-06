package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoMensalTransferredOutCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonCohortQueries {

  private HivMetadata hivMetadata;

  private TbMetadata tbMetadata;

  @Autowired
  public CommonCohortQueries(HivMetadata hivMetadata, TbMetadata tbMetadata) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
  }

  /**
   * <b>Description:</b> Number of patients who are on TB treatment
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patients who in patient clinical record for ART in follow-up, adults and children
   * <b>(encounterType_id = 6 or 9)</b> have a TB Start Date <b>((obs concept_id = 1113)</b> >
   * (reporting_end_date - 6 months) and <= reporting_end_date)
   *
   * <p>Have TB treatment End Date <b>((concept_id = 6120)</b> null or > reporting_end_date)
   *
   * <p>Enrolled in TB program <b>(program_id = 5 and patient_state_id =6269)</b>, with Start_date
   * >= (reporting end date - 6 months) and <= reporting end date and endDate is null or is >
   * reporting end date
   *
   * <p>Active TB <b>(concept_id = 23761)</b> value_coded "Yes <b>(concept_id = 1065)</b>" or
   * treatment plan in ficha clinica MasterCard and encounter_datetime between reporting_end_date
   *
   * <p>Marked LAST TB Treatment Plan <b>(concept_id = 1268)</b> with valued_coded "start Drugs"
   * <b>(concept_id = 1256)</b> or continue regimen (concept_id = 1258)</b> and LAST Date <b>(obs
   * datetime)>= (reporting_end_date -6 months) and <= reporting_end_date</b>
   *
   * <p>Pulmonary TB <b>(obs concept_id = 42)</b> with value_coded "Yes" in ficha resumo -
   * mastercard <b>(encounterType_id = 53)</b> and <b>(obs_datetime) >=(reporting_end_date - 6
   * monts) and < = reporting_end_date
   *
   * <p></b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsOnTbTreatment() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsOnTbTreatment");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        CommonQueries.getPatientsOnTbTreatmentQuery(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getTBDrugStartDateConcept().getConceptId(),
            hivMetadata.getTBDrugEndDateConcept().getConceptId(),
            hivMetadata.getTBProgram().getProgramId(),
            hivMetadata.getPatientActiveOnTBProgramWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getActiveTBConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTBTreatmentPlanConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            tbMetadata.getPulmonaryTB().getConceptId()));

    return cd;
  }

  /**
   * <b>Description:</b> 15 MOH Transferred-in patients TARV
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMohTransferredInPatients() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    addParameters(cd);

    EptsTransferredInCohortDefinition2 transferredInCurrentPeriod =
        new EptsTransferredInCohortDefinition2();
    addParameters(transferredInCurrentPeriod);
    transferredInCurrentPeriod.addArtProgram(EptsTransferredInCohortDefinition2.ARTProgram.TARV);

    EptsTransferredInCohortDefinition2 transferredInPreviousMonth =
        new EptsTransferredInCohortDefinition2();
    transferredInPreviousMonth.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    transferredInPreviousMonth.addParameter(new Parameter("location", "Location", Location.class));
    transferredInPreviousMonth.addArtProgram(EptsTransferredInCohortDefinition2.ARTProgram.TARV);

    ResumoMensalTransferredOutCohortDefinition transferredOutPreviousMonth =
        getMohTransferredOutPatientsByEndOfPeriod();

    cd.addSearch("current", mapStraightThrough(transferredInCurrentPeriod));
    String byEndOfPreviousExclusive = "onOrBefore=${onOrAfter-1d},location=${location}";
    cd.addSearch("previous", map(transferredInPreviousMonth, byEndOfPreviousExclusive));
    String byEndOfPrevious = "onOrBefore=${onOrAfter},location=${location}";
    cd.addSearch("transferredOut", map(transferredOutPreviousMonth, byEndOfPrevious));
    cd.setCompositionString("current NOT (previous NOT transferredOut)");

    return cd;
  }

  /**
   * <b>Description: 16 -</b> MOH Transferred-out patients by end of the reporting period (Last
   * State)
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * 1 - Patients registered in “Ficha Clinica-MasterCard” <b>(encounter_id = 6)</b> with LAST
   * “Patient State” <b>(concept_id = 6273)</b> equal to “Transferred-Out” <b>(concept_id =
   * 1706)</b> AND <b>encounter <= endDate</b> OR
   *
   * <p>2 - Registered in encounter “Ficha Resumo-MasterCard” (encounter id 53) with LAST “Patient
   * State” <b>(Concept ID 6272)</b> equal to Transferred-Out” AND <b>obs_datetime <=endDate</b> OR
   *
   * <p>3 - Registered as transferred-out in LAST Patient Program State during the reporting period
   * <b>Patient_program.program_id =2 = ART SERVICE</b> AND <b>Patient_State.state = 7
   * (Transferred-out)</b> OR <b>Patient_State.end_date = max(endDate)</b>
   *
   * <p>Except all patients who after the most recent date from <b>1 to 3</b> have a drugs pick up
   * or consultation <b>EncounterType ID= 6, 9, 18</b> and  encounter_datetime> the most recent date
   * and <=endDate or Encounter Type ID = 52 and “Data de Levantamento” <b>(Concept Id 23866
   * value_datetime)</b> > the most recent date and <= endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public ResumoMensalTransferredOutCohortDefinition getMohTransferredOutPatientsByEndOfPeriod() {
    ResumoMensalTransferredOutCohortDefinition transferredOutPreviousMonth =
        new ResumoMensalTransferredOutCohortDefinition();
    transferredOutPreviousMonth.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    transferredOutPreviousMonth.addParameter(new Parameter("location", "Location", Location.class));
    transferredOutPreviousMonth.setMaxDates(true);
    return transferredOutPreviousMonth;
  }

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * <b>Description: 18 and 19 -</b> MOH MQ Females on Condition
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Get all female patients in: Pregnant or Breastfeeding
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMohMQPatientsOnCondition(
      Boolean female,
      Boolean transfIn,
      String occurType,
      EncounterType encounterType,
      Concept question,
      List<Concept> answers,
      Concept question2,
      List<Concept> answers2) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Nutritional Classification");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    List<Integer> answerIds = new ArrayList<>();
    List<Integer> answerIds2 = new ArrayList<>();

    for (Concept concept : answers) {
      answerIds.add(concept.getConceptId());
    }

    if (question2 != null && answers2 != null) {
      for (Concept concept : answers2) {
        answerIds2.add(concept.getConceptId());
      }
    }
    String query = "";
    if (occurType == "last") {

      query +=
          "SELECT p.person_id FROM person p "
              + " INNER JOIN  encounter e ON e.patient_id = p.person_id "
              + " INNER JOIN  obs o ON o.encounter_id = e.encounter_id "
              + " INNER JOIN (SELECT p.person_id, MAX(e.encounter_datetime) AS encounter_datetime"
              + " FROM person p INNER JOIN encounter e ";

    } else if (occurType == "first") {
      query +=
          "SELECT p.person_id FROM person p "
              + " INNER JOIN  encounter e ON e.patient_id = p.person_id "
              + " INNER JOIN  obs o ON o.encounter_id = e.encounter_id "
              + " INNER JOIN (SELECT p.person_id, MIN(e.encounter_datetime) AS encounter_datetime"
              + " FROM person p INNER JOIN encounter e ";

    } else if (occurType == "once") {
      query += "SELECT p.person_id FROM person p INNER JOIN encounter e ";
    }

    query +=
        "ON p.person_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id ";
    if (transfIn) {
      query += "INNER JOIN obs o2 " + "ON e.encounter_id = o2.encounter_id ";
    }
    if (occurType == "last" || occurType == "first") {
      query +=
          "WHERE e.location_id = :location AND e.encounter_type = ${encounterType} "
              + "AND o.concept_id = ${question}  ";
    } else {
      query +=
          "WHERE e.location_id = :location AND e.encounter_type = ${encounterType} "
              + "AND o.concept_id = ${question}  "
              + "AND o.value_coded in (${answers}) ";
    }
    if (transfIn) {
      query +=
          "AND o2.concept_id = ${question2}  "
              + "AND o2.value_coded in (${answers2}) AND o2.voided = 0 ";
    } else if (female) {
      query += "AND p.gender = 'F' ";
    }
    query +=
        "AND o.obs_datetime >= :startDate AND o.obs_datetime <= :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 ";

    if (occurType == "last" || occurType == "first") {
      query +=
          "GROUP BY p.person_id) list ON p.person_id = list.person_id "
              + " WHERE  e.encounter_datetime = list.encounter_datetime "
              + " AND e.location_id = :location  AND e.encounter_type = ${encounterType} "
              + " AND o.concept_id = ${question} AND e.voided = 0 "
              + " AND o.voided = 0  AND o.value_coded in (${answers}) ";
    }

    Map<String, String> map = new HashMap<>();
    map.put("encounterType", String.valueOf(encounterType.getEncounterTypeId()));
    // Just convert the conceptId to String so it can be added to the map
    map.put("question", String.valueOf(question.getConceptId()));
    map.put("answers", StringUtils.join(answerIds, ","));

    if (question2 != null && answers2 != null) {
      map.put("question2", String.valueOf(question2.getConceptId()));
      map.put("answers2", StringUtils.join(answerIds2, ","));
    }

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Transferred Out Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Select all patients registered in encounter “Ficha Resumo-MasterCard” (encounter type 53) with
   * LAST “Patient State” (PT:“Estado de Permanência”) (Concept ID 6272) equal to “Transferred Out”
   * (PT: “Transferido Para”) (Concept ID 1706) AND obs_datetime <=endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTranferredOutPatients() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients From Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        " SELECT patient_id "
            + "FROM   (SELECT transferout.patient_id, "
            + "               Max(transferout.transferout_date) transferout_date "
            + "        FROM   (SELECT p.patient_id, "
            + "                       Max(e.encounter_datetime) AS transferout_date "
            + "                FROM   patient p "
            + "                       JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                       JOIN obs o "
            + "                         ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${6} "
            + "                       AND e.encounter_datetime <= :revisionEndDate "
            + "                       AND o.voided = 0 "
            + "                       AND o.concept_id = ${6273} "
            + "                       AND o.value_coded = ${1706} "
            + "                GROUP  BY p.patient_id "
            + "                UNION "
            + "                SELECT p.patient_id, "
            + "                       Max(o.obs_datetime) AS transferout_date "
            + "                FROM   patient p "
            + "                       JOIN encounter e "
            + "                         ON p.patient_id = e.patient_id "
            + "                       JOIN obs o "
            + "                         ON e.encounter_id = o.encounter_id "
            + "                WHERE  p.voided = 0 "
            + "                       AND e.voided = 0 "
            + "                       AND e.location_id = :location "
            + "                       AND e.encounter_type = ${53} "
            + "                       AND o.obs_datetime BETWEEN :startDate AND :revisionEndDate "
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
            + "              e.encounter_datetime > transferout_date "
            + "                                                 AND "
            + "              e.encounter_datetime <= :revisionEndDate "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                 JOIN encounter e "
            + "                                                   ON p.patient_id = "
            + "                                                      e.patient_id "
            + "                                                 JOIN obs o "
            + "                                                   ON e.encounter_id = "
            + "                                                      o.encounter_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                                 AND e.voided = 0 "
            + "                                                 AND e.encounter_type = ${52} "
            + "                                                 AND e.location_id = :location "
            + "                                                 AND o.concept_id = ${23866} "
            + "                                                 AND o.value_datetime > "
            + "                                                     transferout_date "
            + "                                                 AND o.value_datetime <= "
            + "                                                     :revisionEndDate)  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Last Clinical Consultation Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Select all patients with Last Clinical Consultation (encounter type 6, encounter_datetime)
   * occurred during the period (encounter_datetime > endDateInclusion and <= endDateRevision)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsLastClinicalConsultation() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Last Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        " SELECT  "
            + "     patient_id  "
            + " FROM  "
            + "     (SELECT   "
            + "         p.patient_id, MAX(e.encounter_datetime) last_clinical  "
            + "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  "
            + "             AND e.encounter_type = ${6}  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "     GROUP BY p.patient_id) AS list";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Patients on Treatments for 6 Months
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B2 - Select all patients who have the FIRST “LINHA TERAPEUTICA” (Concept Id 21151) recorded in
   * Ficha Clinica (encounter type 6, encounter_datetime) with value coded “PRIMEIRA LINHA” (concept
   * id 21150) before the the “Last Clinical Consultation Date” (last encounter_datetime from B1)
   * and at least for 6 months
   *
   * <p>B3 - Select all patients who have the MOST RECENT “ALTERNATIVA A LINHA - 1a LINHA” (Concept
   * Id 23898, obs_datetime) recorded in Ficha Resumo (encounter type 53) with any value coded (not
   * null) before the “Last Clinical Consultation Date” (last encounter_datetime from B1) and at
   * least for 6 months
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsOnTreatmentFor6Months(
      Boolean masterCard,
      EncounterType lastClinicalEncounter,
      EncounterType treatmentEncounter,
      Concept treatmentConcept,
      List<Concept> treatmentValueCoded) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients on treatment for 6 months from last clinical visit");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    List<Integer> answerIds = new ArrayList<>();

    for (Concept concept : treatmentValueCoded) {
      answerIds.add(concept.getConceptId());
    }
    String query = "";
    if (masterCard) {
      query =
          " SELECT "
              + " max_table.patient_id "
              + " FROM "
              + " ( "
              + " SELECT "
              + " p.patient_id, "
              + " MAX(o.obs_datetime) AS max_obs_datetime "
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
              + " MAX(e.encounter_datetime) last_visit "
              + " FROM "
              + " patient p "
              + " INNER JOIN "
              + " encounter e "
              + " ON e.patient_id = p.patient_id "
              + " WHERE "
              + " p.voided = 0 "
              + " AND e.voided = 0 "
              + " AND e.encounter_type = ${lastClinicalEncounter} "
              + " AND e.location_id = :location "
              + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
              + " GROUP BY p.patient_id "
              + " ) AS clinical ON clinical.patient_id = p.patient_id "
              + " WHERE "
              + " p.voided = 0 "
              + " AND e.voided = 0 "
              + " AND o.voided = 0 "
              + " AND e.encounter_type = ${treatmentEncounter} "
              + " AND e.location_id = :location "
              + " AND o.concept_id = ${treatmentConcept} "
              + " AND o.value_coded IS NOT NULL "
              + " GROUP BY p.patient_id "
              + " ) max_table INNER JOIN "
              + " ( "
              + " SELECT "
              + " p.patient_id, "
              + " MAX(e.encounter_datetime) last_visit "
              + " FROM "
              + " patient p "
              + " INNER JOIN "
              + " encounter e "
              + " ON e.patient_id = p.patient_id "
              + " WHERE "
              + " p.voided = 0 "
              + " AND e.voided = 0 "
              + " AND e.encounter_type = ${lastClinicalEncounter} "
              + " AND e.location_id = :location "
              + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
              + " GROUP BY p.patient_id "
              + " ) "
              + " AS clinc_max ON max_table.patient_id = clinc_max.patient_id "
              + " WHERE "
              + " max_table.max_obs_datetime <= clinc_max.last_visit "
              + " AND max_table.max_obs_datetime <= DATE_SUB(clinc_max.last_visit, INTERVAL 6 MONTH) ";
    } else {
      query =
          " SELECT treatment_line.patient_id FROM( "
              + " SELECT p.patient_id, MIN(e.encounter_datetime) "
              + " FROM patient p "
              + " INNER JOIN encounter e ON e.patient_id = p.patient_id  "
              + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
              + " INNER JOIN "
              + " (SELECT "
              + " p.patient_id, MAX(e.encounter_datetime) last_visit "
              + " FROM patient p "
              + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
              + " WHERE "
              + " p.voided = 0 AND e.voided = 0 "
              + " AND e.encounter_type = ${lastClinicalEncounter} "
              + " AND e.location_id = :location "
              + " AND e.encounter_datetime BETWEEN :startDate AND :endDate "
              + " GROUP BY p.patient_id "
              + ") AS clinical ON clinical.patient_id = p.patient_id "
              + " WHERE "
              + " p.voided = 0 AND e.voided = 0 "
              + " AND o.voided = 0 "
              + " AND e.encounter_type = ${treatmentEncounter} "
              + " AND e.location_id = :location "
              + " AND o.concept_id = ${treatmentConcept} "
              + " AND o.value_coded IN (${treatmentValueCoded) "
              + " AND DATE(e.encounter_datetime) < DATE(clinical.last_visit) "
              + " AND DATE(e.encounter_datetime) <= DATE_SUB(clinical.last_visit,INTERVAL 6 MONTH) "
              + "  GROUP BY p.patient_id) treatment_line ";
    }

    Map<String, String> map = new HashMap<>();
    map.put("lastClinicalEncounter", String.valueOf(lastClinicalEncounter.getEncounterTypeId()));
    map.put("treatmentEncounter", String.valueOf(treatmentEncounter.getEncounterTypeId()));
    map.put("treatmentConcept", String.valueOf(treatmentConcept.getConceptId()));
    map.put("treatmentValueCoded", StringUtils.join(answerIds, ","));

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b>B2-NEW MOH Patients on Treatments for 6 Months
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B2 NEW - B2New - Select all patients with “LINHA TERAPEUTICA” (Concept Id 21151) equal to
   * “PRIMEIRA LINHA” (concept id 21150) recorded in the Last Clinical Consultation (encounter type
   * 6, encounter_datetime) occurred during the period (encounter_datetime >= startDateInclusion and
   * <= endDateRevision) and Last Clinical Consultation Date (encounter_datetime) minus “Patient ART
   * Start Date” (Concept Id 1190, value_datetime) recorded in Ficha Resumo (encounter type 53,
   * encounter_datetime) >= 6 months)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithFirstTherapeuticLineOnLastClinicalEncounterB2NEW() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant Or Breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(
        new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("21151", hivMetadata.getTherapeuticLineConcept().getConceptId());
    map.put("21150", hivMetadata.getFirstLineConcept().getConceptId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());

    String query =
        " SELECT "
            + "     pa.patient_id "
            + " FROM "
            + "     patient pa "
            + "          JOIN "
            + "     encounter enc ON enc.patient_id = pa.patient_id "
            + "          JOIN "
            + "     obs ob ON ob.encounter_id = enc.encounter_id "
            + "          JOIN "
            + "      (SELECT "
            + "          p.patient_id, filtered.encounter_datetime "
            + "      FROM "
            + "          patient p "
            + "      JOIN encounter e ON e.patient_id = p.patient_id "
            + "      JOIN obs o ON o.encounter_id = e.encounter_id "
            + "      INNER JOIN (SELECT "
            + "          p.patient_id, "
            + "              MAX(e.encounter_datetime) AS encounter_datetime "
            + "      FROM "
            + "          patient p "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "      WHERE "
            + "          e.encounter_type = ${6} AND p.voided = 0 "
            + "              AND e.voided = 0 "
            + "             AND e.location_id = :location "
            + "              AND e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
            + "      GROUP BY p.patient_id) filtered ON filtered.patient_id = p.patient_id "
            + "      WHERE "
            + "          e.encounter_datetime = filtered.encounter_datetime "
            + "              AND e.location_id = :location "
            + "              AND o.concept_id = ${21151} "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "              AND o.value_coded = ${21150} "
            + "              AND e.encounter_type = ${6}) first_line ON first_line.patient_id = pa.patient_id "
            + "          INNER JOIN "
            + "      (SELECT "
            + "          p.patient_id, MAX(e.encounter_datetime) last_visit "
            + "      FROM "
            + "          patient p "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "      WHERE "
            + "          p.voided = 0 AND e.voided = 0 "
            + "              AND e.encounter_type = ${6} "
            + "              AND e.location_id = :location "
            + "              AND e.encounter_datetime BETWEEN :startDate AND :revisionEndDate "
            + "      GROUP BY p.patient_id) AS last_clinical ON last_clinical.patient_id = pa.patient_id "
            + "          INNER JOIN "
            + "      (SELECT "
            + "          p.patient_id, o.value_datetime AS arv_date "
            + "      FROM "
            + "          patient p "
            + "      INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "      INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "      WHERE "
            + "          o.concept_id = ${1190} "
            + "              AND e.encounter_type = ${53} "
            + "              AND e.location_id = :location "
            + "              AND p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0) arv_start_date ON arv_start_date.patient_id = pa.patient_id "
            + "          AND DATE(arv_start_date.arv_date) <= DATE_SUB(last_clinical.last_visit, INTERVAL 6 MONTH) "
            + "  GROUP BY pa.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Patients to Exclude From Treatment in 6 Months
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B2E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who have
   * “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA LINHA”(Concept
   * id 21150) and encounter_datetime > first “LINHA TERAPEUTICA” = “PRIMEIRA LINHA” (from B2) and
   * <= “Last Clinical Consultation” (last encounter_datetime from B1)
   *
   * <p>B3E - Exclude all patients from Ficha Clinica (encounter type 6, encounter_datetime) who
   * have “LINHA TERAPEUTICA”(Concept id 21151) with value coded DIFFERENT THAN “PRIMEIRA
   * LINHA”(Concept id 21150) and encounter_datetime > the most recent “ALTERNATIVA A LINHA - 1a
   * LINHA” (from B3) and <= “Last Clinical Consultation” (last encounter_datetime from B1)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsToExcludeFromTreatmentIn6Months(
      Boolean masterCard,
      EncounterType clinicalEncounter,
      EncounterType treatmentEncounter,
      Concept treatmentConcept,
      List<Concept> treatmentValueCoded,
      EncounterType exclusionEncounter,
      Concept exclusionConcept,
      List<Concept> exclusionValueCoded) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients to exclude in treatment in the last 6 months");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    List<Integer> answerIds = new ArrayList<>();
    List<Integer> answerIds2 = new ArrayList<>();

    for (Concept concept : treatmentValueCoded) {
      answerIds.add(concept.getConceptId());
    }

    for (Concept concept : exclusionValueCoded) {
      answerIds2.add(concept.getConceptId());
    }

    String query =
        " SELECT  "
            + "     p.patient_id  "
            + " FROM  "
            + "     patient p  "
            + "         INNER JOIN  "
            + "     encounter e ON e.patient_id = p.patient_id  "
            + "         INNER JOIN  "
            + "     obs o ON o.encounter_id = e.encounter_id  "
            + "         INNER JOIN  "
            + "     (SELECT   ";
    if (masterCard) {
      query += "         p.patient_id, MAX(o.obs_datetime) the_time ";
    } else {
      query += "         p.patient_id, MIN(e.encounter_datetime) the_time  ";
    }

    query +=
        "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + "     INNER JOIN (SELECT   "
            + "         p.patient_id, MAX(e.encounter_datetime) last_visit  "
            + "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  "
            + "             AND e.encounter_type = ${clinicalEncounter}  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "     GROUP BY p.patient_id) AS clinical ON clinical.patient_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  "
            + "             AND o.voided = 0  "
            + "             AND e.encounter_type = ${treatmentEncounter}  "
            + "             AND e.location_id = :location  "
            + "             AND o.concept_id = ${treatmentConcept}  ";
    if (masterCard) {
      query +=
          "             AND o.value_coded IS NOT NULL  "
              + "             AND DATE(o.obs_datetime) <= DATE(clinical.last_visit)  "
              + "             AND DATE(o.obs_datetime) <= DATE_SUB(clinical.last_visit,INTERVAL 6 MONTH)  "; // check
      // other
      // queries for time they use
    } else {
      query +=
          "             AND o.value_coded IN (${treatmentValueCoded})  "
              + "             AND DATE(e.encounter_datetime) < DATE(clinical.last_visit)  "
              + "             AND DATE(e.encounter_datetime) <= DATE_SUB(clinical.last_visit,INTERVAL 6 MONTH)  "; // check other queries for time they use
    }
    query +=
        "     GROUP BY p.patient_id) treatment_line ON treatment_line.patient_id = p.patient_id  "
            + "     INNER JOIN (SELECT   "
            + "         p.patient_id, MAX(e.encounter_datetime) last_visit  "
            + "     FROM  "
            + "         patient p  "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + "     WHERE  "
            + "         p.voided = 0 AND e.voided = 0  "
            + "             AND e.encounter_type = ${clinicalEncounter}  "
            + "             AND e.location_id = :location  "
            + "             AND e.encounter_datetime BETWEEN :startDate AND :endDate  "
            + "     GROUP BY p.patient_id) AS clinical ON clinical.patient_id = p.patient_id  "
            + " WHERE  "
            + "     p.voided = 0 AND e.voided = 0  "
            + "         AND o.voided = 0  "
            + "         AND e.location_id = :location  "
            + "         AND e.encounter_type = ${exclusionEncounter} "
            + "         AND o.concept_id = ${exclusionConcept}  "
            + "         AND o.value_coded <> ${exclusionValueCoded}  "
            + "         AND DATE(e.encounter_datetime) > DATE(treatment_line.the_time)  "
            + "         AND DATE(e.encounter_datetime) <= DATE(clinical.last_visit)"
            + "       GROUP BY patient_id";

    Map<String, String> map = new HashMap<>();
    map.put("clinicalEncounter", String.valueOf(clinicalEncounter.getEncounterTypeId()));
    map.put("treatmentEncounter", String.valueOf(treatmentEncounter.getEncounterTypeId()));
    map.put("treatmentConcept", String.valueOf(treatmentConcept.getConceptId()));
    map.put("treatmentValueCoded", StringUtils.join(answerIds, ","));
    map.put("exclusionEncounter", String.valueOf(exclusionEncounter.getEncounterTypeId()));
    map.put("exclusionConcept", String.valueOf(exclusionConcept.getConceptId()));
    map.put("exclusionValueCoded", StringUtils.join(answerIds2, ","));

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Last Clinical Consultation Query
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Select all patients with Last Clinical Consultation (encounter type 6, encounter_datetime)
   * occurred during the period (encounter_datetime > endDateInclusion and <= endDateRevision)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsAgeOnLastClinicalConsultationDate(
      Integer minAge, Integer maxAge) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients Age at Last Ficha Clinica");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("minAge", minAge);
    map.put("maxAge", maxAge);

    String query =
        " SELECT  "
            + "     p.person_id  "
            + " FROM  "
            + "     person p  "
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
            + "     GROUP BY p.patient_id) clinical ON clinical.patient_id = p.person_id  "
            + " WHERE  ";
    if (minAge != null && maxAge != null) {
      query +=
          "     TIMESTAMPDIFF(YEAR, p.birthdate, clinical.last_visit) >= ${minAge}  "
              + "         AND   "
              + "   TIMESTAMPDIFF(YEAR, p.birthdate, clinical.last_visit) <= ${maxAge}; ";
    } else if (minAge == null && maxAge != null) {
      query += "   TIMESTAMPDIFF(YEAR, p.birthdate, clinical.last_visit) <= ${maxAge}; ";
    } else if (minAge != null && maxAge == null) {
      query += "   TIMESTAMPDIFF(YEAR, p.birthdate, clinical.last_visit) >= ${minAge};  ";
    }

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MOH Patients With Viral Load Request or Result Between Last Clinical
   * Consultations
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * B4E - Exclude all patients with “Carga Viral” (Concept id 856, value_numeric not null)
   * registered in Ficha Clinica (encounter type 6, encounter_datetime) or Ficha Resumo (encounter
   * type 53, obs_datetime) during the last 12 months from the Last Clinical Consultation, i.e, at
   * least one “Carga Viral” encounter_datetime between “Last Clinical Consultation”-12months (last
   * encounter_datetime-12months from B1) and “Last Clinical Consultation” (last encounter_datetime
   * from B1).
   *
   * <p>B5E- exclude all patients with concept “PEDIDO DE INVESTIGACOES LABORATORIAIS” (Concept Id
   * 23722) and value coded “HIV CARGA VIRAL” (Concept Id 856) registered in Ficha Clinica
   * (encounter type 6) during the last 3 months from the Last Clinical Consultation (at least one
   * “Pedido de Carga Viral” encounter_datetime between “Last Clinical Consultation”-3months and
   * “Last Clinical Consultation” (last encounter_datetime from B1).
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPatientsWithVLRequestorResultBetweenClinicalConsultations(
      Boolean b4e, Boolean b5e, Integer period) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients to exclude with VL request or results between last clinical visits");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    String query =
        " SELECT  "
            + "     p.patient_id  "
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
            + "     GROUP BY p.patient_id) clinical ON clinical.patient_id = p.patient_id  "
            + " WHERE  "
            + "     p.voided = 0 AND e.voided = 0  "
            + "         AND o.voided = 0  "
            + "         AND e.location_id = :location  ";
    if (b4e) {
      query +=
          "         AND ((((concept_id = ${856} AND o.value_numeric IS NOT NULL)  "
              + "               OR (concept_id = ${1305}  AND o.value_coded IS NOT NULL)) ";
    } else if (b5e) {
      query += "         AND (concept_id = ${23722}  " + "         AND o.value_coded =  ${856}  ";
    }
    if (b5e) {
      query +=
          "         AND e.encounter_type = ${6}  "
              + "         AND e.encounter_datetime >= date_add(clinical.last_visit, INTERVAL ${period} MONTH) "
              + "         AND e.encounter_datetime < clinical.last_visit)  ";
    } else {
      query +=
          "         AND e.encounter_type = ${6}  "
              + "         AND DATE(e.encounter_datetime) BETWEEN DATE_SUB(clinical.last_visit,  "
              + "         INTERVAL ${period} MONTH) AND clinical.last_visit)  ";
    }
    if (b4e) {
      query +=
          "      OR   "
              + "         (concept_id = ${856}  "
              + "         AND o.value_numeric IS NOT NULL "
              + "         AND e.encounter_type = ${53}  "
              + "         AND o.obs_datetime BETWEEN DATE_SUB(clinical.last_visit,  "
              + "         INTERVAL 12 MONTH) AND clinical.last_visit))";
    }

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("23722", hivMetadata.getApplicationForLaboratoryResearch().getConceptId());
    map.put("period", period);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MQ-MOH Query For pregnant or Breastfeeding patients
   *
   * <p><b>Technical Specs</b>
   * <li>A - Select all female patients who are pregnant as following: all patients registered in
   *     Ficha Resumo (encounter type=53) with “Gestante”(concept_id 1982) value coded equal to
   *     “Yes” (concept_id 1065) and sex=Female
   * <li>B - Select all female patients who are breastfeeding as following: all patients registered
   *     in Ficha Resumo (encounter type=53) with “Lactante”(concept_id 6332) value coded equal to
   *     “Yes” (concept_id 1065) and sex=Female
   *
   * @param question
   * @param answer
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getMOHPregnantORBreastfeeding(int question, int answer) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant Or Breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("question", question);
    map.put("answer", answer);

    String query =
        "SELECT p.person_id  "
            + "FROM   person p  "
            + "       JOIN encounter e  "
            + "         ON e.patient_id = p.person_id  "
            + "       JOIN obs o  "
            + "         ON o.encounter_id = e.encounter_id  "
            + "            AND encounter_type = ${53}  "
            + "            AND o.concept_id = ${question}  "
            + "            AND o.value_coded = ${answer}  "
            + "            AND e.location_id = :location  "
            + "            AND p.gender = 'F'  "
            + "            AND e.voided = 0  "
            + "            AND o.voided = 0  "
            + "            AND p.voided = 0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> MQ- New Query For pregnant or Breastfeeding patients
   * <li>C - All female patients registered as “Pregnant” (concept_id 1982, value_coded equal to
   *     concept_id 1065) on Last Clinical Consultation (encounter type 6, encounter_datetime)
   *     occurred during the revision period (encounter_datetime >= startDateInclusion and <=
   *     endDateRevision)
   * <li>C - All female patients registered as “Pregnant” (concept_id 6332, value_coded equal to
   *     concept_id 1065) on Last Clinical Consultation (encounter type 6, encounter_datetime)
   *     occurred during the revision period (encounter_datetime >= startDateInclusion and <=
   *     endDateRevision)
   *
   * @param question
   * @param answer
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getNewMQPregnantORBreastfeeding(int question, int answer) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant Or Breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Date.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("question", question);
    map.put("answer", answer);

    String query =
        " SELECT p.person_id p "
            + "FROM   person p "
            + "       JOIN encounter e "
            + "         ON e.patient_id = p.person_id "
            + "       JOIN obs o "
            + "         ON o.encounter_id = e.encounter_id "
            + "       JOIN (SELECT p.patient_id, "
            + "                    Max(e.encounter_datetime) AS clinical_encounter "
            + "             FROM   patient p "
            + "                    JOIN encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "                    JOIN obs o "
            + "                      ON o.encounter_id = e.encounter_id "
            + "             WHERE  e.encounter_type = ${6} "
            + "                    AND e.location_id = :location "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND p.voided = 0 "
            + "                    AND e.encounter_datetime BETWEEN "
            + "                        :startDate AND :endDate "
            + "             GROUP  BY p.patient_id) last_clinical "
            + "         ON last_clinical.patient_id = p.person_id "
            + " WHERE  last_clinical.clinical_encounter = e.encounter_datetime "
            + "       AND e.voided = 0"
            + "       AND p.voided = 0"
            + "       AND o.voided = 0"
            + "       AND p.gender = 'F' "
            + "       AND e.encounter_type = 6"
            + "       AND o.concept_id = ${question} "
            + "       AND o.value_coded = ${answer}  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }
}
