package org.openmrs.module.eptsreports.reporting.library.converter;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.formatDate;

import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class BirthDateConverter implements DataConverter {

  private String what;

  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  public BirthDateConverter() {}

  public BirthDateConverter(String what) {
    this.what = what;
  }

  @Override
  public Object convert(Object obj) {

    if (obj == null) {
      return "";
    }
    Birthdate birthdate = (Birthdate) obj;
    if (birthdate.getBirthdate() != null && what.equals("birthDate")) {
      return formatDate(birthdate.getBirthdate());
    } else if (birthdate.isEstimated() && what.equals("state")) {
      return "Yes";
    } else if (!birthdate.isEstimated() && what.equals("state")) {
      return "No";
    }
    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return CalculationResult.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
