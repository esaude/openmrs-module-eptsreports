package org.openmrs.module.eptsreports.reporting.calculation.tbprev;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.CalculationProcessorUtils;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTbrevEndINHProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxTbEndINHCalculation extends BaseFghCalculation {

  private static int DAYS_TPI = 173;

  @Autowired private TXTbrevEndINHProcessor tXTbrevEndINHProcessor;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    Map<Integer, Date> resutlsInitINH = tXTbrevEndINHProcessor.getResutls(context);

    Map<Integer, Date> finalMap = addAllMaps(context);

    Map<Integer, Date> map = new HashMap<>();

    if (!resutlsInitINH.isEmpty() && !finalMap.isEmpty()) {

      for (Integer patientId : resutlsInitINH.keySet()) {

        Date startINH =
            DateUtil.adjustDate(resutlsInitINH.get(patientId), DAYS_TPI, DurationUnit.DAYS);

        Date endINH = finalMap.get(patientId);

        if (endINH != null && startINH != null) {
          if (endINH.after(startINH)) {
            map.put(patientId, endINH);
          }
        }
      }
      if (!map.isEmpty()) {
        for (Entry<Integer, Date> entry : map.entrySet()) {
          Integer patientId = entry.getKey();
          resultMap.put(patientId, new BooleanResult(Boolean.TRUE, this));
        }
      }
    }
    return resultMap;
  }

  @SuppressWarnings("unchecked")
  public Map<Integer, Date> addAllMaps(EvaluationContext context) {

    Map<Integer, Date> firstResults =
        tXTbrevEndINHProcessor.processPatientsByMaxInFichaClinicaFichaResumoFichaSeguimento(
            context, tXTbrevEndINHProcessor.getResutls(context));

    Map<Integer, Date> resultsixFichaClinicaEncouters =
        tXTbrevEndINHProcessor.processPatientsByMaxReportingPeriodSixEncounters(
            context, tXTbrevEndINHProcessor.getResutls(context));

    Map<Integer, Date> resultTwoFlits =
        tXTbrevEndINHProcessor.findMaxEncounterDateByPatientMin2FlitsEncounters(
            context, tXTbrevEndINHProcessor.getResutls(context));

    Map<Integer, Date> resultForMin6Flits =
        tXTbrevEndINHProcessor.findMaxEncounterDateByPatientMin6FlitsEncounters(
            context, tXTbrevEndINHProcessor.getResutls(context));

    Map<Integer, Date> resultForMin2EncountersFichaClinica =
        tXTbrevEndINHProcessor.findMaxEncounterDateByPatientMin2Encounters(
            context, tXTbrevEndINHProcessor.getResutls(context));

    return CalculationProcessorUtils.getMaxMapDateByPatient(
        firstResults,
        resultsixFichaClinicaEncouters,
        resultTwoFlits,
        resultForMin6Flits,
        resultForMin2EncountersFichaClinica);
  }
}
