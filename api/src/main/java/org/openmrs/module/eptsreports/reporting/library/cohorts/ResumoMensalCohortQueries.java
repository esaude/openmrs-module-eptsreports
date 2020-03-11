/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.map;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.resumomensal.ResumoMensalINHCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.resumomensal.ResumoMensalTBCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.resumomensal.ResumoMensalTbExclusionCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.BaseFghCalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.ResumoMensalQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.TxNewQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalCohortQueries {

  private HivMetadata hivMetadata;
  private TbMetadata tbMetadata;
  private GenericCohortQueries genericCohortQueries;
  @Autowired private TxNewCohortQueries txNewCohortQueries;

  private static int TRASFERED_FROM_STATE_PRE_TARV = 28;
  private static int TRASFERED_FROM_STATE_TARV = 29;

  private static int PRE_TARV_CONCEPT = 6275;
  private static int TARV_CONCEPT = 6276;
  private static int ENROLMENT_DATE_CONCEPT = 23891;

  @Autowired
  public ResumoMensalCohortQueries(
      HivMetadata hivMetadata, TbMetadata tbMetadata, GenericCohortQueries genericCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.setTbMetadata(tbMetadata);
    this.genericCohortQueries = genericCohortQueries;
  }

  /** A1 Number of patients who initiated Pre-TARV at this HF by end of previous month */
  public CohortDefinition getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("NumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.setName("getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "PRETARV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1",
                ResumoMensalQueries.getAllPatientsWithPreArtStartDateLessThanReportingStartDateA1(
                    hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                    hivMetadata.getPreArtStartDate().getConceptId(),
                    hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getHIVCareProgram().getId())),
            mappings));

    definition.addSearch(
        "TRASFERED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1",
                ResumoMensalQueries
                    .getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
                        hivMetadata.getHIVCareProgram().getId(),
                        hivMetadata.getARTProgram().getId(),
                        TRASFERED_FROM_STATE_PRE_TARV,
                        TRASFERED_FROM_STATE_TARV,
                        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                        hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                        PRE_TARV_CONCEPT,
                        TARV_CONCEPT,
                        hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                        hivMetadata.getPatientFoundYesConcept().getConceptId(),
                        ENROLMENT_DATE_CONCEPT)),
            mappings));

    definition.setCompositionString("PRETARV NOT TRASFERED");

    return definition;
  }

  /**
   * A2 Number of patients who initiated Pre-TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("NumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA2");
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.setName("patientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    definition.addSearch(
        "PRETARV",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2",
                ResumoMensalQueries.getAllPatientsWithPreArtStartDateWithBoundariesA2(
                    hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                    hivMetadata.getPreArtStartDate().getConceptId(),
                    hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
                    hivMetadata.getHIVCareProgram().getId())),
            mappings));

    definition.addSearch(
        "TRASFERED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "patientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2",
                ResumoMensalQueries
                    .getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentStartDateEndDate(
                        hivMetadata.getHIVCareProgram().getId(),
                        hivMetadata.getARTProgram().getId(),
                        TRASFERED_FROM_STATE_PRE_TARV,
                        TRASFERED_FROM_STATE_TARV,
                        hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
                        hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(),
                        PRE_TARV_CONCEPT,
                        TARV_CONCEPT,
                        hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
                        hivMetadata.getPatientFoundYesConcept().getConceptId(),
                        ENROLMENT_DATE_CONCEPT)),
            mappings));

    definition.setCompositionString("PRETARV NOT TRASFERED");

    return definition;
  }

  /**
   * A3 = A.1 + A.2
   *
   * @return CohortDefinition
   */
  public CohortDefinition getSumOfA1AndA2() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.setName("Sum of A1 and A2");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "A1", map(getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(), mappings));
    cd.addSearch(
        "A2", map(getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), mappings));
    cd.setCompositionString("A1 OR A2");
    return cd;
  }

  // Start of the B queries
  /**
   * B1 Number of patientes who initiated TARV at this HF during the current month
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.setName("Number of patientes who initiated TARV at this HF End Date");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "B1",
        map(
            txNewCohortQueries.getTxNewCompositionCohort("Number of patientes who initiated TARV"),
            mappings));
    cd.setCompositionString("B1");
    return cd;
  }

  /**
   * B.2: Number of patients transferred-in from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition
      getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("Number Of Patients Transferred In From Other Health Facilities");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "TRANSFERED-IN-1",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD-1",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                TxNewQueries.QUERY
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-2",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriodStartDateB2",
                ResumoMensalQueries
                    .findPatientsWithAProgramStateMarkedAsTransferedInInAPeriodStartDateB2),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD-2",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardStartDateB2",
                ResumoMensalQueries
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCardStartDateB2),
            mappings));

    definition.addSearch(
        "TRANSFERED-OUT",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARnOutAPeriodOnMasterCardStartDateB2",
                ResumoMensalQueries.findPatientsWhoWhereMarkedAsTransferedOutAPeriodB2),
            mappings));

    definition.setCompositionString(
        "(TRANSFERED-IN-1 OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD-1) NOT((TRANSFERED-IN-2 OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD-2) NOT(TRANSFERED-OUT))");

    return definition;
  }

  @DocumentedDefinition(value = "B3")
  public CohortDefinition getSumPatientsB3() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.setName("Sum B9");

    definition.addSearch(
        "B13", EptsReportUtils.map(findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch("B9", EptsReportUtils.map(getSumPatientsB9(), mappings));

    definition.addSearch(
        "B12",
        EptsReportUtils.map(
            this.findPatientsWhoAreCurrentlyEnrolledOnArtMOHLastMonthB12(), mappings));

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(), mappings));

    definition.addSearch(
        "B2",
        EptsReportUtils.map(
            getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2(),
            mappings));

    definition.setCompositionString("(B13 OR B9) NOT (B12 OR B1 OR B2)");

    return definition;
  }

  /**
   * B.5: Number of patients transferred-out from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "B5")
  public CohortDefinition
      getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientsPregnantEnrolledOnART");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityB5();
    definition.setQuery(query);

    return definition;
  }

  /**
   * B.5: Number of patients transferred-out from another HFs during the current month
   *
   * @return Cohort
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "B6")
  public CohortDefinition getPatientsWhoSuspendTratmentB6() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Suspend Tratment B6");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.getPatientsWhoSuspendTratmentB6();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "B7")
  public CohortDefinition getPatientsWhoAbandonedTratmentUpB7() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("patients who abandoned tratment B7");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "ABANDONED",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "ABANDONED", ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7()),
            mappings));

    definition.addSearch(
        "EXCLUSION1",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "EXCLUSION1", ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7Exclusion()),
            mappings));

    definition.addSearch(
        "EXCLUSION2",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "EXCLUSION2",
                ResumoMensalQueries
                    .getPatientsWhoSuspendAndDiedAndTransferedOutTratmentB7ExclusionEndDate()),
            mappings));

    definition.setCompositionString("ABANDONED NOT (EXCLUSION1 OR EXCLUSION2)");

    return definition;
  }

  @DocumentedDefinition(value = "B8")
  public CohortDefinition getPatientsWhoDiedTratmentB8() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Patient Died B8");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.getPatientsWhoDiedTratmentB8();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "B9")
  public CohortDefinition getSumPatientsB9() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.setName("Sum B9");

    definition.addSearch(
        "B5",
        EptsReportUtils.map(
            getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(),
            mappings));

    definition.addSearch("B6", EptsReportUtils.map(getPatientsWhoSuspendTratmentB6(), mappings));

    definition.addSearch(
        "B7", EptsReportUtils.map(this.getPatientsWhoAbandonedTratmentUpB7(), mappings));

    definition.addSearch("B8", EptsReportUtils.map(getPatientsWhoDiedTratmentB8(), mappings));

    definition.setCompositionString("B5 OR B6 OR B7 OR B8 ");

    return definition;
  }

  @DocumentedDefinition(value = "B10")
  public CohortDefinition getTxNewEndDateB10() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx New End Date");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate-1d},location=${location}";

    definition.addSearch(
        "START-ART",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoAreNewlyEnrolledOnART",
                ResumoMensalQueries.findPatientsWhoAreNewlyEnrolledOnARTB10),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
                ResumoMensalQueries.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod),
            mappings));

    definition.addSearch(
        "TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
                ResumoMensalQueries
                    .findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
            mappings));

    definition.setCompositionString(
        "START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

    return definition;
  }

  @DocumentedDefinition(value = "B11")
  public CohortDefinition getSumPatients11() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    definition.addSearch(
        "B1",
        EptsReportUtils.map(
            getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(), mappings));

    definition.addSearch("B10", EptsReportUtils.map(getTxNewEndDateB10(), mappings));

    definition.setCompositionString("B1 OR B10");

    return definition;
  }

  @DocumentedDefinition(value = "B12")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHLastMonthB12() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Tx Curr B12");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHLastMonthB12();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "B13")
  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13() {

    final SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("Tx Curr B13");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "Location", Location.class));

    String query = ResumoMensalQueries.findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13();
    definition.setQuery(query);

    return definition;
  }

  @DocumentedDefinition(value = "C1")
  public CohortDefinition findPatientWhoHaveTbSymthomsC1() {

    final CompositionCohortDefinition definition = new CompositionCohortDefinition();
    definition.setName("C1");

    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "A2",
        EptsReportUtils.map(
            getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), mappings));

    definition.addSearch(
        "C1",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "C1", ResumoMensalQueries.findPatientWhoHaveTbSymthomsC1()),
            mappings));

    definition.setCompositionString("A2 AND C1");

    return definition;
  }

  @DocumentedDefinition(value = "C2")
  public CohortDefinition getPatientsWhoMarkedINHC2() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "C2", Context.getRegisteredComponents(ResumoMensalINHCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "C3")
  private CohortDefinition getPatientsWhoMarkedTbActiveC3() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "C3", Context.getRegisteredComponents(ResumoMensalTBCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  public CohortDefinition getPatientsWhoMarkedINHC2A2() {

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("INH");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A2",
        EptsReportUtils.map(
            this.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), mapping));

    cd.addSearch("C2", EptsReportUtils.map(this.getPatientsWhoMarkedINHC2(), mapping));

    cd.setCompositionString("A2 AND C2");
    return cd;
  }

  public CohortDefinition getPatientsWhoMarkedTbActiveC3A2() {

    String mapping = "startDate=${startDate},endDate=${endDate},location=${location}";
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("TB Active");

    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "A2",
        EptsReportUtils.map(
            this.getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(), mapping));

    cd.addSearch("C3", EptsReportUtils.map(this.getPatientsWhoMarkedTbActiveC3(), mapping));

    cd.setCompositionString("A2 AND C3");
    return cd;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithRequestForVLE1() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx Curr E1");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "B13", EptsReportUtils.map(findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatietWithRequestForVL(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                    hivMetadata.getHivViralLoadConcept().getConceptId())),
            mappings));

    definition.addSearch(
        "E1x",
        map(
            genericCohortQueries.generalSql(
                "E1x",
                ResumoMensalQueries.getE1ExclusionCriteria(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getApplicationForLaboratoryResearch().getConceptId(),
                    hivMetadata.getHivViralLoadConcept().getConceptId())),
            mappings));

    definition.setCompositionString("(B13 AND VL) NOT E1x");

    return definition;
  }

  public CohortDefinition findPatientsWhoAreCurrentlyEnrolledOnArtMOHWithVLResultE2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "B13", EptsReportUtils.map(findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResult(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappings));

    definition.addSearch(
        "Ex2",
        map(
            genericCohortQueries.generalSql(
                "Ex2",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappings));

    definition.setCompositionString("(B13 AND VL) NOT Ex2");

    return definition;
  }

  public CohortDefinition findPatientWithVlResulLessThan1000E3() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "B13", EptsReportUtils.map(findPatientsWhoAreCurrentlyEnrolledOnArtMOHB13(), mappings));

    definition.addSearch(
        "VL",
        EptsReportUtils.map(
            this.genericCohortQueries.generalSql(
                "VL",
                ResumoMensalQueries.findPatientWithVlResulLessThan1000(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappings));

    definition.addSearch(
        "Ex3",
        map(
            genericCohortQueries.generalSql(
                "Ex3",
                ResumoMensalQueries.getE2ExclusionCriteria(
                    hivMetadata.getHivViralLoadConcept().getConceptId(),
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
                    hivMetadata.getHivViralLoadQualitative().getConceptId())),
            mappings));

    definition.setCompositionString("(B13 AND VL) NOT Ex3");

    return definition;
  }

  @DocumentedDefinition(value = "F3")
  public CohortDefinition findPatientWhoHaveTbSymthomsAndTbActive() {

    BaseFghCalculationCohortDefinition cd =
        new BaseFghCalculationCohortDefinition(
            "F3", Context.getRegisteredComponents(ResumoMensalTbExclusionCalculation.class).get(0));
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "end Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    return cd;
  }

  @DocumentedDefinition(value = "F3")
  public CohortDefinition getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("F1: Number of patients who had clinical appointment during the reporting month");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setQuery(
        ResumoMensalQueries.getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId()));
    return cd;
  }

  @DocumentedDefinition(value = "F1TB")
  public CohortDefinition
      getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthTbF2() {
    final CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.setName("Tx Curr Vl");
    definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
    definition.addParameter(new Parameter("endDate", "End Date", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    definition.addSearch(
        "F1",
        EptsReportUtils.map(
            getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1(), mappings));

    definition.addSearch("TB1", map(findPatientWhoHaveTbSymthomsAndTbActive(), mappings));

    definition.addSearch(
        "TB2",
        map(
            genericCohortQueries.generalSql(
                "TB2", ResumoMensalQueries.findPatientWhoHaveTbActiveF2()),
            mappings));

    definition.setCompositionString("F1 AND (TB1 OR TB2)");

    return definition;
  }

  /**
   * F3: Number of patients who had at least one clinical appointment during the year
   *
   * @return CohortDefinition
   */
  public CohortDefinition getNumberOfPatientsWithAtLeastOneClinicalAppointmentDuringTheYearF3() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Number of patients who had at least one clinical appointment during the year");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

    cd.addSearch(
        "F3",
        map(getNumberOfPatientsWhoHadClinicalAppointmentDuringTheReportingMonthF1(), mappings));

    cd.addSearch(
        "Fx3",
        map(
            genericCohortQueries.generalSql(
                "Fx3",
                ResumoMensalQueries.getF3Exclusion(
                    hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId())),
            mappings));

    cd.addSearch(
        "Fx4",
        map(
            genericCohortQueries.generalSql(
                "Fx4", ResumoMensalQueries.getF3ExclusionTransferedIn()),
            mappings));

    cd.setCompositionString("F3 NOT(Fx3 OR Fx4)");
    return cd;
  }

  public TbMetadata getTbMetadata() {
    return tbMetadata;
  }

  public void setTbMetadata(TbMetadata tbMetadata) {
    this.tbMetadata = tbMetadata;
  }
}
