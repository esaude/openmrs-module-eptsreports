package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.rtt.ReturnedDateIITDateDaysCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRttCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * From the patients who returned to treatment and remained on ARV, the algorithm will select the
 *
 * <p>Most recent date from the set of the criteria to define the patient as IIT and the oldest date
 * from the set of criteria to define that the patient returned to treatment, and calculate the
 * difference in days between patient Return to treatment date during reporting period and the date
 * patient experienced IIT most recently during previous reporting period or before. The number of
 * days will identify the patient disaggregation < 12 months and >= 12 months
 */
@Component
public class TxRTTDimenstion {

  private TxRttCohortQueries txRttCohortQueries;

  private final String MAPPINGS =
      "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";

  @Autowired
  public TxRTTDimenstion(TxRttCohortQueries txRttCohortQueries) {
    this.txRttCohortQueries = txRttCohortQueries;
  }

  public CohortDefinitionDimension getAnBFromRTT() {

    CohortDefinitionDimension cdd = new CohortDefinitionDimension();
    cdd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cdd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cdd.addParameter(new Parameter("location", "Location", Location.class));

    cdd.setName("B & A days difference");

    cdd.addCohortDefinition(
        "<365",
        EptsReportUtils.map(
            txRttCohortQueries.treatmentInterruptionOfXDays(null, 365),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cdd.addCohortDefinition(
        "365+",
        EptsReportUtils.map(
            txRttCohortQueries.treatmentInterruptionOfXDays(365, null),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cdd.addCohortDefinition(
        "unk", EptsReportUtils.map(getPatientsReturnedAndIITDays(PLHIVDays.UNKNOWN), MAPPINGS));

    return cdd;
  }

  public CohortDefinition getPatientsReturnedAndIITDays(PLHIVDays period) {
    CalculationCohortDefinition calculationCohortDefinition =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(ReturnedDateIITDateDaysCalculation.class).get(0));
    if (period == PLHIVDays.UNKNOWN) {
      calculationCohortDefinition.setName("unknown");
    } else if (period == PLHIVDays.LESS_THAN_365) {
      calculationCohortDefinition.setName("lessThan365Days");
    } else if (period == PLHIVDays.MORE_THAN_365) {
      calculationCohortDefinition.setName("moreThan365Days");
    }

    calculationCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    calculationCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    calculationCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    calculationCohortDefinition.addCalculationParameter("period", period);

    return calculationCohortDefinition;
  }
}
