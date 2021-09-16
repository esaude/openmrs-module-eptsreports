package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality.duplicate;

import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryDataQualityDuplicateDataset extends BaseDataSet {

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EC1PatientListDuplicateDataset eC1PatientListDuplicateDataset;

  @Autowired private EC2PatientListDuplicateDataset eC2PatientListDuplicateDataset;

  public DataSetDefinition constructSummaryDataQualityDatset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("Data Quality Duplicated Summary Dataset");
    final String mappings = "";

    final CohortDefinition summaryCohortQueryEC1 = eC1PatientListDuplicateDataset.getEC1Total();

    final CohortDefinition summaryCohortQueryEC2 = eC2PatientListDuplicateDataset.getEC1Total();

    dsd.addColumn(
        "EC1D-TOTAL",
        "EC1D: patients using same NID",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC1Indicator",
                EptsReportUtils.map(summaryCohortQueryEC1, mappings)),
            mappings),
        "");

    dsd.addColumn(
        "EC2D-TOTAL",
        "EC2D: patients with Duplicated NID",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "summaryCohortQueryEC2Indicator",
                EptsReportUtils.map(summaryCohortQueryEC2, mappings)),
            mappings),
        "");

    return dsd;
  }

  public static String getBaseCohortQuery() {

    final String query =
        " 																	"
            + " select p.patient_id 														"
            + " from patient p 																"
            + " 	inner join encounter e on e.patient_id=p.patient_id 					"
            + " where e.voided=0 and p.voided=0 											"
            + " 	and e.encounter_type in (5,7) 											"
            + " union 																		"
            + " select pg.patient_id 														"
            + " from patient p 																"
            + " 	inner join patient_program pg on p.patient_id=pg.patient_id 			"
            + " where pg.voided=0 and p.voided=0 											"
            + " 	and program_id in (1,2)  												"
            + " union 																		"
            + " select p.patient_id 														"
            + " from patient p 																"
            + " 	inner join encounter e on p.patient_id=e.patient_id 					"
            + " 	inner join obs o on e.encounter_id=o.encounter_id 						"
            + " where p.voided=0 and e.voided=0 and o.voided=0 								"
            + " 	and e.encounter_type=53 and o.concept_id=23891 							"
            + "	and o.value_datetime is not null 											";

    return query;
  }
}
