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

  /**
   * <b>Description:</b> Number of patients who are on TB treatment
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Patients who in patient clinical record for ART in follow-up, adults and children
   * <b>(encounterType_id = 6 or 9)</b> have a TB Start Date <b>((obs concept_id = 1113)</b> >
   * (reporting_end_date - 6 months) and <= reporting_end_date)
   *
   * <p>Have TB treatment End Date <b>((concept_id = 6120)</b> null or > reporting_end_date)
   *
   * <p>Enrolled in TB program <b>(program_id = 5 and patient_state_id =6269)</b>, with Start_date
   * >= (reporting end date - 6 months) and <= reporting end date and endDate is null or is >
   * reporting end date
   *
   * <p>Active TB <b>(concept_id = 23761)</b> value_coded "Yes <b>(concept_id = 1065)</b>" or
   * treatment plan in ficha clinica MasterCard and encounter_datetime between reporting_end_date
   *
   * <p>Marked LAST TB Treatment Plan <b>(concept_id = 1268)</b> with valued_coded "start Drugs"
   * <b>(concept_id = 1256)</b> or continue regimen (concept_id = 1258)</b> and LAST Date <b>(obs
   * datetime)>= (reporting_end_date -6 months) and <= reporting_end_date</b>
   *
   * <p>Pulmonary TB <b>(obs concept_id = 42)</b> with value_coded "Yes" in ficha resumo -
   * mastercard <b>(encounterType_id = 53)</b> and <b>(obs_datetime) >=(reporting_end_date - 6
   * monts) and < = reporting_end_date
   *
   * <p></b>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
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
