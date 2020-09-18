package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.NextAndPrevDatesCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.dsd.OnArtForAtleastXmonthsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.DsdQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.MappedParametersCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EriDSDCohortQueries {
  @Autowired private TxCurrCohortQueries txCurrCohortQueries;
  @Autowired private TxNewCohortQueries txNewCohortQueries;
  @Autowired private GenericCohortQueries genericCohortQueries;
  @Autowired private CommonCohortQueries commonCohortQueries;

  @Autowired private AgeCohortQueries ageCohortQueries;
  @Autowired private HivCohortQueries hivCohortQueries;
  @Autowired private HivMetadata hivMetadata;
  @Autowired private CommonMetadata commonMetadata;

  /**
   * <b>Name: D1</b>
   *
   * <p><b>Description:</b> Number of active patients on ART Eligible for DSD”
   *
   * <p><b>NOTE:</b> Excluding patients registered as pregnant, breastfeeding, in TB Treatment and
   * were ever on Sarcoma Karposi
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("D1 - Number of active, stable, patients on ART. Combination of Criteria 1,2,3,4,5");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "2",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("moreThanOrEqual2Years", 2, 200),
            "effectiveDate=${endDate}"));

    cd.addSearch(
        "pregnantBreastfeedingTB",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "sarcomaKarposi",
        EptsReportUtils.map(
            getAllPatientsOnSarcomaKarposi(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "7",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(2 AND NOT (pregnantBreastfeedingTB OR sarcomaKarposi) AND 7)");

    return cd;
  }

  /**
   * <b>Description:</b> Active and stable Patients who are Non-Pregnant and Non-Breastfeeding for
   * <b>D1</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D1 - Patients who are Non-pregnant and Non-Breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStable",
        EptsReportUtils.map(
            getD1(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * <b>Name: D2</b>
   *
   * <p><b>Description:</b> Number of active patients on ART Not Eligible for <b>D1</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getD2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D2 - Number of active patients on ART Not Eligible for DSD D1");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "activeAndStablePatients",
        EptsReportUtils.map(
            getD1(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "pregnantOrBreastfeedingOrTBTreatment",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "NOT (activeAndStablePatients OR pregnantOrBreastfeedingOrTBTreatment)");

    return cd;
  }

  /**
   * <b>Description:</b> Patients who are Non-Pregnant and Non-Breastfeeding for <b>D2</b>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D2 - who are Non-pregnant and Non-Breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(true),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(true),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstable",
        EptsReportUtils.map(
            getD2(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * <b>Name: N1</b>
   *
   * <p><b>Description:</b> Number of active on ART whose next ART pick-up is schedule for 83-97
   * days after the date of their last ART drug pick-up (Fluxo Rápido)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "N1 - Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date of their last ART drug pick-up (Fluxo Rápido)");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsScheduled =
        getPatientsScheduled(
            hivMetadata.getReturnVisitDateForArvDrugConcept(),
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()),
            97,
            83);
    CohortDefinition quarterly = getPatientsWithQuarterlyTypeOfDispensation();
    CohortDefinition startOrContinue = getPatientsWithStartOrContinueOnQuarterlyDispensation();
    CohortDefinition completed = getPatientsWithCompletedOnQuarterlyDispensation();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("scheduled", EptsReportUtils.map(patientsScheduled, mappings));
    cd.addSearch("quarterly", EptsReportUtils.map(quarterly, mappings));
    cd.addSearch("startOrContinue", EptsReportUtils.map(startOrContinue, mappings));
    cd.addSearch("completed", EptsReportUtils.map(completed, mappings));

    cd.setCompositionString("(scheduled OR quarterly OR startOrContinue) NOT completed");

    return cd;
  }

  /**
   * <b>Name: N2</b>
   *
   * <p><b>Description:</b> Number of active patients on ART whose next clinical consultation is
   * scheduled 175-190 days after the date of the last clinical consultation
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "N2 - Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsScheduled =
        getPatientsScheduled175to190days(
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getPediatriaSeguimentoEncounterType()),
            hivMetadata.getReturnVisitDateConcept());
    CohortDefinition rapidFlow = getPatientsWithStartOrContinueOnRapidFlow();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("scheduledN2", EptsReportUtils.map(patientsScheduled, mappings));

    cd.addSearch("rapidFlow", EptsReportUtils.map(rapidFlow, mappings));
    cd.addSearch("completed", EptsReportUtils.map(getPatientsWhoCompletedRapidFlow(), mappings));

    cd.setCompositionString("(scheduledN2 OR rapidFlow) NOT completed");

    return cd;
  }

  /**
   * <b>Name: N3</b>
   *
   * <p><b>Description:</b> Number of active patients on ART that are participating in <b>GAAC</b>
   * at the end of the month prior to month of results submission deadline.
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "N3 - Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsEnrolledOnGaac = getAllPatientsEnrolledOnGaac();
    CohortDefinition startOrContinueGAAC = getPatientsWithStartOrContinueGAAC();
    CohortDefinition completedGAAC = getPatientsWhoCompletedGAAC();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("patientsEnrolledOnGaac", mapStraightThrough(patientsEnrolledOnGaac));
    cd.addSearch("startOrContinueGAAC", EptsReportUtils.map(startOrContinueGAAC, mappings));
    cd.addSearch("completedGAAC", EptsReportUtils.map(completedGAAC, mappings));

    cd.setCompositionString("(patientsEnrolledOnGaac OR startOrContinueGAAC) NOT completedGAAC");

    return cd;
  }

  /**
   * <b>Name: N4</b>
   *
   * <p><b>Description:</b> Number of active patients on ART who are in <b>AF</b> (Abordagem
   * Familiar)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN4() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("N4 - Active patients on ART  who are in AF");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("masterCardPatients", mapStraightThrough(getPatientsOnMasterCardAF()));
    cd.setCompositionString("masterCardPatients");
    return cd;
  }

  /**
   * <b>Name: N5</b>
   *
   * <p><b>Description:</b> Number of active patients on ART who are in <b>CA</b> (Clubes de Adesao)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN5() {
    CodedObsCohortDefinition cd1 = new CodedObsCohortDefinition();
    cd1.setName("N5 - Number of active patients on ART who are in CA");
    cd1.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd1.addParameter(new Parameter("locationList", "Location", Location.class));
    cd1.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd1.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd1.setQuestion(hivMetadata.getAccessionClubs());
    cd1.setOperator(SetComparator.IN);
    cd1.addValue(hivMetadata.getStartDrugsConcept());
    cd1.addValue(hivMetadata.getContinueRegimenConcept());
    return new MappedParametersCohortDefinition(
        cd1, "onOrBefore", "endDate", "locationList", "location");
  }

  /**
   * <b>Name: N7</b>
   *
   * <p><b>Description:</b> Number of active patients on ART who are in <b>DC</b> (Dispensa
   * Comunitaria)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN7() {
    CodedObsCohortDefinition cd1 = new CodedObsCohortDefinition();
    cd1.setName("N7 - Active patients in ART marked in last DC as start or continue regimen");
    cd1.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd1.addParameter(new Parameter("locationList", "Location", Location.class));
    cd1.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd1.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd1.setQuestion(hivMetadata.getCommunityDispensation());
    cd1.setOperator(SetComparator.IN);
    cd1.addValue(hivMetadata.getStartDrugs());
    cd1.addValue(hivMetadata.getContinueRegimenConcept());
    return new MappedParametersCohortDefinition(
        cd1, "endDate", "onOrBefore", "location", "locationList");
  }

  /**
   * <b>Name: N8</b>
   *
   * <p><b>Description:</b> Number of active patients on ART who participate in at least one DSD
   * model
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN8() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("participatingInDsdModel");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("N1", mapStraightThrough(getN1()));
    cd.addSearch("N2", mapStraightThrough(getN2()));
    cd.addSearch("N3", mapStraightThrough(getN3()));
    cd.addSearch("N4", mapStraightThrough(getN4()));
    cd.addSearch("N5", mapStraightThrough(getN5()));
    cd.addSearch("N7", mapStraightThrough(getN7()));
    cd.addSearch("N9", mapStraightThrough(getN9()));
    cd.setCompositionString("(N1 OR N2 OR N3 OR N4 OR N5 OR N7 OR N9)");
    return cd;
  }

  /**
   * <b>Name: N9</b>
   *
   * <p><b>Description:</b> Number of active patients on ART who are on <b>DS</b> (Dispensa
   * semestral)
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getN9() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("N9: Number of active patients on ART who are on DS");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition patientsScheduled =
        getPatientsScheduled175to190days(
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()),
            hivMetadata.getReturnVisitDateForArvDrugConcept());
    CohortDefinition semestralDispensation =
        getPatientsWithStartOrContinueOnSemestralDispensation();
    CohortDefinition semestral = getPatientsWithSemestralTypeOfDispensation();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("scheduledN9", EptsReportUtils.map(patientsScheduled, mappings));
    cd.addSearch("semestral", EptsReportUtils.map(semestral, mappings));

    cd.addSearch("semestralDispensation", EptsReportUtils.map(semestralDispensation, mappings));
    cd.addSearch("completed", EptsReportUtils.map(getPatientsWhoCompletedRapidFlow(), mappings));

    cd.setCompositionString("(scheduledN9 OR semestral OR semestralDispensation) NOT completed");

    return cd;
  }

  /**
   * <b>Description:</b> Patients who are registered as pregnant, as breastfeeding or who are on TB
   * treatment
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPregnantAndBreastfeedingAndOnTBTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("Pregnant, Breastfeeding or on TB Treatment");
    CohortDefinition breastfeeding = txNewCohortQueries.getTxNewBreastfeedingComposition(true);
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART(true);
    CohortDefinition tb = commonCohortQueries.getPatientsOnTbTreatment();

    String pregnantMappings = "startDate=${endDate-9m},endDate=${endDate},location=${location}";
    cd.addSearch("pregnant", EptsReportUtils.map(pregnant, pregnantMappings));

    String breastfeedingMappings =
        "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}";
    cd.addSearch("breastfeeding", EptsReportUtils.map(breastfeeding, breastfeedingMappings));

    String tbMappings = "endDate=${endDate},location=${location}";
    cd.addSearch("tb", EptsReportUtils.map(tb, tbMappings));

    cd.setCompositionString("pregnant OR breastfeeding OR tb");

    return cd;
  }

  /**
   * <b>Description:</b> Number of patients who are on Sarcoma Karposi
   *
   * <p><b>Techinal Specs</b>
   *
   * <blockquote>
   *
   * <pre>
   * <p>Include patients who have Sarcoma Kaposi <b>(concept_id = 507)</b> registered
   * in the follow-up (Adults <b>(encounterType = 6)</b> and Children <b>(encounterType = 9)</b>))
   * consultation and <b>encounter_datetime <= reporting_end_date</b>
   * </pre>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getAllPatientsOnSarcomaKarposi() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("sarcomaKarposiPatients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        DsdQueries.getPatientsOnSarcomaKarposi(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getOtherDiagnosis().getConceptId(),
            hivMetadata.getKaposiSarcomaConcept().getConceptId()));

    return cd;
  }

  /**
   * <b>Description:</b> Filter patients (from 4) who are considered stable according to criteria 5:
   * <b>a, b, c, d, e, f</b>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWhoAreStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Patients who are stable");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            getPatientsWhoAreStableA(), "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            getPatientsWithViralLoadLessThan1000Within12Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            getCD4CountAndCD4PercentCombined(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            genericCohortQueries.hasCodedObs(
                hivMetadata.getAdverseReaction(),
                BaseObsCohortDefinition.TimeModifier.ANY,
                SetComparator.IN,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType()),
                Arrays.asList(
                    hivMetadata.getPancreatitis(),
                    hivMetadata.getLacticAcidosis(),
                    hivMetadata.getCytopeniaConcept(),
                    hivMetadata.getNephrotoxicityConcept(),
                    hivMetadata.getHepatitisConcept(),
                    hivMetadata.getStevensJonhsonSyndromeConcept(),
                    hivMetadata.getHypersensitivityToAbcOrRailConcept(),
                    hivMetadata.getHepaticSteatosisWithHyperlactataemiaConcept())),
            "onOrAfter=${endDate-6m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "patientsWithViralLoad",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsViralLoadWithin12Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("A AND (B OR (C AND NOT patientsWithViralLoad)) AND NOT F");

    return cd;
  }

  /**
   * <b>Name: 5A</b>
   *
   * <p><b>Description:</b> Patients on ART for at least 12 months <b>(if patients age >=2 and
   * <=9)</b> or On ART for at least 6 months <b>(if patients age >=10)</b>
   *
   * <p><b>Technical Specs</b>
   * <blockquote>
   * <pre>
   * <p>On ART for at least x months means: <b>(patient_art_initiation date–
   * reporting end date) >= x months</b>
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWhoAreStableA() {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "onArtAtleastXmonths",
            Context.getRegisteredComponents(OnArtForAtleastXmonthsCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  /**
   * <b>Name: 5C</b>
   *
   * <p><b>Description:</b> One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year <b>(if
   * patients age >=2 and <=4)</b> or One CD4 result > 200 cels/mm3 in last ART year <b>(if patients
   * age >=5 and <=9)</b> 5C (i) 5C (ii)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getCD4CountAndCD4PercentCombined() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Only if VL does not exist");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "CI",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent1(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "CII",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent2(),
            "startDate=${endDate-12m},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(CI OR CII)");

    return cd;
  }

  /**
   * <b>Name: 5C (i)</b>
   *
   * <p><b>Description:</b> One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year (if patients
   * age >=2 and <=4)
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <ol>
   *   <li>Get the most recent encounter between A and B: <div>A. The last Encounter of type 6,9,13
   *       or 51 occurred (Encounter_date) between reporting end date and (reporting end date – 12
   *       months) which contains one of the following concept:
   *       <p>
   *       <ul>
   *         <li>CD4 Abs Result <b>(Concept id 1695 or Concept id 5497)</b> or
   *         <li>CD4 % Result <b>(Concept id 730)</b>
   *       </ul>
   *       <div>B. The last obs.datetime of obs concept id 1695 occurred between reporting end date
   *       and (reporting end date – 12 months) recorded in Encounter of type 53.
   *   <li>Check If the most recent encounter between A and B contains:
   *       <ul>
   *         <li>CD4 Abs Result <b>(Concept id 1695 or Concept id 5497) >750</b> or
   *         <li>CD4 % Result <b>(Concept id 730) >15%</b>
   *       </ul>
   * </ol>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getCD4CountAndCD4Percent1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("CD4CountAndCD4percent");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "Cd4Abs",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteOBSConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                750.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getFsrEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Lab",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                750.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getFsrEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Percent",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4PercentConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                15.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getFsrEncounterType(),
                    hivMetadata.getMasterCardEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4), "effectiveDate=${endDate}"));

    cd.setCompositionString("(Cd4Abs OR Cd4Lab OR Cd4Percent) AND Age");

    return cd;
  }

  /**
   * <b>Name: 5C (ii)</b>
   *
   * <p><b>Description:</b> One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5
   * years)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getCD4CountAndCD4Percent2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "5C (ii) One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 years)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("greaterThan5", 5, 900),
            "effectiveDate=${endDate}"));

    cd.addSearch(
        "CD4CountAndCD4Percent2Part1",
        EptsReportUtils.map(
            getCD4CountAndCD4Percent2Part1(), "endDate=${endDate},location=${location}"));

    cd.setCompositionString("CD4CountAndCD4Percent2Part1 AND Age");

    return cd;
  }
  /**
   * <b>Description:</b> LAST CD4 result > 200 cels/mm3 in last ART year (if patients age >=5)
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <ol>
   *   <li>Get the most recent encounter between A and B: <div>A. The last Encounter of type 6,9,13
   *       or 51 occurred (Encounter_date) between reporting end date and (reporting end date–12
   *       months) which contains one of the following concept:
   *       <p>
   *       <ul>
   *         <li>CD4 Abs Result <b>(Concept id 1695 or Concept id 5497)</b>
   *       </ul>
   *       <div>B. The last obs.datetime of obs concept id 1695 occurred between reporting end date
   *       and (reporting end date – 12 months) recorded in Encounter of type 53.
   *   <li>Check If the most recent encounter between A and B contains:
   *       <ul>
   *         <li>CD4 Abs Result <b>(Concept id 1695 or Concept id 5497) >200</b> or
   *             <ul>
   *               <li>No active clinical condition of WHO stage III or IV in last clinical
   *                   appointment and
   *               <li>No adverse reactions to medications in last six months that require regular
   *                   monitoring
   *             </ol>
   *             </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getCD4CountAndCD4Percent2Part1() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("LAST CD4 result > 200 cels/mm3 in last ART year (if patients age >=5");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1695", hivMetadata.getCD4AbsoluteOBSConcept().getConceptId());
    map.put("5497", hivMetadata.getCD4AbsoluteConcept().getConceptId());

    String query =
        "SELECT  cd4_max.patient_id "
            + "FROM "
            + "( "
            + "    SELECT  most_recent.patient_id, MAX(most_recent.last_date) cd4_max_date "
            + "    FROM "
            + "    ( "
            + "        SELECT p.patient_id, MAX(e.encounter_datetime) last_date "
            + "        FROM  patient p "
            + "            INNER JOIN encounter e  "
            + "                ON e.patient_id=p.patient_id "
            + "            INNER JOIN obs o  "
            + "                ON o.encounter_id=e.encounter_id "
            + "        WHERE  "
            + "            e.encounter_type IN (${6},${9},${13},${51})   "
            + "            AND  o.concept_id IN (${1695},${5497})   "
            + "            AND e.encounter_datetime   "
            + "                    BETWEEN date_add(date_add( :endDate, interval -12 MONTH), interval 1 day)  "
            + "                        AND  :endDate  "
            + "            AND e.location_id=   :location   "
            + "            AND o.voided = 0  "
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "        GROUP BY p.patient_id "
            + "        UNION "
            + "        SELECT p.patient_id, MAX(o.obs_datetime) latest_date    "
            + "        FROM patient p   "
            + "            INNER JOIN encounter e   "
            + "                    ON p.patient_id=e.patient_id   "
            + "            INNER JOIN obs o   "
            + "                    ON o.encounter_id=e.encounter_id   "
            + "        WHERE e.encounter_type=${53}  "
            + "            AND o.concept_id = ${1695}   "
            + "            AND  o.obs_datetime   "
            + "                        BETWEEN date_add(date_add( :endDate, interval -12 MONTH), interval 1 day)  "
            + "                            AND  :endDate   "
            + "            AND e.location_id=   :location   "
            + "            AND p.voided=0   "
            + "            AND e.voided=0   "
            + "            AND o.voided=0   "
            + "        GROUP BY p.patient_id "
            + "    )most_recent "
            + "    GROUP BY most_recent.patient_id "
            + ")cd4_max "
            + "    INNER JOIN encounter e  "
            + "        ON e.patient_id = cd4_max.patient_id  "
            + "    INNER JOIN obs o  "
            + "        ON o.encounter_id = e.encounter_id  "
            + "WHERE  e.voided=0   "
            + "    AND o.voided=0   "
            + "    AND "
            + "    (  "
            + "        (o.concept_id IN (${1695} ,${5497}) AND o.value_numeric > 200)  "
            + "          "
            + "    )   "
            + "    AND e.location_id=  :location   "
            + "    AND  "
            + "    ( "
            + "        (e.encounter_type IN (${6},${9},${13},${51})  "
            + "            AND e.encounter_datetime   "
            + "                BETWEEN date_add(date_add( :endDate, interval -12 MONTH), interval 1 day) AND  :endDate "
            + "            AND e.encounter_datetime = cd4_max.cd4_max_date  "
            + "        ) "
            + "        OR  "
            + "        (e.encounter_type = ${53} "
            + "            AND o.obs_datetime     "
            + "                BETWEEN date_add(date_add( :endDate, interval -12 MONTH), interval 1 day) AND  :endDate "
            + "            AND o.obs_datetime = cd4_max.cd4_max_date  "
            + "        ) "
            + "    )  "
            + " "
            + "ORDER BY cd4_max.patient_id";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /**
   * <b>Name: 5B </b>
   *
   * <p><b>Description:</b> Patients with LAST Viral Load Result < 1000 copies/ml in last ART year
   * (only if VL exists)
   *
   * <p><b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <ol>
   *   <li>Get the most recent encounter between A and B: <div>A. The last Encounter of type 6,9,13
   *       or 51 occurred (Encounter_date) between reporting end date and (reporting end date – 12
   *       months) which contains one of the following concept:
   *       <p>
   *       <ul>
   *         <li>HIV VIRAL LOAD OBS Concept id = 856</b>or
   *         <li>HIV VIRAL LOAD OBS Concept id = 1305</b>
   *       </ul>
   *       <div>B. The last obs.datetime of obs concept id 1695 occurred between reporting end date
   *       and (reporting end date – 12 months) recorded in Encounter of type 53.
   *   <li>Check If the most recent encounter between A and B contains:
   *       <ul>
   *         <li><b>Concept id 856 and OBS VALUE_NUMERIC is > 1000</b> or
   *         <li><b>Concept id 1305 and value_coded in</b> { See in Specs DSD D1 Indicator}
   *       </ul>
   * </ol>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithViralLoadLessThan1000Within12Months() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName(
        "Patients with LAST Viral Load Result < 1000 copies/ml in last ART year (only if VL exists)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put(
        "adultoSeguimento", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "pediatriaSeguimento",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put(
        "misauLaboratorio", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("fsr", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("masterCard", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("hivViralLoad", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("hivViralLoadQualitative", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("beyondDetectableLimit", hivMetadata.getBeyondDetectableLimitConcept().getConceptId());
    map.put("undetectableViralLoad", hivMetadata.getUndetectableViralLoadConcept().getConceptId());
    map.put("lessThan10Copies", hivMetadata.getLessThan10CopiesConcept().getConceptId());
    map.put("lessThan20Copies", hivMetadata.getLessThan20CopiesConcept().getConceptId());
    map.put("lessThan40Copies", hivMetadata.getLessThan40CopiesConcept().getConceptId());
    map.put("lessThan400Copies", hivMetadata.getLessThan400CopiesConcept().getConceptId());
    map.put("lessThan839Copies", hivMetadata.getLessThan839CopiesConcept().getConceptId());

    String query =
        " SELECT vl_max.patient_id "
            + "FROM "
            + "( "
            + "SELECT vl.patient_id, MAX(vl.latest_date) max_date "
            + "FROM "
            + "(  "
            + "        SELECT p.patient_id, MAX(e.encounter_datetime) latest_date  "
            + "        FROM patient p  "
            + "            INNER JOIN encounter e  "
            + "                    ON p.patient_id=e.patient_id  "
            + "            INNER JOIN obs o  "
            + "                    ON e.encounter_id=o.encounter_id  "
            + "        WHERE e.encounter_type IN (${adultoSeguimento},${pediatriaSeguimento},${misauLaboratorio},${fsr} )  "
            + "            AND  o.concept_id IN (${hivViralLoad},${hivViralLoadQualitative} )  "
            + "            AND e.encounter_datetime  "
            + "                        BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate "
            + "            AND e.location_id=  :location "
            + "            AND p.voided=0  "
            + "            AND e.voided=0  "
            + "            AND o.voided=0  "
            + "        GROUP BY p.patient_id  "
            + "        UNION  "
            + "        SELECT p.patient_id, MAX(o.obs_datetime) latest_date   "
            + "        FROM patient p  "
            + "            INNER JOIN encounter e  "
            + "                    ON p.patient_id=e.patient_id  "
            + "            INNER JOIN obs o  "
            + "                    ON o.encounter_id=e.encounter_id  "
            + "        WHERE e.encounter_type=${masterCard} "
            + "            AND o.concept_id = ${hivViralLoad} "
            + "            AND  o.obs_datetime  "
            + "                        BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate  "
            + "            AND e.location_id=  :location  "
            + "            AND p.voided=0  "
            + "            AND e.voided=0  "
            + "            AND o.voided=0  "
            + "        GROUP BY p.patient_id"
            + "		) vl "
            + " GROUP BY   vl.patient_id "
            + ") vl_max"
            + "        INNER JOIN encounter e "
            + "            ON e.patient_id = vl_max.patient_id "
            + "        INNER JOIN obs o "
            + "            ON o.encounter_id = e.encounter_id "
            + "    WHERE  e.voided=0  "
            + "        AND o.voided=0  "
            + "        AND( "
            + "                (o.concept_id=${hivViralLoad} AND o.value_numeric < 1000) "
            + "                OR "
            + "                (o.concept_id=${hivViralLoadQualitative} AND o.value_coded IN (${beyondDetectableLimit},${undetectableViralLoad},${lessThan10Copies},${lessThan20Copies},${lessThan40Copies},${lessThan400Copies},${lessThan839Copies})) "
            + "            )  "
            + "        AND e.location_id= :location "
            + "AND (  "
            + "                                       (e.encounter_type IN (${adultoSeguimento},${pediatriaSeguimento},${misauLaboratorio},${fsr})   "
            + "                                             AND e.encounter_datetime    "
            + "                        BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate  "
            + "                                           AND e.encounter_datetime = vl_max.max_date )  "
            + "                           OR   "
            + "                                       (e.encounter_type =${masterCard}  "
            + "                                             AND o.obs_datetime      "
            + "                        BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate  "
            + "                                             AND o.obs_datetime = vl_max.max_date       )  "
            + "                                 )  "
            + "        "
            + "   ORDER BY vl_max.patient_id ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /**
   * <b>Description:</b> Patients whose next drugs pickup appointment is scheduled for 83-97 after
   * the date of their last drugs pickup
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsScheduled(
      Concept conceptId,
      List<EncounterType> encounterTypes,
      Integer upperBound,
      Integer lowerBound) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "scheduledPatients",
            Context.getRegisteredComponents(NextAndPrevDatesCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addCalculationParameter("conceptId", conceptId);
    cd.addCalculationParameter("encounterTypes", encounterTypes);
    cd.addCalculationParameter("upperBound", upperBound);
    cd.addCalculationParameter("lowerBound", lowerBound);

    return cd;
  }

  /**
   * <b>Description:</b> Patients who are marked <b>Completed</b> for their last <b>Rapid Flow</b>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWhoCompletedRapidFlow() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("Patients who are completed for their rapid flow");
    cd.setQuery(
        GenericCohortQueries.getLastCodedObsBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getRapidFlow().getConceptId(),
            Arrays.asList(hivMetadata.getCompletedConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients marked in last <b>Rapid Flow</b> as <b>Start Drugs or Continue
   * Regimen</b> on (ficha clinica - Master Card)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithStartOrContinueOnRapidFlow() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("Patients who marked start or continue rapid flow");
    cd.setQuery(
        GenericCohortQueries.getLastCodedObsBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getRapidFlow().getConceptId(),
            Arrays.asList(
                hivMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients whose next clinical appointment is scheduled for 175-190 days
   * after the date of their last clinical consultant
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsScheduled175to190days(
      List<EncounterType> encounterTypes, Concept concept) {
    int lowerBound = 175;
    int upperBound = 190;
    return getPatientsScheduled(concept, encounterTypes, upperBound, lowerBound);
  }

  /**
   * <b>Description:</b> Active patients on ART MasterCard who are in AF (Abordagem Familiar)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsOnMasterCardAF() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("Active patients on ART MasterCard who are in AF Query");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        DsdQueries.getPatientsOnMasterCardAF(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getFamilyApproach().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimenConcept().getConceptId()));

    return cd;
  }

  /**
   * <b>Description:</b> Patients who have been enrolled in the <b>GAAC</b> program
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getAllPatientsEnrolledOnGaac() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Enrolled On GAAC");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.getPatientsEnrolledOnGAAC());
    return cd;
  }

  /**
   * <b>Description:</b> Patients marked in last <b>GAAC</b> as <b>Start Drugs or Continue
   * Regimen</b> on (ficha clinica - Master Card)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithStartOrContinueGAAC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        GenericCohortQueries.getLastCodedObsBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getGaac().getConceptId(),
            Arrays.asList(
                hivMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients who are marked <b>Completed</b> for their last <b>GAAC</b>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWhoCompletedGAAC() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        GenericCohortQueries.getLastCodedObsBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getGaac().getConceptId(),
            Arrays.asList(hivMetadata.getCompletedConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients who are marked <b>Completed</b> for their last <b>Quartely
   * Dispensation</b>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithCompletedOnQuarterlyDispensation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsWithCompletedOnQuarterlyDispensation");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        GenericCohortQueries.getLastCodedObservationBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            Arrays.asList(hivMetadata.getCompletedConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients marked in last <b>Quartely Dispensation</b> as <b>Start Drugs or
   * Continue Regimen</b> on (ficha clinica - Master Card)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithStartOrContinueOnQuarterlyDispensation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsWithStartOrContinueOnQuarterlyDispensation");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        GenericCohortQueries.getLastCodedObservationBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            Arrays.asList(
                hivMetadata.getStartDrugsConcept().getConceptId(),
                hivMetadata.getContinueRegimenConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients marked as <b>Quartely Dispensation</b> on Tipo de Levantamento
   * (ficha clinica - Master Card)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithQuarterlyTypeOfDispensation() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("patientsWithQuarterlyTypeOfDispensation");
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        GenericCohortQueries.getLastCodedObservationBeforeDate(
            Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()),
            hivMetadata.getTypeOfDispensationConcept().getConceptId(),
            Arrays.asList(hivMetadata.getQuarterlyConcept().getConceptId())));
    return cd;
  }

  /**
   * <b>Description:</b> Patients marked in last <b>Semestral Dispensation</b> as <b>Start Drugs</b>
   * or <b>Continue Regimen</b> on (ficha clinica - Master Card)
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithStartOrContinueOnSemestralDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getSemiannualDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  /**
   * <b>Description:</b> Patients who are marked <b>Completed</b> for their last <b>Semestral
   * Dispensation</b>
   *
   * @return {@link CohortDefinition}
   */
  private CohortDefinition getPatientsWithSemestralTypeOfDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getTypeOfDispensationConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getSemiannualDispensation());
    return cd;
  }
}
