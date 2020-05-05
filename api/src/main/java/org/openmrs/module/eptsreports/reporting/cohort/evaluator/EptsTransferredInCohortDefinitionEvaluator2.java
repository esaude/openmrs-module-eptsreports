package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import static org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2.ARTProgram.PRE_TARV;
import static org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2.ARTProgram.TARV;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = EptsTransferredInCohortDefinition2.class)
public class EptsTransferredInCohortDefinitionEvaluator2 implements CohortDefinitionEvaluator {

  private HivMetadata hivMetadata;

  private EvaluationService evaluationService;

  @Autowired
  public EptsTransferredInCohortDefinitionEvaluator2(
      HivMetadata hivMetadata, EvaluationService evaluationService) {
    this.hivMetadata = hivMetadata;
    this.evaluationService = evaluationService;
  }

  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {

    EptsTransferredInCohortDefinition2 cd = (EptsTransferredInCohortDefinition2) cohortDefinition;
    EvaluatedCohort ret = new EvaluatedCohort(null, cd, context);

    List<Concept> programConcepts = new ArrayList<>();
    List<Program> programsEnrolled = new ArrayList<>();
    List<ProgramWorkflowState> programWorkflowStates = new ArrayList<>();
    setupMetadata(cd, programConcepts, programsEnrolled, programWorkflowStates);

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
    q.append("       AND type.value_coded in (:programConcept) ");
    q.append("       AND opening.voided = 0 ");
    q.append("       AND opening.concept_id = :dateOfMasterCardFileOpening ");
    if (cd.getOnOrAfter() == null) {
      q.append("     AND opening.value_datetime <= :onOrBefore ");
    } else {
      q.append("     AND opening.value_datetime BETWEEN :onOrAfter AND :onOrBefore ");
    }

    q.append("UNION ");

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
    q.append("    AND pp.program_id in (:programsEnrolled)");
    q.append("    AND location_id= :location AND  ");
    if (cd.getOnOrAfter() == null) {
      q.append(" ps.start_date <= :onOrBefore ");
    } else {
      q.append(" ps.start_date BETWEEN :onOrAfter AND :onOrBefore ");
    }
    q.append("GROUP BY pp.patient_program_id) pgEnrollment ");
    q.append("  JOIN patient_state ps ");
    q.append("  ON ps.patient_program_id=pgEnrollment.patient_program_id ");
    q.append("  where ps.start_date=pgEnrollment.pgEnrollmentDate ");
    q.append("  AND ps.state in (:transferredInState) ");
    q.append("  AND ps.voided=0 ");

    q.addParameter("mastercard", hivMetadata.getMasterCardEncounterType());
    q.addParameter("transferFromOther", hivMetadata.getTransferFromOtherFacilityConcept());
    q.addParameter("yes", hivMetadata.getYesConcept());
    q.addParameter("typeOfPatient", hivMetadata.getTypeOfPatientTransferredFrom());
    q.addParameter("preTarv", hivMetadata.getPreTarvConcept());
    q.addParameter("programConcept", programConcepts);
    q.addParameter(
        "dateOfMasterCardFileOpening", hivMetadata.getDateOfMasterCardFileOpeningConcept());
    q.addParameter("programsEnrolled", programsEnrolled);
    q.addParameter("transferredInState", programWorkflowStates);
    q.addParameter("location", cd.getLocation());

    if (cd.getOnOrAfter() != null) {
      q.addParameter("onOrAfter", cd.getOnOrAfter());
    }
    q.addParameter("onOrBefore", cd.getOnOrBefore());

    List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
    ret.setMemberIds(new HashSet<>(results));
    return ret;
  }

  private void setupMetadata(
      EptsTransferredInCohortDefinition2 cd,
      List<Concept> programConcepts,
      List<Program> programsEnrolled,
      List<ProgramWorkflowState> programWorkflowStates) {
    for (EptsTransferredInCohortDefinition2.ARTProgram program : cd.getArtPrograms()) {
      if (PRE_TARV.equals(program)) {
        programsEnrolled.add(hivMetadata.getHIVCareProgram());
        programConcepts.add(hivMetadata.getPreTarvConcept());
        programWorkflowStates.add(
            hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState());
      }
      if (TARV.equals(program)) {
        programsEnrolled.add(hivMetadata.getARTProgram());
        programConcepts.add(hivMetadata.getArtStatus());
        programWorkflowStates.add(
            hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
      }
    }
  }
}
