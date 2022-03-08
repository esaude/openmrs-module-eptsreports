/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBMontlyCascadeReportQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBMontlyCascadeReportQueries.QUERY.DiagnosticTestTypes;
import org.openmrs.module.eptsreports.reporting.library.queries.TXTBQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXTBDenominatorForTBMontlyCascadeQueries {

  @Autowired private TbMetadata tbMetadata;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private TXTBCohortQueries txtbCohortQueries;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  private String generalParameterMapping =
      "startDate=${endDate-6m},endDate=${endDate},location=${location}";

  @DocumentedDefinition(value = "TxTBDenominatorForPreviuosPeriod")
  public CohortDefinition getTxTBDenominator() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TxTB - Denominator Previous Period");
    this.addGeneralParameters(definition);

    definition.addSearch(
        "art-list",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate},location=${location}"));
    definition.addSearch(
        "tb-screening",
        EptsReportUtils.map(
            txtbCohortQueries.yesOrNoInvestigationResult(), generalParameterMapping));
    definition.addSearch(
        "tb-investigation",
        EptsReportUtils.map(
            txtbCohortQueries.positiveInvestigationResultComposition(), generalParameterMapping));
    definition.addSearch(
        "started-tb-treatment",
        EptsReportUtils.map(
            txtbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            generalParameterMapping));
    definition.addSearch(
        "in-tb-program",
        EptsReportUtils.map(txtbCohortQueries.getInTBProgram(), generalParameterMapping));

    definition.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            txtbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            "startDate=${endDate-12m},endDate=${endDate-6m-1d},location=${location}"));

    definition.addSearch(
        "in-tb-program-previous-period",
        EptsReportUtils.map(
            txtbCohortQueries.getInTBProgram(),
            "startDate=${endDate-12m},endDate=${endDate-6m-1d},location=${location}"));

    definition.addSearch(
        "transferred-out",
        EptsReportUtils.map(
            txtbCohortQueries.getPatientsWhoAreTransferredOut(),
            "startDate=${endDate-6m},endDate=${endDate},location=${location}"));

    definition.addSearch(
        "A-PREVIOUS-PERIOD",
        EptsReportUtils.map(this.getNumeratorPreviosPeriod(), generalParameterMapping));

    definition.addSearch(
        "art-started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate-6m-1d},location=${location}"));

    CohortDefinition fichaResumoMasterCard =
        this.genericCohortQueries.generalSql(
            "onFichaResumoMasterCard",
            TXTBQueries.dateObsByObsDateTimeClausule(
                this.tbMetadata.getPulmonaryTB().getConceptId(),
                this.hivMetadata.getYesConcept().getConceptId(),
                this.hivMetadata.getMasterCardEncounterType().getEncounterTypeId()));

    CohortDefinition fichaClinicaMasterCard =
        this.genericCohortQueries.generalSql(
            "fichaClinicaMasterCard",
            TXTBQueries.dateObsByObsValueDateTimeClausule(
                this.tbMetadata.getTBTreatmentPlanConcept().getConceptId(),
                this.hivMetadata.getStartDrugsConcept().getConceptId(),
                this.hivMetadata.getAdultoSeguimentoEncounterType().getId()));

    this.addGeneralParameters(fichaClinicaMasterCard);
    this.addGeneralParameters(fichaResumoMasterCard);

    definition.addSearch(
        "ficha-resumo-master-card",
        EptsReportUtils.map(fichaResumoMasterCard, generalParameterMapping));

    definition.addSearch(
        "ficha-clinica-master-card",
        EptsReportUtils.map(fichaClinicaMasterCard, generalParameterMapping));

    definition.addSearch(
        "all-tb-symptoms",
        EptsReportUtils.map(getAllTBSymptomsForDemoninatorComposition(), generalParameterMapping));

    definition.addSearch(
        "ficha-laboratorio-results",
        EptsReportUtils.map(getResultsOnFichaLaboratorio(), generalParameterMapping));

    definition.setCompositionString(
        "(art-list AND "
            + " ( tb-screening OR tb-investigation OR started-tb-treatment OR in-tb-program OR ficha-resumo-master-card OR ficha-clinica-master-card OR all-tb-symptoms OR ficha-laboratorio-results)) "
            + " NOT ((transferred-out NOT (started-tb-treatment OR in-tb-program)) OR started-tb-treatment-previous-period OR in-tb-program-previous-period OR (A-PREVIOUS-PERIOD AND art-started-by-end-previous-reporting-period ))");

    return definition;
  }

  @DocumentedDefinition(value = "TxTBDenominatorPositiveScreening")
  public CohortDefinition getTxTBDenominatorAndPositiveScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("TxTB - Denominator Positive Screening");
    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getTxTBDenominator(), generalParameterMapping));

    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.getTxTBPPositiveScreening(), generalParameterMapping));
    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator AND positive-screening");
    return definition;
  }

  @DocumentedDefinition(value = "TxTBDenominatorNegativeScreening")
  public CohortDefinition getTxTBDenominatorAndNegativeScreening() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - Denominator  Negative Screening");

    definition.addSearch(
        "denominator", EptsReportUtils.map(this.getTxTBDenominator(), generalParameterMapping));

    definition.addSearch(
        "positive-screening",
        EptsReportUtils.map(this.getTxTBPPositiveScreening(), generalParameterMapping));

    definition.addSearch(
        "new-on-art", EptsReportUtils.map(this.getNewOnArt(), this.generalParameterMapping));

    this.addGeneralParameters(definition);
    definition.setCompositionString("denominator NOT (new-on-art OR positive-screening)");
    return definition;
  }

  @DocumentedDefinition(value = "get Specimen Sent")
  public CohortDefinition getSpecimenSentCohortDefinition() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB -specimen-sent");

    CohortDefinition applicationForLaboratoryResearchDataset =
        this.genericCohortQueries.generalSql(
            "applicationForLaboratoryResearch",
            TXTBQueries.dateObsForEncounterAndQuestionAndAnswers(
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.hivMetadata.getApplicationForLaboratoryResearch().getConceptId()),
                Arrays.asList(
                    this.tbMetadata.getTbGenexpertTest().getConceptId(),
                    this.tbMetadata.getCultureTest().getConceptId(),
                    this.tbMetadata.getTbLam().getConceptId(),
                    this.tbMetadata.getSputumForAcidFastBacilli().getConceptId())));

    this.addGeneralParameters(applicationForLaboratoryResearchDataset);

    definition.addSearch(
        "application-for-laboratory-research",
        EptsReportUtils.map(applicationForLaboratoryResearchDataset, this.generalParameterMapping));
    definition.addSearch(
        "tb-genexpert-culture-lam-bk-test",
        EptsReportUtils.map(
            txtbCohortQueries.getTbGenExpertORCultureTestOrTbLamOrBk(),
            this.generalParameterMapping));
    definition.addSearch(
        "lab-results",
        EptsReportUtils.map(this.getResultsOnFichaLaboratorio(), this.generalParameterMapping));

    definition.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(this.getTxTBDenominator(), this.generalParameterMapping));

    definition.setCompositionString(
        "(application-for-laboratory-research OR tb-genexpert-culture-lam-bk-test OR lab-results) AND DENOMINATOR");

    return definition;
  }

  @DocumentedDefinition(value = "getTxTBPPositiveScreening")
  private CohortDefinition getTxTBPPositiveScreening() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();

    cd.setName("TxTB - positiveScreening");

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            txtbCohortQueries.codedYesTbScreening(),
            "onOrAfter=${endDate-6},onOrBefore=${endDate},locationList=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            txtbCohortQueries.positiveInvestigationResultComposition(), generalParameterMapping));
    cd.addSearch(
        "C",
        EptsReportUtils.map(
            txtbCohortQueries.negativeInvestigationResultAndAnyResultForTBScreeningComposition(),
            generalParameterMapping));
    cd.addSearch(
        "D",
        EptsReportUtils.map(
            txtbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            generalParameterMapping));
    cd.addSearch(
        "E", EptsReportUtils.map(txtbCohortQueries.getInTBProgram(), generalParameterMapping));
    cd.addSearch(
        "F",
        EptsReportUtils.map(
            txtbCohortQueries.getPulmonaryTBWithinReportingDate(), generalParameterMapping));
    cd.addSearch(
        "G",
        EptsReportUtils.map(
            txtbCohortQueries.getTuberculosisTreatmentPlanWithinReportingDate(),
            generalParameterMapping));
    cd.addSearch(
        "H",
        EptsReportUtils.map(
            txtbCohortQueries.getAllTBSymptomsForDisaggregationComposition(),
            generalParameterMapping));
    cd.addSearch(
        "I",
        EptsReportUtils.map(
            txtbCohortQueries.getSputumForAcidFastBacilliWithinReportingDate(),
            generalParameterMapping));

    cd.setCompositionString("A OR B OR C OR D OR E OR F OR G OR H OR I");
    this.addGeneralParameters(cd);
    return cd;
  }

  private CohortDefinition getNumeratorPreviosPeriod() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - txTbNumerator Previous Period");

    final CohortDefinition i =
        this.genericCohortQueries.generalSql(
            "onTbTreatment-Previous Reporting Period",
            TXTBQueries.dateObsForPreviousReportingPeriod(
                this.tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    this.hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));

    final CohortDefinition ii = txtbCohortQueries.getInTBProgramPreviousPeriod();

    this.addGeneralParameters(i);
    cd.addSearch("i", EptsReportUtils.map(i, this.generalParameterMapping));
    cd.addSearch("ii", EptsReportUtils.map(ii, this.generalParameterMapping));

    cd.addSearch(
        "iii",
        EptsReportUtils.map(
            txtbCohortQueries.getPulmonaryTBWithinPreviousReportingDate(),
            this.generalParameterMapping));

    cd.addSearch(
        "iv",
        EptsReportUtils.map(
            txtbCohortQueries.getTuberculosisTreatmentPlanWithinPreviousReportingDate(),
            this.generalParameterMapping));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            txtbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            "startDate=${endDate-18m},endDate=${endDate-12m-1d},location=${location}"));

    cd.setCompositionString("(i OR ii OR iii OR iv) NOT started-tb-treatment-previous-period");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "get All TB Symptoms for Denominator")
  private CohortDefinition getAllTBSymptomsForDemoninatorComposition() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB - All TB Symptoms for Denominator");

    definition.addSearch(
        "tuberculosis-symptoms",
        EptsReportUtils.map(
            txtbCohortQueries.getTuberculosisSymptoms(
                this.hivMetadata.getYesConcept().getConceptId(),
                this.hivMetadata.getNoConcept().getConceptId()),
            generalParameterMapping));

    definition.addSearch(
        "active-tuberculosis",
        EptsReportUtils.map(txtbCohortQueries.getActiveTuberculosis(), generalParameterMapping));

    definition.addSearch(
        "tb-observations",
        EptsReportUtils.map(txtbCohortQueries.getTbObservations(), generalParameterMapping));

    definition.addSearch(
        "application-for-laboratory-research",
        EptsReportUtils.map(
            txtbCohortQueries.getApplicationForLaboratoryResearch(), generalParameterMapping));

    definition.addSearch(
        "tb-genexpert-or-culture-test-or-lam-or-bk-test",
        EptsReportUtils.map(
            txtbCohortQueries.getTbGenExpertORCultureTestOrTbLamOrBk(), generalParameterMapping));

    definition.addSearch(
        "tb-raioxtorax",
        EptsReportUtils.map(txtbCohortQueries.getTbRaioXTorax(), generalParameterMapping));

    definition.setCompositionString(
        "tuberculosis-symptoms OR active-tuberculosis OR tb-observations OR application-for-laboratory-research OR tb-genexpert-or-culture-test-or-lam-or-bk-test OR tb-raioxtorax");

    return definition;
  }

  @DocumentedDefinition(value = "get Diagnóstico Laboratorial para TB")
  private CohortDefinition getResultsOnFichaLaboratorio() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    this.addGeneralParameters(definition);
    definition.setName("TxTB -Diagnóstico Laboratorial para TB");

    definition.addSearch(
        "sputum-for-acid-fast-bacilli",
        EptsReportUtils.map(
            txtbCohortQueries.getSputumForAcidFastBacilliWithinReportingDate(),
            generalParameterMapping));

    definition.addSearch(
        "genexpert-culture",
        EptsReportUtils.map(
            txtbCohortQueries.getGenExpertOrCulturaOnFichaLaboratorio(), generalParameterMapping));

    definition.addSearch(
        "tblam",
        EptsReportUtils.map(
            txtbCohortQueries.getTbLamOnFichaLaboratorio(), generalParameterMapping));

    definition.addSearch(
        "xpert-mtb",
        EptsReportUtils.map(
            txtbCohortQueries.getXpertMTBOnFichaLaboratorio(), generalParameterMapping));

    definition.setCompositionString(
        "sputum-for-acid-fast-bacilli OR genexpert-culture OR tblam OR xpert-mtb");

    return definition;
  }

  @DocumentedDefinition(value = "get New on Art")
  public CohortDefinition getNewOnArt() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - New on ART");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "started-on-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtOnPeriod(false, true),
            "onOrAfter=${endDate-6m},onOrBefore=${endDate},location=${location}"));
    definition.setCompositionString("started-on-period");
    return definition;
  }

  @DocumentedDefinition(value = "GenExpertTests")
  public CohortDefinition getGenExpertTests() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("GeneXpert MTB/RIF");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "genexperts",
        EptsReportUtils.map(
            this.findDiagnostiTests(DiagnosticTestTypes.GENEXPERT), generalParameterMapping));
    definition.setCompositionString("genexperts");
    return definition;
  }

  @DocumentedDefinition(value = "getGenExpertPositiveTestResults")
  public CohortDefinition getGenExpertPositiveTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("GeneXpert MTB/RIF Posetive Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "genexperts",
        EptsReportUtils.map(
            this.findDiagnosticPositiveTestResults(DiagnosticTestTypes.GENEXPERT),
            generalParameterMapping));
    definition.setCompositionString("genexperts");
    return definition;
  }

  @DocumentedDefinition(value = "getGenExpertNegativeTestResults")
  public CohortDefinition getGenExpertNegativeTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("GeneXpert MTB/RIF Negative Test Results");
    this.addGeneralParameters(definition);

    definition.addSearch(
        "genexperts", EptsReportUtils.map(this.getGenExpertTests(), generalParameterMapping));
    definition.addSearch(
        "positiveGenexperts",
        EptsReportUtils.map(this.getGenExpertPositiveTestResults(), generalParameterMapping));

    definition.setCompositionString("genexperts not positiveGenexperts");
    return definition;
  }

  @DocumentedDefinition(value = "BaciloscopiaTests")
  public CohortDefinition getBaciloscopiaTests() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Smear microscopy only");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "baciloscopia",
        EptsReportUtils.map(
            this.findDiagnostiTests(DiagnosticTestTypes.BACILOSCOPIA), generalParameterMapping));

    definition.addSearch(
        "genexperts", EptsReportUtils.map(this.getGenExpertTests(), generalParameterMapping));

    definition.setCompositionString("baciloscopia NOT genexperts");
    return definition;
  }

  @DocumentedDefinition(value = "getBaciloscopiaPositiveTestResults")
  public CohortDefinition getBaciloscopiaPositiveTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Smear microscopy only Positive Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "baciloscopia",
        EptsReportUtils.map(
            this.findDiagnosticPositiveTestResults(DiagnosticTestTypes.BACILOSCOPIA),
            generalParameterMapping));

    definition.addSearch(
        "genexperts",
        EptsReportUtils.map(this.getGenExpertPositiveTestResults(), generalParameterMapping));

    definition.setCompositionString("baciloscopia NOT genexperts");
    return definition;
  }

  @DocumentedDefinition(value = "getBaciloscopiaNegativeTestResults")
  public CohortDefinition getBaciloscopiaNegativeTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Smear microscopy only Negative Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "baciloscopia", EptsReportUtils.map(this.getBaciloscopiaTests(), generalParameterMapping));

    definition.addSearch(
        "positiveBaciloscopia",
        EptsReportUtils.map(this.getBaciloscopiaPositiveTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeGenexpert",
        EptsReportUtils.map(this.getGenExpertNegativeTestResults(), generalParameterMapping));

    definition.setCompositionString("baciloscopia NOT (positiveBaciloscopia OR negativeGenexpert)");
    return definition;
  }

  @DocumentedDefinition(value = "TBLAM")
  public CohortDefinition getTBLAMTests() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB LAM");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "tblam",
        EptsReportUtils.map(
            this.findDiagnostiTests(DiagnosticTestTypes.TBLAM), generalParameterMapping));

    definition.addSearch(
        "genexperts", EptsReportUtils.map(this.getGenExpertTests(), generalParameterMapping));
    definition.addSearch(
        "baciloscopia", EptsReportUtils.map(this.getBaciloscopiaTests(), generalParameterMapping));

    definition.setCompositionString("tblam NOT (genexperts OR baciloscopia)");
    return definition;
  }

  @DocumentedDefinition(value = "getTBLAMPositiveTestResults")
  public CohortDefinition getTBLAMPositiveTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB LAM Positive Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "tblam",
        EptsReportUtils.map(
            this.findDiagnosticPositiveTestResults(DiagnosticTestTypes.TBLAM),
            generalParameterMapping));

    definition.addSearch(
        "genexperts",
        EptsReportUtils.map(this.getGenExpertPositiveTestResults(), generalParameterMapping));
    definition.addSearch(
        "baciloscopia",
        EptsReportUtils.map(this.getBaciloscopiaPositiveTestResults(), generalParameterMapping));

    definition.setCompositionString("tblam NOT (genexperts OR baciloscopia)");
    return definition;
  }

  @DocumentedDefinition(value = "getTBLAMNegativeTestResults")
  public CohortDefinition getTBLAMNegativeTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB LAM Negative Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "tblam", EptsReportUtils.map(this.getTBLAMTests(), generalParameterMapping));

    definition.addSearch(
        "positiveTBLAM",
        EptsReportUtils.map(this.getTBLAMPositiveTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeGenexpert",
        EptsReportUtils.map(this.getGenExpertNegativeTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeBaciloscopia",
        EptsReportUtils.map(this.getBaciloscopiaNegativeTestResults(), generalParameterMapping));

    definition.setCompositionString(
        "tblam NOT (positiveTBLAM OR negativeGenexpert OR negativeBaciloscopia)");
    return definition;
  }

  @DocumentedDefinition(value = "AdditionalTests")
  public CohortDefinition getAdditionalTests() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Additional test other than GeneXpert");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "additionalTests",
        EptsReportUtils.map(
            this.findDiagnostiTests(DiagnosticTestTypes.CULTURA), generalParameterMapping));

    definition.addSearch(
        "genexperts", EptsReportUtils.map(this.getGenExpertTests(), generalParameterMapping));
    definition.addSearch(
        "baciloscopia", EptsReportUtils.map(this.getBaciloscopiaTests(), generalParameterMapping));
    definition.addSearch(
        "tblam", EptsReportUtils.map(this.getTBLAMTests(), generalParameterMapping));

    definition.setCompositionString("additionalTests NOT (genexperts OR baciloscopia OR tblam)");
    return definition;
  }

  @DocumentedDefinition(value = "getAdditionalPositiveTestResults")
  public CohortDefinition getAdditionalPositiveTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Additional test other than GeneXpert Positive Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "additionalTests",
        EptsReportUtils.map(
            this.findDiagnosticPositiveTestResults(DiagnosticTestTypes.CULTURA),
            generalParameterMapping));

    definition.addSearch(
        "genexperts",
        EptsReportUtils.map(this.getGenExpertPositiveTestResults(), generalParameterMapping));
    definition.addSearch(
        "baciloscopia",
        EptsReportUtils.map(this.getBaciloscopiaPositiveTestResults(), generalParameterMapping));
    definition.addSearch(
        "tblam", EptsReportUtils.map(this.getTBLAMPositiveTestResults(), generalParameterMapping));

    definition.setCompositionString("additionalTests NOT (genexperts OR baciloscopia OR tblam)");
    return definition;
  }

  @DocumentedDefinition(value = "getAdditionalNegativeTestResults")
  public CohortDefinition getAdditionalNegativeTestResults() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TB LAM Negative Test Results");
    this.addGeneralParameters(definition);
    definition.addSearch(
        "additionalTests", EptsReportUtils.map(this.getAdditionalTests(), generalParameterMapping));

    definition.addSearch(
        "positiveAdditional",
        EptsReportUtils.map(this.getAdditionalPositiveTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeTBLAM",
        EptsReportUtils.map(this.getTBLAMNegativeTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeGenexpert",
        EptsReportUtils.map(this.getGenExpertNegativeTestResults(), generalParameterMapping));

    definition.addSearch(
        "negativeBaciloscopia",
        EptsReportUtils.map(this.getBaciloscopiaNegativeTestResults(), generalParameterMapping));

    definition.setCompositionString(
        "additionalTests NOT (positiveAdditional OR negativeTBLAM OR negativeGenexpert OR negativeBaciloscopia)");
    return definition;
  }

  @DocumentedDefinition(value = "txTbNumerator")
  public CohortDefinition txTbNumerator() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - txTbNumerator");
    final CohortDefinition A = this.txTbNumeratorA();
    cd.addSearch("A", EptsReportUtils.map(A, this.generalParameterMapping));

    cd.addSearch(
        "started-tb-treatment-previous-period",
        EptsReportUtils.map(
            txtbCohortQueries.getTbDrugTreatmentStartDateWithinReportingDate(),
            "startDate=${endDate-12m},endDate=${endDate-6m-1d},location=${location}"));

    cd.addSearch(
        "A-PREVIOUS-PERIOD",
        EptsReportUtils.map(getNumeratorPreviosPeriod(), this.generalParameterMapping));

    cd.addSearch(
        "art-started-by-end-previous-reporting-period",
        EptsReportUtils.map(
            this.genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${endDate-6m-1d},location=${location}"));

    cd.setCompositionString(
        "A NOT (started-tb-treatment-previous-period OR (A-PREVIOUS-PERIOD AND art-started-by-end-previous-reporting-period ))");

    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "txTbNumeratorA")
  private CohortDefinition txTbNumeratorA() {
    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TxTB - txTbNumeratorA");
    final CohortDefinition i =
        this.genericCohortQueries.generalSql(
            "onTbTreatment",
            TXTBQueries.dateObs(
                this.tbMetadata.getTBDrugTreatmentStartDate().getConceptId(),
                Arrays.asList(
                    this.hivMetadata.getAdultoSeguimentoEncounterType().getId(),
                    this.hivMetadata.getARVPediatriaSeguimentoEncounterType().getId()),
                true));
    final CohortDefinition ii = txtbCohortQueries.getInTBProgram();
    this.addGeneralParameters(i);
    cd.addSearch("i", EptsReportUtils.map(i, generalParameterMapping));
    cd.addSearch("ii", EptsReportUtils.map(ii, generalParameterMapping));
    cd.addSearch(
        "iii",
        EptsReportUtils.map(
            txtbCohortQueries.getPulmonaryTBWithinReportingDate(), generalParameterMapping));
    cd.addSearch(
        "iv",
        EptsReportUtils.map(
            txtbCohortQueries.getTuberculosisTreatmentPlanWithinReportingDate(),
            generalParameterMapping));

    final CohortDefinition artList = txtbCohortQueries.artList();
    cd.addSearch("artList", EptsReportUtils.map(artList, generalParameterMapping));
    cd.setCompositionString("(i OR ii OR iii OR iv) AND artList");
    this.addGeneralParameters(cd);
    return cd;
  }

  @DocumentedDefinition(value = "findDiagnostiTests")
  private CohortDefinition findDiagnostiTests(DiagnosticTestTypes diagnosticTestType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findDiagnostiTests");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.setQuery(
        TXTBMontlyCascadeReportQueries.QUERY.findDiagnosticTests(diagnosticTestType));

    return definition;
  }

  @DocumentedDefinition(value = "findDiagnosticPositiveTestResults")
  private CohortDefinition findDiagnosticPositiveTestResults(
      DiagnosticTestTypes diagnosticTestType) {
    final SqlCohortDefinition definition = new SqlCohortDefinition();

    definition.setName("findDiagnosticPositiveTestResults");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.setQuery(
        TXTBMontlyCascadeReportQueries.QUERY.findDiagnosticTestsWithPositiveTestResults(
            diagnosticTestType));

    return definition;
  }

  @DocumentedDefinition(value = "PatientsWithPositiveResultWhoStartedTBTreatment")
  public CohortDefinition getPositiveResultAndTXTBNumerator() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB -Denominator Positive Results Who Started TB Treatment");
    this.addGeneralParameters(definition);

    definition.addSearch(
        "posetiveResults",
        EptsReportUtils.map(this.getPositiveResultCohortDefinition(), generalParameterMapping));
    definition.addSearch(
        "txtbNumerator", EptsReportUtils.map(this.txTbNumerator(), generalParameterMapping));

    definition.setCompositionString("posetiveResults AND txtbNumerator");
    return definition;
  }

  @DocumentedDefinition(value = "screenedPatientsWhoStartedTBTreatment")
  public CohortDefinition getScreenedPatientsWhoStartedTBTreatment() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - Screened Patients Who Started TB Treatment");
    this.addGeneralParameters(definition);

    definition.addSearch(
        "txtbDenominator", EptsReportUtils.map(this.getTxTBDenominator(), generalParameterMapping));
    definition.addSearch(
        "txtbNumerator", EptsReportUtils.map(this.txTbNumerator(), generalParameterMapping));

    definition.setCompositionString("txtbDenominator AND txtbNumerator");
    return definition;
  }

  @DocumentedDefinition(value = "ScreenedPatientsWhoStartedTBTreatmentAndTXCurr")
  public CohortDefinition getScreenedPatientsWhoStartedTBTreatmentAndTXCurr() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("TxTB - Screened Patients Who Started TB Treatment and Are TX_CURR");
    this.addGeneralParameters(definition);

    definition.addSearch(
        "txTB",
        EptsReportUtils.map(
            this.getScreenedPatientsWhoStartedTBTreatment(), generalParameterMapping));
    definition.addSearch(
        "txCurr",
        EptsReportUtils.map(
            this.txCurrCohortQueries.findPatientsWhoAreActiveOnART(),
            "endDate=${endDate},location=${location}"));

    definition.setCompositionString("txTB AND txCurr");
    return definition;
  }

  @DocumentedDefinition(value = "get Positive Results")
  public CohortDefinition getPositiveResultCohortDefinition() {

    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    this.addGeneralParameters(cd);
    cd.setName("TxTB -Denominator Positive Results");

    final CohortDefinition tbPositiveResultInFichaClinica =
        this.genericCohortQueries.generalSql(
            "tbPositiveResultReturned",
            TXTBQueries.dateObsForEncounterAndQuestionAndAnswers(
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.tbMetadata.getTbGenexpertTest().getConceptId(),
                    this.tbMetadata.getCultureTest().getConceptId(),
                    this.tbMetadata.getTbLam().getConceptId(),
                    this.tbMetadata.getSputumForAcidFastBacilli().getConceptId()),
                Arrays.asList(this.tbMetadata.getPositiveConcept().getConceptId())));

    final CohortDefinition tbPositiveResultsInFichaLaboratorio =
        this.genericCohortQueries.generalSql(
            "baciloscopiaResult",
            TXTBQueries.dateObsForEncounterAndQuestionAndAnswers(
                this.hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.tbMetadata.getSputumForAcidFastBacilli().getConceptId(),
                    this.tbMetadata.getTbGenexpertTest().getConceptId(),
                    this.tbMetadata.getCultureTest().getConceptId(),
                    this.tbMetadata.getTbLam().getConceptId()),
                Arrays.asList(this.tbMetadata.getPositiveConcept().getConceptId())));

    this.addGeneralParameters(tbPositiveResultInFichaClinica);
    this.addGeneralParameters(tbPositiveResultsInFichaLaboratorio);

    cd.addSearch(
        "tb-positive-result-ficha-clinica",
        EptsReportUtils.map(tbPositiveResultInFichaClinica, this.generalParameterMapping));
    cd.addSearch(
        "tb-positive-result-laboratorio",
        EptsReportUtils.map(tbPositiveResultsInFichaLaboratorio, this.generalParameterMapping));
    cd.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(this.getTxTBDenominator(), this.generalParameterMapping));

    cd.setCompositionString(
        "(tb-positive-result-ficha-clinica OR tb-positive-result-laboratorio) AND DENOMINATOR");

    return cd;
  }

  @DocumentedDefinition(value = "get Negative Results")
  public CohortDefinition getNegativeResultCohortDefinition() {

    final CompositionCohortDefinition cd = new CompositionCohortDefinition();
    this.addGeneralParameters(cd);
    cd.setName("TxTB -Denominator Negative Results");

    final CohortDefinition tbNegativeResultInFichaClinica =
        this.genericCohortQueries.generalSql(
            "tbPositiveResultReturned",
            TXTBQueries.dateObsForEncounterAndQuestionAndAnswers(
                this.hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.tbMetadata.getTbGenexpertTest().getConceptId(),
                    this.tbMetadata.getCultureTest().getConceptId(),
                    this.tbMetadata.getTbLam().getConceptId(),
                    this.tbMetadata.getSputumForAcidFastBacilli().getConceptId(),
                    this.tbMetadata.getXpertMtb().getConceptId()),
                Arrays.asList(this.tbMetadata.getNegativeConcept().getConceptId())));

    final CohortDefinition tbNegativeResultsInFichaLaboratorio =
        this.genericCohortQueries.generalSql(
            "baciloscopiaResult",
            TXTBQueries.dateObsForEncounterAndQuestionAndAnswers(
                this.hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId(),
                Arrays.asList(
                    this.tbMetadata.getSputumForAcidFastBacilli().getConceptId(),
                    this.tbMetadata.getTbGenexpertTest().getConceptId(),
                    this.tbMetadata.getCultureTest().getConceptId(),
                    this.tbMetadata.getTbLam().getConceptId()),
                Arrays.asList(
                    this.tbMetadata.getNegativeConcept().getConceptId(),
                    this.hivMetadata.getNoConcept().getConceptId())));

    this.addGeneralParameters(tbNegativeResultInFichaClinica);
    this.addGeneralParameters(tbNegativeResultsInFichaLaboratorio);

    cd.addSearch(
        "tb-negative-result-ficha-clinica",
        EptsReportUtils.map(tbNegativeResultInFichaClinica, this.generalParameterMapping));
    cd.addSearch(
        "tb-negative-result-laboratorio",
        EptsReportUtils.map(tbNegativeResultsInFichaLaboratorio, this.generalParameterMapping));
    cd.addSearch(
        "DENOMINATOR",
        EptsReportUtils.map(this.getTxTBDenominator(), this.generalParameterMapping));

    cd.addSearch(
        "positive-results",
        EptsReportUtils.map(
            this.getPositiveResultCohortDefinition(), this.generalParameterMapping));

    cd.setCompositionString(
        "((tb-negative-result-ficha-clinica OR tb-negative-result-laboratorio) AND DENOMINATOR ) NOT positive-results ");

    return cd;
  }

  private void addGeneralParameters(final CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }
}
