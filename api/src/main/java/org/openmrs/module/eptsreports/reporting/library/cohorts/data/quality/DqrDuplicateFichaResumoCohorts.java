package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.duplicate.Ec1Queries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.query.encounter.definition.EncounterQuery;
import org.openmrs.module.reporting.query.encounter.definition.SqlEncounterQuery;
import org.springframework.stereotype.Component;

@Component
public class DqrDuplicateFichaResumoCohorts {

  /**
   * The patient has more than one Ficha Resumo registered in OpenMRS EPTS at the same Health
   * Facility
   *
   * @param encounterType
   * @return org.openmrs.module.reporting.cohort.definition.CohortDefinition
   */
  public CohortDefinition getDuplicatePatientsForFichaResumo(int encounterType) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get duplicate patients for ficha resumo");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.setQuery(Ec1Queries.getEc1FichaResumoDuplicates(encounterType));
    return cd;
  }

  /**
   * Get duplicate encounters based on an encounter type
   *
   * @param encounterType
   * @return EncounterQuery
   */
  public EncounterQuery getEncounterQuery(int encounterType) {
    SqlEncounterQuery sqlEncounterQuery = new SqlEncounterQuery();
    sqlEncounterQuery.setName("Get duplicate encounter query");
    sqlEncounterQuery.addParameter(new Parameter("location", "Location", Location.class));
    sqlEncounterQuery.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlEncounterQuery.setQuery(Ec1Queries.getDuplicateEncounters(encounterType));
    return sqlEncounterQuery;
  }
}
