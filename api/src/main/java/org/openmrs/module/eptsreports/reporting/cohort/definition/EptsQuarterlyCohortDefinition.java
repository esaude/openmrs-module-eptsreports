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
    validateParameters(cohortDefinition);
    this.cohortDefinition = cohortDefinition;
  }

  private void validateParameters(CohortDefinition cohortDefinition) {
    String[][] valid = {{"onOrAfter", "onOrBefore"}, {"startDate", "endDate"}};
    List<Parameter> cohortParams = cohortDefinition.getParameters();
    boolean contains;
    for (String[] v : valid) {
      List<Parameter> params = new ArrayList<>();
      params.add(new Parameter(v[0], "Start Date", Date.class));
      params.add(new Parameter(v[1], "Start Date", Date.class));
      contains = cohortParams.containsAll(params);
      if (!contains) {
        throw new IllegalArgumentException(
            "CohortDefinition must have both " + v[0] + " and " + v[1] + " parameters");
      }
      break;
    }
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
