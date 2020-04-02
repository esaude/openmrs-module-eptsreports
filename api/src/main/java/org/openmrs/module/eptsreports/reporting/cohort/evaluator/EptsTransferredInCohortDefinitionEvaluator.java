package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.HashSet;
import java.util.List;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
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

    // Boolean B10Flag = cd.getB10Flag();

    SqlQueryBuilder q = new SqlQueryBuilder();
    q.append("SELECT p.patient_id ");
    q.append("FROM   patient p ");
    q.append("       JOIN encounter e ");
    q.append("         ON p.patient_id = e.patient_id ");
    q.append("       JOIN obs transf ");
    q.append("         ON transf.encounter_id = e.encounter_id ");
    q.append("       JOIN obs type ");
    q.append("         ON type.encounter_id = e.encounter_id ");
    q.append("       JOIN obs opening ");
    q.append("         ON opening.encounter_id = e.encounter_id");
    q.append("WHERE  p.voided = 0 ");
    q.append("       AND e.voided = 0 ");
    q.append("       AND e.encounter_type = :mastercard ");
    q.append("       AND e.location_id = :location ");

    q.append("       AND transf.voided = 0 ");
    q.append("       AND transf.concept_id = :transferFromOther ");
    q.append("       AND transf.value_coded = :yes ");

    q.append("       AND type.voided = 0 ");
    q.append("       AND type.concept_id = :typeOfPatient ");
    if (cd.getB10Flag() == true) {
      q.append("       AND type.value_coded = :tarv ");
    } else {
      q.append("       AND type.value_coded in (:tarv,:preTarv) ");
    }
    q.append("       AND opening.voided = 0 ");
    q.append("       AND opening.concept_id = :dateOfMasterCardFileOpening ");
    if (cd.getOnOrBefore() == null) {
      q.append("     AND opening.value_datetime <= :onOrAfter ");
    } else if (cd.getOnOrAfter() == null) {
      q.append("     AND opening.value_datetime <= :onOrBefore ");
    } else {
      q.append("     AND opening.value_datetime <= :onOrBefore ");
    }

    q.append("UNION ");

    if (cd.getProgramEnrolled2() == null) {
      q.append("select pgEnrollment.patient_id ");
      q.append("FROM(SELECT p.patient_id, pp.patient_program_id,");
      q.append(" min(ps.start_date) as pgEnrollmentDate ");
      q.append("FROM patient p ");
      q.append("    JOIN patient_program pp ");
      q.append("    ON p.patient_id=pp.patient_id ");
      q.append("    JOIN patient_state ps ");
      q.append("    ON pp.patient_program_id=ps.patient_program_id ");
      q.append("    WHERE  pp.voided=0 ");
      q.append("    AND ps.voided=0 ");
      q.append("    AND p.voided=0 ");
      q.append("    AND pp.program_id=:programEnrolled ");
      q.append("    AND location_id= :location AND  ");
      if (cd.getOnOrBefore() == null) {
        q.append(" ps.start_date <= :onOrAfter ");
      } else {
        q.append(" ps.start_date <= :onOrBefore ");
      }
      q.append("GROUP BY pp.patient_program_id) pgEnrollment ");
      q.append("  JOIN patient_state ps ");
      q.append("  ON ps.patient_program_id=pgEnrollment.patient_program_id ");
      q.append("  where ps.start_date=pgEnrollment.pgEnrollmentDate ");
      q.append("  AND ps.state = :transferredInState ");
      q.append("  AND ps.voided=0 ");
    } else {
      q.append("SELECT pgEnrollment.patient_id  ");
      q.append(
          "FROM(SELECT p.patient_id, pp.patient_program_id, min(ps.start_date) as pgEnrollmentDate ");
      q.append("FROM patient p ");
      q.append("    JOIN patient_program pp on p.patient_id=pp.patient_id ");
      q.append("    JOIN patient_state ps on pp.patient_program_id=ps.patient_program_id ");
      q.append("    WHERE  pp.voided=0 ");
      q.append("    AND ps.voided=0 ");
      q.append("    AND p.voided=0 ");
      q.append("    AND pp.program_id=:programEnrolled");
      q.append("    AND location_id= :location");
      if (cd.getOnOrBefore() == null) {
        q.append(" AND ps.start_date<=:onOrAfter ");
      } else {
        q.append(" AND ps.start_date<= :onOrBefore ");
      }
      q.append("GROUP BY pp.patient_program_id) pgEnrollment  ");
      q.append("JOIN patient_state ps on ps.patient_program_id=pgEnrollment.patient_program_id ");
      q.append("  WHERE ps.start_date=pgEnrollment.pgEnrollmentDate ");
      q.append("  AND ps.state = :transferredInState ");
      q.append("  AND ps.voided=0 ");
      q.append("UNION SELECT pgEnrollment.patient_id ");
      q.append(" FROM( ");
      q.append(
          "SELECT p.patient_id, pp.patient_program_id, min(ps.start_date) as pgEnrollmentDate  ");
      q.append("FROM patient p ");
      q.append("  JOIN patient_program pp on p.patient_id=pp.patient_id ");
      q.append("  JOIN patient_state ps on pp.patient_program_id=ps.patient_program_id ");
      q.append("  WHERE  pp.voided=0 ");
      q.append("  AND ps.voided=0 ");
      q.append("  AND p.voided=0 ");
      q.append("  AND pp.program_id=:programEnrolled2 ");
      q.append("  AND location_id= :location ");
      if (cd.getOnOrBefore() == null) {
        q.append(" AND ps.start_date <=:onOrAfter ");
      } else {
        q.append(" AND ps.start_date <= :onOrBefore ");
      }
      q.append("GROUP BY pp.patient_program_id) pgEnrollment ");
      q.append("JOIN patient_state ps on ps.patient_program_id = pgEnrollment.patient_program_id ");
      q.append("where ps.start_date = pgEnrollment.pgEnrollmentDate ");
      q.append("AND ps.state=:transferredInState2 ");
      q.append("AND ps.voided=0 ");
    }

    q.addParameter("mastercard", hivMetadata.getMasterCardEncounterType());
    q.addParameter("transferFromOther", hivMetadata.getTransferFromOtherFacilityConcept());
    q.addParameter("yes", hivMetadata.getYesConcept());
    q.addParameter("typeOfPatient", hivMetadata.getTypeOfPatientTransferredFrom());
    Program programEnrolled = cd.getProgramEnrolled();
    Program programEnrolled2 = cd.getProgramEnrolled2();
    ProgramWorkflowState programWorkflowState = cd.getPatientState();
    ProgramWorkflowState programWorkflowState2 = cd.getPatientState2();

    q.addParameter("preTarv", hivMetadata.getPreTarvConcept());
    q.addParameter("tarv", hivMetadata.getArtStatus());
    q.addParameter(
        "dateOfMasterCardFileOpening", hivMetadata.getDateOfMasterCardFileOpeningConcept());
    q.addParameter("programEnrolled", programEnrolled);
    q.addParameter("programEnrolled2", programEnrolled2);
    q.addParameter("transferredInState", programWorkflowState);
    q.addParameter("transferredInState2", programWorkflowState2);
    q.addParameter("location", cd.getLocation());
    q.addParameter("onOrAfter", cd.getOnOrAfter());
    q.addParameter("onOrBefore", cd.getOnOrBefore());

    List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
    ret.setMemberIds(new HashSet<>(results));
    return ret;
  }
}
