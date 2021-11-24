package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.mq.MQQueriesInterface;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MIAgeDimentions {

  @Autowired private MQAgeDimensions mQAgeDimensions;

  public CohortDefinitionDimension getDimensionForPatientsWhoAreNewlyEnrolledOnART() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patients new Enrolled On ART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappingsMILessTwoMonths =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappingsMILessSevenMonths =
        "startInclusionDate=${endRevisionDate-7m+1d},endInclusionDate=${endRevisionDate-6m},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappingsMILess8Months =
        "startInclusionDate=${endRevisionDate-8m+1d},endInclusionDate=${endRevisionDate-7m},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappingsMILessFiveMonths =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    final String mappingsMILessTreeMonths =
        "startInclusionDate=${endRevisionDate-3m+1d},endInclusionDate=${endRevisionDate-2m},endRevisionDate=${endRevisionDate},location=${location}";

    /*   Dimension Age for new enrrolment on ART less than 2 months
     */

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(15),
            mappingsMILessTwoMonths));
    
    dimension.addCohortDefinition(
            "LESS_2_MONTHS_1-14",
            EptsReportUtils.map(
                mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(1, 14),
                mappingsMILessTwoMonths));


    dimension.addCohortDefinition(
        "LESS_2_MONTHS_2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_3-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_LESS_9MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9),
            mappingsMILessTwoMonths));

    dimension.addCohortDefinition(
        "LESS_2_MONTHS_0-18M",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18),
            mappingsMILessTwoMonths));

    /*   Dimension Age for new enrrolment on ART less than 7 months
     */

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(15),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_3-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_LESS_9MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9),
            mappingsMILessSevenMonths));

    dimension.addCohortDefinition(
        "LESS_7_MONTHS_0-18M",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18),
            mappingsMILessSevenMonths));

    /*   Dimension Age for new enrrolment on ART less than 5 months
     */

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(15),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_3-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_LESS_9MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_2-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(2),
            mappingsMILessFiveMonths));

    dimension.addCohortDefinition(
        "LESS_5_MONTHS_0-18M",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18),
            mappingsMILessFiveMonths));

    /*   Dimension Age for new enrrolment on ART less than 3 months
     */

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(15),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_3-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_LESS_9MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_2-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(2),
            mappingsMILessTreeMonths));

    dimension.addCohortDefinition(
        "LESS_3_MONTHS_0-18M",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18),
            mappingsMILessTreeMonths));

    /*   Dimension Age for new enrrolment on ART less than 8 months
     */

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAdult(15),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTChildren(15),
            mappingsMILess8Months));
    
    dimension.addCohortDefinition(
            "LESS_8_MONTHS_1-14",
            EptsReportUtils.map(
                mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(1, 14),
                mappingsMILess8Months));


    dimension.addCohortDefinition(
        "LESS_8_MONTHS_2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(2, 14),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(0, 4),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(5, 9),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_3-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRenge(3, 14),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_LESS_9MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 9),
            mappingsMILess8Months));

    dimension.addCohortDefinition(
        "LESS_8_MONTHS_0-18M",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWhoAreNewlyEnrolledOnARTByAgeRengeUsingMonth(0, 18),
            mappingsMILess8Months));

    return dimension;
  }

  public CohortDefinitionDimension getDimensionForPatientsPatientWithCVOver1000Copies() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappingsMILessFourMonths =
        "startInclusionDate=${endRevisionDate-4m+1d},endInclusionDate=${endRevisionDate-3m},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "CV_LESS_4_MONTHS_15+",
        EptsReportUtils.map(
            mQAgeDimensions.findPAtientWithCVOver1000CopiesAdult(15), mappingsMILessFourMonths));

    dimension.addCohortDefinition(
        "CV_LESS_4_MONTHS_15-",
        EptsReportUtils.map(
            mQAgeDimensions.findPAtientWithCVOver1000CopiesChildren(15), mappingsMILessFourMonths));

    return dimension;
  }

  /*		Calculate age on the last consultation
   */ public CohortDefinitionDimension getDimensionForLastClinicalConsultation() {

    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappingsMI =
        "startInclusionDate=${endRevisionDate-2m+1d},endInclusionDate=${endRevisionDate-1m},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "15+",
        EptsReportUtils.map(
            mQAgeDimensions
                .findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation15Plus(15),
            mappingsMI));

    dimension.addCohortDefinition(
        "0-4",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
                0, 4),
            mappingsMI));

    dimension.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
                5, 9),
            mappingsMI));

    dimension.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
                10, 14),
            mappingsMI));

    dimension.addCohortDefinition(
        "2-14",
        EptsReportUtils.map(
            mQAgeDimensions.findPatientsWithLastClinicalConsultationDenominatorB1AgeCalculation(
                2, 14),
            mappingsMI));

    return dimension;
  }

  /*   		Calculate age on the last consultation
   */
  public CohortDefinitionDimension getDimensionAgeEndInclusionDate() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    /*   		Calculate age on 10 months
     */
    final String mappings =
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "<15-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeLessThan(15), mappings));

    dimension.addCohortDefinition(
        "15+10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeBiggerThan(15), mappings));

    dimension.addCohortDefinition(
        "0-14-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(0, 14), mappings));

    dimension.addCohortDefinition(
        "2-14-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(2, 14), mappings));

    dimension.addCohortDefinition(
        "5-9-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(5, 9), mappings));

    dimension.addCohortDefinition(
        "3-14-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(3, 14), mappings));

    dimension.addCohortDefinition(
        "0-4-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(0, 4), mappings));

    dimension.addCohortDefinition(
        "10-14-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(10, 14), mappings));

    dimension.addCohortDefinition(
        "2-9-10MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(2, 9), mappings));

    /*   		Calculate age on 5 months
     */

    final String mappings5Months =
        "startInclusionDate=${endRevisionDate-5m+1d},endInclusionDate=${endRevisionDate-4m},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "<15-5MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeLessThan(15), mappings5Months));

    dimension.addCohortDefinition(
        "15+5MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeBiggerThan(15), mappings5Months));

    dimension.addCohortDefinition(
        "0-14-5MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.calculateDefaulteAgeByAgeRenge(0, 14), mappings5Months));

    dimension.addCohortDefinition(
        "2-14-5MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.calculateDefaulteAgeByAgeRenge(2, 14), mappings5Months));

    dimension.addCohortDefinition(
        "5-9-5MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(5, 9), mappings5Months));

    dimension.addCohortDefinition(
        "3-14-5MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.calculateDefaulteAgeByAgeRenge(3, 14), mappings5Months));

    dimension.addCohortDefinition(
        "0-4-5MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(0, 4), mappings5Months));

    dimension.addCohortDefinition(
        "10-14-5MONTHS",
        EptsReportUtils.map(
            mQAgeDimensions.calculateDefaulteAgeByAgeRenge(10, 14), mappings5Months));

    dimension.addCohortDefinition(
        "2-9-5MONTHS",
        EptsReportUtils.map(mQAgeDimensions.calculateDefaulteAgeByAgeRenge(2, 9), mappings5Months));

    return dimension;
  }

  @DocumentedDefinition(
      value =
          "findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodP3B2NEWCalculeteAgeBiggerThan")
  public CohortDefinition
      findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodP3B2NEWCalculeteAgeBiggerThan(
          int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorBiggerThan;

    String finalQuery = String.format(query, age);

    definition.setQuery(finalQuery);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorLessThan")
  public CohortDefinition
      findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorLessThan(
          int age) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorLessThan;

    String finalQuery = String.format(query, age);

    definition.setQuery(finalQuery);

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorAgeRange")
  public CohortDefinition
      findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorAgeRange(
          int startAge, int endAge) {

    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query =
        MQQueriesInterface.QUERY
            .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorAgeRange;

    String finalQuery = String.format(query, startAge, endAge);

    definition.setQuery(finalQuery);

    return definition;
  }

  public CohortDefinitionDimension
      findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodP3B2NEWCalculeteAgeBiggerThan() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();

    dimension.setName("patientsPregnantEnrolledOnART");
    dimension.addParameter(new Parameter("startInclusionDate", "Start Date", Date.class));
    dimension.addParameter(new Parameter("endInclusionDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("endRevisionDate", "End Revision Date", Date.class));
    dimension.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings =
        "startInclusionDate=${endRevisionDate-10m+1d},endInclusionDate=${endRevisionDate-9m},endRevisionDate=${endRevisionDate},location=${location}";

    dimension.addCohortDefinition(
        "15+10MONTHS",
        EptsReportUtils.map(
            this
                .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodP3B2NEWCalculeteAgeBiggerThan(
                    15),
            mappings));

    dimension.addCohortDefinition(
        "<15-10MONTHS",
        EptsReportUtils.map(
            this
                .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorLessThan(
                    15),
            mappings));

    dimension.addCohortDefinition(
        "2-14-10MONTHS",
        EptsReportUtils.map(
            this
                .findAllPatientsWhoHaveTherapheuticLineSecondLineDuringInclusionPeriodCategory13P3B2NEWDenominatorAgeRange(
                    2, 14),
            mappings));

    return dimension;
  }
}
