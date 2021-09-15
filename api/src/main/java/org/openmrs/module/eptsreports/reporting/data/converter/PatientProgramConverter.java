package org.openmrs.module.eptsreports.reporting.data.converter;

import java.util.Date;
import org.openmrs.PatientProgram;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class PatientProgramConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    PatientProgram patientProgram = (PatientProgram) obj;
    return EptsReportUtils.formatDate((Date) patientProgram.getDateEnrolled());
  }

  @Override
  public Class<?> getInputDataType() {
    return PatientProgram.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
