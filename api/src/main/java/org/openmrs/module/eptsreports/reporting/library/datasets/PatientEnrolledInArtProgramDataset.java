package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PatientEnrolledInArtProgramCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientEnrolledInArtProgramDataset extends BaseDataSet {
  @Autowired
  private PatientEnrolledInArtProgramCohortQueries patientEnrolledInArtProgramCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  public DataSetDefinition constructPatientEnrolledOnAetProgramDataset() {
    CohortIndicatorDataSetDefinition dataSetDefinition = new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setName("Patient Enrolled On ART Program");
    dataSetDefinition.addParameters(getParameters());
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    CohortDefinition patientEnrolledInArtProgram =
        this.patientEnrolledInArtProgramCohortQueries
            .findPatientEnrolledInArtProgramOnReportingPeriod();
    CohortIndicator patientEnrolledInArtProgramIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientEnrolledInrArtProgram",
            EptsReportUtils.map(patientEnrolledInArtProgram, mappings));
    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dataSetDefinition.addColumn(
        "TOTAL",
        "PAtients Enrolled In Art Program: total",
        EptsReportUtils.map(patientEnrolledInArtProgramIndicator, mappings),
        "");
    dataSetDefinition.addColumn(
        "male",
        "Male: Enrolled in Art Program",
        EptsReportUtils.map(patientEnrolledInArtProgramIndicator, mappings),
        "gender=M");
    dataSetDefinition.addColumn(
        "female",
        "Felame: Enrolled in Art Program",
        EptsReportUtils.map(patientEnrolledInArtProgramIndicator, mappings),
        "gender=F");
    return dataSetDefinition;
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
