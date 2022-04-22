/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.cohort.evaluator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/** Evaluator for calculation based cohorts */
@Handler(supports = CalculationCohortDefinition.class)
public class CalculationCohortDefinitionEvaluator implements CohortDefinitionEvaluator {

  /**
   * @see
   *     org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator#evaluate(org.openmrs.module.reporting.cohort.definition.CohortDefinition,
   *     org.openmrs.module.reporting.evaluation.EvaluationContext)
   */
  @Override
  public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
      throws EvaluationException {
    CalculationResultMap map = doCalculation(cohortDefinition, context);

    CalculationCohortDefinition cd = (CalculationCohortDefinition) cohortDefinition;
    Set<Integer> passing =
        EptsCalculationUtils.patientsThatPass(
            map, cd.getWithResult(), cd.getWithResultFinder(), context);

    return new EvaluatedCohort(new Cohort(passing), cohortDefinition, context);
  }

  /**
   * Performs the calculation
   *
   * @param cohortDefinition the cohort definition
   * @param context the evaluation context
   * @return the calculation results
   */
  protected CalculationResultMap doCalculation(
      CohortDefinition cohortDefinition, EvaluationContext context) {
    CalculationCohortDefinition cd = (CalculationCohortDefinition) cohortDefinition;

    PatientCalculationService pcs = Context.getService(PatientCalculationService.class);
    PatientCalculationContext calcContext = pcs.createCalculationContext();
    calcContext.addToCache("location", cd.getLocation());
    calcContext.addToCache("onOrAfter", cd.getOnOrAfter());
    calcContext.addToCache("onOrBefore", cd.getOnOrBefore());

    Cohort cohort = context.getBaseCohort();
    if (cohort == null) {
      cohort = getAllPatientsCohort();
    }

    return pcs.evaluate(
        cohort.getMemberIds(), cd.getCalculation(), cd.getCalculationParameters(), calcContext);
  }

  private Cohort getAllPatientsCohort() {

    List<Patient> patients = Context.getPatientService().getAllPatients();
    Set<Integer> ids =
        patients.stream().map(patient -> patient.getPatientId()).collect(Collectors.toSet());

    return new Cohort("All patients", "All Patients returned from the DB", ids);
  }
}
