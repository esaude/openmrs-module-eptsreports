package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class LastSeguimentoProcessor {

  public Map<Integer, Date> getResutls(EvaluationContext context) {

    SqlQueryBuilder qb =
        new SqlQueryBuilder(
            "Select p.patient_id,max(encounter_datetime) data_seguimento from patient p "
                + "			inner join encounter e on e.patient_id=p.patient_id "
                + "	where 	p.voided=0 and e.voided=0 and e.encounter_type in (6,9) and "
                + "			e.location_id= :location and e.encounter_datetime<= :endDate "
                + "	group by p.patient_id",
            context.getParameterValues());

    return Context.getRegisteredComponents(EvaluationService.class)
        .get(0)
        .evaluateToMap(qb, Integer.class, Date.class, context);
  }
}
