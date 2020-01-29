package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxCurrDimensions {

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  public CohortDefinitionDimension findPatientsOnArtOnArvDispenseForLessThan3Months() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Patients On Arv Dispensation < 3 Months");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));
    dimension.addCohortDefinition(
        "arvdispenseless3months",
        EptsReportUtils.map(
            this.txCurrCohortQueries.getPatientsOnArtOnArvDispenseForLessThan3Months(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsOnArtOnArvDispenseBetween3And5Months() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Patients On Arv Dispensation between 3 and 5 Months");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "arvdispensefor3and5months",
        EptsReportUtils.map(
            this.txCurrCohortQueries.getPatientsOnArtOnArvDispenseBetween3And5Months(), mappings));

    return dimension;
  }

  public CohortDefinitionDimension findPatientsOnArtOnArvDispenseFor6OrMoreMonths() {
    final CohortDefinitionDimension dimension = new CohortDefinitionDimension();
    final String mappings = "endDate=${endDate},location=${location}";
    dimension.setName("Patients On Arv Dispensation for 6 and more months");
    dimension.addParameter(new Parameter("endDate", "End Date", Date.class));
    dimension.addParameter(new Parameter("location", "location", Location.class));

    dimension.addCohortDefinition(
        "arvdispensefor6andmoremonths",
        EptsReportUtils.map(
            this.txCurrCohortQueries.getPatientsOnArtOnArvDispenseFor6OrMoreMonths(), mappings));

    return dimension;
  }
}
