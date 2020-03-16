package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * A cohort definition that computes date ranges for a specific month of a quarter of a year and
 * evaluates another wrapped cohort definition with the computed dates as parameter values.
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class EptsQuarterlyCohortDefinition extends BaseCohortDefinition {

  public enum Month {
    M1,
    M2,
    M3
  };

  @ConfigurationProperty private Month month;

  @ConfigurationProperty private Integer year;

  @ConfigurationProperty private Location location;

  @ConfigurationProperty private CohortDefinition cohortDefinition;

  public EptsQuarterlyCohortDefinition() {}

  public EptsQuarterlyCohortDefinition(CohortDefinition cohortDefinition, Month month) {
    setCohortDefinition(cohortDefinition);
    this.month = month;
  }

  public Month getMonth() {
    return month;
  }

  public void setMonth(Month month) {
    this.month = month;
  }

  public CohortDefinition getCohortDefinition() {
    return cohortDefinition;
  }

  public void setCohortDefinition(CohortDefinition cohortDefinition) {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("onOrAfter", "After Date", Date.class));
    parameters.add(new Parameter("onOrBefore", "Before Date", Date.class));
    if (!cohortDefinition.getParameters().containsAll(parameters)) {
      throw new IllegalArgumentException(
          "CohortDefinition must have both onOrAfter and onOrBefore parameters");
    }
    parameters.clear();
    parameters.add(new Parameter("startDate", "After Date", Date.class));
    parameters.add(new Parameter("endDate", "Before Date", Date.class));
    if (!cohortDefinition.getParameters().containsAll(parameters)) {
      throw new IllegalArgumentException(
              "CohortDefinition must have both startDate and endDate parameters");
    }
    this.cohortDefinition = cohortDefinition;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }
}
