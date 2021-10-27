package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxMlCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TxMlDataset25 extends BaseDataSet {

  @Autowired private EptsCommonDimension eptsCommonDimension;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;

  @Autowired private TxMlCohortQueries txMlCohortQueries;

  /**
   * <b>Description:</b> Constructs TXML Dataset
   *
   * @return
   */
  public DataSetDefinition constructtxMlDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String mappingsKp = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";
    dsd.setName("Tx_Ml Data Set");
    dsd.addParameters(getParameters());
    // tie dimensions to this data definition
    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));
    // start building the datasets
    // get the column for the totals
    dsd.addColumn(
        "M1",
        "Total missed appointments",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "totals missed",
                EptsReportUtils.map(
                    txMlCohortQueries
                        .getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
                    mappings)),
            mappings),
        "");
    // get totals disaggregated by gender and age
    addRow(
        dsd,
        "M2",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(
                    txMlCohortQueries
                        .getPatientsWhoMissedNextAppointmentAndNoScheduledDrugPickupOrNextConsultation(),
                    mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());

    // Missed appointment and dead
    addRow(
        dsd,
        "M3",
        "Dead",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "missed and dead",
                EptsReportUtils.map(
                    txMlCohortQueries
                        .getPatientsWhoMissedNextAppointmentAndDiedDuringReportingPeriod(),
                    mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());
    // LTFU Less Than 90 days
    addRow(
        dsd,
        "M4",
        "ITT Less Than 90 days",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "missed and ITT Less Than 90 days",
                EptsReportUtils.map(
                    txMlCohortQueries.getPatientsIITLessThan90DaysComposition(), mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());
    // Transferred Out
    addRow(
        dsd,
        "M5",
        "TransferredOut",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "missed and TransferredOut",
                EptsReportUtils.map(
                    txMlCohortQueries.getPatientsWhoMissedNextAppointmentAndTransferredOut(),
                    mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());
    // Refused Or Stopped Treatment
    //    addRow(
    //        dsd,
    //        "M6",
    //        "RefusedOrStoppedTreatment",
    //        EptsReportUtils.map(
    //            eptsGeneralIndicator.getIndicator(
    //                "missed and RefusedOrStoppedTreatment",
    //                EptsReportUtils.map(
    //                    txMlCohortQueries
    //                        .getPatientsWhoMissedNextAppointmentAndRefusedOrStoppedTreatment(),
    //                    mappings)),
    //            mappings),
    //        getColumnsForAgeAndGenderAndKeyPop());
    // LTFU More Than 180 days
    addRow(
        dsd,
        "M7",
        "ITT More Than 180 days",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "missed and ITT More Than 180 days",
                EptsReportUtils.map(
                    txMlCohortQueries.getPatientsIITMoreThan180DaysComposition(), mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());

    addRow(
        dsd,
        "M8",
        "IIT On Treatment for 3-5 months",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "missed and ITT for 3-5 months",
                EptsReportUtils.map(
                    txMlCohortQueries.getPatientsIITBetween90DaysAnd180DaysComposition(),
                    mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());

    dsd.addColumn(
        "M9",
        "Total IIT",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "totals IIT",
                EptsReportUtils.map(txMlCohortQueries.getPatientsWithIITComposition(), mappings)),
            mappings),
        "");

    return dsd;
  }

  /**
   * <b>Description:</b> Creates Desagregation based on Age and Gender
   *
   * @return
   */
  private List<ColumnParameters> getColumnsForAgeAndGenderAndKeyPop() {
    ColumnParameters under1M =
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "01");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "02");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "03");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "04");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "05");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "06");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty4To29M", "25 - 29 male", "gender=M|age=25-29", "07");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "08");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "09");
    ColumnParameters foutyTo44M =
        new ColumnParameters("foutyTo44M", "40 - 44 male", "gender=M|age=40-44", "10");
    ColumnParameters fouty5To49M =
        new ColumnParameters("fouty5To49M", "45 - 49 male", "gender=M|age=45-49", "11");
    ColumnParameters above50M =
        new ColumnParameters("above50M", "50+ male", "gender=M|age=50+", "12");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "13");
    ColumnParameters totalM = new ColumnParameters("totalM", "Total of Males", "gender=M", "28");

    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "14");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "15");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "16");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "17");
    ColumnParameters fifteenTo19F =
        new ColumnParameters("fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "18");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "19");
    ColumnParameters twenty5To29F =
        new ColumnParameters("twenty4To29F", "25 - 29 female", "gender=F|age=25-29", "20");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "21");
    ColumnParameters thirty5To39F =
        new ColumnParameters("thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "22");
    ColumnParameters foutyTo44F =
        new ColumnParameters("foutyTo44F", "40 - 44 female", "gender=F|age=40-44", "23");
    ColumnParameters fouty5To49F =
        new ColumnParameters("fouty5To49F", "45 - 49 female", "gender=F|age=45-49", "24");
    ColumnParameters above50F =
        new ColumnParameters("above50F", "50+ female", "gender=F|age=50+", "25");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "26");
    ColumnParameters total = new ColumnParameters("totals", "Totals", "", "27");
    ColumnParameters totalF = new ColumnParameters("totalF", "Total of Females", "gender=F", "29");

    // Key population
    ColumnParameters pid = new ColumnParameters("pid", "PID", "KP=PID", "30");
    ColumnParameters msm = new ColumnParameters("msm", "MSM", "KP=MSM", "31");
    ColumnParameters csw = new ColumnParameters("msm", "CSW", "KP=CSW", "32");
    ColumnParameters pri = new ColumnParameters("pri", "PRI", "KP=PRI", "33");

    return Arrays.asList(
        under1M,
        oneTo4M,
        fiveTo9M,
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        foutyTo44M,
        fouty5To49M,
        above50M,
        unknownM,
        totalM,
        under1F,
        oneTo4F,
        fiveTo9F,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        foutyTo44F,
        fouty5To49F,
        above50F,
        unknownF,
        total,
        totalF,
        pid,
        msm,
        csw,
        pri);
  }
}
