package org.openmrs.module.eptsreports.reporting.library.evaluators;

import org.openmrs.annotation.Handler;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PatientsWithStartDrugsObsCohortDefinition;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Handler(supports = PatientsWithStartDrugsObsCohortDefinition.class)
public class PatientsWithStartDrugsObsCohortDefinitionEvaluator implements CohortDefinitionEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context)
	        throws EvaluationException {
		PatientsWithStartDrugsObsCohortDefinition cd = (PatientsWithStartDrugsObsCohortDefinition) cohortDefinition;
		context = ObjectUtil.nvl(context, new EvaluationContext());
		EvaluatedCohort ret = new EvaluatedCohort(null, cd, context);
		
		SqlQueryBuilder q = new SqlQueryBuilder();
		
		q.append("select p.patient_id");
		q.append("from patient p");
		q.append("join encounter e");
		q.append("on p.patient_id = e.patient_id");
		q.append("join obs o");
		q.append("on e.encounter_id = o.encounter_id");
		q.append("where e.voided = 0 and o.voided = 0 and p.voided = 0");
		
		q.append("and e.encounter_type in (:encounterTypes)").addParameter("encounterTypes",
		    Arrays.asList(cd.getArvPediatriaSeguimento().getEncounterTypeId(), cd.getAdultoSeguimento().getEncounterTypeId(),
		        cd.getArvPharmacia().getEncounterTypeId()));
		
		q.append("and o.concept_id = :conceptId").addParameter("conceptId", cd.getArvPlan().getConceptId());
		
		q.append("and o.value_coded = :valueCoded").addParameter("valueCoded", cd.getStartDrugs().getConceptId());
		
		if (cd.getOnOrBefore() != null) {
			q.append("and e.encounter_datetime <= :onOrBefore").addParameter("onOrBefore",
			    DateUtil.getEndOfDayIfTimeExcluded(cd.getOnOrBefore()));
		}
		
		q.append("group by p.patient_id");
		
		List<Integer> results = evaluationService.evaluateToList(q, Integer.class, context);
		ret.setMemberIds(new HashSet<Integer>(results));
		
		return ret;
	}
}
