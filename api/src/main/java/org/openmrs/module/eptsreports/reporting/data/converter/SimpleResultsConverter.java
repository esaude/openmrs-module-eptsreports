package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class SimpleResultsConverter implements DataConverter {
  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    Object value = ((CalculationResult) obj).getValue();
    if (value instanceof Integer) {
      return Context.getConceptService().getConcept(((Integer) value)).getName().getName();
    } else if (value instanceof SimpleResult) {
      return Context.getConceptService()
          .getConcept((Integer) ((SimpleResult) value).getValue())
          .getName()
          .getName();
    }
    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return CalculationResult.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
