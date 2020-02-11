package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class LastFilaProcessor {

  public Map<Integer, Date> getLastLevantamentoOnFila(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "Select p.patient_id,max(encounter_datetime) data_fila from patient p "
                + "			inner join encounter e on e.patient_id=p.patient_id "
                + "	where 	p.voided=0 and e.voided=0 and e.encounter_type=18 and "
                + "			e.location_id= :location and e.encounter_datetime<= :endDate group by p.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public Map<Integer, Date> getLastTipoDeLevantamentoOnFichaClinicaMasterCard(
      EvaluationContext context, Integer conceptId, Integer valueCodedId) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                "select max_ficha.patient_id, max_ficha.data_levantamento from( "
                    + " (Select p.patient_id,max(e.encounter_datetime) data_levantamento "
                    + "	from 	patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "			inner join obs o on o.encounter_id=e.encounter_id "
                    + "	where 	e.voided=0 and o.voided=0 and p.voided=0 and "
                    + "			e.encounter_type =6 and o.concept_id=%s and o.value_coded=%s and "
                    + "			e.encounter_datetime<=:endDate and e.location_id=:location "
                    + "	group by p.patient_id ) max_ficha "
                    + "inner join patient patient_without_fila on patient_without_fila.patient_id = max_ficha.patient_id) ",
                conceptId, valueCodedId),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public Map<Integer, Date> getLastMarkedInModelosDiferenciadosDeCuidadosOnFichaClinicaMasterCard(
      EvaluationContext context, Integer conceptId) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            String.format(
                "select max_ficha.patient_id, max_ficha.data_marcado from( "
                    + " (Select p.patient_id,max(e.encounter_datetime) data_marcado "
                    + " from 	patient p inner join encounter e on p.patient_id=e.patient_id "
                    + "		inner join obs o on o.encounter_id=e.encounter_id "
                    + " where 	e.voided=0 and o.voided=0 and p.voided=0 and "
                    + "		e.encounter_type =6 and o.concept_id=%s and o.value_coded in(1256,1257) and "
                    + "		e.encounter_datetime<=:endDate and e.location_id=:location "
                    + " group by p.patient_id ) max_ficha "
                    + " inner join patient patient_without_fila on patient_without_fila.patient_id = max_ficha.patient_id)",
                conceptId),
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }

  public List<Object[]> getMaxFilaWithProximoLevantamento(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            " select patient_id, last_levantamento, max(proximo_levantamento) from( select last_fila.patient_id, last_fila.last_levantamento, obs_proximo_levantamento.value_datetime proximo_levantamento from "
                + "	(select p.patient_id, max(e.encounter_datetime) last_levantamento from patient p "
                + "	inner join encounter e on e.patient_id = p.patient_id "
                + "	where p.voided = 0 and e.voided = 0 and e.encounter_type = 18 "
                + "	   and e.location_id =:location and e.encounter_datetime <=:endDate "
                + "	   group by p.patient_id) last_fila "
                + "	   inner join obs obs_proximo_levantamento on obs_proximo_levantamento.person_id = last_fila.patient_id "
                + "	   and obs_proximo_levantamento.obs_datetime = last_fila.last_levantamento and "
                + "	   obs_proximo_levantamento.concept_id = 5096 and obs_proximo_levantamento.voided = 0 "
                + "	   order by last_fila.patient_id ) plastiFila group by patient_id ",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToList(qb, context);
  }
}
