package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

@Component
public class ExcludeCriteriaForECalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    Location location = (Location) context.getFromCache("location");
    Date startDate = (Date) context.getFromCache("onOrAfter");

    Date requiredDate;

    CalculationResultMap map = new CalculationResultMap();
    // External Dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);

    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    Concept concept = (Concept) parameterValues.get("concept");
    EncounterType encounterType = (EncounterType) parameterValues.get("encounterType");
    String type = (String) parameterValues.get("type");

    if (!(day == 21 && month == 11)) {
      requiredDate = EptsCalculationUtils.addMonths(startDate, -12);
    } else {
      requiredDate = startDate;
    }
    CalculationResultMap calculationResultMap =
        ePTSCalculationService.getObs(
            concept,
            Arrays.asList(encounterType),
            cohort,
            Arrays.asList(location),
            null,
            TimeQualifier.ANY,
            requiredDate,
            context);
    for (Integer pId : cohort) {
      boolean toExclude = false;

      ListResult listResult = (ListResult) calculationResultMap.get(pId);
      List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
      for (Obs obs : obsList) {
        if (type.equals("CODED")
            && obs.getValueCoded() != null
            && obs.getValueCoded().equals(hivMetadata.getHivViralLoadConcept())) {
          toExclude = true;
          break;
        } else if (type.equals("CODED") && obs.getValueCoded() != null) {
          toExclude = true;
          break;
        } else if (type.equals("NUMERIC") && obs.getValueNumeric() != null) {
          toExclude = true;
          break;
        }
      }
      if (calculationResultMap.containsKey(pId)) {
        toExclude = true;
      }
      map.put(pId, new BooleanResult(toExclude, this));
    }
    return map;
  }
}
