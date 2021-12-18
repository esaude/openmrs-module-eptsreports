package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

/** Converter to return the state of stay for ART patients */
public class StateOfStayArtPatientConverter implements DataConverter {

  final String transferred = "Transferido para";

  final String suspended = "Suspensão";

  final String abandoned = "Abandono";

  final String died = "Óbito";

  final String autoTransfer = "Auto Transferência";

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }

    switch (obj.toString()) {
      case "7":
        return transferred;
      case "8":
        return suspended;
      case "9":
        return abandoned;
      case "10":
        return died;
      case "1706":
        return transferred;
      case "1709":
        return suspended;
      case "1707":
        return abandoned;
      case "1366":
        return died;
      case "23863":
        return autoTransfer;
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
