package org.openmrs.module.eptsreports.reporting.library.converter;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.formatDateTime;

import org.openmrs.Encounter;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class EncounterDataConverter implements DataConverter {

  private String what;

  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  public EncounterDataConverter() {}

  public EncounterDataConverter(String what) {
    this.what = what;
  }

  @Override
  public Object convert(Object obj) {

    if (obj == null) {
      return "";
    }
    Encounter encounter = (Encounter) obj;
    if (what.equals("encounterDate") && encounter.getEncounterDatetime() != null) {
      return formatDateTime(encounter.getEncounterDatetime());
    } else if (what.equals("encounterCreatedDate") && encounter.getDateCreated() != null) {
      return formatDateTime(encounter.getDateCreated());
    }

    return null;
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
