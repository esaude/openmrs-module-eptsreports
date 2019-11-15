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

  private final String generalParameterMapping =
      "startDate=${startDate},endDate=${endDate},location=${location}";

  private final String codedObsParameterMapping =
      "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";

  private Mapped<CohortDefinition> map(final CohortDefinition cd, final String parameterMappings) {
    return EptsReportUtils.map(
        cd,
        EptsReportUtils.removeMissingParameterMappingsFromCohortDefintion(cd, parameterMappings));
  }

  private void addGeneralParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  /**
   * INICIO DE TRATAMENTO DE TUBERCULOSE DATA NOTIFICADA NAS FICHAS DE: SEGUIMENTO, RASTREIO E LIVRO
   * TB. codes: DATAINICIO
   */
  public CohortDefinition tbTreatmentStartDateWithinReportingDate() {
    final CohortDefinition definition =
        this.genericCohortQueries.generalSql(
            "startedTbTreatment",
            TXTBQueries.dateObs(
                this.tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    this.hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));
    this.addGeneralParameters(definition);
    return definition;
  }

  /** PROGRAMA: PACIENTES INSCRITOS NO PROGRAMA DE TUBERCULOSE - NUM PERIODO */
  public CohortDefinition getInTBProgram() {
    final CohortDefinition definition =
        this.genericCohortQueries.generalSql(
            "TBPROGRAMA",
            TXTBQueries.inTBProgramWithinReportingPeriodAtLocation(
                this.tbMetadata.getTBProgram().getProgramId()));
    this.addGeneralParameters(definition);
    return definition;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE NEGATIVO codes: RASTREIOTBNEG */
  public CohortDefinition codedNoTbScreening() {
    final CohortDefinition cd =
        this.genericCohortQueries.hasCodedObs(
            this.tbMetadata.getTbScreeningConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                this.hivMetadata.getAdultoSeguimentoEncounterType(),
                this.hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(this.commonMetadata.getNoConcept()));
    this.addGeneralParameters(cd);
    return cd;
  }

  /** PACIENTES COM RASTREIO DE TUBERCULOSE POSITIVO codes: RASTREIOTBPOS */
  public CohortDefinition codedYesTbScreening() {
    final CohortDefinition cd =
        this.genericCohortQueries.hasCodedObs(
            this.tbMetadata.getTbScreeningConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                this.hivMetadata.getAdultoSeguimentoEncounterType(),
                this.hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(this.commonMetadata.getYesConcept()));
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition artList() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addSearch(
        "started-by-end-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("started-by-end-reporting-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveInvestigationResult() {
    final CohortDefinition cd =
        this.genericCohortQueries.hasCodedObs(
            this.tbMetadata.getResearchResultConcept(),
            TimeModifier.ANY,
            SetComparator.IN,
            Arrays.asList(
                this.hivMetadata.getAdultoSeguimentoEncounterType(),
                this.hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(this.tbMetadata.getPositiveConcept()));
    this.addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “POS” or “NEG” selected for “Resultado da Investigação para TB de BK e/ou RX?”
   * during the reporting period consultations; ( response 703: POS or 664: NEG for question: 6277)
   */
  public CohortDefinition positiveInvestigationResultComposition() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition P = this.positiveInvestigationResult();
    cd.addSearch("P", this.map(P, this.codedObsParameterMapping));
    cd.setCompositionString("P");
    this.addGeneralParameters(cd);
    return cd;
  }

  /**
   * at least one “S” or “N” selected for TB Screening (Rastreio de TB) during the reporting period
   * consultations; (response 1065: YES or 1066: NO for question 6257: SCREENING FOR TB)
   */
  public CohortDefinition yesOrNoInvestigationResult() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition S = this.codedYesTbScreening();
    cd.addSearch("S", this.map(S, this.codedObsParameterMapping));
    final CohortDefinition N = this.codedNoTbScreening();
    cd.addSearch("N", this.map(N, this.codedObsParameterMapping));
    cd.setCompositionString("S OR N");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumeratorA() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition i =
        this.genericCohortQueries.generalSql(
            "onTbTreatment",
            TXTBQueries.dateObs(
                this.tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    this.hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));
    this.addGeneralParameters(i);
    cd.addSearch("i", this.map(i, this.generalParameterMapping));
    final CohortDefinition ii = this.getInTBProgram();
    cd.addSearch("ii", this.map(ii, this.generalParameterMapping));
    final CohortDefinition artList = this.artList();
    cd.addSearch("artList", this.map(artList, this.generalParameterMapping));
    cd.setCompositionString("(i OR ii) AND artList");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition txTbNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition A = this.txTbNumeratorA();
    cd.addSearch("A", this.map(A, this.generalParameterMapping));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            this.tbTreatmentStartDateWithinReportingDate(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));

    cd.setCompositionString("A NOT started-tb-treatment-previous-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition positiveScreening() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addSearch(
        "A", EptsReportUtils.map(this.codedYesTbScreening(), this.codedObsParameterMapping));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            this.positiveInvestigationResultComposition(), this.generalParameterMapping));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            this.tbTreatmentStartDateWithinReportingDate(), this.generalParameterMapping));
    cd.addSearch("D", EptsReportUtils.map(this.getInTBProgram(), this.generalParameterMapping));
    cd.setCompositionString("A OR B OR C OR D");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition newOnARTPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.positiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator AND new-on-art AND positive-screening");
    return definition;
  }

  public CohortDefinition newOnARTNegativeScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.positiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND new-on-art) NOT positive-screening");
    return definition;
  }

  public CohortDefinition previouslyOnARTPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("newOnARTPositiveScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.positiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("(denominator AND positive-screening) NOT new-on-art");
    return definition;
  }

  public CohortDefinition previouslyOnARTNegativeScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("previouslyOnARTNegativeScreening()");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getDenominator(), this.generalParameterMapping));
    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.positiveScreening(), this.generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator NOT (new-on-art OR positive-screening)");
    return definition;
  }

  public CohortDefinition patientsNewOnARTNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition NUM = this.txTbNumerator();
    cd.addSearch("NUM", this.map(NUM, this.generalParameterMapping));
    cd.addSearch(
        "started-during-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("NUM AND started-during-reporting-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition patientsPreviouslyOnARTNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final CohortDefinition NUM = this.txTbNumerator();
    cd.addSearch("NUM", this.map(NUM, this.generalParameterMapping));
    cd.addSearch(
        "started-before-start-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${startDate-1d},location=${location}"));
    cd.setCompositionString("NUM AND started-before-start-reporting-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  public CohortDefinition getDenominator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB - Denominator");
    definition.addSearch(
        "art-list",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));
    definition.addSearch(
        "tb-screening",
        EptsReportUtils.map(this.yesOrNoInvestigationResult(), this.generalParameterMapping));
    definition.addSearch(
        "tb-investigation",
        EptsReportUtils.map(
            this.positiveInvestigationResultComposition(), this.generalParameterMapping));
    definition.addSearch(
        "started-tb-treatment",
        EptsReportUtils.map(
            this.tbTreatmentStartDateWithinReportingDate(), this.generalParameterMapping));
    definition.addSearch(
        "in-tb-program", EptsReportUtils.map(this.getInTBProgram(), this.generalParameterMapping));

    definition.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            this.tbTreatmentStartDateWithinReportingDate(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));
    definition.addSearch(
        "in-tb-program-previous-period",
        EptsReportUtils.map(
            this.getInTBProgram(),
            "startDate=${startDate-6m},endDate=${startDate-1d},location=${location}"));

    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            this.genericCohortQueries.getPatientsBasedOnPatientStates(
                this.hivMetadata.getARTProgram().getProgramId(),
                this.hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    definition.setCompositionString(
        "(art-list AND (tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program)) "
            + "NOT ((transferred-out NOT (started-tb-treatment OR in-tb-program)) OR started-tb-treatment-previous-period OR in-tb-program-previous-period)");

    return definition;
  }

  public CohortDefinition getNewOnArt() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB New on ART");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "started-on-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    definition.setCompositionString("started-on-period");
    return definition;
  }
}
