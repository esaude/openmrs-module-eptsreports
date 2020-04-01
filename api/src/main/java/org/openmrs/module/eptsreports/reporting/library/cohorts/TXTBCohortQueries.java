package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
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
            TXTBQueries.tuberculosisSymptoms(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getHasTbSymptomsConcept().getConceptId(),
                commonMetadata.getYesConcept().getConceptId(),
                commonMetadata.getNoConcept().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getTuberculosisSymptomsPositiveScreening() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "tuberculosisSymptoms",
            TXTBQueries.tuberculosisSymptoms(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getHasTbSymptomsConcept().getConceptId(),
                commonMetadata.getYesConcept().getConceptId(),
                null));
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
            "ResultForBasiloscopia",
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

    String mappings = "onOrBefore=${endDate},location=${location}";

    cd.addSearch(
        "started-by-end-reporting-period",
        EptsReportUtils.map(genericCohortQueries.getStartedArtBeforeDate(false), mappings));

    cd.addSearch(
        "trasnferedInProgram",
        EptsReportUtils.map(hivCohortQueries.getTransferredInViaProgram(false), mappings));

    cd.addSearch(
        "trasnferedInMasterCard",
        EptsReportUtils.map(hivCohortQueries.getTransferredInViaMastercard(), mappings));

    cd.setCompositionString(
        "started-by-end-reporting-period NOT (trasnferedInProgram OR trasnferedInMasterCard)");
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
    cd.addSearch("B", mapStraightThrough(positiveInvestigationResultComposition()));
    cd.addSearch("C", mapStraightThrough(tbTreatmentStartDateWithinReportingDate()));
    cd.addSearch("D", mapStraightThrough(getInTBProgram()));
    cd.addSearch("E", mapStraightThrough(getResultForBasiloscopia()));
    cd.addSearch("F", mapStraightThrough(getTBTreatmentStart()));
    cd.addSearch("G", mapStraightThrough(getPulmonaryTB()));
    cd.addSearch("H", mapStraightThrough(getPatientsWithAtLeastOneResponseForPositiveScreeningH()));
    cd.setCompositionString("A OR B OR C OR D OR E OR F OR G OR H");
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
    definition.addSearch("art-list", EptsReportUtils.map(artList(), generalParameterMapping));
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
        "(art-list AND (tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program OR pulmonary-tb OR marked-as-tb-treatment-start "
            + "OR (tuberculosis-symptomys OR active-tuberculosis OR tb-observations OR application-for-laboratory-research OR tb-genexpert-test OR culture-test "
            + "OR test-tb-lam) OR result-for-basiloscopia)) "
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

  private CompositionCohortDefinition getPatientsWithAtLeastOneResponseForPositiveScreeningH() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addSearch(
        "tuberculosis-symptomys", mapStraightThrough(getTuberculosisSymptomsPositiveScreening()));
    cd.addSearch("active-tuberculosis", mapStraightThrough(getActiveTuberculosis()));
    cd.addSearch("tb-observations", mapStraightThrough(getTBObservation()));
    cd.addSearch(
        "application-for-laboratory-research",
        mapStraightThrough(getApplicationForLaboratoryResearch()));
    cd.addSearch("tb-genexpert-test", mapStraightThrough(getTBGenexpertTest()));
    cd.addSearch("culture-test", mapStraightThrough(getCultureTest()));
    cd.addSearch("test-tb-lam", mapStraightThrough(getTestTBLAM()));
    cd.setCompositionString(
        "tuberculosis-symptomys OR active-tuberculosis OR tb-observations "
            + "OR application-for-laboratory-research OR tb-genexpert-test OR culture-test OR test-tb-lam");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * Get patients with specimen sent
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSpecimenSent() {
    CohortDefinition cd =
        getPatientsWhoHaveSentSpecimen(
            hivMetadata.getMisauLaboratorioEncounterType(),
            hivMetadata.getApplicationForLaboratoryResearch(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getResultForBasiloscopia(),
            tbMetadata.getTBGenexpertTest(),
            tbMetadata.getTestTBLAM(),
            tbMetadata.getCultureTest(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * Get patients who have a GeneXpert Positivo or Negativo registered in the investigations - Ficha
   * Clinica - Mastercard OR have a GeneXpert request registered in the investigations - Ficha
   * Clinica - Mastercard
   *
   * @return CohortDefinition
   */
  public CohortDefinition getGenExpert() {
    CohortDefinition cd =
        getPatientsWhoHaveGeneXpert(
            hivMetadata.getApplicationForLaboratoryResearch(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTBGenexpertTest(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * Get patients who have a Basiloscopia And Not GeneXpert registered
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSmearMicroscopyOnly() {
    CohortDefinition cd =
        getSmearMicroscopyOnly(
            hivMetadata.getMisauLaboratorioEncounterType(),
            hivMetadata.getResultForBasiloscopia(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * Get patients who have a Additional Test AND Not GeneXpert AND Not Smear Microscopy Only
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAdditionalTest() {
    CohortDefinition cd =
        getAdditionalTest(
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTestTBLAM(),
            tbMetadata.getCultureTest(),
            hivMetadata.getApplicationForLaboratoryResearch(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * Get patients from denominator who have positive results returned registered during the period
   * Have a ‘GeneXpert Positivo’ registered in the investigacoes – resultados laboratoriais - ficha
   * clinica – mastercard OR Have a ‘resultado baciloscopia positive’ registered in the laboratory
   * form OR Have a TB LAM positivo registered in the investigacoes – resultados laboratoriais ficha
   * clinica – mastercard OR Have a cultura positiva registered in the investigacoes – resultados
   * laboratoriais ficha clinica – mastercard
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPositiveResultsReturned() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "positiveResultsReturned",
            TXTBQueries.getPositiveResultsReturned(
                hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getResultForBasiloscopia().getConceptId(),
                tbMetadata.getTBGenexpertTest().getConceptId(),
                tbMetadata.getTestTBLAM().getConceptId(),
                tbMetadata.getCultureTest().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * BR-8 Specimen Sent - Get patients from denominator AND tb_screened AND specimen_sent
   *
   * @return CohortDefinition
   */
  public CohortDefinition specimenSent() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("specimenSent()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch(
        "specimen-sent", EptsReportUtils.map(getSpecimenSent(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND specimen-sent AND positive-screening");
    return definition;
  }

  /**
   * BR-9 GenExpert MTB/RIF - Get patients from denominator AND tb_screened AND genexpert
   *
   * @return CohortDefinition
   */
  public CohortDefinition genExpert() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("genExpert()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch("genExpert", EptsReportUtils.map(getGenExpert(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND genExpert AND positive-screening");
    return definition;
  }

  /**
   * BR-10 Get patients who have a Basiloscopia Positivo or Negativo registered in the laboratory
   * form encounter type 13 Except patients identified in GeneXpert
   *
   * @return CohortDefinition
   */
  public CohortDefinition smearMicroscopyOnly() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("smearMicroscopyOnly()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch(
        "smearMicroscopyOnly",
        EptsReportUtils.map(getSmearMicroscopyOnly(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND smearMicroscopyOnly AND positive-screening");
    return definition;
  }

  /**
   * BR-11 Additional Test - Denominator AND Screened AND Additional AND NOT Genexpert AND NOT
   * Microscopy
   *
   * @return CohortDefinition
   */
  public CohortDefinition otherAdditionalTest() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("otherAdditionalTest()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch(
        "otherAdditionalTest", EptsReportUtils.map(getAdditionalTest(), generalParameterMapping));
    definition.addSearch(
        "positive-screening", EptsReportUtils.map(positiveScreening(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND otherAdditionalTest AND positive-screening");
    return definition;
  }

  /**
   * BR-12 Positive Results Returned
   *
   * @return CohortDefinition
   */
  public CohortDefinition positiveResultsReturned() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("positiveResultsReturned()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(getDenominator(), generalParameterMapping));
    definition.addSearch(
        "positiveResultsReturned",
        EptsReportUtils.map(getPositiveResultsReturned(), generalParameterMapping));
    addGeneralParameters(definition);
    definition.setCompositionString("denominator AND positiveResultsReturned");
    return definition;
  }

  /**
   * Get patients who sent specimen within date boundaries
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveSentSpecimen(
      EncounterType laboratory,
      Concept applicationForLaboratoryResearch,
      EncounterType fichaClinica,
      Concept basiloscopiaExam,
      Concept genexpertTest,
      Concept tbLamTest,
      Concept cultureTest,
      Concept positive,
      Concept negative) {

    CohortDefinition basiloscopiaExamCohort =
        getPatientsWithCodedObsBetweenDates(
            laboratory, basiloscopiaExam, Arrays.asList(negative, positive));
    CohortDefinition genexpertTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, genexpertTest, Arrays.asList(negative, positive));
    CohortDefinition tbLamTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, tbLamTest, Arrays.asList(negative, positive));
    CohortDefinition cultureTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, cultureTest, Arrays.asList(negative, positive));
    CohortDefinition applicationForLaboratoryResearchCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica,
            applicationForLaboratoryResearch,
            Arrays.asList(genexpertTest, cultureTest, tbLamTest));

    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("specimenSent()");
    addGeneralParameters(definition);

    definition.addSearch(
        "basiloscopiaExamCohort",
        EptsReportUtils.map(basiloscopiaExamCohort, generalParameterMapping));
    definition.addSearch(
        "genexpertTestCohort", EptsReportUtils.map(genexpertTestCohort, generalParameterMapping));
    definition.addSearch(
        "tbLamTestCohort", EptsReportUtils.map(tbLamTestCohort, generalParameterMapping));
    definition.addSearch(
        "cultureTestCohort", EptsReportUtils.map(cultureTestCohort, generalParameterMapping));
    definition.addSearch(
        "applicationForLaboratoryResearchCohort",
        EptsReportUtils.map(applicationForLaboratoryResearchCohort, generalParameterMapping));

    definition.setCompositionString(
        "basiloscopiaExamCohort OR genexpertTestCohort OR tbLamTestCohort OR cultureTestCohort OR applicationForLaboratoryResearchCohort");
    return definition;
  }

  /**
   * Get patients who have a GeneXpert Positivo or Negativo registered in the investigations - lab
   * results - ficha clinica - mastercard OR Get patients who have a GeneXpert request registered in
   * the investigations - lab results - ficha clinica - mastercard
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoHaveGeneXpert(
      Concept applicationForLaboratoryResearch,
      EncounterType fichaClinica,
      Concept genexpertTest,
      Concept positive,
      Concept negative) {

    CohortDefinition genexpertTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, genexpertTest, Arrays.asList(negative, positive));

    CohortDefinition applicationForLaboratoryResearchCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, applicationForLaboratoryResearch, Arrays.asList(genexpertTest));

    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("haveGeneXpert()");
    addGeneralParameters(definition);

    definition.addSearch(
        "genexpertTestCohort", EptsReportUtils.map(genexpertTestCohort, generalParameterMapping));
    definition.addSearch(
        "applicationForLaboratoryResearchCohort",
        EptsReportUtils.map(applicationForLaboratoryResearchCohort, generalParameterMapping));

    definition.setCompositionString(
        "genexpertTestCohort OR applicationForLaboratoryResearchCohort");
    return definition;
  }

  /**
   * Smear Microscopy - Get patients who have a Basiloscopia Positivo or Negativo registered in the
   * laboratory form encounter type 13 Except patients identified in GeneXpert
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSmearMicroscopyOnly(
      EncounterType laboratory, Concept basiloscopiaExam, Concept positive, Concept negative) {

    CohortDefinition basiloscopiaCohort =
        getPatientsWithCodedObsBetweenDates(
            laboratory, basiloscopiaExam, Arrays.asList(negative, positive));

    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("haveBasiloscopia()");
    addGeneralParameters(definition);

    definition.addSearch(
        "basiloscopiaCohort", EptsReportUtils.map(basiloscopiaCohort, generalParameterMapping));
    definition.addSearch(
        "genExpertCohort", EptsReportUtils.map(getGenExpert(), generalParameterMapping));

    definition.setCompositionString("basiloscopiaCohort NOT genExpertCohort");
    return definition;
  }

  /**
   * Get patients who have a Additional Test AND Not GeneXpert AND Not Smear Microscopy Only
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAdditionalTest(
      EncounterType fichaClinica,
      Concept tbLamTest,
      Concept cultureTest,
      Concept applicationForLaboratoryResearch,
      Concept positive,
      Concept negative) {

    CohortDefinition tbLamTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, tbLamTest, Arrays.asList(negative, positive));
    CohortDefinition cultureTestCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, cultureTest, Arrays.asList(negative, positive));
    CohortDefinition applicationForLaboratoryResearchCohort =
        getPatientsWithCodedObsBetweenDates(
            fichaClinica, applicationForLaboratoryResearch, Arrays.asList(cultureTest, tbLamTest));

    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("additionalTest()");
    addGeneralParameters(definition);

    definition.addSearch(
        "tbLamTestCohort", EptsReportUtils.map(tbLamTestCohort, generalParameterMapping));
    definition.addSearch(
        "cultureTestCohort", EptsReportUtils.map(cultureTestCohort, generalParameterMapping));
    definition.addSearch(
        "applicationForLaboratoryResearchCohort",
        EptsReportUtils.map(applicationForLaboratoryResearchCohort, generalParameterMapping));
    definition.addSearch(
        "genExpertCohort", EptsReportUtils.map(getGenExpert(), generalParameterMapping));
    definition.addSearch(
        "smearMicroscopyOnlyCohort",
        EptsReportUtils.map(getSmearMicroscopyOnly(), generalParameterMapping));

    definition.setCompositionString(
        "(tbLamTestCohort OR cultureTestCohort OR applicationForLaboratoryResearchCohort) NOT genExpertCohort NOT smearMicroscopyOnlyCohort");
    return definition;
  }

  public CohortDefinition getPatientsWithCodedObsBetweenDates(
      EncounterType encounterType, Concept question, List<Concept> answers) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName("Patients With Coded Obs Between Dates");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.setEncounterTypeList(Collections.singletonList(encounterType));
    cd.setTimeModifier(TimeModifier.ANY);
    cd.setQuestion(question);
    cd.setValueList(answers);
    cd.setOperator(SetComparator.IN);
    return cd;
  }
}
