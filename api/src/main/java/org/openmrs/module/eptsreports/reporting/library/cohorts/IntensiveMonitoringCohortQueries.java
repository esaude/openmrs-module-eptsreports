package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntensiveMonitoringCohortQueries {

  private HivMetadata hivMetadata;

  private CommonMetadata commonMetadata;

  private CommonCohortQueries commonCohortQueries;

  private TbMetadata tbMetadata;

  private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;

  private final String MAPPING =
      "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}";

  @Autowired
  public IntensiveMonitoringCohortQueries(
      HivMetadata hivMetadata,
      CommonMetadata commonMetadata,
      CommonCohortQueries commonCohortQueries,
      TbMetadata tbMetadata,
      QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries) {
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
    this.commonCohortQueries = commonCohortQueries;
    this.tbMetadata = tbMetadata;
    this.qualityImprovement2020CohortQueries = qualityImprovement2020CohortQueries;
  }

  /**
   * Get CAT 7.1, 7.3, 7.5 Monitoria Intensiva MQHIV 2021 for the selected location and reporting
   * period Section 7.1 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7DenMOHIV202171Definition(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 7.1, 7.3, 7.5 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI71DEN",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQ7A(level),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    cd.addSearch(
        "MI71NUM",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQ7B(level),
            "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}"));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI71DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI71NUM");
    }
    return cd;
  }

  /**
   * Get CAT 7.2, 7.4, 7.6 Monitoria Intensiva MQHIV 2021 for the selected location and reporting
   * period Section 7.1 (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat7DenMOHIV202172Definition(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 7.2, 7.4, 7.6 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI72DEN",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQ7A(level),
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));
    cd.addSearch(
        "MI72NUM",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQ7B(level),
            "startDate=${revisionEndDate-8m+1d},endDate=${revisionEndDate-7m},location=${location}"));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI72DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI72NUM");
    }
    return cd;
  }

  /**
   * MEPTS-862_MI_REPORT_CAT13_P2 Get CAT 13.15, 13.16 and 13.17 P2 for Numerator and Denominator
   * Monitoria Intensiva MQHIV 2021 for the selected location and reporting period Section
   * (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMICat13Part2(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.15, 13.16 and 13.17 Numerator and Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));

    // DENOMINATOR
    if (level == 15) {
      cd.addSearch(
          "MI13DEN15",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod(), MAPPING));
    } else if (level == 16) {
      cd.addSearch(
          "MI13DEN16",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getgetMQC13P2DenMGInIncluisionPeriod33Month(),
              MAPPING));
    } else if (level == 17) {
      cd.addSearch(
          "MI13DEN17",
          EptsReportUtils.map(
              qualityImprovement2020CohortQueries.getMQC13P2DenMGInIncluisionPeriod33Days(),
              MAPPING));
    }

    // NUMERATOR
    if (level == 15) {
      cd.addSearch(
          "MI13NUM15",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num1(), MAPPING));
    } else if (level == 16) {
      cd.addSearch(
          "MI13NUM16",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num2(), MAPPING));
    } else if (level == 17) {
      cd.addSearch(
          "MI13NUM17",
          EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQC13P2Num3(), MAPPING));
    }

    if ("DEN15".equals(type)) {
      cd.setCompositionString("MI13DEN15");
    } else if ("DEN16".equals(type)) {
      cd.setCompositionString("MI13DEN16");
    } else if ("DEN17".equals(type)) {
      cd.setCompositionString("MI13DEN17");
    } else if ("NUM15".equals(type)) {
      cd.setCompositionString("MI13NUM15");
    } else if ("NUM16".equals(type)) {
      cd.setCompositionString("MI13NUM16");
    } else if ("NUM17".equals(type)) {
      cd.setCompositionString("MI13NUM17");
    }
    return cd;
  }

  /* Get CAT 13.1, 13.4, 13.6, 13.7, 13.8, 13.13 Monitoria Intensiva MQHIV 2021 for the selected
   * location and reporting period Section (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat13Den(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.1, 13.4, 13.6, 13.7, 13.8, 13.13 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    String mapp =
        "startDate=${revisionEndDate-2m+1d},endDate=${revisionEndDate-1m},location=${location}";
    cd.addSearch(
        "MI13DEN",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13(true, level), mapp));
    cd.addSearch(
        "MI13NUM",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ13(false, level), mapp));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI13DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI13NUM");
    }
    return cd;
  }

  /**
   * Get CAT 12.1, 12.2, 12.5, 12.6, 12.9, 12.10 Monitoria Intensiva MQHIV 2021 for the selected
   * location and reporting period (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getCat12P1DenNum(Integer level, String type) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 12.1, 12.2, 12.5, 12.6, 12.9, 12.10 numerator and denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    String MAPPING = "";
    if (level == 1 || level == 5 || level == 9)
      MAPPING =
          "startDate=${revisionEndDate-3m+1d},endDate=${revisionEndDate-2m},location=${location}";
    else
      MAPPING =
          "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}";
    cd.addSearch(
        "MI12P1DEN",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ12DEN(level), MAPPING));
    cd.addSearch(
        "MI12P1NUM",
        EptsReportUtils.map(qualityImprovement2020CohortQueries.getMQ12NUM(level), MAPPING));
    if ("DEN".equals(type)) {
      cd.setCompositionString("MI12P1DEN");
    } else if ("NUM".equals(type)) {
      cd.setCompositionString("MI12P1NUM");
    }
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.2 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN2(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.2 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN2",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13DEN2");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.2 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM2(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.2 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM2",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13NUM2");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.5 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN5(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.5 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN5",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13DEN5");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.5 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM5(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.5 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM5",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13NUM5");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.9 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN9(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.9 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN9",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN9");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.9 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM9(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.9 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM9",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM9");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.10 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN10(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.10 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN10",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN10");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.10 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM10(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.10 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM10",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM10");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.11 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN11(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.11 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN11",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13DEN11");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.11 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM11(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.11 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM11",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-5m+1d},endDate=${revisionEndDate-4m},location=${location}"));
    cd.setCompositionString("MI13NUM11");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.14 Denominator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13DEN14(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.14 Denominator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13DEN14",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3DEN(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13DEN14");
    return cd;
  }

  /**
   * Get CAT 13 Monitoria Intensiva MQHIV 2021 for the selected location and reporting period
   * Section 13.14 Numerator (endDateRevision)
   *
   * @return @{@link org.openmrs.module.reporting.cohort.definition.CohortDefinition}
   */
  public CohortDefinition getMI13NUM14(Integer indicator) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("MI 13.14 Numerator");
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addParameter(new Parameter("revisionEndDate", "revisionEndDate", Date.class));
    cd.addSearch(
        "MI13NUM14",
        EptsReportUtils.map(
            qualityImprovement2020CohortQueries.getMQC13P3NUM(indicator),
            "startDate=${revisionEndDate-10m+1d},endDate=${revisionEndDate-9m},location=${location}"));
    cd.setCompositionString("MI13NUM14");

    return cd;
  }
}
