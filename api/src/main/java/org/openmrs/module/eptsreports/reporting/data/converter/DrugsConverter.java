package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.Drug;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class DrugsConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "Missing";
    } else {
      return ((Drug) obj).getDisplayName();
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
