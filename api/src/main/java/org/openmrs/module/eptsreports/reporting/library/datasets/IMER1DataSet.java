package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.IMER1BNumeratorCohortQueries;
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

  private IMER1BNumeratorCohortQueries imer1BNumeratorCohortQueries;

  @Autowired
  public IMER1DataSet(
      IMER1DenominatorCohortQueries imer1DenominatorCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      IMER1NumeratorCohortQueries imer1NumeratorCohortQueries,
      IMER1BNumeratorCohortQueries imer1BNumeratorCohortQueries) {
    this.imer1DenominatorCohortQueries = imer1DenominatorCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.imer1NumeratorCohortQueries = imer1NumeratorCohortQueries;
    this.imer1BNumeratorCohortQueries = imer1BNumeratorCohortQueries;
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

    /*  IMER1B Numerator */
    CohortIndicator n1ball =
        eptsGeneralIndicator.getIndicator(
            "N1BALL", EptsReportUtils.map(imer1BNumeratorCohortQueries.getAllPatients(), MAPPINGS));

    CohortIndicator n1bbreastfeeding =
        eptsGeneralIndicator.getIndicator(
            "N1BBREASTFEEDING",
            EptsReportUtils.map(imer1BNumeratorCohortQueries.getBreastFeedingWomen(), MAPPINGS));

    CohortIndicator n1bpregnant =
        eptsGeneralIndicator.getIndicator(
            "N1BPREGNANT",
            EptsReportUtils.map(imer1BNumeratorCohortQueries.getPregnantWomen(), MAPPINGS));

    CohortIndicator n1bchildren =
        eptsGeneralIndicator.getIndicator(
            "N1BCHILDREN",
            EptsReportUtils.map(imer1BNumeratorCohortQueries.getChildren(), MAPPINGS));

    CohortIndicator n1badults =
        eptsGeneralIndicator.getIndicator(
            "N1BADULTS", EptsReportUtils.map(imer1BNumeratorCohortQueries.getAdults(), MAPPINGS));

    dsd.addColumn("N1BALL", "1B ALL Patients", EptsReportUtils.map(n1ball, MAPPINGS), "");
    dsd.addColumn(
        "N1BBREASTFEEDING",
        "1B BREASTFEEDING",
        EptsReportUtils.map(n1bbreastfeeding, MAPPINGS),
        "");
    dsd.addColumn("N1BPREGNANT", "1B PREGNANT", EptsReportUtils.map(n1bpregnant, MAPPINGS), "");
    dsd.addColumn("N1BCHILDREN", "1B CHILDREN", EptsReportUtils.map(n1bchildren, MAPPINGS), "");
    dsd.addColumn("N1BADULTS", "1B ADULTS", EptsReportUtils.map(n1badults, MAPPINGS), "");

    return dsd;
  }
}
