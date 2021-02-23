package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.GenericMQQueryIntarface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQCategory13Section1QueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.stereotype.Component;

@Component
public class MQAgeDimensions {
  // Dimension for last clinical consultation

  public CohortDefinitionDimension getDimensionForLastClinicalConsultation() {

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
        MQCategory13Section1QueriesInterface.QUERY
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
        MQCategory13Section1QueriesInterface.QUERY
            .findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(startAge, endAge);

    definition.setQuery(query);

    return definition;
  }

  // Dimension Age for new enrrolment on ART

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAdulOrChildren")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAdult(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTBiggerThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAdulOrChildren")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTChildren(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTLessThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(
            startAge, endAge);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(
      int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(
            startAge, endAge);
    definition.setQuery(query);

    return definition;
  }

  public CohortDefinitionDimension getDimensionForPatientsWhoAreNewlyEnrolledOnART() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "15+", EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15), mappings));

    dimension.addCohortDefinition(
        "15-",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTChildren(15), mappings));

    dimension.addCohortDefinition(
        "2-14",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14), mappings));

    dimension.addCohortDefinition(
        "0-4",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4), mappings));

    dimension.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9), mappings));

    dimension.addCohortDefinition(
        "3-14",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14), mappings));

    dimension.addCohortDefinition(
        "<9MONTHS",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9), mappings));

    dimension.addCohortDefinition(
        "0-18M",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18), mappings));

    return dimension;
  }

  // Dimension Age for new enrrolment on ART Until RevisionDate

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAdulOrChildren")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateByAdult(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTTUntilRevisionDateBiggerThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAdulOrChildren")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateChildren(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTTUntilRevisionDateLessThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge")
  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateByAgeRenge(
      int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY
            .findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateByAgeRenge(startAge, endAge);
    definition.setQuery(query);

    return definition;
  }

  public CohortDefinitionDimension
      getDimensionForPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDate() {

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
            this.findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateByAdult(14), mappings));

    dimension.addCohortDefinition(
        "15-",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDateChildren(15), mappings));

    return dimension;
  }

  // Dimension Age for Viral Load Result

  @DocumentedDefinition(value = "findPAtientWithCVOver1000CopiesAdult")
  public CohortDefinition findPAtientWithCVOver1000CopiesAdult(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPAtientWithCVOver1000CopiesBiggerThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPAtientWithCVOver1000CopiesChildren")
  public CohortDefinition findPAtientWithCVOver1000CopiesChildren(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = GenericMQQueryIntarface.QUERY.findPAtientWithCVOver1000CopiesLessThanParam(age);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "findPAtientWithCVOver1000CopiesByAgeRenge")
  public CohortDefinition findPAtientWithCVOver1000CopiesByAgeRenge(int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        GenericMQQueryIntarface.QUERY.findPAtientWithCVOver1000CopiesAgeRenge(startAge, endAge);
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "calculateDefaulteAgeByAgeRenge")
  public CohortDefinition calculateDefaulteAgeByAgeRenge(int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsAgeRange;

    String finalQuery = String.format(query, startAge, endAge);

    definition.setQuery(finalQuery);

    return definition;
  }

  @DocumentedDefinition(value = "calculateDefaulteAgeBiggerThan")
  public CohortDefinition calculateDefaulteAgeBiggerThan(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsBiggerThan;

    String finalQuery = String.format(query, age);

    definition.setQuery(finalQuery);

    return definition;
  }

  @DocumentedDefinition(value = "calculateDefaulteAgeLessThan")
  public CohortDefinition calculateDefaulteAgeLessThan(int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = MQQueriesInterface.QUERY.findPatientsLessThan;

    String finalQuery = String.format(query, age);

    definition.setQuery(finalQuery);

    return definition;
  }

  public CohortDefinitionDimension getDimensionAgeEndInclusionDate() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "<15", EptsReportUtils.map(this.calculateDefaulteAgeLessThan(15), mappings));

    dimension.addCohortDefinition(
        "15+", EptsReportUtils.map(this.calculateDefaulteAgeBiggerThan(14), mappings));

    dimension.addCohortDefinition(
        "0-14", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(0, 14), mappings));

    dimension.addCohortDefinition(
        "2-14", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(2, 14), mappings));

    dimension.addCohortDefinition(
        "5-9", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(5, 9), mappings));

    dimension.addCohortDefinition(
        "3-14", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(3, 14), mappings));

    dimension.addCohortDefinition(
        "0-4", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(0, 4), mappings));

    dimension.addCohortDefinition(
        "10-14", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(10, 14), mappings));

    dimension.addCohortDefinition(
        "2-9", EptsReportUtils.map(this.calculateDefaulteAgeByAgeRenge(2, 9), mappings));

    return dimension;
  }

  public CohortDefinitionDimension getDimensionForPatientsPatientWithCVOver1000Copies() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "15+", EptsReportUtils.map(this.findPAtientWithCVOver1000CopiesAdult(15), mappings));

    dimension.addCohortDefinition(
        "15-", EptsReportUtils.map(this.findPAtientWithCVOver1000CopiesChildren(15), mappings));

    return dimension;
  }
}
