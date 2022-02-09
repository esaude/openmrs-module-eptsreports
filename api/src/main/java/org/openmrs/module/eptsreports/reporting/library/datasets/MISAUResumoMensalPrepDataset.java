package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.eptsreports.reporting.library.cohorts.PrepNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.RMPREPCohortQueries;
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

import java.util.Arrays;
import java.util.List;

@Component
public class MISAUResumoMensalPrepDataset extends BaseDataSet {

  private final EptsGeneralIndicator eptsGeneralIndicator;

  private final EptsCommonDimension eptsCommonDimension;

  private final PrepNewCohortQueries prepNewCohortQueries;

  private final RMPREPCohortQueries rmprepCohortQueries;

  @Autowired
  public MISAUResumoMensalPrepDataset(
      EptsGeneralIndicator eptsGeneralIndicator,
      EptsCommonDimension eptsCommonDimension,
      PrepNewCohortQueries prepNewCohortQueries,
      RMPREPCohortQueries rmprepCohortQueries) {
    this.eptsGeneralIndicator = eptsGeneralIndicator;
    this.eptsCommonDimension = eptsCommonDimension;
    this.prepNewCohortQueries = prepNewCohortQueries;
    this.rmprepCohortQueries = rmprepCohortQueries;
  }

  public DataSetDefinition constructResumoMensalPrepDataset() {

    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();

    dsd.setName("Resumo Mensal PrEP Dataset");
    dsd.addParameters(getParameters());

    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String mappingsKp = "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}";

    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.getPatientAgeOnReportEndDate(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    dsd.addDimension(
        "KP", EptsReportUtils.map(eptsCommonDimension.getKeyPopsDimension(), mappingsKp));

    dsd.addDimension("gender", EptsReportUtils.map(eptsCommonDimension.gender(), ""));

    dsd.addDimension(
        "maternity",
        EptsReportUtils.map(
            eptsCommonDimension.getPregnantAndBreastfeedingPatientsBasedOnPrep(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    dsd.addDimension(
        "TG",
        EptsReportUtils.map(
            eptsCommonDimension.getTargetGroupDimension(),
            "onOrBefore=${endDate},location=${location}"));
    // A1
    dsd.addColumn(
        "TTA1",
        "Total de Utentes elegíveis a PrEP durante o período de reporte",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de Utentes elegíveis a PrEP durante o período de reporte",
                EptsReportUtils.map(rmprepCohortQueries.getClientsEligibleForPrep(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "A1",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(rmprepCohortQueries.getClientsEligibleForPrep(), mappings)),
            mappings),
        getColumnsDisaggregations());

    // B1
    dsd.addColumn(
        "TTB1",
        "Total de Utentes que iniciaram PrEP pela 1ª vez durante o período de reporte",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de Utentes que iniciaram PrEP pela 1ª vez durante o período de reporte",
                EptsReportUtils.map(
                    prepNewCohortQueries.getClientsWhoNewlyInitiatedPrep(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "B1",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(
                    prepNewCohortQueries.getClientsWhoNewlyInitiatedPrep(), mappings)),
            mappings),
        getColumnsDisaggregations());

    // B2
    dsd.addColumn(
        "TTB2",
        "Total de de Novos Inícios que retornaram a PrEP durante o período de reporte",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de Novos Inícios que retornaram a PrEP durante o período de reporte",
                EptsReportUtils.map(rmprepCohortQueries.getClientsReturnedToPrep(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "B2",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(rmprepCohortQueries.getClientsReturnedToPrep(), mappings)),
            mappings),
        getColumnsDisaggregations());

    // C1
    dsd.addColumn(
        "TTC1",
        "Total de Utentes que receberam a PrEP durante o período de reporte",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de Utentes que receberam a PrEP durante o período de reporte",
                EptsReportUtils.map(rmprepCohortQueries.getClientsWhoReceivedPrep(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "C1",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(rmprepCohortQueries.getClientsWhoReceivedPrep(), mappings)),
            mappings),
        getColumnsDisaggregations());

    // D1
    dsd.addColumn(
        "TTD1",
        "Total de Utentes em PrEP por 3 meses consecutivos após terem iniciado a PrEP",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Total de Utentes em PrEP por 3 meses consecutivos após terem iniciado a PrEP",
                EptsReportUtils.map(
                    rmprepCohortQueries.getClientsFromB1PreviousPeriod(), mappings)),
            mappings),
        "");

    addRow(
        dsd,
        "D1",
        "Age and Gender",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "Age and Gender",
                EptsReportUtils.map(
                    rmprepCohortQueries.getClientsFromB1PreviousPeriod(), mappings)),
            mappings),
        getColumnsDisaggregations());

    return dsd;
  }

  /**
   * <b>Description:</b> Creates disaggregation based on Age, Gender, Maternity and Key Population
   * and Target Group.
   *
   * @return
   */
  private List<ColumnParameters> getColumnsDisaggregations() {
    // age and gender
    ColumnParameters tenTo14M =
        new ColumnParameters(
            "tenTo14M",
            "10 - 14 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "01");

    ColumnParameters fifteenTo19M =
        new ColumnParameters(
            "fifteenTo19M",
            "15 - 19 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "02");

    ColumnParameters twentyTo24M =
        new ColumnParameters(
            "twentyTo24M",
            "20 - 24 male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.between20And24Years)
                .getDimensions(),
            "03");

    ColumnParameters above25M =
        new ColumnParameters(
            "above25M",
            "25+ male",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male)
                .and(DimensionKeyForAge.overOrEqualTo25Years)
                .getDimensions(),
            "04");

    ColumnParameters totalM =
        new ColumnParameters(
            "totalM",
            "Total of Males",
            EptsCommonDimensionKey.of(DimensionKeyForGender.male).getDimensions(),
            "05");

    ColumnParameters tenTo14F =
        new ColumnParameters(
            "tenTo14F",
            "10 - 14 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between10And14Years)
                .getDimensions(),
            "06");

    ColumnParameters fifteenTo19F =
        new ColumnParameters(
            "fifteenTo19F",
            "15 - 19 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between15And19Years)
                .getDimensions(),
            "07");

    ColumnParameters twentyTo24F =
        new ColumnParameters(
            "twentyTo24F",
            "20 - 24 female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.between20And24Years)
                .getDimensions(),
            "08");

    ColumnParameters above25F =
        new ColumnParameters(
            "above25F",
            "25+ female",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female)
                .and(DimensionKeyForAge.overOrEqualTo25Years)
                .getDimensions(),
            "09");

    ColumnParameters totalF =
        new ColumnParameters(
            "totalF",
            "Total of Females",
            EptsCommonDimensionKey.of(DimensionKeyForGender.female).getDimensions(),
            "10");

    // Maternity
    ColumnParameters tenTo14pregnant =
        new ColumnParameters(
            "tenTo14pregnant", "10 - 14 Pregnant", "age=10-14|maternity=pregnant", "11");
    ColumnParameters above15pregnant =
        new ColumnParameters("above15pregnant", "15+ Pregnant", "age=15+|maternity=pregnant", "12");
    ColumnParameters totalPregnant =
        new ColumnParameters("totalPregnant", "Total of Pregnant", "maternity=pregnant", "13");
    ColumnParameters tenTo14breastfeeding =
        new ColumnParameters(
            "tenTo14breastfeeding",
            "10 - 14 Breastfeeding",
            "age=10-14|maternity=breastfeeding",
            "14");
    ColumnParameters above15breastfeeding =
        new ColumnParameters(
            "above15breastfeeding", "15+ Breastfeeding", "age=15+|maternity=breastfeeding", "15");
    ColumnParameters totalBreastfeeding =
        new ColumnParameters(
            "totalBreastfeeding", "Total of Breastfeeding", "maternity=breastfeeding", "16");

    // Key population
    ColumnParameters pid = new ColumnParameters("pid", "People who inject drugs", "KP=PID", "17");
    ColumnParameters msm = new ColumnParameters("msm", "Men who have sex with men", "KP=MSM", "18");
    ColumnParameters sw = new ColumnParameters("sw", "Sex workers", "KP=SW", "19");
    ColumnParameters pri =
        new ColumnParameters("pri", "People in prison and other closed settings", "KP=PRI", "20");
    ColumnParameters tg = new ColumnParameters("tg", "Transgender", "KP=TG", "22");

    // Target group
    ColumnParameters ayr =
        new ColumnParameters("ayr", "Adolescents and Youth at Risk", "TG=AYR", "23");
    ColumnParameters pw =
        new ColumnParameters("pw", "Pregnant Woman 15+ at Risk", "age=15+|TG=PW", "24");
    ColumnParameters bw =
        new ColumnParameters("bw", "Breastfeeding Woman 15+ at Risk", "age=15+|TG=BW", "25");
    ColumnParameters mil = new ColumnParameters("mil", "Military", "TG=MIL", "26");
    ColumnParameters min = new ColumnParameters("min", "Miner", "TG=MIN", "27");
    ColumnParameters td = new ColumnParameters("td", "Long Course Truck Driver", "TG=TD", "28");
    ColumnParameters cs = new ColumnParameters("cs", "Serodiscordant Couples", "TG=CS", "29");

    return Arrays.asList(
        tenTo14M,
        fifteenTo19M,
        twentyTo24M,
        above25M,
        totalM,
        tenTo14F,
        fifteenTo19F,
        twentyTo24F,
        above25F,
        totalF,
        tenTo14pregnant,
        above15pregnant,
        totalPregnant,
        tenTo14breastfeeding,
        above15breastfeeding,
        totalBreastfeeding,
        pid,
        msm,
        sw,
        pri,
        tg,
        ayr,
        pw,
        bw,
        mil,
        min,
        td,
        cs);
  }
}
