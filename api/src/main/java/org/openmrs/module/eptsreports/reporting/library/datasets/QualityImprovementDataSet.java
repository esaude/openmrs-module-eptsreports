package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.QualityImprovementCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class QualityImprovementDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private QualityImprovementCohortQueries qualityImprovementCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructInitQltyImpDataSet() {

    final String mappingsAll =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location},testStart=${testStart}";

    final String mappingsWitshEvaluate =
        "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Quality Improvement DataSet");
    dataSetDefinition.addParameters(getParameters());

    /* add dimensions */
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    /* CONSULTAINIC */
    CohortIndicator consultanic =
        eptsGeneralIndicator.getIndicator(
            "patientInARVSampleWithEncounterIn7DaysAfterDianosisO",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInARVSampleWithEncounterIn7DaysAfterDianosis(),
                mappingsWitshEvaluate));
    consultanic.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "CONSULTAINIC.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV E QUE TIVERAM CONSULTA CLINICA DENTRO DE 7 DIAS APOS DIAGNOSTICO",
        EptsReportUtils.map(consultanic, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /* COORTEINIC*/
    CohortIndicator coortenic =
        eptsGeneralIndicator.getIndicator(
            "patientStartedARVInInclusionPeriodWithAtLeastOneEncounter",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(),
                "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"));
    coortenic.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "COORTEINIC.IDADE",
        "MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO",
        EptsReportUtils.map(
            coortenic,
            "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"),
        getDisagregateAdultsAndChildrenSColumn());

    /* RASTREIOTB */
    CohortIndicator reatreioTB =
        eptsGeneralIndicator.getIndicator(
            "patientsInARVSampleNotInTBTrackEncounter",
            EptsReportUtils.map(
                qualityImprovementCohortQueries.getPatientsInARVSampleNotInTBTrackEncounter(),
                mappingsWitshEvaluate));
    reatreioTB.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "RASTREIOTB.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV E QUE NAO SE ENCONTRAM EM TRATAMENTO DE TB E RASTREIADOS EM CADA CONSULTA",
        EptsReportUtils.map(reatreioTB, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /* RASTREIOTBGRAVIDA */
    CohortIndicator reatreioGravida =
        eptsGeneralIndicator.getIndicator(
            "pregnantPatientsInTBTrackForEachEncounter",
            EptsReportUtils.map(
                qualityImprovementCohortQueries.getPregnantPatientsInTBTrackForEachEncounter(),
                mappingsWitshEvaluate));
    reatreioGravida.addParameter(
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    dataSetDefinition.addColumn(
        "RASTREIOTBGRAVIDA",
        "MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) E QUE FORAM RASTREIADAS PARA TUBERCULOSE EM CADA VISITA",
        EptsReportUtils.map(reatreioGravida, mappingsWitshEvaluate),
        "");

    /* GRAVIDASINIC */
    CohortIndicator gravidaInic =
        eptsGeneralIndicator.getIndicator(
            "pragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    dataSetDefinition.addColumn(
        "GRAVIDASINIC",
        "MQ_GRAVIDAS INSCRITAS NO SERVICO TARV E QUE INICIARAM TARV NO PERIODO DE INCLUSAO (AMOSTRA GRAVIDA)",
        EptsReportUtils.map(
            gravidaInic, "startDate=${startDate},endDate=${endDate},location=${location}"),
        "");

    /* TPINUM1*/
    CohortIndicator tpiNum1 =
        eptsGeneralIndicator.getIndicator(
            "TPINUM1.IDADE",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaTPI(),
                mappingsWitshEvaluate));
    tpiNum1.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Avaliacao", Date.class));

    addRow(
        dataSetDefinition,
        "TPINUM1.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA (TPI) E QUE RECEBERAM",
        EptsReportUtils.map(tpiNum1, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*TPIDENOM1*/
    CohortIndicator tpiDeNom1 =
        eptsGeneralIndicator.getIndicator(
            "TPIDENOM1.IDADE",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod(),
                mappingsWitshEvaluate));
    tpiDeNom1.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));
    addRow(
        dataSetDefinition,
        "TPIDENOM1.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA (TPI) NUM DETERMINADO PERIODO",
        EptsReportUtils.map(tpiDeNom1, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*TPINUM2*/
    CohortIndicator tpiNum2 =
        eptsGeneralIndicator.getIndicator(
            "TPINUM2.IDADE",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsInARVSampleElegibleToProphilaxisIzoniazidWhomCompleted(),
                mappingsWitshEvaluate));
    tpiNum2.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "TPINUM2.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA QUE INICIARAM, DEVERIAM TERMINAR E TERMINARAM",
        EptsReportUtils.map(tpiNum2, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*TPIDENOM2*/
    CohortIndicator tpiDeNom2 =
        eptsGeneralIndicator.getIndicator(
            "TPIDENOM2.IDADE",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInARVSampleElegibleToProphylaxisIsoniazisStaredShouldComplete(),
                mappingsWitshEvaluate));
    tpiDeNom2.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "TPIDENOM2.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV ELEGIVEIS A PROFILAXIA COM ISONIAZIDA, INICIARAM E QUE DEVERIAM TERMINAR",
        EptsReportUtils.map(tpiDeNom2, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*TPINUM3*/
    CohortIndicator tpiNum3 =
        eptsGeneralIndicator.getIndicator(
            "patientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived(),
                "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}"));
    tpiNum3.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    dataSetDefinition.addColumn(
        "TPINUM3",
        "MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) ELEGIVEIS A PROFILAXIA COM ISONIAZIDA E QUE RECEBERAM",
        EptsReportUtils.map(
            tpiNum3,
            "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}"),
        "");

    /*TPIDENOM3*/
    CohortIndicator tpiDeNom3 =
        eptsGeneralIndicator.getIndicator(
            "patientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazid",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazid(),
                mappingsWitshEvaluate));
    tpiDeNom3.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    dataSetDefinition.addColumn(
        "TPIDENOM3",
        "MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) ELEGIVEIS A PROFILAXIA COM ISONIAZIDA",
        EptsReportUtils.map(
            tpiDeNom3,
            "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}"),
        "");

    /*RASTREIOITS*/
    CohortIndicator rastreioITS =
        eptsGeneralIndicator.getIndicator(
            "pacientsInARVSampleWhichHadScreeningForSTIInEncounterAnalisisPeriod",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPacientsInARVSampleWhichHadScreeningForSTIInEncounterAnalisisPeriod(),
                mappingsWitshEvaluate));
    rastreioITS.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "RASTREIOITS.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV E QUE TIVERAM RASTREIO DE ITS EM CADA VISITA DO PERIODO DE ANALISE",
        EptsReportUtils.map(rastreioITS, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*CD4NUM*/
    CohortIndicator cd4num =
        eptsGeneralIndicator.getIndicator(
            "pacientsInARVWithCD4SampleRegisteredWithin35Days",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPacientsInARVWithCD4SampleRegisteredWithin35Days(),
                mappingsWitshEvaluate));
    cd4num.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "CD4NUM.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV COM CD4 REGISTADO DENTRO DE 35 DIAS APOS A INSCRICAO",
        EptsReportUtils.map(cd4num, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*ELEGIBILIDADE*/
    CohortIndicator elegibility =
        eptsGeneralIndicator.getIndicator(
            "pacientsInARVSampleStartedIn15DaysAfterBeingDeclaredAsElegible",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPacientsInARVSampleStartedIn15DaysAfterBeingDeclaredAsElegible(),
                mappingsWitshEvaluate));
    elegibility.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "ELEGIBILIDADE.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV E QUE INICIARAM TARV DENTRO DE 15 DIAS DEPOIS DE DECLARADAS ELEGIVEIS",
        EptsReportUtils.map(elegibility, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM5*/
    CohortIndicator retnunm5 =
        eptsGeneralIndicator.getIndicator(
            "pacientsARVInAPeriodWhoReturnedToEncounter33DaysAfterBegining",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPacientsARVInAPeriodWhoReturnedToEncounter33DaysAfterBegining(),
                mappingsWitshEvaluate));
    retnunm5.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));
    retnunm5.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    addRow(
        dataSetDefinition,
        "RETNUM5.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV QUE RETORNARAM PARA 2ª CONSULTA CLINICA OU LEVANTAMENTO DE ARV DENTRO DE 33 DIAS APÓS INICIO DO TARV",
        EptsReportUtils.map(retnunm5, mappingsAll),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM1*/
    CohortIndicator retnum1 =
        eptsGeneralIndicator.getIndicator(
            "pacientsARVSampleWhoHadAtLeast3Encounters3MonthsAfterTARVStartNew",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPacientsARVSampleWhoHadAtLeast3Encounters3MonthsAfterTARVStartNew(),
                mappingsWitshEvaluate));
    retnum1.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));
    retnum1.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    addRow(
        dataSetDefinition,
        "RETNUM1.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV QUE TIVERAM PELO MENOS 3 CONSULTAS CLINICAS OU LEVANTAMENTO DE ARV DENTRO DE 3 MESES DEPOIS DE INICIO DE TARV - NOVO",
        EptsReportUtils.map(retnum1, mappingsAll),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM2*/
    CohortIndicator retnum2 =
        eptsGeneralIndicator.getIndicator(
            "getPatientInTARVSampleWhoHaddAtLeast3AdherenceEvaluationWithin3MothsARIEL",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInTARVSampleWhoHaddAtLeast3AdherenceEvaluationWithin3MothsARIEL(),
                mappingsWitshEvaluate));
    retnum2.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));
    retnum2.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    addRow(
        dataSetDefinition,
        "RETNUM2.IDADE",
        "MQ_PACIENTES NA AMOSTRA TARV QUE TIVERAM PELO MENOS 3 AVALIAÇÕES DE ADESÃO DENTRO DE 3 MESES DEPOIS DE INICIO DE TARV - NOVO",
        EptsReportUtils.map(retnum2, mappingsAll),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM3*/
    CohortIndicator retnum3 =
        eptsGeneralIndicator.getIndicator(
            "patientInTARVSampleWhomHadMonthEncountersAfterTARVInitialization",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInTARVSampleWhomHadMonthEncountersAfterTARVInitialization(),
                mappingsWitshEvaluate));
    retnum3.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "RETNUM3.IDADE",
        "MQ_PACIENTES NA AMOSTRA (INICIO TARV) E QUE TIVERAM CONSULTAS MENSAIS APOS INICIO DE TARV NOVO",
        EptsReportUtils.map(retnum3, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM7*/
    CohortIndicator retnum7 =
        eptsGeneralIndicator.getIndicator(
            "patientInTARVSampleWhomHadAPSSMonthEncountersAfterTARVInitialization",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInTARVSampleWhomHadAPSSMonthEncountersAfterTARVInitialization(),
                mappingsWitshEvaluate));
    retnum7.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "RETNUM7.IDADE",
        "MQ_PACIENTES NA AMOSTRA (INICIO TARV) E QUE TIVERAM CONSULTAS MENSAIS DE APSS APOS INICIO DE TARV",
        EptsReportUtils.map(retnum7, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*RETNUM6*/
    CohortIndicator retnum6 =
        eptsGeneralIndicator.getIndicator(
            "pregantPatientInTARVSampleReturnedToSecondEncounterIn33ARVStart",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPregantPatientInTARVSampleReturnedToSecondEncounterIn33ARVStart(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));
    retnum6.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    dataSetDefinition.addColumn(
        "RETNUM6",
        "MQ_GRAVIDAS NA AMOSTRA TARV QUE RETORNARAM PARA 2ª CONSULTA CLINICA OU LEVANTAMENTO DE ARV DENTRO DE 33 DIAS APÓS INICIO DO TARV",
        EptsReportUtils.map(
            retnum6,
            "startDate=${startDate},endDate=${endDate},location=${location},testStart=${testStart}"),
        "");

    /*RETNUM4*/
    CohortIndicator retnum4 =
        eptsGeneralIndicator.getIndicator(
            "pregantPatientsEnrolledInTARVServiceWhoHas3EncountersInFisrt3MonthsTARVNew",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPregantPatientsEnrolledInTARVServiceWhoHas3EncountersInFisrt3MonthsTARVNew(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));
    retnum4.addParameter(new Parameter("testStart", "Test Start", Boolean.class));

    dataSetDefinition.addColumn(
        "RETNUM4",
        "MQ_GRAVIDAS INSCRITAS NO SERVICO TARV (AMOSTRA GRAVIDA) E QUE TIVERAM 3 CONSULTAS OU 3 LEVANTAMENTOS NOS PRIMEIROS 3 MESES DE TARV - NOVO",
        EptsReportUtils.map(
            retnum4,
            "startDate=${startDate},endDate=${endDate},location=${location},testStart=${testStart}"),
        "");

    /*MODELODIFNUM*/
    CohortIndicator modeloDifNum =
        eptsGeneralIndicator.getIndicator(
            "patientsElegibleInDiffModelAndAreEnrolledInDiffModel",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsElegibleInDiffModelAndAreEnrolledInDiffModel(),
                mappingsWitshEvaluate));
    modeloDifNum.addParameter(
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "MODELODIFNUM.IDADE",
        "PACIENTES ELEGIVEIS A MODELOS DIFERENCIADOS E QUE ESTÃO INSCRITOS EM ALGUM MODELO DIFERENCIADO",
        EptsReportUtils.map(modeloDifNum, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /* MODELODIFDENOM */
    CohortIndicator modeloDifNom =
        eptsGeneralIndicator.getIndicator(
            "patientsInARTElegibleToBeEnrolledInSomeDiffModel",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInARTElegibleToBeEnrolledInSomeDiffModel(),
                "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"));
    modeloDifNom.addParameter(
        new Parameter("dataFinalAvaliacao", "Data Final Avaliacao", Date.class));

    addRow(
        dataSetDefinition,
        "MODELODIFDENOM.IDADE",
        "PACIENTES ACTUALMENTE EM TARV ELEGIVEIS PARA SEREM INSCRITOS EM ALGUM MODELO DIFERENCIADO",
        EptsReportUtils.map(
            modeloDifNom,
            "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"),
        getDisagregateAdultsAndChildrenSColumn());

    /*CVNUM*/
    CohortIndicator cvnum =
        eptsGeneralIndicator.getIndicator(
            "patientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart(),
                "endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "CVNUM.IDADE",
        "MQ_PACIENTES QUE RECEBERAM RESULTADO DA CARGA VIRAL ENTRE O SEXTO E NONO MES DEPOIS DE INICIO DE TARV",
        EptsReportUtils.map(cvnum, "endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    /*CVDENOM*/
    CohortIndicator cvdenom =
        eptsGeneralIndicator.getIndicator(
            "patientStartedARVInInclusionPeriodWithAtLeastOneEncounter",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(),
                "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"));
    cvdenom.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "CVDENOM.IDADE",
        "MQ_INICIO TARV NO PERIODO DE INCLUSAO (AMOSTRA TARV) - NOVO",
        EptsReportUtils.map(
            cvdenom,
            "startDate=${dataFinalAvaliacao-12m+1d},endDate=${startDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"),
        getDisagregateAdultsAndChildrenSColumn());

    /*FALENCIANUM*/
    CohortIndicator falencianum =
        eptsGeneralIndicator.getIndicator(
            "patientInTARVInSecondLinheFinalPeriodoNotifiedAbandonmentAndNotNotified",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInTARVInSecondLinheFinalPeriodoNotifiedAbandonmentAndNotNotified(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "FALENCIANUM.IDADE",
        "PACIENTES ACTUALMENTE EM TARV E QUE ESTAO NA SEGUNDA LINHA DE ARV - PERIODO FINAL (ABANDONO RETIRA NOTIFICADO E NAO NOTIFICADO)",
        EptsReportUtils.map(
            falencianum, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    /*FALENCIADENOM*/
    CohortIndicator falenciadenom =
        eptsGeneralIndicator.getIndicator(
            "patientInTARVWithClinicOrImmunogicsFailtureNotification",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInTARVWithClinicOrImmunogicsFailtureNotification(),
                "startDate=${startDate},endDate=${endDate},location=${location}"));

    addRow(
        dataSetDefinition,
        "FALENCIADENOM.IDADE",
        "PACIENTES ACTUALMENTE EM TARV E NOTIFICADOS DE FALHAS CLINICAS OU IMUNOLOGICAS",
        EptsReportUtils.map(
            falenciadenom, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getDisagregateAdultsAndChildrenSColumn() {
    ColumnParameters ADULTOS = new ColumnParameters("ADULTOS", "Adultos", "age=15+", "ADULTOS");
    ColumnParameters CRIANCAS = new ColumnParameters("CRIANCAS", "Criancas", "age=<15", "CRIANCAS");
    return Arrays.asList(ADULTOS, CRIANCAS);
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startDate", "Data Inicial Inclusão", Date.class),
        new Parameter("endDate", "Data Final Inclusão", Date.class),
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class),
        new Parameter("testStart", "Testar Iniciar", Boolean.class));
  }
}
