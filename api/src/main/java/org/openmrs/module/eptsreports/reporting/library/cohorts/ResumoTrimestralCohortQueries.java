package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralCohortQueries {

  private GenericCohortQueries genericCohortQueries;
  private HivCohortQueries hivCohortQueries;
  private HivMetadata hivMetadata;

  @Autowired
  public ResumoTrimestralCohortQueries(
      GenericCohortQueries genericCohortQueries,
      HivCohortQueries hivCohortQueries,
      HivMetadata hivMetadata) {
    this.genericCohortQueries = genericCohortQueries;
    this.hivCohortQueries = hivCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /** @return Nº de pacientes que iniciou TARV nesta unidade sanitária durante o mês */
  public CohortDefinition getA() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        hivCohortQueries.getPatientsTransferredFromOtherHealthFacility();
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addSearch("startedArt", mapStraightThrough(startedArt));
    cd.addSearch("transferredIn", mapStraightThrough(transferredIn));
    cd.setCompositionString("startedArt NOT transferredIn");
    return cd;
  }

  /** @return Nº de pacientes Transferidos de (+) outras US em TARV durante o mês */
  public CohortDefinition getB() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredIn =
        hivCohortQueries.getPatientsTransferredFromOtherHealthFacility();
    CompositionCohortDefinition wrap = new CompositionCohortDefinition();
    wrap.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    wrap.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    wrap.addParameter(new Parameter("location", "location", Location.class));
    wrap.addSearch("startedArt", mapStraightThrough(startedArt));
    wrap.addSearch("transferredIn", mapStraightThrough(transferredIn));
    wrap.setCompositionString("startedArt AND transferredIn");
    return wrap;
  }

  /** @return Nº de pacientes Transferidos para (-) outras US em TARV durante o mês */
  public CohortDefinition getC() {
    CohortDefinition startedArt = genericCohortQueries.getStartedArtOnPeriod(false, true);
    CohortDefinition transferredOut = hivCohortQueries.getPatientsTransferredOut();
    CompositionCohortDefinition wrap = new CompositionCohortDefinition();
    wrap.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    wrap.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    wrap.addParameter(new Parameter("location", "location", Location.class));
    wrap.addSearch("startedArt", mapStraightThrough(startedArt));
    wrap.addSearch("transferredOut", mapStraightThrough(transferredOut));
    wrap.setCompositionString("startedArt AND transferredOut");
    return wrap;
  }

  /** @return Number of patients who is in the 1st line treatment during the cohort month */
  public CohortDefinition getE() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  /**
   * @return Number of patients in Cohort who completed 12 months ARV treatment in the 1st line
   *     treatment who received one Viral load result
   */
  public CohortDefinition getF() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  /** @return Number of patients who is in the 2nd line treatment during the cohort month */
  public CohortDefinition getG() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  /**
   * @return Number of patients in Cohort who completed 12 months ARV treatment in the 2nd line
   *     treatment who received one Viral load result
   */
  public CohortDefinition getH() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  /** @return Number of Suspended patients in the actual cohort */
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

  /** @return Number of Abandoned Patients in the actual cohort */
  public CohortDefinition getJ() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  /** @return Number of Deceased patients in the actual cohort */
  public CohortDefinition getL() {
    AllPatientsCohortDefinition cd = new AllPatientsCohortDefinition();
    cd.setParameters(getParameters());
    return cd;
  }

  private List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("onOrAfter", "Start date", Date.class),
        new Parameter("onOrBefore", "End date", Date.class),
        new Parameter("location", "Location", Location.class));
  }
}
