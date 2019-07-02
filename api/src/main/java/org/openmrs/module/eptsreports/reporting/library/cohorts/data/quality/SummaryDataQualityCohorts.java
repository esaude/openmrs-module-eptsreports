package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.BaseQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityCohorts {

  private GenericCohortQueries genericCohortQueries;

  private TxNewCohortQueries txNewCohortQueries;

  @Autowired
  public SummaryDataQualityCohorts(
      GenericCohortQueries genericCohortQueries, TxNewCohortQueries txNewCohortQueries) {
    this.genericCohortQueries = genericCohortQueries;
    this.txNewCohortQueries = txNewCohortQueries;
  }

  /** Get all the patients in the system */
  public CohortDefinition getAllPatients() {
    return genericCohortQueries.generalSql("All", "SELECT patient_id FROM patient");
  }

  /**
   * Non pregnant and non breastfeeding patients
   *
   * @return Cohort Definition
   */
  public CohortDefinition getNonPreganatAndNonBreastfeedingPatients() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Non pregnant and non breastfeeding patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("All", EptsReportUtils.map(getAllPatients(), ""));
    cd.addSearch(
        "Pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "Breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("All AND NOT(Pregnant OR Breastfeeding)");
    return cd;
  }

  /**
   * Note:<all, active&transferred in, abandoned, transferred out>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getQualityDataReportBaseCohort() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patient States");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Facilities", Location.class, List.class, null));
    cd.addParameter(new Parameter("state", "States", ProgramWorkflowState.class, List.class, null));
    cd.setQuery(BaseQueries.getBaseQueryForDataQuality());
    return cd;
  }
}
