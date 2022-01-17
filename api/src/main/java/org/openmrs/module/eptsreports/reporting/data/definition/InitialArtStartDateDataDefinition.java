package org.openmrs.module.eptsreports.reporting.data.definition;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class InitialArtStartDateDataDefinition extends BaseDataDefinition
    implements PatientDataDefinition {

  public InitialArtStartDateDataDefinition() {
    super();
  }

  public InitialArtStartDateDataDefinition(String name) {
    super(name);
  }

  public static final long serialVersionUID = 1L;

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  @Override
  public Class<?> getDataType() {
    return Date.class;
  }

  @ConfigurationProperty private Date onOrBefore;

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @ConfigurationProperty private Location location;

  public Date getOnOrBefore() {
    return onOrBefore;
  }

  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }
}
