/** */
package org.openmrs.module.eptsreports.reporting.cohort.definition;

import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyAndParameterCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyAndParameterCachingStrategy.class)
public class ResumoTrimestralCalculationCohortDefinition extends BaseCohortDefinition {

  @ConfigurationProperty(required = true, group = "calculation")
  private BaseFghCalculation calculation;

  public ResumoTrimestralCalculationCohortDefinition() {}

  public ResumoTrimestralCalculationCohortDefinition(
      final String name, final BaseFghCalculation calculation) {
    this.calculation = calculation;
    this.setName(name);
  }

  public BaseFghCalculation getCalculation() {
    return this.calculation;
  }

  public void setCalculation(final BaseFghCalculation calculation) {
    this.calculation = calculation;
  }
}
