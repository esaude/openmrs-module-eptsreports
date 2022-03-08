package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreDeadCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreIITBetween3And5MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreIITGreaterOrEquel6MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreIITLessThan3MonthsCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoAreTransferedOutCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoMissedNextApointmentCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.txml.TxMLPatientsWhoRefusedOrStoppedTreatmentCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** All queries needed for TxMl report needed for EPTS project */
@Component
public class TxMlCohortQueries {

  public CohortDefinition getPatientsWhoAreIITLessThan3Months() {
    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are LTFU less than 3 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentIITLess3Month",
        EptsReportUtils.map(this.getPatientsWhoAreIITLessThan3MonthsCalculation(), mapping));
    cd.addSearch("dead", EptsReportUtils.map(this.getPatientsMarkedAsDead(), mapping));
    cd.addSearch(
        "transferedOut", EptsReportUtils.map(this.getPatientsWhoAreTransferedOut(), mapping));
    cd.addSearch(
        "numerator", EptsReportUtils.map(this.getPatientsWhoMissedNextApointment(), mapping));

    cd.setCompositionString(
        "(numerator AND missedAppointmentIITLess3Month) NOT (dead OR transferedOut)");
    return cd;
  }

  public CohortDefinition getPatientsWhoAreIITGreaterOrEqual6Months() {
    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are LTFU less than 6 months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentIITGreaterOrEqual6Month",
        EptsReportUtils.map(this.getPatientsWhoAreIITGreatherOrEqual6MonthsCalculation(), mapping));
    cd.addSearch("dead", EptsReportUtils.map(this.getPatientsMarkedAsDead(), mapping));
    cd.addSearch(
        "transferedOut", EptsReportUtils.map(this.getPatientsWhoAreTransferedOut(), mapping));
    cd.addSearch(
        "numerator", EptsReportUtils.map(this.getPatientsWhoMissedNextApointment(), mapping));

    cd.setCompositionString(
        "(numerator AND missedAppointmentIITGreaterOrEqual6Month) NOT (dead OR transferedOut)");
    return cd;
  }

  public CohortDefinition getPatientsWhoAreIITBetween3And5Months() {
    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get patients who are LTFU Greater than 3 months And Less Than 6 Months");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "missedAppointmentIITBetween3And5Months",
        EptsReportUtils.map(
            this.getPatientsWhoAreIITBetween3And5MonthsCalculationCalculation(), mapping));
    cd.addSearch("dead", EptsReportUtils.map(this.getPatientsMarkedAsDead(), mapping));
    cd.addSearch(
        "transferedOut", EptsReportUtils.map(this.getPatientsWhoAreTransferedOut(), mapping));
    cd.addSearch(
        "numerator", EptsReportUtils.map(this.getPatientsWhoMissedNextApointment(), mapping));

    cd.setCompositionString(
        "(numerator AND missedAppointmentIITBetween3And5Months) NOT (dead OR transferedOut)");
    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoMissedNextApointment")
  public CohortDefinition getPatientsWhoMissedNextApointment() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "txMLPatientsWhoMissedNextApointmentCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoMissedNextApointmentCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsMarkedAsDead")
  public CohortDefinition getPatientsMarkedAsDead() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsMarkedAsDeadCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreDeadCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoAreTransferedOut")
  public CohortDefinition getPatientsWhoAreTransferedOut() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoAreTransferedOutCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreTransferedOutCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "patientsWhoRefusedStoppedTreatmentCalculation")
  public CohortDefinition getPatientsWhoRefusedOrStoppedTreatment() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "patientsWhoRefusedStoppedTreatmentCalculation",
            Context.getRegisteredComponents(
                    TxMLPatientsWhoRefusedOrStoppedTreatmentCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition compositionCohort = new CompositionCohortDefinition();
    compositionCohort.setName("Get patients who are Refused/Stopped Treatment");
    compositionCohort.addParameter(new Parameter("startDate", "Start Date", Date.class));
    compositionCohort.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohort.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohort.addSearch("refusedTreatment", EptsReportUtils.map(cd, mapping));
    compositionCohort.addSearch(
        "numerator", EptsReportUtils.map(this.getPatientsWhoMissedNextApointment(), mapping));

    compositionCohort.addSearch(
        "iit1", EptsReportUtils.map(this.getPatientsWhoAreIITLessThan3Months(), mapping));
    compositionCohort.addSearch(
        "iit2", EptsReportUtils.map(this.getPatientsWhoAreIITBetween3And5Months(), mapping));
    compositionCohort.addSearch(
        "iit3", EptsReportUtils.map(this.getPatientsWhoAreIITGreaterOrEqual6Months(), mapping));

    compositionCohort.setCompositionString(
        "(numerator AND refusedTreatment) NOT (iit1 OR iit2 OR iit3)");

    return compositionCohort;
  }

  @DocumentedDefinition(value = "PatientsWhoAreIITLessThan3MonthsCalculation")
  private CohortDefinition getPatientsWhoAreIITLessThan3MonthsCalculation() {
    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "PatientsWhoAreIITLessThan3MonthsCalculation",
            Context.getRegisteredComponents(TxMLPatientsWhoAreIITLessThan3MonthsCalculation.class)
                .get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "PatientsWhoAreIITGreatherOrEqual6MonthsCalculation")
  private CohortDefinition getPatientsWhoAreIITGreatherOrEqual6MonthsCalculation() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "PatientsWhoAreIITGreatherOrEqual6MonthsCalculation",
            Context.getRegisteredComponents(
                    TxMLPatientsWhoAreIITGreaterOrEquel6MonthsCalculation.class)
                .get(0));
    cd.setName("PatientsWhoAreIITGreatherOrEqual6MonthsCalculation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "PatientsWhoAreIITBetween3And5MonthsCalculation")
  private CohortDefinition getPatientsWhoAreIITBetween3And5MonthsCalculationCalculation() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "PatientsWhoAreIITBetween3And5MonthsCalculation",
            Context.getRegisteredComponents(
                    TxMLPatientsWhoAreIITBetween3And5MonthsCalculation.class)
                .get(0));
    cd.setName("PatientsWhoAreIITBetween3And5MonthsCalculation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }
}
