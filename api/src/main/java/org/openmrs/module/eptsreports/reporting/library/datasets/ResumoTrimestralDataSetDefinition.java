package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.cohort.definition.EptsQuarterlyCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.ResumoTrimestralCohortQueries;
import org.openmrs.module.reporting.ReportingConstants;
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

  private static final String NO_DIMENSION_OPTIONS = "";

  public static final String A =
      "Nº de pacientes que iniciou TARV nesta unidade sanitária durante o mês";

  private static final String B =
      "Nº de pacientes Transferidos de (+) outras US em TARV durante o mês";

  private static final String C =
      "Nº de pacientes Transferidos para (-) outras US em TARV durante o mês";

  private static final String D = "Actual Cohort during the month((A+B) - C) - Total";

  private static final String E =
      "Nº de pacientes na Coorte actual que continuam na 1ª Linha de Tratamento";

  private static final String F =
      "Nº de pacientes na Coorte dos que completaram 12 meses de TARV na 1ª Linha que receberam um resultado de Carga Viral";

  private static final String G =
      "Nº de pacientes na Coorte actual que continuam na 2ª Linha de Tratamento";

  private static final String H =
      "Nº de pacientes na Coorte dos que completaram 12 meses de TARV na 2ª Linha que receberam um resultado de Carga Viral";

  private static final String I = "Nº de pacientes com suspensão na coorte actual";

  private static final String J = "Nº de pacientes com Abandono na coorte atual";

  private static final String L = "Nº de pacientes óbitos na coorte atual";

  private ResumoTrimestralCohortQueries resumoTrimestralCohortQueries;

  @Autowired
  public ResumoTrimestralDataSetDefinition(
      ResumoTrimestralCohortQueries resumoTrimestralCohortQueries) {
    this.resumoTrimestralCohortQueries = resumoTrimestralCohortQueries;
  }

  public DataSetDefinition constructResumoTrimestralDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Resumo trimestral data set");
    dsd.addParameters(getParameters());
    dsd.addColumn("Am1", A, getA(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Am2", A, getA(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Am3", A, getA(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Bm1", B, getB(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Bm2", B, getB(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Bm3", B, getB(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Cm1", C, getC(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Cm2", C, getC(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Cm3", C, getC(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn(
        "Dm1", D + " M1", getD(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn(
        "Dm2", D + " M2", getD(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn(
        "Dm3", D + " M3", getD(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Em1", E, getE(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Em2", E, getE(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Em3", E, getE(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Fm1", F, getF(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Fm2", F, getF(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Fm3", F, getF(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Gm1", G, getG(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Gm2", G, getG(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Gm3", G, getG(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Hm1", H, getH(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Hm2", H, getH(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Hm3", H, getH(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Im1", I, getI(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Im2", I, getI(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Im3", I, getI(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Jm1", J, getJ(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Jm2", J, getJ(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Jm3", J, getJ(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Lm1", L, getL(EptsQuarterlyCohortDefinition.Month.M1), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Lm2", L, getL(EptsQuarterlyCohortDefinition.Month.M2), NO_DIMENSION_OPTIONS);
    dsd.addColumn("Lm3", L, getL(EptsQuarterlyCohortDefinition.Month.M3), NO_DIMENSION_OPTIONS);
    return dsd;
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("year", "Year", Integer.class));
    parameters.add(
        new Parameter("quarter", "Quarter", EptsQuarterlyCohortDefinition.Quarter.class));
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  private Mapped<CohortIndicator> getA(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition a = resumoTrimestralCohortQueries.getA();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(a, month, getParameters());
    return mapStraightThrough(getCohortIndicator(A, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getB(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getB();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(B, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getC(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getC();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(C, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getD(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getD();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(D, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getE(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getE();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(E, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getF(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getF();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(F, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getG(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getG();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(G, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getH(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getH();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(H, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getI(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getI();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(I, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getJ(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getJ();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(J, mapStraightThrough(quarterly)));
  }

  private Mapped<CohortIndicator> getL(EptsQuarterlyCohortDefinition.Month month) {
    CohortDefinition wrap = resumoTrimestralCohortQueries.getL();
    CohortDefinition quarterly =
        resumoTrimestralCohortQueries.getQuarterlyCohort(wrap, month, getParameters());
    return mapStraightThrough(getCohortIndicator(L, mapStraightThrough(quarterly)));
  }

  private CohortIndicator getCohortIndicator(String name, Mapped<CohortDefinition> cohort) {
    CohortIndicator indicator = new CohortIndicator(name);
    indicator.setCohortDefinition(cohort);
    indicator.addParameters(getParameters());
    return indicator;
  }
}
