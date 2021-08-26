package org.openmrs.module.eptsreports.reporting.data.converter;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class GeneralDateConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    return EptsReportUtils.formatDate((Date) obj);
  }

  @Override
  public Class<?> getInputDataType() {
    return Date.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
