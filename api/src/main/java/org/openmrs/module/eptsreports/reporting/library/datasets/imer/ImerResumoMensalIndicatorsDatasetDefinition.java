package org.openmrs.module.eptsreports.reporting.library.datasets.imer;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoMensalCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.disaggregations.ResumoMensalAandBdisaggregations;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ImerResumoMensalIndicatorsDatasetDefinition extends BaseDataSet {
  private EptsCommonDimension eptsCommonDimension;

  private ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations;

  private ResumoMensalCohortQueries resumoMensalCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired
  public ImerResumoMensalIndicatorsDatasetDefinition(
      EptsCommonDimension eptsCommonDimension,
      ResumoMensalAandBdisaggregations resumoMensalAandBdisaggregations,
      ResumoMensalCohortQueries resumoMensalCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator) {
    this.eptsCommonDimension = eptsCommonDimension;
    this.resumoMensalAandBdisaggregations = resumoMensalAandBdisaggregations;
    this.resumoMensalCohortQueries = resumoMensalCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
  }

  public DataSetDefinition constructImerResumoMensalIndicatorsDataset() {

    CohortIndicatorDataSetDefinition cd = new CohortIndicatorDataSetDefinition();

    cd.setName("Resumo Mensal Indicators for IM-ER Report");

    cd.setParameters(getParameters());

    cd.addDimension("gender", map(eptsCommonDimension.gender(), ""));

    cd.addDimension(
        "age", map(eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    String mappings = "endDate=${endDate},location=${location}";

    CohortIndicator resumoMensalIndicator =
        eptsGeneralIndicator.getIndicator(
            "Resumo Mensal B13",
            Mapped.mapStraightThrough(
                resumoMensalCohortQueries.getPatientsWhoWereActiveByEndOfMonthB13()));

    // Resumo Mensal - B13 Indicators
    cd.addColumn(
        "B13TP",
        "Total patients - Total Geral",
        EptsReportUtils.map(resumoMensalIndicator, mappings),
        "");

    addRow(
        cd,
        "B13TA",
        "Patients over 15 years - adults",
        EptsReportUtils.map(resumoMensalIndicator, mappings),
        resumoMensalAandBdisaggregations.getAdultPatients());

    addRow(
        cd,
        "B13TAD",
        "Adolescentes patients",
        EptsReportUtils.map(resumoMensalIndicator, mappings),
        resumoMensalAandBdisaggregations.getAdolescentesColumns());

    addRow(
        cd,
        "B13TC",
        "Patients under 15 years",
        EptsReportUtils.map(resumoMensalIndicator, mappings),
        resumoMensalAandBdisaggregations.getUnder14YearsColumns());

    return cd;
  }
}
