package org.openmrs.module.eptsreports.reporting.calculation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

/** This acts as a test level storage of shared calculations logic & data */
public class CalculationsTestsCache {

  public SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");

  /** Parses date from string using {@link CalculationsTestsCache#DATE_FORMAT} */
  public Date getDate(String dateString) {
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param a;startDate
   * @param b; endDate
   * @param d;date in question
   * @return true if d is between a & b else false
   */
  public boolean dateBetween(Date a, Date b, Date d) {
    return a == null || b == null || d == null
        ? false
        : (d.equals(a) || d.equals(b) || (a.compareTo(d) * d.compareTo(b) >= 0));
  }

  /** Mocks {@link EptsCalculations} with org.jmockit */
  public void stubEptsCalculations(final PatientCalculation calculation) {
    new EptsCalculationsMock(calculation);
  }

  /** Mocks {@link EptsCalculationUtils} with org.jmockit */
  public void stubEptsCalculationUtils(final PatientCalculation calculation) {
    new EptsCalculationUtilsMock(calculation);
  }

  public Obs createBasicObs(
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
