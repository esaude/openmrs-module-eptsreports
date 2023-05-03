package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.tpt.CompletedIsoniazidTPTCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TPTCompletionQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TPTEligiblePatientsQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TbPrevQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.queries.UnionBuilder;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTCompletionCohortQueries {

  private HivMetadata hivMetadata;

  private TbMetadata tbMetadata;

  private TbPrevCohortQueries tbPrevCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  private TXTBCohortQueries txTbCohortQueries;

  private GenericCohortQueries genericCohortQueries;

  private TPTEligiblePatientListCohortQueries tptEligiblePatientListCohortQueries;

  private final TPTInitiationCohortQueries tptInitiationCohortQueries;

  @Autowired private TbPrevQueries tbPrevQueries;

  @Autowired
  public TPTCompletionCohortQueries(
      HivMetadata hivMetadata,
      TbMetadata tbMetadata,
      TbPrevCohortQueries tbPrevCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      TXTBCohortQueries txTbCohortQueries,
      GenericCohortQueries genericCohortQueries,
      TPTEligiblePatientListCohortQueries tptEligiblePatientListCohortQueries,
      TPTInitiationCohortQueries tptInitiationCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
    this.tbPrevCohortQueries = tbPrevCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.txTbCohortQueries = txTbCohortQueries;
    this.genericCohortQueries = genericCohortQueries;
    this.tptEligiblePatientListCohortQueries = tptEligiblePatientListCohortQueries;
    this.tptInitiationCohortQueries = tptInitiationCohortQueries;
  }

  private final String mapping = "endDate=${endDate},location=${location}";
  private final String mapping2 = "onOrBefore=${endDate},location=${location}";
  private final String mapping3 = "startDate=${startDate},endDate=${endDate},location=${location}";

  public CohortDefinition getPatientsThatCompletedProphylaticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidTPTCalculation.class).get(0));
    cd.setName("Patients that completed prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   *
   *
   * <h4>TX_CURR with TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR with TPT Completion");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "txcurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true),
            "onOrBefore=${endDate},location=${location},locations=${location}"));

    CohortDefinition A1Inh =
        getINHStartA2Part2(
            Collections.singletonList(
                hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
            tbMetadata.getRegimeTPTConcept().getConceptId(),
            tbMetadata.getIsoniazidConcept().getConceptId(),
            tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId());

    CohortDefinition A2Inh =
        getINHStartA2Part2(
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId()),
            tbMetadata.getRegimeTPTConcept().getConceptId(),
            tbMetadata.getIsoniazidConcept().getConceptId(),
            tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId());

    compositionCohortDefinition.addSearch("A1", EptsReportUtils.map(A1Inh, mapping));

    compositionCohortDefinition.addSearch("A2", EptsReportUtils.map(A2Inh, mapping));

    compositionCohortDefinition.addSearch("A3", EptsReportUtils.map(getINHStartA6(), mapping));

    compositionCohortDefinition.addSearch("A4", EptsReportUtils.map(getINHStartA4(), mapping));

    compositionCohortDefinition.addSearch("B1B", EptsReportUtils.map(getIPTB1part2(), mapping));

    compositionCohortDefinition.addSearch("B2", EptsReportUtils.map(getIPTB2(), mapping));

    compositionCohortDefinition.addSearch(
        "B5Part1",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.getIPTB5Part1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                hivMetadata.getYesConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "B5Part2",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.getIPTB5Part2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.getDtINHConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "B5Part3",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.getIPTB5Part3(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getContinueRegimenConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId(),
                tbMetadata.getDtINHConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "B6Part1",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.getIPTB6Part1(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "B6Part2",
        EptsReportUtils.map(
            getIPTB6Part2(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "B6Part3",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.getIPTB6Part3(
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.getIsoniazidConcept().getConceptId(),
                tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch("C1", EptsReportUtils.map(get3HPStartC1(), mapping));

    compositionCohortDefinition.addSearch("C2", EptsReportUtils.map(get3HPStartC2(), mapping));

    compositionCohortDefinition.addSearch("C3", EptsReportUtils.map(get3HPStartC3(), mapping));

    compositionCohortDefinition.addSearch("C4", EptsReportUtils.map(get3HPStartC4(), mapping));

    compositionCohortDefinition.addSearch("C5", EptsReportUtils.map(getINHStartC5Query(), mapping));

    compositionCohortDefinition.addSearch(
        "D1",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.get3HPLastProfilaxyDuringM3orM1PeriodsComposition(),
            mapping));

    compositionCohortDefinition.addSearch(
        "D2",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.get3HPC1(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "D3",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.get3HPC1part2(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getTreatmentPrescribedConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "D4",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.get3HPC2(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.addSearch(
        "D5",
        EptsReportUtils.map(
            tptEligiblePatientListCohortQueries.get3HPC3(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.setCompositionString(
        "txcurr AND (((A1 OR A2 OR A3 OR A4) AND (B1B OR B2 OR (B5Part1 OR B5Part2 OR B5Part3) OR (B6Part1 OR B6Part2 OR B6Part3))) OR ((C1 OR C2 OR C3 OR C4 OR C5) AND (D1 OR D2 OR D3 OR D4)))");

    return compositionCohortDefinition;
  }

  public CohortDefinition getTbPrevDenominatorForTPTCompletion() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "A",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));

    definition.addSearch(
        "B",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPatientsTransferredOut(),
            "startDate=${startDate-6m},endDate=${endDate},location=${location}"));
    definition.addSearch(
        "C",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    definition.addSearch(
        "D",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPatientsStartedTpt(),
            "startDate=${startDate+6m},endDate=${endDate+6m},location=${location}"));
    definition.setCompositionString("A AND D AND NOT (B AND NOT C)");

    return definition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR without TPT Completion");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "txcurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort("txCurr", true),
            "onOrBefore=${endDate},location=${location},locations=${location}"));

    compositionCohortDefinition.addSearch(
        "tpt0", EptsReportUtils.map(getTxCurrWithTPTCompletion(), mapping));

    compositionCohortDefinition.setCompositionString("txcurr AND NOT tpt0");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion with TB Treatment</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletionWithTB() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR without TPT Completion with TB Treatment");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "end Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "startDate=${endDate-1095d},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "E", EptsReportUtils.map(txTbCohortQueries.txTbNumerator(), generalParameterMapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND E");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR without TPT Completion with Positive TB Screening</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithoutTPTCompletionWithPositiveTBScreening() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName(
        "TX_CURR without TPT Completion with Positive TB Screening");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "startDate=${endDate-14d},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "Denominator",
        EptsReportUtils.map(txTbCohortQueries.getDenominator(), generalParameterMapping));

    compositionCohortDefinition.addSearch(
        "PositiveScreening",
        EptsReportUtils.map(txTbCohortQueries.positiveScreening(), generalParameterMapping));

    // F = Dnominator AND Positive Screening

    compositionCohortDefinition.setCompositionString(
        "tpt1 AND (Denominator AND PositiveScreening)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR eligible for TPT Completion</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrEligibleForTPTCompletion() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR eligible for TPT Completion");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt2", EptsReportUtils.map(getTxCurrWithoutTPTCompletionWithTB(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt3",
        EptsReportUtils.map(getTxCurrWithoutTPTCompletionWithPositiveTBScreening(), mapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND NOT (tpt2 OR tpt3)");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR with TPT in last 7 months</h4>
   *
   * <ul>
   *   <li>From all patients currently receiving ART who do not have a documented TPT Completion
   *       (TPT_FR8), the system will identify those who have initiated TPT in the past 7 months as
   *       follows: Patients who have Initiated TPT (TB_PREV - Denominator) in a 7-month period
   *       before the end date.
   *       <p>
   *       <p>For the 7-month period:
   *       <p>Start Date = Selected End Date – 210 days
   *       <p>End Date = Selected End Date
   *       <p>
   *   <li>For the complete requirements definition to identify patients who initiated TPT therapy
   *       please refer to the TB_PREV Indicator Requirements and Specification (Denominator –
   *       TB_PREV_FR2), using the 7-month period start and end date instead of the previous
   *       reporting period.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrWithTPTInLast7Months() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR with TPT in last 7 months");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String generalParameterMapping =
        "startDate=${endDate-7m},endDate=${endDate},location=${location}";

    compositionCohortDefinition.addSearch(
        "tpt1", EptsReportUtils.map(getTxCurrWithoutTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "G", EptsReportUtils.map(getTbPrevDenominatorForTPTCompletion(), generalParameterMapping));

    compositionCohortDefinition.setCompositionString("tpt1 AND G");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>TX_CURR eligible for TPT Initiation</h4>
   *
   * <ul>
   *   <li>
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTxCurrEligibleForTPTInitiation() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("TX_CURR eligible for TPT Initiation");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "tpt4", EptsReportUtils.map(getTxCurrEligibleForTPTCompletion(), mapping));

    compositionCohortDefinition.addSearch(
        "tpt5", EptsReportUtils.map(getTxCurrWithTPTInLast7Months(), mapping));

    compositionCohortDefinition.setCompositionString("tpt4 AND NOT tpt5");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>B - INH final and D - 3HP Final
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsThatCompletedIsoniazidProphylacticTreatment() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            Context.getRegisteredComponents(CompletedIsoniazidTPTCalculation.class).get(0));
    cd.setName("Patients that completed Isoniazid prophylatic treatment");
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    return cd;
  }

  /**
   * <b> Patients who initiated IPT <b>
   *
   * <blockquote>
   *
   * <p>Patients who have Regime de TPT with the values (“Isoniazida” or “Isoniazida + Piridoxina”)
   * and “Seguimento de Tratamento TPT” with values “Continua” or no value marked on the first pick-
   * up on Ficha de Levantamento de TPT (FILT) during the reporting period as FILT INH Start Date
   * and:
   *
   * <ul>
   *   <li>No other INH values [Regime de TPT” (concept id 23985) value coded ‘Isoniazid’ or
   *       ‘Isoniazid + piridoxina’ (concept id in [656, 23982])] marked on FILT in the 7 months
   *       prior to ‘INH Start Date’; and
   *   <li>No other INH Start Dates marked on Ficha resumo (Encounter Type ID 53) Última profilaxia
   *       TPT with value “Isoniazida (INH)” (concept id 23985 value 656) and Data Inicio (obs
   *       datetime for concept 165308 value 1256) selected in the 7 months prior to this FILT INH
   *       Start Date and
   *   <li>No other INH Start Dates marked on Ficha Clinica (Encounter Type ID 6) (Profilaxia TPT
   *       with the value “Isoniazida (INH)” (concept id 23985 value 656) and Estado da Profilaxia
   *       with the value “Inicio (I)” (concept 165308 value 1256) in the 7 months prior to this
   *       FILT INH Start Date and; and
   *   <li>No other INH Start Dates marked on Ficha de Seguimento (Encounter Type ID 9) Profilaxia
   *       TPT with the value “Isoniazida (INH)” (concept id 23985 value 656) and Data Início (obs
   *       datetime for concept 165308 value 1256) in the 7 months prior to this FILT INH Start Date
   * </ul>
   *
   * </blockquote>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getINHStartA4() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("TPT Completion A4");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    valuesMap.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    valuesMap.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    valuesMap.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    valuesMap.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    valuesMap.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());
    valuesMap.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());

    EptsQueriesUtil patientBuilder = new EptsQueriesUtil();

    String query =
        patientBuilder.patientIdQueryBuilder(TPTCompletionQueries.getInhStartOnFilt()).getQuery();

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>Select all patients with Última profilaxia(concept id 23985) value coded 3HP(concept id
   *       23954) Data Início da Profilaxia TPT(value datetime, concept id 6128) registered before
   *       end date on Ficha Resumo (Encounter type 53)and before end date.
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA1ForAnyEncounterType(
      List<Integer> encounterTypes,
      int regimeTPTConcept,
      int threeHPConceptConcept,
      int dataEstadoDaProfilaxiaConcept,
      List<Integer> states) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Profilaxia INH");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("23985", String.valueOf(regimeTPTConcept));
    map.put("23954", String.valueOf(threeHPConceptConcept));
    map.put("165308", String.valueOf(dataEstadoDaProfilaxiaConcept));
    map.put("encounterTypes", StringUtils.join(encounterTypes, ","));
    map.put("states", StringUtils.join(states, ","));

    String query =
        "  SELECT"
            + "  p.patient_id"
            + "  FROM"
            + "  patient p"
            + "     INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "     INNER JOIN"
            + "  obs o ON e.encounter_id = o.encounter_id"
            + "  INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + " AND e.encounter_type = ${encounterTypes}"
            + " AND (o.concept_id = ${23985}"
            + " AND o.value_coded = ${23954})"
            + " AND (o2.concept_id = ${165308} AND o2.value_coded IN (${states})) "
            + " AND o2.obs_datetime < :endDate"
            + " AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A -A1.2: Select all patients with Ficha Resumo (encounter type 53) with Última
   *       profilaxia(concept id 23985) value coded INH(concept id 656)
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA1Part2A(
      int masterCardEncounterType, int regimeTPTConcept, int isoniazidConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", masterCardEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);

    String query =
        "  SELECT"
            + "  p.patient_id"
            + "  FROM"
            + "  patient p"
            + "     INNER JOIN"
            + "  encounter e ON p.patient_id = e.patient_id"
            + "     INNER JOIN"
            + "  obs o ON e.encounter_id = o.encounter_id"
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + " AND e.encounter_type = ${53}"
            + " AND o.concept_id = ${23985}"
            + " AND o.value_coded = ${656}"
            + " AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A2: Select all patients Profilaxia TPT (concept id 23985) value coded INH (concept id
   *       656) and Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256)
   *       and encounter datetime before end date
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA2Part2(
      List<Integer> encounterTypes,
      int regimeTPTConcept,
      int isoniazidConcept,
      int dataEstadoDaProfilaxiaConcept,
      int startDrugsConcept) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Profilaxia TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, String> map = new HashMap<>();
    map.put("23985", String.valueOf(regimeTPTConcept));
    map.put("656", String.valueOf(isoniazidConcept));
    map.put("165308", String.valueOf(dataEstadoDaProfilaxiaConcept));
    map.put("1256", String.valueOf(startDrugsConcept));
    map.put("encounterTypes", StringUtils.join(encounterTypes, ","));

    String query =
        " SELECT"
            + " p.patient_id"
            + " FROM"
            + " patient p"
            + " INNER JOIN"
            + " encounter e ON p.patient_id = e.patient_id"
            + " INNER JOIN"
            + " obs o ON e.encounter_id = o.encounter_id"
            + " INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + " WHERE"
            + " p.voided = 0 AND e.voided = 0 AND o.voided = 0"
            + "    AND e.encounter_type IN (${encounterTypes})"
            + " AND (o.concept_id = ${23985} AND o.value_coded = ${656})   "
            + " AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256}   "
            + "    AND o2.obs_datetime < :endDate) "
            + "    AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A5: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982])
   *       and encounter datetime before the reporting period
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA5(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int seguimentoTPTConcept,
      int continuaConcept,
      int dataInicioConcept,
      int fichaResumoEncounterType,
      int profilaxiaConcept,
      int inicioConcept,
      int fichaClinicaEncounterType,
      int adultoseguimentoEncounterType) {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Regime de TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23987", seguimentoTPTConcept);
    map.put("1257", continuaConcept);
    map.put("6128", dataInicioConcept);
    map.put("53", fichaResumoEncounterType);
    map.put("6122", profilaxiaConcept);
    map.put("1256", inicioConcept);
    map.put("6", fichaClinicaEncounterType);
    map.put("9", adultoseguimentoEncounterType);

    String query =
        " SELECT p.patient_id FROM patient p    "
            + " INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + " WHERE e.encounter_type = ${60}    "
            + " AND (o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982}))   "
            + " AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1257} , null))   "
            + " AND e.location_id = :location   "
            + " AND e.encounter_datetime < :endDate   "
            + " AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + " AND p.patient_id NOT IN (   "
            + "    SELECT p.patient_id FROM patient p    "
            + "    INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "    INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "    INNER JOIN (   "
            + "        SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "        INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + "        WHERE e.encounter_type = ${60}    "
            + "        AND (o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982}))   "
            + "        AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1257} , null))   "
            + "        AND e.location_id = :location   "
            + "        AND e.encounter_datetime < :endDate   "
            + "        AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "    ) AS filt    "
            + "    ON filt.patient_id = p.patient_id   "
            + "    WHERE e.encounter_type = ${60}    "
            + "    AND o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982})   "
            + "    AND e.location_id = :location   "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(filt.encounter_datetime, INTERVAL 7 MONTH) AND filt.encounter_datetime   "
            + "    AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + " )   "
            + " AND p.patient_id NOT IN (   "
            + "    SELECT p.patient_id FROM patient p    "
            + "    INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "    INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "    INNER JOIN (   "
            + "        SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "        INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + "        WHERE e.encounter_type = ${60}    "
            + "        AND (o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982}))   "
            + "        AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1257} , null))           "
            + "        AND e.location_id = :location   "
            + "        AND e.encounter_datetime < :endDate   "
            + "        AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "    ) AS fichaResumo    "
            + "    ON fichaResumo.patient_id = p.patient_id   "
            + "    WHERE e.encounter_type = ${53}   "
            + "    AND o.concept_id = ${6128}    "
            + "    AND e.location_id = :location   "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(fichaResumo.encounter_datetime, INTERVAL 7 MONTH) AND fichaResumo.encounter_datetime   "
            + "    AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + " )   "
            + " AND p.patient_id NOT IN (   "
            + "    SELECT p.patient_id FROM patient p    "
            + "    INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "    INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "    INNER JOIN (   "
            + "        SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "        INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + "        WHERE e.encounter_type = ${60}    "
            + "        AND (o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982}))   "
            + "        AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1257} , null))           "
            + "        AND e.location_id = :location   "
            + "        AND e.encounter_datetime < :endDate   "
            + "        AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "    ) AS fichaClinica    "
            + "    ON fichaClinica.patient_id = p.patient_id   "
            + "    WHERE e.encounter_type = ${6}   "
            + "    AND o.concept_id = ${6122}    "
            + "    AND o.value_coded = ${1256}   "
            + "    AND e.location_id = :location   "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(fichaClinica.encounter_datetime, INTERVAL 7 MONTH) AND fichaClinica.encounter_datetime   "
            + "    AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + " )   "
            + " AND p.patient_id NOT IN (   "
            + "    SELECT p.patient_id FROM patient p    "
            + "    INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "    INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "    INNER JOIN (   "
            + "        SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "        INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "        INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "        INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + "        WHERE e.encounter_type = ${60}    "
            + "        AND (o.concept_id = ${23985} AND o.value_coded IN(${656}, ${23982}))   "
            + "        AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1257} , null))           "
            + "        AND e.location_id = :location   "
            + "        AND e.encounter_datetime < :endDate   "
            + "        AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "    ) AS seguimento    "
            + "    ON seguimento.patient_id = p.patient_id   "
            + "    WHERE e.encounter_type IN(${6},${9})   "
            + "    AND o.concept_id = ${6128}    "
            + "    AND e.location_id = :location   "
            + "    AND e.encounter_datetime BETWEEN DATE_SUB(seguimento.encounter_datetime, INTERVAL 7 MONTH) AND seguimento.encounter_datetime   "
            + "    AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + " ) ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>A6: Select all patients with “Regime de TPT” (concept id 23985) value coded ‘Isoniazid’
   *       or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Seguimento de tratamento
   *       TPT”(concept ID 23987) value coded “inicio” or “re-inicio” (concept ID in [1256, 1705])
   *       marked on FILT (encounter type 60) and encounter datetime before end date Note: RegimeTPT
   *       and Seguimento de Tratamento TPT should be on the same encounter.
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartA6() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("all patients with Regime de TPT and Seguimento de tratamento TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT "
            + "  p.patient_id "
            + "  FROM "
            + "  patient p "
            + "  INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "  INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "  INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id  "
            + "  WHERE "
            + "  p.voided = 0  "
            + "  AND e.voided = 0  "
            + "  AND o.voided = 0 "
            + "  AND e.encounter_type = ${60} "
            + "  AND (o.concept_id = ${23985} AND o.value_coded IN (${656} , ${23982})) "
            + "  AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256} , ${1705})) "
            + "  AND o2.obs_datetime <= :endDate  "
            + "  AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Patients who initiated 3HP therapy </b> <
   *
   * <ul>
   *   <li>Patients who have Outras Prescrições with the value “DT-3HP” marked on Ficha Clínica –
   *       Mastercard (3HP Start Date) or
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC1() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica Master Card ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    valuesMap.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${6} "
            + "       AND o.concept_id = ${1719} "
            + "       AND o.value_coded = ${165307} "
            + "       AND e.encounter_datetime < :endDate ";

    StringSubstitutor sb = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C2: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or “3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       “Seguimento de tratamento TPT”(concept ID 23987) value coded “inicio” or
   *       “re-inicio”(concept ID in [1256, 1705]) and obs datetime before end date; Note: RegimeTPT
   *       and Seguimento de Tratamento TPT should be on the same encounter.
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with FILT ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        " SELECT  p.patient_id FROM patient p   "
            + "INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id   "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "WHERE   "
            + "  e.encounter_type = ${60} AND p.voided = 0   "
            + "  AND e.voided = 0   "
            + "  AND o.voided = 0   "
            + "  AND ( o.concept_id = ${23985} AND o.value_coded in (${23954},${23984}) )  "
            + "  AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256} , ${1705}))  "
            + "  AND e.location_id = :location  "
            + "  AND o2.obs_datetime < :endDate ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C3: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       encounter_datetime before end date as “3HP Start Date” no other 3HP prescriptions
   *       [“Outras prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id
   *       23954)] marked on Ficha-Clínica in the 4 months prior to the 3HP Start Date and no
   *       “Regime de TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept
   *       id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the 3HP
   *       Start Date;
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC3() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName(" all patients with Ficha Clinica Master Card ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        "SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN ( SELECT p.patient_id, min(o2.obs_datetime) AS start_date "
            + "                 FROM patient p "
            + "                     INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                     INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                 WHERE p.voided = 0 "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND o2.voided = 0 "
            + "                 AND e.encounter_type = ${60} "
            + "                 AND e.location_id = :location "
            + "                 AND ((o.concept_id = ${23985} AND o.value_coded IN (${23954}, ${23984})) "
            + "                 AND ((o2.concept_id = ${23987} AND (o2.value_coded IN (${1257}, ${1267}) "
            + "                       OR o2.value_coded IS NULL)) "
            + "                 AND o2.obs_datetime < :endDate)) "
            + "                 GROUP BY p.patient_id) AS filt ON p.patient_id = filt.patient_id "
            + "    AND NOT EXISTS ( SELECT pp.patient_id, e.encounter_datetime "
            + "                     FROM patient pp "
            + "                         INNER JOIN encounter e ON pp.patient_id = e.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     WHERE pp.voided = 0 "
            + "                     AND e.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND pp.patient_id = p.patient_id "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${60} "
            + "                     AND o.concept_id = ${23985} AND o.value_coded IN ( ${23954}, ${23984} ) "
            + "                     AND e.encounter_datetime >= date_sub(filt.start_date, INTERVAL 4 month) "
            + "                     AND e.encounter_datetime < filt.start_date) "
            + "    AND NOT EXISTS ( SELECT pp.patient_id "
            + "                     FROM patient pp "
            + "                         INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                         INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "                     WHERE pp.voided = 0 "
            + "                     AND pp.patient_id = p.patient_id "
            + "                     AND e.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND o2.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type IN (${6}, ${53}) "
            + "                     AND ( (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "                       AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256} "
            + "                            AND o2.obs_datetime >= date_sub(filt.start_date, INTERVAL 4 month) "
            + "                            AND o2.obs_datetime < filt.start_date) ) ) "
            + "    AND NOT EXISTS ( SELECT pp.patient_id "
            + "                     FROM patient pp "
            + "                         INNER JOIN encounter e ON e.patient_id = pp.patient_id "
            + "                         INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                     WHERE pp.voided = 0 "
            + "                     AND pp.patient_id = p.patient_id "
            + "                     AND e.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND o.concept_id = ${1719} "
            + "                     AND o.value_coded = ${165307} "
            + "                     AND e.encounter_datetime >= date_sub(filt.start_date, INTERVAL 4 month) "
            + "                     AND e.encounter_datetime < filt.start_date)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <h4>TPT_INI_FR4 : Patients who initiated 3HP therapy</h4>
   *
   * <ul>
   *   <li>Patients who have Última Profilaxia TPT with value “3HP” and Data Inicio selected in
   *       Ficha Resumo - Mastercard (3HP Start Date) during the reporting period or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC4() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("3HP Start C4");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "C4",
        EptsReportUtils.map(
            getINHStartA1ForAnyEncounterType(
                Collections.singletonList(
                    hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId(),
                Collections.singletonList(hivMetadata.getStartDrugs().getConceptId())),
            mapping));

    compositionCohortDefinition.setCompositionString("C4");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>Patients who have Profilaxia TPT with the value “3HP” and Estado da Profilaxia with the
   *       value “Inicio (I)” marked on Ficha Clínica – Mastercard (3HP Start Date) during the
   *       reporting period or
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartC5Query() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("C5 Query: all patients with Profilaxia TPT");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "C5",
        EptsReportUtils.map(
            getINHStartA1ForAnyEncounterType(
                Collections.singletonList(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId(),
                Collections.singletonList(hivMetadata.getStartDrugs().getConceptId())),
            mapping));

    compositionCohortDefinition.setCompositionString("C5");

    return compositionCohortDefinition;
  }

  /**
   *
   *
   * <h4>User_Story_ TPT</h4>
   *
   * <ul>
   *   <li>C4: Select all patients with “Regime de TPT” (concept id 23985) with value coded “3HP” or
   *       ” 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento
   *       TPT”(concept ID 23987) value coded “continua” or “fim” or no value(concept ID in [1257,
   *       1267, null]) marked on the first FILT (encounter type 60) and encounter datetime before
   *       end date and: No other Regime de TPT (concept id 23985) value coded “3HP” or ”
   *       3HP+Piridoxina” (concept id in [23954, 23984]) marked on FILT (encounter type 60) in the
   *       4 months prior to the FILT 3HP start date. ; and No other 3HP start dates marked on Ficha
   *       clinica (encounter type 6, encounter datetime) with Profilaxia TPT (concept id 23985)
   *       value coded 3HP (concept id 23954) and Estado da Profilaxia (concept id 165308) value
   *       coded Início (concept id 1256) or Outras prescrições (concept id 1719) value coded 3HP or
   *       DT-3HP (concept id in [23954,165307])in the 4 months prior to the FILT 3HP start date. ;
   *       and No other 3HP start dates marked on Ficha Resumo (encounter type 53) with Última
   *       profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início da
   *       Profilaxia TPT(value datetime, concept id 6128) in the 4 months prior to the FILT 3HP
   *       start date. ;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPStartC7() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("3HP Start C4");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "C4Part1", EptsReportUtils.map(getINHStartC7Part1Query(), mapping));

    compositionCohortDefinition.addSearch(
        "C4Part2", EptsReportUtils.map(getINHStartC7Part2Query(), mapping));

    compositionCohortDefinition.addSearch(
        "C4Part3", EptsReportUtils.map(getINHStartC7Part3Query(), mapping));

    compositionCohortDefinition.addSearch(
        "C4Part4", EptsReportUtils.map(getINHStartC7Part4Query(), mapping));

    compositionCohortDefinition.setCompositionString("C4Part1 AND C4Part2 AND C4Part3 AND C4Part4");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C7 Part 1: Select all patients with “Regime de TPT” (concept id 23985) with value coded
   *       “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and “Seguimento de tratamento
   *       TPT”(concept ID 23987) value coded “continua” or “fim” or no value(concept ID in [1257,
   *       1267, null]) marked on the first FILT (encounter type 60) and encounter datetime before
   *       end date and:
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartC7Part1Query() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "C7 Part 1 Query: all patients with Regime de TPT and Seguimento de tratamento TPT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT     p.patient_id "
            + "FROM       patient p "
            + "INNER JOIN encounter e "
            + "ON         p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON         e.encounter_id = o.encounter_id "
            + "INNER JOIN obs o2 "
            + "ON         e.encounter_id = o2.encounter_id "
            + "INNER JOIN "
            + "           ( "
            + "                      SELECT     p.patient_id, "
            + "                                 Min(e.encounter_datetime) AS first_filt "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         p.patient_id = e.patient_id "
            + "                      WHERE      p.voided = 0 "
            + "                      AND        e.voided = 0 "
            + "                      AND        e.encounter_type = ${60} "
            + "                      AND        e.encounter_datetime < :endDate "
            + "                      AND        e.location_id = :location "
            + "                      GROUP BY   p.patient_id) filt "
            + "ON         filt.patient_id = p.patient_id "
            + "WHERE      p.voided = 0 "
            + "AND        e.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.encounter_type = ${60} "
            + "AND        e.encounter_datetime = filt.first_filt "
            + "AND        ( "
            + "                      o.concept_id = ${23985}"
            + "           AND        o.value_coded IN( ${23954}, "
            + "                                       ${23984} ) ) "
            + "AND        ( "
            + "                      o2.concept_id = ${23987} "
            + "           AND        o2.value_coded IN ( ${1257}, "
            + "                                         ${1267}, "
            + "                                         NULL ) ) "
            + "AND        e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C7 Part 2: No other Regime de TPT (concept id 23985) value coded “3HP” or ”
   *       3HP+Piridoxina” (concept id in [23954, 23984]) marked on FILT (encounter type 60) in the
   *       4 months prior to the FILT 3HP start date.
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartC7Part2Query() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("C7 Part 2 Query: all patients with Regime de TPT marked on FILT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "INNER JOIN (   "
            + "SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "WHERE e.encounter_type = ${60}    "
            + "AND o.concept_id = ${23985} AND o.value_coded IN(${23954}, ${23984})   "
            + "AND e.location_id = :location       "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + ") AS regimeTPT    "
            + "ON regimeTPT.patient_id = p.patient_id   "
            + "WHERE e.encounter_type = ${60}   "
            + "AND o.concept_id = ${23985} AND o.value_coded IN(${23954},${23984})   "
            + "AND e.location_id = :location   "
            + "AND e.encounter_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime   "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C7 Part 3: No other 3HP start dates marked on Ficha clinica (encounter type 6, encounter
   *       datetime) with Profilaxia TPT (concept id 23985) value coded 3HP (concept id 23954) and
   *       Estado da Profilaxia (concept id 165308) value coded Início (concept id 1256) or Outras
   *       prescrições (concept id 1719) value coded 3HP or DT-3HP (concept id in [23954,165307])in
   *       the 4 months prior to the FILT 3HP start date. ; and
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartC7Part3Query() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("C7 Part 3 Query: all patients with Regime de TPT marked on FILT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + "INNER JOIN (   "
            + "SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "WHERE e.encounter_type = ${60}    "
            + "AND o.concept_id = ${23985} AND o.value_coded IN(${23954}, ${23984})   "
            + "AND e.location_id = :location       "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + ") AS regimeTPT    "
            + "ON regimeTPT.patient_id = p.patient_id   "
            + "WHERE e.encounter_type = ${6}   "
            + "AND (o.concept_id = ${23985} AND o.value_coded = ${23954}) "
            + "AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256})   "
            + "OR (o.concept_id = ${1719} AND o.value_coded IN (${23954},${165307})) "
            + "AND e.location_id = :location   "
            + "AND e.encounter_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime   "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>: User_Story_ TPT <br>
   *
   * <ul>
   *   <li>C7 Part 4: No other 3HP start dates marked on Ficha Resumo (encounter type 53) with
   *       Última profilaxia(concept id 23985) value coded 3HP(concept id 23954) and Data Início da
   *       Profilaxia TPT(value datetime, concept id 6128) in the 4 months prior to the FILT 3HP
   *       start date. ;
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getINHStartC7Part4Query() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();

    sqlCohortDefinition.setName("C7 Part 4 Query: all patients with Regime de TPT marked on FILT");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "INNER JOIN (   "
            + "SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "WHERE e.encounter_type = ${60}    "
            + "AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "AND e.location_id = :location       "
            + "AND e.encounter_datetime < :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + ") AS regimeTPT    "
            + "ON regimeTPT.patient_id = p.patient_id   "
            + "WHERE e.encounter_type = ${53}   "
            + "AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + "AND e.location_id = :location   "
            + "AND o.value_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime   "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Completion Patient List <br>
   *
   * <ul>
   *   <li>67) D1: Select all patients with Última profilaxia(concept id 23985) value coded
   *       3HP(concept id 23954) and Data Fim da Profilaxia TPT(value datetime, concept id 6129)
   *       registered on Ficha Resumo by end date (Encounter type 53) and with value datetime
   *       between 86 days and 365 days from the date of C3 or
   *   <li>
   *   <li>C3: Select all patients with FILT (encounter type 60) with “Regime de TPT” (concept id
   *       23985) value coded “3HP” or ” 3HP+Piridoxina” (concept id in [23954, 23984]) and
   *       encounter_datetime before end date as “3HP Start Date” no other 3HP prescriptions
   *       [“Outras prescricoes” (concept id 1719) with value coded equal to “3HP” (concept id
   *       23954)] marked on Ficha-Clínica in the 4 months prior to the 3HP Start Date and no
   *       “Regime de TPT” (concept id 23985) with value coded “3HP” or ” 3HP+Piridoxina” (concept
   *       id in [23954, 23984]) marked on FILT (encounter type 60) in the 4 months prior to the 3HP
   *       Start Date;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD1() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6129", hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id    "
            + "INNER JOIN (   "
            + " SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "WHERE e.encounter_type = ${60}    "
            + "AND o.concept_id = ${23985} AND o.value_coded IN(${23954}, ${23984})   "
            + "AND e.location_id = :location    "
            + "AND e.encounter_datetime < :endDate   "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "AND p.patient_id NOT IN (   "
            + "   SELECT p.patient_id FROM patient p    "
            + "   INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "   INNER JOIN (   "
            + "      SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "      INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " WHERE e.encounter_type = ${60}    "
            + " AND o.concept_id = ${23985} AND o.value_coded IN(${23954}, ${23984})   "
            + "      AND e.location_id = :location    "
            + "      AND e.encounter_datetime < :endDate   "
            + "      AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "   ) AS fichaClinica    "
            + "   ON fichaClinica.patient_id = p.patient_id   "
            + "   WHERE e.encounter_type = ${6}    "
            + "   AND o.concept_id = ${1719} AND o.value_coded = ${23954}    "
            + "   AND e.location_id = :location    "
            + "   AND e.encounter_datetime BETWEEN DATE_SUB(fichaClinica.encounter_datetime, INTERVAL 4 MONTH) AND fichaClinica.encounter_datetime   "
            + "   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + ")   "
            + "AND p.patient_id NOT IN (   "
            + "   SELECT p.patient_id FROM patient p    "
            + "   INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "   INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "   INNER JOIN (   "
            + "      SELECT p.patient_id, e.encounter_datetime FROM patient p    "
            + "      INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "      INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " WHERE e.encounter_type = ${60}    "
            + " AND o.concept_id = ${23985} AND o.value_coded IN(${23954}, ${23984})   "
            + "      AND e.location_id = :location    "
            + "      AND e.encounter_datetime < :endDate   "
            + "      AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + "   ) AS regimeTPT   "
            + "   ON regimeTPT.patient_id = p.patient_id   "
            + "   WHERE e.encounter_type = ${60}    "
            + "   AND o.concept_id = ${23985} AND o.value_coded IN(${23954},${23984})   "
            + "   AND e.location_id = :location   "
            + "   AND e.encounter_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime   "
            + "   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0   "
            + ") ) AS regimeTPT    "
            + "ON regimeTPT.patient_id = p.patient_id   "
            + "WHERE e.encounter_type = ${53}   "
            + " AND o.concept_id = ${23985} AND o.value_coded = ${23954} "
            + " AND e.location_id = :location  AND o2.concept_id = ${165308} AND o2.value_coded = ${1267} "
            + " AND DATEDIFF(o2.obs_datetime, regimeTPT.encounter_datetime) >= 86 AND DATEDIFF(o2.obs_datetime, regimeTPT.encounter_datetime) <= 365 AND o2.obs_datetime <= :endDate"
            + " AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND o2.voided = 0";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Completion Patient List <br>
   *
   * <ul>
   *   <li>D2: Select all patients withProfilaxia TPT (concept id 23985) value coded 3HP (concept id
   *       23954) and Estado da Profilaxia (concept id 165308) value coded Fim (concept id 1267)
   *       registered on Ficha clinica(encounter type 6) by end date and encounter datetime between
   *       86 days and 365 days from the date of C5; OR
   *   <li>
   *   <li>C5: Select all patients with Profilaxia TPT (concept id 23985) value coded 3HP (concept
   *       id 23954) and Estado da Profilaxia (concept id 165308) value coded Início (concept id
   *       1256) registered reporting end date on Ficha Clinica (Encounter type 6) ; or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());

    String query =
        " SELECT p.patient_id FROM patient p    "
            + "INNER JOIN encounter e ON p.patient_id  = e.patient_id    "
            + "INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + "INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id     "
            + "INNER JOIN (SELECT "
            + "             p.patient_id, e.encounter_datetime "
            + "             FROM "
            + "             patient p "
            + "             INNER JOIN "
            + "             encounter e ON p.patient_id = e.patient_id "
            + "             INNER JOIN "
            + "             obs o ON e.encounter_id = o.encounter_id "
            + "             INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id     "
            + "             WHERE "
            + "             p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "                AND e.encounter_type = ${6} "
            + "             AND (o.concept_id = ${23985} AND o.value_coded = ${23954})    "
            + "             AND (o2.concept_id = ${165308} AND o2.value_coded = ${1256})    "
            + "                AND e.encounter_datetime < :endDate "
            + "                AND e.location_id = :location ) AS regimeTPT    "
            + "ON regimeTPT.patient_id = p.patient_id   "
            + "WHERE e.encounter_type = ${6}   "
            + "AND (o.concept_id = ${23985} AND o.value_coded = ${23954})    "
            + "AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267})    "
            + "AND e.location_id = :location  AND o2.obs_datetime <= :endDate "
            + "AND DATEDIFF(o2.obs_datetime, regimeTPT.encounter_datetime) >= 86 AND DATEDIFF(o2.obs_datetime, regimeTPT.encounter_datetime) <= 365   "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0 ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <h4>User_Story_ TPT</h4>
   *
   * <ul>
   *   <li>D3: Select all pacientes from C and check if The date from M is registered on Ficha
   *       Clinica - Master Card (encounter type 6) or Ficha Resumo (encounter type 53) and: (a) The
   *       patient has at least 3 consultations (encounter type 6) with Profilaxia TPT (concept id
   *       23985) value coded 3HP (concept id 23954) and Estado da Profilaxia (concept id 165308)
   *       value coded Início/continua (concept id in [1256,1257]) during 120 days from the date
   *       from C1;
   *       <p>or (b) At least 1 consultation registered on Ficha Clínica (encounter type 6) with
   *       DT-3HP (concept ID 1719, value_coded =165307) during 120 days from the date from C1; or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD3() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("3HP Final D3");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch("get3HPD3A", EptsReportUtils.map(get3HPD3A(), mapping));

    compositionCohortDefinition.addSearch("get3HPD3B", EptsReportUtils.map(get3HPD3B(), mapping));

    compositionCohortDefinition.setCompositionString("get3HPD3A OR get3HPD3B");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Completion Patient List <br>
   *
   * <ul>
   *   <li>D3 A: The date from C is registered on Ficha Clinica - Master Card (encounter type 6) or
   *       Ficha Resumo (encounter type 53) and:
   *   <li>a) The patient has at least 3 consultations (encounter type 6) with Profilaxia TPT
   *       (concept id 23985) value coded 3HP (concept id 23954) and Estado da Profilaxia (concept
   *       id 165308) value coded Início/continua (concept id in [1256,1257]) during 120 days from
   *       the date from C1;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD3A() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p "
            + "  INNER JOIN encounter e "
            + "    ON e.patient_id = p.patient_id "
            + "  INNER JOIN obs o "
            + "    ON o.encounter_id = e.encounter_id "
            + "  INNER JOIN ( SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "             INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "             WHERE e.encounter_type =  ${6}   "
            + "             AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "             AND e.location_id = :location     "
            + "             AND e.encounter_datetime < :endDate    "
            + "             AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             AND p.patient_id NOT IN (    "
            + "                SELECT p.patient_id FROM patient p     "
            + "                INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                INNER JOIN (    "
            + "                   SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "                   INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                   WHERE e.encounter_type =  ${6}   "
            + "                   AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                   AND e.location_id = :location     "
            + "                   AND e.encounter_datetime < :endDate    "
            + "                   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "                ) AS fichaClinica     "
            + "                ON fichaClinica.patient_id = p.patient_id    "
            + "                WHERE e.encounter_type =  ${6}   "
            + "                AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                AND e.location_id = :location     "
            + "                AND e.encounter_datetime BETWEEN DATE_SUB(fichaClinica.encounter_datetime, INTERVAL 4 MONTH) AND fichaClinica.encounter_datetime    "
            + "                AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             )    "
            + "             AND p.patient_id NOT IN (    "
            + "                SELECT p.patient_id FROM patient p     "
            + "                INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                INNER JOIN (    "
            + "                   SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "                   INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                   WHERE e.encounter_type =  ${6}   "
            + "                   AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                   AND e.location_id = :location     "
            + "                   AND e.encounter_datetime < :endDate    "
            + "                   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "                ) AS regimeTPT     "
            + "                ON regimeTPT.patient_id = p.patient_id    "
            + "                WHERE e.encounter_type = ${60}     "
            + "                AND o.concept_id = ${23985} AND o.value_coded IN(${23954},${23984})    "
            + "                AND e.location_id = :location    "
            + "                AND e.encounter_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime    "
            + "                AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             )) AS tabela "
            + "    ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_type = ${6} "
            + "  AND ( (SELECT count(*) "
            + "  FROM   patient pp "
            + "       JOIN encounter ee "
            + "      ON pp.patient_id = ee.patient_id "
            + "       JOIN obs oo "
            + "      ON oo.encounter_id = ee.encounter_id "
            + "JOIN obs o2 "
            + " ON o2.encounter_id = ee.encounter_id"
            + "     WHERE  pp.voided = 0 "
            + "      AND ee.voided = 0 "
            + "      AND oo.voided = 0 "
            + "      AND p.patient_id = pp.patient_id "
            + "      AND ee.encounter_type = ${6} "
            + "      AND ee.location_id = :location "
            + "      AND ee.voided = 0 "
            + " AND (( oo.concept_id = ${23985}   AND oo.value_coded = ${23954} ) "
            + "                   AND (o2.concept_id = ${165308}  AND o2.value_coded in (${1256} ,${1257} )))"
            + "      AND ee.encounter_datetime BETWEEN "
            + "       tabela.encounter_datetime AND "
            + "    DATE_ADD(tabela.encounter_datetime, "
            + "    INTERVAL 120 DAY)) >= 3 ) "
            + " GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Completion Patient List <br>
   *
   * <ul>
   *   <li>D1: The date from C is registered on Ficha Clinica - Master Card (encounter type 6) or
   *       Ficha Resumo (encounter type 53) and:
   *   <li>At least 1 consultation registered on Ficha Clínica (encounter type 6) with DT-3HP
   *       (concept ID 1719, value_coded =165307) during 120 days from the date from C1;
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD3B() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Outras prescricoes D1");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("23954", tbMetadata.get3HPConcept().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("23984", tbMetadata.get3HPPiridoxinaConcept().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p "
            + "  INNER JOIN encounter e "
            + "    ON e.patient_id = p.patient_id "
            + "  INNER JOIN obs o "
            + "    ON o.encounter_id = e.encounter_id "
            + "  INNER JOIN ( SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "             INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "             WHERE e.encounter_type =  ${6}   "
            + "             AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "             AND e.location_id = :location     "
            + "             AND e.encounter_datetime < :endDate    "
            + "             AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             AND p.patient_id NOT IN (    "
            + "                SELECT p.patient_id FROM patient p     "
            + "                INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                INNER JOIN (    "
            + "                   SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "                   INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                   WHERE e.encounter_type =  ${6}   "
            + "                   AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                   AND e.location_id = :location     "
            + "                   AND e.encounter_datetime < :endDate    "
            + "                   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "                ) AS fichaClinica     "
            + "                ON fichaClinica.patient_id = p.patient_id    "
            + "                WHERE e.encounter_type =  ${6}   "
            + "                AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                AND e.location_id = :location     "
            + "                AND e.encounter_datetime BETWEEN DATE_SUB(fichaClinica.encounter_datetime, INTERVAL 4 MONTH) AND fichaClinica.encounter_datetime    "
            + "                AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             )    "
            + "             AND p.patient_id NOT IN (    "
            + "                SELECT p.patient_id FROM patient p     "
            + "                INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                INNER JOIN (    "
            + "                   SELECT p.patient_id, e.encounter_datetime FROM patient p     "
            + "                   INNER JOIN encounter e ON p.patient_id  = e.patient_id     "
            + "                   INNER JOIN obs o ON e.encounter_id = o.encounter_id     "
            + "                   WHERE e.encounter_type =  ${6}   "
            + "                   AND o.concept_id = ${1719} AND o.value_coded = ${23954}     "
            + "                   AND e.location_id = :location     "
            + "                   AND e.encounter_datetime < :endDate    "
            + "                   AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "                ) AS regimeTPT     "
            + "                ON regimeTPT.patient_id = p.patient_id    "
            + "                WHERE e.encounter_type = ${60}     "
            + "                AND o.concept_id = ${23985} AND o.value_coded IN(${23954},${23984})    "
            + "                AND e.location_id = :location    "
            + "                AND e.encounter_datetime BETWEEN DATE_SUB(regimeTPT.encounter_datetime, INTERVAL 4 MONTH) AND regimeTPT.encounter_datetime    "
            + "                AND p.voided = 0 AND e.voided = 0 AND o.voided = 0    "
            + "             )) AS tabela "
            + "    ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0 "
            + "  AND e.voided = 0 "
            + "  AND o.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND e.encounter_type = ${6} "
            + "  AND ( (SELECT count(*) "
            + "  FROM   patient pp "
            + "       JOIN encounter ee "
            + "      ON pp.patient_id = ee.patient_id "
            + "       JOIN obs oo "
            + "      ON oo.encounter_id = ee.encounter_id "
            + "     WHERE  pp.voided = 0 "
            + "      AND ee.voided = 0 "
            + "      AND oo.voided = 0 "
            + "      AND p.patient_id = pp.patient_id "
            + "      AND ee.encounter_type = ${6} "
            + "      AND ee.location_id = :location "
            + "      AND ee.voided = 0 "
            + "      AND oo.concept_id = ${1719} "
            + "      AND oo.value_coded = ${165307} "
            + "      AND ee.encounter_datetime BETWEEN "
            + "       tabela.encounter_datetime AND "
            + "    DATE_ADD(tabela.encounter_datetime, "
            + "    INTERVAL 120 DAY)) >= 1 ) "
            + " GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <h4>User_Story_ TPT</h4>
   *
   * <ul>
   *   <li>D4: The patient date from M is registered on FILT (encounter type 60, encounter
   *       datetime<= enddate) and: The patient has at least 1 drug pick-up on FILT (encounter type
   *       60) with “Regime de TPT” (concept id 23985) value coded “3HP” doxina” (concept id in
   *       [23954, 23984]) and “Tipo de dispensa” (concept id 23986) with value coded “Trimestral”
   *       (concept id 23720) during 120 days from the date from C2; or The patient has at least 3
   *       drug pick-up on FILT (encounter type 60) with “Regime de TPT” (concept id 23985) value
   *       coded “3HP” doxina” (concept id -in [23954, 23984]) and “Tipo de dispensa” (concept id
   *       23986) with value coded “Mensal” (concept id 1098) during 120 days from the date from C2.
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD4() {
    CompositionCohortDefinition compositionCohortDefinition = new CompositionCohortDefinition();
    compositionCohortDefinition.setName("3HP Final D4");
    compositionCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    compositionCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    compositionCohortDefinition.addSearch(
        "get3HPD4A",
        EptsReportUtils.map(
            get3HPD4A(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getQuarterlyConcept().getConceptId(),
                hivMetadata.getPatientTreatmentFollowUp().getConceptId(),
                hivMetadata.getStartDrugs().getConceptId(),
                hivMetadata.getRestartConcept().getConceptId()),
            mapping));
    compositionCohortDefinition.addSearch(
        "get3HPD4B",
        EptsReportUtils.map(
            get3HPD4B(
                tbMetadata.get3HPConcept().getConceptId(),
                tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId(),
                tbMetadata.getRegimeTPTConcept().getConceptId(),
                tbMetadata.get3HPPiridoxinaConcept().getConceptId(),
                tbMetadata.getTypeDispensationTPTConceptUuid().getConceptId(),
                hivMetadata.getMonthlyConcept().getConceptId()),
            mapping));

    compositionCohortDefinition.setCompositionString("get3HPD4A OR get3HPD4B");

    return compositionCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Completion Patient List <br>
   *
   * <ul>
   *   <li>D2: The patient has at least 1 drug pick-up on FILT (encounter type 60) with “Regime de
   *       TPT” (concept id 23985) value coded “3HP” doxina” (concept id in [23954, 23984]) and
   *       “Tipo de dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720)
   *       during 120 days from the date from C2; or
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD4A(
      int threeHPConcept,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int hPPiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int quarterlyConcept,
      int seguimentoTPTConcept,
      int inicioConcept,
      int reinicioConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT D2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("23954", threeHPConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("23720", quarterlyConcept);
    map.put("23987", seguimentoTPTConcept);
    map.put("1256", inicioConcept);
    map.put("1705", reinicioConcept);

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join (SELECT p.patient_id,    "
            + "                          e.encounter_datetime "
            + "                   FROM   patient p    "
            + "                          inner join encounter e   "
            + "                                  ON e.patient_id = p.patient_id   "
            + "                          inner join obs o "
            + "                                  ON o.encounter_id = e.encounter_id   "
            + "                          INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id   "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_type = ${60} "
            + "                          AND ( o.concept_id = ${23985} AND o.value_coded IN (${23954},${23984}) ) "
            + "                          AND (o2.concept_id = ${23987} AND o2.value_coded IN (${1256},${1705})) "
            + "                          AND e.encounter_datetime <= :endDate) AS tabela "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND ( (SELECT Count(*)  "
            + "              FROM   patient pp    "
            + "                     join encounter ee "
            + "                       ON pp.patient_id = ee.patient_id    "
            + "              WHERE  pp.voided = 0 "
            + "                     AND ee.voided = 0 "
            + "                     AND p.patient_id = pp.patient_id  "
            + "                     AND ee.encounter_type = ${60}    "
            + "                     AND ee.location_id = :location  "
            + "                     AND ee.voided = 0 "
            + "                     AND ( EXISTS (SELECT o.person_id  "
            + "                                   FROM   obs o    "
            + "                                   WHERE  o.encounter_id = ee.encounter_id "
            + "                                          AND o.concept_id = ${23985} "
            + "                                          AND o.value_coded IN ( ${23954}, ${23984} ))   "
            + "                           AND EXISTS (SELECT o.person_id  "
            + "                                       FROM   obs o    "
            + "                                       WHERE  o.encounter_id = ee.encounter_id "
            + "                                              AND o.concept_id = ${23986} "
            + "                                              AND o.value_coded IN ( ${23720} )) )    "
            + "                     AND ee.encounter_datetime BETWEEN "
            + "                         tabela.encounter_datetime AND "
            + "             Date_add(tabela.encounter_datetime,   "
            + "             INTERVAL 120 DAY)) >= 1 ) "
            + " GROUP  BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>IMER1</b>:User Story TPT Eligible Patient List <br>
   *
   * <ul>
   *   <li>D3: The patient has at least 3 drug pick-up on FILT (encounter type 6) with “Regime de
   *       TPT” (concept id 23985) value coded “3HP” doxina” (concept id in [23954, 23984]) and
   *       “Tipo de dispensa” (concept id 23986) with value coded “Trimestral” (concept id 1098)
   *       during 120 days from the date from C2.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition get3HPD4B(
      int threeHPConcept,
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int hPPiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int monthlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT D3");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("23954", threeHPConcept);
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("23984", hPPiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("1098", monthlyConcept);

    String query =
        " SELECT p.patient_id   "
            + " FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join (SELECT p.patient_id,    "
            + "                          e.encounter_datetime "
            + "                   FROM   patient p    "
            + "                          inner join encounter e   "
            + "                                  ON e.patient_id = p.patient_id   "
            + "                          inner join obs o "
            + "                                  ON o.encounter_id = e.encounter_id   "
            + "                   WHERE  p.voided = 0 "
            + "                          AND e.voided = 0 "
            + "                          AND o.voided = 0 "
            + "                          AND e.location_id = :location  "
            + "                          AND e.encounter_type = ${60}    "
            + "                          AND o.concept_id = ${23985} "
            + "                          AND o.value_coded IN ( ${23954}, ${23984} )    "
            + "                          AND e.encounter_datetime <= :endDate) AS tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND ( (SELECT Count(*)  "
            + "              FROM   patient pp    "
            + "                     join encounter ee "
            + "                       ON pp.patient_id = ee.patient_id    "
            + "              WHERE  pp.voided = 0 "
            + "                     AND ee.voided = 0 "
            + "                     AND p.patient_id = pp.patient_id  "
            + "                     AND ee.encounter_type = ${60}    "
            + "                     AND ee.location_id = :location  "
            + "                     AND ee.voided = 0 "
            + "                     AND ( EXISTS (SELECT o.person_id  "
            + "                                   FROM   obs o    "
            + "                                   WHERE  o.encounter_id = ee.encounter_id "
            + "                                          AND o.concept_id = ${23985} "
            + "                                          AND o.value_coded IN ( ${23954}, ${23984} ))   "
            + "                           AND EXISTS (SELECT o.person_id  "
            + "                                       FROM   obs o    "
            + "                                       WHERE  o.encounter_id = ee.encounter_id "
            + "                                              AND o.concept_id = ${23986} "
            + "                                              AND o.value_coded IN ( ${1098} )) ) "
            + "                     AND ee.encounter_datetime BETWEEN "
            + "                         tabela.encounter_datetime AND "
            + "             Date_add(tabela.encounter_datetime,   "
            + "             INTERVAL 120 DAY)) >= 3 ) "
            + " GROUP  BY p.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>TBPREV</b>: Denominator <br>
   *
   * <ul>
   *   <li>G - TB_PREV - Denominator 7 months before the reporting period
   *   <li>(Start Date = Selected End Date – 210 days and End Date = Selected End Date)
   *   <li>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getTBPrevDenominator() {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB-PREV Denominator Query");
    definition.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    definition.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));
    definition.addSearch(
        "started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${onOrBefore},location=${location}"));
    definition.addSearch(
        "A",
        EptsReportUtils.map(tbPrevQueries.getPatientsWhoCompleted3HPAtLeast86Days(), mapping3));
    definition.addSearch(
        "B",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast1ConsultarionWithDT3HPOnFichaClinica(), mapping3));
    definition.addSearch(
        "C", EptsReportUtils.map(tbPrevQueries.getAtLeast3ConsultationOnFichaClinica(), mapping3));

    definition.addSearch(
        "D", EptsReportUtils.map(tbPrevQueries.getAtLeastOne3HPWithDTOnFilt(), mapping3));

    definition.addSearch(
        "E",
        EptsReportUtils.map(
            tbPrevQueries.getAtLeast3ConsultarionWithINHDispensaMensalOnFilt(), mapping3));

    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPatientsTransferredOut(),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
    definition.addSearch(
        "completed-isoniazid",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPatientsThatCompletedIsoniazidProphylacticTreatment(),
            mapping3));

    definition.setCompositionString(
        "started-by-end-previous-reporting-period "
            + " AND (A OR (B OR C) or (D OR E)"
            + "    AND NOT (transferred-out AND NOT completed-isoniazid)"
            + "     ) ");

    return definition;
  }

  /**
   *
   *
   * <ul>
   *   <li>Patient has Profilaxia TPT with the value “Isoniazida (INH)” and Estado da Profilaxia
   *       with the value “Fim (F)”) marked in Ficha Clínica - Mastercard or Ficha de Seguimento
   *       with Data do Estado da Profilaxia by reporting end date and between 173 days and 365 days
   *       from the INH start date or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Profilaxia com INH B2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    String Y =
        new UnionBuilder(TPTEligiblePatientsQueries.getY1Query())
            .union(TPTEligiblePatientsQueries.getY2Query())
            .union(TPTEligiblePatientsQueries.getY3Query())
            .union(TPTEligiblePatientsQueries.getY5Query())
            .union(TPTCompletionQueries.getInhStartOnFilt())
            .buildQuery();
    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + " INNER JOIN ( "
            + Y
            + " ) inh_start ON inh_start.patient_id = p.patient_id "
            + "       WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "             AND e.encounter_type IN (${6}, ${9}) "
            + "             AND  (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "             AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267}) "
            + "             AND o2.obs_datetime BETWEEN DATE_ADD(inh_start.start_date, interval 173 day) "
            + "            AND DATE_ADD(inh_start.start_date, interval 365 day) "
            + "           AND o2.obs_datetime <= :endDate "
            + " GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   *
   *
   * <ul>
   *   <li>B6: At least 2 drug pick-ups with “Regime de TPT” (concept id 23985) value coded
   *       ‘Isoniazid’ or ‘Isoniazid + piridoxina’ (concept id in [656, 23982]) and “Tipo de
   *       dispensa” (concept id 23986) with value coded “Trimestral” (concept id 23720) until a 5
   *       Month Period from the INH Start Date (date from Y4) or
   *   <li>
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB6Part2(
      int regimeTPTEncounterType,
      int regimeTPTConcept,
      int isoniazidConcept,
      int isoniazidePiridoxinaConcept,
      int typeDispensationTPTConceptUuid,
      int quarterlyConcept) {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Regime de TPT B6.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("60", regimeTPTEncounterType);
    map.put("23985", regimeTPTConcept);
    map.put("656", isoniazidConcept);
    map.put("23982", isoniazidePiridoxinaConcept);
    map.put("23986", typeDispensationTPTConceptUuid);
    map.put("23720", quarterlyConcept);
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1719", tbMetadata.getTreatmentPrescribedConcept().getConceptId());
    map.put("165307", tbMetadata.getDT3HPConcept().getConceptId());

    EptsQueriesUtil unionBuilder = new EptsQueriesUtil();

    // this will generate one union separated query based on the given queries
    String unionQuery =
        unionBuilder
            .unionBuilder(TPTEligiblePatientsQueries.getY5QueryWithPatientIdForB5())
            .union(TPTCompletionQueries.getInhStartOnFilt())
            .buildQuery();

    String query =
        " SELECT p.patient_id   "
            + "FROM   patient p   "
            + "       inner join encounter e  "
            + "               ON e.patient_id = p.patient_id  "
            + "       inner join obs o    "
            + "               ON o.encounter_id = e.encounter_id  "
            + "       inner join ( "
            + unionQuery
            + " ) AS tabela  "
            + "               ON tabela.patient_id = p.patient_id "
            + " WHERE  p.voided = 0    "
            + "       AND e.voided = 0    "
            + "       AND o.voided = 0    "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${60}   "
            + "       AND (( (SELECT Count(*) "
            + "               FROM   patient pp   "
            + "                      inner join encounter ee  "
            + "                              ON pp.patient_id = ee.patient_id "
            + "               WHERE  pp.voided = 0    "
            + "                      AND ee.voided = 0    "
            + "                      AND p.patient_id = pp.patient_id "
            + "                      AND ee.location_id = :location "
            + "                      AND ( EXISTS(SELECT oo.obs_id    "
            + "                                   FROM   obs oo   "
            + "                                   WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                          AND oo.concept_id = ${23985}    "
            + "                                          AND oo.value_coded IN ( ${656}, ${23982} ))    "
            + "                            AND EXISTS(SELECT oo.obs_id    "
            + "                                       FROM   obs oo   "
            + "                                       WHERE  oo.encounter_id = ee.encounter_id    "
            + "                                              AND oo.concept_id = ${23986}    "
            + "                                              AND oo.value_coded IN ( ${23720} )) )   "
            + "                      AND ee.encounter_datetime BETWEEN    "
            + "                          tabela.start_date AND    "
            + "              Date_add(tabela.start_date,  "
            + "              INTERVAL 5 MONTH) AND ee.encounter_datetime <= :endDate) >= 2 ))   "
            + " GROUP  BY p.patient_id  ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <ul>
   *   <li>Patient has at least one Última profilaxia TPT with value “Isoniazida (INH)” and Estado
   *       da Profilaxia with the value “Fim (F)” and Data de Fim da Profilaxia TPT registered by
   *       reporting end date in Ficha Resumo – Mastercard and between 173 days and 365 days from
   *       the INH start date
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getIPTB1part2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" all patients with Ultima profilaxia Isoniazida (Data Fim) B1.2");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Before Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("23985", tbMetadata.getRegimeTPTConcept().getConceptId());
    map.put("656", tbMetadata.getIsoniazidConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId());
    map.put("165308", tbMetadata.getDataEstadoDaProfilaxiaConcept().getConceptId());
    map.put("1267", hivMetadata.getCompletedConcept().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("60", tbMetadata.getRegimeTPTEncounterType().getEncounterTypeId());
    map.put("23982", tbMetadata.getIsoniazidePiridoxinaConcept().getConceptId());
    map.put("23987", hivMetadata.getPatientTreatmentFollowUp().getConceptId());
    map.put("1705", hivMetadata.getRestartConcept().getConceptId());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId());

    String Y =
        new UnionBuilder(TPTEligiblePatientsQueries.getY1Query())
            .union(TPTEligiblePatientsQueries.getY2Query())
            .union(TPTEligiblePatientsQueries.getY3Query())
            .union(TPTEligiblePatientsQueries.getY5Query())
            .union(TPTCompletionQueries.getInhStartOnFilt())
            .buildQuery();

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + " INNER JOIN obs o2 ON e.encounter_id = o2.encounter_id "
            + " INNER JOIN ( "
            + Y
            + " ) inh_start ON inh_start.patient_id = p.patient_id "
            + "       WHERE p.voided =0 AND e.voided = 0 AND o.voided=0 AND o2.voided=0 "
            + "             AND e.encounter_type = ${53} "
            + "             AND  (o.concept_id = ${23985} AND o.value_coded = ${656}) "
            + "             AND (o2.concept_id = ${165308} AND o2.value_coded = ${1267}) "
            + "             AND o2.obs_datetime BETWEEN DATE_ADD(inh_start.start_date, interval 173 day) "
            + "            AND DATE_ADD(inh_start.start_date, interval 365 day) "
            + "           AND o2.obs_datetime <= :endDate "
            + " GROUP BY p.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(sb.replace(query));

    return sqlCohortDefinition;
  }
}
