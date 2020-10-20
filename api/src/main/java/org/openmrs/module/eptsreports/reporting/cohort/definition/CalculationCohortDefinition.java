/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.cohort.definition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.CalculationWithResultFinder;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** Cohort definition based on a calculation */
@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class CalculationCohortDefinition extends BaseCohortDefinition {

  @ConfigurationProperty(required = true, group = "calculation")
  private PatientCalculation calculation;

  @ConfigurationProperty(group = "calculation")
  private Date onOrAfter;

  @ConfigurationProperty(group = "calculation")
  private Date onOrBefore;

  @ConfigurationProperty(group = "calculation")
  private Object withResult;

  @ConfigurationProperty(group = "calculation")
  private CalculationWithResultFinder withResultFinder = CalculationWithResultFinder.DEFAULT;

  @ConfigurationProperty(group = "calculation")
  private Map<String, Object> calculationParameters;

  @ConfigurationProperty(group = "calculation")
  private Location location;

  /** Default constructor */
  public CalculationCohortDefinition() {}

  /**
   * Constructs a new calculation based cohort definition
   *
   * @param calculation the calculation
   */
  public CalculationCohortDefinition(PatientCalculation calculation) {
    setCalculation(calculation);
  }

  /**
   * Constructor to populate name and calculation
   *
   * @param name the name
   * @param calculation the calculation
   */
  public CalculationCohortDefinition(String name, PatientCalculation calculation) {
    setName(name);
    setCalculation(calculation);
  }

  /** @return the calculation */
  public PatientCalculation getCalculation() {
    return calculation;
  }

  /** @param calculation the calculation to set */
  public void setCalculation(PatientCalculation calculation) {
    this.calculation = calculation;
  }

  public Date getOnOrAfter() {
    return onOrAfter;
  }

  public void setOnOrAfter(Date onOrAfter) {
    this.onOrAfter = onOrAfter;
  }

  /**
   * Gets the date for which to calculate
   *
   * @return the date
   */
  public Date getOnOrBefore() {
    return onOrBefore;
  }

  /**
   * Sets the date for which to calculate
   *
   * @param onOrBefore the date
   */
  public void setOnOrBefore(Date onOrBefore) {
    this.onOrBefore = onOrBefore;
  }

  /**
   * Gets the result value required for inclusion in the cohort
   *
   * @return the result value
   */
  public Object getWithResult() {
    return withResult;
  }

  /**
   * Sets the result value required for inclusion in the cohort
   *
   * @param withResult the result value
   */
  public void setWithResult(Object withResult) {
    this.withResult = withResult;
  }

  /**
   * Gets the calculation parameters
   *
   * @return the calculation parameters
   */
  public Map<String, Object> getCalculationParameters() {
    return calculationParameters;
  }

  /**
   * Sets the calculation parameters
   *
   * @param calculationParameters the calculation parameters
   */
  public void setCalculationParameters(Map<String, Object> calculationParameters) {
    this.calculationParameters = calculationParameters;
  }

  /**
   * Adds a calculation parameter
   *
   * @param name the name
   * @param value the value
   */
  public void addCalculationParameter(String name, Object value) {
    if (calculationParameters == null) {
      calculationParameters = new HashMap<>();
    }
    if (value != null) {
      calculationParameters.put(name, value);
    }
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public CalculationWithResultFinder getWithResultFinder() {
    return withResultFinder;
  }

  public void setWithResultFinder(CalculationWithResultFinder withResultFinder) {
    this.withResultFinder = withResultFinder;
  }
}
