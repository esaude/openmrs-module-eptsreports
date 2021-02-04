package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory9CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQGenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory9DataSet extends MQGenericDataSet {

  @Autowired private MQCategory9CohortQueries mQCategory9CohortQueries;
  @Autowired private MQGenericCohortQueries mQGenericCohortQueries;

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
}
