/** */
package org.openmrs.module.eptsreports.reporting.calculation.keypopulation;

import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class HomosexualCalculation extends KeyPopulationCalculation {

  @Override
  public void keyPopulation(
      final CalculationResultMap resultMap, final Integer patientId, final String value) {

    if (value.equals("HSH") || value.equals("MSM") || value.equals("1377")) {
      resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
    }
  }
}
