package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.DSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EriDSDDataset extends BaseDataSet {

  @Autowired private DSDCohortQueries dsdCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructEriDSDDataset() {
    final CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.setName("DSD Data Set");
    definition.addParameters(this.getParameters());

    this.addAgeDimensions(
        definition,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN,
        AgeRange.ADULT);

    this.dsdDenominator1(definition, mappings);

    this.dsdNumerator1(definition, mappings);
    this.dsdNumerator2(definition, mappings);
    this.dsdNumerator3(definition, mappings);
    this.dsdNumerator4(definition, mappings);
    this.dsdNumerator5(definition, mappings);
    this.dsdNumerator6(definition, mappings);
    this.dsdNumerator7(definition, mappings);
    this.dsdNumerator8(definition, mappings);

    return definition;
  }

  private void dsdNumerator8(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN8T",
        "DSDN8T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInCommunityDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInCommunityDrugsDistribution(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN8-E",
        "DSDN8-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleToSixMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtEligibleToCommunityDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN8-N",
        "DSDN8-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtNotEligibleToCommunityDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator7(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN7T",
        "DSDN7T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInSixMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInSixMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN7-E",
        "DSDN7-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleToSixMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtEligibleToSixMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN7-N",
        "DSDN7-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtNotEligibleToSixMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator6(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN6T",
        "DSDN6T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInAdherenceClubs",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInAdherenceClubs(), mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN6-E",
        "DSDN6-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleToFamilyApproach",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtEligibleToAdherenceClubs(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN6-N",
        "DSDN6-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleToFamilyApproach",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtNotEligibleToAdherenceClubs(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator5(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN5T",
        "DSDN5T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInFamilyApproach",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInFamilyApproach(), mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN5-E",
        "DSDN5-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleToFamilyApproach",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtEligibleToFamilyApproach(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN5-N",
        "DSDN5-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleToFamilyApproach",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtNotEligibleToFamilyApproach(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator4(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN4T",
        "DSDN4T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInCommunityAdherennceGroups",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInCommunityAdherennceGroups(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN4-E",
        "DSDN4-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleToCommunityAdherennceGroups",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtEligibleToCommunityAdherennceGroups(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN4-N",
        "DSDN4-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleToCommunityAdherennceGroups",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsActiveOnArtNotEligibleToCommunityAdherennceGroups(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator3(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN3T",
        "DSDN3T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtInFastTrack",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtInFastTrack(), mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN3-E",
        "DSDN3-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtAndEligibleToFastTrack",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtEligibleToFastTrack(), mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN3-N",
        "DSDN3-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotElegibleToDsdWhoInFastFlow",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtNotElegibleToFastTrack(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator2(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN2T",
        "DSDN2T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtAndInThreeMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN2-E",
        "DSDN2-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtAndEligibleToThreeMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtAndEligibleToThreeMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN2-N",
        "DSDN2-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtAndNotEligibleToThreeMonthsDrugsDistribution",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtAndNotEligibleToThreeMonthsDrugsDistribution(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void dsdNumerator1(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDN1T",
        "DSDN1T Total",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtAndInAtleastOneDSD",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsWhoAreActiveOnArtAndInAtleastOneDSD(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDN1-E",
        "DSDN1-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreStable",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreStable(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDN1-N",
        "DSDN1-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "findPatientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreUnstable",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtAndInAtleastOneDSDAndAreUnstable(),
                    mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  /**
   * @param definition
   * @param mappings
   */
  private void dsdDenominator1(
      final CohortIndicatorDataSetDefinition definition, final String mappings) {
    definition.addColumn(
        "DSDD1T",
        "DSDD1",
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb",
                EptsReportUtils.map(
                    this.dsdCohortQueries
                        .findPatientsWhoAreActiveOnArtExcludingPregnantBreastfeedingAndTb(),
                    mappings)),
            mappings),
        "");

    this.addColumns(
        "DSDD1-E",
        "DSDD1-E",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtEligibleForDsd",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtEligibleForDsd(), mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);

    this.addColumns(
        "DSDD1-N",
        "DSDD1-N",
        definition,
        EptsReportUtils.map(
            this.eptsGeneralIndicator.getIndicator(
                "patientsActiveOnArtNotEligibleForDsd",
                EptsReportUtils.map(
                    this.dsdCohortQueries.findPatientsActiveOnArtNotEligibleForDsd(), mappings)),
            mappings),
        AgeRange.ADULT,
        AgeRange.UNDER_TWO,
        AgeRange.TWO_TO_FOUR,
        AgeRange.FIVE_TO_NINE,
        AgeRange.TEN_TO_FOURTEEN);
  }

  private void addColumns(
      final String name,
      final String label,
      final CohortIndicatorDataSetDefinition definition,
      final Mapped<CohortIndicator> indicator,
      final AgeRange... ranges) {

    int position = 1;

    String baseName = name + position;
    String baseLabel = label + "(" + position + ")";

    definition.addColumn(baseName, baseLabel, indicator, "");
    position++;

    for (final AgeRange range : ranges) {

      baseName = name + position;
      baseLabel = label + "(" + position + ")";

      definition.addColumn(baseName, baseLabel, indicator, range.getName() + "=" + range.getName());
      position++;
    }
  }

  private void addAgeDimensions(
      final CohortIndicatorDataSetDefinition definition, final AgeRange... ranges) {

    for (final AgeRange range : ranges) {
      definition.addDimension(
          range.getName(),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByRange(range.getName(), range),
              "endDate=${endDate}"));
    }
  }
}
