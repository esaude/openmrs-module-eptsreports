package org.openmrs.module.eptsreports.reporting.data.converter;

import org.openmrs.Encounter;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class EncounterDatetimeConverter implements DataConverter {

  @Override
  public Object convert(Object original) {
    Encounter e = (Encounter) original;

    if (e == null) return null;

    return EptsReportUtils.formatDate(e.getEncounterDatetime());
  }

  @Override
  public Class<?> getInputDataType() {
    return Encounter.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
