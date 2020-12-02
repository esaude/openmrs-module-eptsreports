package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArt() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
        definition.addParameter(new Parameter("endRevisionDate", "End Revision Date",
     Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdult;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultRF11")
  public CohortDefinition findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultRF11() {

    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Tx NEW");
    defeinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    defeinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    defeinition.addParameter(new Parameter("location", "Location", Location.class));
    defeinition.addParameter(new Parameter("dataFinalAvaliacao", "dataFinalAvaliacao", Date.class));

    final String mappings =
            "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearAdult),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    defeinition.setCompositionString("START-ART NOT (TRANSFERED-IN)");

    return defeinition;
  }

  public CohortDefinition
      findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInChildrenRF13() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Tx NEW");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildren),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    defeinition.setCompositionString("START-ART NOT (TRANSFERED-IN)");

    return defeinition;
  }

  public CohortDefinition findPatientsNewlyEnrolledByAgeMinor14InAPeriodRF15() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Tx NEW");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnARTByAgeUsingYearChildren),
            mappings));

    defeinition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    defeinition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "PREGNANT",
                QualityImprovementQueriesInterface.QUERY.findPatientsWhoArePregnantInclusionDate),
            mappings));

    defeinition.setCompositionString("START-ART NOT (TRANSFERED-IN OR PREGNANT)");

    return defeinition;
  }

  public CohortDefinition findPatientsNewlyEnrolledByAgeAndNutritionalAssessmentInAPeriodRF16() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();

    defeinition.setName("Tx NEW");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    defeinition.addSearch(
        "RF15",
        EptsReportUtils.map(this.findPatientsNewlyEnrolledByAgeMinor14InAPeriodRF15(), mappings));

    defeinition.addSearch(
        "NUTRITIONAL_ASSESSMENT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "NUTRITIONAL_ASSESSMENT",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoHasNutritionalAssessmentInLastConsultation),
            mappings));

    defeinition.setCompositionString("RF15 AND NUTRITIONAL_ASSESSMENT)");

    return defeinition;
  }

  public CohortDefinition
      findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriod() {
    final CompositionCohortDefinition defeinition = new CompositionCohortDefinition();
    defeinition.setName("First Consult IN Inclusion Period");
    defeinition.addParameter(
        new Parameter("startInclusionDate", "Start Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("endInclusionDate", "End Inclusion Date", Date.class));
    defeinition.addParameter(new Parameter("location", "location", Location.class));
    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},location=${location}";
    defeinition.addSearch(
        "RF12",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "RF12",
                QualityImprovementQueriesInterface.QUERY
                    .findPatientsWhoAreNewEnrolledOnArtByAgeUsingYearAdulyAndHaveFirstConsultInclusionPeriod),
            mappings));
    defeinition.addSearch(
        "RF11",
        EptsReportUtils.map(
            this.findPatientsNewlyEnrolledByAgeInAPeriodExcludingTrasferedInAdultRF11(), mappings));
    defeinition.setCompositionString("RF12 AND RF11");
    return defeinition;
  }
}
