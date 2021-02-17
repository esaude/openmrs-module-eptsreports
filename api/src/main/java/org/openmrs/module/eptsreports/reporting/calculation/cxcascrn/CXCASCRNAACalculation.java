package org.openmrs.module.eptsreports.reporting.calculation.cxcascrn;

import java.util.*;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.springframework.stereotype.Component;

/**
 *
 *
 * <ul>
 *   <li>AA:
 *       <ul>
 *         <li>( A_FichaCCU: ( Select all patients with the first Ficha de Registo Para Rastreio do
 *             Cancro do Colo Uterino (encounter type 28) with the following conditions:
 *             <ul>
 *               <li>VIA RESULTS (concept id 2094) and value coded SUSPECTED CANCER, NEGATIVE or
 *                   POSITIVE (concept id IN [2093, 664, 703])
 *               <li>And encounter datetime >= startdate and <= enddate
 *               <li>Note1: if there is more than one record registered, consider the first one,
 *                   i.e., the earliest date during the reporting period
 *             </ul>
 *         <li>If Ficha de Registo para rastreio do CCU(encounter type 28) is not available during
 *             the period the system will consider the following sources:
 *             <ul>
 *               <li>A_FichaClinca: Select all patients with the ficha clinica (encounter type 6)
 *                   with the following conditions:
 *                   <ul>
 *                     <li>VIA RESULTS (concept id 2094) and value coded SUSPECTED CANCER, NEGATIVE
 *                         or POSITIVE (concept id IN [2093, 664, 703])
 *                     <li>And encounter datetime >= startdate and <= enddate
 *                   </ul>
 *               <li>A_FichaResumo: Select all patients on Ficha Resumo (encounter type 53) who have
 *                   the following conditions:
 *                   <ul>
 *                     <li>VIA RESULTS (concept id 2094) and value coded POSITIVE (concept id 703)
 *                     <li>And value datetime >= startdate and <= enddate )
 *                   </ul>
 *               <li>Note2: The system will consider the earliest VIA date during the reporting
 *                   period from the different sources listed above (from encounter type 6 and 53).
 *             </ul>
 *       </ul>
 * </ul>
 */
@Component
public class CXCASCRNAACalculation extends AbstractPatientCalculation {

  private final String ON_OR_AFTER = "onOrAfter";

  private final String ON_OR_BEFORE = "onOrBefore";

  private final String LOCATION = "location";

  private final String ANSWERS = "answers";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();

    Date startDate = (Date) context.getFromCache(ON_OR_AFTER);
    Date endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    Location location = (Location) context.getFromCache(LOCATION);

    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    EncounterType rastreioDoCancroDoColoUterinoEncounterType =
        hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType();
    EncounterType adultoSeguimentoEncounterType = hivMetadata.getAdultoSeguimentoEncounterType();
    EncounterType masterCardEncounterType = hivMetadata.getMasterCardEncounterType();

    Concept resultadoViaConcept = hivMetadata.getResultadoViaConcept();

    List<Concept> conceptsAnswers = (List<Concept>) parameterValues.get(ANSWERS);

    CalculationResultMap finchaCCUResulMap =
        eptsCalculationService.getObs(
            resultadoViaConcept,
            rastreioDoCancroDoColoUterinoEncounterType,
            cohort,
            location,
            conceptsAnswers,
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap fichaClinicaResulMap =
        eptsCalculationService.getObs(
            resultadoViaConcept,
            adultoSeguimentoEncounterType,
            cohort,
            location,
            conceptsAnswers,
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.ENCOUNTER_DATETIME,
            context);

    CalculationResultMap fichaResumoResulMap =
        eptsCalculationService.getObs(
            resultadoViaConcept,
            masterCardEncounterType,
            cohort,
            location,
            conceptsAnswers,
            TimeQualifier.FIRST,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
            context);

    for (Integer pId : cohort) {

      Obs finchaCCU = EptsCalculationUtils.resultForPatient(finchaCCUResulMap, pId);
      Obs fichaClinica = EptsCalculationUtils.resultForPatient(fichaClinicaResulMap, pId);
      Obs fichaResumo = EptsCalculationUtils.resultForPatient(fichaResumoResulMap, pId);

      if (finchaCCU != null) {
        map.put(pId, new SimpleResult(finchaCCU, this));
        continue;

      } else if (fichaClinica != null && fichaResumo != null) {
        if (fichaClinica
                .getEncounter()
                .getEncounterDatetime()
                .compareTo(fichaResumo.getValueDatetime())
            > 0) {
          map.put(pId, new SimpleResult(fichaResumo, this));
          continue;
        } else {
          map.put(pId, new SimpleResult(fichaClinica, this));
        }
      }
    }

    return map;
  }
}
