package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.reporting.library.cohorts.data.quality.SummaryDataQualityCohorts;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** A dataset to display list of patients who are pregnant and male */
@Component
public class Ec1PatientListDataset extends BaseDataSet {

  private SummaryDataQualityCohorts summaryDataQualityCohorts;

  @Autowired
  public Ec1PatientListDataset(SummaryDataQualityCohorts summaryDataQualityCohorts) {
    this.summaryDataQualityCohorts = summaryDataQualityCohorts;
  }

  public DataSetDefinition ec1DataSetDefinition() {
    PatientDataSetDefinition dsd = new PatientDataSetDefinition();
    dsd.setName("EC1");
    dsd.addParameters(getDataQualityParameters());
    dsd.addRowFilter(summaryDataQualityCohorts.getPregnantMalePatients(), "location=${location}");

    // identifier
    PatientIdentifierType preARTNo =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");
    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(preARTNo.getName(), preARTNo),
            identifierFormatter);

    // start adding columns here
    dsd.addColumn("Patient Id", new PatientIdDataDefinition(), "");
    dsd.addColumn("Patient NID", identifierDef, "");

    return dsd;
  }
}
