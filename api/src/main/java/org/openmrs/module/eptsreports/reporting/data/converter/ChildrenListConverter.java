package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class ChildrenListConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "Não";
    } else {
      return ("Sim");
    }
  }

  @Override
  public Class<?> getInputDataType() {
    return Object.class;
  }

  @Override
  public Class<?> getDataType() {
    return Object.class;
  }
}
