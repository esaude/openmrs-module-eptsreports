package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
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
        "startInclusionDate=${endRevisionDate-7m+1d},endInclusionDate=${endRevisionDate-6m},endRevisionDate=${endRevisionDate-7m+1d},location=${location}";

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
}
