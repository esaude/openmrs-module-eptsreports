package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory12P2CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory13P2Dataset extends MQAbstractDataSet {

  @Autowired private MICategory12P2CohortQueries mICategory12P2CohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVDENOMINATOR",
        "13.15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
        "13.16: Mulheres Gravidas elegíveis a CV com registo de pedido de "
            + "CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Denumerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInTARVNUMINATOR",
        "13:15: Mulheres Gravidas elegíveis a CV com registo de pedido de CV feito pelo clínico (MG que iniciaram TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInTARVNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
        "13.16: Mulheres elegíveis a CV com registo de pedido de CV feito pelo clínico na primeira CPN (MG que entraram em TARV na CPN) Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVInFirstConsultationTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVInFirstConsultationTARVNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
        "13.17: Mulheres Gravidas que receberam o resultado da Carga Viral dentro de 33 dias após pedido Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory12P2CohortQueries
                    .findPatientsWhoArePregnantWithCVIn33DaysAfterInclusionDateTARVCategory13P2Numerator(),
                "CAT13P2PregnantWithCVIn33DaysAfterInclusionDateTARVNUMINATOR",
                mappings),
            mappings),
        "");
  }
}
