package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
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
  @Autowired private HivMetadata hivMetadata;

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
        "TX-CURR",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(this.findPatientsWhoAreNewlyEnrolledOnArtKeyPop(), mappings));

    definition.addSearch(
        "START-ART2",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "START-ART2",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnArtKeyPopCoorte12Months),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "TRANSFERED-OUT",
                KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedAsTransferedOutAEndDateRF7),
            mappings));

    definition.addSearch(
        "SUSPEND",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "SUSPEND", KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedSuspendEndDate),
            mappings));

    definition.addSearch(
        "ABANDONED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "ABANDONED", ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7()),
            mappings));

    definition.addSearch(
        "DIED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "DIED", KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedDiedEndDate),
            mappings));

    definition.setCompositionString(
        "(TX-CURR OR START-ART OR START-ART2) NOT(TRANSFERED-OUT OR SUSPEND OR ABANDONED OR DIED)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtKeyPopPreviousPeriodCoorte12Months() {

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
                "",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoAreNewlyEnrolledOnArtKeyPopCoorte12Months),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARnOutAPeriodOnMasterCardStartDateB2",
                KeyPopQueriesInterface.QUERY.findPatientsWhoWhereMarkedAsTransferedOutAEndDateRF7),
            mappingsEndDate));

    definition.setCompositionString("START-ART NOT (TRANSFERED-OUT)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtPreviousPeriodCoorte12Months() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX CURR KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappingsEndDate = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TX-CURR",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            mappingsEndDate));

    definition.addSearch(
        "TX-NEW-12MONTHS",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnArtKeyPopPreviousPeriodCoorte12Months(),
            mappingsEndDate));

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
        "(TX-NEW-12MONTHS AND TX-CURR) NOT (TRANSFERED-OUT OR SUSPEND OR ABANDONED OR DIED)");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreNewlyEnrolledOnArtKeyPop6MonthsCoorte() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-7m},endDate=${startDate-4m},location=${location}";
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

  public CohortDefinition findPatientsWhoActiveOnArtKeyPop6MonthsCoorte() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings =
        "startDate=${startDate-7m},endDate=${startDate-4m},location=${location}";
    final String mappingsToBeExclude =
        "startDate=${startDate-7m},endDate=${endDate},location=${location}";

    final String mappingsEndDate = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TX-CURR-6-MONTHS",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "TX-NEW-6-MONTHS",
        EptsReportUtils.map(
            this.findPatientsWhoAreNewlyEnrolledOnArtKeyPop6MonthsCoorte(), mappingsEndDate));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN",
                ResumoMensalQueries.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriodB2),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
                ResumoMensalQueries
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardB2),
            mappings));

    // Transfereidos do periodo anterior a 6 meses

    definition.addSearch(
        "TRANSFERED-IN-PREVIOS-6-MESES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-PREVIOS-6-MESES",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriodStartDate),
            mappingsToBeExclude));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD-PREVIOS-6-MESES",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "TRANSFERED-IN-AND-IN-ART-MASTER-CARD-PREVIOS-6-MESES",
                KeyPopQueriesInterface.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardStartDate),
            mappingsToBeExclude));

    /*    pacientes por remover
     */ definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARnOutAPeriodOnMasterCardStartDateB2",
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
        "((TX-CURR-6-MONTHS AND TX-NEW-6-MONTHS) OR ((TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD) NOT(TRANSFERED-IN-PREVIOS-6-MESES OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD-PREVIOS-6-MESES))) NOT (TRANSFERED-OUT OR SUSPEND OR ABANDONED OR DIED)");

    return definition;
  }

  public CohortDefinition findPatientsWhoActiveOnArtKeyPop6MonthsCoorteWithViralLoadResult() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingsToVL = "startDate=${startDate-7m},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TX-CURR-COORTE-6-MONTHS",
        EptsReportUtils.map(this.findPatientsWhoActiveOnArtKeyPop6MonthsCoorte(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsToVL));

    definition.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsToVL));

    definition.setCompositionString("(TX-CURR-COORTE-6-MONTHS AND VL) NOT Ex2");

    return definition;
  }

  public CohortDefinition
      findPatientsWhoActiveOnArtKeyPop6MonthsCoorteWithViralLoadResultLessThan1000() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TX NEW KEY POP");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    final String mappingsToVL = "startDate=${startDate-7m},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TX-CURR-COORTE-6-MONTHS",
        EptsReportUtils.map(this.findPatientsWhoActiveOnArtKeyPop6MonthsCoorte(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResulLessThan1000(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsToVL));

    definition.addSearch(
        "Ex3",
        map(
            genericCohortQueries.generalSql(
                "Ex3",
                ResumoMensalQueries.getE3ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappingsToVL));

    definition.setCompositionString("(TX-CURR-COORTE-6-MONTHS AND VL) NOT Ex3");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithRequestForVLE1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx Curr E1");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "B13",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappings));

    definition.addSearch(
        "E1x",
        map(
            genericCohortQueries.generalSql(
                "E1x",
                ResumoMensalQueries.getE1ExclusionCriteria(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                    hivMetadata.getHivViralLoadConcept().getConceptId())),
            mappings));

    definition.setCompositionString("(B13 AND VL) NOT E1x");

    return definition;
  }
}
