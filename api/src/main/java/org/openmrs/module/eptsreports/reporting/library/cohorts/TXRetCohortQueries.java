package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXRetQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.common.RangeComparator;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXRetCohortQueries {

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private CommonMetadata commonMetadata;

  @Autowired private HivMetadata hivMetadata;

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
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
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "NAONOTIFICADO",
        EptsReportUtils.map(
            cohortDefinition(
                genericCohortQueries.generalSql(
                    "naonotificado", TXRetQueries.naonotificadoTwelveMonths())),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("NOTIFICADO OR NAONOTIFICADO");
    addParameters(cd);
    return cd;
  }

  /** numerator */
  public CohortDefinition inCourtForTwelveMonths() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("inCourt12Months");
    cd.addSearch(
        "OBITO",
        EptsReportUtils.map(
            cohortDefinition(obitoTwelveMonths()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "SUSPENSO",
        EptsReportUtils.map(
            cohortDefinition(suspensoTwelveMonths()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "INICIOTARV",
        EptsReportUtils.map(
            cohortDefinition(initiotArvTwelveMonths()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "ABANDONO",
        EptsReportUtils.map(
            cohortDefinition(abandonoTwelveMonths()),
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("INICIOTARV NOT (OBITO OR SUSPENSO OR ABANDONO)");
    addParameters(cd);
    return cd;
  }

  /** denominator */
  public CohortDefinition courtNotTransferredTwelveMonths() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "courtNotTransferred", TXRetQueries.courtNotTransferredTwelveMonths()));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition diagnosedOfModerateMalnutrition() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            commonMetadata.getClassificationOfMalnutritionConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(
                commonMetadata.getMalnutritionLightConcept(),
                commonMetadata.getMalnutritionConcept())));
  }

  public CohortDefinition receivedDomiciliaryVisitOrSearchFailureToTreat() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "receivedDomiciliaryVisitOrSearchFailureToTreat",
            TXRetQueries.receivedDomiciliaryVisitOrSearchFailureToTreat()));
  }

  public CohortDefinition males1YearLessEnrolledInARTAgeAtRegistration() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "males1YearLessEnrolledInARTAgeAtRegistration",
            TXRetQueries.males1YearLessEnrolledInARTAgeAtRegistration()));
  }

  public CohortDefinition tenToForteenFemalesWhoIncreasedHAARTAtARTStartDate() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "tenToForteenFemalesWhoIncreasedHAARTAtARTStartDate",
            TXRetQueries.tenToForteenFemalesWhoIncreasedHAARTAtARTStartDate()));
  }

  /** map endDate, location rightly when using this */
  public CohortDefinition withFirstWHOStage1() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            hivMetadata.getCurrentWHOHIVStageConcept(),
            TimeModifier.FIRST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(hivMetadata.getCurrentWHOHIVStage1Concept())));
  }

  public CohortDefinition upTo14WhoIncreasedHAARTAtARTStartDate() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "upTo14WhoIncreasedHAARTAtARTStartDate",
            TXRetQueries.upTo14WhoIncreasedHAARTAtARTStartDate()));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition withCD4CountGreaterThan350() {
    return cohortDefinition(
        genericCohortQueries.hasNumericObs(
            hivMetadata.getCD4CountConcept(),
            TimeModifier.FIRST,
            RangeComparator.GREATER_THAN,
            350.0,
            null,
            null,
            Arrays.asList(hivMetadata.getMisauLaboratorioEncounterType())));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition diagnosedOfChronicMalnutrition() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            commonMetadata.getClassificationOfMalnutritionConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(commonMetadata.getChronicMalnutritionConcept())));
  }

  public CohortDefinition notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentInitiation() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentInitiation",
            TXRetQueries.notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentInitiation()));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition lastRegisteredThroughMobileClinic() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            hivMetadata.getHivCareEntryPointConcept(),
            TimeModifier.LAST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(hivMetadata.getMobileClinicEntryPointConcept())));
  }

  /** map startDate, endDate, location rightly when using this */
  public CohortDefinition firstRegisteredThroughATIP() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            hivMetadata.getHivCareEntryPointConcept(),
            TimeModifier.FIRST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(
                hivMetadata.getProviderBasedServiceEntryPointConcept(),
                hivMetadata.getMedicalInPatientEntryPointConcept(),
                hivMetadata.getExternalConsultationEntryPointConcept(),
                hivMetadata.getLabSamplesEntryPointConcept(),
                hivMetadata.getHospitalEntryPointConcept(),
                hivMetadata.getReferredFromPedTreatmentEntryPointConcept())));
  }

  public CohortDefinition notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentRecently() {
    return cohortDefinition(
        genericCohortQueries.generalSql(
            "notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentRecently",
            TXRetQueries.notEligibleProphylaxiaWithIsoniazideBecauseOfTbTreatmentRecently()));
  }

  /** map startDate, endDate, location rightly when using this */
  private CohortDefinition withCD4CountLessOrEqual350() {
    return cohortDefinition(
        genericCohortQueries.hasNumericObs(
            hivMetadata.getCD4CountConcept(),
            TimeModifier.FIRST,
            RangeComparator.LESS_EQUAL,
            350.0,
            null,
            null,
            Arrays.asList(hivMetadata.getMisauLaboratorioEncounterType())));
  }

  /** map startDate, endDate and location rightly when using this */
  private CohortDefinition withFirstWHOStage3And4() {
    return cohortDefinition(
        genericCohortQueries.hasCodedObs(
            hivMetadata.getCurrentWHOHIVStageConcept(),
            TimeModifier.FIRST,
            SetComparator.IN,
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getARVPediatriaSeguimentoEncounterType()),
            Arrays.asList(
                hivMetadata.getCurrentWHOHIVStage3Concept(),
                hivMetadata.getCurrentWHOHIVStage4Concept())));
  }

  private CohortDefinition eligibleOnARTStages3And4AndCD4LessOr350() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("eligibleOnARTStages3And4AndCD4LessOr350");
    cd.addSearch(
        "CD4MENOR350", EptsReportUtils.map(cohortDefinition(withCD4CountLessOrEqual350()), ""));
    cd.addSearch(
        "ESTADIOIIIIV",
        EptsReportUtils.map(
            cohortDefinition(withFirstWHOStage3And4()),
            "onOrAfter=${startDate},onOrBefore=${endDate},locationList=${location}"));

    cd.setCompositionString("ESTADIOIIIIV OR CD4MENOR350");
    addParameters(cd);
    return cd;
  }
}
