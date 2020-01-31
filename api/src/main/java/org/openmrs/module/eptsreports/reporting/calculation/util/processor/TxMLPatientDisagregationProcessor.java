package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxMLPatientDisagregationProcessor {

  @Autowired private HivMetadata hivMetadata;

  @SuppressWarnings("unchecked")
  public Map<Integer, Date> getPatientsWhoRefusedOrStoppedTreatmentResults(
      EvaluationContext context, CalculationResultMap numerator) {

    Map<Integer, Date> patientsDeadInHomeVisitForm =
        getPatientsInHomeVisitForm(
            context,
            numerator,
            Arrays.asList(2016),
            Arrays.asList(2005, 2006, 2007, 2010, 23915, 23946, 2015, 2013, 2017));

    return CalculationProcessorUtils.getMaxMapDateByPatient(patientsDeadInHomeVisitForm);
  }

  @SuppressWarnings("unchecked")
  public Map<Integer, Date> getPatienTransferedOutResults(
      EvaluationContext context, CalculationResultMap numerator) {

    Map<Integer, Date> patientsDeadInArtProgram =
        this.getPatientsDeadInArtProgram(
            context,
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId());

    Map<Integer, Date> patientsDeadInHomeVisitForm =
        getPatientsInHomeVisitForm(
            context, numerator, Arrays.asList(2016), Arrays.asList(1706, 23863));

    Map<Integer, Date> deadFichaClinicaAndFichaResumo =
        getPatientDeadInFichaResumoAndClinica(context, 1706);

    return CalculationProcessorUtils.getMaxMapDateByPatient(
        patientsDeadInArtProgram, patientsDeadInHomeVisitForm, deadFichaClinicaAndFichaResumo);
  }

  @SuppressWarnings("unchecked")
  public Map<Integer, Date> getPatientsMarkedAsDeadResults(
      EvaluationContext context, CalculationResultMap numerator) {

    Map<Integer, Date> patientsDeadInArtProgram =
        this.getPatientsDeadInArtProgram(
            context, hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId());
    Map<Integer, Date> patientsDeadInHomeVisitForm =
        getPatientsInHomeVisitForm(
            context, numerator, Arrays.asList(2031, 23944, 23945), Arrays.asList(1383));
    Map<Integer, Date> patientsDeadInDemographicModule =
        getPatientsDeadInDemographicModule(context);

    // 1366
    Map<Integer, Date> deadFichaClinicaAndFichaResumo =
        getPatientDeadInFichaResumoAndClinica(context, 1366);

    return CalculationProcessorUtils.getMaxMapDateByPatient(
        patientsDeadInArtProgram,
        patientsDeadInHomeVisitForm,
        patientsDeadInDemographicModule,
        deadFichaClinicaAndFichaResumo);
  }

  /**
   * Verifica se o paciente esta no map exclusionsToUse
   *
   * @param patientId
   * @param candidateDate
   * @param exclusionsToUse
   * @return
   */
  public static boolean hasDatesGreatherThanEvaluatedDateToExclude(
      Integer patientId, Date candidateDate, CalculationResultMap exclusionsToUse) {

    CalculationResult exclusionDateResult = exclusionsToUse.get(patientId);
    Date exclusionDate =
        (Date) ((exclusionDateResult != null) ? exclusionDateResult.getValue() : null);

    if (exclusionDate != null && exclusionDate.compareTo(candidateDate) > 0) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public static boolean hasPatientsFromOtherDisaggregationToExclude(
      Integer patientId, CalculationResultMap... resultMap) {
    for (CalculationResultMap resultMapItem : resultMap) {
      if (resultMapItem.get(patientId) != null) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  /**
   * DEAD Patients who registered in ART Program SERVICO TARV TRATAMENTO
   *
   * @param context
   * @return
   */
  private Map<Integer, Date> getPatientsDeadInArtProgram(EvaluationContext context, Integer state) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                "select pg.patient_id, max(ps.start_date) data_estado from 	patient p "
                    + "					inner join patient_program pg on p.patient_id=pg.patient_id "
                    + "					inner join patient_state ps on pg.patient_program_id=ps.patient_program_id "
                    + "			where 	pg.voided=0 and ps.voided=0 and p.voided=0 and "
                    + "					pg.program_id=2 and ps.state= %s and ps.end_date is null and "
                    + "					ps.start_date<= :endDate and location_id= :location "
                    + "			group by pg.patient_id",
                state),
            context.getParameterValues());

    context.getParameterValues();

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  private Map<Integer, Date> getPatientsInHomeVisitForm(
      EvaluationContext context,
      CalculationResultMap numerator,
      List<Integer> conceptIds,
      List<Integer> answerIds) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                "select p.patient_id, max(obsHomeSource.obs_datetime) data_estado from patient p "
                    + "					inner join encounter e on p.patient_id=e.patient_id "
                    + "					inner join obs obsHomeSource on e.encounter_id=obsHomeSource.encounter_id "
                    + "			where 	e.voided=0 and p.voided=0 and obsHomeSource.voided=0 and "
                    + "					e.encounter_type in (21) and e.encounter_datetime>= :startDate and e.encounter_datetime<= :endDate  and  e.location_id= :location and "
                    + "					obsHomeSource.concept_id in (%s) and obsHomeSource.value_coded in (%s) "
                    + "			group by p.patient_id",
                StringUtils.join(conceptIds, ","), StringUtils.join(answerIds, ",")),
            context.getParameterValues());

    Map<Integer, Date> result = new HashMap<>();
    Map<Integer, Date> homeVisitResult =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToMap(qb, Integer.class, Date.class, context);

    for (Integer patientId : numerator.keySet()) {
      CalculationResult numeratorResult = numerator.get(patientId);

      if (numeratorResult != null) {
        Date numeratorNextExpectedDate = (Date) numeratorResult.getValue();
        if (numeratorNextExpectedDate != null) {
          Date candidateDate = homeVisitResult.get(patientId);
          if (candidateDate != null) {
            if (candidateDate.compareTo(numeratorNextExpectedDate) >= 0) {
              result.put(patientId, candidateDate);
            }
          }
        }
      }
    }
    return result;
  }

  private Map<Integer, Date> getPatientsDeadInDemographicModule(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "select person_id as patient_id,death_date as data_estado from person "
                + "			where dead=1 and death_date is not null and death_date<= :endDate ",
            context.getParameterValues());
    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  /**
   * Estado no estado de permanencia da ficha resumo and Ficha Clinica
   *
   * @param context
   * @return
   */
  private Map<Integer, Date> getPatientDeadInFichaResumoAndClinica(
      EvaluationContext context, Integer valuedCoded) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                "select p.patient_id, max(o.obs_datetime) data_estado from patient p "
                    + "					inner join encounter e on p.patient_id=e.patient_id "
                    + "					inner join obs  o on e.encounter_id=o.encounter_id "
                    + "			where 	e.voided=0 and o.voided=0 and p.voided=0 and "
                    + "					e.encounter_type in (53,6) and o.concept_id in (6272,6273) and o.value_coded= %s and "
                    + "					o.obs_datetime<= :endDate and e.location_id= :location "
                    + "			group by p.patient_id",
                valuedCoded),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }
}
