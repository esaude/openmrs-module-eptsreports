package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsTransferredInCohortDefinition2;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoMensalTransferredOutCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonCohortQueries {

  private HivMetadata hivMetadata;

  private TbMetadata tbMetadata;

  @Autowired
  public CommonCohortQueries(HivMetadata hivMetadata, TbMetadata tbMetadata) {
    this.hivMetadata = hivMetadata;
    this.tbMetadata = tbMetadata;
  }

  /** 9. Patients on TB Treatment */
  public CohortDefinition getPatientsOnTbTreatment() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsOnTbTreatment");
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        CommonQueries.getPatientsOnTbTreatmentQuery(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getTBDrugStartDateConcept().getConceptId(),
            hivMetadata.getTBDrugEndDateConcept().getConceptId(),
            hivMetadata.getTBProgram().getProgramId(),
            hivMetadata.getPatientActiveOnTBProgramWorkflowState().getProgramWorkflowStateId(),
            hivMetadata.getActiveTBConcept().getConceptId(),
            hivMetadata.getYesConcept().getConceptId(),
            hivMetadata.getTBTreatmentPlanConcept().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            tbMetadata.getPulmonaryTB().getConceptId()));

    return cd;
  }

  /** 15 MOH Transferred-in patients */
  public CohortDefinition getMohTransferredInPatients() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    addParameters(cd);

    EptsTransferredInCohortDefinition2 transferredInCurrentPeriod =
        new EptsTransferredInCohortDefinition2();
    addParameters(transferredInCurrentPeriod);
    transferredInCurrentPeriod.addArtProgram(EptsTransferredInCohortDefinition2.ARTProgram.TARV);

    EptsTransferredInCohortDefinition2 transferredInPreviousMonth =
        new EptsTransferredInCohortDefinition2();
    transferredInPreviousMonth.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    transferredInPreviousMonth.addParameter(new Parameter("location", "Location", Location.class));
    transferredInPreviousMonth.addArtProgram(EptsTransferredInCohortDefinition2.ARTProgram.TARV);

    ResumoMensalTransferredOutCohortDefinition transferredOutPreviousMonth =
        getMohTransferredOutPatientsByEndOfPeriod();

    cd.addSearch("current", mapStraightThrough(transferredInCurrentPeriod));
    String byEndOfPreviousExclusive = "onOrBefore=${onOrAfter-1d},location=${location}";
    cd.addSearch("previous", map(transferredInPreviousMonth, byEndOfPreviousExclusive));
    String byEndOfPrevious = "onOrBefore=${onOrAfter},location=${location}";
    cd.addSearch("transferredOut", map(transferredOutPreviousMonth, byEndOfPrevious));
    cd.setCompositionString("current NOT (previous NOT transferredOut)");

    return cd;
  }

  /** 16 MOH Transferred-out patients by end of the reporting period (Last State) */
  public ResumoMensalTransferredOutCohortDefinition getMohTransferredOutPatientsByEndOfPeriod() {
    ResumoMensalTransferredOutCohortDefinition transferredOutPreviousMonth =
        new ResumoMensalTransferredOutCohortDefinition();
    transferredOutPreviousMonth.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    transferredOutPreviousMonth.addParameter(new Parameter("location", "Location", Location.class));
    transferredOutPreviousMonth.setMaxDates(true);
    return transferredOutPreviousMonth;
  }

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("onOrBefore", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }
}
