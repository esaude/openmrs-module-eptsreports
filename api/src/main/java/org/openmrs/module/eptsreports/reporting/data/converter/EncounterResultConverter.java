package org.openmrs.module.eptsreports.reporting.data.converter;

import java.util.Date;
import java.util.List;
import org.openmrs.Encounter;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class EncounterResultConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    List<Encounter> encounterLists = (List<Encounter>) obj;
    Date encounterDate = null;
    for (Encounter e : encounterLists) {

      encounterDate = e.getEncounterDatetime();
    }

    return EptsReportUtils.formatDate(encounterDate);
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
