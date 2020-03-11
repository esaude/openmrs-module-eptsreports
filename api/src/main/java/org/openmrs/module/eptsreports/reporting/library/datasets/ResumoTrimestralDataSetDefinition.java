package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;
import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.cohort.definition.AllPatientsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralDataSetDefinition extends BaseDataSet {

  public static final String A =
      "Nº de pacientes que iniciou TARV nesta unidade sanitária durante o mês";

  private EptsGeneralIndicator eptsGeneralIndicator;

  private GenericCohortQueries genericCohortQueries;

  @Autowired
  public ResumoTrimestralDataSetDefinition(
      EptsGeneralIndicator eptsGeneralIndicator, GenericCohortQueries genericCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.genericCohortQueries = genericCohortQueries;
  }

  public DataSetDefinition constructResumoTrimestralDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Resumo trimestral data set");
    dsd.addParameters(getParameters());
    dsd.addDimension("month", mapStraightThrough(getADimension()));
    addRow(dsd, "A", "A", getAllPatientsIndicator(), getColumns());
    return dsd;
  }

  /**
   * @return Return an indicator of all patients. Make sure the report definition uses a base
   *     cohort.
   */
  private Mapped<CohortIndicator> getAllPatientsIndicator() {
    CohortDefinition cd = new AllPatientsCohortDefinition();
    CohortIndicator indicator =
        eptsGeneralIndicator.getIndicator(A, mapStraightThrough(cd), getParameters());
    return mapStraightThrough(indicator);
  }

  private CohortDefinitionDimension getADimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(ReportingConstants.START_DATE_PARAMETER);
    dim.addParameter(ReportingConstants.END_DATE_PARAMETER);
    dim.addParameter(ReportingConstants.LOCATION_PARAMETER);
    CohortDefinition cd = genericCohortQueries.getStartedArtOnPeriod(false, true);
    dim.addCohortDefinition(
        "m1", map(cd, "onOrAfter=${startDate},onOrBefore=${endDate-2m},location=${location}"));
    dim.addCohortDefinition(
        "m2", map(cd, "onOrAfter=${startDate+1m},onOrBefore=${endDate-1m},location=${location}"));
    dim.addCohortDefinition(
        "m3", map(cd, "onOrAfter=${startDate+2m},onOrBefore=${endDate},location=${location}"));
    return dim;
  }

  private List<ColumnParameters> getColumns() {
    ArrayList<ColumnParameters> cols = new ArrayList<>();
    cols.add(new ColumnParameters("m1", "First month of quarter", "month=m1", "01"));
    cols.add(new ColumnParameters("m2", "Second month of quarter", "month=m2", "02"));
    cols.add(new ColumnParameters("m3", "Third month of quarter", "month=m3", "03"));
    return cols;
  }
}
