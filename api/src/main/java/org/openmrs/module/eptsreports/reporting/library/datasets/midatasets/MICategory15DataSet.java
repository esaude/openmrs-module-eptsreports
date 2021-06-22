package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory15CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory15DataSet extends MQAbstractDataSet {

  @Autowired private MICategory15CohortQueries miCategory15CohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT15NUMERADOR_15_1",
        "15.1 % de pacientes elegíveis a MDS, que foram inscritos em MDS - NUMERADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodAndRegisteredAtLeastInOneMDS_Numerator_15_1(),
                "CAT15NUMERADOR_15_1",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT15DENOMINADOR_15_1",
        "15.1 % de pacientes elegíveis a MDS, que foram inscritos em MDS - DENOMINADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsElegibleForMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriod_Denominator_15_1(),
                "CAT15DENOMINADOR_15_1",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT15NUMERADOR_15_2",
        "15.2 % de inscritos em MDS que receberam CV acima de 1000 cópias que foram suspensos de MDS - NUMERADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000AndSuspendedInTheMDS_Numerator_15_2(),
                "CAT15NUMERADOR_15_2",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT15DENOMINADOR_15_2",
        "15.2 % de inscritos em MDS que receberam CV acima de 1000 cópias que foram suspensos de MDS - DENOMINADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodWhoReceivedCargaViralGreaterThan1000_Denominator_15_2(),
                "CAT15DENOMINADOR_15_2",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT15NUMERADOR_15_3",
        "15.3 % de pacientes inscritos em MDS em TARV há mais de 21 meses, que conhecem o seu resultado de CV de seguimento - NUMERADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsRegisteredInOneMDSForStablePatientsAndHadCVBetweenTwelveAndEigtheenMonthsAfterCVLessThan1000_Numerator_15_3(),
                "CAT15NUMERADOR_15_3",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT15DENOMINADOR_15_3",
        "15.3 % de pacientes inscritos em MDS em TARV há mais de 21 meses, que conhecem o seu resultado de CV de seguimento - DENOMINADOR",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.miCategory15CohortQueries
                    .findPatientsRegisteredInMDSForStablePatientsWithClinicalConsultationInTheRevisionPeriodInARTMoreThan21Months_Denominator_15_3(),
                "CAT15DENOMINADOR_15_3",
                mappings),
            mappings),
        "");
  }
}
