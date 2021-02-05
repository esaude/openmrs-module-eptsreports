package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory11CohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.mq.MQCategory11P2CohortQueries;
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
public class MQCategory11DataSet {

  @Autowired private MQCategory11CohortQueries mQCategory11CohortQueries;
  @Autowired private MQCategory11P2CohortQueries mQCategory11P2CohortQueries;
  @Autowired private MQGenericCohortQueries mQGenericCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public void constructTMqDatset(
      CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addColumn(
        "CAT11AdultoAPSSPPDENOMINATOR",
        "11.1: Adultos (15/+anos) em TARV com o mínimo de 3 consultas de seguimento de "
            + "adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQGenericCohortQueries
                    .findPatientOnARTdExcludingPregantAndBreastfeedingAndTransferredInTransferredOut(),
                "CAT11AdultoAPSSPPDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT11AdultoAPSSPPNUMERATOR",
        "11.1: Adultos (15/+anos) em TARV com o mínimo de 3 consultas de seguimento de "
            + "adesão na FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatientsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NUMERATOR(),
                "CAT11AdultoAPSSPPNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=15+");

    dataSetDefinition.addColumn(
        "CAT11Adulto1000CVDENOMINATOR",
        "11.2: Adultos (15/+anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram "
            + "3 consultas de APSS/PP mensais consecutivas para reforço de adesão Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT11Adulto1000CVDENOMINATOR",
                mappings),
            mappings),
        "ageOnCV=15+");

    dataSetDefinition.addColumn(
        "CAT11Adulto1000CVNUMERATOR",
        "11.2: % de adultos (15/+anos) na 1a linha de TARV com CV acima de 1000 cópias "
            + "que tiveram 3 consultas de APSS/PP mensais consecutivas para reforço de adesão Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR(),
                "CAT11Adulto1000CVNUMERATOR",
                mappings),
            mappings),
        "ageOnCV=15+");

    dataSetDefinition.addColumn(
        "CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR",
        "11.5: Crianças >2 anos de idade em TARV com registo mensal de seguimento da"
            + " adesão na ficha de APSS/PP nos primeiros 99 dias de TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQGenericCohortQueries
                    .findPatientOnARTdExcludingPregantAndBreastfeedingAndTransferredInTransferredOut(),
                "CAT11ChildrenBiggerThen2eLess14APSSPPDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=2-14");

    dataSetDefinition.addColumn(
        "CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR",
        "11.5: Crianças >2 anos de idade em TARV com registo mensal de seguimento da "
            + "adesão na ficha de APSS/PP nos primeiros 99 dias de TARV Numerador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatientsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NUMERATOR(),
                "CAT11ChildrenBiggerThen2eLess14APSSPPNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=2-14");

    dataSetDefinition.addColumn(
        "CAT11Least9APSSConsultationDENOMINATOR",
        "11.6: Crianças <2 anos de idade em TARV com registo mensal de seguimento da "
            + "adesão na ficha de APSS/PP no primeiro ano de TARV Denominador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQGenericCohortQueries
                    .findPatientOnARTdExcludingPregantAndBreastfeedingAndTransferredInTransferredOut(),
                "CAT11Least9APSSConsultationDENOMINATOR",
                mappings),
            mappings),
        "ageMqNewART=<9MONTHS");

    dataSetDefinition.addColumn(
        "CAT11Least9APSSConsultationNUMERATOR",
        "11.6: Crianças <2 anos de idade em TARV com registo mensal de seguimento da "
            + "adesão na ficha de APSS/PP no primeiro ano de TARV Numerador ",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatientsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTransferedOutCategory11SectionAPSS_I(),
                "CAT11Least9APSSConsultationNUMERATOR",
                mappings),
            mappings),
        "ageMqNewART=<9MONTHS");

    dataSetDefinition.addColumn(
        "CAT11Children1000CVDENOMINATOR",
        "11.7: Crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram "
            + "3 consultas mensais consecutivas de APSS/PP para reforço de adesão Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11Denominator(),
                "CAT11Children1000CVDENOMINATOR",
                mappings),
            mappings),
        "ageOnCV=15-");

    dataSetDefinition.addColumn(
        "CAT11Children1000CVNUMERATOR",
        "11.7: Crianças (0-14 anos) na 1a linha de TARV com CV acima de 1000 cópias que tiveram"
            + " 3 consultas mensais consecutivas de APSS/PP para reforço de adesão Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11CohortQueries
                    .findPatietsOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR(),
                "CAT11Children1000CVNUMERATOR",
                mappings),
            mappings),
        "ageOnCV=15-");

    dataSetDefinition.addColumn(
        "CAT11PREGNANTAPSSPPDENOMINATOR",
        "11.3: Mulheres Gravidas em TARV com o mínimo de 3 consultas de seguimento de adesão na "
            + "FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11P2CohortQueries
                    .findPregnantOnARTStartedExcludingBreastfeedingAndTransferredInCategory11DenominatorP2(),
                "CAT11PREGNANTAPSSPPDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT11PREGNANTAPSSPPNUMINATOR",
        "11.3: Mulheres Gravidas em TARV com o mínimo de 3 consultas de seguimento de adesão na "
            + "FM-ficha de APSS/PP nos primeiros 3 meses após início do TARV Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11P2CohortQueries
                    .findPregnantOnARTStartedExcludingBreastfeedingAndTransferredInTRANSFEREDOUTCategory11NumeratorP2(),
                "CAT11PREGNANTAPSSPPNUMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT11PREGNANT1000CVDENOMINATOR",
        "11.4: Mulheres Gravidas na 1a linha de TARV com CV acima de 1000 cópias que tiveram "
            + "3 consultas de APSS/PP mensais consecutivas para reforço de adesão Denominador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11P2CohortQueries
                    .findPregnantOnARTStartedExcludingBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11DenominatorP2(),
                "CAT11PREGNANT1000CVDENOMINATOR",
                mappings),
            mappings),
        "");

    dataSetDefinition.addColumn(
        "CAT11PREGNANT1000CVNUMINATOR",
        "11.4: Mulheres Gravidas na 1a linha de TARV com CV acima de 1000 cópias que tiveram "
            + "3 consultas de APSS/PP mensais consecutivas para reforço de adesão Numerador",
        EptsReportUtils.map(
            this.setIndicatorWithAllParameters(
                this.mQCategory11P2CohortQueries
                    .findPregnantOnARTStartedExcludingPregantAndBreastfeedingAndTransferredInTRANSFEREDOUTWITH1000CVCategory11NUMERATOR(),
                "CAT11PREGNANT1000CVNUMINATOR",
                mappings),
            mappings),
        "");
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
