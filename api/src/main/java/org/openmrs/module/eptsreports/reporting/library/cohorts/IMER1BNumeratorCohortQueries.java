package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.StartedArtMinusARTCareEnrollmentDateCalculationIMER1B;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IMER1BNumeratorCohortQueries {

  private HivMetadata hivMetadata;

  private TxNewCohortQueries txNewCohortQueries;

  private GenericCohortQueries genericCohortQueries;

  private IMER1DenominatorCohortQueries imer1DenominatorCohortQueries;

  @Autowired
  public IMER1BNumeratorCohortQueries(
      HivMetadata hivMetadata,
      TxNewCohortQueries txNewCohortQueries,
      GenericCohortQueries genericCohortQueries,
      IMER1DenominatorCohortQueries imer1DenominatorCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.txNewCohortQueries = txNewCohortQueries;
    this.genericCohortQueries = genericCohortQueries;
    this.imer1DenominatorCohortQueries = imer1DenominatorCohortQueries;
  }

  /**
   * <b>E: Filter all patients who initiated ART within 15 days from the ART Care Enrollment as
   * following:</b>
   *
   * <ul>
   *   <li>Patient ART Start Date minus Patient ART Care Enrollment date <= 15 days
   *   <li>Patient ART Start Date is the oldest date from the set of criterias defined in the common
   *       query: 1/1 Patients who initiated ART and ART Start Date as earliest from the following
   *       criterias is by End of the period (reporting endDate)
   *   <li>Patient ART Care Enrollment date is the oldest date from the set of criterias defined in
   *       the IMER1 denominador A
   *   <li>Age should be calculated on Patient ART Care enrollment date (Check Section A for the
   *       algorithm to define this date). [birth date minus ART Care enrollment date]
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getE(boolean considerTransferredIn) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(
                    StartedArtMinusARTCareEnrollmentDateCalculationIMER1B.class)
                .get(0));
    cd.setName("Initiated ART within 15 days from the ART Care Enrollment");
    cd.addCalculationParameter("considerTransferredIn", considerTransferredIn);
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));

    return cd;
  }

  public CohortDefinition getAllPatients() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("IMER1B - All Patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getEarliestPreART(),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getE(true), "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND E AND NOT D");

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>Pregnant Women: A and B and E and NOT (C or D)
   * </ul>
   *
   * @return
   */
  public CohortDefinition getPregnantWomen() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("IMER1B - Pregnant Women");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getEarliestPreART(),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getE(true), "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND B AND E AND NOT (C OR D)");

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>Breastfeeding Women: A and C and E and NOT (B or D)
   * </ul>
   *
   * @return
   */
  public CohortDefinition getBreastFeedingWomen() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("IMER1B - BreastFeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getEarliestPreART(),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getE(true), "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND C AND E AND NOT (B OR D)");

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>Children (0-14 years): A and NOT B and NOT C NOT D and E and Age < 15 years*
   * </ul>
   *
   * @return
   */
  public CohortDefinition getChildren() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("IMER1B - Children");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getEarliestPreART(),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getE(true), "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "CHILDREN",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnPreArtDate(0, 14),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.setCompositionString("A AND NOT B AND NOT C NOT D AND E AND CHILDREN");

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>Adults (15+ years): A and NOT B and NOT C NOT D and E and Age>= 15 years*
   * </ul>
   *
   * @return
   */
  public CohortDefinition getAdults() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator - IMER1 - BreastFeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Date.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getEarliestPreART(),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-2m+1d},endDate=${endDate-1m},location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.addSearch(
        "D",
        EptsReportUtils.map(
            imer1DenominatorCohortQueries.getTransferredInPatients(),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "E",
        EptsReportUtils.map(
            getE(true), "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "ADULTS",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnPreArtDate(15, 200),
            "onOrAfter=${endDate-2m+1d},onOrBefore=${endDate-1m},location=${location}"));

    cd.setCompositionString("A and NOT B and NOT C NOT D and E AND ADULTS");

    return cd;
  }
}
