package org.openmrs.module.eptsreports.reporting.library.datasets.midatasets;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.MIAgeDimentions;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MICommonsDementions {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private MIAgeDimentions mIAgeDimentions;

  public void getMICommonDementions(
      final CohortIndicatorDataSetDefinition dataSetDefinition, String mappings) {

    dataSetDefinition.addDimension("gender", map(eptsCommonDimension.gender(), ""));

    dataSetDefinition.addDimension(
        "ageMiNewART",
        EptsReportUtils.map(
            this.mIAgeDimentions.getDimensionForPatientsWhoAreNewlyEnrolledOnART(), mappings));

    dataSetDefinition.addDimension(
        "ageOnCV",
        EptsReportUtils.map(
            this.mIAgeDimentions.getDimensionForPatientsPatientWithCVOver1000Copies(), mappings));

    dataSetDefinition.addDimension(
        "age",
        EptsReportUtils.map(
            this.mIAgeDimentions.getDimensionForLastClinicalConsultation(), mappings));
  }
}
