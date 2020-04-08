package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.caching.NoCachingStrategy;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * A cohort definition that computes date ranges for a specific month of a quarter of a year and
 * evaluates another wrapped cohort definition with the computed dates as parameter values.
 */
@Caching(strategy = NoCachingStrategy.class)
public class EptsQuarterlyCohortDefinition extends BaseCohortDefinition {

  public enum Quarter {
    Q1,
    Q2,
    Q3,
    Q4;
  }

  public enum Month {
    M1,
    M2,
    M3
  };

  @ConfigurationProperty private Quarter quarter;

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

  public Quarter getQuarter() {
    return quarter;
  }

  public void setQuarter(Quarter quarter) {
    this.quarter = quarter;
  }

  private void validateParameters(CohortDefinition cohortDefinition) {
    String[][] valid = {{"onOrAfter", "onOrBefore"}, {"startDate", "endDate"}};
    List<Parameter> cohortParams = cohortDefinition.getParameters();
    boolean contains = false;
    for (String[] v : valid) {
      List<Parameter> params = new ArrayList<>();
      params.add(new Parameter(v[0], "", Date.class));
      params.add(new Parameter(v[1], "", Date.class));
      contains = cohortParams.containsAll(params);
      if (contains) break;
    }
    if (!contains) {
      throw new IllegalArgumentException(getMessage(valid));
    }
  }

  private String getMessage(String[][] valid) {
    return "CohortDefinition must have one of "
        + valid[0][0]
        + "/"
        + valid[0][1]
        + " or "
        + valid[1][0]
        + "/"
        + valid[1][1]
        + " parameters.";
  }
}
