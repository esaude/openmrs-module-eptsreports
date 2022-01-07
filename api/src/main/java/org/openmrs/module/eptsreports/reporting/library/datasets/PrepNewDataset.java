package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.DimensionKeyForAge;
import org.openmrs.module.eptsreports.reporting.library.dimensions.DimensionKeyForGender;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimensionKey;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrepNewDataset extends BaseDataSet {

  private PrepNewCohortQueries prepNewCohortQueries;

  private EptsGeneralIndicator eptsGeneralIndicator;

  private EptsCommonDimension eptsCommonDimension;

  @Autowired
  public PrepNewDataset(
      PrepNewCohortQueries prepNewCohortQueries,
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension) {
    this.prepNewCohortQueries = prepNewCohortQueries;
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.eptsCommonDimension = eptsCommonDimension;
  }

  /**
   * <b>Description:</b> Constructs PrEP New Dataset
   *
   * @return
   */
  public DataSetDefinition constructPrepNewDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    dsd.setName("PrEP New Dataset");
    dsd.addParameters(getParameters());
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String mappingsKp = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";

    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.getPatientAgeBasedOnPrepStartDate(),
            "endDate=${endDate},location=${location}"));

    dsd.addColumn(
        "TOTAL",
        "Total of Clients Who Newly Initiated PrEP",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total of Clients Who Newly Initiated PrEP",
                EptsReportUtils.map(
                    prepNewCohortQueries.getClientsWhoNewlyInitiatedPrep(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "P2",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(
                    prepNewCohortQueries.getClientsWhoNewlyInitiatedPrep(), mappings)),
            mappings),
        getColumnsForAgeAndGenderAndKeyPop());

    return dsd;
  }

  /**
   * <b>Description:</b> Creates disaggregation based on Age and Gender
   *
   * @return
   */
  private List<ColumnParameters> getColumnsForAgeAndGenderAndKeyPop() {
    ColumnParameters fifteenTo19M =
        new ColumnParameters(
            "fifteenTo19M",
            "15 - 19 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "01");
    ColumnParameters twentyTo24M =
        new ColumnParameters(
            "twentyTo24M",
            "20 - 24 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between20And24Years)
                .getDimensions(),
            "02");
    ColumnParameters twenty5To29M =
        new ColumnParameters(
            "twenty4To29M",
            "25 - 29 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between25And29Years)
                .getDimensions(),
            "03");
    ColumnParameters thirtyTo34M =
        new ColumnParameters(
            "thirtyTo34M",
            "30 - 34 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between30And34Years)
                .getDimensions(),
            "04");
    ColumnParameters thirty5To39M =
        new ColumnParameters(
            "thirty5To39M",
            "35 - 39 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between35And39Years)
                .getDimensions(),
            "05");
    ColumnParameters fortyTo44M =
        new ColumnParameters(
            "fortyTo44M",
            "40 - 44 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between40And44Years)
                .getDimensions(),
            "06");
    ColumnParameters forty5To49M =
        new ColumnParameters(
            "forty5To49M",
            "45 - 49 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between45and49Years)
                .getDimensions(),
            "07");
    ColumnParameters above50M =
        new ColumnParameters(
            "above50M",
            "50+ male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.overOrEqualTo50Years)
                .getDimensions(),
            "08");
    ColumnParameters unknownM =
        new ColumnParameters(
            "unknownM",
            "Unknown age male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.unknown)
                .getDimensions(),
            "09");
    ColumnParameters totalM =
        new ColumnParameters(
            "totalM",
            "Total of Males",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male).getDimensions(),
            "20");

    ColumnParameters fifteenTo19F =
        new ColumnParameters(
            "fifteenTo19F",
            "15 - 19 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "10");
    ColumnParameters twentyTo24F =
        new ColumnParameters(
            "twentyTo24F",
            "20 - 24 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between20And24Years)
                .getDimensions(),
            "11");
    ColumnParameters twenty5To29F =
        new ColumnParameters(
            "twenty4To29F",
            "25 - 29 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between25And29Years)
                .getDimensions(),
            "12");
    ColumnParameters thirtyTo34F =
        new ColumnParameters(
            "thirtyTo34F",
            "30 - 34 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between30And34Years)
                .getDimensions(),
            "13");
    ColumnParameters thirty5To39F =
        new ColumnParameters(
            "thirty5To39F",
            "35 - 39 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between35And39Years)
                .getDimensions(),
            "14");
    ColumnParameters fortyTo44F =
        new ColumnParameters(
            "fortyTo44F",
            "40 - 44 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between40And44Years)
                .getDimensions(),
            "15");
    ColumnParameters forty5To49F =
        new ColumnParameters(
            "forty5To49F",
            "45 - 49 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between45and49Years)
                .getDimensions(),
            "16");
    ColumnParameters above50F =
        new ColumnParameters(
            "above50F",
            "50+ female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.overOrEqualTo50Years)
                .getDimensions(),
            "17");
    ColumnParameters unknownF =
        new ColumnParameters(
            "unknownF",
            "Unknown age female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.unknown)
                .getDimensions(),
            "18");
    ColumnParameters totalF =
        new ColumnParameters(
            "totalF",
            "Total of Females",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female).getDimensions(),
            "19");

    // Key population
    ColumnParameters pid = new ColumnParameters("pid", "People who inject drugs", "KP=PID", "21");
    ColumnParameters msm = new ColumnParameters("msm", "Men who have sex with men", "KP=MSM", "22");
    ColumnParameters csw = new ColumnParameters("csw", "Female sex workers", "KP=CSW", "23");
    ColumnParameters pri =
        new ColumnParameters("pri", "People in prison and other closed settings", "KP=PRI", "24");
    ColumnParameters msw = new ColumnParameters("msw", "Male sex workers", "KP=MSW", "25");
    ColumnParameters tg = new ColumnParameters("tg", "Transgender", "KP=TG", "26");

    return Arrays.asList(
        fifteenTo19M,
        twentyTo24M,
        twenty5To29M,
        thirtyTo34M,
        thirty5To39M,
        fortyTo44M,
        forty5To49M,
        above50M,
        unknownM,
        totalM,
        fifteenTo19F,
        twentyTo24F,
        twenty5To29F,
        thirtyTo34F,
        thirty5To39F,
        fortyTo44F,
        forty5To49F,
        above50F,
        unknownF,
        totalF,
        pid,
        msm,
        csw,
        pri,
        msw,
        tg);
  }
}
