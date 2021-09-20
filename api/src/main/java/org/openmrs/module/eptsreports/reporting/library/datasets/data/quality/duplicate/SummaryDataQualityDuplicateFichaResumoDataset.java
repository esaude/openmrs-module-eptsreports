package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.duplicate;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDuplicateFichaResumoDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired
  private EC1PatientListDuplicateFichaResumoDataset eC1PatientListDuplicateFichaResumoDataset;

  public DataSetDefinition constructSummaryDataQualityDatset(List<Parameter> parameterList) {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Data Quality Duplicated Ficha Resumo Summary Dataset");
    final String mappings = "location=${location}";

    final CohortDefinition summaryCohortQueryEC1 =
        eC1PatientListDuplicateFichaResumoDataset.getEC1Total(parameterList);

    dsd.addParameters(parameterList);
    dsd.addColumn(
        "EC1D-TOTAL",
        "EC1D: patients with more than on Ficha Resumo",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC1Indicator",
                EptsReportUtils.map(summaryCohortQueryEC1, mappings)),
            mappings),
        "");

    return dsd;
  }
}
