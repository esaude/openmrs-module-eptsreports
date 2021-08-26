package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.queries.KeyPopQueriesInterface;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeyPopCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;
  @Autowired private GenericCohortQueries genericCohortQueries;

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtKeyPop() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                KeyPopQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnArtKeyPop),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtIncludingTransferedIn() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX CURR KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-START-DATE",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                ResumoMensalQueries.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD-START-DATE",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                ResumoMensalQueries
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARnOutAPeriodOnMasterCardStartDateB2",
                ResumoMensalQueries.findPatientsWhoWhereMarkedAsTransferedOutAPeriodB2),
            mappings));

    definition.setCompositionString(
        "(START-ART OR TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD) NOT((TRANSFERED-IN-START-DATE OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD-START-DATE ) NOT (TRANSFERED-OUT))");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtKeyPopPreviousPeriod() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-12m},endDate=${endDate-12m},location=${location}";

    final String mappingsEndDate = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "", KeyPopQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnArtKeyPop),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARnOutAPeriodOnMasterCardStartDateB2",
                KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedAsTransferedOutAEndDateRF7),
            mappingsEndDate));

    definition.setCompositionString(
        "(START-ART OR TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD) NOT (TRANSFERED-OUT)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtPreviousPeriod() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX CURR KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-12m},endDate=${endDate-12m},location=${location}";

    final String mappingsEndDate = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "TRANSFERED-OUT",
                KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedAsTransferedOutAEndDateRF7),
            mappingsEndDate));

    definition.addSearch(
        "SUSPEND",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "SUSPEND", KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedSuspendEndDate),
            mappingsEndDate));

    definition.addSearch(
        "ABANDONED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "ABANDONED", ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7()),
            mappingsEndDate));

    definition.addSearch(
        "DIED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "DIED", KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedDiedEndDate),
            mappingsEndDate));

    definition.setCompositionString(
        "(START-ART OR TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD) NOT (TRANSFERED-OUT OR SUSPEND OR ABANDONED OR DIED)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtKeyPop6MonthsCoorte() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate-7m},endDate=${endDate-4m},location=${location}";
    final String mappingsEndDate = "startDate=${startDate},endDate=${endDate},location=${location}";


    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART",
                KeyPopQueriesInterface.QUERY.findPatientsWhoAreNewlyEnrolledOnArtKeyPop),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappingsEndDate));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappingsEndDate));

    definition.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return definition;
  }
}
