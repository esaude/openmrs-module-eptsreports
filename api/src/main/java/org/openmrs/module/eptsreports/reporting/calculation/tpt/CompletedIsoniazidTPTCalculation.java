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
package org.openmrs.module.eptsreports.reporting.calculation.tpt;

import java.util.*;
import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.*;
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
public class CompletedIsoniazidTPTCalculation extends AbstractPatientCalculation {

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
    CalculationResultMap patientMap = new CalculationResultMap();
    Location location = (Location) context.getFromCache("location");

    Date onOrBefore = (Date) context.getFromCache(ON_OR_BEFORE);

    if (onOrBefore != null) {

      Concept c23985 = tbMetadata.getRegimeTPTConcept();
      EncounterType e60 = tbMetadata.getRegimeTPTEncounterType();
      EncounterType e6 = hivMetadata.getAdultoSeguimentoEncounterType();
      EncounterType e9 = hivMetadata.getPediatriaSeguimentoEncounterType();
      EncounterType e53 = hivMetadata.getMasterCardEncounterType();
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
      Concept c6129 = hivMetadata.getDataFinalizacaoProfilaxiaIsoniazidaConcept();

      /** ----- all patients who started IPT ---- */
      // A1
      CalculationResultMap startProfilaxiaObservation53 =
          ePTSCalculationService.getObs(
              hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
              e53,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
              context);

      // A2
      CalculationResultMap startDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              e6,
              cohort,
              location,
              Arrays.asList(hivMetadata.getStartDrugs()),
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // A3
      CalculationResultMap startProfilaxiaObservation6 =
          ePTSCalculationService.getObs(
              hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
              e6,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // A4
      CalculationResultMap startProfilaxiaObservation9 =
          ePTSCalculationService.getObs(
              hivMetadata.getDataInicioProfilaxiaIsoniazidaConcept(),
              e9,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // A5 - FILT - Patients who have Regime de TPT with the values marked on the
      // first pick-up date
      CalculationResultMap regimeTPT1stPickUpMap =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c656, c23982),
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      /** ------ who completed IPT treatment--- */
      // B1
      CalculationResultMap endProfilaxiaObservations53 =
          ePTSCalculationService.getObs(
              c6129,
              e53,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
              context);

      // B2
      CalculationResultMap completedDrugsObservations =
          ePTSCalculationService.getObs(
              hivMetadata.getIsoniazidUsageConcept(),
              e6,
              cohort,
              location,
              Arrays.asList(hivMetadata.getCompletedConcept()),
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // B3
      CalculationResultMap endProfilaxiaObservations6 =
          ePTSCalculationService.getObs(
              c6129,
              e6,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
              context);

      // B4
      CalculationResultMap endProfilaxiaObservations9 =
          ePTSCalculationService.getObs(
              c6129,
              e9,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // INH start or Continue
      CalculationResultMap isoniazidStartContinueMap =
          ePTSCalculationService.getObs(
              c6122,
              e6,
              cohort,
              location,
              Arrays.asList(c1256, c1257),
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // Other Perscriptions INH
      CalculationResultMap outrasPrescricoesINHMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23955),
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // Other Perscriptions
      CalculationResultMap outrasPrescricoesExcludeINHMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              null,
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // Type of Dispensation - Monthly (DM)
      CalculationResultMap filtTypeOfDispensationMonthlyMap =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c1098),
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // Type of Dispensation - Trimestral (DT)
      CalculationResultMap filtTypeOfDispensationTrimestralMap =
          ePTSCalculationService.getObs(
              c23986,
              e60,
              cohort,
              location,
              Arrays.asList(c23720),
              TimeQualifier.ANY,
              null,
              null,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      /** START and END 3HP Treatment */

      // C1 and D part 1

      CalculationResultMap start3HPMap =
          ePTSCalculationService.getObs(
              c1719,
              e6,
              cohort,
              location,
              Arrays.asList(c23954),
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      // C2
      CalculationResultMap regimeTPTStart3HPMap =
          ePTSCalculationService.getObs(
              c23985,
              e60,
              cohort,
              location,
              Arrays.asList(c23954, c23984),
              TimeQualifier.ANY,
              null,
              onOrBefore,
              EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
              context);

      for (Integer patientId : cohort) {

        // ipt start date section

        List<Obs> startProfilaxiaObs53 =
            getObsListFromResultMap(startProfilaxiaObservation53, patientId);

        List<Obs> startProfilaxiaObs6 =
            getObsListFromResultMap(startProfilaxiaObservation6, patientId);

        List<Obs> startProfilaxiaObs9 =
            getObsListFromResultMap(startProfilaxiaObservation9, patientId);

        List<Obs> startDrugsObs = getObsListFromResultMap(startDrugsObservations, patientId);

        List<Obs> startTPTFilt = getObsListFromResultMap(regimeTPT1stPickUpMap, patientId);

        List<Obs> outrasPrescricoesCleanObsList =
            getObsListFromResultMap(outrasPrescricoesExcludeINHMap, patientId);

        List<Obs> outrasPrescricoesINHObsList =
            getObsListFromResultMap(outrasPrescricoesINHMap, patientId);

        // ipt end date section
        List<Obs> endProfilaxiaObs6 =
            getObsListFromResultMap(endProfilaxiaObservations6, patientId);
        List<Obs> endProfilaxiaObs53 =
            getObsListFromResultMap(endProfilaxiaObservations53, patientId);
        List<Obs> endProfilaxiaObs9 =
            getObsListFromResultMap(endProfilaxiaObservations9, patientId);
        List<Obs> endDrugsObs = getObsListFromResultMap(completedDrugsObservations, patientId);

        if (startProfilaxiaObs53 == null
            && startProfilaxiaObs6 == null
            && startProfilaxiaObs9 == null
            && startDrugsObs == null) {
          continue;
        }

        // A1 AND (B1 OR B2 OR B3 OR B4)
        for (Obs a1 : startProfilaxiaObs53) {
          if (a1 != null) {
            Obs b1 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs53, a1.getValueDatetime(), 173, 365, false);

            Obs b2 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endDrugsObs, a1.getValueDatetime(), 173, 365, true);

            Obs b3 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs6, a1.getValueDatetime(), 173, 365, false);

            Obs b4 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs9, a1.getValueDatetime(), 173, 365, true);

            if (b1 != null || b2 != null || b3 != null || b4 != null) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        for (Obs a2 : startDrugsObs) {
          if (a2 != null) {
            Obs b1 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs53, a2.getEncounter().getEncounterDatetime(), 173, 365, false);

            Obs b2 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endDrugsObs, a2.getEncounter().getEncounterDatetime(), 173, 365, true);

            Obs b3 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs6, a2.getEncounter().getEncounterDatetime(), 173, 365, false);

            Obs b4 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs9, a2.getEncounter().getEncounterDatetime(), 173, 365, true);

            // B5 is a obs list made of
            CalculationResultMap b5Map = new CalculationResultMap();

            List<Obs> cleanList =
                this.excludeObs(outrasPrescricoesCleanObsList, outrasPrescricoesINHObsList);

            List<Obs> obsICList = getObsListFromResultMap(isoniazidStartContinueMap, patientId);

            int evaluateINHOccurrences6 =
                evaluateOccurrence(
                    obsICList, cleanList, a2.getEncounter().getEncounterDatetime(), 6, 7);

            int evaluateINHOccurences2 =
                evaluateOccurrence(
                    obsICList,
                    outrasPrescricoesINHObsList,
                    a2.getEncounter().getEncounterDatetime(),
                    2,
                    5);

            int evaluateINHOccurrences3 =
                evaluateOccurrence2(
                    obsICList,
                    outrasPrescricoesINHObsList,
                    a2.getEncounter().getEncounterDatetime(),
                    3,
                    7);

            if (evaluateINHOccurrences6 >= 6
                || evaluateINHOccurences2 >= 2
                || evaluateINHOccurrences3 >= 3) {
              b5Map.put(patientId, new BooleanResult(true, this));
            }

            if (b1 != null || b2 != null || b3 != null || b4 != null || !b5Map.isEmpty()) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        for (Obs a3 : startProfilaxiaObs6) {
          if (a3 != null) {
            Obs b1 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs53, a3.getValueDatetime(), 173, 365, false);

            Obs b2 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endDrugsObs, a3.getValueDatetime(), 173, 365, true);

            Obs b3 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs6, a3.getValueDatetime(), 173, 365, false);

            Obs b4 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs9, a3.getValueDatetime(), 173, 365, true);

            // B5 is a obs list made of
            CalculationResultMap b5Map = new CalculationResultMap();

            List<Obs> cleanList =
                this.excludeObs(outrasPrescricoesCleanObsList, outrasPrescricoesINHObsList);

            List<Obs> obsICList = getObsListFromResultMap(isoniazidStartContinueMap, patientId);

            int evaluateINHOccurrences6 =
                evaluateOccurrence(obsICList, cleanList, a3.getValueDatetime(), 6, 7);

            int evaluateINHOccurences2 =
                evaluateOccurrence(
                    obsICList, outrasPrescricoesINHObsList, a3.getValueDatetime(), 2, 5);

            int evaluateINHOccurrences3 =
                evaluateOccurrence2(
                    obsICList, outrasPrescricoesINHObsList, a3.getValueDatetime(), 3, 7);

            if (evaluateINHOccurrences6 >= 6
                || evaluateINHOccurences2 >= 2
                || evaluateINHOccurrences3 >= 3) {
              b5Map.put(patientId, new BooleanResult(true, this));
            }

            if (b1 != null || b2 != null || b3 != null || b4 != null || !b5Map.isEmpty()) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        for (Obs a4 : startProfilaxiaObs9) {
          if (a4 != null) {
            Obs b1 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs53, a4.getValueDatetime(), 173, 365, false);

            Obs b2 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endDrugsObs, a4.getValueDatetime(), 173, 365, true);

            Obs b3 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs6, a4.getValueDatetime(), 173, 365, false);

            Obs b4 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs9, a4.getValueDatetime(), 173, 365, true);

            if (b1 != null || b2 != null || b3 != null || b4 != null) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        for (Obs a5 : startTPTFilt) {
          if (a5 != null) {
            Obs b1 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs53, a5.getEncounter().getEncounterDatetime(), 173, 365, false);

            Obs b2 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endDrugsObs, a5.getEncounter().getEncounterDatetime(), 173, 365, true);

            Obs b3 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs6, a5.getEncounter().getEncounterDatetime(), 173, 365, false);

            Obs b4 =
                returnObsBetweenIptStartDateAndIptEndDate(
                    endProfilaxiaObs9, a5.getEncounter().getEncounterDatetime(), 173, 365, true);

            // B6 is a obs list made of
            CalculationResultMap b6Map = new CalculationResultMap();

            int evaluateRegimeTPTOccurrences6 =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPT1stPickUpMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationMonthlyMap, patientId),
                    a5.getEncounter().getEncounterDatetime(),
                    6,
                    7);

            int evaluateRegimeTPTOccurrences2 =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPT1stPickUpMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationTrimestralMap, patientId),
                    a5.getEncounter().getEncounterDatetime(),
                    2,
                    7);

            int evaluateRegimeTPTOccurrences3DM =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPT1stPickUpMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationMonthlyMap, patientId),
                    a5.getEncounter().getEncounterDatetime(),
                    3,
                    7);

            int evaluateRegimeTPTOccurrences3DT =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPT1stPickUpMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationTrimestralMap, patientId),
                    a5.getEncounter().getEncounterDatetime(),
                    1,
                    7);

            if (evaluateRegimeTPTOccurrences6 >= 6
                || evaluateRegimeTPTOccurrences2 >= 2
                || (evaluateRegimeTPTOccurrences3DM >= 3 && evaluateRegimeTPTOccurrences3DT >= 1)) {
              b6Map.put(patientId, new BooleanResult(true, this));
            }

            if (b1 != null || b2 != null || b3 != null || b4 != null || !b6Map.isEmpty()) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        /* 3HP */
        Obs start3HPObs = EptsCalculationUtils.obsResultForPatient(start3HPMap, patientId);

        Obs regimeTPT3HPObs =
            EptsCalculationUtils.obsResultForPatient(regimeTPTStart3HPMap, patientId);

        // for each startDate 3HP obs

        for (Obs c1 : Arrays.asList(start3HPObs)) {
          if (c1 != null) {
            int atLeastThree3HPOccurrence =
                evaluateOccurrenceSingleList(
                    getObsListFromResultMap(start3HPMap, patientId),
                    c1.getEncounter().getEncounterDatetime(),
                    3,
                    4);

            if (atLeastThree3HPOccurrence >= 3) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }

        for (Obs c2 : Arrays.asList(regimeTPT3HPObs)) {
          if (c2 != null) {
            int atLeast1FILT3HPTrimestralsOccurence =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPTStart3HPMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationTrimestralMap, patientId),
                    c2.getEncounter().getEncounterDatetime(),
                    1,
                    4);

            int atleast3FILTS3HPTrimestralOccurencies =
                evaluateOccurrence(
                    getObsListFromResultMap(regimeTPTStart3HPMap, patientId),
                    getObsListFromResultMap(filtTypeOfDispensationTrimestralMap, patientId),
                    c2.getEncounter().getEncounterDatetime(),
                    3,
                    4);

            if (atLeast1FILT3HPTrimestralsOccurence >= 1
                || atleast3FILTS3HPTrimestralOccurencies >= 3) {
              patientMap.put(patientId, new BooleanResult(true, this));
            }
          }
        }
      }

      return patientMap;

    } else {
      throw new IllegalArgumentException(
          String.format("Parameters %s and %s must be set", ON_OR_BEFORE));
    }
  }

  private List<Obs> getObsListFromResultMap(CalculationResultMap map, Integer pid) {
    List<Obs> obss = new ArrayList<>();
    if (map.get(pid) instanceof ListResult) {

      ListResult listResult = (ListResult) map.get(pid);
      obss = EptsCalculationUtils.extractResultValues(listResult);
      obss.removeAll(Collections.singleton(null));
    }

    return obss;
  }

  /**
   * Given 1 list of obs the method extracts the encounter list , and then check for each encounter
   * in the list evaluates if it reocurred ntimes during period.
   *
   * @param obss
   * @param iptStartDate
   * @param nTimes
   * @param plusIPTDate
   * @return
   */
  private int evaluateOccurrenceSingleList(
      List<Obs> obss, Date iptStartDate, int nTimes, int plusIPTDate) {
    int num = 0;
    for (Obs o : obss) {

      if (o.getEncounter()
                  .getEncounterDatetime()
                  .compareTo(DateUtils.addMonths(iptStartDate, plusIPTDate))
              <= 0
          && o.getEncounter().getEncounterDatetime().compareTo(iptStartDate) >= 0) {
        num++;
        if (num == nTimes) {
          break;
        }
      }
    }
    return num;
  }

  /**
   * Given 2 list of obs the method extracts the encounter list from both, and then check for each
   * encounter in the fisrt list evaluates if it is present in the second list It uses the
   * implementation of contains method of ArrayList class which behind the schene it check if the
   * two encounters share the uuid.
   *
   * @param a list of OBs
   * @param b
   * @param iptStartDate
   * @param nTimes
   * @param plusIPTDate
   * @return
   */
  private int evaluateOccurrence(
      List<Obs> a, List<Obs> b, Date iptStartDate, int nTimes, int plusIPTDate) {
    List<Encounter> encountersA = new ArrayList<>();
    List<Encounter> encountersB = new ArrayList<>();

    for (Obs obs : a) {
      encountersA.add(obs.getEncounter());
    }
    for (Obs obs : b) {
      encountersB.add(obs.getEncounter());
    }
    int num = 0;

    for (Encounter e : encountersA) {
      if (e.getEncounterDatetime().compareTo(DateUtils.addMonths(iptStartDate, plusIPTDate)) <= 0
          && e.getEncounterDatetime().compareTo(iptStartDate) >= 0
          && encountersB.contains(e)) {
        num++;
        if (num == nTimes) {
          break;
        }
      }
    }
    return num;
  }

  /**
   * Given 2 list of obs the method extracts the encounter list from both, and then check for each
   * encounter in the fisrt list evaluates if AT LEAST ONE ENCOUNTER is present in the second list
   *
   * @param a list of OBs
   * @param b
   * @param iptStartDate
   * @param nTimes
   * @param plusIPTDate
   * @return
   */
  private int evaluateOccurrence2(
      List<Obs> a, List<Obs> b, Date iptStartDate, int nTimes, int plusIPTDate) {
    List<Encounter> encountersA = new ArrayList<>();
    List<Encounter> encountersB = new ArrayList<>();

    for (Obs obs : a) {
      encountersA.add(obs.getEncounter());
    }
    for (Obs obs : b) {
      encountersB.add(obs.getEncounter());
    }
    int num = 0;

    for (Encounter e : encountersA) {
      if (e.getEncounterDatetime().compareTo(DateUtils.addMonths(iptStartDate, plusIPTDate)) <= 0
          && e.getEncounterDatetime().compareTo(iptStartDate) >= 0) {
        num++;
      }
    }

    for (Encounter e : encountersA) {
      if (encountersB.contains(e) && num == nTimes) {
        break;
      }
    }

    return num;
  }

  /**
   * Given iptStartDate evaluate if Obs is between IPT startDate plus some number of days and IPT
   * and some number of days
   *
   * @param a list of OBs
   * @param iptStartDate
   * @param plusIPTStartDate
   * @param plusIPTEndDate
   * @return
   */
  private Obs returnObsBetweenIptStartDateAndIptEndDate(
      List<Obs> a,
      Date iptStartDate,
      int plusIPTStartDate,
      int plusIPTEndDate,
      boolean encounterDate) {

    Obs returnedObs = null;

    if (iptStartDate == null) {
      return returnedObs;
    }

    Date startDate = DateUtils.addDays(iptStartDate, plusIPTStartDate);
    Date endDate = DateUtils.addDays(iptStartDate, plusIPTEndDate);

    for (Obs obs : a) {
      if (obs != null) {
        if (encounterDate) {
          if (obs.getEncounter() != null) {
            if (obs.getEncounter().getEncounterDatetime().compareTo(startDate) >= 0
                && obs.getEncounter().getEncounterDatetime().compareTo(endDate) <= 0) {
              returnedObs = obs;
              break;
            }
          }
        } else {
          if (obs.getValueDatetime() != null) {
            if (obs.getValueDatetime().compareTo(startDate) >= 0
                && obs.getValueDatetime().compareTo(endDate) <= 0) {
              returnedObs = obs;
              break;
            }
          }
        }
      }
    }
    return returnedObs;
  }

  private List<Obs> excludeObs(List<Obs> pure, List<Obs> dirty) {
    List<Obs> clean = new ArrayList<>();
    for (Obs o : pure) {
      if (!dirty.contains(o)) {
        clean.add(o);
      }
    }
    return clean;
  }
}
