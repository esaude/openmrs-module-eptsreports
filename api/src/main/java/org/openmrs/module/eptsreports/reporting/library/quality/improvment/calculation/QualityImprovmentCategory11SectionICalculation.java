package org.openmrs.module.eptsreports.reporting.library.quality.improvment.calculation;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsDateUtil;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

/** Calculador para Seccao I da categoria 11 */
@Component
public class QualityImprovmentCategory11SectionICalculation extends BaseFghCalculation {

  @Override
  public CalculationResultMap evaluate(
      Map<String, Object> parameterValues, EvaluationContext context) {
    CalculationResultMap resultMap = new CalculationResultMap();

    List<Integer> cohort = this.findMinPatientChildrenAPSSConsultation(context);
    ListMap<Integer, Date> patientConsultations = this.getRelatedConsultations(context, cohort);

    for (Entry<Integer, List<Date>> entry : patientConsultations.entrySet()) {
      if (entry.getValue().size() < 9) {
        continue;
      }
      ListIterator<Date> dateIterator = entry.getValue().listIterator();
      Date previousDate = dateIterator.next();
      boolean constantDelta = true;

      while (dateIterator.hasNext() && constantDelta) {
        Date nextDate = dateIterator.next();
        int daysBetween = EptsDateUtil.getDaysBetween(previousDate, nextDate);

        // TODO: o Algoritmo considera que se a diferenca das datas for igual a zero,
        // entao pode ser que seja data quality. Confirmar com Eurico
        if (daysBetween != 0 && (daysBetween < 25 || daysBetween > 33)) {
          constantDelta = false;
        }
        previousDate = nextDate;
      }
      if (constantDelta) {
        resultMap.put(entry.getKey(), new BooleanResult(Boolean.TRUE, this));
      }
    }
    return resultMap;
  }

  private List<Integer> findMinPatientChildrenAPSSConsultation(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            MQQueriesInterface.QUERY
                .findFirstPatientChildrenAPSSConsultationWithinInclusionReportingPeriod,
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, Integer.class, context);
  }

  /**
   * Busca todas consultas dos pacientes da coorte passada no intervalo dataInicialDeInclusao e
   * dataFinalDeRevisao
   *
   * @param context
   * @param cohort
   * @return
   */
  public ListMap<Integer, Date> getRelatedConsultations(
      EvaluationContext context, List<Integer> cohort) {

    if (cohort == null || cohort.isEmpty()) {
      return new ListMap<>();
    }
    Location location = (Location) context.getParameterValues().get("location");
    Date startInclusionDate =
        (Date) context.getParameterValues().get(EptsReportConstants.START_INCULSION_DATE);
    Date endRevisionDate =
        (Date) context.getParameterValues().get(EptsReportConstants.END_REVISION_DATE);

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EvaluationContext newContext = new EvaluationContext();
    newContext.setBaseCohort(new Cohort(cohort));

    HqlQueryBuilder q = new HqlQueryBuilder();
    q.select("e", "e");
    q.from(Encounter.class, "e");
    q.wherePatientIn("e.patient.patientId", newContext)
        .and()
        .whereEqual("e.encounterType", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType())
        .and()
        .whereEqual("e.voided", false)
        .and()
        .whereGreaterOrEqualTo("e.encounterDatetime", startInclusionDate)
        .and()
        .whereLessOrEqualTo("e.encounterDatetime", endRevisionDate)
        .and()
        .whereEqual("e.location", location)
        .orderAsc("e.encounterDatetime");

    List<Object[]> queryResult =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToList(q, newContext);

    ListMap<Integer, Date> encounterPatientes = new ListMap<Integer, Date>();
    for (Object[] row : queryResult) {
      Encounter encounter = (Encounter) row[0];
      encounterPatientes.putInList(
          encounter.getPatient().getId(), encounter.getEncounterDatetime());
    }
    return encounterPatientes;
  }
}
