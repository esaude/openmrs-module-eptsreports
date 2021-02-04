package org.openmrs.module.eptsreports.reporting.library.cohorts.mq;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory12P2CohortQueries {

  @Autowired private MQCohortQueries mqCohortQueries;
  @Autowired private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @DocumentedDefinition(
      value =
          "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE(")
  public CohortDefinition
      findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A(),
            mappings));

    definition.addSearch(
        "B1-FIRSTLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString(
        "(START-ART AND B1-FIRSTLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE(")
  public CohortDefinition
      findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDNumerator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "FIRST-LINE",
        EptsReportUtils.map(
            this
                .findPatientsInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line64ColumnDDenominator(),
            mappings));

    definition.addSearch(
        "B1E-NOTFIRSTLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("(FIRST-LINE AND B13-RESUMO-MENSAL) NOT (B1E-NOTFIRSTLINE)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(")
  public CohortDefinition
      findPatietsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator");
    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A(),
            mappings));

    definition.addSearch(
        "B2-SECONDLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B2(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.setCompositionString(
        "(START-ART AND B2-SECONDLINE) NOT (PREGNANT OR BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findAdultInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator(")
  public CohortDefinition
      findPatientsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findAdultInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionCategory12Line14ColumnE");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "SECOND-LINE",
        EptsReportUtils.map(
            this
                .findPatietsInTheSecondLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line65ColumnEDenominator(),
            mappings));

    definition.addSearch(
        "B2E-NOTSECONDLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNotInTheSecondLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateB2E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString("(SECOND-LINE AND B13-RESUMO-MENSAL) NOT (B2E-NOTSECONDLINE)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(")
  public CohortDefinition
      findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNewlyEnrolledOnARTByAge14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12A(),
            mappings));

    definition.addSearch(
        "PREGNANT",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoArePregnantInclusionDateRF08(), mappings));

    definition.addSearch(
        "B1-FIRSTLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1(),
            mappings));

    definition.addSearch(
        "BREASTFEEDING",
        EptsReportUtils.map(
            this.mqCohortQueries.findPatientsWhoAreBreastfeedingInclusionDateRF09(), mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardRF06(),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(this.mqCohortQueries.findPatientsWhoTransferedOutRF07(), mappings));

    definition.setCompositionString(
        "(START-ART AND  PREGNANT AND B1-FIRSTLINE) NOT (BREASTFEEDING OR TRANSFERED-IN)");

    return definition;
  }

  @DocumentedDefinition(
      value =
          "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator(")
  public CohortDefinition
      findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName(
        "findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDNumerator");

    definition.addParameter(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    definition.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    definition.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    definition.addParameter(new Parameter("location", "location", Date.class));

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    definition.addSearch(
        "PREGNANT-FIRST-LINE",
        EptsReportUtils.map(
            this
                .findPregnantInTheFirstLineWhoStartedARTDuring14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12Line73ColumnDDenominator(),
            mappings));

    definition.addSearch(
        "B1E-NOTFIRSTLINE",
        EptsReportUtils.map(
            this.mqCohortQueries
                .findPatientsWhoAreNotInTheFirstLine14MonthsBeforeRevisionDateAnd11MonthsBeforeRevisionDateCategory12B1E(),
            mappings));

    definition.addSearch(
        "B13-RESUMO-MENSAL",
        EptsReportUtils.map(
            resumoMensalCohortQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(),
            "endDate=${endRevisionDate},location=${location}"));

    definition.setCompositionString(
        "(PREGNANT-FIRST-LINE AND B13-RESUMO-MENSAL) NOT (B1E-NOTFIRSTLINE)");

    return definition;
  }
}
