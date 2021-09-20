package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsEligileToTPTDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  private static final String FIND_PATIENTS_ELIGIBLE_TPT_LIST =
      "LIST-PATIENTS-ELIGIBLE-TPT/PATIENTS_ELIGIBLE_TPT_LIST.sql";
  private static final String FIND_PATIENTS_ELIGIBLE_TPT_TOTAL =
      "LIST-PATIENTS-ELIGIBLE-TPT/PATIENTS_ELIGIBLE_TPT_TOTAL.sql";

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("Find list of patients who are eligible to TPT");
    dsd.addParameters(list);
    dsd.setSqlQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_ELIGIBLE_TPT_LIST));
    return dsd;
  }

  public DataSetDefinition getTotalEligibleTPTDataset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Patients Eligible TPT Total");
    dataSetDefinition.addParameters(this.getParameters());

    final String mappings = "endDate=${endDate},location=${location}";
    final CohortDefinition tptEligibleTotal = this.findPatientsEligibleTPTTotal();
    dataSetDefinition.addColumn(
        "TOTALELIGIBLETPT",
        "Total de Pacientes Elegiveis a TPT",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "tptEligibleTotal", EptsReportUtils.map(tptEligibleTotal, mappings)),
            mappings),
        "");

    return dataSetDefinition;
  }

  @DocumentedDefinition(value = "findPatientsEligibleTPTTOTAL")
  private CohortDefinition findPatientsEligibleTPTTotal() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findPatientsEligibleTPTT");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_ELIGIBLE_TPT_TOTAL));

    return definition;
  }
}
