/** */
package org.openmrs.module.eptsreports.reporting.calculation.keypopulation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.BaseFghCalculation;
import org.openmrs.module.eptsreports.reporting.library.queries.KeyPopulationQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

/** @author Stélio Moiane */
public abstract class KeyPopulationCalculation extends BaseFghCalculation {

  private static final int MEDICAL_RECORD = 6;

  private static final int APSS_RECORD = 35;

  @Autowired private HivMetadata hivMetadata;

  @Override
  public CalculationResultMap evaluate(
      final Map<String, Object> parameterValues, final EvaluationContext context) {

    final CalculationResultMap resultMap = new CalculationResultMap();

    final EvaluationService evaluationService =
        Context.getRegisteredComponents(EvaluationService.class).get(0);

    final String kp =
        String.format(
            KeyPopulationQueries.QUERY.findKeyPopulationPatients,
            hivMetadata.getPersonAttributeType().getId());

    final List<Integer> keyPopulationPatients =
        evaluationService.evaluateToList(
            new SqlQueryBuilder(kp, context.getParameterValues()), Integer.class, context);

    if (context.getBaseCohort() != null) {
      keyPopulationPatients.retainAll(context.getBaseCohort().getMemberIds());
    }

    for (final Integer patientId : keyPopulationPatients) {

      final String query =
          String.format(
              KeyPopulationQueries.QUERY.findFilledKeyPopulationByPatient,
              patientId,
              hivMetadata.getPersonAttributeType().getId(),
              patientId);

      final List<Object[]> evaluateToList =
          evaluationService.evaluateToList(
              new SqlQueryBuilder(query, context.getParameterValues()), context);

      if (!evaluateToList.isEmpty()) {

        if (evaluateToList.size() == 1) {
          final Object[] filledKeyPopulation = evaluateToList.get(0);
          final String value = (String) filledKeyPopulation[1];

          this.keyPopulation(resultMap, patientId, value);
        } else {

          if (this.hasTheSameDate(evaluateToList)) {
            final Object[] sourceKeyPopulation =
                this.findBySourceType(evaluateToList, MEDICAL_RECORD, APSS_RECORD);
            if (sourceKeyPopulation != null) {

              final String value = (String) sourceKeyPopulation[1];

              this.keyPopulation(resultMap, patientId, value);
            }
          } else {

            final Object[] sourceKeyPopulation = evaluateToList.get(0);

            final String value = (String) sourceKeyPopulation[1];

            this.keyPopulation(resultMap, patientId, value);
          }
        }
      }
    }

    return resultMap;
  }

  public abstract void keyPopulation(
      final CalculationResultMap resultMap, final Integer patientId, final String value);

  private Object[] findBySourceType(
      final List<Object[]> keyPopulations, final int firstSource, final int lastSource) {

    for (final Object[] sourceKeyPopulation : keyPopulations) {
      final int sourceType = (int) sourceKeyPopulation[2];

      if (sourceType == firstSource) {
        return sourceKeyPopulation;
      }
    }

    for (final Object[] sourceKeyPopulation : keyPopulations) {
      final int sourceType = (int) sourceKeyPopulation[2];

      if (sourceType == lastSource) {
        return sourceKeyPopulation;
      }
    }

    return null;
  }

  private boolean hasTheSameDate(final List<Object[]> filledKeyPopulations) {
    final Object[] firstKeyPopulation = filledKeyPopulations.get(0);

    for (final Object[] filledKeyPopulation : filledKeyPopulations) {
      final Date firstKeyPopulationDate = (Date) firstKeyPopulation[0];
      final Date filledKeyPopulationDate = (Date) filledKeyPopulation[0];

      final long days =
          EptsReportUtils.getDifferenceInDaysBetweenDates(
              firstKeyPopulationDate, filledKeyPopulationDate);
      if (days != 0) {
        return false;
      }
    }

    return true;
  }
}
