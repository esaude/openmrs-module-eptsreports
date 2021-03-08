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
package org.openmrs.module.eptsreports.reporting.calculation.prev;

import java.util.*;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.metadata.TbMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Patients that completed isoniazid prophylactic treatment */
@Component
public class CompletedIsoniazidProphylaticTreatmentCalculation extends AbstractPatientCalculation {

  private static final int COMPLETION_PERIOD_OFFSET = 0;

  private static final int TREATMENT_BEGIN_PERIOD_OFFSET = -6;

  private static final int NUMBER_ISONIAZID_USAGE_TO_CONSIDER_COMPLETED = 5;

  private static final int MONTHS_TO_CHECK_FOR_ISONIAZID_USAGE = 7;

  private static final int MINIMUM_DURATION_IN_DAYS = 173;

  private static final String ON_OR_AFTER = "onOrAfter";

  private static final String ON_OR_BEFORE = "onOrBefore";

  private enum Priority {
    MIN,
    MAX
  };

  @Autowired private HivMetadata hivMetadata;

  @Autowired private TbMetadata tbMetadata;

  @Autowired private EPTSCalculationService ePTSCalculationService;

  @SuppressWarnings("unused")
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");

    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);
    Date onOrAfter = (Date) context.getFromCache(ON_OR_AFTER);

    if (onOrAfter != null && onOrBefore != null) {
      Date beginPeriodStartDate =
          EptsCalculationUtils.addMonths(onOrAfter, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date beginPeriodEndDate =
          EptsCalculationUtils.addMonths(onOrBefore, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date completionPeriodStartDate =
          EptsCalculationUtils.addMonths(onOrAfter, TREATMENT_BEGIN_PERIOD_OFFSET);
      Date completionPeriodEndDate =
          EptsCalculationUtils.addMonths(onOrBefore, COMPLETION_PERIOD_OFFSET);

      final List<EncounterType> consultationEncounterTypes =
          Arrays.asList(
              hivMetadata.getAdultoSeguimentoEncounterType(),
              hivMetadata.getPediatriaSeguimentoEncounterType(),
              hivMetadata.getMasterCardEncounterType());

      Concept c23985 = tbMetadata.getRegimeTPTConcept();
      EncounterType e60 = tbMetadata.getRegimeTPTEncounterType();
      EncounterType e6 = hivMetadata.getAdultoSeguimentoEncounterType();
      Concept c656 = tbMetadata.getIsoniazidConcept();
      Concept c23982 = tbMetadata.getIsoniazidePiridoxinaConcept();
      Concept c1719 = tbMetadata.getTreatmentPrescribedConcept();
      Concept c23955 = tbMetadata.getDtINHConcept();
      Concept c23986 = tbMetadata.getTypeDispensationTPTConceptUuid();
      Concept c1098 = hivMetadata.getMonthlyConcept();
      Concept c6122 = hivMetadata.getIsoniazidUsageConcept();
      Concept c1256 = hivMetadata.getStartDrugs();
      Concept c1257 = hivMetadata.getContinueRegimenConcept();
      Concept c23720 = hivMetadata.getQuarterlyConcept();
      Concept c23954 = tbMetadata.get3HPConcept();
      Concept c23984 = tbMetadata.get3HPPiridoxinaConcept();

      /** ----- all patients who started IPT in the previous reporting period ---- */
      // I.
      CalculationResultMap startProfilaxiaObservations =
          ePTSCalculationService.firstObs(
              hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
              null,
              location,
              false,
              beginPeriodStartDate,
              beginPeriodEndDate,
              consultationEncounterTypes,
              cohort,
              context);
      // II
      CalculationResultMap startDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
              cohort,
              Arrays.asList(location),
              Arrays.asList(hivMetadata.getStartDrugs()),
              TimeQualifier.FIRST,
              beginPeriodStartDate,
              onOrAfter,
              context);
      // III. Patients who have Regime de TPT with the values marked on the first pick-up date
      // during the previous period

      CalculationResultMap regimeTPT1stPickUpPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656, c23982),
              TimeQualifier.FIRST,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      CalculationResultMap exclisionRegimeTPT1stPickUpPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656, c23982),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -13),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // IV. Patients who have Outras Prescrições with the values (DT-INH) */
      CalculationResultMap outrasPrescricoesPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.FIRST,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      CalculationResultMap exclusionOutrasPrescricoesPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -13),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      /** ------ who completed IPT treatment during the reporting period--- */
      // V
      CalculationResultMap endProfilaxiaObservations =
          ePTSCalculationService.lastObs(
              hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept(),
              null,
              location,
              false,
              null,
              completionPeriodEndDate,
              consultationEncounterTypes,
              cohort,
              context);
      // VI
      CalculationResultMap completedDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(hivMetadata.getAdultoSeguimentoEncounterType()),
              cohort,
              Arrays.asList(location),
              Arrays.asList(hivMetadata.getCompletedConcept()),
              TimeQualifier.LAST,
              null,
              completionPeriodEndDate,
              context);

      /** ------ with IPTStarteDate-IPTEndDate>=173days--- */
      // VI is omitted, actually is: vii.have at least 173 days apart between the IPT Start date
      // (the most oldest date from above criterias) and IPT End date (the most recent date from
      // above criterias).

      // VIII
      CalculationResultMap isoniazidUsageObservationsList =
          ePTSCalculationService.allObservations(
              hivMetadata.getIsoniazidUsageConcept(),
              Arrays.asList(hivMetadata.getYesConcept(), hivMetadata.getContinueRegimenConcept()),
              consultationEncounterTypes,
              location,
              cohort,
              context);
      // IX
      CalculationResultMap filtMap1 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap filtMap2 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c1098),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap filtMap3 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23982),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap filtMap4 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c1098),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // X
      CalculationResultMap dtINHMap1 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1256),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dtINHMap2 =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dtINHMap3 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1257),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dtINHMap4 =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // XI
      CalculationResultMap dfiltdtINHMap1 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dfiltdtINHMap2 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dfiltdtINHMap3 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23982),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap dfiltdtINHMap4 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // XII
      CalculationResultMap atLeast3FichaClínicaINHMap1 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1256),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FichaClínicaINHMap2 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1257),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FichaClínicaINHMap3 =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FichaClínicaINHMap4 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1256),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FichaClínicaINHMap5 =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1257),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FichaClínicaINHMap6 =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // XIII

      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap1 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap2 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap3 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23982),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap4 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap5 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap6 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c1098),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap7 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23982),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atLeast3FILTINHMensal1FILTDTINHMap8 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c1098),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, 6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      /** START and END 3HP Treatment */

      // I

      CalculationResultMap artListTbPrevList3HPPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.FIRST,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      CalculationResultMap exclucsionAartListTbPrevList3HPPreviousPeriodMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -12),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      // II
      CalculationResultMap regimeTPT3HPMap =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23954, c23984),
              TimeQualifier.FIRST,
              DateUtils.addMonths(onOrAfter, -6),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      CalculationResultMap exclusionregimeTPT3HPMap =
          ePTSCalculationService.getObs(
              c1719,
              e60,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      // III
      CalculationResultMap atleast1FILT3HPTrimestralMa1 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atleast1FILT3HPTrimestralMa2 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atleast1FILT3HPTrimestralMa3 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23984),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atleast1FILT3HPTrimestralMa4 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      // part 2 from  III
      CalculationResultMap atleast3FILTS3HPMensal1 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      CalculationResultMap atleast3FILTS3HPMensal2 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atleast3FILTS3HPMensal3 =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23984),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);
      CalculationResultMap atleast3FILTS3HPMensal4 =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // IV
      CalculationResultMap atLeast3FichaClínicaMastercard3HPUntil4MonthMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              DateUtils.addMonths(onOrAfter, -10),
              DateUtils.addMonths(onOrBefore, -6),
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      for (Integer patientId : cohort) {
        // ipt start date section
        Obs startProfilaxiaObs =
            EptsCalculationUtils.obsResultForPatient(startProfilaxiaObservations, patientId);

        Obs startDrugsObs =
            EptsCalculationUtils.obsResultForPatient(startDrugsObservations, patientId);

        Obs regimeTPT1stPickUpPreviousPeriod =
            EptsCalculationUtils.obsResultForPatient(
                regimeTPT1stPickUpPreviousPeriodMap, patientId);
        getObsListFromResultMap(exclisionRegimeTPT1stPickUpPreviousPeriodMap, patientId);
        List<Obs> exclisionRegimeTPT1stPickUpPreviousPeriod =
            getObsListFromResultMap(exclisionRegimeTPT1stPickUpPreviousPeriodMap, patientId);

        Obs outrasPrescricoesPreviousPeriod =
            EptsCalculationUtils.obsResultForPatient(outrasPrescricoesPreviousPeriodMap, patientId);
        List<Obs> exclusionOutrasPrescricoesPreviousPeriod =
            getObsListFromResultMap(exclusionOutrasPrescricoesPreviousPeriodMap, patientId);

        // ipt end date section
        Obs endProfilaxiaObs =
            EptsCalculationUtils.obsResultForPatient(endProfilaxiaObservations, patientId);
        Obs endDrugsObs =
            EptsCalculationUtils.obsResultForPatient(completedDrugsObservations, patientId);
        /*
         * If We can't find a startDate from Ficha de Seguimento (adults and children) / Ficha
         * Resumo or Ficha Clinica-MasterCard, we can't do the calculations. -Just move to the next
         * patient.
         */
        if (startProfilaxiaObs == null && startDrugsObs == null) {
          continue;
        }
        Date iptStartDate =
            getMinOrMaxObsDate(
                Arrays.asList(
                    startProfilaxiaObs,
                    startDrugsObs,
                    this.exclude(
                        regimeTPT1stPickUpPreviousPeriod,
                        exclusionOutrasPrescricoesPreviousPeriod,
                        -7),
                    this.exclude(
                        outrasPrescricoesPreviousPeriod,
                        exclusionOutrasPrescricoesPreviousPeriod,
                        -7)),
                Priority.MIN);

        Date iptEndDate =
            getMinOrMaxObsDate(Arrays.asList(endProfilaxiaObs, endDrugsObs), Priority.MAX);

        // viii
        int viii =
            calculateNumberOfYesAnswers(isoniazidUsageObservationsList, patientId, iptStartDate);

        // ix
        List<Obs> filts = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(filtMap1, patientId));
        filts.addAll(getObsListFromResultMap(filtMap1, patientId));
        filts.addAll(getObsListFromResultMap(filtMap1, patientId));
        filts.addAll(getObsListFromResultMap(filtMap1, patientId));

        int ix = evaluateOccurrence(filts, iptStartDate, 6, 7);

        // x
        List<Obs> dtINHs = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(dtINHMap1, patientId));
        filts.addAll(getObsListFromResultMap(dtINHMap2, patientId));
        filts.addAll(getObsListFromResultMap(dtINHMap3, patientId));
        filts.addAll(getObsListFromResultMap(dtINHMap4, patientId));

        int x = evaluateOccurrence(dtINHs, iptStartDate, 2, 5);

        // xi

        List<Obs> dfiltdtINHs = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(dfiltdtINHMap1, patientId));
        filts.addAll(getObsListFromResultMap(dfiltdtINHMap2, patientId));
        filts.addAll(getObsListFromResultMap(dfiltdtINHMap3, patientId));
        filts.addAll(getObsListFromResultMap(dfiltdtINHMap4, patientId));

        int xi = evaluateOccurrence(dfiltdtINHs, iptStartDate, 2, 5);

        // xii

        List<Obs> atLeast3FichaClínicaINHA = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap1, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap2, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap3, patientId));

        int xiia = evaluateOccurrence(atLeast3FichaClínicaINHA, iptStartDate, 3, 7);

        List<Obs> atLeast3FichaClínicaINHB = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap4, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap5, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FichaClínicaINHMap6, patientId));

        int xiib = evaluateOccurrence(atLeast3FichaClínicaINHB, iptStartDate, 1, 7);

        // xiii

        List<Obs> atLeast3FILTINHMensal1FILTDTINHA = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap1, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap2, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap3, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap4, patientId));

        int xiiia = evaluateOccurrence(atLeast3FILTINHMensal1FILTDTINHA, iptStartDate, 1, 7);

        List<Obs> atLeast3FILTINHMensal1FILTDTINHB = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap5, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap6, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap7, patientId));
        filts.addAll(getObsListFromResultMap(atLeast3FILTINHMensal1FILTDTINHMap8, patientId));

        int xiiib = evaluateOccurrence(atLeast3FILTINHMensal1FILTDTINHB, iptStartDate, 3, 7);

        if (getProfilaxiaDuration(iptStartDate, iptEndDate) >= MINIMUM_DURATION_IN_DAYS
            || viii >= NUMBER_ISONIAZID_USAGE_TO_CONSIDER_COMPLETED
                && iptStartDate != null
                && (viii > 0
                    || ix > 0
                    || x > 0
                    || xi > 0
                    || (xiia > 0 && xiib > 0)
                    || (xiiia > 0 && xiiib > 0))) {
          map.put(patientId, new BooleanResult(true, this));
        }

        /* 3HP */
        Obs artListTbPrevList3HPPreviousPeriod =
            EptsCalculationUtils.obsResultForPatient(
                artListTbPrevList3HPPreviousPeriodMap, patientId);
        Obs regimeTPT3HP = EptsCalculationUtils.obsResultForPatient(regimeTPT3HPMap, patientId);

        List<Obs> exclucsionAartListTbPrevList3HPPreviousPeriod =
            getObsListFromResultMap(exclucsionAartListTbPrevList3HPPreviousPeriodMap, patientId);
        List<Obs> exclusionregimeTPT3HP =
            getObsListFromResultMap(exclusionregimeTPT3HPMap, patientId);

        Date first3HPDate =
            getMinOrMaxObsDate(
                Arrays.asList(
                    this.exclude(
                        artListTbPrevList3HPPreviousPeriod,
                        exclucsionAartListTbPrevList3HPPreviousPeriod,
                        4),
                    this.exclude(regimeTPT3HP, exclusionregimeTPT3HP, 4)),
                Priority.MIN);

        List<Obs> atleast1FILT3HPTrimestrals = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atleast1FILT3HPTrimestralMa1, patientId));
        filts.addAll(getObsListFromResultMap(atleast1FILT3HPTrimestralMa2, patientId));
        filts.addAll(getObsListFromResultMap(atleast1FILT3HPTrimestralMa3, patientId));
        filts.addAll(getObsListFromResultMap(atleast1FILT3HPTrimestralMa4, patientId));

        int atleast1FILT3HPTrimestralsOccurencies =
            evaluateOccurrence(atleast1FILT3HPTrimestrals, first3HPDate, 1, 4);

        List<Obs> atleast3FILTS3HPMensalList = new ArrayList<>();
        filts.addAll(getObsListFromResultMap(atleast3FILTS3HPMensal1, patientId));
        filts.addAll(getObsListFromResultMap(atleast3FILTS3HPMensal2, patientId));
        filts.addAll(getObsListFromResultMap(atleast3FILTS3HPMensal3, patientId));
        filts.addAll(getObsListFromResultMap(atleast3FILTS3HPMensal4, patientId));

        int atleast3FILTS3HPMensalOccurencies =
            evaluateOccurrence(atleast3FILTS3HPMensalList, first3HPDate, 4, 4);

        List<Obs> atLeast3FichaClínicaMastercard3HPUntil4Month =
            getObsListFromResultMap(atLeast3FichaClínicaMastercard3HPUntil4MonthMap, patientId);

        int atleast3FILTS3HPMensalListMensalOccurencies =
            evaluateOccurrence(atleast3FILTS3HPMensalList, first3HPDate, 3, 4);

        if (first3HPDate != null
            && (atleast1FILT3HPTrimestralsOccurencies > 0
                || atleast3FILTS3HPMensalOccurencies > 0
                || atleast3FILTS3HPMensalListMensalOccurencies > 0)) {
          map.put(patientId, new BooleanResult(true, this));
        }
      }
      return map;
    } else {
      throw new IllegalArgumentException(
          String.format("Parameters %s and %s must be set", ON_OR_AFTER, ON_OR_BEFORE));
    }
  }

  private int calculateNumberOfYesAnswers(
      CalculationResultMap isoniazidUsageObservationsList, Integer patientId, Date startDate) {
    List<Obs> isoniazidUsageObservations =
        EptsCalculationUtils.extractResultValues(
            (ListResult) isoniazidUsageObservationsList.get(patientId));
    int count = 0;
    Date isoniazidUsageEndDate = getIsoniazidUsageEndDate(startDate);
    for (Obs obs : isoniazidUsageObservations) {
      Date date = obs.getObsDatetime();
      if (date.compareTo(startDate) > 0 && date.compareTo(isoniazidUsageEndDate) <= 0) {
        count++;
      }
    }
    return count;
  }

  private Date getIsoniazidUsageEndDate(Date startDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    calendar.add(Calendar.MONTH, MONTHS_TO_CHECK_FOR_ISONIAZID_USAGE);
    return calendar.getTime();
  }

  private Date getDateFromObs(Obs obs) {
    if (obs != null) {
      return obs.getValueDatetime();
    }
    return null;
  }
  /**
   * @param obss list of Obss
   * @param priority MIN to retrieve the start drugs date, MAX to retrieve the end drugs date
   * @return The earliest or most recent date according to the priority parameter
   */
  private Date getMinOrMaxObsDate(List<Obs> obss, Priority priority) {

    Date returedDate = null;

    List<Date> dates = new ArrayList<>();
    for (Obs o : obss) {
      if (o != null) dates.add(o.getEncounter().getEncounterDatetime());
    }
    if (!dates.isEmpty()) {
      if (priority == Priority.MAX) {
        returedDate = Collections.max(dates);
      }
      if (priority == Priority.MIN) {
        returedDate = Collections.min(dates);
      }
    }

    return returedDate;
  }
  /**
   * Calculate the interval in days between two dates
   *
   * @param startDate
   * @param endDate
   * @return the interval, 0 otherwise
   */
  private int getProfilaxiaDuration(Date startDate, Date endDate) {
    if (startDate != null && endDate != null && endDate.compareTo(startDate) > 0) {
      return Days.daysIn(new Interval(startDate.getTime(), endDate.getTime())).getDays();
    } else {
      return 0;
    }
  }

  private Obs exclude(Obs firstDate, List<Obs> bGroup, int month) {
    Obs obs = firstDate;
    if (firstDate != null) {
      if (month < 0) {
        for (Obs o : bGroup) {

          if (o.getEncounter()
                      .getEncounterDatetime()
                      .compareTo(
                          DateUtils.addMonths(
                              firstDate.getEncounter().getEncounterDatetime(), month))
                  >= 0
              && o.getEncounter()
                      .getEncounterDatetime()
                      .compareTo(firstDate.getEncounter().getEncounterDatetime())
                  < 0) {
            obs = null;
            break;
          }
        }
      }
      if (month > 0) {
        for (Obs o : bGroup) {

          if (o.getEncounter()
                      .getEncounterDatetime()
                      .compareTo(
                          DateUtils.addMonths(
                              firstDate.getEncounter().getEncounterDatetime(), month))
                  <= 0
              && o.getEncounter()
                      .getEncounterDatetime()
                      .compareTo(firstDate.getEncounter().getEncounterDatetime())
                  >= 0) {
            obs = null;
            break;
          }
        }
      }
    }
    return obs;
  }

  private List<Obs> getObsListFromResultMap(CalculationResultMap map, Integer pid) {
    ListResult listResult = (ListResult) map.get(pid);
    List<Obs> obss = EptsCalculationUtils.extractResultValues(listResult);
    obss.removeAll(Collections.singleton(null));
    return obss;
  }

  private int evaluateOccurrence(List<Obs> obss, Date iptStartDate, int nTimes, int plusIPTDate) {
    int num = 0;
    for (Obs o : obss) {

      if (o.getEncounter()
                  .getEncounterDatetime()
                  .compareTo(DateUtils.addMonths(iptStartDate, plusIPTDate))
              >= 0
          && o.getEncounter().getEncounterDatetime().compareTo(iptStartDate) < 0) {
        num++;
        if (num == nTimes) {
          break;
        }
      }
    }
    return num;
  }
}
