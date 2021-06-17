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
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri2MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.EriDSDCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.HivCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TbPrevCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxCurrCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InverseCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {

  @Autowired private GenderCohortQueries genderCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private Eri4MonthsCohortQueries eri4MonthsCohortQueries;

  @Autowired private Eri2MonthsCohortQueries eri2MonthsCohortQueries;

  @Autowired private EriCohortQueries eriCohortQueries;

  @Autowired private TbPrevCohortQueries tbPrevCohortQueries;

  @Autowired private HivCohortQueries hivCohortQueries;

  @Autowired private TxPvlsCohortQueries txPvlsQueries;

  @Autowired private TxCurrCohortQueries txCurrCohortQueries;

  @Autowired private EriDSDCohortQueries eriDSDCohortQueries;

  /**
   * Gender dimension
   *
   * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension gender() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("gender");
    dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.maleCohort(), ""));
    dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.femaleCohort(), ""));
    return dim;
  }

  /**
   * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
   *
   * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension age(AgeDimensionCohortInterface ageDimensionCohort) {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setParameters(ageDimensionCohort.getParameters());
    dim.setName("age dimension");

    dim.addCohortDefinition("UK", ageDimensionCohort.createUnknownAgeCohort());
    dim.addCohortDefinition(
        "<1", ageDimensionCohort.createXtoYAgeCohort("patients with age below 1", 0, 0));
    dim.addCohortDefinition(
        "<2", ageDimensionCohort.createXtoYAgeCohort("patients with age below 2 years", 0, 1));
    dim.addCohortDefinition(
        "0-4",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 4 years", 0, 4));
    dim.addCohortDefinition(
        "0-14",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 14 years", 0, 14));
    dim.addCohortDefinition(
        "0-15",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 0 and 15 years", 0, 15));
    dim.addCohortDefinition(
        "1-4", ageDimensionCohort.createXtoYAgeCohort("patients with age between 1 and 4", 1, 4));
    dim.addCohortDefinition(
        "2-4", ageDimensionCohort.createXtoYAgeCohort("patients with age between 1 and 4", 2, 4));
    dim.addCohortDefinition(
        "5-9", ageDimensionCohort.createXtoYAgeCohort("patients with age between 5 and 9", 5, 9));
    dim.addCohortDefinition(
        "<15", ageDimensionCohort.createXtoYAgeCohort("patients with age below 15", null, 14));
    dim.addCohortDefinition(
        "14+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 14", 14, null));
    dim.addCohortDefinition(
        "10-14",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 10 and 14", 10, 14));
    dim.addCohortDefinition(
        "15+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 15", 15, null));
    dim.addCohortDefinition(
        "15-19",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 15 and 19", 15, 19));
    dim.addCohortDefinition(
        "20-24",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 20 and 24", 20, 24));
    dim.addCohortDefinition(
        "25-29",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 25 and 29", 25, 29));
    dim.addCohortDefinition(
        "30-34",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 30 and 34", 30, 34));
    dim.addCohortDefinition(
        "35-39",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 35 and 39", 35, 39));
    dim.addCohortDefinition(
        "40-44",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 40 and 44", 40, 44));
    dim.addCohortDefinition(
        "45-49",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 45 and 49", 45, 49));
    dim.addCohortDefinition(
        "50+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 50", 50, null));

    dim.addCohortDefinition(
        "20+", ageDimensionCohort.createXtoYAgeCohort("patients with age over 20 years", 20, null));

    dim.addCohortDefinition(
        "10-19",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 10 and 19", 10, 19));
    dim.addCohortDefinition(
        "2-14",
        ageDimensionCohort.createXtoYAgeCohort("patients with age between 2 and 14", 2, 14));
    dim.addCohortDefinition(
        "2-9", ageDimensionCohort.createXtoYAgeCohort("patients with age between 2 and 9", 2, 9));
    return dim;
  }

  /** @return CohortDefinitionDimension */
  public CohortDefinitionDimension maternityDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Maternity Dimension");

    dim.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(false),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(false),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri4MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingStartDate", "Report Start Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Report End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patient states");

    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "AIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "LTFU",
        EptsReportUtils.map(
            eri4MonthsCohortQueries
                .getPatientsLostToFollowUpAndNotDeadTransferredOrStoppedTreatment(),
            "onOrBefore=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWhoStoppedTreatment(),
            "onOrBefore=${reportingEndDate},location=${location}"));

    dim.addCohortDefinition(
        "ANIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndNotOnTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri2MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patients dimensions for Eri2Months");
    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eriCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DNPUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoDidNotPickDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "PUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoPickedUpDrugsOnTheirSecondVisit(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            genericCohortQueries.getDeceasedPatients(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsTransferredOut(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWhoStoppedTreatment(),
            "onOrBefore=${reportingEndDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension getArtStatusDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("ART-status Dimension");
    dim.addCohortDefinition(
        "new-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getNewOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "previously-on-art",
        EptsReportUtils.map(
            tbPrevCohortQueries.getPreviouslyOnArt(),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    return dim;
  }

  /**
   * <b>Description</b> Disaggregation for Key population
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getKeyPopsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("Key Population Dimension");
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition drugUserKeyPopCohort = hivCohortQueries.getDrugUserKeyPopCohort();
    CohortDefinition homosexualKeyPopCohort = hivCohortQueries.getMaleHomosexualKeyPopDefinition();
    CohortDefinition imprisonmentKeyPopCohort = hivCohortQueries.getImprisonmentKeyPopCohort();
    CohortDefinition sexWorkerKeyPopCohort =
        hivCohortQueries.getFemaleSexWorkersKeyPopCohortDefinition();
    dim.addCohortDefinition("PID", mapStraightThrough(drugUserKeyPopCohort));
    dim.addCohortDefinition("MSM", mapStraightThrough(homosexualKeyPopCohort));
    dim.addCohortDefinition("CSW", mapStraightThrough(sexWorkerKeyPopCohort));
    dim.addCohortDefinition("PRI", mapStraightThrough(imprisonmentKeyPopCohort));
    return dim;
  }

  public CohortDefinitionDimension getViralLoadRoutineTargetReasonsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("endDate", "onOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition routineViralLoadCohort = txPvlsQueries.getPatientsWhoAreOnRoutine();
    CohortDefinition targetedViralLoadCohort = txPvlsQueries.getPatientsWhoAreOnTarget();
    dim.addCohortDefinition("VLR", mapStraightThrough(routineViralLoadCohort));
    dim.addCohortDefinition("VLT", mapStraightThrough(targetedViralLoadCohort));
    return dim;
  }

  public CohortDefinitionDimension getDispensingQuantityDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("ARV Dispensing quantity dimension");
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "orOrBefore", Date.class));
    dim.addParameter(new Parameter("locationList", "Location", Location.class));
    CohortDefinition less3m = txCurrCohortQueries.monthlyDispensationComposition();
    CohortDefinition threeTo5m = txCurrCohortQueries.quarterlyDispensationComposition();
    CohortDefinition more6m = txCurrCohortQueries.semiAnnualDispensationComposition();
    dim.addCohortDefinition(
        "<3m", EptsReportUtils.map(less3m, "onOrBefore=${onOrBefore},location=${locationList}"));
    dim.addCohortDefinition(
        "3-5m",
        EptsReportUtils.map(threeTo5m, "onOrBefore=${onOrBefore},location=${locationList}"));
    dim.addCohortDefinition(
        ">6m", EptsReportUtils.map(more6m, "onOrBefore=${onOrBefore},location=${locationList}"));
    return dim;
  }

  /** Dimension for DSD eligible and not eligible patients */
  public CohortDefinitionDimension getDSDEligibleDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("DSD Eligible dimension");
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition eligible = eriDSDCohortQueries.getD1();
    CohortDefinition notEligible = eriDSDCohortQueries.getD2();

    dim.addCohortDefinition("E", mapStraightThrough(eligible));
    dim.addCohortDefinition("NE", mapStraightThrough(notEligible));
    return dim;
  }

  /** Dimension for DSD Non-Pregnant, Non-Breastfeeding and Not on TB treatment */
  public CohortDefinitionDimension getDSDNonPregnantNonBreastfeedingAndNotOnTbDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("DSD Non-Pregnant, Non-Breastfeeding and Non-Tb dimension");
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    CohortDefinition pregnantBreastfeedingTb =
        eriDSDCohortQueries.getPregnantAndBreastfeedingAndOnTBTreatment();
    CohortDefinition inverse = new InverseCohortDefinition(pregnantBreastfeedingTb);
    dim.addCohortDefinition("NPNBNTB", mapStraightThrough(inverse));
    return dim;
  }

  /** Dimension for Age in months */
  public CohortDefinitionDimension ageInMonths() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("Patients having age in months");
    dim.addParameter(new Parameter("effectiveDate", "End Date", Date.class));
    dim.addCohortDefinition(
        "9m-",
        EptsReportUtils.map(
            genericCohortQueries.getAgeInMonths(0, 9), "effectiveDate=${effectiveDate}"));
    return dim;
  }
  /**
   * Dimension for returning patients age based on their ART start date
   *
   * @return @{@link CohortDefinitionDimension}
   */
  public CohortDefinitionDimension ageBasedOnArtStartDateMOH() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    dim.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Patients having age based on ART start date by reporting end date MOH");
    dim.addCohortDefinition(
        "adultsArt",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(15, null, false),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "childrenArt",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(null, 14, false),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "2-14",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(2, 14, false),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));
    dim.addCohortDefinition(
        "<2",
        EptsReportUtils.map(
            genericCohortQueries.getAgeOnMOHArtStartDate(null, 1, false),
            "onOrAfter=${onOrAfter},onOrBefore=${onOrBefore},location=${location}"));

    return dim;
  }

  /**
   * Dimension for returning patients age based on the first viral load date
   *
   * @return @{@link CohortDefinitionDimension}
   */
  public CohortDefinitionDimension getPatientAgeBasedOnFirstViralLoadDate() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "startDate", Date.class));
    dim.addParameter(new Parameter("endDate", "endDate", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Patients having age based on first viral load date by reporting end date MOH");
    dim.addCohortDefinition(
        "MqAdults",
        EptsReportUtils.map(
            genericCohortQueries.getPatientAgeBasedOnFirstViralLoadDate(15, 200),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "MqChildren",
        EptsReportUtils.map(
            genericCohortQueries.getPatientAgeBasedOnFirstViralLoadDate(0, 14),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }
}
