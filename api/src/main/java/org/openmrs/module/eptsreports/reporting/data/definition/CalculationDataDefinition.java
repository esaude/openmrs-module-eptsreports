package org.openmrs.module.eptsreports.reporting.data.definition;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/** Patient data definition based on a calculation */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class CalculationDataDefinition extends BaseDataDefinition implements PatientDataDefinition {
  public static final long serialVersionUID = 1L;

  @ConfigurationProperty(required = true, group = "calculation")
  private PatientCalculation calculation;

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @ConfigurationProperty(group = "calculation")
  private Location location;

  @ConfigurationProperty(group = "calculation")
  private Map<String, Object> calculationParameters;

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

  @ConfigurationProperty(group = "calculation")
  private Date onOrAfter;

  @ConfigurationProperty(group = "calculation")
  private Date onOrBefore;
  /** Default Constructor */
  public CalculationDataDefinition() {
    super();
  }
  /** Constructor to populate name and calculation */
  public CalculationDataDefinition(String name, PatientCalculation calculation) {
    super(name);
    setCalculation(calculation);
  }
  // ***** INSTANCE METHODS *****
  /** @return the calculation */
  public PatientCalculation getCalculation() {
    return calculation;
  }
  /** @param calculation the calculation to set */
  public void setCalculation(PatientCalculation calculation) {
    this.calculation = calculation;
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
      calculationParameters = new HashMap<String, Object>();
    }
    calculationParameters.put(name, value);
  }
  /** @see org.openmrs.module.reporting.data.DataDefinition#getDataType() */
  @Override
  public Class<?> getDataType() {
    return CalculationResult.class;
  }
}
