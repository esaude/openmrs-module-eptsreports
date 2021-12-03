package org.openmrs.module.eptsreports.reporting.library.cohorts;

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
public class TxPvlsBySourceLabOrFsrCohortQueries {

  private TxPvlsCohortQueries txPvlsCohortQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public TxPvlsBySourceLabOrFsrCohortQueries(
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
  @DocumentedDefinition(value = "suppressedViralLoadWithin12MonthsForLabAndFsrNumerator")
  public CohortDefinition getPatientsWithSuppressedViralLoadWithin12MonthsForLabAndFsrNumerator() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("suppressedViralLoadWithin12MonthsForLabAndFsrNumerator");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        TxPvlsBySourceQueries.getPatientsWithViralLoadSuppressionForLabAndFsrNumerator(
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getFsrEncounterType().getEncounterTypeId(),
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
  @DocumentedDefinition(value = "viralLoadWithin12MonthsForLabAndFsrDenominator")
  public CohortDefinition getPatientsViralLoadWithin12MonthsForLabAndFsrDenominator() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("viralLoadWithin12MonthsForLabAndFsrDenominator");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        TxPvlsBySourceQueries.getPatientsHavingViralLoadInLast12MonthsForLabAndFsrDenominator(
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getFsrEncounterType().getEncounterTypeId(),
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
      getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithSuppressedViralLoadWithin12MonthsForLabAndFsrNumerator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(),
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
      getPatientsViralLoadWithin12MonthsForLabAndFsrDenominatorAndOnArtForMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(getPatientsViralLoadWithin12MonthsForLabAndFsrDenominator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(),
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
  @DocumentedDefinition(value = "breastfeedingWomenWithViralSuppressionForLabAndFsrNumerator")
  public CohortDefinition getBreastfeedingWomenWithViralSuppressionForLabAndFsrNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral suppression for Lab and FSR Numerator");
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
            getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND suppression");

    return cd;
  }

  /**
   * <b>Description</b> Breast feeding women with viral load suppression
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResultsForLabAndFsrDenominator")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResultsForLabAndFsrDenominator() {
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
            getPatientsViralLoadWithin12MonthsForLabAndFsrDenominatorAndOnArtForMoreThan3Months(),
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
    cd.setName("Get pregnant women with viral load suppression for lab and fsr");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
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
  public CohortDefinition getPregnantWomenWithViralLoadResultsForLabAndFsrDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results denominator for Lab and fsr only");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForLabAndFsrDenominatorAndOnArtForMoreThan3Months(),
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
  public CohortDefinition getPatientsWithViralLoadResultsAndOnRoutineForLabAndFsrDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(getPatientsViralLoadWithin12MonthsForLabAndFsrDenominator(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            txPvlsCohortQueries.getPatientsWhoAreMoreThan3MonthsOnArt(),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "RoutineByLab",
        EptsReportUtils.map(txPvlsCohortQueries.getPatientsWhoAreOnRoutine(), mappings));
    cd.addSearch("RoutineByFsr", EptsReportUtils.map(getRoutineByFsr(), mappings));
    cd.setCompositionString("(results AND onArtLongEnough) AND (RoutineByLab OR RoutineByFsr)");
    return cd;
  }

  private CohortDefinition getRoutineByFsr() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        TxPvlsBySourceQueries.getPatientsHavingRoutineViralLoadTestsUsingFsr(
            hivMetadata.getFsrEncounterType().getEncounterTypeId(),
            hivMetadata.getReasonForRequestingViralLoadConcept().getConceptId(),
            hivMetadata.getRoutineForRequestingViralLoadConcept().getConceptId(),
            hivMetadata.getUnkownConcept().getConceptId()));
    return cd;
  }

  /**
   * <b>Description</b> Get patients who are on target Composition
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreOnTargetForLabAndFsrDenominator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients on Target");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsViralLoadWithin12MonthsForLabAndFsrDenominator(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWithViralLoadResultsAndOnRoutineForLabAndFsrDenominator(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND NOT routine");
    return cd;
  }

  /**
   * <b>Description</b>Get patients having viral load suppression and routine for adults and
   * children - Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnRoutineForLabAndFsrNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on routine adult and children ForLabAndFsrNumerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
            mappings));
    cd.addSearch(
        "RoutineByLab",
        EptsReportUtils.map(txPvlsCohortQueries.getPatientsWhoAreOnRoutine(), mappings));
    cd.addSearch("RoutineByFsr", EptsReportUtils.map(getRoutineByFsr(), mappings));
    cd.setCompositionString("supp AND (RoutineByLab OR RoutineByFsr)");
    return cd;
  }

  /**
   * <b>Description</b>Get patients having viral load suppression and target for adults and children
   * - Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnTargetForLabAndFsrNumerator() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on target adult and children ForLabAndFsrNumerator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionForLabAndFsrNumeratorWhoAreOnArtMoreThan3Months(),
            mappings));
    cd.addSearch(
        "RoutineByLab",
        EptsReportUtils.map(txPvlsCohortQueries.getPatientsWhoAreOnRoutine(), mappings));
    cd.addSearch("RoutineByFsr", EptsReportUtils.map(getRoutineByFsr(), mappings));
    cd.setCompositionString("supp AND NOT (RoutineByLab OR RoutineByFsr)");
    return cd;
  }
}
