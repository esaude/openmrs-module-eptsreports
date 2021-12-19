package org.openmrs.module.eptsreports.reporting.intergrated.library.cohorts;

import java.util.HashMap;
import java.util.Map;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.datimcode.DatimExtractCalculation;

public class EptsCalculationProvider implements CalculationProvider {

  private Map<String, Class<? extends BaseFghCalculation>> calculations =
      new HashMap<String, Class<? extends BaseFghCalculation>>();

  public EptsCalculationProvider() {
    calculations.put("DatimExtractCalculation", DatimExtractCalculation.class);
  }

  @Override
  public Calculation getCalculation(String calculationName, String configuration) {

    if (calculationName != null) {
      Class<? extends Calculation> clazz = calculations.get(calculationName);
      if (clazz != null) {
        try {
          return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
        }
      }
    }
    return null;
  }
}
