package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** Cohort for transferred-in patients as specified in Resumo Mensal MISAU. */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class ResumoMensalTransferredOutCohortDefinition extends BaseCohortDefinition {

  @ConfigurationProperty private Date onOrAfter;

  @ConfigurationProperty private Date onOrBefore;

  @ConfigurationProperty private Location location;

  @ConfigurationProperty private Boolean maxDates;

  public Date getOnOrAfter() {
    return onOrAfter;
  }

  public void setOnOrAfter(Date onOrAfter) {
    this.onOrAfter = onOrAfter;
  }

  public Date getOnOrBefore() {
    return onOrBefore;
  }

  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Boolean getMaxDates() {
    return maxDates;
  }

  public void setMaxDates(Boolean maxDates) {
    this.maxDates = maxDates;
  }
}
