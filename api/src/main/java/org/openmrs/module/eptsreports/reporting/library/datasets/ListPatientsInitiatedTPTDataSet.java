package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Date;
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
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListPatientsInitiatedTPTDataSet extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  private static final String FIND_PATIENTS_WHO_INITIATED_TPT_LIST =
      "LIST-PATIENTS-INITIATED-TPT/PATIENTS_INITIATED_TPT_LIST.sql";
  private static final String FIND_PATIENTS_WHO_INITIATED_TPT_TOTAL =
      "LIST-PATIENTS-INITIATED-TPT/PATIENTS_INITIATED_TPT_TOTAL.sql";

  public DataSetDefinition constructListDataset() {
    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("INICIOTPI");
    dsd.addParameters(super.getParameters());
    dsd.setSqlQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_WHO_INITIATED_TPT_LIST));
    return dsd;
  }

  public DataSetDefinition constructTotalDataset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("INICIO TPT data Set - Total");
    dataSetDefinition.addParameters(super.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition tptTotal = this.finPatientsWhoInitieted3hp();

    final CohortIndicator tpt =
        this.eptsGeneralIndicator.getIndicator("tptTotal", EptsReportUtils.map(tptTotal, mappings));

    dataSetDefinition.addColumn("INICIO", "INICIO", EptsReportUtils.map(tpt, mappings), "");

    return dataSetDefinition;
  }

  @DocumentedDefinition(value = "finPatientsWhoInitieted3hp")
  private CohortDefinition finPatientsWhoInitieted3hp() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("finPatientsWhoInitieted3hp");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_WHO_INITIATED_TPT_TOTAL));

    return definition;
  }
}
