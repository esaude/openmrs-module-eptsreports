package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition getRegisteredInPreArtBooks1and2() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordPreArtFlowConcept(),
        BaseObsCohortDefinition.TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getPreArtEncounterType()),
        null);
  }
}
