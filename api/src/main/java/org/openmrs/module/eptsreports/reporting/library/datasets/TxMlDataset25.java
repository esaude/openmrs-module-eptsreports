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
        "totalIIT",
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
        new ColumnParameters("under1M", "under 1 year male", "gender=M|age=<1", "under1M");
    ColumnParameters oneTo4M =
        new ColumnParameters("oneTo4M", "1 - 4 years male", "gender=M|age=1-4", "oneTo4M");
    ColumnParameters fiveTo9M =
        new ColumnParameters("fiveTo9M", "5 - 9 years male", "gender=M|age=5-9", "fiveTo9M");
    ColumnParameters tenTo14M =
        new ColumnParameters("tenTo14M", "10 - 14 male", "gender=M|age=10-14", "tenTo14M");
    ColumnParameters fifteenTo19M =
        new ColumnParameters("fifteenTo19M", "15 - 19 male", "gender=M|age=15-19", "fifteenTo19M");
    ColumnParameters twentyTo24M =
        new ColumnParameters("twentyTo24M", "20 - 24 male", "gender=M|age=20-24", "twentyTo24M");
    ColumnParameters twenty5To29M =
        new ColumnParameters("twenty5To29M", "25 - 29 male", "gender=M|age=25-29", "twenty5To29M");
    ColumnParameters thirtyTo34M =
        new ColumnParameters("thirtyTo34M", "30 - 34 male", "gender=M|age=30-34", "thirtyTo34M");
    ColumnParameters thirty5To39M =
        new ColumnParameters("thirty5To39M", "35 - 39 male", "gender=M|age=35-39", "thirty5To39M");
    ColumnParameters fortyTo44M =
        new ColumnParameters("fortyTo44M", "40 - 44 male", "gender=M|age=40-44", "fortyTo44M");
    ColumnParameters forty5To49M =
        new ColumnParameters("forty5To49M", "45 - 49 male", "gender=M|age=45-49", "forty5To49M");
    // 50-54, 55-59, 60-64, 65+ male
    ColumnParameters fiftyTo54M =
        new ColumnParameters("fiftyTo54M", "50 - 54 male", "gender=M|age=50-54", "fiftyTo54M");
    ColumnParameters fifty5To59M =
        new ColumnParameters("fifty5To59M", "55 - 59 male", "gender=M|age=55-59", "fifty5To59M");
    ColumnParameters sixtyTo64M =
        new ColumnParameters("sixtyTo64M", "60 - 64 male", "gender=M|age=60-64", "sixtyTo64M");
    ColumnParameters above65M =
        new ColumnParameters("above65M", "65+ male", "gender=M|age=65+", "above65M");
    ColumnParameters unknownM =
        new ColumnParameters("unknownM", "Unknown age male", "gender=M|age=UK", "unknownM");
    ColumnParameters totalM =
        new ColumnParameters("totalM", "Total of Males", "gender=M", "subTotalM");

    ColumnParameters under1F =
        new ColumnParameters("under1F", "under 1 year female", "gender=F|age=<1", "under1F");
    ColumnParameters oneTo4F =
        new ColumnParameters("oneTo4F", "1 - 4 years female", "gender=F|age=1-4", "oneTo4F");
    ColumnParameters fiveTo9F =
        new ColumnParameters("fiveTo9F", "5 - 9 years female", "gender=F|age=5-9", "fiveTo9F");
    ColumnParameters tenTo14F =
        new ColumnParameters("tenTo14F", "10 - 14 female", "gender=F|age=10-14", "tenTo14F");
    ColumnParameters fifteenTo19F =
        new ColumnParameters(
            "fifteenTo19F", "15 - 19 female", "gender=F|age=15-19", "fifteenTo19F");
    ColumnParameters twentyTo24F =
        new ColumnParameters("twentyTo24F", "20 - 24 female", "gender=F|age=20-24", "twentyTo24F");
    ColumnParameters twenty5To29F =
        new ColumnParameters(
            "twenty5To29F", "25 - 29 female", "gender=F|age=25-29", "twenty5To29F");
    ColumnParameters thirtyTo34F =
        new ColumnParameters("thirtyTo34F", "30 - 34 female", "gender=F|age=30-34", "thirtyTo34F");
    ColumnParameters thirty5To39F =
        new ColumnParameters(
            "thirty5To39F", "35 - 39 female", "gender=F|age=35-39", "thirty5To39F");
    ColumnParameters fortyTo44F =
        new ColumnParameters("fortyTo44F", "40 - 44 female", "gender=F|age=40-44", "fortyTo44F");
    ColumnParameters forty5To49F =
        new ColumnParameters("forty5To49F", "45 - 49 female", "gender=F|age=45-49", "forty5To49F");
    // 50-54, 55-59, 60-64, 65+ female
    ColumnParameters fiftyTo54F =
        new ColumnParameters("fiftyTo54F", "50 - 54 female", "gender=F|age=50-54", "fiftyTo54F");
    ColumnParameters fifty5To59F =
        new ColumnParameters("fifty5To59F", "55 - 59 female", "gender=F|age=55-59", "fifty5To59F");
    ColumnParameters sixtyTo64F =
        new ColumnParameters("sixtyTo64F", "60 - 64 female", "gender=F|age=60-64", "sixtyTo64F");
    ColumnParameters above65F =
        new ColumnParameters("above65F", "65+ female", "gender=F|age=65+", "above65F");
    ColumnParameters unknownF =
        new ColumnParameters("unknownF", "Unknown age female", "gender=F|age=UK", "unknownF");
    ColumnParameters total = new ColumnParameters("totals", "Totals", "", "27");
    ColumnParameters totalF =
        new ColumnParameters("totalF", "Total of Females", "gender=F", "subTotalF");

    // Key population
    ColumnParameters pid = new ColumnParameters("pid", "PID", "KP=PID", "pid");
    ColumnParameters msm = new ColumnParameters("msm", "MSM", "KP=MSM", "msm");
    ColumnParameters csw = new ColumnParameters("msm", "CSW", "KP=CSW", "csw");
    ColumnParameters pri = new ColumnParameters("pri", "PRI", "KP=PRI", "pri");

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
        fortyTo44M,
        forty5To49M,
        fiftyTo54M,
        fifty5To59M,
        sixtyTo64M,
        above65M,
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
        fortyTo44F,
        forty5To49F,
        fiftyTo54F,
        fifty5To59F,
        sixtyTo64F,
        above65F,
        unknownF,
        total,
        totalF,
        pid,
        msm,
        csw,
        pri);
  }
}
