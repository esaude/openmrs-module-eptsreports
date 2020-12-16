package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.QualityImprovementQueriesInterfaceCategory13Section2;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13Section2CohortQueries {

  @Autowired private MQCategory13Section1CohortQueries mQCategory13Section1CohortQueries;

  @DocumentedDefinition(
      value = "findPatientsWithLastClinicalConsultationwhoAreInSecondLineDenominatorB2")
  public CohortDefinition
      findPatientsWithLastClinicalConsultationwhoAreInSecondLineDenominatorB2() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13Section2.QUERY
            .findPatientsWithLastClinicalConsultationwhoAreInSecondLineDenominatorB2;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(
      value = "findPatientsWithLastClinicalConsultationwhoAreNotInSecondLineDenominatorB2E")
  public CohortDefinition
      findPatientsWithLastClinicalConsultationwhoAreNotInSecondLineDenominatorB2E() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        QualityImprovementQueriesInterfaceCategory13Section2.QUERY
            .findPatientsWithLastClinicalConsultationwhoAreNotInSecondLineDenominatorB2E;

    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findDenominatorCategory13SectionIIB")
  public CohortDefinition findDenominatorCategory13SectionIIB() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findDenominatorCategory13SectionIIB");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            mQCategory13Section1CohortQueries
                .findPatientsWithLastClinicalConsultationDenominatorB1(),
            mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationwhoAreInSecondLineDenominatorB2(),
            mappings));

    definition.addSearch(
        "B2E",
        EptsReportUtils.map(
            this.findPatientsWithLastClinicalConsultationwhoAreNotInSecondLineDenominatorB2E(),
            mappings));

    definition.addSearch(
        "B4E",
        EptsReportUtils.map(
            mQCategory13Section1CohortQueries.findPatientsWithCVDenominatorB4E(), mappings));

    definition.addSearch(
        "B5E",
        EptsReportUtils.map(
            mQCategory13Section1CohortQueries.findPatientsWithRequestCVDenominatorB5E(), mappings));

    definition.setCompositionString("B1 AND ((B2 NOT B2E)  NOT (B4E OR B5E))");

    return definition;
  }

  @DocumentedDefinition(value = "findFinalNumeratorCategory13SectionIIC")
  public CohortDefinition findFinalNumeratorCategory13SectionIIC() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("findFinalNumeratorCategory13SectionIIC");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "B", EptsReportUtils.map(this.findDenominatorCategory13SectionIIB(), mappings));

    definition.addSearch(
        "C",
        EptsReportUtils.map(
            mQCategory13Section1CohortQueries.findNumeratorCategory13Section1C(), mappings));

    definition.setCompositionString("B AND C");

    return definition;
  }
}
