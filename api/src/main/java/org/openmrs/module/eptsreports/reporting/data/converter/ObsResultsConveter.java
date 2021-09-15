package org.openmrs.module.eptsreports.reporting.data.converter;

import java.util.Date;
import org.openmrs.Obs;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class ObsResultsConveter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    Obs obs = (Obs) obj;
    return EptsReportUtils.formatDate((Date) obs.getValueDatetime());
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
