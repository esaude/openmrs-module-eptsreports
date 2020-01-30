package org.openmrs.module.eptsreports.reporting.calculation.txcurr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.util.processor.LastFilaProcessor;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public abstract class TxCurrPatientsOnArtOnArvDispenseIntervalsCalculation
    extends BaseFghCalculation {

  private static int CONCEPT_TYPE_OF_DISPENSATION = 23739;
  private static int CONCEPT_MONTHLY = 1098;
  private static int CONCEPT_QUARTERLY = 23720;
  private static int CONCEPT_QUARTERLY_DISPENSATION = 23730;
  private static int CONCEPT_SEMESTER_ARV_PICKUP = 23888;

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {

    List<Object[]> maxFilas =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getMaxFilaWithProximoLevantamento(context);
    return calculateDisagregration(context, maxFilas);
  }

  @SuppressWarnings("unchecked")
  private CalculationResultMap calculateDisagregration(
      EvaluationContext context, List<Object[]> maxFilas) {
    CalculationResultMap resultMap = new CalculationResultMap();

    Map<Integer, PatientDisaggregated> filaPorIntervaloDeDesagregacao =
        this.getFilaDisaggregations(maxFilas);
    Map<Integer, PatientDisaggregated> levantamentoMensal = this.getAllLevantamentoMensal(context);
    Map<Integer, PatientDisaggregated> levantamentoTrimestral =
        this.getAllLevantamentoTrimestral(context);
    Map<Integer, PatientDisaggregated> levantamentoSemestral =
        this.getAllLevantamentoSemestral(context);
    Map<Integer, PatientDisaggregated> modeloDiferenciadoTrimestral =
        this.getAllModeloDiferenciadoTrimestral(context);
    Map<Integer, PatientDisaggregated> modeloDiferenciadoSemestral =
        this.getAllModeloDiferenciadoSemestral(context);

    Set<Integer> allPatients = new HashSet<>();
    allPatients.addAll(filaPorIntervaloDeDesagregacao.keySet());
    allPatients.addAll(levantamentoMensal.keySet());
    allPatients.addAll(levantamentoTrimestral.keySet());
    allPatients.addAll(levantamentoSemestral.keySet());
    allPatients.addAll(modeloDiferenciadoTrimestral.keySet());
    allPatients.addAll(modeloDiferenciadoSemestral.keySet());

    for (Integer patientId : allPatients) {

      List<PatientDisaggregated> allPatientDisaggregated =
          this.getNonNullPatientDisaggregated(
              patientId,
              filaPorIntervaloDeDesagregacao,
              levantamentoMensal,
              levantamentoTrimestral,
              levantamentoSemestral,
              modeloDiferenciadoTrimestral,
              modeloDiferenciadoSemestral);

      this.evaluateDisaggregatedPatients(patientId, resultMap, allPatientDisaggregated);
    }
    return resultMap;
  }

  protected abstract Map<Integer, PatientDisaggregated> getFilaDisaggregations(
      List<Object[]> maxFilas);

  protected abstract void evaluateDisaggregatedPatients(
      Integer patientId,
      CalculationResultMap resultMap,
      List<PatientDisaggregated> allPatientDisaggregated);

  protected Map<Integer, PatientDisaggregated> getAllLevantamentoMensal(EvaluationContext context) {
    Map<Integer, Date> map =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_MONTHLY));
    Map<Integer, PatientDisaggregated> result = new HashMap<>();
    for (Integer patientId : map.keySet()) {
      result.put(patientId, new DispensaMensalPatientDisaggregated(patientId, map.get(patientId)));
    }
    return result;
  }

  protected Map<Integer, PatientDisaggregated> getAllLevantamentoTrimestral(
      EvaluationContext context) {
    Map<Integer, Date> map =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_QUARTERLY));
    Map<Integer, PatientDisaggregated> result = new HashMap<>();
    for (Integer patientId : map.keySet()) {
      result.put(
          patientId, new DispensaTrimestralPatientDisaggregated(patientId, map.get(patientId)));
    }
    return result;
  }

  protected Map<Integer, PatientDisaggregated> getAllLevantamentoSemestral(
      EvaluationContext context) {
    Map<Integer, Date> map =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
                context,
                Integer.valueOf(CONCEPT_TYPE_OF_DISPENSATION),
                Integer.valueOf(CONCEPT_SEMESTER_ARV_PICKUP));
    Map<Integer, PatientDisaggregated> result = new HashMap<>();
    for (Integer patientId : map.keySet()) {
      result.put(
          patientId, new DispensaSemestralPatientDisaggregated(patientId, map.get(patientId)));
    }
    return result;
  }

  protected Map<Integer, PatientDisaggregated> getAllModeloDiferenciadoTrimestral(
      EvaluationContext context) {
    Map<Integer, Date> map =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(
                context, CONCEPT_QUARTERLY_DISPENSATION);
    Map<Integer, PatientDisaggregated> result = new HashMap<>();
    for (Integer patientId : map.keySet()) {
      result.put(
          patientId,
          new ModeloDiferenciadoTrimestralPatientDisaggregated(patientId, map.get(patientId)));
    }
    return result;
  }

  protected Map<Integer, PatientDisaggregated> getAllModeloDiferenciadoSemestral(
      EvaluationContext context) {
    Map<Integer, Date> map =
        Context.getRegisteredComponents(LastFilaProcessor.class)
            .get(0)
            .getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(
                context, CONCEPT_SEMESTER_ARV_PICKUP);
    Map<Integer, PatientDisaggregated> result = new HashMap<>();
    for (Integer patientId : map.keySet()) {
      result.put(
          patientId,
          new ModeloDiferenciadoSemestralPatientDisaggregated(patientId, map.get(patientId)));
    }
    return result;
  }

  protected PatientDisaggregated getMaxPatientDisaggregated(List<PatientDisaggregated> patients) {
    Date maxDate = DateUtil.getDateTime(Integer.MAX_VALUE, 1, 1);
    PatientDisaggregated auxDisaggregated = null;

    for (PatientDisaggregated pDisaggregated : patients) {
      if (pDisaggregated.getDate().compareTo(maxDate) > 0) {
        maxDate = pDisaggregated.getDate();
        auxDisaggregated = pDisaggregated;
      }
    }
    return auxDisaggregated;
  }

  @SuppressWarnings("unchecked")
  protected List<PatientDisaggregated> getNonNullPatientDisaggregated(
      Integer patientId, Map<Integer, PatientDisaggregated>... allMaps) {

    List<PatientDisaggregated> result = new ArrayList<>();

    for (Map<Integer, PatientDisaggregated> map : allMaps) {
      PatientDisaggregated pDisaggregated = map.get(patientId);
      if (pDisaggregated != null) {
        result.add(pDisaggregated);
      }
    }
    return result;
  }

  protected boolean havePatientDisagregatedSameDates(List<PatientDisaggregated> patients) {
    List<Date> allDates = new ArrayList<Date>();

    for (PatientDisaggregated pDisaggregated : patients) {
      allDates.add(pDisaggregated.getDate());
    }
    if (!allDates.isEmpty()) {
      return this.hasSameDates(allDates);
    }
    return false;
  }

  private boolean hasSameDates(List<Date> allDates) {
    Date firstDate = allDates.get(0);
    for (Date date : allDates) {
      if (!this.isSameDay(firstDate, date)) {
        return false;
      }
    }
    return true;
  }

  private boolean isSameDay(Date date1, Date date2) {
    if (date1 == null || date2 == null) {
      return false;
    }
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    return DateUtils.isSameDay(cal1, cal2);
  }

  public enum DisaggregationSourceTypes {
    FILA,

    DISPENSA_MENSAL,

    DISPENSA_TRIMESTRAL,

    DISPENSA_SEMESTRAL,

    MODELO_DIFERENCIADO_TRIMESTRAL,

    MODELO_DIFERENCIADO_SEMESTRAL
  }

  public abstract class PatientDisaggregated {

    private Integer patientId;
    private Date date;

    public PatientDisaggregated(Integer patientId, Date date) {
      this.patientId = patientId;
      this.date = date;
    }

    public Integer getPatientId() {
      return patientId;
    }

    public Date getDate() {
      return date;
    }

    public abstract DisaggregationSourceTypes getDisaggregationSourceType();
  }

  public class FilaPatientDisaggregated extends PatientDisaggregated {
    public FilaPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.FILA;
    }
  }

  public class DispensaMensalPatientDisaggregated extends PatientDisaggregated {
    public DispensaMensalPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.DISPENSA_MENSAL;
    }
  }

  public class DispensaTrimestralPatientDisaggregated extends PatientDisaggregated {
    public DispensaTrimestralPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.DISPENSA_TRIMESTRAL;
    }
  }

  public class DispensaSemestralPatientDisaggregated extends PatientDisaggregated {
    public DispensaSemestralPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.DISPENSA_SEMESTRAL;
    }
  }

  public class ModeloDiferenciadoTrimestralPatientDisaggregated extends PatientDisaggregated {
    public ModeloDiferenciadoTrimestralPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.MODELO_DIFERENCIADO_TRIMESTRAL;
    }
  }

  public class ModeloDiferenciadoSemestralPatientDisaggregated extends PatientDisaggregated {
    public ModeloDiferenciadoSemestralPatientDisaggregated(Integer patientId, Date date) {
      super(patientId, date);
    }

    @Override
    public DisaggregationSourceTypes getDisaggregationSourceType() {
      return DisaggregationSourceTypes.MODELO_DIFERENCIADO_SEMESTRAL;
    }
  }
}
