package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterfaceCategory13Section1;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.stereotype.Component;

@Component
public class MQAgeDimensions {

  public CohortDefinitionDimension getDimension() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "15+",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus(15),
            mappings));

    dimension.addCohortDefinition(
        "0-4",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(0, 4),
            mappings));

    dimension.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(5, 9),
            mappings));

    dimension.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(10, 14),
            mappings));

    dimension.addCohortDefinition(
        "2-14",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(2, 14),
            mappings));

    return dimension;
  }

  @DocumentedDefinition(
      value = "findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus")
  public CohortDefinition findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus(
      int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13Section1.QUERY
            .findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus(age);

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation")
  public CohortDefinition findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
      int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13Section1.QUERY
            .findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(startAge, endAge);

    definition.setQuery(query);

    return definition;
  }
}
