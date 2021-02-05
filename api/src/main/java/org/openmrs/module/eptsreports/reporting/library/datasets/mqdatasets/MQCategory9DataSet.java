package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory9CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQGenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory9DataSet {

  @Autowired private MQCategory9CohortQueries mQCategory9CohortQueries;
  @Autowired private MQGenericCohortQueries mQGenericCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT09_91AdultNUMERATOR",
        "9.1. % de adultos HIV+ em TARV que tiveram conhecimento do resultado do primeiro\n"
            + "CD4 dentro de 33 dias após a inscrição - Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory9CohortQueries
                    .findPatientsInARTWhoReceivedCD4ResultIn33DaysAfterFirstClinicalConsultaionCategory9_91_Numerator(),
                "CAT09_91AdultNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT09_91AdultDENOMINATOR",
        "9.1. % de adultos HIV+ em TARV que tiveram conhecimento do resultado do primeiro\n"
            + "CD4 dentro de 33 dias após a inscrição - Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQGenericCohortQueries
                    .findPatientOnARTdExcludingPregantAndBreastfeedingAndTransferredInTransferredOut(),
                "CAT09_91AdultDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT09_92ChildrenNUMERATOR",
        "9.2. % de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro\n"
            + "CD4 dentro de 33 dias após a inscrição - Numerador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory9CohortQueries
                    .findPatientsInARTWhoReceivedCD4ResultIn33DaysAfterFirstClinicalConsultaionCategory9_91_Numerator(),
                "CAT09_92ChildrenNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");

    dataSetDefinition.addColumn(
        "CAT09_92ChildrenDENOMINATOR",
        "9.2. % de crianças HIV+ em TARV que tiveram conhecimento do resultado do primeiro\n"
            + "CD4 dentro de 33 dias após a inscrição - Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQGenericCohortQueries
                    .findPatientOnARTdExcludingPregantAndBreastfeedingAndTransferredInTransferredOut(),
                "CAT09_92ChildrenDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15-");
  }

  public CohortIndicator setIndicatorWithAllParameters(
      final CohortDefinition cohortDefinition, final String indicatorName, final String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    indicator.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    indicator.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }
}
