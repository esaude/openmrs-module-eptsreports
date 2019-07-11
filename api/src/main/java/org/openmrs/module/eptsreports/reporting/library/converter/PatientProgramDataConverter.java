package org.openmrs.module.eptsreports.reporting.library.converter;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.formatDate;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class PatientProgramDataConverter implements DataConverter {

  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  private String what;

  private Log log = LogFactory.getLog(getClass());

  public PatientProgramDataConverter() {}

  public PatientProgramDataConverter(String what) {
    this.what = what;
  }

  /** @should return a blank string if valueNumeric is null */
  @Override
  public Object convert(Object obj) {

    if (obj == null) {
      return "";
    }
    PatientProgram patientProgram = ((PatientProgram) obj);
    if (what.equals("date")) {
      return formatDate(patientProgram.getDateEnrolled());
    } else if (what.equals("lastStatus")) {
      List<PatientState> allStates = new ArrayList<>(patientProgram.getCurrentStates());
      return (allStates.get(allStates.size() - 1)).getState().getConcept();
    } else if (what.equals("lastStatusDate")) {
      List<PatientState> allStates = new ArrayList<>(patientProgram.getCurrentStates());
      return formatDate((allStates.get(allStates.size() - 1)).getStartDate());
    }
    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return String.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
