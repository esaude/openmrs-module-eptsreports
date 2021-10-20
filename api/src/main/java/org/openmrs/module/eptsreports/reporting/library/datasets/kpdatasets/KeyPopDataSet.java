package org.openmrs.module.eptsreports.reporting.library.datasets.kpdatasets;

import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class KeyPopDataSet extends BaseDataSet {
  @Autowired private KeyPopDataSetSection1 keyPopDataSetSection1;
  @Autowired private KeyPopDataSetSection2 keyPopDataSetSection2;
  @Autowired private KeyPopDataSetSection3 keyPopDataSetSection3;
  @Autowired private KeyPopDataSetSection4 keyPopDataSetSection4;
  @Autowired private KeyPopDataSetSection5 keyPopDataSetSection5;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructTKeyPopDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();

    dataSetDefinition.setName("MQ Data Set");
    dataSetDefinition.setParameters(getParameters());
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    dataSetDefinition.setName("TX_NEW Data Set");
    dataSetDefinition.addParameters(this.getParameters());
    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    keyPopDataSetSection1.constructDataset(dataSetDefinition, mappings);
    keyPopDataSetSection2.constructDataset(dataSetDefinition, mappings);
    keyPopDataSetSection3.constructDataset(dataSetDefinition, mappings);
    keyPopDataSetSection4.constructDataset(dataSetDefinition, mappings);
    keyPopDataSetSection5.constructDataset(dataSetDefinition, mappings);

    return dataSetDefinition;
  }
}
