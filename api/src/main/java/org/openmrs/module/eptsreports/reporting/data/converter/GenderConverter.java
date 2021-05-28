package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class GenderConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    return obj.equals("M") ? "Masculino".toString() : "Feminino".toString();
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
