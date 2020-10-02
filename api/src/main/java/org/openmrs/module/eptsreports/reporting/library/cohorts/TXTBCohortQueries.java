package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
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

  private void addCodedObsParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Dat", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Data Inicio De Tratamento De Tuberculose <b>(concept_id = 1113)</b>
   *
   * <p>Data notificada nas fichas de: Seguimento <b>(encounterType_id = 6 or 9)</b>, Rastreio E
   * Livro Tb.
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> Number of patients enrolled in TB program <b>(program_id = 5)</b>
   *
   * @return {@link CohortDefinition}
   */
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients with Pulmonary TB Date <b>(concept_id = 41)</b> and Positive Screening "YES"
   * <b>(concept_id = 1065)</b> in Patient Clinical Record of ART date TB
   *
   * <p>Registered Ficha Resumo Mastercard <b>(encounterType_id = 53)</b> during the reporting
   * period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Patients marked as “Tratamento TB = Inicio (I) - <b>Start Drugs</b>" in
   * (Ficha Clinica - Mastercard) Card
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients with Tuberculosis Symptoms <b>(concept_id = 23758)</b> registered in the Adult
   * follow-up <b>(encounter_id = 6)</b> with Answers:
   *
   * <ul>
   *   <li>YES <b>(concept_id = 1065)</b>
   *   <li>NO <b>(concept_id = 1066)</b>
   * </ul>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients with Tuberculosis Symptoms <b>(concept_id = 23758)</b> and Positive Screening "YES"
   * <b>(concept_id = 1065)</b>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients with Active Tuberculosis <b>(concept_id = 23761)</b> with Answer "YES" <b>(concept_id
   * = 1065)</b>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Patients with TB Observations <b>(concept_id = 1766)</b> in Answers =
   *
   * <ul>
   *   <li>Fever Lasting More Than 3 Weeks <b>(id = 1763)</b> OR
   *   <li>Weight Loss Of More Than 3 Kg In Last Month <b>(id = 1764)</b> OR
   *   <li>Nightsweats Lasting More Than 3 Weeks <b>(id = 1762)</b> OR
   *   <li>Cough Lasting More Than 3 Weeks <b>( id = 1760)</b> OR
   *   <li>Asthenia <b>(id = 23760)</b> OR
   *   <li>Cohabitant Being Treated For Tb <b>(id = 1765)</b> OR
   *   <li>Lymphadenopathy <b>(id = 161)</b>
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Application for Laboratory Research <b>(concept_id = 23722)</b> with Answers:
   *
   * <ul>
   *   <li>TB Genexpert Test <b>(concept_id = 23723)</b>
   *   <li>Culture Test <b>(concept_id = 23774)</b>
   *   <li>Test TB LAM <b>(concept_id = 23951)</b>
   * </ul>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getApplicationForLaboratoryResearch() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "applicationForLaboratoryResearch",
            TXTBQueries.applicationForLaboratoryResearch(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                tbMetadata.getTBGenexpertTestConcept().getConceptId(),
                tbMetadata.getCultureTest().getConceptId(),
                tbMetadata.getTestTBLAM().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * TB GeneExpertTest <b>(concept_id = 23723)</b> with Answer Positive <b>(concept_id = 703)</b> or
   * Negative <b>(id = 664)</b>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getTBGenexpertTestCohort() {
    CohortDefinition cd =
        genericCohortQueries.generalSql(
            "TBGenexpertTest",
            TXTBQueries.tbGenexpertTest(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.getTBGenexpertTestConcept().getConceptId(),
                commonMetadata.getPositive().getConceptId(),
                commonMetadata.getNegative().getConceptId()));
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Culture Test <b>(concept_id = 23774)</b> with Answer Positive <b>(concept_id = 703)</b> or
   * Negative <b>(id = 664)</b>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Test TB LAM <b>(concept_id = 237951)</b> with Answer Positive <b>(concept_id = 703)</b> or
   * Negative <b>(id = 664)</b>
   *
   * <p>Registered in the Adult follow-up <b>(encounterType_id = 6)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Result For Basiloscopia <b>(concept_id = 307)</b> with Answer Positive <b>(concept_id =
   * 703)</b> or Negative <b>(id = 664)</b>
   *
   * <p>Registered in Misau Laboratorio <b>(encounterType_id = 13)</b> during the reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Pacientes Com Rastreio De Tuberculose <b>(concept_id = 6257)</b> Negativo
   * <b>(concept_id = 1066)</b> in the follow-up (Adult and Children) codes: RASTREIOTBNEG
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Pacientes Com Rastreio De Tuberculose <b>(concept_id = 6257)</b> Positivo
   * <b>(concept_id = 1065)</b> in the follow-up (Adult and Children) codes: RASTREIOTBNEG
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Patients who started art <b>on</b> period considering the transferred in
   * for that same period
   *
   * <p>And patients who started art <b>before</b> period also considering the transferred in for
   * that same period
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition artList() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addSearch(
        "started-art-on-period-including-transferred-in",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtOnPeriod(true, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "started-art-before-startDate-including-transferred-in",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(true),
            "onOrBefore=${startDate-1d},location=${location}"));

    cd.setCompositionString(
        "started-art-on-period-including-transferred-in OR started-art-before-startDate-including-transferred-in");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Description:</b> Positive Investigation Research result <b>(concept_id = 6277)</b> Positivo
   * <b>(concept_id = 1065)</b> in the follow-up (Adult and Children)
   *
   * @return {@link CohortDefinition}
   */
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
   * <b>Description:</b> At least one “POS” selected for “Resultado da Investigação para TB de BK
   * e/ou RX?” during the reporting period consultations;
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * response 703: "POS" for question: 6277
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> At least one “S” or “N” selected for TB Screening (Rastreio de TB) during
   * the reporting period consultations
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * (response 1065: "YES" or 1066: "NO" for question 6257: "SCREENING FOR TB")
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> TX_TB Numerator A
   *
   * <p><b>Technical Specs</b>
   *
   * <p>Number of patients on TB Treatment, with Pulmonary TB Date, who initiated TB Treatment in
   * ART
   *
   * @return {@link CohortDefinition}
   */
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

    CohortDefinition patientswithPulmonaryTbDate =
        TXTBQueries.getPatientsWithObsBetweenDates(
            "Patients with Pulmonary TB Date",
            tbMetadata.getPulmonaryTB(),
            hivMetadata.getPatientFoundYesConcept(),
            Arrays.asList(hivMetadata.getMasterCardEncounterType()));
    cd.addSearch(
        "patientswithPulmonaryTbDate", map(patientswithPulmonaryTbDate, codedObsParameterMapping));

    CohortDefinition patientsWhoInitiatedTbTreatment =
        genericCohortQueries.generalSql(
            "patientsWhoInitiatedTbTreatment",
            TXTBQueries.getPatientsWithObsBetweenDates(
                hivMetadata.getTBTreatmentPlanConcept().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId())));
    addGeneralParameters(patientsWhoInitiatedTbTreatment);

    cd.addSearch(
        "patientsWhoInitiatedTbTreatment",
        map(patientsWhoInitiatedTbTreatment, generalParameterMapping));

    CohortDefinition artList = artList();
    cd.addSearch("artList", map(artList, generalParameterMapping));
    cd.setCompositionString(
        "(i OR ii OR patientswithPulmonaryTbDate OR patientsWhoInitiatedTbTreatment) AND artList");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Description:</b> TX_TB Numerator
   *
   * <p><b>Technical Specs</b> Exclusion for patients who started TB Treatment on previous period
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description: BR-6</b> Positive Screening
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition positiveScreening() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addSearch("A", mapStraightThrough(getPatientsWithAtLeastOneYesForTBScreening()));
    cd.addSearch("B", mapStraightThrough(getPatientsWithAtLeastPosInvestigationResultTB()));
    cd.addSearch("C", mapStraightThrough(getPatientsWithAtLeastNegInvestigationResultTB()));
    cd.addSearch("D", mapStraightThrough(getPatientsInTBProgramInThePreviousPeriod()));
    cd.addSearch("E", mapStraightThrough(getResultForBasiloscopia()));
    cd.addSearch("F", mapStraightThrough(getTBTreatmentStart()));
    cd.addSearch("G", mapStraightThrough(getPulmonaryTB()));
    cd.addSearch("H", mapStraightThrough(getPatientsWithAtLeastOneResponseForPositiveScreeningH()));
    cd.setCompositionString("A OR B OR C OR D OR E OR F OR G OR H");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Description:</b> All patients with at least one “POS” selected for “Resultado da
   * Investigação para TB de BK e/ou RX?” (Ficha de Seguimento) during reporting period
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithAtLeastPosInvestigationResultTB() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("Patients With At Least One Yes For TB Screening During the reporting  period");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("researchResultConcept", tbMetadata.getResearchResultConcept().getConceptId());
    map.put("positiveConcept", tbMetadata.getPositiveConcept().getConceptId());
    map.put("negativeConcept", tbMetadata.getNegativeConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON o.encounter_id = e.encounter_id "
            + "WHERE "
            + "    p.voided = 0 AND "
            + "    e.voided = 0 AND "
            + "    o.voided = 0 AND "
            + "    e.encounter_type IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType})  AND "
            + "    o.concept_id = ${researchResultConcept} AND "
            + "    o.value_coded = ${positiveConcept} AND "
            + "    e.encounter_datetime BETWEEN :startDate AND :endDate AND "
            + "    e.location_id  = :location "
            + "GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /**
   * <b>Description:</b> All patients with at least one “NEG” selected for “Resultado da
   * Investigação para TB de BK e/ou RX?” (Ficha de Seguimento) AND “N” selected for TB Screening
   * “Rastreio TB” in same encounter occurred during reporting period during reporting period
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithAtLeastNegInvestigationResultTB() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("Patients With At Least One Yes For TB Screening During the reporting  period");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("researchResultConcept", tbMetadata.getResearchResultConcept().getConceptId());
    map.put("tbScreening", tbMetadata.getTbScreeningConcept().getConceptId());
    map.put("negativeConcept", tbMetadata.getNegativeConcept().getConceptId());
    map.put("noConcept", commonMetadata.getNoConcept().getConceptId());

    String query =
        "SELECT patient_id FROM ( "
            + "SELECT p.patient_id, e.encounter_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON o.encounter_id = e.encounter_id "
            + "    INNER JOIN"
            + "    (SELECT p.patient_id, e.encounter_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON o.encounter_id = e.encounter_id "
            + "WHERE "
            + "    p.voided = 0 AND "
            + "    e.voided = 0 AND "
            + "    o.voided = 0 AND "
            + "    o.concept_id = ${tbScreening} AND "
            + "    o.value_coded = ${noConcept} "
            + ") as screening "
            + "ON e.encounter_id = screening.encounter_id "
            + "WHERE "
            + "    p.voided = 0 AND "
            + "    e.voided = 0 AND "
            + "    o.voided = 0 AND "
            + "    e.encounter_type IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType})  AND "
            + "    o.concept_id = ${researchResultConcept} AND "
            + "    o.value_coded = ${negativeConcept} AND "
            + "    e.encounter_datetime BETWEEN :startDate AND :endDate AND "
            + "    e.location_id  = :location "
            + "    GROUP BY p.patient_id) as list";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients with at least one “S” (Yes) <b>(concept_id =1065)</b> selected for TB Screening
   * “Rastreio TB <b>(concept_id = 6257)</b>” (Ficha de Seguimento Adult or Pediatric) during the
   * reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithAtLeastOneYesForTBScreening() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("Patients With At Least One Yes For TB Screening During the reporting  period");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("tbScreeningConcept", tbMetadata.getTbScreeningConcept().getConceptId());
    map.put("getYesConcept", commonMetadata.getYesConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON o.encounter_id = e.encounter_id "
            + "WHERE "
            + "    p.voided = 0 AND "
            + "    e.voided = 0 AND  "
            + "    o.voided = 0 AND "
            + "    e.encounter_type IN (${adultoSeguimentoEncounterType},${pediatriaSeguimentoEncounterType})  AND "
            + "    o.concept_id = ${tbScreeningConcept} AND "
            + "    o.value_coded = ${getYesConcept} AND "
            + "    e.encounter_datetime BETWEEN :startDate AND :endDate AND "
            + "    e.location_id  = :location "
            + "GROUP BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /**
   * <b>Description:</b> New On ART Positive Screening
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> New On ART Negative Screening
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Previously On ART Positive Screening
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Previously On ART Negative Screening
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Patients New On ART Numerator
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Patients Previously On ART Numerator
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Denominator
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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
        "tb-genexpert-test",
        EptsReportUtils.map(getTBGenexpertTestCohort(), generalParameterMapping));

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
            getPatientsInTBProgramInThePreviousPeriod(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));
    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            getPatientsTransferredOut(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    definition.setCompositionString(
        "(art-list AND (tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program OR pulmonary-tb OR marked-as-tb-treatment-start "
            + "OR (tuberculosis-symptomys OR active-tuberculosis OR tb-observations OR application-for-laboratory-research OR tb-genexpert-test OR culture-test "
            + "OR test-tb-lam) OR result-for-basiloscopia)) "
            + "NOT ((transferred-out NOT (started-tb-treatment OR in-tb-program)) OR started-tb-treatment-previous-period OR in-tb-program-previous-period)");

    return definition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All Transferred-out <b>(Patient_State.state = 7)</b> in ART Service Program
   * <b>(Patient_program.program_id = 2)</b>
   *
   * <p>(and had no registred drug pickup Mastercard Date<b>(concept_id = 23866)</b> From
   * <b>(EncounterType_id = 52)</b>) after the transferred Out date within reporting period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTransferredOut() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(
        "Patient Transferred Out With No Drug Pick After The Transferred out Date ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pharmaciaEncounterType", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("artDatePickup", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put(
        "stateOfStayOfPreArtPatient", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    map.put("transferredOutConcept", hivMetadata.getTransferredOutConcept().getConceptId());
    map.put("autoTransferConcept", hivMetadata.getAutoTransferConcept().getConceptId());
    map.put("stateOfStayOfArtPatient", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    map.put("defaultingMotiveConcept", hivMetadata.getDefaultingMotiveConcept().getConceptId());
    map.put(
        "buscaActivaEncounterType", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    map.put("artProgram", hivMetadata.getARTProgram().getProgramId());
    map.put(
        "transferredOutToAnotherHealthFacilityWorkflowState",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        "  SELECT mostrecent.patient_id "
            + "FROM ("
            + " SELECT lastest.patient_id ,Max(lastest.last_date) as  last_date "
            + " FROM (  "
            + "    SELECT p.patient_id , Max(ps.start_date) AS last_date  "
            + "    FROM patient p   "
            + "        INNER JOIN patient_program pg   "
            + "            ON p.patient_id=pg.patient_id   "
            + "        INNER JOIN patient_state ps   "
            + "            ON pg.patient_program_id=ps.patient_program_id   "
            + "    WHERE pg.voided=0   "
            + "        AND ps.voided=0   "
            + "        AND p.voided=0   "
            + "        AND pg.program_id= ${artProgram}  "
            + "        AND ps.state = ${transferredOutToAnotherHealthFacilityWorkflowState}   "
            + "        AND ps.end_date is null   "
            + "        AND ps.start_date BETWEEN :startDate AND :endDate    "
            + "        AND pg.location_id= :location   "
            + "    group by p.patient_id  "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT  p.patient_id,  Max(o.obs_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${masterCardEncounterType}   "
            + "        AND o.concept_id = ${stateOfStayOfPreArtPatient}  "
            + "        AND o.value_coded =  ${transferredOutConcept}   "
            + "        AND o.obs_datetime BETWEEN :startDate AND :endDate   "
            + "        AND e.location_id =  :location   "
            + "    GROUP BY p.patient_id  "
            + "    UNION   "
            + "    SELECT  p.patient_id , Max(e.encounter_datetime) AS last_date  "
            + "    FROM patient p    "
            + "        INNER JOIN encounter e   "
            + "            ON e.patient_id=p.patient_id   "
            + "        INNER JOIN obs o   "
            + "            ON o.encounter_id=e.encounter_id   "
            + "    WHERE  p.voided = 0   "
            + "        AND e.voided = 0   "
            + "        AND o.voided = 0   "
            + "        AND e.encounter_type = ${adultoSeguimentoEncounterType}  "
            + "        AND o.concept_id = ${stateOfStayOfArtPatient}  "
            + "        AND o.value_coded = ${transferredOutConcept}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate   "
            + "        AND e.location_id =  :location  "
            + "    GROUP BY p.patient_id   "
            + "  "
            + "    UNION  "
            + "  "
            + "    SELECT p.patient_id, Max(e.encounter_datetime) last_date   "
            + "    FROM patient p   "
            + "        INNER JOIN encounter e   "
            + "              ON p.patient_id = e.patient_id   "
            + "        INNER JOIN obs o   "
            + "              ON e.encounter_id = o.encounter_id   "
            + "    WHERE o.concept_id = ${defaultingMotiveConcept}  "
            + "    	   AND e.location_id = :location   "
            + "        AND e.encounter_type= ${buscaActivaEncounterType}   "
            + "        AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "		   AND o.value_coded IN (${transferredOutConcept} ,${autoTransferConcept})  "
            + "        AND e.voided=0   "
            + "        AND o.voided=0   "
            + "        AND p.voided=0   "
            + "    GROUP BY p.patient_id "
            + ") lastest   "
            + "WHERE lastest.patient_id NOT  IN("
            + " "
            + "  			     SELECT  p.patient_id    "
            + "	                 FROM patient p      "
            + "	                     INNER JOIN encounter e     "
            + "	                         ON e.patient_id=p.patient_id     "
            + "	                 WHERE  p.voided = 0     "
            + "	                     AND e.voided = 0     "
            + "	                     AND e.encounter_type IN (${adultoSeguimentoEncounterType},"
            + "${pediatriaSeguimentoEncounterType},"
            + "${pharmaciaEncounterType})    "
            + "	                     AND e.encounter_datetime > lastest.last_date "
            + " AND e.encounter_datetime <=  :endDate    "
            + "	                     AND e.location_id =  :location    "
            + "	                 GROUP BY p.patient_id "
            + " UNION "
            + "        			 SELECT  p.patient_id    "
            + "	                 FROM patient p       "
            + "	                      INNER JOIN encounter e      "
            + "	                          ON e.patient_id=p.patient_id      "
            + "	                      INNER JOIN obs o      "
            + "	                          ON o.encounter_id=e.encounter_id      "
            + "	                  WHERE  p.voided = 0      "
            + "	                      AND e.voided = 0      "
            + "	                      AND o.voided = 0      "
            + "	                      AND e.encounter_type = ${masterCardDrugPickupEncounterType}     "
            + "	                      AND o.concept_id = ${artDatePickup}     "
            + "	                      AND o.value_datetime > lastest.last_date  "
            + " AND o.value_datetime <= :endDate      "
            + "	                      AND e.location_id =  :location     "
            + "	                  GROUP BY p.patient_id   "
            + ")  "
            + " GROUP BY lastest.patient_id"
            + " )mostrecent "
            + " GROUP BY mostrecent.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * <b>Description:</b> Patients in TB Program<b>(p.program_id = 5)</b> in Previous Period
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsInTBProgramInThePreviousPeriod() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("in tb in the previeus period");

    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("tbProgram", tbMetadata.getTBProgram().getProgramId());
    String sql =
        " SELECT pg.patient_id "
            + " FROM patient p "
            + "    INNER JOIN patient_program pg "
            + "        ON p.patient_id=pg.patient_id "
            + " WHERE pg.voided=0 "
            + "    AND p.voided=0 "
            + "    AND program_id= ${tbProgram}"
            + "    AND date_enrolled "
            + "        BETWEEN :startDate AND :endDate "
            + "    AND location_id= :location "
            + " GROUP BY pg.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replacedStrinbg = sb.replace(sql).toString();

    cd.setQuery(replacedStrinbg);

    return cd;
  }

  /**
   * <b>Description:</b> New On ART
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Description:</b> Patients With At Least One Response For Positive ScreeningH
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  private CompositionCohortDefinition getPatientsWithAtLeastOneResponseForPositiveScreeningH() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addSearch(
        "tuberculosis-symptomys", mapStraightThrough(getTuberculosisSymptomsPositiveScreening()));
    cd.addSearch("active-tuberculosis", mapStraightThrough(getActiveTuberculosis()));
    cd.addSearch("tb-observations", mapStraightThrough(getTBObservation()));
    cd.addSearch(
        "application-for-laboratory-research",
        mapStraightThrough(getApplicationForLaboratoryResearch()));
    cd.addSearch("tb-genexpert-test", mapStraightThrough(getTBGenexpertTestCohort()));
    cd.addSearch("culture-test", mapStraightThrough(getCultureTest()));
    cd.addSearch("test-tb-lam", mapStraightThrough(getTestTBLAM()));
    cd.setCompositionString(
        "tuberculosis-symptomys OR active-tuberculosis OR tb-observations "
            + "OR application-for-laboratory-research OR tb-genexpert-test OR culture-test OR test-tb-lam");
    addGeneralParameters(cd);
    return cd;
  }

  /**
   * <b>Description:</b> Get patients who have specimen sent
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getSpecimenSent() {
    CohortDefinition cd =
        getPatientsWhoHaveSentSpecimen(
            hivMetadata.getMisauLaboratorioEncounterType(),
            hivMetadata.getApplicationForLaboratoryResearch(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getResultForBasiloscopia(),
            tbMetadata.getTBGenexpertTestConcept(),
            tbMetadata.getTestTBLAM(),
            tbMetadata.getCultureTest(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Get patients who have a GeneXpert Positivo or Negativo registered in the investigations - Ficha
   * Clinica - Mastercard OR
   *
   * <p>Have a GeneXpert request registered in the investigations - Ficha Clinica - Mastercard
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getGenExpert() {
    CohortDefinition cd =
        getPatientsWhoHaveGeneXpert(
            hivMetadata.getApplicationForLaboratoryResearch(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            tbMetadata.getTBGenexpertTestConcept(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * <b>Description:</b> Get patients who have a Basiloscopia And Not GeneXpert registered
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Get patients who have a Additional Test AND Not GeneXpert AND Not Smear
   * Microscopy Only
   *
   * <p><b>Technical Specs</b>
   *
   * @return {@link CohortDefinition}
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Get patients from denominator who have positive results returned registered during the period
   * Have a ‘GeneXpert Positivo’ registered in the
   *
   * <ul>
   *   <li>investigacoes – resultados laboratoriais - ficha clinica – mastercard OR
   *   <li>Have a ‘resultado baciloscopia positive’ registered in the laboratory form OR
   *   <li>Have a TB LAM positivo registered in the investigacoes – resultados laboratoriais ficha
   *       clinica – mastercard OR
   *   <li>Have a cultura positiva registered in the investigacoes – resultados laboratoriais ficha
   *       clinica – mastercard
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPositiveResultsReturned() {
    CohortDefinition cd =
        getPositiveResultsReturned(
            hivMetadata.getMisauLaboratorioEncounterType(),
            hivMetadata.getAdultoSeguimentoEncounterType(),
            hivMetadata.getResultForBasiloscopia(),
            tbMetadata.getTBGenexpertTestConcept(),
            tbMetadata.getTestTBLAM(),
            tbMetadata.getCultureTest(),
            commonMetadata.getPositive(),
            commonMetadata.getNegative());
    return cd;
  }

  /**
   * <b>Description:</b> BR-8 Specimen Sent - Get patients from denominator AND tb_screened AND
   * specimen_sent
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> BR-9 GenExpert MTB/RIF - Get patients from denominator AND
   * positive_screened AND genexpert
   *
   * @return {@link CohortDefinition}
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
   * <b>Description: BR-10 </b> Get patients who have a Basiloscopia Positivo or Negativo registered
   * in the laboratory form encounter type 13 Except patients identified in GeneXpert
   *
   * @return {@link CohortDefinition}
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
   * <b>Description: BR-11</b> Additional Test - Denominator AND Screened AND Additional AND NOT
   * Genexpert AND NOT Microscopy
   *
   * @return {@link CohortDefinition}
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
   * <b>Description: BR-12</b> Positive Results Returned All patients from denominator who have the
   * following requests/results registered during the period:
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Get patients who sent specimen within date boundaries
   *
   * @return {@link CohortDefinition}
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
        genericCohortQueries.generalSql(
            "basiloscopiaExamCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                laboratory, basiloscopiaExam, Arrays.asList(negative, positive)));
    addGeneralParameters(basiloscopiaExamCohort);

    CohortDefinition genexpertTestCohort =
        genericCohortQueries.generalSql(
            "genexpertTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, genexpertTest, Arrays.asList(negative, positive)));
    addGeneralParameters(genexpertTestCohort);

    CohortDefinition tbLamTestCohort =
        genericCohortQueries.generalSql(
            "tbLamTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, tbLamTest, Arrays.asList(negative, positive)));
    addGeneralParameters(tbLamTestCohort);

    CohortDefinition cultureTestCohort =
        genericCohortQueries.generalSql(
            "cultureTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, cultureTest, Arrays.asList(negative, positive)));
    addGeneralParameters(cultureTestCohort);

    CohortDefinition applicationForLaboratoryResearchCohort =
        genericCohortQueries.generalSql(
            "applicationForLaboratoryResearchCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica,
                applicationForLaboratoryResearch,
                Arrays.asList(genexpertTest, cultureTest, tbLamTest)));
    addGeneralParameters(applicationForLaboratoryResearchCohort);

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
   * <b>Description:</b> Get patients who have a GeneXpert Positivo or Negativo registered in the
   * investigations - lab results - ficha clinica - mastercard OR Get patients who have a GeneXpert
   * request registered in the investigations - lab results - ficha clinica - mastercard
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveGeneXpert(
      Concept applicationForLaboratoryResearch,
      EncounterType fichaClinica,
      Concept genexpertTest,
      Concept positive,
      Concept negative) {

    CohortDefinition genexpertTestCohort =
        genericCohortQueries.generalSql(
            "genexpertTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, genexpertTest, Arrays.asList(negative, positive)));
    addGeneralParameters(genexpertTestCohort);

    CohortDefinition applicationForLaboratoryResearchCohort =
        genericCohortQueries.generalSql(
            "applicationForLaboratoryResearchCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, applicationForLaboratoryResearch, Arrays.asList(genexpertTest)));
    addGeneralParameters(applicationForLaboratoryResearchCohort);

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
   * <b>Description:</b> Smear Microscopy - Get patients who have a Basiloscopia Positivo or
   * Negativo registered in the laboratory form encounter type 13 Except patients identified in
   * GeneXpert
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getSmearMicroscopyOnly(
      EncounterType laboratory, Concept basiloscopiaExam, Concept positive, Concept negative) {

    CohortDefinition basiloscopiaCohort =
        genericCohortQueries.generalSql(
            "basiloscopiaCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                laboratory, basiloscopiaExam, Arrays.asList(negative, positive)));
    addGeneralParameters(basiloscopiaCohort);

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
   * <b>Description:</b> Get patients who have a Additional Test AND Not GeneXpert AND Not Smear
   * Microscopy Only
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getAdditionalTest(
      EncounterType fichaClinica,
      Concept tbLamTest,
      Concept cultureTest,
      Concept applicationForLaboratoryResearch,
      Concept positive,
      Concept negative) {

    CohortDefinition tbLamTestCohort =
        genericCohortQueries.generalSql(
            "tbLamTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, tbLamTest, Arrays.asList(negative, positive)));
    addGeneralParameters(tbLamTestCohort);

    CohortDefinition cultureTestCohort =
        genericCohortQueries.generalSql(
            "cultureTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, cultureTest, Arrays.asList(negative, positive)));
    addGeneralParameters(cultureTestCohort);

    CohortDefinition applicationForLaboratoryResearchCohort =
        genericCohortQueries.generalSql(
            "applicationForLaboratoryResearchCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica,
                applicationForLaboratoryResearch,
                Arrays.asList(cultureTest, tbLamTest)));
    addGeneralParameters(cultureTestCohort);

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

  /**
   * <b>Description:</b> Get patients who have positive results returned
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPositiveResultsReturned(
      EncounterType laboratory,
      EncounterType fichaClinica,
      Concept basiloscopiaExam,
      Concept genexpertTest,
      Concept tbLamTest,
      Concept cultureTest,
      Concept positive,
      Concept negative) {

    CohortDefinition genexpertTestCohort =
        genericCohortQueries.generalSql(
            "genexpertTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, genexpertTest, Arrays.asList(positive)));
    addGeneralParameters(genexpertTestCohort);

    CohortDefinition basiloscopiaExamCohort =
        genericCohortQueries.generalSql(
            "basiloscopiaExamCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                laboratory, basiloscopiaExam, Arrays.asList(positive)));
    addGeneralParameters(basiloscopiaExamCohort);

    CohortDefinition tbLamTestCohort =
        genericCohortQueries.generalSql(
            "tbLamTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, tbLamTest, Arrays.asList(positive)));
    addGeneralParameters(tbLamTestCohort);

    CohortDefinition cultureTestCohort =
        genericCohortQueries.generalSql(
            "cultureTestCohort",
            genericCohortQueries.getPatientsWithObsBetweenDates(
                fichaClinica, cultureTest, Arrays.asList(positive)));
    addGeneralParameters(cultureTestCohort);

    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("positiveResultsReturned()");
    addGeneralParameters(definition);

    definition.addSearch(
        "genexpertTestCohort", EptsReportUtils.map(genexpertTestCohort, generalParameterMapping));
    definition.addSearch(
        "basiloscopiaExamCohort",
        EptsReportUtils.map(basiloscopiaExamCohort, generalParameterMapping));
    definition.addSearch(
        "tbLamTestCohort", EptsReportUtils.map(tbLamTestCohort, generalParameterMapping));
    definition.addSearch(
        "cultureTestCohort", EptsReportUtils.map(cultureTestCohort, generalParameterMapping));

    definition.setCompositionString(
        "genexpertTestCohort OR basiloscopiaExamCohort OR tbLamTestCohort OR cultureTestCohort");
    return definition;
  }
}
