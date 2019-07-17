package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class DataQualityBaseDataset {

  @Autowired private GenericCohortQueries genericCohortQueries;

  public List<Parameter> getDataQualityParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(new Parameter("location", "Facilities", Location.class, List.class, null));
    parameters.add(genericCohortQueries.getArtProgramConfigurableParameter());
    return parameters;
  }
}
