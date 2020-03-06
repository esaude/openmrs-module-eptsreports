package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralDataSetDefinition extends BaseDataSet {

  enum Quarter {
    Q1("1st"),
    Q2("2nd"),
    Q3("3rd");

    private String display;

    Quarter(String display) {
      this.display = display;
    }

    @Override
    public String toString() {
      return display;
    }
  };

  public DataSetDefinition constructResumoTrimestralDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Resumo trimestral data set");
    return dsd;
  }

  @Override
  public List<Parameter> getParameters() {
    ArrayList<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("year", "Year", Integer.class));
    parameters.add(new Parameter("quarter", "Quarter", Quarter.class));
    return parameters;
  }
}
