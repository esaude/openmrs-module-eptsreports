package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralCohortQueries {

  private GenericCohortQueries genericCohortQueries;
  private HivMetadata hivMetadata;
  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public ResumoTrimestralCohortQueries(
      GenericCohortQueries genericCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      HivMetadata hivMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /** Indicator A - Nº de pacientes que iniciou TARV nesta unidade sanitária durante o mês */
  public CohortDefinition getA() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredIn", map(transferredIn, less12m));
    cd.setCompositionString("startedArt NOT transferredIn");
    return cd;
  }

  /** Indicator B - Nº de pacientes Transferidos de (+) outras US em TARV durante o mês */
  public CohortDefinition getB() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredIn", map(transferredIn, less12m));
    cd.setCompositionString("startedArt AND transferredIn");
    return cd;
  }

  /** Indicator C - Nº de pacientes Transferidos para (-) outras US em TARV durante o mês */
  public CohortDefinition getC() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredOut", map(transferredOut, less12m));
    cd.setCompositionString("startedArt AND transferredOut");
    return cd;
  }

  /** Indicator D - ((A+B) - C) */
  public CohortDefinition getD() {
    CompositionCohortDefinition cdAbc = new CompositionCohortDefinition();
    cdAbc.setName("Indicators A, B and C parameters");
    cdAbc.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cdAbc.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cdAbc.addParameter(new Parameter("location", "location", Location.class));
    cdAbc.addSearch(
        "A",
        EptsReportUtils.map(
            getA(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    cdAbc.addSearch(
        "B",
        EptsReportUtils.map(
            getB(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    cdAbc.addSearch(
        "C",
        EptsReportUtils.map(
            getC(), "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    cdAbc.setCompositionString("((A OR B) AND NOT C)");

    return cdAbc;
  }

  /** Indicator E - Number of patients who is in the 1st line treatment during the cohort month */
  public CohortDefinition getE() {
    CohortDefinition preTarv = getA();
    CohortDefinition transferredIn = getB();
    CohortDefinition transferredOut = getC();
    CohortDefinition suspended = getI();
    CohortDefinition abandoned = getJ();
    CohortDefinition dead = getL();
    CohortDefinition lastFirstTherapeuticLine =
        getPatientsWithLastTherapeuticLineEqualsToFirstLineOrWithoutInformation();
    CompositionCohortDefinition wrapper = new CompositionCohortDefinition();
    wrapper.setParameters(getParameters());
    wrapper.addSearch("preTarv", mapStraightThrough(preTarv));
    wrapper.addSearch("transferredIn", mapStraightThrough(transferredIn));
    wrapper.addSearch("transferredOut", mapStraightThrough(transferredOut));
    wrapper.addSearch("suspended", mapStraightThrough(suspended));
    wrapper.addSearch("abandoned", mapStraightThrough(abandoned));
    wrapper.addSearch("dead", mapStraightThrough(dead));
    wrapper.addSearch("lastFirstTherapeuticLine", mapStraightThrough(lastFirstTherapeuticLine));

    wrapper.setCompositionString(
        "((preTarv OR transferredIn) NOT (transferredOut OR suspended OR abandoned OR dead)) AND lastFirstTherapeuticLine ");
    return wrapper;
  }

  /**
   * Indicator F - Number of patients in Cohort who completed 12 months ARV treatment in the 1st
   * line treatment who received one Viral load result
   */
  public CohortDefinition getF() {
    CohortDefinition cohortE = getE();
    CohortDefinition viralLoadResult = getNumberOfPatientsInFichaClinicaWithViralLoadResult();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setParameters(getParameters());
    cd.addSearch("cohortE", mapStraightThrough(cohortE));
    cd.addSearch("viralLoadResult", mapStraightThrough(viralLoadResult));
    cd.setCompositionString("cohortE AND viralLoadResult");

    return cd;
  }

  /** Indicator G - Number of patients who is in the 2nd line treatment during the cohort month */
  public CohortDefinition getG() {
    CohortDefinition indicatorA = getA();
    CohortDefinition indicatorB = getB();
    CohortDefinition indicatorC = getC();
    CohortDefinition indicatorI = getI();
    CohortDefinition indicatorJ = getJ();
    CohortDefinition indicatorL = getL();
    CohortDefinition lastSecondTherapeuticLine =
        getPatientsWithLastObsInSecondTherapeuticLineInMasterCardFichaClinicaBeforeMonthEndDate();

    CompositionCohortDefinition comp = new CompositionCohortDefinition();
    comp.setParameters(getParameters());
    comp.addSearch("A", mapStraightThrough(indicatorA));
    comp.addSearch("B", mapStraightThrough(indicatorB));
    comp.addSearch("C", mapStraightThrough(indicatorC));
    comp.addSearch("I", mapStraightThrough(indicatorI));
    comp.addSearch("J", mapStraightThrough(indicatorJ));
    comp.addSearch("L", mapStraightThrough(indicatorL));
    comp.addSearch(
        "lastSecondTherapeuticLine",
        map(lastSecondTherapeuticLine, "endDate=${onOrBefore},location=${location}"));
    comp.setCompositionString(
        "((A OR B) AND NOT (C OR I OR J OR L)) AND lastSecondTherapeuticLine");
    return comp;
  }

  /**
   * Indicator H - Number of patients in Cohort who completed 12 months ARV treatment in the 2nd
   * line treatment who received one Viral load result
   */
  public CohortDefinition getH() {
    CohortDefinition cohortG = getG();
    CohortDefinition viralLoadResult = getNumberOfPatientsInFichaClinicaWithViralLoadResult();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setParameters(getParameters());
    cd.addSearch("cohortG", mapStraightThrough(cohortG));
    cd.addSearch("viralLoadResult", mapStraightThrough(viralLoadResult));
    cd.setCompositionString("cohortG AND viralLoadResult");

    return cd;
  }

  /** Indicator I - Number of Suspended patients in the actual cohort */
  public CohortDefinition getI() {
    CohortDefinition indicatorA = getA();
    CohortDefinition indicatorB = getB();
    CohortDefinition indicatorC = getC();

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Number of patients with ART suspension during the current month");
    sqlCohortDefinition.setParameters(getParameters());
    sqlCohortDefinition.setQuery(
        ResumoTrimestralQueries.getPatientsWhoSuspendedTreatment(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getSuspendedTreatmentConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));

    CompositionCohortDefinition comp = new CompositionCohortDefinition();
    comp.setName("I indicator - Suspended Patients");
    comp.setParameters(getParameters());
    comp.addSearch("A", mapStraightThrough(indicatorA));
    comp.addSearch("B", mapStraightThrough(indicatorB));
    comp.addSearch("C", mapStraightThrough(indicatorC));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    comp.addSearch("Suspended", map(sqlCohortDefinition, less12m));

    comp.setCompositionString("((A OR B) AND NOT C) AND Suspended");
    return comp;
  }

  /** Indicator J - Number of Abandoned Patients in the actual cohort */
  public CohortDefinition getJ() {
    CohortDefinition abandoned =
        resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setParameters(getParameters());
    cd.addSearch("A", mapStraightThrough(getA()));
    cd.addSearch("B", mapStraightThrough(getB()));
    cd.addSearch("abandoned", map(abandoned, "date=${onOrBefore},location=${location}"));
    cd.addSearch("C", mapStraightThrough(getC()));
    cd.addSearch("I", mapStraightThrough(getI()));
    cd.addSearch("L", mapStraightThrough(getL()));
    cd.setCompositionString("(A OR B) AND abandoned NOT (C OR I OR L)");
    return cd;
  }

  /**
   * Fetches Patients with Last registered Line Treatment equals to (1st Line) or without
   * information regarding Therapeutic Line
   */
  private CohortDefinition
      getPatientsWithLastTherapeuticLineEqualsToFirstLineOrWithoutInformation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients in the first Line of treatment during a period");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoTrimestralQueries
            .getPatientsWithLastTherapeuticLineEqualsToFirstLineOrWithoutInformation(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getTherapeuticLineConcept().getConceptId(),
                hivMetadata.getFirstLineConcept().getConceptId()));
    return cd;
  }

  /** Indicator L - Number of Deceased patients in the actual cohort */
  public CohortDefinition getL() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition cohortA = getA();
    CohortDefinition cohortB = getB();
    CohortDefinition cohortC = getC();
    CohortDefinition dead = getDeceasedPatients();
    cd.setParameters(getParameters());
    cd.addSearch("A", mapStraightThrough(cohortA));
    cd.addSearch("B", mapStraightThrough(cohortB));
    cd.addSearch("C", mapStraightThrough(cohortC));
    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("dead", map(dead, less12m));
    cd.setCompositionString("((A OR B) AND NOT C) AND dead");
    return cd;
  }

  public EptsQuarterlyCohortDefinition getQuarterlyCohort(
      CohortDefinition wrap,
      EptsQuarterlyCohortDefinition.Month month,
      List<Parameter> parameters) {
    EptsQuarterlyCohortDefinition cd = new EptsQuarterlyCohortDefinition(wrap, month);
    cd.addParameters(parameters);
    return cd;
  }

  /** Number Of Patients In Ficha Clinica With Viral Load Result */
  private CohortDefinition getNumberOfPatientsInFichaClinicaWithViralLoadResult() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Patients in the 1st line treatment who received one Viral load result");
    sqlCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
    sqlCohortDefinition.setQuery(
        ResumoTrimestralQueries.getPatientsWhoReceivedOneViralLoadResult(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));

    return sqlCohortDefinition;
  }

  /**
   * All patients with last observation registered in Second Therapeutic Line in Master Card – Ficha
   * Clinica before MonthEndDate encounter type id 6
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getPatientsWithLastObsInSecondTherapeuticLineInMasterCardFichaClinicaBeforeMonthEndDate() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName(
        "All patients with last observation registered in Second Therapeutic Line in Master Card – Ficha Clinica 6");
    sql.addParameter(new Parameter("endDate", "End date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        ResumoTrimestralQueries
            .getPatientsWithLastObsInSecondTherapeuticLineInMasterCardFichaClinicaBeforeMonthEndDate(
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getTherapeuticLineConcept().getConceptId(),
                hivMetadata.getSecondLineConcept().getConceptId()));
    return sql;
  }

  /** Number of patients transferred-in from another HFs during the current month */
  private CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth() {

    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setProgramEnrolled(hivMetadata.getHIVCareProgram());
    cd.setProgramEnrolled2(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState());
    cd.setPatientState2(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameters(getParameters());
    cd.setB10Flag(false);
    return cd;
  }

  private List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("onOrAfter", "Start date", Date.class),
        new Parameter("onOrBefore", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }

  /**
   * Number of transferred-out patients in TARV at this HF during the cohort month.
   *
   * <p>This is the same as {@link HivCohortQueries#getPatientsTransferredOut()} except that it
   * filters for both {@code onOrAfter} and {@code onOrBefore}.
   *
   * <p>It was duplicated here because the spec pointed to an incompatible query
   */
  private CohortDefinition getPatientsTransferredOut() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("transferredOutPatients");
    cd.addParameters(getParameters());
    cd.setQuery(
        ResumoTrimestralQueries.getTransferedOutPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata
                .getTransferredOutToAnotherHealthFacilityWorkflowState()
                .getProgramWorkflowStateId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getTransferredOutConcept().getConceptId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));
    return cd;
  }

  /**
   * Deceased patients
   *
   * <p>Copied from {@link GenericCohortQueries#getDeceasedPatients()} because it was not compatible
   * with current specs.
   *
   * <p>Should be removed after refactoring common queries.
   */
  private CohortDefinition getDeceasedPatients() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Get deceased patients based on patient states and person object");
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoTrimestralQueries.getDeceasedPatients(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getPatientHasDiedWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId(),
            hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId(),
            hivMetadata.getPatientFoundConcept().getConceptId(),
            hivMetadata.getNoConcept().getConceptId(),
            hivMetadata.getReasonPatientNotFound().getConceptId(),
            hivMetadata.getPatientIsDead().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getStateOfStayOfPreArtPatient().getConceptId(),
            hivMetadata.getStateOfStayOfArtPatient().getConceptId(),
            hivMetadata.getPatientHasDiedConcept().getConceptId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));
    return cd;
  }
}
