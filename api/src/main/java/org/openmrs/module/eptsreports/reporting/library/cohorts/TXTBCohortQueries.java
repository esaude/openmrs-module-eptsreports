package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBCohortQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  private String generalParameterMapping =
      "startDate=${startDate},endDate=${endDate},location=${location}";

  private String codedObsParameterMapping =
      "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";

  private Mapped<CohortDefinition> map(CohortDefinition cd, String parameterMappings) {
    return EptsReportUtils.map(
        cd,
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, parameterMappings));
  }

  private void addGeneralParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * INICIO DE TRATAMENTO DE TUBERCULOSE DATA NOTIFICADA NAS FICHAS DE: SEGUIMENTO, RASTREIO E LIVRO
   * TB. codes: DATAINICIO
   */
  public CohortDefinition tbTreatmentStartDateWithinReportingDate() {
    CohortDefinition definition =
        genericCohortQueries.generalSql(
            "startedTbTreatment",
            TXTBQueries.dateObs(
                tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    hivMetadata.getPediatriaSeguimentoEncounterType().getId()),
                true));
    addGeneralParameters(definition);
    return definition;
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO */
  public CohortDefinition getInTBProgram() {
    CohortDefinition definition =
        genericCohortQueries.generalSql(
            "TBPROGRAMA",
            TXTBQueries.inTBProgramWithinReportingPeriodAtLocation(
                tbMetadata.getTBProgram().getProgramId()));
    addGeneralParameters(definition);
    return definition;
  }

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB (Condicoes medicas
   * importantes – Ficha Resumo Mastercard during reporting period
   *
   * @return cd
   */
  public CohortDefinition getPulmonaryTB() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "PULMONARYTB",
            TXTBQueries.pulmonaryTB(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                tbMetadata.getPulmonaryTB().getConceptId(),
                commonMetadata.getYesConcept().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * Patients marked as “Tratamento TB = Inicio (I) ” in Ficha Clinica Master Card
   *
   * @return cd
   */
  public CohortDefinition getTBTreatmentStart() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TBTREATMENTSTART",
            TXTBQueries.tbTreatmentStart(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTBTreatmentPlanConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getTuberculosisSymptoms() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "tuberculosisSymptoms",
            TXTBQueries.tuberculosisSympots(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getHasTbSymptomsConcept().getConceptId(),
                commonMetadata.getYesConcept().getConceptId(),
                commonMetadata.getNoConcept().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getActiveTuberculosis() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "activeTuberculosis",
            TXTBQueries.activeTuberculosis(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getActiveTBConcept().getConceptId(),
                commonMetadata.getYesConcept().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getTBObservation() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "tbObservation",
            TXTBQueries.tbObservation(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getObservationTB().getConceptId(),
                tbMetadata.getFeverLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getWeightLossOfMoreThan3KgInLastMonth().getConceptId(),
                tbMetadata.getNightsWeatsLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getCoughLastingMoraThan3Weeks().getConceptId(),
                tbMetadata.getAsthenia().getConceptId(),
                tbMetadata.getCohabitantBeingTreatedForTB().getConceptId(),
                tbMetadata.getLymphadenopathy().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getApplicationForLaboratoryResearch() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "applicationForLaboratoryResearch",
            TXTBQueries.applicationForLaboratoryResearch(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                tbMetadata.getTBGenexpertTest().getConceptId(),
                tbMetadata.getCultureTest().getConceptId(),
                tbMetadata.getTestTBLAM().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getTBGenexpertTest() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TBGenexpertTest",
            TXTBQueries.tbGenexpertTest(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTBGenexpertTest().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getCultureTest() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "CultureTest",
            TXTBQueries.cultureTest(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getCultureTest().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getTestTBLAM() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TestTBLAM",
            TXTBQueries.testTBLAM(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTestTBLAM().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getResultForBasiloscopia() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TestTBLAM",
            TXTBQueries.resultForBasiloscopia(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                hivMetadata.getResultForBasiloscopia().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO codes: RASTREIOTBNEG */
  public CohortDefinition codedNoTbScreening() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getNoConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO codes: RASTREIOTBPOS */
  public CohortDefinition codedYesTbScreening() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getTbScreeningConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getYesConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artList() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addSearch(
        "started-by-end-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("started-by-end-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveInvestigationResult() {
    CohortDefinition cd =
        genericCohortQueries.hasCodedObs(
            tbMetadata.getResearchResultConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getPediatriaSeguimentoEncounterType()),
            Arrays.asList(tbMetadata.getPositiveConcept()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “POS” or “NEG” selected for “Resultado da Investigação para TB de BK e/ou RX?”
   * during the reporting period consultations; ( response 703: POS or 664: NEG for question: 6277)
   */
  public CohortDefinition positiveInvestigationResultComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition P = positiveInvestigationResult();
    cd.addSearch("P", map(P, codedObsParameterMapping));
    cd.setCompositionString("P");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “S” or “N” selected for TB Screening (Rastreio de TB) during the reporting period
   * consultations; (response 1065: YES or 1066: NO for question 6257: SCREENING FOR TB)
   */
  public CohortDefinition yesOrNoInvestigationResult() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition S = codedYesTbScreening();
    cd.addSearch("S", map(S, codedObsParameterMapping));
    CohortDefinition N = codedNoTbScreening();
    cd.addSearch("N", map(N, codedObsParameterMapping));
    cd.setCompositionString("S OR N");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumeratorA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition i =
        genericCohortQueries.generalSql(
            "onTbTreatment",
            TXTBQueries.dateObs(
                tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    hivMetadata.getPediatriaSeguimentoEncounterType().getId()),
                true));
    addGeneralParameters(i);
    cd.addSearch("i", map(i, generalParameterMapping));
    CohortDefinition ii = getInTBProgram();
    cd.addSearch("ii", map(ii, generalParameterMapping));
    CohortDefinition artList = artList();
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii) AND artList");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition A = txTbNumeratorA();
    cd.addSearch("A", map(A, generalParameterMapping));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            tbTreatmentStartDateWithinReportingDate(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));

    cd.setCompositionString("A NOT started-tb-treatment-previous-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addSearch("A", EptsReportUtils.map(codedYesTbScreening(), codedObsParameterMapping));
    cd.addSearch(
        "B",
        EptsReportUtils.map(positiveInvestigationResultComposition(), generalParameterMapping));
    cd.addSearch(
        "C",
        EptsReportUtils.map(tbTreatmentStartDateWithinReportingDate(), generalParameterMapping));
    cd.addSearch("D", EptsReportUtils.map(getInTBProgram(), generalParameterMapping));
    cd.setCompositionString("A OR B OR C OR D");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition newOnARTPositiveScreening() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch("new-on-art", EptsReportUtils.map(getNewOnArt(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND new-on-art AND positive-screening");
    return definition;
  }

  public CohortDefinition newOnARTNegativeScreening() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch("new-on-art", EptsReportUtils.map(getNewOnArt(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND new-on-art) NOT positive-screening");
    return definition;
  }

  public CohortDefinition previouslyOnARTPositiveScreening() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch("new-on-art", EptsReportUtils.map(getNewOnArt(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND positive-screening) NOT new-on-art");
    return definition;
  }

  public CohortDefinition previouslyOnARTNegativeScreening() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("previouslyOnARTNegativeScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch("new-on-art", EptsReportUtils.map(getNewOnArt(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator NOT (new-on-art OR positive-screening)");
    return definition;
  }

  public CohortDefinition patientsNewOnARTNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition NUM = txTbNumerator();
    cd.addSearch("NUM", map(NUM, generalParameterMapping));
    cd.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("NUM AND started-during-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition patientsPreviouslyOnARTNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition NUM = txTbNumerator();
    cd.addSearch("NUM", map(NUM, generalParameterMapping));
    cd.addSearch(
        "started-before-start-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${startDate-1d},location=${location}"));
    cd.setCompositionString("NUM AND started-before-start-reporting-period");
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    addGeneralParameters(definition);
    definition.setName("TxTB - Denominator");
    definition.addSearch(
        "art-list",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));
    definition.addSearch(
        "tb-screening", EptsReportUtils.map(yesOrNoInvestigationResult(), generalParameterMapping));
    definition.addSearch(
        "tb-investigation",
        EptsReportUtils.map(positiveInvestigationResultComposition(), generalParameterMapping));
    definition.addSearch(
        "started-tb-treatment",
        EptsReportUtils.map(tbTreatmentStartDateWithinReportingDate(), generalParameterMapping));
    definition.addSearch(
        "in-tb-program", EptsReportUtils.map(getInTBProgram(), generalParameterMapping));
    definition.addSearch(
        "pulmonary-tb", EptsReportUtils.map(getPulmonaryTB(), generalParameterMapping));
    definition.addSearch(
        "marked-as-tb-treatment-start",
        EptsReportUtils.map(getTBTreatmentStart(), generalParameterMapping));

    definition.addSearch(
        "tuberculosis-symptomys",
        EptsReportUtils.map(getTuberculosisSymptoms(), generalParameterMapping));

    definition.addSearch(
        "active-tuberculosis",
        EptsReportUtils.map(getActiveTuberculosis(), generalParameterMapping));

    definition.addSearch(
        "tb-observations", EptsReportUtils.map(getTBObservation(), generalParameterMapping));

    definition.addSearch(
        "application-for-laboratory-research",
        EptsReportUtils.map(getApplicationForLaboratoryResearch(), generalParameterMapping));

    definition.addSearch(
        "tb-genexpert-test", EptsReportUtils.map(getTBGenexpertTest(), generalParameterMapping));

    definition.addSearch(
        "culture-test", EptsReportUtils.map(getCultureTest(), generalParameterMapping));

    definition.addSearch(
        "test-tb-lam", EptsReportUtils.map(getTestTBLAM(), generalParameterMapping));

    definition.addSearch(
        "result-for-basiloscopia",
        EptsReportUtils.map(getResultForBasiloscopia(), generalParameterMapping));

    definition.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            tbTreatmentStartDateWithinReportingDate(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));
    definition.addSearch(
        "in-tb-program-previous-period",
        EptsReportUtils.map(
            getInTBProgram(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));

    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    definition.setCompositionString(
        "(art-list AND (tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program OR pulmonary-tb OR marked-as-tb-treatment-start"
            + "OR (tuberculosis-symptomys OR active-tuberculosis OR tb-observations OR application-for-laboratory-research OR tb-genexpert-test OR culture-test"
            + "OR test-tb-lam) OR result-for-basiloscopia))"
            + "NOT ((transferred-out NOT (started-tb-treatment OR in-tb-program)) OR started-tb-treatment-previous-period OR in-tb-program-previous-period)");

    return definition;
  }

  public CohortDefinition getNewOnArt() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB New on ART");
    addGeneralParameters(definition);
    definition.addSearch(
        "started-on-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    definition.setCompositionString("started-on-period");
    return definition;
  }
}
