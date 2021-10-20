package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TPTCompletationQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxTbPrevQueriesInterface.QUERY.DisaggregationTypes;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTCompletationCohortQueries {

  @Autowired private GenericCohortQueries genericCohorts;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private TXTBCohortQueries tXTBCohortQueries;

  @Autowired private TxTbPrevCohortQueries txTbPrevCohortQueries;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  private static final String FIND_PATIENTS_WHO_COMPLETED_INH_THERAPY_BY_END_OF_REPORTING_PERIOD =
      "TPTCOMPLETION/PATIENTS_WHO_COMPLETED_INH_THERAPY_BY_END_OF_REPORTING_PERIOD.sql";

  private static final String
      FIND_PATIENTS_WHO_STARTED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD =
          "TPTCOMPLETION/PATIENTS_WHO_STARTED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD.sql";

  private static final String
      FIND_PATIENTS_WHO_COMPLETED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD =
          "TPTCOMPLETION/PATIENTS_WHO_COMPLETED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD.sql";

  @DocumentedDefinition(value = "findTxCurrWithTPTCompletation")
  public CohortDefinition findTxCurrWithTPTCompletation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get TxCurr With TPT Completation");
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TXCURR",
        EptsReportUtils.map(this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappings));
    dsd.addSearch(
        "STARTED-INH",
        EptsReportUtils.map(
            this.findPatientsWhoStartedINHTherapyBeforeReportingEndDate(), mappings));
    dsd.addSearch(
        "COMPLETED-INH", EptsReportUtils.map(this.findPatientsWhoCompletedINHTherapy(), mappings));
    dsd.addSearch(
        "STARTED-3HP",
        EptsReportUtils.map(this.findPatientsWhoStarted3HPTherapyBeforeReportEndDate(), mappings));
    dsd.addSearch(
        "COMPLETED-3HP",
        EptsReportUtils.map(
            this.findPatientsWhoCompleted3HPTherapyBeforeReportEndDate(), mappings));

    dsd.setCompositionString(
        "TXCURR AND ((STARTED-INH AND COMPLETED-INH) OR (STARTED-3HP AND COMPLETED-3HP))");

    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletation")
  public CohortDefinition findTxCurrWithoutTPTCompletation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get TxCurr Without TPT Completation");
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TXCURR",
        EptsReportUtils.map(this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(), mappings));
    dsd.addSearch(
        "TPT-COMPLETATION", EptsReportUtils.map(this.findTxCurrWithTPTCompletation(), mappings));

    dsd.setCompositionString("TXCURR NOT TPT-COMPLETATION");

    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletionWhoWereTreatedForTBForLast3Years")
  public CohortDefinition findTxCurrWithoutTPTCompletionWhoWereTreatedForTBForLast3Years() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();
    dsd.setName("get TxCurr Without TPT Completion But Who were treated for TB In last 3 Years");

    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TPT-NO-COMPLETION",
        EptsReportUtils.map(this.findTxCurrWithoutTPTCompletation(), mappings));

    dsd.addSearch("TB-NUMERATOR", EptsReportUtils.map(this.getTxTBNumerator(), mappings));

    dsd.setCompositionString("TPT-NO-COMPLETION AND TB-NUMERATOR");

    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletionWithPositivTBScreening")
  public CohortDefinition findTxCurrWithoutTPTCompletionWithPositivTBScreening() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();
    dsd.setName("get TxCurr Without TPT Completion With Positive TB Screening");

    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TPT-NO-COMPLETION",
        EptsReportUtils.map(this.findTxCurrWithoutTPTCompletation(), mappings));

    dsd.addSearch(
        "TB-POSITIVE-SCREENING",
        EptsReportUtils.map(this.getTxTBDenominatorAndPositiveScreening(), mappings));

    dsd.setCompositionString("TPT-NO-COMPLETION AND TB-POSITIVE-SCREENING");

    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletionButEligibleForTPTCompletation")
  public CohortDefinition findTxCurrWithoutTPTCompletionButEligibleForTPTCompletation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();
    dsd.setName("get TxCurr Without TPT Completion That Are Eligible for TPT Completation");

    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TPT-NO-COMPLETION",
        EptsReportUtils.map(this.findTxCurrWithoutTPTCompletation(), mappings));

    dsd.addSearch(
        "TPT-NO-COMPLETION-WITH-TB",
        EptsReportUtils.map(
            this.findTxCurrWithoutTPTCompletionWhoWereTreatedForTBForLast3Years(), mappings));

    dsd.addSearch(
        "TPT-NO-COMPLETION-WITH-TB-POSITIVE-SCREENING",
        EptsReportUtils.map(this.findTxCurrWithoutTPTCompletionWithPositivTBScreening(), mappings));

    dsd.setCompositionString(
        "TPT-NO-COMPLETION NOT (TPT-NO-COMPLETION-WITH-TB OR TPT-NO-COMPLETION-WITH-TB-POSITIVE-SCREENING)");
    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletionWhoInitiatedTPTInLast7Months")
  public CohortDefinition findTxCurrWithoutTPTCompletionWhoInitiatedTPTInLast7Months() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();
    dsd.setName("get TxCurr Without TPT Completion Who Initated TPT in the laste 7 Months");

    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TPT-NO-COMPLETION",
        EptsReportUtils.map(this.findTxCurrWithoutTPTCompletation(), mappings));
    dsd.addSearch(
        "TBPREV-DENOMINATOR", EptsReportUtils.map(this.getTbPrevTotalDenominator(), mappings));

    dsd.setCompositionString("TPT-NO-COMPLETION AND TBPREV-DENOMINATOR");

    return dsd;
  }

  @DocumentedDefinition(value = "findTxCurrWithoutTPTCompletionButEligibleForTPTInitiation")
  public CohortDefinition findTxCurrWithoutTPTCompletionButEligibleForTPTInitiation() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();
    dsd.setName("get TxCurr Without TPT Completion That Are Eligible for TPT Initiation");

    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "TXCURR-ELIGIBLE-TPT-COMPLETION",
        EptsReportUtils.map(
            this.findTxCurrWithoutTPTCompletionButEligibleForTPTCompletation(), mappings));

    dsd.addSearch(
        "TXCURR-TPT-INITIATION",
        EptsReportUtils.map(
            this.findTxCurrWithoutTPTCompletionWhoInitiatedTPTInLast7Months(), mappings));

    dsd.setCompositionString("TXCURR-ELIGIBLE-TPT-COMPLETION NOT TXCURR-TPT-INITIATION");
    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsWhoStartedINHTherapyBeforeReportingEndDate")
  private CohortDefinition findPatientsWhoStartedINHTherapyBeforeReportingEndDate() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("get Patients Who have started INH Therapy Before Reporting endDate");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.setQuery(
        TPTCompletationQueries.QUERY.findPatientsWhoStartedINHTherapyBeforeReportingEndDate);

    return definition;
  }

  @DocumentedDefinition(value = "findPatientsWhoStarted3HPTherapyBeforeReportEndDate")
  private CohortDefinition findPatientsWhoStarted3HPTherapyBeforeReportEndDate() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get Patients Who have Started 3HP Therapy before Report End Date");
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTED-3HP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Patients Who have Started 3HP Therapy before Report End Date",
                TPTCompletationQueries.QUERY
                    .findPatientsWhoStarted3HPTherapyBeforeReportingEndDate),
            mappings));

    dsd.addSearch(
        "COMPLETED-INH", EptsReportUtils.map(this.findPatientsWhoCompletedINHTherapy(), mappings));
    dsd.setCompositionString("STARTED-3HP NOT COMPLETED-INH");
    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsWhoCompleted3HPTherapyBeforeReportEndDate")
  private CohortDefinition findPatientsWhoCompleted3HPTherapyBeforeReportEndDate() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("get Patients Who have Started 3HP Therapy before Report End Date");
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "endDate=${endDate},location=${location}";

    dsd.addSearch(
        "COMPLETED-3HP",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "get Patients Who Completed 3HP Therapy",
                TPTCompletationQueries.QUERY.findPatientsWhoCompleted3HPTherapy),
            mappings));
    dsd.addSearch(
        "COMPLETED-INH", EptsReportUtils.map(this.findPatientsWhoCompletedINHTherapy(), mappings));
    dsd.setCompositionString("COMPLETED-3HP NOT COMPLETED-INH");
    return dsd;
  }

  @DocumentedDefinition(value = "txTbNumerator")
  private CohortDefinition getTxTBNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    String generalParameterMapping =
        "startDate=${endDate-1095d},endDate=${endDate},location=${location}";
    String previousPeriodParameters =
        "startDate=${endDate-6m-1095d},endDate=${endDate-1095d-1d},location=${location}";

    cd.setName("TxTB - Numerator");
    final CohortDefinition A = this.generateTxTBNumerator(generalParameterMapping);
    cd.addSearch("A", EptsReportUtils.map(A, generalParameterMapping));

    cd.addSearch(
        "A-PREVIOUS-PERIOD",
        EptsReportUtils.map(
            getTxTBNumeratorPreviousPeriod(
                previousPeriodParameters,
                "startDate=${endDate-12m-1095d},endDate=${endDate-6m-1095d-1d},location=${location}"),
            previousPeriodParameters));
    cd.addSearch(
        "art-started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            this.genericCohorts.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate-6m-1095d},location=${location}"));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            previousPeriodParameters));

    cd.setCompositionString(
        "A NOT (started-tb-treatment-previous-period OR (A-PREVIOUS-PERIOD AND art-started-by-end-previous-reporting-period))");

    addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "txTbNumeratorPreviousPeriod")
  private CohortDefinition getTxTBNumeratorPreviousPeriod(
      String previousPeriodParameters, String tbTreatmentPeriod) {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("TxTB - Numerator Previous Period");
    final CohortDefinition A = this.generateTxTBNumerator(previousPeriodParameters);
    cd.addSearch("A-PREVIOUS-PERIOD", EptsReportUtils.map(A, previousPeriodParameters));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(), tbTreatmentPeriod));
    cd.setCompositionString("A-PREVIOUS-PERIOD NOT started-tb-treatment-previous-period");

    addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "generateTxTBNumerator")
  private CohortDefinition generateTxTBNumerator(String generalParameterMapping) {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("TxTB - Numerator - generating Compositions");
    final CohortDefinition i =
        this.genericCohorts.generalSql(
            "onTbTreatment",
            TXTBQueries.dateObs(
                this.tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    this.hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));
    final CohortDefinition ii = this.tXTBCohortQueries.getInTBProgram();
    this.addGeneralParameters(i);
    cd.addSearch("i", EptsReportUtils.map(i, generalParameterMapping));
    cd.addSearch("ii", EptsReportUtils.map(ii, generalParameterMapping));
    cd.addSearch(
        "iii",
        EptsReportUtils.map(
            tXTBCohortQueries.getPulmonaryTBWithinReportingDate(), generalParameterMapping));
    cd.addSearch(
        "iv",
        EptsReportUtils.map(
            tXTBCohortQueries.getTuberculosisTreatmentPlanWithinReportingDate(),
            generalParameterMapping));

    final CohortDefinition artList = tXTBCohortQueries.artList();
    cd.addSearch("artList", EptsReportUtils.map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii OR iii OR iv) AND artList");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "getTxTBDenominatorAndPositiveScreening")
  private CohortDefinition getTxTBDenominatorAndPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    String generalParameterMapping =
        "startDate=${endDate-14d},endDate=${endDate},location=${location}";

    definition.setName("TxTB - Denominator and Positive Sreening");
    definition.addSearch(
        "denominator",
        EptsReportUtils.map(
            this.getTxTBDenominator(
                generalParameterMapping,
                "startDate=${endDate-6m-14d},endDate=${endDate-14d-1d},location=${location}"),
            generalParameterMapping));
    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(
            this.getTxTBPPositiveScreening(generalParameterMapping), generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator AND positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "getTxTBPPositiveScreening")
  private CohortDefinition getTxTBPPositiveScreening(String generalParameterMapping) {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("TxTB - positiveScreening");

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            tXTBCohortQueries.codedYesTbScreening(),
            "onOrAfter=${endDate-14d},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            tXTBCohortQueries.positiveInvestigationResultComposition(), generalParameterMapping));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            tXTBCohortQueries.negativeInvestigationResultAndAnyResultForTBScreeningComposition(),
            generalParameterMapping));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            generalParameterMapping));
    cd.addSearch(
        "E", EptsReportUtils.map(tXTBCohortQueries.getInTBProgram(), generalParameterMapping));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            tXTBCohortQueries.getPulmonaryTBWithinReportingDate(), generalParameterMapping));
    cd.addSearch(
        "G",
        EptsReportUtils.map(
            tXTBCohortQueries.getTuberculosisTreatmentPlanWithinReportingDate(),
            generalParameterMapping));
    cd.addSearch(
        "H",
        EptsReportUtils.map(
            tXTBCohortQueries.getAllTBSymptomsForDisaggregationComposition(),
            generalParameterMapping));
    cd.addSearch(
        "I",
        EptsReportUtils.map(
            tXTBCohortQueries.getSputumForAcidFastBacilliWithinReportingDate(),
            generalParameterMapping));

    cd.setCompositionString("A OR B OR C OR D OR E OR F OR G OR H OR I");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "getTxTBDenominator")
  private CohortDefinition getTxTBDenominator(
      String generalParameterMapping, String previousPeriodParameterMapping) {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB - Denominator");
    definition.addSearch(
        "art-list",
        EptsReportUtils.map(
            this.genericCohorts.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));
    definition.addSearch(
        "tb-screening",
        EptsReportUtils.map(
            tXTBCohortQueries.yesOrNoInvestigationResult(), generalParameterMapping));
    definition.addSearch(
        "tb-investigation",
        EptsReportUtils.map(
            tXTBCohortQueries.positiveInvestigationResultComposition(), generalParameterMapping));
    definition.addSearch(
        "started-tb-treatment",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            generalParameterMapping));
    definition.addSearch(
        "in-tb-program",
        EptsReportUtils.map(tXTBCohortQueries.getInTBProgram(), generalParameterMapping));

    definition.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            previousPeriodParameterMapping));
    definition.addSearch(
        "in-tb-program-previous-period",
        EptsReportUtils.map(tXTBCohortQueries.getInTBProgram(), previousPeriodParameterMapping));

    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            tXTBCohortQueries.getPatientsWhoAreTransferredOut(), generalParameterMapping));

    CohortDefinition fichaResumoMasterCard =
        this.genericCohorts.generalSql(
            "onFichaResumoMasterCard",
            TXTBQueries.dateObsByObsDateTimeClausule(
                this.tbMetadata.getPulmonaryTB().getConceptId(),
                this.hivMetadata.getYesConcept().getConceptId(),
                this.hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));

    CohortDefinition fichaClinicaMasterCard =
        this.genericCohorts.generalSql(
            "fichaClinicaMasterCard",
            TXTBQueries.dateObsByObsValueDateTimeClausule(
                this.tbMetadata.getTBTreatmentPlanConcept().getConceptId(),
                this.hivMetadata.getStartDrugsConcept().getConceptId(),
                this.hivMetadata.getAdultoSeguimentoEncounterType().getId()));

    this.addGeneralParameters(fichaResumoMasterCard);
    this.addGeneralParameters(fichaClinicaMasterCard);
    definition.addSearch(
        "ficha-resumo-master-card",
        EptsReportUtils.map(fichaResumoMasterCard, generalParameterMapping));
    definition.addSearch(
        "ficha-clinica-master-card",
        EptsReportUtils.map(fichaClinicaMasterCard, generalParameterMapping));
    definition.addSearch(
        "all-tb-symptoms",
        EptsReportUtils.map(
            this.getAllTBSymptomsForDemoninatorComposition(), generalParameterMapping));

    definition.addSearch(
        "A-PREVIOUS-PERIOD",
        EptsReportUtils.map(
            getTxTBNumeratorPreviousPeriod(
                previousPeriodParameterMapping,
                "startDate=${endDate-12m-14d},endDate=${endDate-6m-14d-1d},location=${location}"),
            previousPeriodParameterMapping));

    definition.addSearch(
        "art-started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            this.genericCohorts.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate-12m-14d},location=${location}"));

    definition.setCompositionString(
        "(art-list AND "
            + " ( tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program OR ficha-resumo-master-card OR ficha-clinica-master-card OR all-tb-symptoms)) "
            + " NOT ((transferred-out NOT (started-tb-treatment OR in-tb-program)) OR started-tb-treatment-previous-period OR in-tb-program-previous-period OR (A-PREVIOUS-PERIOD AND art-started-by-end-previous-reporting-period))");

    return definition;
  }

  @DocumentedDefinition(value = "get All TB Symptoms for Denominator")
  private CohortDefinition getAllTBSymptomsForDemoninatorComposition() {

    String generalParameterMapping =
        "startDate=${endDate-14d},endDate=${endDate},location=${location}";

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB - All TB Symptoms for Denominator");

    definition.addSearch(
        "tuberculosis-symptoms",
        EptsReportUtils.map(
            tXTBCohortQueries.getTuberculosisSymptoms(
                this.hivMetadata.getYesConcept().getConceptId(),
                this.hivMetadata.getNoConcept().getConceptId()),
            generalParameterMapping));

    definition.addSearch(
        "active-tuberculosis",
        EptsReportUtils.map(tXTBCohortQueries.getActiveTuberculosis(), generalParameterMapping));

    definition.addSearch(
        "tb-observations",
        EptsReportUtils.map(tXTBCohortQueries.getTbObservations(), generalParameterMapping));

    definition.addSearch(
        "application-for-laboratory-research",
        EptsReportUtils.map(
            tXTBCohortQueries.getApplicationForLaboratoryResearch(), generalParameterMapping));

    definition.addSearch(
        "tb-genexpert-or-culture-test-or-lam-test",
        EptsReportUtils.map(
            tXTBCohortQueries.getTbGenExpertORCultureTestOrTbLamOrBk(), generalParameterMapping));

    definition.setCompositionString(
        "tuberculosis-symptoms OR active-tuberculosis OR tb-observations OR application-for-laboratory-research OR tb-genexpert-or-culture-test-or-lam-test");

    return definition;
  }

  @DocumentedDefinition(value = "getTbPrevTotalDenominator")
  public CohortDefinition getTbPrevTotalDenominator() {
    final CompositionCohortDefinition dsd = new CompositionCohortDefinition();

    dsd.setName("Patients Who Started TPT");
    dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
    dsd.addParameter(new Parameter("location", "location", Location.class));
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    dsd.addSearch(
        "STARTED-TPT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Who have Started TPT During Previous Reporting Period",
                EptsQuerysUtils.loadQuery(
                    FIND_PATIENTS_WHO_STARTED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD)),
            mappings));
    dsd.addSearch(
        "TRF-OUT",
        EptsReportUtils.map(txTbPrevCohortQueries.findPatientsTransferredOut(), mappings));
    dsd.addSearch(
        "ENDED-TPT",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Who have Completed TPT",
                EptsQuerysUtils.loadQuery(
                    FIND_PATIENTS_WHO_COMPLETED_TB_PREV_PREVENTIVE_TREATMENT_DURING_7MONTHS_PREVIOUS_REPORTING_ENDDATE_PERIOD)),
            mappings));
    dsd.addSearch(
        "NEWLY-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients New on ART Who Have Started TPT",
                TPTCompletationQueries.QUERY
                    .findPatientsWhoStartedArtAndTbPrevPreventiveTreatmentInDisaggregation(
                        DisaggregationTypes.NEWLY_ENROLLED)),
            mappings));

    dsd.addSearch(
        "PREVIOUS-ART",
        EptsReportUtils.map(
            this.genericCohorts.generalSql(
                "Finding Patients Previously on ART Who Have Started TPT",
                TPTCompletationQueries.QUERY
                    .findPatientsWhoStartedArtAndTbPrevPreventiveTreatmentInDisaggregation(
                        DisaggregationTypes.PREVIOUSLY_ENROLLED)),
            mappings));

    dsd.setCompositionString(
        "(STARTED-TPT AND (NEWLY-ART OR PREVIOUS-ART)) NOT (TRF-OUT NOT ENDED-TPT) ");

    return dsd;
  }

  @DocumentedDefinition(value = "findPatientsWhoCompletedINHTherapy")
  public CohortDefinition findPatientsWhoCompletedINHTherapy() {
    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Finding Patients Who completed INH Therapy ");
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.setQuery(
        EptsQuerysUtils.loadQuery(
            FIND_PATIENTS_WHO_COMPLETED_INH_THERAPY_BY_END_OF_REPORTING_PERIOD));

    return definition;
  }

  private void addGeneralParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }
}
