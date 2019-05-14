package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsMonthlySummaryHivCohortQueries {

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  public CohortDefinition getRegisteredInPreArtBooks1and2() {
    return genericCohortQueries.hasCodedObs(
        hivMetadata.getRecordPreArtFlowConcept(),
        BaseObsCohortDefinition.TimeModifier.ANY,
        SetComparator.IN,
        Arrays.asList(hivMetadata.getPreArtEncounterType()),
        null);
  }

  public CohortDefinition getNewlyEnrolled() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    cd.addSearch("LIVRO", map(getRegisteredInPreArtBooks1and2(), mappings));

    CohortDefinition transferredToArtCare =
        hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility();
    cd.addSearch("TRANSFERIDOSDE", mapStraightThrough(transferredToArtCare));

    cd.setCompositionString("LIVRO NOT TRANSFERIDOSDE");

    return cd;
  }

  public CohortDefinition getEnrolledByTransfer() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mappings = "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}";
    cd.addSearch("INSCRITOS", map(getRegisteredInPreArtBooks1and2(), mappings));

    CohortDefinition transferredToArtCare =
        hivCohortQueries.getPatientsInArtCareTransferredFromOtherHealthFacility();
    cd.addSearch("TRANSFERIDOS", mapStraightThrough(transferredToArtCare));

    cd.setCompositionString("INSCRITOS AND TRANSFERIDOS");

    return cd;
  }
}
