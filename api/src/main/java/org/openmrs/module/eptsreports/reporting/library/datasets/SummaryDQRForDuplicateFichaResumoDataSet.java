package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.DqrDuplicateFichaResumoCohorts;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDQRForDuplicateFichaResumoDataSet extends BaseDataSet {

  private EptsGeneralIndicator eptsGeneralIndicator;
  private DqrDuplicateFichaResumoCohorts duplicateFichaResumoCohorts;
  private HivMetadata hivMetadata;

  @Autowired
  public SummaryDQRForDuplicateFichaResumoDataSet(
      EptsGeneralIndicator eptsGeneralIndicator,
      DqrDuplicateFichaResumoCohorts duplicateFichaResumoCohorts,
      HivMetadata hivMetadata) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.duplicateFichaResumoCohorts = duplicateFichaResumoCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition constructIndicatorDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "endDate=${endDate},location=${location}";
    dsd.setName("DQRD");
    dsd.addParameters(getParameters());
    dsd.addColumn(
        "EC1T",
        "The patient has more than one Ficha Resumo registered in OpenMRS EPTS at the same Health Facility",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "EC1T",
                EptsReportUtils.map(
                    duplicateFichaResumoCohorts.getDuplicatePatientsForFichaResumo(
                        hivMetadata.getMasterCardEncounterType().getEncounterTypeId()),
                    mappings)),
            mappings),
        "");

    return dsd;
  }
}
