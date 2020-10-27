package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXRetQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXRetCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private GenderCohortQueries genderCohorts;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  private final String mappings =
      "startDate=${startDate},endDate=${endDate},location=${location},months=${months}";

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Data Inicial", Date.class));
    cd.addParameter(new Parameter("endDate", "Data Final", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("months", "Número de Meses (12, 24, 36)", Integer.class));
  }

  private CohortDefinition cohortDefinition(CohortDefinition cohortDefinition) {
    addParameters(cohortDefinition);
    return cohortDefinition;
  }

  private CohortDefinition obitoTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("obito", TXRetQueries.obitoTwelveMonths()));
  }

  private CohortDefinition suspensoTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("suspenso", TXRetQueries.suspensoTwelveMonths()));
  }

  private CohortDefinition initiotArvTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql("initiotArv", TXRetQueries.initiotArvTwelveMonths()));
  }

  private CohortDefinition abandonoTwelveMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("abandono");
    cd.addSearch(
        "NOTIFICADO",
        EptsReportUtils.map(
            cohortDefinition(
                genericCohortQueries.generalSql(
                    "notificado", TXRetQueries.notificadoTwelveMonths())),
            mappings));
    cd.addSearch(
        "NAONOTIFICADO",
        EptsReportUtils.map(
            cohortDefinition(
                genericCohortQueries.generalSql(
                    "naonotificado", TXRetQueries.naonotificadoTwelveMonths())),
            mappings));
    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");
    addParameters(cd);
    return cd;
  }

  /**
   * Numerator for patients in court for 12 months Patients on INICIOTARV excluding those on (OBITO
   * OR SUSPENSO OR ABANDONO)
   *
   * @return CohortDefinition
   */
  public CohortDefinition inCourtForTwelveMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("inCourt12Months");
    cd.addSearch("OBITO", EptsReportUtils.map(cohortDefinition(obitoTwelveMonths()), mappings));
    cd.addSearch(
        "SUSPENSO", EptsReportUtils.map(cohortDefinition(suspensoTwelveMonths()), mappings));
    cd.addSearch(
        "INICIOTARV", EptsReportUtils.map(cohortDefinition(initiotArvTwelveMonths()), mappings));
    cd.addSearch(
        "ABANDONO", EptsReportUtils.map(cohortDefinition(abandonoTwelveMonths()), mappings));

    cd.setCompositionString("INICIOTARV NOT (OBITO OR SUSPENSO OR ABANDONO)");
    addParameters(cd);
    return cd;
  }

  /**
   * Denominator INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS PARA (SQL)
   *
   * @return CohortDefinition
   */
  public CohortDefinition courtNotTransferredTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "INICIO DE TRATAMENTO ARV - NUM PERIODO: EXCLUI TRANSFERIDOS PARA (SQL)",
            TXRetQueries.courtNotTransferredTwelveMonths()));
  }

  /**
   * Children who are below 1 year and incresed HAART at ART start date endDate and location
   * parameters need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition under1YearIncreasedHARTAtARTStartDate() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "under1YearIncreasedHARTAtARTStartDate",
            TXRetQueries.under1YearIncreasedHARTAtARTStartDate()));
  }

  /**
   * Children who are between 1 to 19 months and started target at ART initiation endDate and
   * location parameters need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition oneTo19WhoStartedTargetAtARTInitiation() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "oneTo19WhoStartedTargetAtARTInitiation",
            TXRetQueries.oneTo19WhoStartedTargetAtARTInitiation()));
  }

  /**
   * LACTANTES OU PUERPUERAS (POS-PARTO) REGISTADAS: PROCESSO CLINICO E FICHA DE SEGUIMENTO
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition possibleRegisteredClinicalProcedureAndFollowupForm() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setDescription(
        "LACTANTES OU PUERPUERAS (POS-PARTO) REGISTADAS: PROCESSO CLINICO E FICHA DE SEGUIMENTO");
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "DATAPARTO",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsWithUpdatedDepartureInART(),
            "value1=${onOrAfter},value2=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "INICIOLACTANTE",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                hivMetadata.getCriteriaForArtStart(),
                BaseObsCohortDefinition.TimeModifier.FIRST,
                SetComparator.IN,
                Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(commonMetadata.getBreastfeeding())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));
    cd.addSearch(
        "GRAVIDAS",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${onOrAfter},endDate=${onOrBefore},location=${location}"));
    cd.addSearch(
        "LACTANTEPROGRAMA",
        EptsReportUtils.map(
            infantsWhoGaveAwardsTwoYearsBehindReferenceDate(),
            "startDate=${onOrAfter},location=${location}"));
    cd.addSearch("FEMININO", EptsReportUtils.map(genderCohorts.femaleCohort(), ""));
    cd.addSearch(
        "LACTANTE",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                commonMetadata.getBreastfeeding(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                SetComparator.IN,
                Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
                Arrays.asList(commonMetadata.getYesConcept())),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},locationList=${location}"));

    String compositionString =
        "((DATAPARTO OR INICIOLACTANTE OR LACTANTEPROGRAMA  OR LACTANTE) NOT GRAVIDAS) AND FEMININO";
    cd.setCompositionString(compositionString);
    return cd;
  }

  /**
   * Male patients on ART treatment aged between 10 t0 14 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt10To14() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt10To14", TXRetQueries.genderOnArtXToY("M", 10, 14)));
  }

  /**
   * Female patients on ART treatment aged between 10 t0 14 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt10To14() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt10To14", TXRetQueries.genderOnArtXToY("F", 10, 14)));
  }

  /**
   * Male patients on ART treatment aged between 15 t0 19 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt15To19() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt15To19", TXRetQueries.genderOnArtXToY("M", 15, 19)));
  }

  /**
   * Female patients on ART treatment aged between 15 t0 19 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt15To19() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt15To19", TXRetQueries.genderOnArtXToY("F", 15, 19)));
  }

  /**
   * Male patients on ART treatment aged between 20 t0 24 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt20To24() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt20To24", TXRetQueries.genderOnArtXToY("M", 20, 24)));
  }

  /**
   * Female patients on ART treatment aged between 20 t0 24 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt20To24() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt20To24", TXRetQueries.genderOnArtXToY("F", 20, 24)));
  }

  /**
   * Male patients on ART treatment aged between 25 t0 29 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt25To29() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt25To29", TXRetQueries.genderOnArtXToY("M", 25, 29)));
  }

  /**
   * Female patients on ART treatment aged between 25 t0 29 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt25To29() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt25To29", TXRetQueries.genderOnArtXToY("F", 25, 29)));
  }

  /**
   * Male patients on ART treatment aged between 30 t0 34 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt30To34() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt30To34", TXRetQueries.genderOnArtXToY("M", 30, 34)));
  }

  /**
   * Female patients on ART treatment aged between 30 t0 34 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt30To34() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt30To34", TXRetQueries.genderOnArtXToY("F", 30, 34)));
  }

  /**
   * Male patients on ART treatment aged between 35 t0 39 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt35To39() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt35To39", TXRetQueries.genderOnArtXToY("M", 35, 39)));
  }

  /**
   * Female patients on ART treatment aged between 35 t0 39 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt35To39() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt35To39", TXRetQueries.genderOnArtXToY("F", 35, 39)));
  }

  /**
   * Male patients on ART treatment aged between 40 t0 49 years endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArt40To49() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "menOnArt40To49", TXRetQueries.genderOnArtXToY("M", 40, 49)));
  }

  /**
   * Female patients on ART treatment aged between 40 t0 49 years endDate and location parameters
   * need to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArt40To49() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "womenOnArt40To49", TXRetQueries.genderOnArtXToY("F", 40, 49)));
  }

  /**
   * Male patients on ART treatment aged 50 years and above endDate and location parameters need to
   * be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition menOnArtAbove50() {
    return cohortDefinition(
        genericCohortQueries.generalSql("menOnArtAbove50", TXRetQueries.genderOnArtAbove50("M")));
  }

  /**
   * Female patients on ART treatment aged 50 years and above endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition womenOnArtAbove50() {
    return cohortDefinition(
        genericCohortQueries.generalSql("womenOnArtAbove50", TXRetQueries.genderOnArtAbove50("F")));
  }

  /**
   * Infants who gave awards 2 years behind the reference date endDate and location parameters need
   * to be mapped correctly
   *
   * @return @{@link CohortDefinition}
   */
  public CohortDefinition infantsWhoGaveAwardsTwoYearsBehindReferenceDate() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "infantsWhoGaveAwardsTwoYearsBehindReferenceDate",
            TXRetQueries.infantsWhoGaveAwardsTwoYearsBehindReferenceDate()));
  }
}
