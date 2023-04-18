package org.openmrs.module.eptsreports.reporting.library.datasets;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.dimensions.AgeDimensionCohortInterface;
import org.openmrs.module.eptsreports.reporting.library.dimensions.EptsCommonDimension;
import org.openmrs.module.eptsreports.reporting.library.dimensions.TxRetDimensionCohort;
import org.openmrs.module.eptsreports.reporting.library.indicators.EptsGeneralIndicator;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.indicator.CohortIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EriDSDDataset extends BaseDataSet {
  private static final String N1 =
      "N1: Number of Non-pregnant and Non-Breastfeeding patients who are not on TB treatment and Eligible for DSD who are in at least one DSD model for stable patients (GA, DT, DS, FR, DCA, DD)";

  private static final String N2 =
      "N2: Number of all patients currently on ART who are included in DSD model: Dispensa Trimestral (DT)";

  private static final String N3 =
      "N3: Number of all patients currently on ART who are included in DSD model: Dispensa Semestral (DS)";

  private static final String N4 =
      "N4: Number of all patients currently on ART who are included in DSD model: Dispensa Anual (DA)";

  private static final String N5 =
      "N5: Number of all patients currently on ART who are included in DSD model: Dispensa Decentralizada (DD)";

  private static final String N6 =
      "N6: Number of all patients currently on ART who are included in DSD model: Dispensa Comunitária pelo APE (DCA)";

  private static final String N7 =
      "N7: Number of all patients currently on ART who are included in DSD model: Fluxo Rápido (FR)";

  private static final String N8 =
      "N8: Number of all patients currently on ART who are included in DSD model: GAAC (GA)";

  private static final String N9 =
      "N9: Number of all patients currently on ART who are included in DSD model: Dispensa Comunitária pelo Provedor (DCP)";

  private static final String N10 =
      "N10: Number of all patients currently on ART who are included in DSD model: Brigada Móvel (BM)";

  private static final String N11 =
      "N11: Number of all patients currently on ART who are included in DSD model: Clinica Móvel (CM)";

  private static final String N12 =
      "N12: Number of all patients currently on ART who are included in DSD model: Abordagem Familiar (AF)";

  private static final String N13 =
      "N13: Number of all patients currently on ART who are included in DSD model: Clube de Adesão (CA)";

  private static final String N14 =
      "N14: Number of all patients currently on ART who are included in DSD model: Extensão Horário (EH)";

  private static final String N15 =
      "N15: Number of all patients currently on ART who are included in DSD model: Paragem Única de Tuberculose (TB)";

  private static final String N16 =
      "N16: Number of all patients currently on ART who are included in DSD model: Paragem Única de Cuidados e Tratamento (CT)";

  private static final String N17 =
      "N17: Number of all patients currently on ART who are included in DSD model: Paragem Única Serviços Amigos de Adolescentes e Jovens (SAAJ)";

  private static final String N18 =
      "N18: Number of all patients currently on ART who are included in DSD model: Paragem Única Saúde Materno-Infantil (SMI)";

  private static final String N19 =
      "N19: Number of all patients currently on ART who are included in DSD model: Doença Avançada de HIV (DAH)";

  private static final String N20 =
      "N20: Number of active patients on ART who are included in DSD model: Dispensa Bimestral (DB)";

  private static final String N21 =
      "N21: Number of patients active on ART who are included in at least one DSD model (DB, DT, DS, DA, DD, DCP, DCA, BM, CM, AF, FR, GA, CA, EH, TB, C&T, SAAJ, SMI).";

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;
  @Autowired private EptsGeneralIndicator eptsGeneralIndicator;
  @Autowired private EptsCommonDimension eptsCommonDimension;
  @Autowired private TxRetDimensionCohort txRetDimensionCohort;

  @Autowired
  @Qualifier("commonAgeDimensionCohort")
  private AgeDimensionCohortInterface ageDimensionCohort;

  public DataSetDefinition constructEriDSDDataset() {
    CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
    String mappings = "endDate=${endDate},location=${location}";

    dsd.setName("DSD Data Set");
    dsd.addParameters(getParameters());
    dsd.addDimension(
        "age",
        EptsReportUtils.map(
            eptsCommonDimension.age(ageDimensionCohort), "effectiveDate=${endDate}"));
    dsd.addDimension("eligible", mapStraightThrough(eptsCommonDimension.getDSDEligibleDimension()));
    dsd.addDimension(
        "pregnantBreastfeedingTb",
        mapStraightThrough(
            eptsCommonDimension.getDSDNonPregnantNonBreastfeedingAndNotOnTbDimension()));
    dsd.addDimension(
        "pregnantOrBreastFeeding",
        EptsReportUtils.map(
            txRetDimensionCohort.pregnantOrBreastFeeding(),
            "startDate=${endDate-24m+1d},endDate=${endDate-12m},location=${location}"));

    dsd.setName("total");
    dsd.addColumn(
        "D1T",
        "DSD D1 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D1 Total", EptsReportUtils.map(eriDSDCohortQueries.getD1(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D1SNPNB",
        "D1 Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D1SNPNBC",
        "D1 Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D1SNPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD1(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2T",
        "DSD D2 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D2 Total", EptsReportUtils.map(eriDSDCohortQueries.getD2(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D2NPNB",
        "D2 Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D2NPNBC",
        "D2 Non-pregnant and Non-Breastfeeding Children  By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD2(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D2PW",
        "D2 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnantD2(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D2BW",
        "D2 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D2BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingD2(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D3T",
        "DSD D3 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D3 Total", EptsReportUtils.map(eriDSDCohortQueries.getD3(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D3NPNB",
        "D3 Non-pregnant and Non-Breastfeeding Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D3NPNB",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD3(),
                    mappings)),
            mappings),
        "age=15+");
    addRow(
        dsd,
        "D3NPNBC",
        "D3 Non-pregnant and Non-Breastfeeding Children By age",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D3NPNBC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreNotPregnantAndNotBreastfeedingD3(),
                    mappings)),
            mappings),
        getChildrenColumn());
    dsd.addColumn(
        "D3PW",
        "D3 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D3PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnantD3(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D3BW",
        "D3 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D3BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingD3(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D4T",
        "DSD D4 Total",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "DSD D4 Total", EptsReportUtils.map(eriDSDCohortQueries.getD4(), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "D4BWA",
        "D4 Breastfeeding Woman Adults (>=15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D4BWA",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingD4(), mappings)),
            mappings),
        "age=15+");
    dsd.addColumn(
        "D4BWC",
        "D4 Breastfeeding Woman Children (<15)",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "D4BWC",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeedingD4(), mappings)),
            mappings),
        "age=<15");
    dsd.addColumn(
        "N1PW",
        "N1 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(1), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N1BW",
        "N1 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N1BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(1), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N3PW",
        "N3 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(3), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N3BW",
        "N3 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N3BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(3), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4PW",
        "N4 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(4), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N4BW",
        "N4 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N4BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(4), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5PW",
        "N5 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(5), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N5BW",
        "N5 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N5BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(5), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N6PW",
        "N6 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N6PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(6), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N6BW",
        "N6 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N6BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(6), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N7PW",
        "N7 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(7), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N7BW",
        "N7 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N7BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(7), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N8PW",
        "N8 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(8), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N8BW",
        "N8 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N8BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(8), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N9PW",
        "N9 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N9PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(9), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N9BW",
        "N9 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N9BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(9), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N10PW",
        "N10 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N10PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(10), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N10BW",
        "N10 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N10BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(10), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N11PW",
        "N11 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N11PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(11), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N11BW",
        "N11 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N11BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(11), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N12PW",
        "N12 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N12PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(12), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N12BW",
        "N12 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N12BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(12), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N13PW",
        "N13 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N13PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(13), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N13BW",
        "N13 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N13BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(13), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N14PW",
        "N14 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N14PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(14), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N14BW",
        "N14 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N14BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(14), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N15PW",
        "N15 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N15PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(15), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N15BW",
        "N15 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N15BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(15), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N16PW",
        "N16 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N16PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(16), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N16BW",
        "N16 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N16BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(16), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N17PW",
        "N17 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N17PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(17), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N17BW",
        "N17 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N17BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(17), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N18PW",
        "N18 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N18PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(18), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N18BW",
        "N18 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N18BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(18), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N19PW",
        "N19 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N19PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(19), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N19BW",
        "N19 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N19BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(19), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N20PW",
        "N20 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N20PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(20), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N20BW",
        "N20 Breastfeeding Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N20BW",
                EptsReportUtils.map(
                    eriDSDCohortQueries.getPatientsWhoAreBreastfeeding(20), mappings)),
            mappings),
        "");
    dsd.addColumn(
        "N21PW",
        "N21 Pregnant Women",
        EptsReportUtils.map(
            eptsGeneralIndicator.getIndicator(
                "N21PW",
                EptsReportUtils.map(eriDSDCohortQueries.getPatientsWhoArePregnant(21), mappings)),
            mappings),
        "");

    addRow(dsd, "N1", N1, mapStraightThrough(getN1()), getDisags());
    addRow(dsd, "N2", N2, mapStraightThrough(getN2()), getDisags());
    addRow(dsd, "N3", N3, mapStraightThrough(getN3()), getDisags());
    addRow(dsd, "N4", N4, mapStraightThrough(getN4()), getDisags());
    addRow(dsd, "N5", N5, mapStraightThrough(getN5()), getDisags());
    addRow(dsd, "N6", N6, mapStraightThrough(getN6()), getDisags());
    addRow(dsd, "N7", N7, mapStraightThrough(getN7()), getDisags());
    addRow(dsd, "N8", N8, mapStraightThrough(getN8()), getDisags());
    addRow(dsd, "N9", N9, mapStraightThrough(getN9()), getDisags());
    addRow(dsd, "N10", N10, mapStraightThrough(getN10()), getDisags());
    addRow(dsd, "N11", N11, mapStraightThrough(getN11()), getDisags());
    addRow(dsd, "N12", N12, mapStraightThrough(getN12()), getDisags());
    addRow(dsd, "N13", N13, mapStraightThrough(getN13()), getDisags());
    addRow(dsd, "N14", N14, mapStraightThrough(getN14()), getDisags());
    addRow(dsd, "N15", N15, mapStraightThrough(getN15()), getDisags());
    addRow(dsd, "N16", N16, mapStraightThrough(getN16()), getDisags());
    addRow(dsd, "N17", N17, mapStraightThrough(getN17()), getDisags());
    addRow(dsd, "N18", N18, mapStraightThrough(getN18()), getDisags());
    addRow(dsd, "N19", N19, mapStraightThrough(getN19()), getDisags());
    addRow(dsd, "N20", N20, mapStraightThrough(getN20()), getDisags());
    addRow(dsd, "N21", N21, mapStraightThrough(getN21()), getDisags());

    return dsd;
  }

  /**
   * 2-14 years old children List
   *
   * @return java.util.List
   */
  private List<ColumnParameters> getChildrenColumn() {
    ColumnParameters twoTo4 = new ColumnParameters("twoTo4", "2-4", "age=2-4", "01");
    ColumnParameters fiveTo9 = new ColumnParameters("fiveTo9", "5-9", "age=5-9", "02");
    ColumnParameters tenTo14 = new ColumnParameters("tenTo14", "10-14", "age=10-14", "03");
    ColumnParameters lesThan2 = new ColumnParameters("lesThan2", "<2", "age=<2", "04");

    return Arrays.asList(lesThan2, twoTo4, fiveTo9, tenTo14);
  }

  private CohortIndicator getN1() {
    return eptsGeneralIndicator.getIndicator("N1", mapStraightThrough(eriDSDCohortQueries.getN1()));
  }

  private CohortIndicator getN2() {
    return eptsGeneralIndicator.getIndicator("N2", mapStraightThrough(eriDSDCohortQueries.getN2()));
  }

  private CohortIndicator getN3() {
    return eptsGeneralIndicator.getIndicator("N3", mapStraightThrough(eriDSDCohortQueries.getN3()));
  }

  private CohortIndicator getN4() {
    return eptsGeneralIndicator.getIndicator("N4", mapStraightThrough(eriDSDCohortQueries.getN4()));
  }

  private CohortIndicator getN5() {
    return eptsGeneralIndicator.getIndicator("N5", mapStraightThrough(eriDSDCohortQueries.getN5()));
  }

  private CohortIndicator getN6() {
    return eptsGeneralIndicator.getIndicator("N6", mapStraightThrough(eriDSDCohortQueries.getN6()));
  }

  private CohortIndicator getN7() {
    return eptsGeneralIndicator.getIndicator("N7", mapStraightThrough(eriDSDCohortQueries.getN7()));
  }

  private CohortIndicator getN8() {
    return eptsGeneralIndicator.getIndicator("N8", mapStraightThrough(eriDSDCohortQueries.getN8()));
  }

  private CohortIndicator getN9() {
    return eptsGeneralIndicator.getIndicator("N9", mapStraightThrough(eriDSDCohortQueries.getN9()));
  }

  private CohortIndicator getN10() {
    return eptsGeneralIndicator.getIndicator(
        "N10", mapStraightThrough(eriDSDCohortQueries.getN10()));
  }

  private CohortIndicator getN11() {
    return eptsGeneralIndicator.getIndicator(
        "N11", mapStraightThrough(eriDSDCohortQueries.getN11()));
  }

  private CohortIndicator getN12() {
    return eptsGeneralIndicator.getIndicator(
        "N12", mapStraightThrough(eriDSDCohortQueries.getN12()));
  }

  private CohortIndicator getN13() {
    return eptsGeneralIndicator.getIndicator(
        "N13", mapStraightThrough(eriDSDCohortQueries.getN13()));
  }

  private CohortIndicator getN14() {
    return eptsGeneralIndicator.getIndicator(
        "N14", mapStraightThrough(eriDSDCohortQueries.getN14()));
  }

  private CohortIndicator getN15() {
    return eptsGeneralIndicator.getIndicator(
        "N15", mapStraightThrough(eriDSDCohortQueries.getN15()));
  }

  private CohortIndicator getN16() {
    return eptsGeneralIndicator.getIndicator(
        "N16", mapStraightThrough(eriDSDCohortQueries.getN16()));
  }

  private CohortIndicator getN17() {
    return eptsGeneralIndicator.getIndicator(
        "N17", mapStraightThrough(eriDSDCohortQueries.getN17()));
  }

  private CohortIndicator getN18() {
    return eptsGeneralIndicator.getIndicator(
        "N18", mapStraightThrough(eriDSDCohortQueries.getN18()));
  }

  private CohortIndicator getN19() {
    return eptsGeneralIndicator.getIndicator(
        "N19", mapStraightThrough(eriDSDCohortQueries.getN19()));
  }

  private CohortIndicator getN20() {
    return eptsGeneralIndicator.getIndicator(
        "N20", mapStraightThrough(eriDSDCohortQueries.getN20()));
  }

  private CohortIndicator getN21() {
    return eptsGeneralIndicator.getIndicator(
        "N21", mapStraightThrough(eriDSDCohortQueries.getN21()));
  }

  private List<ColumnParameters> getDisags() {
    return Arrays.asList(
        new ColumnParameters("Total", "Total", "", "01"),
        new ColumnParameters("Eligible Sub-Total", "Eligible Sub-Total", "eligible=E", "02"),
        new ColumnParameters("Eligible Adults", "Eligible Adults", "eligible=E|age=15+", "03"),
        new ColumnParameters("Eligible 2-4", "Eligible 2-4", "eligible=E|age=2-4", "04"),
        new ColumnParameters("Eligible 5-9", "Eligible 5-9", "eligible=E|age=5-9", "05"),
        new ColumnParameters("Eligible 10-14", "Eligible 10-14", "eligible=E|age=10-14", "06"),
        new ColumnParameters(
            "Not Eligible Sub-Total", "Not Eligible Sub-Total", "eligible=NE", "07"),
        new ColumnParameters(
            "Not Eligible Adults",
            "Not Eligible Adults",
            "eligible=NE|pregnantBreastfeedingTb=NPNB|age=15+",
            "08"),
        new ColumnParameters(
            "Not Eligible <2",
            "Not Eligible <2",
            "eligible=NE|pregnantBreastfeedingTb=NPNB|age=<2",
            "09"),
        new ColumnParameters(
            "Not Eligible 2-4",
            "Not Eligible 2-4",
            "eligible=NE|pregnantBreastfeedingTb=NPNB|age=2-4",
            "10"),
        new ColumnParameters(
            "Not Eligible 5-9",
            "Not Eligible 5-9",
            "eligible=NE|pregnantBreastfeedingTb=NPNB|age=5-9",
            "11"),
        new ColumnParameters(
            "Not Eligible 10-14",
            "Not Eligible 10-14",
            "eligible=NE|pregnantBreastfeedingTb=NPNB|age=10-14",
            "12"),
        new ColumnParameters(
            "Not Eligible Pregnant Women",
            "Not Eligible Pregnant Women",
            "eligible=NE|pregnantBreastfeedingTb=P",
            "15"),
        new ColumnParameters(
            "Not Eligible Breastfeeding Women",
            "Not Eligible Breastfeeding Women",
            "eligible=NE|pregnantBreastfeedingTb=B",
            "16"),

        // N9 - N19 disags
        new ColumnParameters("Adults", "Adults", "pregnantBreastfeedingTb=NPNB|age=15+", "26"),
        new ColumnParameters("<2", "<2", "pregnantBreastfeedingTb=NPNB|age=<2", "27"),
        new ColumnParameters("2-4", "2-4", "pregnantBreastfeedingTb=NPNB|age=2-4", "28"),
        new ColumnParameters("5-9", "5-9", "pregnantBreastfeedingTb=NPNB|age=5-9", "29"),
        new ColumnParameters("10-14", "10-14", "pregnantBreastfeedingTb=NPNB|age=10-14", "30"),
        new ColumnParameters("Pregnant Women", "Pregnant Women", "pregnantBreastfeedingTb=P", "31"),
        new ColumnParameters(
            "Breastfeeding Women", "Breastfeeding Women", "pregnantBreastfeedingTb=B", "32"),

        // N20 disags
        new ColumnParameters(
            "Eligible D4 Sub-Total", "Eligible D4 Sub-Total", "eligible=ED4", "17"),
        new ColumnParameters(
            "Eligible D4 Adults", "Eligible D4 Adults", "eligible=ED4|age=15+", "18"),
        new ColumnParameters(
            "Eligible D4 Adults", "Eligible D4 Adults", "eligible=ED4|age=<15", "19"),
        new ColumnParameters(
            "Not Eligible D4 Sub-Total", "Not Eligible D4 Sub-Total", "eligible=NED4", "20"),
        new ColumnParameters(
            "Not Eligible D4 Adults",
            "Not Eligible D4 Adults",
            "eligible=NED4|pregnantBreastfeedingTb=NPNB|age=15+",
            "21"),
        new ColumnParameters(
            "Not Eligible D4 <2",
            "Not Eligible D4 <2",
            "eligible=NED4|pregnantBreastfeedingTb=NPNB|age=<2",
            "22"),
        new ColumnParameters(
            "Not Eligible D4 2-4",
            "Not Eligible D4 2-4",
            "eligible=NED4|pregnantBreastfeedingTb=NPNB|age=2-4",
            "23"),
        new ColumnParameters(
            "Not Eligible D4 5-9",
            "Not Eligible D4 5-9",
            "eligible=NED4|pregnantBreastfeedingTb=NPNB|age=5-9",
            "24"),
        new ColumnParameters(
            "Not Eligible D4 10-14",
            "Not Eligible D4 10-14",
            "eligible=NED4|pregnantBreastfeedingTb=NPNB|age=10-14",
            "25"),
        new ColumnParameters(
            "Not Eligible D4 Pregnant Women",
            "Not Eligible D4 Pregnant Women",
            "eligible=NED4|pregnantBreastfeedingTb=P",
            "13"),
        new ColumnParameters(
            "Not Eligible D4 Breastfeeding Women",
            "Not Eligible D4 Breastfeeding Women",
            "eligible=NED4|pregnantBreastfeedingTb=B",
            "14"));
  }
}
