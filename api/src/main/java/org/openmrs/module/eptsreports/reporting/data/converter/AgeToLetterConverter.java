package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class AgeToLetterConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    Object value = ((Age) obj).getFullYears();

    if (value != null && value instanceof Integer) {
      if ((Integer) value < 15) {
        return "C";
      } else {
        return "A";
      }
    }
    return "";
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
