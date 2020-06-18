package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
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

  /** D1: Number of active, stable, patients on ART. Combinantion of Criteria 1,2,3,4,5 */
  public CohortDefinition getAllPatientsWhoAreActiveAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "Number of active, stable, patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("moreThanOrEqual2Years", 2, 200),
            "effectiveDate=${endDate}"));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "4",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "5",
        EptsReportUtils.map(
            getAllPatientsOnSarcomaKarposi(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "6",
        EptsReportUtils.map(
            commonCohortQueries.getPatientsOnTbTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "7",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(1 AND 2 AND NOT (3 OR 4 OR 5 OR 6) AND 7)");

    return cd;
  }

  /**
   * Filter patients (from 4) who are considered stable according to criteria 5: a,b,c,d,e,f
   *
   * @return
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
            "onOrAfter=${endDate - 6m},onOrBefore=${endDate},locationList=${location}"));
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
   *
   * @return
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
   *
   * @return
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
   *
   * @return
   */
  private CohortDefinition getCD4CountAndCD4Percent1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

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
                    hivMetadata.getMisauLaboratorioEncounterType())),
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
                    hivMetadata.getMisauLaboratorioEncounterType())),
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
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("2-4", 2, 4), "effectiveDate=${endDate}"));

    cd.setCompositionString("(Cd4Abs OR Cd4Lab OR Cd4Percent) AND Age");

    return cd;
  }

  /**
   * 5C (ii) One CD4 result > 200 cels/mm3 in last ART year (if patients age >=5 years)
   *
   * @return
   */
  private CohortDefinition getCD4CountAndCD4Percent2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

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
                    hivMetadata.getMisauLaboratorioEncounterType())),
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
                    hivMetadata.getMisauLaboratorioEncounterType())),
            "onOrAfter=${endDate-12m},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "Age",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("greaterThan5", 5, 900),
            "effectiveDate=${endDate}"));

    cd.setCompositionString("((CD4Abs OR Cd4Lab) AND Age)");

    return cd;
  }

  /**
   * D1: Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D1 Patients who are NOT pregnant and NOT breastfeeding");
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
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * Patients with LAST Viral Load Result is < 1000 copies/ml in last ART year (only if VL exists)
   *
   * @return
   */
  public CohortDefinition getPatientsWithViralLoadLessThan1000Within12Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("LAST Viral Load Result is < 1000 copies/ml in last ART year (only if VL exists)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithViralLoadConcepts",
        EptsReportUtils.map(
            getPatientsWithViralLoadConcepts(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWithRecentViralLoadEncounter",
        EptsReportUtils.map(
            getPatientsWithTheRecentViralLoadEncounter(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWithViralLoadConcepts AND patientsWithRecentViralLoadEncounter");

    return cd;
  }

  /**
   * Get Patients with Viral Load less than 1000 in the last 12 months.
   *
   * @return
   */
  private CohortDefinition getPatientsWithViralLoadConcepts() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Viral Load Less Than 1000 Within 12Months");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));
    sql.setQuery(
        DsdQueries.patientsWithViralLoadLessThan1000(
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId(),
            hivMetadata.getBeyondDetectableLimitConcept().getConceptId(),
            hivMetadata.getUndetectableViralLoadConcept().getConceptId(),
            hivMetadata.getLessThan10CopiesConcept().getConceptId(),
            hivMetadata.getLessThan20CopiesConcept().getConceptId(),
            hivMetadata.getLessThan40CopiesConcept().getConceptId(),
            hivMetadata.getLessThan400CopiesConcept().getConceptId()));

    return sql;
  }

  /**
   * Patients with The Recent VL Encounter
   *
   * @return
   */
  private CohortDefinition getPatientsWithTheRecentViralLoadEncounter() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients with The Recent VL Encounter");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));

    sql.setQuery(
        DsdQueries.patientsWithTheRecentViralLoadEncounter(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));

    return sql;
  }

  /**
   * D2: Number of active patients on ART Not Eligible for DSD D1
   *
   */
  public CohortDefinition getD2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName =
        "All  patients  (adults  and  children)  currently  receiving  treatment (TxCurr)";

    cd.setName("Number of active patients on ART Not Eligible for DSD(D1)");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStablePatients",
        EptsReportUtils.map(
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "pregnantOrBreastfeedingOrTBTreatment",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatientsTxCurr AND NOT (activeAndStablePatients OR pregnantOrBreastfeedingOrTBTreatment)");

    return cd;
  }

  /**
   * D2: Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingD2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("D2 Patients who are NOT pregnant and NOT breastfeeding\"");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
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
   * N1: Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date of
   * their last ART drug pick-up (Fluxo Rápido)
   *
   * @return
   */
  public CohortDefinition getN1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N1: Number of active, stable, patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true);
    CohortDefinition patientsScheduled =
        getPatientsScheduled(
            hivMetadata.getReturnVisitDateForArvDrugConcept(),
            Arrays.asList(
                hivMetadata.getARVPharmaciaEncounterType(),
                hivMetadata.getMasterCardDrugPickupEncounterType()),
            97,
            83);
    CohortDefinition quarterly = getPatientsWithQuarterlyTypeOfDispensation();
    CohortDefinition startOrContinue = getPatientsWithStartOrContinueOnQuarterlyDispensation();
    CohortDefinition completed = getPatientsWithCompletedOnQuarterlyDispensation();

    String mappings = "onOrBefore=${endDate},location=${location}";
    String dispensationMappings =
        "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    String genericMappinggs = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch("TxCurr", EptsReportUtils.map(txCurr, mappings));
    cd.addSearch("scheduled", EptsReportUtils.map(patientsScheduled, mappings));
    cd.addSearch("quarterly", EptsReportUtils.map(quarterly, dispensationMappings));
    cd.addSearch("startOrContinue", EptsReportUtils.map(startOrContinue, dispensationMappings));
    cd.addSearch("completed", EptsReportUtils.map(completed, dispensationMappings));
    cd.addSearch(
        "pregnantOrBreastfeedingOrTBTreatment",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "TxCurr AND (scheduled OR quarterly OR startOrContinue) NOT (completed OR pregnantOrBreastfeedingOrTBTreatment");

    return cd;
  }

  /**
   * N1: Get all patients who are scheduled for the next pickup.
   *
   * @return
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
   * N1 STABLE: Get number of active on ART whose next ART pick-up is schedule for 83-97 days after
   * the date of their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getN1AndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupAs3Months",
        EptsReportUtils.map(getN1(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsWithNextPickupAs3Months AND patientsWhoAreStable");

    return cd;
  }

  /**
   * N1: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingDTStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N1 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "activeAndStableN1",
        EptsReportUtils.map(
            getN1AndStable(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "eligiblePatientsD1",
        EptsReportUtils.map(
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStableN1 AND eligiblePatientsD1");

    return cd;
  }

  /**
   * N1: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingDTUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N1 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupDate",
        EptsReportUtils.map(getN1(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getN1AndStable(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWithNextPickupDate AND NOT patientsWhoAreStable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N2 : Get the number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getN2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName =
        "N2: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true);
    CohortDefinition patientsScheduled = getPatientsScheduled175to190days();
    CohortDefinition rapidFlow = getPatientsWithStartOrContinueOnRapidFlow();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("TxCurr", EptsReportUtils.map(txCurr, mappings));
    cd.addSearch("scheduledN2", EptsReportUtils.map(patientsScheduled, mappings));

    String rapidFlowMappings =
        "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("rapidFlow", EptsReportUtils.map(rapidFlow, rapidFlowMappings));
    cd.addSearch(
        "completed", EptsReportUtils.map(getPatientsWhoCompletedRapidFlow(), rapidFlowMappings));

    cd.addSearch(
        "nonPregnantNonBreastFeedingNonTb",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString(
        "TxCurr AND (scheduledN2 OR rapidFlow) NOT (completed OR nonPregnantNonBreastFeedingNonTb)");

    return cd;
  }

  private CohortDefinition getPatientsWhoCompletedRapidFlow() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getRapidFlow());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());
    return cd;
  }

  private CohortDefinition getPatientsWithStartOrContinueOnRapidFlow() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getRapidFlow());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  private CohortDefinition getPatientsScheduled175to190days() {
    int lowerBound = 175;
    int upperBound = 190;
    Concept returnVisitDate = hivMetadata.getReturnVisitDateConcept();
    List<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getPediatriaSeguimentoEncounterType());
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    return getPatientsScheduled(returnVisitDate, encounterTypes, upperBound, lowerBound);
  }

  /**
   * N2 STABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getN2Stable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N2 STABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients", EptsReportUtils.map(getN2(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatients AND stablePatients");

    return cd;
  }

  /**
   * N2 UNSTABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getN2Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N2 UNSTABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients", EptsReportUtils.map(getN2(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatients AND (NOT stablePatients OR breastfeeding OR pregnant)");

    return cd;
  }

  /**
   * N2: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingFRUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N2 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "allPatients", EptsReportUtils.map(getN2(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatients AND NOT stablePatients AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N2: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingFRStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N2 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStableN2",
        EptsReportUtils.map(
            getN2Stable(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStableN2 AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N3: Get number of active patients on ART that are participating in GAAC at the end of the month
   * prior to month of results submission deadline.
   */
  public CohortDefinition getN3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N3: Patients who are active and participating in GAAC";

    cd.setName(
        "N3: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true);
    CohortDefinition patientsEnrolledOnGaac = getAllPatientsEnrolledOnGaac();
    CohortDefinition startOrContinueGAAC = getPatientsWithStartOrContinueGAAC();
    CohortDefinition completedGAAC = getPatientsWhoCompletedGAAC();

    cd.addSearch(
        "TxCurr", EptsReportUtils.map(txCurr, "onOrBefore=${endDate},location=${location}"));
    cd.addSearch("patientsEnrolledOnGaac", mapStraightThrough(patientsEnrolledOnGaac));

    String gaacMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("startOrContinueGAAC", EptsReportUtils.map(startOrContinueGAAC, gaacMappings));
    cd.addSearch("completedGAAC", EptsReportUtils.map(completedGAAC, gaacMappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "tbTreatment",
        EptsReportUtils.map(
            commonCohortQueries.getPatientsOnTbTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "TxCurr AND (patientsEnrolledOnGaac OR startOrContinueGAAC) NOT (completedGAAC OR pregnant OR breastfeeding OR tbTreatment)");

    return cd;
  }

  /**
   * N3 STABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 STABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4Stable",
        EptsReportUtils.map(
            getN3(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsN4Stable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsN4Stable AND stablePatientsN4Stable");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 UNSTABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4InGaac",
        EptsReportUtils.map(
            getN3(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsN4Stable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatientsN4InGaac AND (NOT stablePatientsN4Stable OR breastfeeding OR pregnant)");

    return cd;
  }

  /**
   * N3: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N3 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndStableN4",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaacStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStableN4 AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N3: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingGAACUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N3 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "allPatientsN4InGaac",
        EptsReportUtils.map(
            getN3(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatientsN4InGaac AND NOT stablePatients AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * 1.Select all patients (adults and children) currently receiving treatment, included in Tx-Curr
   * for selected Location and reporting period (Tx-Curr)
   *
   * <p>2.Filter all patients marked in last “Abordagem Familiar (AF)” as Iniciar (I) or Continua
   * (C) on Ficha Clinica – Master Card Encounter Type Id = 6 Last Family Approach Concept
   * (id=23725) Value.coded= START DRUGS (id=1256) OR Value.coded= (CONTINUE REGIMEN id=1257)
   *
   * <p>3.Exclude patients who are registered as pregnant during the following period: a.startDate =
   * reporting endDate – 9 months b.endDate = reporting endDate *4.Exclude patients who are
   * registered as breastfeeding during the following period: a.startDate = reporting endDate – 18
   * months b.endDate = reporting endDate 5.Exclude patients who are on TB Treatment (see common
   * queries for the specs on 9. Patients on TB Treatment)
   *
   * @return
   */
  public CohortDefinition getN4() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "Active patients on ART  who are in AF";

    cd.setName(
        "Active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in AF");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "txCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "masterCardPatients",
        EptsReportUtils.map(
            getPatientsOnMasterCardAFQuery(), "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "PregnantAndBreastfeedingAndOnTBTreatment",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "(txCurr AND masterCardPatients) AND NOT  PregnantAndBreastfeedingAndOnTBTreatment");

    return cd;
  }

  /**
   * Active patients on ART MasterCard who are in AF Cohort Definition Query
   *
   * @return
   */
  private CohortDefinition getPatientsOnMasterCardAFQuery() {
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
   * Active patients on ART who are in AF and Eligible
   *
   * @return
   */
  public CohortDefinition getPatientsOnMasterCardAFWhoAreEligible() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Active patients on ART who are in AF and Eligible");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "eligiblePatientsD1",
        EptsReportUtils.map(
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "masterCardAndTxCurrPatients",
        EptsReportUtils.map(
            getN4(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("eligiblePatientsD1 AND masterCardAndTxCurrPatients");

    return cd;
  }

  /**
   * Active patients on ART who are in AF and Not Eligible
   *
   * @return
   */
  public CohortDefinition getPatientsOnMasterCardAFWhoAreNotEligible() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Active patients on ART who are in AF and Not Eligible");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "masterCardAndTxCurrPatients",
        EptsReportUtils.map(
            getN4(), "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsNotEligibleD2",
        EptsReportUtils.map(
            getD2(), "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsNotEligibleD2 AND masterCardAndTxCurrPatients");

    return cd;
  }

  /**
   * Get All patients who have been enrolled in the GAAC program
   *
   * @return
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
   * DSD Breastfeeding Composition Cohort
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "BreastfeedingComposition")
  public CohortDefinition getBreastfeedingComposition() {
    return txNewCohortQueries.getTxNewBreastfeedingComposition();
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

  /**
   * N7 Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment)
   * who are in DC
   */
  public CohortDefinition getN7() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "Active in ART marked with DC";
    cd.setName("N7 : Active patients in ART marked in last DC as start or continue regimen");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true);
    CohortDefinition communityDispense = getPatientsMarkedInLastCommunityDispense();
    CohortDefinition pregnantBreastfeedingOrTB = getPregnantAndBreastfeedingAndOnTBTreatment();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, mappings));

    String dspMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("communityDispense", EptsReportUtils.map(communityDispense, dspMappings));

    cd.addSearch("pregnantBreastfeedingOrTB", mapStraightThrough(pregnantBreastfeedingOrTB));

    cd.setCompositionString("txCurr AND communityDispense NOT pregnantBreastfeedingOrTB");

    return cd;
  }

  /**
   * Get Patients who are on Sarcoma Karposi
   *
   * @return
   */
  public CohortDefinition getAllPatientsOnSarcomaKarposi() {
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

  private CohortDefinition getPatientsWithStartOrContinueGAAC() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getGaac());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  private CohortDefinition getPatientsWhoCompletedGAAC() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getGaac());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());
    return cd;
  }

  /**
   * N5: Filter all patients marked in last “Clubes de Adesão (CA)” as Iniciar (I) or Continua (C)
   * on Ficha Clinica – Master Card
   *
   * @return
   */
  @DocumentedDefinition(
      "N5: Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA")
  public CohortDefinition getN5() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort("TX_CURR", true);
    CohortDefinition startOrContinueCA = getPatientsWithStartOrContinueCA();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();
    CohortDefinition breastfeeding = txNewCohortQueries.getTxNewBreastfeedingComposition();
    CohortDefinition tbTreatment = commonCohortQueries.getPatientsOnTbTreatment();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, mappings));

    String caMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("startOrContinueCA", EptsReportUtils.map(startOrContinueCA, caMappings));

    cd.addSearch(
        "PregnantAndBreastfeedingAndOnTBTreatment",
        EptsReportUtils.map(
            getPregnantAndBreastfeedingAndOnTBTreatment(),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString(
        "txCurr AND startOrContinueCA NOT PregnantAndBreastfeedingAndOnTBTreatment");

    return cd;
  }

  @DocumentedDefinition(
      "N5: Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and stable")
  public CohortDefinition getPatientsWhoAreActiveParticipatingInAccessionClubsAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition ca = getN5();
    CohortDefinition stable = getPatientsWhoAreStable();

    cd.addSearch("ca", mapStraightThrough(ca));
    cd.addSearch("stable", mapStraightThrough(stable));

    cd.setCompositionString("ca AND stable");

    return cd;
  }

  @DocumentedDefinition(
      "N5: Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and unstable")
  public CohortDefinition getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition ca = getN5();
    CohortDefinition stable = getPatientsWhoAreStable();

    cd.addSearch("ca", mapStraightThrough(ca));
    cd.addSearch("stable", mapStraightThrough(stable));

    cd.setCompositionString("ca NOT stable");

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
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());
    return cd;
  }

  private CohortDefinition getPatientsWithStartOrContinueOnQuarterlyDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugsConcept());
    cd.addValue(hivMetadata.getContinueRegimenConcept());
    return cd;
  }

  private CohortDefinition getPatientsWithQuarterlyTypeOfDispensation() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getTypeOfDispensationConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getQuarterlyConcept());
    return cd;
  }

  /**
   * Get Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment)
   *
   * @return CohortDefinition
   */
  public CohortDefinition getAllActivePatientsOnArt() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "Number of active patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            ageCohortQueries.createXtoYAgeCohort("moreThanOrEqual2Years", 2, 200),
            "effectiveDate=${endDate}"));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "4",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "5",
        EptsReportUtils.map(
            getAllPatientsOnSarcomaKarposi(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "6",
        EptsReportUtils.map(
            commonCohortQueries.getPatientsOnTbTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(1 AND 2 AND NOT (3 OR 4 OR 5 OR 6))");

    return cd;
  }

  /*
   * Get number of patients participating in at least one DSD model
   *
   * @return CohortDefinition
   * */
  public CohortDefinition getPatientsParticipatingInAtLeastOneDsdModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("participatingInDsdModel");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsParticipatingInAfCaPuFrDcDsdModels(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getAllPatientsWhoseDPIsScheduled83To97DaysAfterLastDrugPickupDate(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            getPatientsWhoseClinicalAppointmentScheduledFor175To190DaysAfterClinicalConsultation(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "4",
        EptsReportUtils.map(
            getAllPatientsEnrolledOnGaac(), "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "5",
        EptsReportUtils.map(
            getPatientsMarkedInLastGaaCAsIniciarOrContinuaOnFichaClinica(),
            "onOrBefore=${startDate},onOrAfter=${endDate},location=${location}"));
    cd.addSearch(
        "6",
        EptsReportUtils.map(
            getPatientsMarkedCompletedForLastGaac(),
            "onOrBefore=${startDate},onOrAfter=${endDate},location=${location}"));

    cd.setCompositionString("(1 OR 2 OR 3 OR 4 OR 5) AND NOT 6");

    return cd;
  }

  private CohortDefinition getPatientsParticipatingInAfCaPuFrDcDsdModels() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.getPatientsParticipatingInAfCaPuFrDcDsdModels());

    return cd;
  }

  /*
   * Get number of active patients on ART who participate in at least one DSD model
   *
   * @return CohortDefinition
   * */
  public CohortDefinition getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getAllActivePatientsOnArt(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsParticipatingInAtLeastOneDsdModel(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(1 AND 2)");

    return cd;
  }

  /*
   * Get all patients who participated in at least 1 DSD model and are stable
   *
   * @return CohortDefifnition
   * */
  public CohortDefinition getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModel(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(1 AND 2)");

    return cd;
  }

  /*
   * Get number of patients who participated in at least 1 DSD model and are unstable
   *
   * @return CohortDefinition
   * */
  public CohortDefinition getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModelAndUnStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getActivePatientsOnArtWhoParticipatedInAtLeastOneDsdModel(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("(1 AND NOT 2)");

    return cd;
  }

  /**
   * 2.5 All patients whose next Drugs Pick up appointment is scheduled for 83-97 days after the
   * date of their last Drugs pick up
   *
   * @return CohortDefinition
   */
  private CohortDefinition getAllPatientsWhoseDPIsScheduled83To97DaysAfterLastDrugPickupDate() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsWithLastDPWithin5monthsFromEndDateAndNextDPScheduled3MonthsLater(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsMarkedAsDTOnTipoDeLevantamento(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            getPatientsMarkedinlastDTAsIniciarOrManterFichaClinica(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "4",
        EptsReportUtils.map(
            getPatientsMarkedCompletedForTheirLastDT(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("(1 OR 2 OR 3) AND NOT 4");
    return cd;
  }

  /**
   * 2.5.1: Looks for patients who had last drug pickup within last 5 months from end date and had
   * next drug pickup scheduled for 3 months later
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getPatientsWithLastDPWithin5monthsFromEndDateAndNextDPScheduled3MonthsLater() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsWithLastDrugPickupWithin5monthsFromEndDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsWithNextDrugPickupScheduled3MonthsLater(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("1 AND 2");

    return cd;
  }

  /*
   * Looks for patients who had next drug pickup scheduled for 3 months later
   *
   * @return CohortDefinition
   * */
  private CohortDefinition getPatientsWithNextDrugPickupScheduled3MonthsLater() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.patientsWithNextDrugPickupScheduled3MonthsLater());

    return cd;
  }

  /**
   * Looks for patients who had last drug pickup within last 5 months from end date
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsWithLastDrugPickupWithin5monthsFromEndDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.patientsWithLastDrugPickupWithin5monthsFromEndDate());

    return cd;
  }

  /**
   * 2.5.2 Marked as DT on Tipo de Levantamento (ficha clinica – Master Card)
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsMarkedAsDTOnTipoDeLevantamento() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getTypeOfDispensationConcept());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getQuarterlyConcept());

    return cd;
  }

  /**
   * 2.5.3 Marked in last “Dispensa Trimestral (DT)” as Iniciar (I) or Manter (C) on Ficha Clinica –
   * Master Card
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsMarkedinlastDTAsIniciarOrManterFichaClinica() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugs());
    cd.addValue(hivMetadata.getContinueRegimenConcept());

    return cd;
  }

  /**
   * Get patients who are marked Completed for their last “Dispensa Trimestral (DT)”
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsMarkedCompletedForTheirLastDT() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getQuarterlyDispensation());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());

    return cd;
  }

  /**
   * 2.6 All patients whose next clinical appointment is scheduled for 175-190 days after the date
   * of their last clinical consultation
   *
   * @return @CohortDefinition
   */
  private CohortDefinition
      getPatientsWhoseClinicalAppointmentScheduledFor175To190DaysAfterClinicalConsultation() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Before Date", Date.class));
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDateWithNextApptmt6MonthsLater(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsMarkedInLastFluxoRapidoAsIniciarOrManterOnFichaClinica(),
            "onOrBefore=${startDate},onOrAfter=${endDate},location=${location}"));
    cd.addSearch(
        "3",
        EptsReportUtils.map(
            getPatientsMarkedCompletedForTheirLastFluxoRapido(),
            "onOrBefore=${startDate},onOrAfter=${endDate},location=${location}"));

    cd.setCompositionString("(1 OR 2) AND NOT 3");

    return cd;
  }

  /**
   * 2.6.3 Patients who are marked Completed for their last “Fluxo Rápido (FR)”
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsMarkedCompletedForTheirLastFluxoRapido() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getRapidFlow());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());

    return cd;
  }

  /**
   * 2.6.2 Marked in last “Fluxo Rapido (FR)” as Iniciar (I) or Manter (C) on Ficha Clinica – Master
   * Card
   *
   * @return CodedObsCohortDefinition
   */
  private CohortDefinition getPatientsMarkedInLastFluxoRapidoAsIniciarOrManterOnFichaClinica() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getRapidFlow());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugs());
    cd.addValue(hivMetadata.getContinueRegimenConcept());

    return cd;
  }

  /**
   * Looks for patients who had last follow up consultation within last 7 months from end date and
   * had next appointment scheduled for 6 months later
   *
   * @return CohortDefinition
   */
  private CohortDefinition
      getPatientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDateWithNextApptmt6MonthsLater() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "1",
        EptsReportUtils.map(
            getPatientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDate(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "2",
        EptsReportUtils.map(
            getPatientsWithNextApptmt6MonthsAfterConsultationDate(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("1 AND 2");

    return cd;
  }

  /**
   * Looks for patients who had last follow up consultation within last 7 months from end date
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsWithNextApptmt6MonthsAfterConsultationDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.patientsWithNextApptmt6MonthsAfterConsultationDate());

    return cd;
  }

  /**
   * Looks for patients who had last follow up consultation within last 7 months from end date
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(DsdQueries.patientsWithLastFollowUpConsultationWithinLast7MonthsFromEndDate());

    return cd;
  }

  /**
   * 2.7.1 All patients Marked in last “GAAC (GA)” as Iniciar (I) or Continua (C) on Ficha Clinica –
   * Master Card
   *
   * @return CohortDefinition
   */
  private CohortDefinition getPatientsMarkedInLastGaaCAsIniciarOrContinuaOnFichaClinica() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getGaac());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getStartDrugs());
    cd.addValue(hivMetadata.getContinueRegimenConcept());

    return cd;
  }

  /** 2.7.2 all patients who are marked Completed for their last “GAAC(GA)” */
  private CohortDefinition getPatientsMarkedCompletedForLastGaac() {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addEncounterType(hivMetadata.getAdultoSeguimentoEncounterType());
    cd.setTimeModifier(BaseObsCohortDefinition.TimeModifier.LAST);
    cd.setQuestion(hivMetadata.getGaac());
    cd.setOperator(SetComparator.IN);
    cd.addValue(hivMetadata.getCompletedConcept());

    return cd;
  }

  /**
   * Patients who are not pregnant 9 months before end date and not breastfeeding at 18 months
   * before end date
   */
  public CohortDefinition getNonPregnantAndNonBreastfeeding() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition breastfeeding = txNewCohortQueries.getTxNewBreastfeedingComposition();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();

    String pregnantMappings = "startDate=${endDate-9m},endDate=${endDate},location=${location}";
    cd.addSearch("pregnant", EptsReportUtils.map(pregnant, pregnantMappings));

    String breastfeedingMappings =
        "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}";
    cd.addSearch("breastfeeding", EptsReportUtils.map(breastfeeding, breastfeedingMappings));

    cd.setCompositionString("NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  private CohortDefinition getPregnantAndBreastfeedingAndOnTBTreatment() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("endDate", "After Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition breastfeeding = txNewCohortQueries.getTxNewBreastfeedingComposition();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();
    CohortDefinition tb = commonCohortQueries.getPatientsOnTbTreatment();

    String pregnantMappings = "startDate=${endDate-9m},endDate=${endDate},location=${location}";
    cd.addSearch("pregnant", EptsReportUtils.map(pregnant, pregnantMappings));

    String breastfeedingMappings =
        "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}";
    cd.addSearch("breastfeeding", EptsReportUtils.map(breastfeeding, breastfeedingMappings));

    String tbMappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch("tb", EptsReportUtils.map(tb, tbMappings));

    cd.setCompositionString("pregnant OR breastfeeding OR tb");

    return cd;
  }
}
