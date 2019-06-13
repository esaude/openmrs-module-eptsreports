package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class OnArtForAtleastXmonthsCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    CalculationResultMap map = new CalculationResultMap();
    System.out.println("The passed date >>>" + onOrBefore);

    CalculationResultMap arvsInitiationDateMap =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            context);
    for (Integer ptId : cohort) {
      boolean onArtAtleastMoths = false;
      SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(ptId);
      if (artStartDateResult != null) {
        Date artStartDate = (Date) artStartDateResult.getValue();
        if (artStartDate != null && onOrBefore != null) {
          Date onArt12Months = EptsCalculationUtils.addMonths(artStartDate, 12);
          Date onArt6Months = EptsCalculationUtils.addMonths(artStartDate, 6);

          if (onArt12Months.compareTo(onOrBefore) >= 0 || onArt6Months.compareTo(onOrBefore) >= 0) {
            onArtAtleastMoths = true;
          }
        }
      }
      map.put(ptId, new BooleanResult(onArtAtleastMoths, this));
    }
    return map;
  }
}
