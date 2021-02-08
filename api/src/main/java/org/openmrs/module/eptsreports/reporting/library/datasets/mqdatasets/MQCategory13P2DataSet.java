package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory13P2CohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQCategory13P2DataSet extends MQAbstractDataSet {

  @Autowired private MQCategory13P2CohortQueries mQCohortQueriesCategory13P2;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVDENOMINATOR",
        "13.15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInTARVDENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
        "13.16: Mulheres Gravidas elegíveis a CV com registo de pedido de "
            + "CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVNUMINATOR",
        "13:15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInTARVNUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
        "13.16: Mulheres elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCohortQueriesCategory13P2
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
                mappings),
            mappings),
        "ageOnEndInclusionDate=15+");
  }
}
