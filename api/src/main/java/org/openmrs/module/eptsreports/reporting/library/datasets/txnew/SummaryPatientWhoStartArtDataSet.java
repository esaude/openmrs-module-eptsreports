package org.openmrs.module.eptsreports.reporting.library.datasets.txnew;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PatientsInARTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryPatientWhoStartArtDataSet extends BaseDataSet {

  @Autowired private PatientsInARTCohortQueries patientsInARTCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  public DataSetDefinition getTotaStartARTDataset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("Pacientes Que Iniciaram TARV");
    dataSetDefinition.addParameters(getParametersNewART());

    dataSetDefinition.addParameter(new Parameter("evaluationDate", "Evaluation Date", Date.class));

    final String mappings =
        "startDate=${startDate},endDate=${endDate},evaluationDate=${evaluationDate},location=${location}";

    final CohortDefinition vlEligibleTotal =
        this.patientsInARTCohortQueries.findPatientsWhoInARTTotal();

    dataSetDefinition.addColumn(
        "TL",
        "Pacientes Que Iniciaram TARV",
        EptsReportUtils.map(
            setParams(
                this.eptsGeneralIndicator.getIndicator(
                    "Pacientes Que Iniciaram TARV", EptsReportUtils.map(vlEligibleTotal, mappings)),
                mappings),
            mappings),
        "");

    return dataSetDefinition;
  }

  private CohortIndicator setParams(CohortIndicator cohortIndicator, String mappings) {

    cohortIndicator.addParameter(new Parameter("startDate", "Cohort Start Date", Date.class));
    cohortIndicator.addParameter(new Parameter("endDate", "Cohort End Date", Date.class));
    cohortIndicator.addParameter(new Parameter("evaluationDate", "Evaluation Date", Date.class));
    cohortIndicator.addParameter(new Parameter("location", "location", Location.class));

    return cohortIndicator;
  }
}
