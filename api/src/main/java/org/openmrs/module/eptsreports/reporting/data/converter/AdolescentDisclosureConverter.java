package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class AdolescentDisclosureConverter implements DataConverter {
  String results = "";

  @Override
  public Object convert(Object obj) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    if (obj == null) {
      results = "";
    }
    Encounter encounter = (Encounter) obj;
    if (encounter == null) {
      results = "";
    }
    if (encounter != null && encounter.getAllObs() != null) {
      if (!checkIfObsExists(
          encounter, hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept())) {
        results = "";
      } else {
        for (Obs obs : encounter.getAllObs()) {
          if (obs.getConcept() != null
              && obs.getConcept()
                  .equals(hivMetadata.getDisclosureOfHIVDiagnosisToChildrenAdolescentsConcept())) {

            if (obs.getValueCoded() == null) {
              results = "";
            } else {
              if (obs.getValueCoded()
                  .equals(
                      Context.getConceptService()
                          .getConceptByUuid("63e43b2f-801f-412b-87bb-45db8e0ad21b"))) {
                results = "P";
              } else if (obs.getValueCoded()
                  .equals(
                      Context.getConceptService()
                          .getConceptByUuid("8279b6c1-572d-428c-be45-96e05fe6165d"))) {
                results = "N";
              }
            }
          }
        }
      }
    }
    return results;
  }

  @Override
  public Class<?> getInputDataType() {
    return Obs.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }

  private boolean checkIfObsExists(Encounter encounter, Concept concept) {
    boolean isPresent = false;
    for (Obs obs : encounter.getAllObs()) {
      if (obs.getConcept().equals(concept)) {
        isPresent = true;
      }
    }
    return isPresent;
  }
}
