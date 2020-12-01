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
      EncounterType encounterType,
      Concept question,
      List<Concept> answers,
      Concept question2,
      List<Concept> answers2) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients with Nutritional Calssification");
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
    String query =
        "SELECT p.person_id FROM person p INNER JOIN encounter e "
            + "ON p.person_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id ";
    if (transfIn) {
      query += "INNER JOIN obs o2 " + "ON e.encounter_id = o2.encounter_id ";
    }
    query +=
        "WHERE e.location_id = :location AND e.encounter_type = ${encounterType} "
            + "AND o.concept_id = ${question}  "
            + "AND o.value_coded in (${answers}) ";
    if (transfIn) {
      query +=
          "AND o2.concept_id = ${question2}  "
              + "AND o2.value_coded in (${answers2}) AND o2.voided = 0 ";
    } else if (female) {
      query += "AND p.gender = 'F' ";
    }
    query +=
        "AND e.encounter_datetime >= :startDate AND e.encounter_datetime <= :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

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
}
