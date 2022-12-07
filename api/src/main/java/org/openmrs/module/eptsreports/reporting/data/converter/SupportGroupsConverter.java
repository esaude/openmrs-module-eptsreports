package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.module.reporting.data.converter.DataConverter;

/** Converter to return the Support Group Name based on the returned concept */
public class SupportGroupsConverter implements DataConverter {

  final String mentorMother = "Mães Mentoras (MM)";

  final String youthAndTeenageMentors = "Adolescentes e Jovens Mentores (AJM)";

  final String championMan = "Homem Campeão (HC)";

  @Override
  public Object convert(Object original) {
    if (original == null) {
      return "";
    }

    switch (original.toString()) {
      case "24031":
        return mentorMother;
      case "165324":
        return youthAndTeenageMentors;
      case "165325":
        return championMan;
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
