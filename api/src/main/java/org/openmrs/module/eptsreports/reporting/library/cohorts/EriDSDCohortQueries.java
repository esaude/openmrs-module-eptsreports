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

  /** D1 - Number of active, stable, patients on ART. Combinantion of Criteria 1,2,3,4,5 */
  public CohortDefinition getD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "D1 - Number of active, stable, patients on ART. Combinantion of Criteria 1,2,3,4,5");
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

  /** D1 - Patients who are Non-pregnant and Non-Breastfeeding */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D1 - Patients who are Non-pregnant and Non-Breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStable",
        EptsReportUtils.map(
            getD1(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /** D2 - Number of active patients on ART Not Eligible for DSD D1 */
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

  /** D2 - Patients who are Non-pregnant and Non-Breastfeeding */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D2 - who are Non-pregnant and Non-Breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstable",
        EptsReportUtils.map(
            getD2(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N1 - Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date
   * of their last ART drug pick-up (Fluxo Rápido)
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
   * N2 - Number of active patients on ART whose next clinical consultation is scheduled 175-190
   * days after the date of the last clinical consultation
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
   * N3 - Number of active patients on ART that are participating in GAAC at the end of the month
   * prior to month of results submission deadline.
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

  /** N4 - Active patients on ART who are in AF */
  public CohortDefinition getN4() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("n4 - Active patients on ART  who are in AF");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch("masterCardPatients", mapStraightThrough(getPatientsOnMasterCardAF()));
    cd.setCompositionString("masterCardPatients");
    return cd;
  }

  /** N5 - Number of active patients on ART who are in CA */
  public CohortDefinition getN5() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("N5 - Number of active patients on ART who are in CA");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition startOrContinueCA = getPatientsWithStartOrContinueCA();
    String caMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("startOrContinueCA", EptsReportUtils.map(startOrContinueCA, caMappings));
    cd.setCompositionString("startOrContinueCA");
    return cd;
  }

  /** N7 - Number of active patients on ART who are in DC */
  public CohortDefinition getN7() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("N7 - Active patients in ART marked in last DC as start or continue regimen");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition communityDispense = getPatientsMarkedInLastCommunityDispense();

    String dspMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("communityDispense", EptsReportUtils.map(communityDispense, dspMappings));

    cd.setCompositionString("communityDispense");

    return cd;
  }

  /** N8 - Number of active patients on ART who participate in at least one DSD model */
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

  /** N9 - Number of active patients on ART who are on DS */
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

  public CohortDefinition getPregnantAndBreastfeedingAndOnTBTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("Pregnant, Breastfeeding or on TB Treatment");
    CohortDefinition breastfeeding = txNewCohortQueries.getTxNewBreastfeedingComposition();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();
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

  /** Patients who are on Sarcoma Karposi */
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

  /** Filter patients (from 4) who are considered stable according to criteria 5: a,b,c,d,e,f */
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
                    hivMetadata.getNeutropenia(),
                    hivMetadata.getPancreatitis(),
                    hivMetadata.getHepatotoxicity(),
                    hivMetadata.getPsychologicalChanges(),
                    hivMetadata.getMyopathy(),
                    hivMetadata.getSkinAllergy(),
                    hivMetadata.getLipodystrophy(),
                    hivMetadata.getLacticAcidosis(),
                    hivMetadata.getPeripheralNeuropathy(),
                    hivMetadata.getDiarrhea(),
                    hivMetadata.getOtherDiagnosis(),
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
    cd.setCompositionString("A AND (B OR (NOT patientsWithViralLoad AND C))  AND NOT F");

    return cd;
  }

  /**
   * 5A Patients who are on ART for at least 12 months (if patients age >=2 and <=9) or On ART for
   * at least 6 months (if patients age >=10)
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
   * 5C One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year (if patients age >=2 and <=4) or
   * One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 and <=9) 5C (i) 5C (ii)
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
   * 5C (i) One CD4 Lab result > 750 cels/mm3 or > 15% in last ART year (if patients age >=2 and
   * <=4)
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
                    hivMetadata.getFsrEncounterType())),
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
                    hivMetadata.getFsrEncounterType())),
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
                    hivMetadata.getFsrEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4), "effectiveDate=${endDate}"));

    cd.setCompositionString("(Cd4Abs OR Cd4Lab OR Cd4Percent) AND Age");

    return cd;
  }

  /** 5C (ii) One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 years) */
  private CohortDefinition getCD4CountAndCD4Percent2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "5C (ii) One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 years)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "CD4Abs",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteOBSConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                200.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Cd4Lab",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                200.0,
                null,
                null,
                Arrays.asList(
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("greaterThan5", 5, 900),
            "effectiveDate=${endDate}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            genericCohortQueries.hasNumericObs(
                hivMetadata.getCD4AbsoluteOBSConcept(),
                BaseObsCohortDefinition.TimeModifier.LAST,
                RangeComparator.GREATER_THAN,
                200.0,
                null,
                null,
                Arrays.asList(hivMetadata.getMasterCardEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("((CD4Abs OR Cd4Lab OR B) AND Age)");

    return cd;
  }

  /** Patients with LAST Viral Load Result < 1000 copies/ml in last ART year (only if VL exists) */
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
        "SELECT vl_max.patient_id from( "
            + "    SELECT vl.patient_id, MAX(vl.latest_date) date FROM (  "
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
            + "        GROUP BY p.patient_id) vl "
            + "        INNER JOIN encounter e "
            + "            ON e.patient_id = vl.patient_id "
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
            + "                                           AND e.encounter_datetime = vl.latest_date )  "
            + "                           OR   "
            + "                                       (e.encounter_type =${masterCard}  "
            + "                                             AND o.obs_datetime      "
            + "                        BETWEEN date_add(date_add(:endDate, interval -12 MONTH), interval 1 day) AND :endDate  "
            + "                                             AND o.obs_datetime = vl.latest_date       )  "
            + "                                 )  "
            + "        "
            + "    GROUP BY   vl.patient_id) vl_max ";

    StringSubstitutor sb = new StringSubstitutor(map);
    String replaceQuery = sb.replace(query);

    cd.setQuery(replaceQuery);

    return cd;
  }

  /** Patients who are scheduled for the next pickup. */
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

  private CohortDefinition getPatientsScheduled175to190days(
      List<EncounterType> encounterTypes, Concept concept) {
    int lowerBound = 175;
    int upperBound = 190;
    return getPatientsScheduled(concept, encounterTypes, upperBound, lowerBound);
  }

  /** Active patients on ART MasterCard who are in AF Cohort Definition Query */
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

  /** Get All patients who have been enrolled in the GAAC program */
  private CohortDefinition getAllPatientsEnrolledOnGaac() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("All Patients Enrolled On GAAC");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.getPatientsEnrolledOnGAAC());
    return cd;
  }

  /** Patients marked in last Dispensa Comunitaria as start or continue regimen query */
  private CohortDefinition getPatientsMarkedInLastCommunityDispense() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getCommunityDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugs());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

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

  private CohortDefinition getPatientsWithStartOrContinueCA() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getAccessionClubs());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

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
