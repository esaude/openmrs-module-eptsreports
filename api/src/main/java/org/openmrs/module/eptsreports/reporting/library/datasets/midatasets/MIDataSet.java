package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MIDataSet extends BaseDataSet {

  @Autowired private MICategory7Dataset miCategory7Dataset;
  @Autowired private MICategory11DataSet miCategory11DataSet;
  @Autowired private MICategory12P1Dataset miCategory12Dataset;
  @Autowired private MICommonsDementions mICommonsDementions;

  public DataSetDefinition constructTMiDatset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("MQ Data Set");
    dataSetDefinition.setParameters(getParameters());

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    this.mICommonsDementions.getMICommonDementions(dataSetDefinition, mappings);
    miCategory7Dataset.constructTMiDatset(dataSetDefinition, mappings);
    miCategory11DataSet.constructTMiDatset(dataSetDefinition, mappings);
    miCategory12Dataset.constructTMiDatset(dataSetDefinition, mappings);

    return dataSetDefinition;
  }

  @Override
  public List<Parameter> getParameters() {
    return Arrays.asList(
        new Parameter("startInclusionDate", "Data Inicio Inclusão", Date.class),
        new Parameter("endInclusionDate", "  Data Final Inclusão", Date.class),
        new Parameter("endRevisionDate", "Data Final Revisão", Date.class),
        new Parameter("location", "Unidade Sanitária", Location.class));
  }
}
