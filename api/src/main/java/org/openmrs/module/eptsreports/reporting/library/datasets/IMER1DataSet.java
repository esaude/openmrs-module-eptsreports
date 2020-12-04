package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.IMER1DenominatorCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.IMER1NumeratorCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IMER1DataSet extends BaseDataSet {

  private final String MAPPINGS = "startDate=${startDate},endDate=${endDate},location=${location}";

  private EptsGeneralIndicator eptsGeneralIndicator;

  private IMER1DenominatorCohortQueries imer1DenominatorCohortQueries;

  private IMER1NumeratorCohortQueries imer1NumeratorCohortQueries;

  @Autowired
  public IMER1DataSet(
      IMER1DenominatorCohortQueries imer1DenominatorCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      IMER1NumeratorCohortQueries imer1NumeratorCohortQueries) {
    this.imer1DenominatorCohortQueries = imer1DenominatorCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.imer1NumeratorCohortQueries = imer1NumeratorCohortQueries;
  }

  public DataSetDefinition constructIMER1DataSet() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("IMER 1 Data Set");
    dsd.addParameters(getParameters());

    /* denominator */
    CohortIndicator dall =
        eptsGeneralIndicator.getIndicator(
            "DALL", EptsReportUtils.map(imer1DenominatorCohortQueries.getAllPatients(), MAPPINGS));

    CohortIndicator dbreastfeeding =
        eptsGeneralIndicator.getIndicator(
            "DBREASTFEEDING",
            EptsReportUtils.map(imer1DenominatorCohortQueries.getBreastfeedingWoman(), MAPPINGS));

    CohortIndicator dpregnant =
        eptsGeneralIndicator.getIndicator(
            "DPREGNANT",
            EptsReportUtils.map(imer1DenominatorCohortQueries.getPregnantWomen(), MAPPINGS));

    CohortIndicator dadults =
        eptsGeneralIndicator.getIndicator(
            "DADULTS", EptsReportUtils.map(imer1DenominatorCohortQueries.getAdults(), MAPPINGS));

    CohortIndicator dchildren =
        eptsGeneralIndicator.getIndicator(
            "DCHILDREN",
            EptsReportUtils.map(imer1DenominatorCohortQueries.getChildreen(), MAPPINGS));

    dsd.addColumn("DALL", "ALL Patients", EptsReportUtils.map(dall, MAPPINGS), "");
    dsd.addColumn(
        "DBREASTFEEDING", "BREASTFEEDING", EptsReportUtils.map(dbreastfeeding, MAPPINGS), "");
    dsd.addColumn("DPREGNANT", "PREGNANT", EptsReportUtils.map(dpregnant, MAPPINGS), "");
    dsd.addColumn("DADULTS", "ADULTS", EptsReportUtils.map(dadults, MAPPINGS), "");
    dsd.addColumn("DCHILDREN", "CHILDREN", EptsReportUtils.map(dchildren, MAPPINGS), "");

    /* numerator */
    CohortIndicator nall =
        eptsGeneralIndicator.getIndicator(
            "NALL", EptsReportUtils.map(imer1NumeratorCohortQueries.getAllPatients(), MAPPINGS));

    CohortIndicator nbreastfeeding =
        eptsGeneralIndicator.getIndicator(
            "NBREASTFEEDING",
            EptsReportUtils.map(
                imer1NumeratorCohortQueries.getPatientWhoAreBreastFeeding(), MAPPINGS));

    CohortIndicator npregnant =
        eptsGeneralIndicator.getIndicator(
            "NPREGNANT",
            EptsReportUtils.map(imer1NumeratorCohortQueries.getPatientWhoArePregnant(), MAPPINGS));

    CohortIndicator nadults =
        eptsGeneralIndicator.getIndicator(
            "NADULTS",
            EptsReportUtils.map(imer1NumeratorCohortQueries.getAdultsPatients(), MAPPINGS));

    CohortIndicator nchildren =
        eptsGeneralIndicator.getIndicator(
            "NCHILDREN",
            EptsReportUtils.map(imer1NumeratorCohortQueries.getChildrenPatients(), MAPPINGS));

    dsd.addColumn("NALL", "ALL Patients", EptsReportUtils.map(nall, MAPPINGS), "");
    dsd.addColumn(
        "NBREASTFEEDING", "BREASTFEEDING", EptsReportUtils.map(nbreastfeeding, MAPPINGS), "");
    dsd.addColumn("NPREGNANT", "PREGNANT", EptsReportUtils.map(npregnant, MAPPINGS), "");
    dsd.addColumn("NADULTS", "ADULTS", EptsReportUtils.map(nadults, MAPPINGS), "");
    dsd.addColumn("NCHILDREN", "CHILDREN", EptsReportUtils.map(nchildren, MAPPINGS), "");

    return dsd;
  }
}
