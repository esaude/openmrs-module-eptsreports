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
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralCohortQueries {

  private GenericCohortQueries genericCohortQueries;
  private HivCohortQueries hivCohortQueries;
  private HivMetadata hivMetadata;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  @Autowired
  public ResumoTrimestralCohortQueries(
      GenericCohortQueries genericCohortQueries,
      HivCohortQueries hivCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      HivMetadata hivMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.hivCohortQueries = hivCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /**
   * Indicator A
   *
   * @return Nº de pacientes que iniciou TARV nesta unidade sanitária durante o mês
   */
  public CohortDefinition getA() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn = getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addSearch("startedArt", mapStraightThrough(startedArt));
    cd.addSearch(
      "transferredIn",
      map(
          transferredIn,
          "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore+12m},location=${location}"));    
    cd.setCompositionString("startedArt NOT transferredIn");
    return cd;
  }

  /**
   * Indicator B
   *
   * @return Nº de pacientes Transferidos de (+) outras US em TARV durante o mês
   */
  public CohortDefinition getB() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth();
    CompositionCohortDefinition wrap = new CompositionCohortDefinition();
    wrap.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    wrap.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    wrap.addParameter(new Parameter("location", "location", Location.class));
    wrap.addSearch("startedArt", mapStraightThrough(startedArt));
    wrap.addSearch(
        "transferredIn",
        map(
            transferredIn,
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore+12m},location=${location}"));
    wrap.setCompositionString("startedArt AND transferredIn");
    return wrap;
  }

  /**
   * Indicator C
   *
   * @return Nº de pacientes Transferidos para (-) outras US em TARV durante o mês
   */
  public CohortDefinition getC() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();

    CompositionCohortDefinition wrap = new CompositionCohortDefinition();
    wrap.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    wrap.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    wrap.addParameter(new Parameter("location", "location", Location.class));
    wrap.addSearch("startedArt", mapStraightThrough(startedArt));
    wrap.addSearch(
        "transferredOut",
        map(
            transferredOut,
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore+12m},location=${location}"));
    wrap.setCompositionString("startedArt AND transferredOut");

    return wrap;
  }

  /**
   * Indicator D
   *
   * @return ((A+B) - C)
   */
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

  /**
   * Indicator E
   *
   * @return Number of patients who is in the 1st line treatment during the cohort month
   */
  public CohortDefinition getE() {
    CohortDefinition preTarv = getA();
    CohortDefinition transferredIn = getB();
    CohortDefinition transferredOut = getC();
    CohortDefinition suspended = getI();
    CohortDefinition abandoned = getJ();
    CohortDefinition dead = getL();
    CohortDefinition inTheFirstLineOrNull =
        getPatientsWithLastTherapeuticLineEqualsToFirstLineOrNull();

    CompositionCohortDefinition wrapper = new CompositionCohortDefinition();
    wrapper.setParameters(getParameters());
    wrapper.addSearch("preTarv", mapStraightThrough(preTarv));
    wrapper.addSearch("transferredIn", mapStraightThrough(transferredIn));
    wrapper.addSearch("transferredOut", mapStraightThrough(transferredOut));
    wrapper.addSearch("suspended", mapStraightThrough(suspended));
    wrapper.addSearch("abandoned", mapStraightThrough(abandoned));
    wrapper.addSearch("dead", mapStraightThrough(dead));
    wrapper.addSearch("inTheFirstLineOrNull", mapStraightThrough(inTheFirstLineOrNull));

    wrapper.setCompositionString(
        "((preTarv OR transferredIn) NOT (transferredOut AND suspended AND abandoned AND dead)) AND inTheFirstLineOrNull ");
    return wrapper;
  }

  /**
   * Indicator F
   *
   * @return Number of patients in Cohort who completed 12 months ARV treatment in the 1st line
   *     treatment who received one Viral load result
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

  /**
   * Indicator G
   *
   * @return Number of patients who is in the 2nd line treatment during the cohort month
   */
  public CohortDefinition getG() {
    CohortDefinition indicatorA = getA();
    CohortDefinition indicatorB = getB();
    CohortDefinition indicatorC = getC();
    CohortDefinition indicatorI = getI();
    CohortDefinition indicatorJ = getJ();
    CohortDefinition indicatorL = getL();
    CohortDefinition lastSecondTherapeuticLine =
        getPatientsWithLastCodedObsInSecondTherapeuticLineInMasterCardBeforeMonthEndDate();

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
        map(lastSecondTherapeuticLine, "onOrBefore=${onOrBefore},locationList=${location}"));
    comp.setCompositionString(
        "((A OR B) AND NOT (C OR I OR J OR L)) AND lastSecondTherapeuticLine");
    return comp;
  }

  /**
   * Indicator H
   *
   * @return Number of patients in Cohort who completed 12 months ARV treatment in the 2nd line
   *     treatment who received one Viral load result
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

  /**
   * Indicator I
   *
   * @return Number of Suspended patients in the actual cohort
   */
  public CohortDefinition getI() {
    CohortDefinition indicatorA = getA();
    CohortDefinition indicatorB = getB();
    CohortDefinition indicatorC = getC();

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Number of patients with ART suspension during the current month");
    sqlCohortDefinition.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
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
    comp.addSearch("Suspended", mapStraightThrough(sqlCohortDefinition));
    comp.setCompositionString("((A OR B) AND NOT C) AND Suspended");
    return comp;
  }

  /**
   * Indicator J
   *
   * @return Number of Abandoned Patients in the actual cohort
   */
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
   * Fetches Patients with Last registered Line Treatment equals to (1st Line)
   *
   * @return SqlCohortDefinition
   */
  private CohortDefinition getPatientsWithLastTherapeuticLineEqualsToFirstLineOrNull() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients in the first Line of treatment during a period");
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setQuery(
        ResumoTrimestralQueries.getPatientsWithLastTherapeuticLineEqualsToFirstLineOrNull(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getTherapeuticLineConcept().getConceptId(),
            hivMetadata.getFirstLineConcept().getConceptId()));
    return cd;
  }

  /**
   * Indicator L
   *
   * @return Number of Deceased patients in the actual cohort
   */
  public CohortDefinition getL() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition cohortA = getA();
    CohortDefinition cohortB = getB();
    CohortDefinition cohortC = getC();
    CohortDefinition dead = genericCohortQueries.getDeceasedPatients();
    cd.setParameters(getParameters());
    cd.addSearch("A", mapStraightThrough(cohortA));
    cd.addSearch("B", mapStraightThrough(cohortB));
    cd.addSearch("C", mapStraightThrough(cohortC));
    cd.addSearch("dead", mapStraightThrough(dead));
    cd.setCompositionString("((A OR B) AND NOT C) AND dead");
    return cd;
  }

  /**
   * Number Of Patients In Ficha Clinica With Viral Load Result
   *
   * @return CohortDefinition
   */
  public CohortDefinition getNumberOfPatientsInFichaClinicaWithViralLoadResult() {
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
   * All patients with last observation registered in Second Therapeutic Line in Master Card before
   * MonthEndDate
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getPatientsWithLastCodedObsInSecondTherapeuticLineInMasterCardBeforeMonthEndDate() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "End date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getMasterCardEncounterType());
    cd.setTimeModifier(TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getTherapeuticLineConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getSecondLineConcept());
    return cd;
  }

  /**
   * Number of patients transferred-in from another HFs during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth() {

    EptsTransferredInCohortDefinition cd = new EptsTransferredInCohortDefinition();
    cd.setProgramEnrolled(hivMetadata.getHIVCareProgram());
    cd.setProgramEnrolled2(hivMetadata.getARTProgram());
    cd.setPatientState(hivMetadata.getArtCareTransferredFromOtherHealthFacilityWorkflowState());
    cd.setPatientState2(hivMetadata.getArtTransferredFromOtherHealthFacilityWorkflowState());
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setB10Flag(new Boolean("false"));
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
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsTransferredOut() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("transferredOutPatients");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
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

  public EptsQuarterlyCohortDefinition getQuarterlyCohort(
      List<Parameter> getParameters,
      CohortDefinition wrap,
      EptsQuarterlyCohortDefinition.Month month) {
    EptsQuarterlyCohortDefinition cd = new EptsQuarterlyCohortDefinition(wrap, month);
    cd.addParameters(getParameters);
    return cd;
  }
}
