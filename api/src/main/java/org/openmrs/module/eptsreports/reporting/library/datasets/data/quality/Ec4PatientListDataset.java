package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import static org.openmrs.module.eptsreports.reporting.utils.EptsCommonColumns.addStandardColumns;

import java.util.Arrays;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec4PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  private HivMetadata hivMetadata;

  @Autowired
  public Ec4PatientListDataset(
      SummaryDataQualityCohorts summaryDataQualityCohorts, HivMetadata hivMetadata) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition ec4PatientListDataset() {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC4");
    dsd.addParameters(getDataQualityParameters());
    dsd.addRowFilter(
        summaryDataQualityCohorts.getPatientsWithStatesAndEncounters(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId(),
            Arrays.asList(
                hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
                hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId())),
        "location=${location}");

    // add standard column
    addStandardColumns(dsd);

    return dsd;
  }
}
