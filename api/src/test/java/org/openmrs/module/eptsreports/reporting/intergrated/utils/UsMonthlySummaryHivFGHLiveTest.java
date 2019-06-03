package org.openmrs.module.eptsreports.reporting.intergrated.utils;

import static org.junit.Assert.assertThat;

import java.util.Date;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.UsMonthlySummaryHivDataset;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class UsMonthlySummaryHivFGHLiveTest extends DefinitionsFGHLiveTest {

  @Autowired UsMonthlySummaryHivDataset usMonthlySummaryHivDataset;
  @Autowired private GenericCohortQueries genericCohortQueries;

  @Override
  protected Date getStartDate() {
    return DateUtil.getDateTime(2018, 6, 21);
  }

  @Override
  protected Date getEndDate() {
    return DateUtil.getDateTime(2018, 7, 20);
  }

  @Override
  protected Location getLocation() {
    return Context.getLocationService().getLocation(6);
  }

  @Before
  public void setUp() throws Exception {}

  @Test
  public void test() throws EvaluationException {
    DataSetDefinition dataSetDefinition =
        usMonthlySummaryHivDataset.constructUsMonthlySummaryHivDataset();
    EvaluatedCohort baseCohort = evaluateCohortDefinition(genericCohortQueries.getBaseCohort());
    MapDataSet ds = (MapDataSet) evaluateDatasetDefinition(dataSetDefinition, baseCohort);

    // Nº cumulativo de pacientes registados até o fim do mês anterior
    assertThat(ds, hasColumnValue("A1-F014", 135));
    assertThat(ds, hasColumnValue("A1-M014", 141));
    assertThat(ds, hasColumnValue("A1-F15", 2220));
    assertThat(ds, hasColumnValue("A1-M15", 1229));

    // Nº de pacientes registados durante o mês
    assertThat(ds, hasColumnValue("A2-F014", 0));
    assertThat(ds, hasColumnValue("A2-M014", 2));
    assertThat(ds, hasColumnValue("A2-F15", 26));
    assertThat(ds, hasColumnValue("A2-M15", 18));

    // Nr cumulativo de pacientes registados nos Livros de Registo de Pré-TARV até o fim do mês
    assertThat(ds, hasColumnValue("A3-F014", 135));
    assertThat(ds, hasColumnValue("A3-M014", 143));
    assertThat(ds, hasColumnValue("A3-F15", 2245));
    assertThat(ds, hasColumnValue("A3-M15", 1247));

    // Nº mensal de novos inscritos
    assertThat(ds, hasColumnValue("B1-F014", 0));
    assertThat(ds, hasColumnValue("B1-M014", 2));
    assertThat(ds, hasColumnValue("B1-F15", 25));
    assertThat(ds, hasColumnValue("B1-M15", 18));

    // Nº mensal de transferidos de outras US
    assertThat(ds, hasColumnValue("B2-F014", 0));
    assertThat(ds, hasColumnValue("B2-M014", 0));
    assertThat(ds, hasColumnValue("B2-F15", 0));
    assertThat(ds, hasColumnValue("B2-M15", 0));

    // Nº cumulativo de transferidos para outras US
    assertThat(ds, hasColumnValue("C1-F014", 0));
    assertThat(ds, hasColumnValue("C1-M014", 2));
    assertThat(ds, hasColumnValue("C1-F15", 46));
    assertThat(ds, hasColumnValue("C1-M15", 28));

    // Nº cumulativo de abandonos pre-tarv
    assertThat(ds, hasColumnValue("C2-F014", 11));
    assertThat(ds, hasColumnValue("C2-M014", 19));
    assertThat(ds, hasColumnValue("C2-F15", 326));
    assertThat(ds, hasColumnValue("C2-M15", 315));

    // Nº cumulativo de óbitos pre-tarv
    assertThat(ds, hasColumnValue("C3-F014", 2));
    assertThat(ds, hasColumnValue("C3-M014", 4));
    assertThat(ds, hasColumnValue("C3-F15", 14));
    assertThat(ds, hasColumnValue("C3-M15", 15));

    // Nº cumulativo que iniciaram TARV
    assertThat(ds, hasColumnValue("C4-F014", 120));
    assertThat(ds, hasColumnValue("C4-M014", 116));
    assertThat(ds, hasColumnValue("C4-F15", 1829));
    assertThat(ds, hasColumnValue("C4-M15", 877));

    // Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para TB
    assertThat(ds, hasColumnValue("E1-F014", 0));
    assertThat(ds, hasColumnValue("E1-M014", 2));
    assertThat(ds, hasColumnValue("E1-F15", 22));
    assertThat(ds, hasColumnValue("E1-M15", 15));

    // Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV rastreados para ITS
    assertThat(ds, hasColumnValue("E2-F014", 0));
    assertThat(ds, hasColumnValue("E2-M014", 0));
    assertThat(ds, hasColumnValue("E2-F15", 25));
    assertThat(ds, hasColumnValue("E2-M15", 16));

    // Nº dos novos inscritos mensais no Livro de Registo Nº 1 de Pré-TARV que iniciaram TPC durante
    // o mês
    assertThat(ds, hasColumnValue("F1-F014", 0));
    assertThat(ds, hasColumnValue("F1-M014", 0));
    assertThat(ds, hasColumnValue("F1-F15", 16));
    assertThat(ds, hasColumnValue("F1-M15", 7));

    // Nº dos novos inscritos mensais no  Livro de Registo Nº 1 de Pré-TARV que iniciaram TPI
    // durante o mês
    assertThat(ds, hasColumnValue("F2-F014", 0));
    assertThat(ds, hasColumnValue("F2-M014", 0));
    assertThat(ds, hasColumnValue("F2-F15", 20));
    assertThat(ds, hasColumnValue("F2-M15", 7));

    // Nº cumulativo de pacientes registados até o fim do mês anterior TARV
    assertThat(ds, hasColumnValue("G1-F014", 146));
    assertThat(ds, hasColumnValue("G1-M014", 133));
    assertThat(ds, hasColumnValue("G1-F15", 2106));
    assertThat(ds, hasColumnValue("G1-M15", 1039));

    // Nº de pacientes registados durante o mês TARV
    assertThat(ds, hasColumnValue("G2-F014", 0));
    assertThat(ds, hasColumnValue("G2-M014", 1));
    assertThat(ds, hasColumnValue("G2-F15", 29));
    assertThat(ds, hasColumnValue("G2-M15", 20));

    // Nº cumulativo de pacientes registados nos Livros de Registo de TARV até o fim do mês
    assertThat(ds, hasColumnValue("G3-F014", 146));
    assertThat(ds, hasColumnValue("G3-M014", 134));
    assertThat(ds, hasColumnValue("G3-F15", 2134));
    assertThat(ds, hasColumnValue("G3-M15", 1058));

    // Nº mensal de novos inícios tarv
    assertThat(ds, hasColumnValue("H1-F014", 0));
    assertThat(ds, hasColumnValue("H1-M014", 1));
    assertThat(ds, hasColumnValue("H1-F15", 24));
    assertThat(ds, hasColumnValue("H1-M15", 17));
  }

  @Override
  protected String username() {
    return "admin";
  }

  @Override
  protected String password() {
    return "eSaude123";
  }

  public static Matcher<MapDataSet> hasColumnValue(String columnName, Object value) {
    return new HasColumnValue(columnName, value);
  }

  private static class HasColumnValue extends TypeSafeMatcher<MapDataSet> {

    private String columnName;
    private Object columnValue;

    public HasColumnValue(String columnName, Object columnValue) {
      this.columnName = columnName;
      this.columnValue = columnValue;
    }

    @Override
    protected boolean matchesSafely(MapDataSet dataSet) {
      return columnValue.equals(getValue(dataSet));
    }

    private Object getValue(MapDataSet dataSet) {
      DataSetColumn column = dataSet.getMetaData().getColumn(columnName);
      if (column == null) {
        throw new IllegalArgumentException("Column " + columnName + " not found.");
      }
      Object data = dataSet.getData(column);
      return ((CohortIndicatorAndDimensionResult) data).getValue();
    }

    @Override
    public void describeTo(Description description) {
      description.appendText("column " + columnName + " to have value ").appendValue(columnValue);
    }

    @Override
    protected void describeMismatchSafely(MapDataSet dataSet, Description mismatchDescription) {
      mismatchDescription.appendText("was ").appendValue(getValue(dataSet));
    }
  }
}
