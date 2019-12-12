package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import org.openmrs.module.reporting.cohort.definition.*;
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
  @Autowired private GenderCohortQueries genderCohorts;
  @Autowired private TbCohortQueries tbCohortQueries;

  @Autowired private AgeCohortQueries ageCohortQueries;
  @Autowired private HivCohortQueries hivCohortQueries;
  @Autowired private HivMetadata hivMetadata;
  @Autowired private CommonMetadata commonMetadata;

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
            hivCohortQueries.getPatientsOnTbTreatment(),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType()),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
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
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getHivViralLoadConcept().getConceptId(),
            hivMetadata.getHivViralLoadQualitative().getConceptId()));

    return sql;
  }

  /**
   * D2: Number of active patients on ART Not Eligible for DSD D1
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndUnstable() {
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
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "tbPatient",
        EptsReportUtils.map(
            tbCohortQueries.getPatientsOnTbTreatment(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatientsTxCurr AND NOT (activeAndStablePatients OR tbPatient OR breastfeeding OR pregnant)");

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
            getPatientsWhoAreActiveAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * D2: Get patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastFeedingAndNotPregnant() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
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
            getPatientsWhoAreActiveAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (breastfeeding AND NOT pregnant)");

    return cd;
  }

  /**
   * D2: Get Patients who are pregnant including those that are pregnant and breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnant() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND pregnant");

    return cd;
  }

  /**
   * N2: Number of active on ART whose next ART pick-up is schedule for 83-97 days after the date of
   * their last ART drug pick-up (Fluxo Rápido)
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N2: Number of active, stable, patients on ART";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true);
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
    String dispensationMappings =
        "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";

    cd.addSearch("TxCurr", EptsReportUtils.map(txCurr, mappings));
    cd.addSearch("scheduled", EptsReportUtils.map(patientsScheduled, mappings));
    cd.addSearch("quarterly", EptsReportUtils.map(quarterly, dispensationMappings));
    cd.addSearch("startOrContinue", EptsReportUtils.map(startOrContinue, dispensationMappings));
    cd.addSearch("completed", EptsReportUtils.map(completed, dispensationMappings));

    cd.setCompositionString("TxCurr AND (scheduled OR quarterly OR startOrContinue) NOT completed");

    return cd;
  }

  /**
   * N2: Get all patients who are scheduled for the next pickup.
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
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Location.class));
    cd.addCalculationParameter("conceptId", conceptId);
    cd.addCalculationParameter("encounterTypes", encounterTypes);
    cd.addCalculationParameter("upperBound", upperBound);
    cd.addCalculationParameter("lowerBound", lowerBound);

    return cd;
  }

  /**
   * N2 STABLE: Get number of active on ART whose next ART pick-up is schedule for 83-97 days after
   * the date of their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupAs3Months",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3Months(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsWithNextPickupAs3Months AND patientsWhoAreStable");

    return cd;
  }

  /**
   * N2 UNSTABLE: Get number of active on ART whose next ART pick-up is schedule for 83-97 days
   * after the date of their last ART drug pick-up
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveWithNextPickupAs3MonthsAndUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsWithNextPickupDate",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3Months(),
            "endDate=${endDate},location=${location}"));
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
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWithNextPickupDate AND (NOT patientsWhoAreStable OR breastfeeding OR pregnant)");

    return cd;
  }

  /**
   * N2: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Stable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N2 Patients who are NOT pregnant and NOT breastfeeding");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "activeAndStableN2",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "eligiblePatientsD1",
        EptsReportUtils.map(
            getAllPatientsWhoAreActiveAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStableN2 AND eligiblePatientsD1");

    return cd;
  }

  /**
   * N2: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN2Unstable() {
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
        "patientsWithNextPickupDate",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3Months(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveWithNextPickupAs3MonthsAndStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "patientsWithNextPickupDate AND NOT patientsWhoAreStable AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }
 
  /**
   * N3 : Get the number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190Days() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName =
        "N3: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation";

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "scheduledN3",
        EptsReportUtils.map(
            getPatientsScheduled(
                hivMetadata.getReturnVisitDateConcept(),
                Arrays.asList(
                    hivMetadata.getARVPediatriaSeguimentoEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType()),
                190,
                175),
            "onOrBefore=${endDate},location=${location}"));
    cd.setCompositionString("TxCurr AND scheduledN3");

    return cd;
  }

  /**
   * N3 STABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190DaysStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 STABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatients",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatients AND stablePatients");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get number of active patients on ART whose next clinical consultation is scheduled
   * 175-190 days after the date of the last clinical consultation
   *
   * @return
   */
  public CohortDefinition getPatientsWithNextConsultationScheduled175To190DaysUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 UNSTABLE: Number of active patients on ART whose next clinical consultation is scheduled 175-190 days after the date of the last clinical consultation");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "endDate=${endDate},location=${location}"));
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
   * N3: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Unstable() {
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
        "allPatients",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "endDate=${endDate},location=${location}"));
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
   * N3: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN3Stable() {
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
        "activeAndStableN3",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190DaysStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndStableN3 AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get Patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnantN3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N3 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeedingN3",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnantN3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndScheduled175To190DaysN3",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "activeAndScheduled175To190DaysN3 AND (breastfeedingN3 AND NOT pregnantN3)");

    return cd;
  }

  /**
   * N3 UNSTABLE: Get Patient who are pregnant and includes breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndBreastfeedingN3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N3: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnantN3",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndScheduled175To190DaysN3",
        EptsReportUtils.map(
            getPatientsWithNextConsultationScheduled175To190Days(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndScheduled175To190DaysN3 AND pregnantN3");

    return cd;
  }

  /**
   * N4: Get number of active patients on ART that are participating in GAAC at the end of the month
   * prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaac() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "N4: Patients who are active and participating in GAAC";

    cd.setName(
        "N4: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "TxCurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "patientsEnrolledOnGaac",
        EptsReportUtils.map(
            getAllPatientsEnrolledOnGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("TxCurr AND patientsEnrolledOnGaac");

    return cd;
  }

  /**
   * N4 STABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 STABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4Stable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsN4Stable",
        EptsReportUtils.map(
            getPatientsWhoAreStable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsN4Stable AND stablePatientsN4Stable");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get number of active patients on ART that are participating in GAAC at the end of
   * the month prior to month of results submission deadline.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreActiveAndParticpatingInGaacUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 UNSTABLE: Number of active patients on ART that are participating in GAAC at the end of the month prior to month of results submission deadline.");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsN4InGaac",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
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
   * N4: Stable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Stable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N4 Patients who are NOT pregnant and NOT breastfeeding");
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
   * N4: Unstable Patients who are Non-pregnant and Non-Breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreNotPregnantAndNotBreastfeedingN4Unstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N4 Patients who are NOT pregnant and NOT breastfeeding");
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
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "allPatientsN4InGaac AND NOT stablePatients AND NOT pregnant AND NOT breastfeeding");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get Patients who are breastfeeding and not pregnant
   *
   * @return
   */
  public CohortDefinition getPatientsWhoAreBreastfeedingAndNotPregnantN4() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N4 Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeedingN4",
        EptsReportUtils.map(
            getBreastfeedingComposition(),
            "onOrAfter=${endDate-18m},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "pregnantN4",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeOnGAACN4",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeOnGAACN4 AND (breastfeedingN4 AND NOT pregnantN4)");

    return cd;
  }

  /**
   * N4 UNSTABLE: Get Patient who are pregnant and including breastfeeding
   *
   * @return
   */
  public CohortDefinition getPatientsWhoArePregnantAndBreastfeedingN4() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N4: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnantN4",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${endDate-9m},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeOnGAACN4",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticpatingInGaac(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeOnGAACN4 AND pregnantN4");

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
            hivMetadata.getARVPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getOtherDiagnosis().getConceptId(),
            hivMetadata.getKaposiSarcomaConcept().getConceptId()));

    return cd;
  }

  @DocumentedDefinition(
      "Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA")
  public CohortDefinition getPatientsWhoAreActiveAndParticipatingInAccessionClubs() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txCurr = txCurrCohortQueries.getTxCurrCompositionCohort("TX_CURR", true);
    CohortDefinition startOrContinueCA = getPatientsWithStartOrContinueCA();

    String mappings = "onOrBefore=${endDate},location=${location}";
    cd.addSearch("txCurr", EptsReportUtils.map(txCurr, mappings));

    String caMappings = "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}";
    cd.addSearch("startOrContinueCA", EptsReportUtils.map(startOrContinueCA, caMappings));

    cd.setCompositionString("txCurr AND startOrContinueCA");

    return cd;
  }

  @DocumentedDefinition(
      "Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and stable")
  public CohortDefinition getPatientsWhoAreActiveParticipatingInAccessionClubsAndStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition ca = getPatientsWhoAreActiveAndParticipatingInAccessionClubs();
    CohortDefinition stable = getPatientsWhoAreStable();

    cd.addSearch("ca", mapStraightThrough(ca));
    cd.addSearch("stable", mapStraightThrough(stable));

    cd.setCompositionString("ca AND stable");

    return cd;
  }

  @DocumentedDefinition(
      "Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and unstable")
  public CohortDefinition getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition ca = getPatientsWhoAreActiveAndParticipatingInAccessionClubs();
    CohortDefinition stable = getPatientsWhoAreStable();

    cd.addSearch("ca", mapStraightThrough(ca));
    cd.addSearch("stable", mapStraightThrough(stable));

    cd.setCompositionString("ca NOT stable");

    return cd;
  }

  @DocumentedDefinition(
      "Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and stable not pregnant or breastfeeding")
  public CohortDefinition getN5StableNonPregnantNonBreastfeeding() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition caStable = getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();
    CohortDefinition breastfeeding =
        txNewCohortQueries.getPatientsWhoGaveBirthWithinReportingPeriod();
    CohortDefinition tbPatients = tbCohortQueries.getPatientsOnTbTreatment();

    cd.addSearch("caStable", mapStraightThrough(caStable));
    cd.addSearch("pregnant", mapStraightThrough(pregnant));
    cd.addSearch("breastfeeding", mapStraightThrough(breastfeeding));
    cd.addSearch("tbPatients", mapStraightThrough(tbPatients));


    cd.setCompositionString("caStable NOT (pregnant OR breastfeeding OR tbPatients)");

    return cd;
  }

  @DocumentedDefinition(
      "Number of active patients on ART (Non-pregnant and Non-Breastfeeding not on TB treatment) who are in CA and unstable not pregnant or breastfeeding")
  public CohortDefinition getN5UnstableNonPregnantNonBreastfeeding() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition caUnstable = getPatientsWhoAreActiveParticipatingInAccessionClubsAndUnstable();
    CohortDefinition pregnant = txNewCohortQueries.getPatientsPregnantEnrolledOnART();
    CohortDefinition breastfeeding =
        txNewCohortQueries.getPatientsWhoGaveBirthWithinReportingPeriod();
    CohortDefinition tbPatients =tbCohortQueries.getPatientsOnTbTreatment();

    cd.addSearch("caUnstable", mapStraightThrough(caUnstable));
    cd.addSearch("pregnant", mapStraightThrough(pregnant));
    cd.addSearch("breastfeeding", mapStraightThrough(breastfeeding));
    cd.addSearch("tbPatients", mapStraightThrough(tbPatients));


    cd.setCompositionString("caUnstable NOT (pregnant OR breastfeeding OR tbPatients)");

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
    cd.addValue(hivMetadata.getContinueRegimen());
    return cd;
  }

  /**
   * THE BELOW CODE IS COMMENTED OUT. THE CODE IS FOR INDICATOR: Number of active patients on ART
   * who participate in >=1 measured DSD model THE INDICATOR WILL NOT BE INCLUDED IN THIS INITIAL
   * RELEASE OF THE DSD REPORTS.
   *
   * <p>Get the number of active patients on ART who participate in >=1 measured DSD model The
   * indicator has been commented out in this release.
   *
   * @return
   */
  /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModel() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String cohortName = "All patients (Adult and Children) included in TxCurr";

    cd.setName("N: Number of active patients on ART who participate in >=1 measured DSD model");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsTxCurrDsdModel",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort(cohortName, true),
            "onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "patientsParticipatingInDsdModel",
        EptsReportUtils.map(
            getAllPatientsParticipatingInDsdModel(), "endDate=${endDate},location=${location}"));

    cd.setCompositionString("allPatientsTxCurrDsdModel AND patientsParticipatingInDsdModel");

    return cd;
  }*/

  /**
   * STABLE: Get all patients who are active and participating in DSD model
   *
   * @return
   */
  /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelStable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "allPatientsInDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "patientsWhoAreStable",
        EptsReportUtils.map(getPatientsWhoAreStable(), "endDate=${endDate}"));

    cd.setCompositionString("allPatientsInDsdModel AND patientsWhoAreStable");

    return cd;
  }*/

  /**
   * UNSTABLE: Get all patients who are active and participating in DSD model
   *
   * @return
   */
  /* public CohortDefinition getPatientsWhoAreActiveAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "patientsInDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModel(),
            "endDate=${endDate},location=${location}"));
    cd.addSearch(
        "stablePatientsDsdModel",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelStable(),
            "endDate=${endDate},location=${location}"));

    cd.setCompositionString("patientsInDsdModel AND NOT stablePatientsDsdModel");

    return cd;
  }*/

  /**
   * Get patients who are breastfeeding and not pregnant
   *
   * @return
   */
  /* public CohortDefinition
      getPatientsWhoAreBreastFeedingAndNotPregnantAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName(
        "N Patients who are breastfeeding: includes all breastfeeding patients excluding pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getPatientsWhoAreBreastfeedingInLast18Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPatientsWhoArePregnantInLast9Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (breastfeeding AND NOT pregnant)");

    return cd;
  }*/

  /**
   * Get Patients who are pregnant and also breastfeeding
   *
   * @return
   */
  /*public CohortDefinition
      getPatientsWhoArePregnantAndNotBreastFeedingAndParticipateInDsdModelUnstable() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("N: Pregnant: includes all pregnant patients");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(
            getPatientsWhoArePregnantInLast9Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(
            getPatientsWhoAreBreastfeedingInLast18Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "activeAndUnstable",
        EptsReportUtils.map(
            getPatientsWhoAreActiveAndParticipateInDsdModelUnstable(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("activeAndUnstable AND (pregnant OR breastfeeding)");

    return cd;
  }*/

  /**
   * Get All patients that are participating in DSD Model
   *
   * @return
   */
  /* private CohortDefinition getAllPatientsParticipatingInDsdModel() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("All Patients participating in >=1 measured DSD model");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.setQuery(
        DsdQueries.getPatientsParticipatingInDsdModel(
            hivMetadata.getPrevencaoPositivaInicialEncounterType().getEncounterTypeId(),
            hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getGaac().getConceptId(),
            hivMetadata.getFamilyApproach().getConceptId(),
            hivMetadata.getAccessionClubs().getConceptId(),
            hivMetadata.getSingleStop().getConceptId(),
            hivMetadata.getRapidFlow().getConceptId(),
            hivMetadata.getQuarterlyDispensation().getConceptId(),
            hivMetadata.getCommunityDispensation().getConceptId(),
            hivMetadata.getAnotherModel().getConceptId(),
            hivMetadata.getStartDrugs().getConceptId(),
            hivMetadata.getContinueRegimen().getConceptId()));
    return cd;
  }*/

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
    cd.addValue(hivMetadata.getContinueRegimen());
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
}
