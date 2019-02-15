package org.openmrs.module.eptsreports.reporting.helper;

import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class OpenMRSTestHelper {

  public Obs createBasicObs(
      Patient patient,
      Concept concept,
      Encounter encounter,
      Date dateTime,
      Location location,
      Object value) {
    Obs newObservation = new Obs();
    newObservation.setConcept(concept);
    newObservation.setPerson(patient);
    newObservation.setEncounter(encounter);
    newObservation.setObsDatetime(dateTime);
    newObservation.setLocation(location);
    if (value instanceof Double) {
      newObservation.setValueNumeric((Double) value);
    } else if (value instanceof Boolean) {
      newObservation.setValueBoolean((Boolean) value);
    } else if (value instanceof Concept) {
      newObservation.setValueCoded((Concept) value);
    } else if (value instanceof Date) {
      newObservation.setValueDatetime((Date) value);
    }

    return Context.getObsService().saveObs(newObservation, null);
  }

  public Encounter createEncounter(
      Patient patient, EncounterType encounterType, Date dateTime, Location location) {
    Encounter encounter = new Encounter();
    encounter.setPatient(patient);
    encounter.setEncounterType(encounterType);
    encounter.setEncounterDatetime(dateTime);
    encounter.setLocation(location);

    return Context.getEncounterService().saveEncounter(encounter);
  }
}
