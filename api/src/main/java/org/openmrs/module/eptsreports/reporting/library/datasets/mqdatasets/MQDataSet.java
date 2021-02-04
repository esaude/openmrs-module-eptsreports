package org.openmrs.module.eptsreports.reporting.library.datasets.mqdatasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.MQAgeDimensions;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MQDataSet extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private MQAgeDimensions mQAgeDimensions;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private MQCategory14DataSet mqCategory14DataSet;
  @Autowired private MQCategory3DataSet mqCategory3DataSet;
  @Autowired private MQCategory4Dataset mQCategory4Dataset;
  @Autowired private MQCategory5DataSet mQCategory5DataSet;
  @Autowired private MQCategory6Dataset mQCategory6Dataset;
  @Autowired private MQCategory7Dataset mQCategory7Dataset;
  @Autowired private MQCategory11DataSet mQCategory11DataSet;
  @Autowired private MQCategory12DataSet mQCategory12DataSet;
  @Autowired private MQCategory12P2DataSet mQCategory12SectionIIDataSet;
  @Autowired private MQCategory13DataSetSectionI mQCategory13DataSetSectionI;
  @Autowired private MQCategory13DataSetSectionII mQCategory13DataSetSectionII;
  @Autowired private MQCategory13P2DataSet mQCategory13P2DataSet;
  @Autowired private MQCategory13P3DataSet mQCategory13P3DataSet;
  @Autowired private MQCategory15DataSet mQCategory15DataSet;

  public DataSetDefinition constructTMqDatset() {

    final CohortIndicatorDataSetDefinition dataSetDefinition =
        new CohortIndicatorDataSetDefinition();
    dataSetDefinition.setParameters(getParameters());

    final String mappings =
        "startInclusionDate=${startInclusionDate},endInclusionDate=${endInclusionDate},endRevisionDate=${endRevisionDate},location=${location}";

    dataSetDefinition.setName("MQ Data Set");

    dataSetDefinition.addDimension("gender", map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForLastClinicalConsultation(), mappings));

    dataSetDefinition.addDimension(
        "ageMq",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));

    dataSetDefinition.addDimension(
        "ageMqNewART",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsWhoAreNewlyEnrolledOnART(), mappings));

    dataSetDefinition.addDimension(
        "ageMqNewARTRevisionDate",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsWhoAreNewlyEnrolledOnARTUntilRevisionDate(),
            mappings));

    dataSetDefinition.addDimension(
        "ageOnCV",
        EptsReportUtils.map(
            this.mQAgeDimensions.getDimensionForPatientsPatientWithCVOver1000Copies(), mappings));

    dataSetDefinition.addDimension(
        "ageOnEndInclusionDate",
        EptsReportUtils.map(
            eptsCommonDimension.ageEndInclusionDate(ageDimensionCohort),
            "effectiveDate=${endInclusionDate}"));

    this.mqCategory3DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory4Dataset.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory5DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory6Dataset.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory7Dataset.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory11DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory12DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory12SectionIIDataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory13DataSetSectionI.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory13DataSetSectionII.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory13P2DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory13P3DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mQCategory15DataSet.constructTMqDatset(dataSetDefinition, mappings);
    this.mqCategory14DataSet.constructTMqDatset(dataSetDefinition, mappings);

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
