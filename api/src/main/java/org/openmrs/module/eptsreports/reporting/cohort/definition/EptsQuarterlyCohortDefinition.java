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
 * A cohort definition that computes a date range for a quarter of a year and evaluates another
 * wrapped cohort definition
 */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class EptsQuarterlyCohortDefinition extends BaseCohortDefinition {

  public enum Quarter {
    Q1("1st"),
    Q2("2nd"),
    Q3("3rd");

    private String display;

    Quarter(String display) {
      this.display = display;
    }

    @Override
    public String toString() {
      return display;
    }
  };

  @ConfigurationProperty private Quarter quarter;

  @ConfigurationProperty private Integer year;

  @ConfigurationProperty private Location location;

  @ConfigurationProperty private CohortDefinition cohortDefinition;

  public Quarter getQuarter() {
    return quarter;
  }

  public void setQuarter(Quarter quarter) {
    this.quarter = quarter;
  }

  public CohortDefinition getCohortDefinition() {
    return cohortDefinition;
  }

  public void setCohortDefinition(CohortDefinition cohortDefinition) {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("onOrBefore", "Before Date", Date.class));
    parameters.add(new Parameter("onOrAfter", "After Date", Date.class));
    if (!cohortDefinition.getParameters().containsAll(parameters)) {
      throw new IllegalArgumentException(
          "CohortDefinition must have onOrAfter and onOrBefore parameters");
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
