package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxPvlsBySourceClinicalOrFichaResumoCohortQueries {

  private TxPvlsCohortQueries txPvlsCohortQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public TxPvlsBySourceClinicalOrFichaResumoCohortQueries(
      TxPvlsCohortQueries txPvlsCohortQueries, HivMetadata hivMetadata) {
    this.txPvlsCohortQueries = txPvlsCohortQueries;
    this.hivMetadata = hivMetadata;
  }
  /**
   * Adult and pediatric patients on ART with suppressed viral load results (<1,000 copies/ml)
   * documented in the medical records and /or supporting laboratory results within the past 12
   * months based on lab and fsr
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "suppressedViralLoadWithin12MonthsForFichaMestreNumerator")
  public CohortDefinition
      getPatientsWithSuppressedViralLoadWithin12MonthsForFichaMestreNumerator() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("suppressedViralLoadWithin12MonthsForFichaMestreNumerator");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        TxPvlsBySourceQueries.getPatientsWithViralLoadSuppressionPvlsFichaMastreNumerator(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));
    return sql;
  }

  /**
   * <b>Description:</b> Number of adult and pediatric ART patients with a viral load result
   * documented in the patient medical record and/ or laboratory records in the past 12 months.
   * Based on Lab and fsr
   *
   * @return {@link CohortDefinition}
   */
  @DocumentedDefinition(value = "viralLoadWithin12MonthsForFichaMestreDenominator")
  public CohortDefinition getPatientsViralLoadWithin12MonthsForFichaMestreDenominator() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("viralLoadWithin12MonthsForFichaMestreDenominator");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        TxPvlsBySourceQueries.getPatientsHavingViralLoadInLast12MonthsPvlsFichaMastreDenominator(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));
    return sql;
  }

  /**
   * <b>Description</b> Viral load suppression
   *
   * <blockquote>
   *
   * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU,
   * transferred out, stopped ART
   *
   * </blockquote>
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithSuppressedViralLoadWithin12MonthsForFichaMestreNumerator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("supp AND onArtLongEnough");
    return cd;
  }

  /**
   * <b>Description</b> Viral load results composition
   *
   * <blockquote>
   *
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries Only filter out patients who are on routine
   *
   * </blockquote>
   *
   * @return CohortDefinition
   */
  public CohortDefinition
      getPatientsViralLoadWithin12MonthsForFichaMestreDenominatorAndOnArtForMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForFichaMestreDenominator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("results AND onArtLongEnough");
    return cd;
  }

  /**
   * <b>Description</b>Breast feeding women with viral load suppression and on ART for more than 3
   * months
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralSuppressionForFichaMestreNumerator")
  public CohortDefinition getBreastfeedingWomenWithViralSuppressionForFichaMestreNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral suppression ForFichaMestre Numerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                EptsReportConstants.PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND suppression");

    return cd;
  }

  /**
   * <b>Description</b> Breast feeding women with viral load suppression
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResultsForFichaMestreDenominator")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResultsForFichaMestreDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral results");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                EptsReportConstants.PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForFichaMestreDenominatorAndOnArtForMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND results");

    return cd;
  }

  /**
   * <b>Description</b> Get pregnant women Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadSuppressionForLabAndFsrNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load suppression ForFichaMestre");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months(),
            mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                EptsReportConstants.PregnantOrBreastfeedingWomen.PREGNANTWOMEN),
            "onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("suppression AND pregnant");
    return cd;
  }
  /**
   * <b>Description</b>Get pregnant women Denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadResultsForFichaMestreDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results denominator ForFichaMestre only");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForFichaMestreDenominatorAndOnArtForMoreThan3Months(),
            mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                EptsReportConstants.PregnantOrBreastfeedingWomen.PREGNANTWOMEN),
            "onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("results AND pregnant");
    return cd;
  }

  /**
   * <b>Description</b> Viral load results and on routine composition
   *
   * <blockquote>
   *
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries Only filter out patients who are on routine
   *
   * </blockquote>
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadResultsAndOnRoutineForFichaMestreDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForFichaMestreDenominator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch("RoutineByClinical", EptsReportUtils.map(getRoutineByClinicalForms(), mappings));
    cd.setCompositionString("(results AND onArtLongEnough) AND RoutineByClinical");
    return cd;
  }

  private CohortDefinition getRoutineByClinicalForms() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        TxPvlsBySourceQueries.getPatientsHavingRoutineViralLoadTestsUsingClinicalForms(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));
    return cd;
  }

  /**
   * <b>Description</b>Get patients having viral load suppression and routine for adults and
   * children - Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnRoutineForFichaMestreNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on routine adult and children ForLabAndFsrNumerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForFichaMestreNumeratorWhoAreOnArtMoreThan3Months(),
            mappings));

    cd.addSearch("RoutineByClinical", EptsReportUtils.map(getRoutineByClinicalForms(), mappings));
    cd.setCompositionString("supp AND RoutineByClinical");
    return cd;
  }
}
