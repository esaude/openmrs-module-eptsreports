package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

public class EmptyToNaoAndAnyToSimConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "Não";
    }
    return "Sim";
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
