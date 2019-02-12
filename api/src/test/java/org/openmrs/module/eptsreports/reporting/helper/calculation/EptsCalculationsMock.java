package org.openmrs.module.eptsreports.reporting.helper.calculation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import mockit.Mock;
import mockit.MockUp;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.EptsCalculations;
import org.openmrs.module.reporting.common.TimeQualifier;

/** Mocks {@link EptsCalculations} with org.jmockit */
public class EptsCalculationsMock extends MockUp<EptsCalculations> {

  private PatientCalculation calculation;

  public EptsCalculationsMock(PatientCalculation calculation) {
    this.calculation = calculation;
  }

  @Mock
  public CalculationResultMap firstPatientProgram(
      Program program,
      Location location,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    for (int i : cohort) {
      Patient p = Context.getPatientService().getPatient(i);
      if (p != null) {
        for (PatientProgram pp :
            Context.getProgramWorkflowService()
                .getPatientPrograms(p, program, null, null, null, null, false)) {
          map.put(i, new SimpleResult(pp, calculation, context));
        }
      }
    }
    return map;
  }

  @Mock
  public CalculationResultMap firstObs(
      Concept question,
      Concept answer,
      Location location,
      boolean sortByDatetime,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    for (int i : cohort) {
      Patient p = Context.getPatientService().getPatient(i);
      if (p != null) {
        Obs matchedObs = null;
        for (Obs o : Context.getObsService().getObservationsByPersonAndConcept(p, question)) {
          // either with mached value or boolean || datetime
          if ((answer != null && answer.equals(o.getValueCoded()))
              || question
                  .getDatatype()
                  .getUuid()
                  .matches(
                      "8d4a5cca-c2cc-11de-8d13-0010c6dffd0f|8d4a5af4-c2cc-11de-8d13-0010c6dffd0f")) {
            matchedObs = o;
            break;
          }
        }
        map.put(i, new SimpleResult(matchedObs, calculation, context));
      }
    }
    return map;
  }

  @Mock
  public CalculationResultMap firstEncounter(
      EncounterType encounterType,
      Collection<Integer> cohort,
      Location location,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    for (int i : cohort) {
      for (Encounter e : Context.getEncounterService().getEncountersByPatientId(i)) {
        if (encounterType.equals(e.getEncounterType())) {
          map.put(i, new SimpleResult(e, calculation, context));
        }
      }
    }
    return map;
  }

  @Mock
  public CalculationResultMap lastObs(
      List<EncounterType> encounterTypes,
      Concept concept,
      Location location,
      Date startDate,
      Date endDate,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    for (int i : cohort) {
      Patient p = Context.getPatientService().getPatient(i);
      if (p != null) {
        Obs matchedObs = null;
        List<Obs> observations =
            Context.getObsService()
                .getObservations(
                    Arrays.asList(p.getPerson()),
                    null,
                    Arrays.asList(concept),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    startDate,
                    endDate,
                    false);

        for (Obs o : observations) {
          if (o.getEncounter()
              .getEncounterType()
              .getUuid()
              .matches(
                  "02c533ab-b74b-4ee4-b6e5-ffb6d09a0ac8|02c533ab-b74b-4ee4-b6e5-ffb6d6777004|02c533ab-b74b-4ee4-b6e5-ffb6d6777005")) {
            matchedObs = o;
            break;
          }
        }
        map.put(i, new SimpleResult(matchedObs, calculation, context));
      }
    }
    return map;
  }

  @Mock
  public CalculationResultMap getObs(
      Concept concept,
      Collection<Integer> cohort,
      List<Location> locationList,
      List<Concept> valueCodedList,
      TimeQualifier timeQualifier,
      Date startDate,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    for (int i : cohort) {
      Patient p = Context.getPatientService().getPatient(i);
      if (p != null) {
        Obs matchedObs = null;
        // TODO, this startDate parameter fails the 2nd criteria
        List<Obs> observations =
            Context.getObsService()
                .getObservations(
                    Arrays.asList(p.getPerson()),
                    null,
                    Arrays.asList(concept),
                    valueCodedList,
                    null,
                    locationList,
                    null,
                    null,
                    null,
                    startDate,
                    null,
                    false);

        if (timeQualifier.equals(TimeQualifier.FIRST)) {
          Collections.reverse(observations);
          if (!observations.isEmpty()) {
            matchedObs = observations.get(0);
          }
          map.put(i, new SimpleResult(matchedObs, calculation, context));
        } else if (timeQualifier.equals(TimeQualifier.ANY)) {
          ListResult results = new ListResult();
          for (Obs o : observations) {
            results.add(new SimpleResult(o, calculation, context));
          }
          map.put(i, results);
        }
      }
    }
    return map;
  }
}
