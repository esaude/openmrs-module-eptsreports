package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2.ARTProgram;
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
  private CommonCohortQueries commonCohortQueries;

  @Autowired
  public ResumoTrimestralCohortQueries(
      GenericCohortQueries genericCohortQueries,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      HivMetadata hivMetadata,
      CommonCohortQueries commonCohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonCohortQueries = commonCohortQueries;
  }

  /**
   * <b>Name: A -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients who started ART at this Health Facility during the
   * month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getA() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn = commonCohortQueries.getMohTransferredInPatients();
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

  /**
   * <b>Name: B -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients transferred-In (+) other HF's on ART during the month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getB() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn = commonCohortQueries.getMohTransferredInPatients();
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

  /**
   * <b>Name: C -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients transferred-Out (-) other HF's on ART during the
   * month
   *
   * @return {@link CohortDefinition}
   */
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

  /**
   * <b>Name: Indicator - D </b> - ((A+B) - C)
   *
   * <p><b>Description:</b> Number of patients who initiated ART And were Not Transferred-Out (-)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getD() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredOut", map(transferredOut, less12m));
    cd.setCompositionString("startedArt NOT transferredOut");

    return cd;
  }

  /**
   * <b>Name: E -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients who is in the 1st line treatment during the cohort
   * month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getE() {

    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();
    CohortDefinition indicatorI = getI();
    CohortDefinition indicatorJ = getJ();
    CohortDefinition indicatorL = getL();
    CohortDefinition lastFirstTherapeuticLine =
        getPatientsWithLastTherapeuticLineEqualsToFirstLineOrWithoutInformation();
    CompositionCohortDefinition wrapper = new CompositionCohortDefinition();
    wrapper.setParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    wrapper.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    wrapper.addSearch("transferredOut", map(transferredOut, less12m));

    wrapper.addSearch("I", mapStraightThrough(indicatorI));
    wrapper.addSearch("J", mapStraightThrough(indicatorJ));
    wrapper.addSearch("L", mapStraightThrough(indicatorL));
    wrapper.addSearch("lastFirstTherapeuticLine", mapStraightThrough(lastFirstTherapeuticLine));

    wrapper.setCompositionString(
        "((startedArt NOT transferredOut) NOT (I OR J OR L)) AND lastFirstTherapeuticLine ");
    return wrapper;
  }

  /**
   * <b>Name: F -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients in Cohort who completed 12 months ARV treatment in
   * the 1st line treatment who received one Viral load result
   *
   * @return {@link CohortDefinition}
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
   * <b>Name: G -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients who is in the 2nd line treatment during the cohort
   * month
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getG() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();
    CohortDefinition indicatorI = getI();
    CohortDefinition indicatorJ = getJ();
    CohortDefinition indicatorL = getL();
    CohortDefinition lastSecondTherapeuticLine =
        getPatientsWithLastObsInSecondTherapeuticLineInMasterCardFichaClinicaBeforeMonthEndDate();

    CompositionCohortDefinition comp = new CompositionCohortDefinition();
    comp.setParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    comp.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    comp.addSearch("transferredOut", map(transferredOut, less12m));

    comp.addSearch("I", mapStraightThrough(indicatorI));
    comp.addSearch("J", mapStraightThrough(indicatorJ));
    comp.addSearch("L", mapStraightThrough(indicatorL));
    comp.addSearch(
        "lastSecondTherapeuticLine",
        map(lastSecondTherapeuticLine, "endDate=${onOrBefore},location=${location}"));
    comp.setCompositionString(
        "((startedArt NOT transferredOut) AND NOT (I OR J OR L)) AND lastSecondTherapeuticLine");
    return comp;
  }

  /**
   * <b>Name: H -</b> Indicator
   *
   * <p><b>Description:</b> Number of patients in Cohort who completed 12 months ARV treatment in
   * the 2nd line treatment who received one Viral load result
   *
   * @return {@link CohortDefinition}
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
   * <b>Name: I -</b>Indicator
   *
   * <p><b>Description:</b> Number of Suspended patients in the actual cohort
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * 1.1 - Registered in Patient Program State by reporting end date <b>Patient_program.program_id =
   * 2 = SERVICO TARV-TRATAMENTO</b> AND <b>Patient_State.state = 8 (Suspended treatment)</b> OR
   * Patient_State.start_date <= endDate AND Patient_state.end_date is null
   *
   * <p>1.2 - Registered in Ficha Resumo and Ficha Clinica of MasterCard by reporting end date
   * <b>(EncounterType_id = 6)</b> Estado de Permanencia <b>(concept_id 6273)</b> = Suspended
   * <b>(concept_id = 1709)</b> Encounter_datetime <= endDate
   *
   * <p>Registered in Ficha Resumo and Ficha Clinica of MasterCard <b>(encounterType_id = 53)</b>
   * Estado de Permanencia <b>(concept_id = 6272)</b> = Suspended <b>(concept_id = 1709)</b>
   * obs_datetime <= endDate
   *
   * <p>1.3 - Except all patients who after the most recent date from <b>1.1 to 1.2</b> have a drugs
   * pick up or consultation <b>EncounterType ID= 6, 9, 18</b> and  encounter_datetime> the most
   * recent date and <=endDate or Encounter Type ID = 52 and “Data de Levantamento” <b>(Concept Id
   * 23866 value_datetime)</b> > the most recent date and <= endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getI() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();

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
    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    comp.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    comp.addSearch("transferredOut", map(transferredOut, less12m));

    comp.addSearch("Suspended", map(sqlCohortDefinition, less12m));

    comp.setCompositionString("(startedArt NOT transferredOut) AND Suspended");
    return comp;
  }

  /**
   * <b>Name: J -</b> Indicator
   *
   * <p><b>Description: </b>Number of Abandoned Patients in the actual cohort
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getJ() {
    CohortDefinition abandoned =
        resumoMensalCohortQueries.getNumberOfPatientsWhoAbandonedArtDuringPreviousMonthForB7();
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setParameters(getParameters());

    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredOut", map(transferredOut, less12m));

    cd.addSearch("abandoned", map(abandoned, "date=${onOrBefore},location=${location}"));
    cd.addSearch("I", mapStraightThrough(getI()));
    cd.addSearch("L", mapStraightThrough(getL()));
    cd.setCompositionString("(startedArt NOT transferredOut) AND abandoned NOT (I OR L)");

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Fetches Patients with Last registered Line Treatment equals to (1st Line (<b>concept_id =
   * 21150</b>)) or without information regarding Therapeutic Line <b>(concept_id = 21151)</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Name: L -</b> Indicator
   *
   * <p><b>Description:</b> Number of Deceased patients in the actual cohort
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getL() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = getPatientsTransferredOut();
    CohortDefinition dead = getDeceasedPatients();
    cd.setParameters(getParameters());
    String yearBefore =
        "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore-12m},location=${location}";
    cd.addSearch("startedArt", map(startedArt, yearBefore));

    String less12m = "onOrAfter=${onOrAfter-12m},onOrBefore=${onOrBefore},location=${location}";
    cd.addSearch("transferredOut", map(transferredOut, less12m));
    cd.addSearch("dead", map(dead, less12m));
    cd.setCompositionString("(startedArt NOT transferredOut) AND dead");
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

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * Number Of Patients In Ficha Clinica <b>(encounterType_id = 6)</b> With Viral Load Result
   * <b>(concept_id = 1035)</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients with last registered Line Treatment (PT: “Linha Terapeutica” <b>(concept_id =
   * 21151)</b>) equal to “Second Line” (PT: “Segunda Linha” <b>(concept_id = 21148)</b>)
   *
   * <p>And encounter date < = MonthEndDate in encounter “Master Card – Ficha Clinica”
   * <b>(encounterType_id = 6)</b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
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

  /**
   * <b>Description:</b> Number of patients transferred-in from another HFs during the current month
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonth() {

    EptsTransferredInCohortDefinition2 cd = new EptsTransferredInCohortDefinition2();
    cd.addArtProgram(ARTProgram.TARV);
    cd.addParameters(getParameters());
    return cd;
  }

  private List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("onOrAfter", "Start date", Date.class),
        new Parameter("onOrBefore", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }

  /**
   * <b>Description:</b> Number of transferred-out patients in TARV at this HF during the cohort
   * month.
   *
   * <p>This is the same as {@link HivCohortQueries#getPatientsTransferredOut()} except that it
   * filters for both {@code onOrAfter} and {@code onOrBefore}.
   *
   * <p>It was duplicated here because the spec pointed to an incompatible query
   *
   * @return {@link CohortDefinition}
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
   * <b>Description:</b> Deceased patients
   *
   * <p>Copied from {@link GenericCohortQueries#getDeceasedPatients()} because it was not compatible
   * with current specs.
   *
   * <p>Should be removed after refactoring common queries.
   *
   * @return {@link CohortDefinition}
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
