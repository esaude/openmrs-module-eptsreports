package org.openmrs.module.eptsreports.reporting.calculation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

/** This acts as a test level storage of shared calculations logic & data */
public class CalculationsTestsCache {

  SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");

  /** Parses date from string using {@link CalculationsTestsCache#DATE_FORMAT} */
  Date getDate(String dateString) {
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** Mocks {@link EptsCalculations} with org.jmockit */
  void stubEptsCalculations(final PatientCalculation calculation) {
    new MockUp<EptsCalculations>() {

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
            addObsToMap(calculation, context, map, i, matchedObs);
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
                Context.getObsService().getObservationsByPersonAndConcept(p, concept);
            // from last to first
            Collections.sort(
                observations,
                new Comparator<Obs>() {

                  @Override
                  public int compare(Obs o1, Obs o2) {
                    return o2.getObsDatetime().compareTo(o1.getObsDatetime());
                  }
                });
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
            addObsToMap(calculation, context, map, i, matchedObs);
          }
        }
        return map;
      }

      private void addObsToMap(
          final PatientCalculation calculation,
          PatientCalculationContext context,
          CalculationResultMap map,
          int i,
          Obs matchedObs) {
        if (matchedObs != null) {
          map.put(i, new SimpleResult(matchedObs, calculation, context));
        }
      }
    };
  }

  /** Mocks {@link EptsCalculationUtils} with org.jmockit */
  void stubEptsCalculationUtils(final PatientCalculation calculation) {
    new MockUp<EptsCalculationUtils>() {

      @Mock
      public Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
        return (results == null
                || results.isEmpty()
                || !results.containsKey(patientId)
                || results.get(patientId) == null)
            ? null
            : (Obs) results.get(patientId).getValue();
      }

      @Mock
      public Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
        return (results == null
                || results.isEmpty()
                || !results.containsKey(patientId)
                || results.get(patientId) == null)
            ? null
            : (Encounter) results.get(patientId).getValue();
      }
    };
  }

  Obs createBasicObs(
      Patient patient,
      Concept concept,
      Encounter encounter,
      Date dateTime,
      Location location,
      Object value) {
    Obs o = new Obs();
    o.setConcept(concept);
    o.setPerson(patient);
    o.setEncounter(encounter);
    o.setObsDatetime(dateTime);
    o.setLocation(location);
    if (value instanceof Double) {
      o.setValueNumeric((Double) value);
    } else if (value instanceof Boolean) {
      o.setValueBoolean((Boolean) value);
    } else if (value instanceof Concept) {
      o.setValueCoded((Concept) value);
    } else if (value instanceof Date) {
      o.setValueDatetime((Date) value);
    }

    return Context.getObsService().saveObs(o, null);
  }
}
