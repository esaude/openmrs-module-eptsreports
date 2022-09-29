package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

/** Converter to return test results depending on the query result */
public class TestResultConverter implements DataConverter {

  @Override
  public Object convert(Object original) {
    if (original == null) {
      return "";
    }

    switch (original.toString()) {
      case "703":
        return "Positivo";
      case "664":
        return "Negativo";
      case "1065":
        return "Sim";
      case "1066":
        return "Não";
      case "1138":
        return "Indeterminado";
      case "165184":
        return "Não Encontrado";
      default:
        return "";
    }
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
