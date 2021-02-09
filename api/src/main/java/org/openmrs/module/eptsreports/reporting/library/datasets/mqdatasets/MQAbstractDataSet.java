package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Date;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.MQAgeDimensions;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public abstract class MQAbstractDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private MQAgeDimensions mQAgeDimensions;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public CohortIndicator setIndicatorWithAllParameters(
      final CohortDefinition cohortDefinition, final String indicatorName, final String mappings) {
    final CohortIndicator indicator =
        this.eptsGeneralIndicator.getIndicator(
            indicatorName, EptsReportUtils.map(cohortDefinition, mappings));

    indicator.addParameter(new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class));
    indicator.addParameter(new Parameter("endInclusionDate", "Data Fim Inclusão", Date.class));
    indicator.addParameter(new Parameter("endRevisionDate", "Data Fim Revisão", Date.class));
    indicator.addParameter(new Parameter("location", "location", Date.class));

    return indicator;
  }

  public void getMQCommonDementions(
      final CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {
    dataSetDefinition.addDimension("gender", map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "ageMqNewART",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsWhoAreNewlyEnrolledOnART(), mappings));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForLastClinicalConsultation(), mappings));

    dataSetDefinition.addDimension(
        "ageOnCV",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsPatientWithCVOver1000Copies(), mappings));

    dataSetDefinition.addDimension(
        "ageMqNewARTRevisionDate",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDate(),
            mappings));

    dataSetDefinition.addDimension(
        "ageOnEndInclusionDate",
        EptsReportUtils.map(this.mQAgeDimensions.getDimensionAgeEndInclusionDate(), mappings));

    dataSetDefinition.addDimension(
        "ageMq",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
  }
}
