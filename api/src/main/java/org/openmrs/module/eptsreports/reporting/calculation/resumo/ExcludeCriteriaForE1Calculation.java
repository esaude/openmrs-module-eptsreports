package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class ExcludeCriteriaForE1Calculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    Location location = (Location) context.getFromCache("location");
    Date startDate = (Date) context.getFromCache("onOrAfter");
    Date endtDate = (Date) context.getFromCache("onOrBefore");

    Date requiredDate = null;

    CalculationResultMap map = new CalculationResultMap();
    // External Dependencies
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    EPTSCalculationService ePTSCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);

    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.DAY_OF_MONTH);

    if (!(day == 21 && month == 11)) {
      requiredDate = EptsCalculationUtils.addMonths(startDate, -12);
    }

    CalculationResultMap calculationResultMap =
        ePTSCalculationService.getObs(
            hivMetadata.getApplicationForLaboratoryResearch(),
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
            cohort,
            Arrays.asList(location),
            Arrays.asList(hivMetadata.getHivViralLoadConcept()),
            TimeQualifier.ANY,
            requiredDate,
            context);
    for (Integer pId : cohort) {
      boolean toExclude = false;
      ListResult listResult = (ListResult) calculationResultMap.get(pId);
      List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
      for (Obs obs : obsList) {
        if (obs.getEncounter() != null
            && obs.getEncounter().getEncounterDatetime() != null
            && obs.getEncounter().getEncounterDatetime().compareTo(endtDate) <= 0) {
          toExclude = true;
        }
      }
      map.put(pId, new BooleanResult(toExclude, this));
    }
    return map;
  }
}
