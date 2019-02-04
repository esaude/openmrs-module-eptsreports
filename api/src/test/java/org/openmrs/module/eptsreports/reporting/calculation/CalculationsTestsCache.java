package org.openmrs.module.eptsreports.reporting.calculation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

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

import mockit.Mock;
import mockit.MockUp;

/**
 * This acts as a test level storage of shared calculations logic & data
 */
public class CalculationsTestsCache {
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");

    public CalculationResultMap initialArtStartDateCalculationDefaultResult(PatientCalculation calculation,
                    PatientCalculationContext evaluationContext, Collection<Integer> cohort) {
        CalculationResultMap map = new CalculationResultMap();

        // initiated ART by hiv enrolment
        if (cohort.contains(2)) {
            map.put(2, new SimpleResult(new Timestamp(getDate("2008-08-01 00:00:00.0", DATE_FORMAT).getTime()), calculation, evaluationContext));
        } // initiated ART by starting ARV plan observation
        if (cohort.contains(6)) {
            map.put(6, new SimpleResult(new Timestamp(getDate("2019-01-19 00:00:00.0", DATE_FORMAT).getTime()), calculation, evaluationContext));
        } // initiated ART by historical start date observation
        if (cohort.contains(7)) {
            map.put(7, new SimpleResult(new Timestamp(getDate("2019-01-18 00:00:00.0", DATE_FORMAT).getTime()), calculation, evaluationContext));
        }
        // initiated ART by first phamarcy encounter observation
        if (cohort.contains(8)) {
            map.put(8, new SimpleResult(new Timestamp(getDate("2019-01-21 00:00:00.0", DATE_FORMAT).getTime()), calculation, evaluationContext));
        }
        // initiated ART by ARV transfer in observation
        if (cohort.contains(999)) {
            map.put(999, new SimpleResult(new Timestamp(getDate("2019-01-20 00:00:00.0", DATE_FORMAT).getTime()), calculation, evaluationContext));
        }
        if (cohort.contains(432)) {
            map.put(432, new SimpleResult("", calculation, evaluationContext));
        }
        return map;
    }

    Date getDate(String dateString, SimpleDateFormat sdf) {
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mocks {@link EptsCalculations} with org.jmockit
     */
    void stubEptsCalculations(final PatientCalculation calculation) {
        new MockUp<EptsCalculations>() {

            @Mock
            public CalculationResultMap firstPatientProgram(Program program, Location location, Collection<Integer> cohort,
                            PatientCalculationContext context) {
                CalculationResultMap map = new CalculationResultMap();
                for (int i : cohort) {
                    Patient p = Context.getPatientService().getPatient(i);
                    if (p != null) {
                        for (PatientProgram pp : Context.getProgramWorkflowService().getPatientPrograms(p, program, null, null, null, null,
                                        false)) {
                            map.put(i, new SimpleResult(pp, calculation, context));
                        }
                    }
                }
                return map;
            }

            @Mock
            public CalculationResultMap firstObs(Concept question, Concept answer, Location location, boolean sortByDatetime,
                            Collection<Integer> cohort, PatientCalculationContext context) {
                CalculationResultMap map = new CalculationResultMap();
                for (int i : cohort) {
                    Patient p = Context.getPatientService().getPatient(i);
                    if (p != null) {
                        Obs matchedObs = null;
                        for (Obs o : Context.getObsService().getObservationsByPersonAndConcept(p, question)) {
                            // either with mached value or boolean || datetime
                            if ((answer != null && answer.equals(o.getValueCoded())) || question.getDatatype().getUuid()
                                            .matches("8d4a5cca-c2cc-11de-8d13-0010c6dffd0f|8d4a5af4-c2cc-11de-8d13-0010c6dffd0f")) {
                                matchedObs = o;
                                break;
                            }
                        }
                        if (matchedObs != null) {
                            map.put(i, new SimpleResult(matchedObs, calculation, context));
                        }
                    }
                }
                return map;
            }

            @Mock
            public CalculationResultMap firstEncounter(EncounterType encounterType, Collection<Integer> cohort, Location location,
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
        };
    }

    /**
     * Mocks {@link EptsCalculationUtils} with org.jmockit
     */
    void stubEptsCalculationUtils(final PatientCalculation calculation) {
        new MockUp<EptsCalculationUtils>() {

            @Mock
            public Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
                return (results.isEmpty() || !results.containsKey(patientId)) ? null : (Obs) results.get(patientId).getValue();
            }

            @Mock
            public Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
                return (results.isEmpty() || !results.containsKey(patientId)) ? null : (Encounter) results.get(patientId).getValue();
            }
        };
    }

    Obs createBasicObs(Patient patient, Concept concept, Encounter encounter, Date dateTime, Location location, Object value) {
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
