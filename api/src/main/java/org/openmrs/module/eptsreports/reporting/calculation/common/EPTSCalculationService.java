/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.calculation.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiObsDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiPatientStateDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.JembiProgramEnrollmentForPatientDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.springframework.stereotype.Service;

/** Utility class of common base calculations */
@Service
public class EPTSCalculationService {

  /**
   * Evaluate for obs based on the time modifier
   *
   * @param concept
   * @param cohort
   * @param locationList
   * @param valueCodedList
   * @param timeQualifier
   * @param startDate
   * @param context
   * @return
   */
  public CalculationResultMap getObs(
      Concept concept,
      Collection<Integer> cohort,
      List<Location> locationList,
      List<Concept> valueCodedList,
      TimeQualifier timeQualifier,
      Date startDate,
      PatientCalculationContext context) {
    ObsForPersonDataDefinition def = new ObsForPersonDataDefinition();
    def.setName(timeQualifier.name() + "obs");
    def.setWhich(timeQualifier);
    def.setQuestion(concept);
    def.setOnOrBefore(context.getNow());
    if (startDate != null) {
      def.setOnOrAfter(startDate);
    }
    if (valueCodedList != null && !valueCodedList.isEmpty()) {
      def.setValueCodedList(valueCodedList);
    }
    if (!locationList.isEmpty()) {
      def.setLocationList(locationList);
    }
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the last patient state for the specified programWorkflowState
   *
   * @param cohort
   * @param location
   * @param programWorkflowState
   * @param context
   * @return
   */
  public CalculationResultMap allPatientStates(
      Collection<Integer> cohort,
      Location location,
      ProgramWorkflowState programWorkflowState,
      PatientCalculationContext context) {
    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrBefore(context.getNow());
    def.setStates(Arrays.asList(programWorkflowState));
    def.setWhich(TimeQualifier.ANY);
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the last patient state for the specified programWorkflowState
   *
   * @param cohort
   * @param location
   * @param programWorkflowState
   * @param context
   * @return
   */
  public CalculationResultMap patientStatesBeforeDate(
      Collection<Integer> cohort,
      Location location,
      Date endDate,
      List<ProgramWorkflowState> states,
      PatientCalculationContext context) {
    JembiPatientStateDefinition def = new JembiPatientStateDefinition();
    def.setLocation(location);
    def.setStartedOnOrBefore(context.getNow());
    def.setStates(states);
    def.setWhich(TimeQualifier.ANY);
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates all ProgramEnrollment for specific Program
   *
   * @param program
   * @param cohort
   * @param context
   * @return
   */
  public CalculationResultMap allProgramEnrollment(
      Program program, Collection<Integer> cohort, PatientCalculationContext context) {
    ProgramEnrollmentsForPatientDataDefinition def =
        new ProgramEnrollmentsForPatientDataDefinition();
    def.setName("All in " + program.getName());
    def.setWhichEnrollment(TimeQualifier.ANY);
    def.setProgram(program);
    def.setEnrolledOnOrBefore(context.getNow());
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the first encounter of a given type of each patient
   *
   * @param encounterType the encounter type
   * @param cohort the patient ids
   * @param context the calculation context
   * @return the encounters in a calculation result map
   */
  public CalculationResultMap firstEncounter(
      EncounterType encounterType,
      Collection<Integer> cohort,
      Location location,
      PatientCalculationContext context) {
    EncountersForPatientDataDefinition def = new EncountersForPatientDataDefinition();
    def.setWhich(TimeQualifier.FIRST);
    def.setLocationList(Arrays.asList(location));
    if (encounterType != null) {
      def.setName("first encounter of type " + encounterType.getName());
      def.addType(encounterType);
    } else {
      def.setName("first encounter of any type");
    }
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }

  /**
   * Evaluates the first Obs for a given question and answer
   *
   * @param question
   * @param answer
   * @param location
   * @param sortByDatetime
   * @param cohort
   * @param context
   * @return
   */
  public CalculationResultMap firstObs(
      Concept question,
      Concept answer,
      Location location,
      boolean sortByDatetime,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    JembiObsDefinition definition = new JembiObsDefinition("JembiObsDefinition");
    definition.setQuestion(question);
    definition.setAnswer(answer);
    definition.setLocation(location);
    definition.setSortByDatetime(sortByDatetime);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }

  /**
   * Evaluates the first PatientProgram for a given Program and location
   *
   * @param program
   * @param location
   * @param cohort
   * @param context
   * @return
   */
  public CalculationResultMap firstPatientProgram(
      Program program,
      Location location,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    JembiProgramEnrollmentForPatientDefinition definition =
        new JembiProgramEnrollmentForPatientDefinition("First Patient Program");
    definition.setProgram(program);
    definition.setLocation(location);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }

  /**
   * Evaluates the last Obs for given encounterTypes and specific concept
   *
   * @param encounterTypes
   * @param concept
   * @param location
   * @param startDate
   * @param endDate
   * @param cohort
   * @param context
   * @return
   */
  public CalculationResultMap lastObs(
      List<EncounterType> encounterTypes,
      Concept concept,
      Location location,
      Date startDate,
      Date endDate,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    ObsForPersonDataDefinition definition = new ObsForPersonDataDefinition();
    definition.setName("last obs");
    definition.setEncounterTypeList(encounterTypes);
    definition.setQuestion(concept);
    definition.setLocationList(Arrays.asList(location));
    definition.setOnOrAfter(startDate);
    definition.setOnOrBefore(endDate);
    definition.setWhich(TimeQualifier.LAST);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }
}
