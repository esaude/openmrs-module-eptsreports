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
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
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
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;

	private static int TRASFERED_FROM_STATE = 28;
	private static int PRE_TARV_CONCEPT = 6275;

	@Autowired
	public ResumoMensalCohortQueries(HivMetadata hivMetadata, TbMetadata tbMetadata,
			GenericCohortQueries genericCohortQueries) {
		this.hivMetadata = hivMetadata;
		this.setTbMetadata(tbMetadata);
		this.genericCohortQueries = genericCohortQueries;
	}

	/**
	 * A1 Number of patients who initiated Pre-TARV at this HF by end of previous
	 * month
	 */
	public CohortDefinition getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1() {

		final CompositionCohortDefinition definition = new CompositionCohortDefinition();
		definition.setName("NumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1");
		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		definition.setName("getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1");
		definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		definition.addParameter(new Parameter("endDate", "End Date", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));

		definition.addSearch("PRETARV",
				EptsReportUtils.map(this.genericCohortQueries.generalSql(
						"getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1",
						ResumoMensalQueries.getAllPatientsWithPreArtStartDateLessThanReportingStartDate(
								hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
								hivMetadata.getPreArtStartDate().getConceptId(),
								hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
								hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
								hivMetadata.getHIVCareProgram().getId())),
						mappings));

		definition.addSearch("TRASFERED",
				EptsReportUtils.map(this.genericCohortQueries.generalSql(
						"getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1",
						ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentMonth(
								hivMetadata.getHIVCareProgram().getId(), TRASFERED_FROM_STATE,
								hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
								hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(), PRE_TARV_CONCEPT,
								hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
								hivMetadata.getPatientFoundYesConcept().getConceptId())),
						mappings));

		definition.setCompositionString("PRETARV NOT TRASFERED");

		return definition;
	}

	/**
	 * A2 Number of patients who initiated Pre-TARV at this HF during the current
	 * month
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

		definition.addSearch("PRETARV",
				EptsReportUtils.map(this.genericCohortQueries.generalSql(
						"patientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2",
						ResumoMensalQueries.getAllPatientsWithPreArtStartDateWithBoundaries(
								hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
								hivMetadata.getPreArtStartDate().getConceptId(),
								hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId(),
								hivMetadata.getARVPediatriaInitialEncounterType().getEncounterTypeId(),
								hivMetadata.getHIVCareProgram().getId())),
						mappings));

		definition.addSearch("TRASFERED", EptsReportUtils.map(this.genericCohortQueries.generalSql(
				"patientsTransferredFromAnotherHealthFacilityDuringTheCurrentStartDateEndDate",
				ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityDuringTheCurrentStartDateEndDate(
						hivMetadata.getHIVCareProgram().getId(), TRASFERED_FROM_STATE,
						hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
						hivMetadata.getTypeOfPatientTransferredFrom().getConceptId(), PRE_TARV_CONCEPT,
						hivMetadata.getTransferFromOtherFacilityConcept().getConceptId(),
						hivMetadata.getPatientFoundYesConcept().getConceptId())),
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
		cd.setName("Sum of A1 and A2");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("A1", map(getNumberOfPatientsWhoInitiatedPreTarvByEndOfPreviousMonthA1(),
				"startDate=${startDate},location=${location}"));
		cd.addSearch("A2", map(getPatientsWhoInitiatedPreTarvAtAfacilityDuringCurrentMonthA2(),
				"startDate=${startDate},endDate=${endDate},location=${location}"));
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

		cd.addSearch("B1",
				map(txNewCohortQueries.getTxNewCompositionCohort("Number of patientes who initiated TARV"), mappings));
		cd.setCompositionString("B1");
		return cd;
	}

	/**
	 * B.2: Number of patients transferred-in from another HFs during the current
	 * month
	 *
	 * @return Cohort
	 * @return CohortDefinition
	 */
	public CohortDefinition getNumberOfPatientsTransferredInFromOtherHealthFacilitiesDuringCurrentMonthB2() {

		final CompositionCohortDefinition definition = new CompositionCohortDefinition();

		definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		definition.addParameter(new Parameter("endDate", "End Date", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));

		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		definition.addSearch("TRANSFERED-IN", EptsReportUtils
				.map(this.genericCohortQueries.generalSql("findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
						TxNewQueries.QUERY.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod), mappings));

		definition.addSearch("TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
				EptsReportUtils.map(this.genericCohortQueries.generalSql(
						"findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
						TxNewQueries.QUERY.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
						mappings));

		definition.setCompositionString("TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD");

		return definition;
	}

	/**
	 * B.5: Number of patients transferred-out from another HFs during the current
	 * month
	 *
	 * @return Cohort
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "B5")
	public CohortDefinition getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5() {

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
	 * B.5: Number of patients transferred-out from another HFs during the current
	 * month
	 *
	 * @return Cohort
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "B6")
	public CohortDefinition getPatientsWhoSuspendTratmentB6() {

		final SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("Suspend RT");
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

		definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		definition.addParameter(new Parameter("endDate", "End Date", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));

		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		final String mappingsExclusion = "startDate=${startDate-1d},location=${location}";

		definition.addSearch("ABANDONED", EptsReportUtils.map(this.genericCohortQueries.generalSql("ABANDONED",
				ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7()), mappings));

		definition
				.addSearch("EXCLUSION",
						EptsReportUtils.map(
								this.genericCohortQueries.generalSql("EXCLUSION",
										ResumoMensalQueries.getPatientsWhoAbandonedTratmentB7Exclusion()),
								mappingsExclusion));

		definition.addSearch("SUSPEND", EptsReportUtils.map(
				this.genericCohortQueries.generalSql("SUSPEND", ResumoMensalQueries.getPatientsWhoSuspendTratmentB6()),
				mappings));

		definition
				.addSearch("TRANSFERED",
						EptsReportUtils.map(
								this.genericCohortQueries.generalSql("TRANSFERED",
										ResumoMensalQueries.getPatientsTransferredFromAnotherHealthFacilityB5()),
								mappings));

		definition.addSearch("DIED", EptsReportUtils.map(
				this.genericCohortQueries.generalSql("DIED", ResumoMensalQueries.getPatientsWhoDiedTratmentB8()),
				mappings));

		definition.setCompositionString("ABANDONED NOT (EXCLUSION OR SUSPEND OR TRANSFERED OR DIED)");

		return definition;
	}

	@DocumentedDefinition(value = "B8")
	public CohortDefinition getPatientsWhoDiedTratmentB8() {

		final SqlCohortDefinition definition = new SqlCohortDefinition();
		definition.setName("Suspend RT");
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

		definition.addSearch("B5", EptsReportUtils
				.map(getNumberOfPatientsTransferredOutFromOtherHealthFacilitiesDuringCurrentMonthB5(), mappings));

		definition.addSearch("B6", EptsReportUtils.map(getPatientsWhoSuspendTratmentB6(), mappings));

		definition.addSearch("B7", EptsReportUtils.map(this.getPatientsWhoAbandonedTratmentUpB7(), mappings));

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

		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		definition.addSearch("START-ART",
				EptsReportUtils.map(this.genericCohortQueries.generalSql("findPatientsWhoAreNewlyEnrolledOnART",
						ResumoMensalQueries.findPatientsWhoAreNewlyEnrolledOnART), mappings));

		definition.addSearch("TRANSFERED-IN", EptsReportUtils
				.map(this.genericCohortQueries.generalSql("findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod",
						ResumoMensalQueries.findPatientsWithAProgramStateMarkedAsTransferedInInAPeriod), mappings));

		definition.addSearch("TRANSFERED-IN-AND-IN-ART-MASTER-CARD",
				EptsReportUtils.map(this.genericCohortQueries.generalSql(
						"findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard",
						ResumoMensalQueries.findPatientsWhoWhereMarkedAsTransferedInAndOnARTOnInAPeriodOnMasterCard),
						mappings));

		definition.setCompositionString("START-ART NOT (TRANSFERED-IN OR TRANSFERED-IN-AND-IN-ART-MASTER-CARD)");

		return definition;
	}

	@DocumentedDefinition(value = "B11")
	public CohortDefinition getSumPatients11() {

		final CompositionCohortDefinition definition = new CompositionCohortDefinition();

		definition.addParameter(new Parameter("startDate", "Start Date", Date.class));
		definition.addParameter(new Parameter("endDate", "End Date", Date.class));
		definition.addParameter(new Parameter("location", "location", Location.class));

		final String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";

		definition.addSearch("B1",
				EptsReportUtils.map(getPatientsWhoInitiatedTarvAtThisFacilityDuringCurrentMonthB1(), mappings));

		definition.addSearch("B10", EptsReportUtils.map(getTxNewEndDateB10(), mappings));

		definition.setCompositionString("B1 OR B10");

		return definition;
	}

	public TbMetadata getTbMetadata() {
		return tbMetadata;
	}

	public void setTbMetadata(TbMetadata tbMetadata) {
		this.tbMetadata = tbMetadata;
	}
}
