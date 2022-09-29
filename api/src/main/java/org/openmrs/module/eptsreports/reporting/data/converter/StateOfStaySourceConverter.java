package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

/**
 * Converter to return the source of the State of Stay for ART patients Who Picked up ARV During the
 * Period
 */
public class StateOfStaySourceConverter implements DataConverter {

  final String fichaResumo = "Ficha Resumo";

  final String fichaClinica = "Ficha Clínica";

  final String sespProgram = "Programa SESP";

  @Override
  public Object convert(Object original) {
    if (original == null) {
      return "";
    }

    switch (original.toString()) {
      case "53":
        return fichaResumo;
      case "6":
        return fichaClinica;
      case "2":
        return sespProgram;
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
