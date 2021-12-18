package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

/** Converter to return YES OR NO depending on the query result */
public class StoYesAndNtoNoConverter implements DataConverter {
  @Override
  public Object convert(Object obj) {

    if (obj == null) {
      return "";
    }

    switch (obj.toString()) {
      case "S":
        return "Sim";
      case "N":
        return "Nao";
      default:
        return "";
    }
  }

  @Override
  public Class<?> getInputDataType() {
    return null;
  }

  @Override
  public Class<?> getDataType() {
    return null;
  }
}
