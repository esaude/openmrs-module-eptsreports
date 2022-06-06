package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class ToStringConverter implements DataConverter {

  public String convert(Object obj) {
    if (obj == null) {
      return " ";
    }

    return " " + obj.toString();
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
