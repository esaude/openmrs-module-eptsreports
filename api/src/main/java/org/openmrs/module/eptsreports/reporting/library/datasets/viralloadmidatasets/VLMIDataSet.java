package org.openmrs.module.eptsreports.reporting.library.datasets.viralloadmidatasets;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.datasets.midatasets.MICommonsDementions;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VLMIDataSet extends BaseDataSet {

  @Autowired private VLMICategory13Dataset VLMiCategory13Dataset;
  @Autowired private VLMICategory22Dataset VLMiCategory22Dataset;
  @Autowired private VLMICategory23Dataset VLMiCategory23Dataset;
  @Autowired private VLMICategory24Dataset VLMiCategory24Dataset;
  @Autowired private VLMICategory25Dataset VLMiCategory25Dataset;
  @Autowired private VLMICategory26Dataset VLMiCategory26Dataset;
  @Autowired private VLMICategory27Dataset VLMiCategory27Dataset;
  @Autowired private MICommonsDementions mICommonsDementions;

  public DataSetDefinition constructVLMiDatset() {
    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("VIRAL LOAD MI DataSet");
    dataSetDefinition.setParameters(getParameters());

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    this.mICommonsDementions.getMICommonDementions(dataSetDefinition, mappings);
    this.VLMiCategory13Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory22Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory23Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory24Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory25Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory26Dataset.constructTMiDatset(dataSetDefinition, mappings);
    this.VLMiCategory27Dataset.constructTMiDatset(dataSetDefinition, mappings);

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
