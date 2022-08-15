package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class AdolescentDisclosureConverter implements DataConverter {
  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "Branco";
    }
    Obs obs = (Obs) obj;
    Concept valueCoded = obs.getValueCoded();
    if (valueCoded == null) {
      return "Branco";
    } else if (valueCoded.equals(
        Context.getConceptService().getConceptByUuid("63e43b2f-801f-412b-87bb-45db8e0ad21b"))) {
      return "P";
    } else if (valueCoded.equals(
        Context.getConceptService().getConceptByUuid("8279b6c1-572d-428c-be45-96e05fe6165d"))) {
      return "N";
    }

    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return Obs.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
