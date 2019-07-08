package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonColumns.addStandardColumns;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PregnantCriteriaCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.data.quality.PregnantEnrollmentStatusCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.converter.CalculationResultDataConverter;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** A dataset to display list of patients who are pregnant and male */
@Component
public class Ec1PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  @Autowired
  public Ec1PatientListDataset(SummaryDataQualityCohorts summaryDataQualityCohorts) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
  }

  public DataSetDefinition ec1DataSetDefinition() {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC1");
    dsd.addParameters(getDataQualityParameters());
    dsd.addRowFilter(summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}");

    addStandardColumns(dsd);
    dsd.addColumn(
        "Pregnant Criteria",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PC"));
    dsd.addColumn(
        "Encounter Date Pregnant Criteria",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Date",
        getPregnantCriteria(),
        "location=${location}",
        new CalculationResultDataConverter("PTVD"));
    dsd.addColumn(
        "PTV/ETC Enrollment Status",
        getPregnancyStatus(),
        "location=${location}",
        new CalculationResultDataConverter("State"));

    return dsd;
  }

  private DataDefinition getPregnantCriteria() {
    CalculationDataDefinition pCriteria =
        new CalculationDataDefinition(
            "Pregnant Criteria",
            Context.getRegisteredComponents(PregnantCriteriaCalculation.class).get(0));
    pCriteria.addParameter(new Parameter("location", "Location", Location.class));
    pCriteria.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pCriteria;
  }

  private DataDefinition getPregnancyStatus() {
    CalculationDataDefinition pStatus =
        new CalculationDataDefinition(
            "Pregnant Status",
            Context.getRegisteredComponents(PregnantEnrollmentStatusCalculation.class).get(0));
    pStatus.addParameter(new Parameter("location", "Location", Location.class));
    pStatus.addParameter(new Parameter("onOrBefore", "Before date", Date.class));
    return pStatus;
  }
}
