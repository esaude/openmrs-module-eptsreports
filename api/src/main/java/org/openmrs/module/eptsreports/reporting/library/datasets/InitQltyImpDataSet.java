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
public class InitQltyImpDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private QualityImprovementCohortQueries qualityImprovementCohortQueries;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  private String mappingsAll =
      "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location},testStart=${testStart}";

  private String mappingsWitshEvaluate =
      "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao},location=${location}";

  private String mappings =
      "startDate=${startDate},endDate=${endDate},dataFinalAvaliacao=${dataFinalAvaliacao}";

  public DataSetDefinition constructInitQltyImpDataSet() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Quality Improvement DataSet");
    dataSetDefinition.addParameters(getParameters());

    /** add dimensions */
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    /* initial encounter for Adults & Children */
    CohortIndicator consultanic =
        eptsGeneralIndicator.getIndicator(
            "patientInARVSampleWithEncounterIn7DaysAfterDianosis",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientInARVSampleWithEncounterIn7DaysAfterDianosis(),
                mappingsWitshEvaluate));
    consultanic.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "CONSULTAINIC.IDADE",
        "Adults & Children initital encounter",
        EptsReportUtils.map(consultanic, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /* initial ARV period inclusion adult and children */
    CohortIndicator coortenic =
        eptsGeneralIndicator.getIndicator(
            "initialARVInInclusionPeriodWithAtLeastOneEncounter",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientStartedARVInInclusionPeriodWithAtLeastOneEncounter(),
                "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"));
    coortenic.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "COORTEINIC.IDADE",
        "initial  ARV period inclusion adult and  children",
        EptsReportUtils.map(
            coortenic,
            "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"),
        getDisagregateAdultsAndChildrenSColumn());

    /* patients In ARV Sample Not In TB Track Encounter */
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
        "patients In ARV Sample Not In TB Track Encounter",
        EptsReportUtils.map(reatreioTB, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /* rastreioGravida */
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
        "rastreio gravidas",
        EptsReportUtils.map(reatreioGravida, mappingsWitshEvaluate),
        "");

    /* gravidas inic */
    CohortIndicator gravidaInic =
        eptsGeneralIndicator.getIndicator(
            "pragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPragnantPatientsEnrolledInARVThatStartedInInclusionPeriodPregnantSample(),
                mappingsWitshEvaluate));
    gravidaInic.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    dataSetDefinition.addColumn(
        "GRAVIDASINIC", "gravidas inicial", EptsReportUtils.map(gravidaInic, mappings), "");

    /* TPINUM1*/
    CohortIndicator tpiNum1 =
        eptsGeneralIndicator.getIndicator(
            "TPINUM1.IDADE",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsInARVSampleElegibleToProfilaxiaWithIzonzidaTPI(),
                mappingsWitshEvaluate));
    tpiNum1.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "TPINUM1.IDADE",
        "patientsInARVSampleElegibleToProfilaxiaWithIzonzidaTPI",
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
        "patientsInARVSampleElegibleToProfilaxiaWithIzonzidaInAPeriod",
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
        "patientsInARVSampleElegibleToProphilaxisIzoniazidWhomCompleted",
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
        "patientInARVSampleElegibleToProphylaxisIsoniazisStaredShouldComplete",
        EptsReportUtils.map(tpiDeNom2, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /*TPINUM3*/
    CohortIndicator tpiNum3 =
        eptsGeneralIndicator.getIndicator(
            "patientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived(),
                mappingsWitshEvaluate));
    tpiNum3.addParameter(new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    dataSetDefinition.addColumn(
        "TPINUM3",
        "patientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazidAndReceived",
        EptsReportUtils.map(tpiNum3, mappings),
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
        "patientsEnrolledInARVSamplePregantElegibleProphylaxisIsoniazid",
        EptsReportUtils.map(tpiDeNom3, mappings),
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
        "pacientsInARVSampleWhichHadScreeningForSTIInEncounterAnalisisPeriod",
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
        "pacientsInARVWithCD4SampleRegisteredWithin35Days",
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
        "pacientsInARVSampleStartedIn15DaysAfterBeingDeclaredAsElegible",
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
        "pacientsARVInAPeriodWhoReturnedToEncounter33DaysAfterBegining",
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
        "pacientsARVSampleWhoHadAtLeast3Encounters3MonthsAfterTARVStartNew",
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
        "getPatientInTARVSampleWhoHaddAtLeast3AdherenceEvaluationWithin3MothsARIEL",
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
        "patientInTARVSampleWhomHadMonthEncountersAfterTARVInitialization",
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
        "patientInTARVSampleWhomHadAPSSMonthEncountersAfterTARVInitialization",
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
        "pregantPatientInTARVSampleReturnedToSecondEncounterIn33ARVStart",
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
        "pregantPatientsEnrolledInTARVServiceWhoHas3EncountersInFisrt3MonthsTARVNew",
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
        "patientsElegibleInDiffModelAndAreEnrolledInDiffModel",
        EptsReportUtils.map(modeloDifNum, mappingsWitshEvaluate),
        getDisagregateAdultsAndChildrenSColumn());

    /** MODELODIFDENOM */
    CohortIndicator modeloDifNom =
        eptsGeneralIndicator.getIndicator(
            "patientsInARTElegibleToBeEnrolledInSomeDiffModel",
            EptsReportUtils.map(
                qualityImprovementCohortQueries
                    .getPatientsInARTElegibleToBeEnrolledInSomeDiffModel(),
                mappingsWitshEvaluate));
    modeloDifNom.addParameter(
        new Parameter("dataFinalAvaliacao", "Data Final Revisão", Date.class));

    addRow(
        dataSetDefinition,
        "MODELODIFDENOM.IDADE",
        "patientsInARTElegibleToBeEnrolledInSomeDiffModel",
        EptsReportUtils.map(modeloDifNom, mappingsWitshEvaluate),
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
        "patientWhoReceivedViralLoadFindingBetween6to9MonthAfterTARVStart",
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
        "patientStartedARVInInclusionPeriodWithAtLeastOneEncounter",
        EptsReportUtils.map(
            cvdenom,
            "startDate=${startDate},endDate=${endDate},location=${location},dataFinalAvaliacao=${dataFinalAvaliacao}"),
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
        "patientInTARVInSecondLinheFinalPeriodoNotifiedAbandonmentAndNotNotified",
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
        "patientInTARVWithClinicOrImmunogicsFailtureNotification",
        EptsReportUtils.map(
            falenciadenom, "startDate=${startDate},endDate=${endDate},location=${location}"),
        getDisagregateAdultsAndChildrenSColumn());

    return dataSetDefinition;
  }

  private List<ColumnParameters> getDisagregateAdultsAndChildrenSColumn() {
    ColumnParameters ADULTOS = new ColumnParameters("ADULTOS", "Adulos", "age=15+", "ADULTOS");
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
