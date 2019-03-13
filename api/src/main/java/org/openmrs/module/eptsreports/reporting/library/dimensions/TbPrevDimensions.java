package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet.ColumnParameters;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Uses age cohorts that determine age on ART start date. */
@Component("tbPrevDimensions")
public class TbPrevDimensions {

  @Autowired private AgeCohortQueries ageCohortQueries;

  public CohortDefinitionDimension getAgeDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("effectiveDate", "effectiveDate", Date.class));
    dim.setName("TB-PREV age dimension");
    dim.addCohortDefinition(
        "0-14",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("0-14", 0, 14), "effectiveDate=${effectiveDate}"));
    dim.addCohortDefinition(
        "15+",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("15+", 15, 200),
            "effectiveDate=${effectiveDate}"));
    return dim;
  }

  public List<ColumnParameters> getAgeColumns() {
    return Arrays.asList(
        new ColumnParameters("M0-14", "M0-14", "age=0-14|gender=M", "01"),
        new ColumnParameters("M15+", "M15+", "age=15+|gender=M", "02"),
        new ColumnParameters("F0-14", "F0-14", "age=0-14|gender=F", "03"),
        new ColumnParameters("F15+", "F15+", "age=15+|gender=F", "04"));
  }
}
