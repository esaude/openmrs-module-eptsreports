package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.HashSet;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = EptsTransferredInCohortDefinition.class)
public class EptsTransferredInCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  private HivMetadata hivMetadata;

  private EvaluationService evaluationService;

  @Autowired
  public EptsTransferredInCohortDefinitionEvaluator(
      HivMetadata hivMetadata, EvaluationService evaluationService) {
    this.hivMetadata = hivMetadata;
    this.evaluationService = evaluationService;
  }

  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {

    EptsTransferredInCohortDefinition cd = (EptsTransferredInCohortDefinition) cohortDefinition;
    EvaluatedCohort ret = new EvaluatedCohort(null, cd, context);

    SqlQueryBuilder q = new SqlQueryBuilder();
    q.append("SELECT p.patient_id ");
    q.append("FROM   patient p ");
    q.append("       JOIN encounter e ");
    q.append("         ON p.patient_id = e.patient_id ");
    q.append("       JOIN obs transf ");
    q.append("         ON transf.encounter_id = e.encounter_id ");
    q.append("       JOIN obs type ");
    q.append("         ON type.encounter_id = e.encounter_id ");
    q.append("WHERE  p.voided = 0 ");
    q.append("       AND e.voided = 0 ");
    q.append("       AND e.encounter_type = :mastercard ");
    q.append("       AND e.location_id = :location ");
    if (cd.getOnOrAfter() == null) {
      q.append("     AND e.encounter_datetime < :onOrBefore ");
    } else if (cd.getOnOrBefore() == null) {
      q.append("     AND e.encounter_datetime > :onOrAfter ");
    } else {
      q.append("     AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    }

    q.append("       AND transf.voided = 0 ");
    q.append("       AND transf.concept_id = :transferFromOther ");
    q.append("       AND transf.value_coded = :yes ");
    if (cd.getOnOrAfter() == null) {
      q.append("     AND transf.obs_datetime <= :onOrBefore ");
    } else if (cd.getOnOrBefore() == null) {
      q.append("     AND transf.obs_datetime > :onOrAfter ");
    } else {
      q.append("     AND transf.obs_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    }

    q.append("       AND type.voided = 0 ");
    q.append("       AND type.concept_id = :typeOfPatient ");
    q.append("       AND type.value_coded = :answer ");

    q.append("UNION ");

    q.append("SELECT p.patient_id ");
    q.append("FROM patient p   ");
    q.append("    JOIN patient_program pp  ");
    q.append("        ON p.patient_id=pp.patient_id ");
    q.append("    JOIN patient_state ps  ");
    q.append("        ON ps.patient_program_id=pp.patient_program_id ");
    q.append("WHERE  pp.program_id = :programEnrolled ");
    q.append("    AND ps.state = :transferredInState ");
    if (cd.getOnOrAfter() == null) {
      q.append("     AND ps.start_date <= :onOrBefore ");
    } else if (cd.getOnOrBefore() == null) {
      q.append("     AND ps.start_date > :onOrAfter ");
    } else {
      q.append("     AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore ");
    }
    q.append("    AND p.voided = 0 ");
    q.append("    AND pp.voided = 0 ");
    q.append("    AND ps.voided = 0");

    q.addParameter("mastercard", hivMetadata.getMasterCardEncounterType());
    q.addParameter("transferFromOther", hivMetadata.getTransferFromOtherFacilityConcept());
    q.addParameter("yes", hivMetadata.getYesConcept());
    q.addParameter("typeOfPatient", hivMetadata.getTypeOfPatientTransferredFrom());
    Concept typeOfPatientTransferredFrom = cd.getTypeOfPatientTransferredFromAnswer();
    Program programEnrolled = cd.getProgramEnrolled();
    ProgramWorkflowState programWorkflowState = cd.getPatientState();
    if (typeOfPatientTransferredFrom == null) {
      throw new NullPointerException(
          "Answer for TYPE OF PATIENT TRANSFERRED FROM concept cannot be null");
    }
    q.addParameter("answer", typeOfPatientTransferredFrom);
    q.addParameter("programEnrolled", programEnrolled);
    q.addParameter("transferredInState", programWorkflowState);
    q.addParameter("location", cd.getLocation());
    q.addParameter("onOrAfter", cd.getOnOrAfter());
    q.addParameter("onOrBefore", DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));

    List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
    ret.setMemberIds(new HashSet<>(results));
    return ret;
  }
}
