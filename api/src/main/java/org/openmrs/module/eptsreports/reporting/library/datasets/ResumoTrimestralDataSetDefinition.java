package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.CohortIndicator;
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
    dsd.addColumn("A", A, getInitiatedArt(), "");
    return dsd;
  }

  private Mapped<CohortIndicator> getInitiatedArt() {
    CohortDefinition cd = getInitiatedArtA();
    CohortIndicator indicator =
        eptsGeneralIndicator.getIndicator(A, mapStraightThrough(cd), getParameters());
    return mapStraightThrough(indicator);
  }

  public EptsQuarterlyCohortDefinition getInitiatedArtA() {
    EptsQuarterlyCohortDefinition cd = new EptsQuarterlyCohortDefinition();
    cd.setCohortDefinition(genericCohortQueries.getStartedArtOnPeriod(false, true));
    cd.addParameter(new Parameter("year", "Year", Integer.class));
    cd.addParameter(
        new Parameter("quarter", "Quarter", EptsQuarterlyCohortDefinition.Quarter.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    return cd;
  }

  @Override
  public List<Parameter> getParameters() {
    ArrayList<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("year", "Year", Integer.class));
    parameters.add(
        new Parameter("quarter", "Quarter", EptsQuarterlyCohortDefinition.Quarter.class));
    parameters.add(new Parameter("location", "Location", Location.class));
    return parameters;
  }
}
