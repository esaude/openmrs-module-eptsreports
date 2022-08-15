package org.openmrs.module.eptsreports.reporting.data.converter;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class DateOfPatientFirstConsultationConverter implements DataConverter {
  public String getWhich() {
    return which;
  }

  public void setWhich(String which) {
    this.which = which;
  }

  private String which;

  public DateOfPatientFirstConsultationConverter() {}

  public DateOfPatientFirstConsultationConverter(String which) {
    this.which = which;
  }

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "NA";
    }
    if (which.equals("date")) {
      Date date = (Date) obj;

      return EptsReportUtils.formatDate(date);
    } else if (which.equals("value")) {
      return obj.toString();
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
