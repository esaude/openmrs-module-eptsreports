package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.List;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.queries.data.quality.Ec18Queries;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Ec18PatientListDataset extends BaseDataSet {
  private HivMetadata hivMetadata;

  @Autowired
  public Ec18PatientListDataset(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  public DataSetDefinition ec18PatientListDataset(List<Parameter> parameterList) {
    SqlDataSetDefinition sqlDataSetDefinition = new SqlDataSetDefinition();
    sqlDataSetDefinition.setName("EC18");
    sqlDataSetDefinition.addParameters(parameterList);
    sqlDataSetDefinition.setSqlQuery(
        Ec18Queries.getEc18CombinedQuery(
            hivMetadata.getARTProgram().getProgramId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            1985));
    return sqlDataSetDefinition;
  }
}
