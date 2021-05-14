package org.openmrs.module.eptsreports.reporting.calculation.util.processor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Cohort;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.stereotype.Component;

@Component
public class TxRttNextFilaUntilEndDateProcessor {

  public ListMap<Integer, Obs> getResutls(List<Integer> cohort, EvaluationContext context) {

    if (cohort == null || cohort.isEmpty()) {
      return new ListMap<>();
    }
    Location location = (Location) context.getParameterValues().get("location");
    Date enDate = (Date) context.getParameterValues().get("enDate");

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EvaluationContext newContext = new EvaluationContext();
    newContext.setBaseCohort(new Cohort(cohort));

    HqlQueryBuilder q = new HqlQueryBuilder();
    q.select("o.personId", "o");
    q.from(Obs.class, "o");
    q.innerJoin("fetch o.encounter", "e");
    q.wherePersonIn("o.personId", newContext);
    q.whereEqual("o.concept", hivMetadata.getReturnVisitDateForArvDrugConcept());

    q.whereIn(
        "o.encounter.encounterType", Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()));
    q.whereIn("o.encounter.location", Arrays.asList(location));
    q.whereNotNull("o.valueDatetime");
    q.whereLessOrEqualTo("o.valueDatetime", enDate);
    q.orderDesc("o.obsDatetime");

    List<Object[]> queryResult =
        Context.getRegisteredComponents(EvaluationService.class)
            .get(0)
            .evaluateToList(q, newContext);

    ListMap<Integer, Obs> obsForPatients = new ListMap<Integer, Obs>();
    for (Object[] row : queryResult) {
      obsForPatients.putInList((Integer) row[0], (Obs) row[1]);
    }

    return obsForPatients;
  }
}
