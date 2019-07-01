package org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataQualityOverallCohorts {

  private GenericCohortQueries genericCohortQueries;

  private TxNewCohortQueries txNewCohortQueries;

  @Autowired
  public DataQualityOverallCohorts(
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
}
