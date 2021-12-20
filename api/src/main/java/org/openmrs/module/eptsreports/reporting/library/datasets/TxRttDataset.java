/** */
package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ABOVE_FIFTY;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIFTEEN_TO_NINETEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FIVE_TO_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_FIVE_TO_FORTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.FORTY_TO_FORTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.ONE_TO_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TEN_TO_FOURTEEN;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_FIVE_TO_THIRTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.THIRTY_TO_THRITY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_FIVE_TO_TWENTY_NINE;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.TWENTY_TO_TWENTY_FOUR;
import static org.openmrs.module.eptsreports.reporting.utils.AgeRange.UNDER_ONE;

import org.openmrs.module.eptsreports.reporting.library.cohorts.TxRTTCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.KeyPopulationDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.AgeRange;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.eptsreports.reporting.utils.Gender;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** @author Stélio Moiane */
@Component
public class TxRttDataset extends BaseDataSet {

  @Autowired private TxRTTCohortQueries txRTTCohortQueries;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired private KeyPopulationDimension keyPopulationDimension;

  public DataSetDefinition constructTxRttDataset() {

    final CohortIndicatorDataSetDefinition definition = new CohortIndicatorDataSetDefinition();
    definition.setName("TX RTT Dataset");
    definition.addParameters(this.getParameters());

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    final CohortDefinition patientsOnRttDefinition = this.txRTTCohortQueries.getPatientsOnRTT();

    final CohortIndicator patientOnRttIndicator =
        this.eptsGeneralIndicator.getIndicator(
            "patientsOnTxRtt", EptsReportUtils.map(patientsOnRttDefinition, mappings));

    this.addDimensions(
        definition,
        "endDate=${endDate}",
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    definition.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    definition.addDimension(
        this.getColumnName(AgeRange.UNKNOWN, Gender.MALE),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getColumnName(AgeRange.UNKNOWN, Gender.MALE), Gender.MALE),
            ""));

    definition.addDimension(
        this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE),
        EptsReportUtils.map(
            this.eptsCommonDimension.findPatientsWithUnknownAgeByGender(
                this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE), Gender.FEMALE),
            ""));

    definition.addDimension(
        "homosexual",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreHomosexual(), mappings));

    definition.addDimension(
        "drug-user",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoUseDrugs(), mappings));

    definition.addDimension(
        "prisioner",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreInPrison(), mappings));

    definition.addDimension(
        "sex-worker",
        EptsReportUtils.map(this.keyPopulationDimension.findPatientsWhoAreSexWorker(), mappings));

    definition.addColumn(
        "RTTALL", "TX_RTT ALL patients", EptsReportUtils.map(patientOnRttIndicator, mappings), "");

    this.addColums(
        definition,
        mappings,
        patientOnRttIndicator,
        UNDER_ONE,
        ONE_TO_FOUR,
        FIVE_TO_NINE,
        TEN_TO_FOURTEEN,
        FIFTEEN_TO_NINETEEN,
        TWENTY_TO_TWENTY_FOUR,
        TWENTY_FIVE_TO_TWENTY_NINE,
        THIRTY_TO_THRITY_FOUR,
        THIRTY_FIVE_TO_THIRTY_NINE,
        FORTY_TO_FORTY_FOUR,
        FORTY_FIVE_TO_FORTY_NINE,
        ABOVE_FIFTY);

    definition.addColumn(
        "R-malesUnknownM",
        "unknownM",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        this.getColumnName(AgeRange.UNKNOWN, Gender.MALE)
            + "="
            + this.getColumnName(AgeRange.UNKNOWN, Gender.MALE));

    definition.addColumn(
        "R-femalesUnknownF",
        "unknownF",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE)
            + "="
            + this.getColumnName(AgeRange.UNKNOWN, Gender.FEMALE));

    definition.addColumn(
        "R-MSM",
        "Homosexual",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "gender=M|homosexual=homosexual");

    definition.addColumn(
        "R-PWID",
        "Drugs User",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "drug-user=drug-user");

    definition.addColumn(
        "R-PRI",
        "Prisioners",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "prisioner=prisioner");

    definition.addColumn(
        "R-FSW",
        "Sex Worker",
        EptsReportUtils.map(patientOnRttIndicator, mappings),
        "gender=F|sex-worker=sex-worker");

    definition.addColumn(
        "R-DurationIIT-LESS-3MONTHS",
        "Duration of IIT Before returning Treatment <3 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Duration of IIT Before returning Treatment <3 months",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getDurationInterruptionOfTreatmentLessThan3Months(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "R-DurationIIT-BETWEEN-3-5MONTHS",
        "Duration of IIT Before returning Treatment Between 3-5 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Duration of IIT Before returning Treatment Between 3-5 months",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getDurationInterruptionOfTreatmentBetween3And5Months(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "R-DurationIIT-GREATER-OR-EQUAL-6MONTHS",
        "Duration of IIT Before returning Treatment Greater Or Equal 6 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Duration of IIT Before returning Treatment Greater or Equal 6 months",
                EptsReportUtils.map(
                    this.txRTTCohortQueries
                        .getDurationInterruptionOfTreatmentGreaterOrEqual6Months(),
                    mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVLESS12MONTH",
        "PLHIV <12 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients PLHIV <12 Months",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVLess12MonthCalculation(), mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVGREATER12MONTH",
        "PLHIV >=12 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients PLHIV >=12 Months",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVGreather12MonthCalculation(), mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVUNKOWN",
        "PLHIV Unknown Desaggregation",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "patients PLHIV With unknown date of IIT ",
                EptsReportUtils.map(
                    this.txRTTCohortQueries.getPLHIVUnknownDesaggregation(), mappings)),
            mappings),
        "");

    definition.addColumn(
        "PLHIVTOTAL",
        "PLHIV Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Patients PLHIV - All",
                EptsReportUtils.map(this.txRTTCohortQueries.getPLHIVTotal(), mappings)),
            mappings),
        "");

    return definition;
  }

  private void addDimensions(
      final CohortIndicatorDataSetDefinition cohortIndicatorDataSetDefinition,
      final String mappings,
      final AgeRange... ranges) {

    for (final AgeRange range : ranges) {

      cohortIndicatorDataSetDefinition.addDimension(
          this.getColumnName(range, Gender.MALE),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getColumnName(range, Gender.MALE), range, Gender.MALE),
              mappings));

      cohortIndicatorDataSetDefinition.addDimension(
          this.getColumnName(range, Gender.FEMALE),
          EptsReportUtils.map(
              this.eptsCommonDimension.findPatientsByGenderAndRange(
                  this.getColumnName(range, Gender.FEMALE), range, Gender.FEMALE),
              mappings));
    }
  }

  private String getColumnName(final AgeRange range, final Gender gender) {
    return range.getDesagregationColumnName("R", gender);
  }

  private void addColums(
      final CohortIndicatorDataSetDefinition dataSetDefinition,
      final String mappings,
      final CohortIndicator cohortIndicator,
      final AgeRange... rannges) {

    for (final AgeRange range : rannges) {

      final String maleName = this.getColumnName(range, Gender.MALE);
      final String femaleName = this.getColumnName(range, Gender.FEMALE);

      dataSetDefinition.addColumn(
          maleName,
          maleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          maleName + "=" + maleName);

      dataSetDefinition.addColumn(
          femaleName,
          femaleName.replace("-", " "),
          EptsReportUtils.map(cohortIndicator, mappings),
          femaleName + "=" + femaleName);
    }
  }
}
