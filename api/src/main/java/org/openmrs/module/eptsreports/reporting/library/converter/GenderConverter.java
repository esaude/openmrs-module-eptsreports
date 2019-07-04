package org.openmrs.module.eptsreports.reporting.library.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class GenderConverter implements DataConverter {

  private Log log = LogFactory.getLog(getClass());

  public GenderConverter() {}

  /** @should return a blank string if valueNumeric is null */
  @Override
  public Object convert(Object original) {

    String o = (String) original;

    if (o == null) return "";

    if (o.equals("M")) {
      return "M";
    } else {
      return "F";
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
