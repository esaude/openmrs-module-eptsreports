package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.mi.MICategory7CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets.MQAbstractDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MICategory7Dataset extends MQAbstractDataSet {

  @Autowired private MICategory7CohortQueries mICategory7CohortQueries;

  public void constructTMiDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT7AdultDENOMINATOR_7_1",
        "7.1: Adultos (15/+anos) HIV+ em TARV elegiveis ao TPT e que iniciaram TPT Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator(),
                "CAT7AdultDENOMINATOR_7_1",
                mappings),
            mappings),
        "ageMiNewART=LESS_2_MONTHS_15+");

    dataSetDefinition.addColumn(
        "CAT7AdultoNUMERATOR_7_1",
        "7.1: Adultos (15/+anos) HIV+ em TARV elegiveis ao TPT e que iniciaram TPT Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator(),
                "CAT7AdultoNUMERATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_2_MONTHS_15+");

    dataSetDefinition.addColumn(
        "CAT7ChildrenDENOMINATOR",
        "7.3: Crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientWhoAreNewlyEnrolledOnARTDuringRevisionPeriodAndStartTPIAndElegibleTPTCategory7RF19Denominator(),
                "CAT7ChildrenDENOMINATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_2_MONTHS_15-");

    dataSetDefinition.addColumn(
        "CAT7ChildrenNUMERATOR",
        "7.3: Crianças HIV+ em TARV elegiveis ao TPT  e que iniciaram TPT Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsNewlyEnrolledOnARTInInclusionPeriodAndHaveINHDuringPeriodCategory7RF20Numerator(),
                "CAT7ChildrenNUMERATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_2_MONTHS_15-");

    dataSetDefinition.addColumn(
        "CAT7PregnantDENOMINATOR",
        "7.5: Mulheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndStartTPIAndElegibleTPTCategory7RF23Denominator(),
                "CAT7PregnantDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PregnantNUMERATOR",
        "7.5: Mlheres grávidas HIV+ elegíveis ao TPI e que iniciaram TPI Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPregnantNewlyEnrolledOnARTInInclusionPeriodAndStartTPIAndElegibleTPTDuringInclusionPeriodCategory7RF24Numerator(),
                "CAT7PregnantNUMERATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7AdultoTPIDENOMINATOR",
        "7.2: Adultos (15/+anos) HIV+ em TARV elegiveis ao TPT que iniciaram e  completaram TPT Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator(),
                "CAT7AdultoTPIDENOMINATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_7_MONTHS_15+");

    dataSetDefinition.addColumn(
        "CAT7AdultoTPINUMERATOR",
        "7.2: Adultos (15/+anos) HIV+ em TARV elegiveis ao TPT que iniciaram e completaram TPT Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(),
                "CAT7AdultoTPINUMERATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_7_MONTHS_15+");

    dataSetDefinition.addColumn(
        "CAT7ChildrenTPIDENOMINATOR",
        "7.4: Crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF25Denominator(),
                "CAT7ChildrenTPIDENOMINATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_7_MONTHS_15-");

    dataSetDefinition.addColumn(
        "CAT7ChildrenTPINUMERATOR",
        "7.4: Crianças HIV+ em TARV elegíveis que iniciaram e completaram TPT Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPatientsWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF26Numerator(),
                "CAT7ChildrenTPINUMERATOR",
                mappings),
            mappings),
        "ageMiNewART=LESS_7_MONTHS_15-");

    dataSetDefinition.addColumn(
        "CAT7PragnantTPIDENOMINATOR",
        "7.6: Mulheres Gravidas HIV+ em TARV que iniciou TPI e que terminou TPI Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPragnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF29Denominator(),
                "CAT7PragnantTPIDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT7PragnantTPINUMERATOR",
        "7.6: Mulheres Gravidas HIV+ em TARV que iniciou TPI e que terminou TPI Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mICategory7CohortQueries
                    .findPregnantWhoAreNewlyEnrolledOnARTDuringInclusionPeriodAndEndTPICategory7RF30Numerator(),
                "CAT7PragnantTPINUMERATOR",
                mappings),
            mappings),
        "");
  }
}
