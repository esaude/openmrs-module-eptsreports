package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.*;
import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class KeyPopulationCalculation extends AbstractPatientCalculation {

  public static final String TYPE = "type";

  // Key population source by precedence in ascending order
  enum KeyPopSource {
    PERSON_ATTRIBUTE,
    APSS_FORM,
    ADULTO_FORM,
    REGISTO_PREP_FORM,
    SEGUIMENTO_PREP_FORM
  }

  public enum KeyPop {
    DRUG_USER,
    HOMOSEXUAL,
    PRISONER,
    SEX_WORKER,
    TRANSGENDER;

    public static KeyPop of(Concept concept) {
      HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
      if (concept.equals(hivMetadata.getHomosexualConcept())) {
        return HOMOSEXUAL;
      } else if (concept.equals(hivMetadata.getDrugUseConcept())) {
        return DRUG_USER;
      } else if (concept.equals(hivMetadata.getImprisonmentConcept())) {
        return PRISONER;
      } else if (concept.equals(hivMetadata.getSexWorkerConcept())) {
        return SEX_WORKER;
      } else if (concept.equals(hivMetadata.getTransGenderConcept())) {
        return TRANSGENDER;
      }
      return null;
    }

    public static KeyPop of(PersonAttribute personAttribute) {
      switch (personAttribute.getValue().toUpperCase()) {
        case "MSM":
        case "HSH":
          return HOMOSEXUAL;
        case "PID":
          return DRUG_USER;
        case "PRISONER":
        case "RC":
        case "REC":
          return PRISONER;
        case "CSW":
        case "TS":
        case "MTS":
        case "FSW":
        case "MSW":
        case "HTS":
          return SEX_WORKER;
        case "TG":
          return TRANSGENDER;
        default:
      }
      return null;
    }
  }

  static class KeyPopAndSource implements Comparable<KeyPopAndSource> {

    private final KeyPop keyPop;
    private final KeyPopSource source;

    KeyPopAndSource(KeyPop keyPop, KeyPopSource source) {
      this.keyPop = keyPop;
      this.source = source;
    }

    @Override
    public int compareTo(KeyPopAndSource keyPopAndSource) {
      return this.source.compareTo(keyPopAndSource.source);
    }

    KeyPop getKeyPop() {
      return this.keyPop;
    }
  }

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap resultMap = new CalculationResultMap();

    Location location = (Location) context.getFromCache("location");
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");

    CalculationResultMap adultoSeguimento =
        getAdultoSeguimento(cohort, context, location, onOrBefore);
    CalculationResultMap apssPrevencaoPositiva =
        getApssPrevencaoPositiva(cohort, context, location, onOrBefore);
    CalculationResultMap personAttribute = getPersonAttribute(cohort, context);

    CalculationResultMap getPrepInicial = getPrepInicial(cohort, context, location, onOrBefore);

    CalculationResultMap getPrepSeguimento =
        getPrepSeguimento(cohort, context, location, onOrBefore);

    KeyPop type = (KeyPop) parameterValues.get(TYPE);

    for (Integer pId : cohort) {
      boolean equals = false;

      KeyPop patientKeyPop =
          getAssignedKeyPop(
              pId,
              adultoSeguimento,
              apssPrevencaoPositiva,
              personAttribute,
              getPrepInicial,
              getPrepSeguimento,
              onOrBefore);
      if (type != null && type.equals(patientKeyPop)) {
        equals = true;
      }
      resultMap.put(pId, new BooleanResult(equals, this));
    }

    return resultMap;
  }

  private KeyPop getAssignedKeyPop(
      Integer pId,
      CalculationResultMap adultoSeguimento,
      CalculationResultMap apssPrevencaoPositiva,
      CalculationResultMap personAttribute,
      CalculationResultMap prepInicial,
      CalculationResultMap prepSeguimento,
      Date endDate) {

    ListMap<Date, KeyPopAndSource> keyPopByDate = new ListMap<>(true);

    if (adultoSeguimento != null && adultoSeguimento.containsKey(pId)) {
      Obs obs = getRequiredObservation(adultoSeguimento, pId, endDate);
      Date date;
      KeyPop keypop;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        keypop = KeyPop.of(obs.getValueCoded());
        keyPopByDate.putInList(date, new KeyPopAndSource(keypop, KeyPopSource.ADULTO_FORM));
      }
    }

    if (!personAttribute.isEmpty(pId)) {
      PersonAttribute attr = personAttribute.get(pId).asType(PersonAttribute.class);
      Date date = attr.getDateCreated();
      try {
        KeyPop keypop = KeyPop.of(attr);
        keyPopByDate.putInList(date, new KeyPopAndSource(keypop, KeyPopSource.PERSON_ATTRIBUTE));
      } catch (IllegalArgumentException e) {
        // Ignore unmapped key population string
      }
    }

    if (apssPrevencaoPositiva != null && apssPrevencaoPositiva.containsKey(pId)) {
      Obs obs = getRequiredObservation(apssPrevencaoPositiva, pId, endDate);
      Date date;
      KeyPop keypop;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        keypop = KeyPop.of(obs.getValueCoded());
        keyPopByDate.putInList(date, new KeyPopAndSource(keypop, KeyPopSource.APSS_FORM));
      }
    }

    if (prepInicial != null && prepInicial.containsKey(pId)) {
      Obs obs = getRequiredObservation(prepInicial, pId, endDate);
      Date date;
      KeyPop keypop;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        keypop = KeyPop.of(obs.getValueCoded());
        keyPopByDate.putInList(date, new KeyPopAndSource(keypop, KeyPopSource.REGISTO_PREP_FORM));
      }
    }

    if (prepSeguimento != null && prepSeguimento.containsKey(pId)) {
      Obs obs = getRequiredObservation(prepSeguimento, pId, endDate);
      Date date;
      KeyPop keypop;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        keypop = KeyPop.of(obs.getValueCoded());
        keyPopByDate.putInList(
            date, new KeyPopAndSource(keypop, KeyPopSource.SEGUIMENTO_PREP_FORM));
      }
    }

    KeyPop assignedKeyPop = null;
    if (!keyPopByDate.isEmpty()) {
      Date maxDate = Collections.max(keyPopByDate.keySet());
      List<KeyPopAndSource> keyPops = keyPopByDate.get(maxDate);
      assignedKeyPop = Collections.max(keyPops).getKeyPop();
    }

    return assignedKeyPop;
  }

  private CalculationResultMap getPersonAttribute(
      Collection<Integer> cohort, PatientCalculationContext context) {
    PersonAttributeDataDefinition definition = new PersonAttributeDataDefinition();
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    PersonAttributeType identificadorDefinidoLocalmente01 =
        hivMetadata.getIdentificadorDefinidoLocalmente01();
    definition.setPersonAttributeType(identificadorDefinidoLocalmente01);
    return EptsCalculationUtils.evaluateWithReporting(definition, cohort, null, null, context);
  }

  private CalculationResultMap getAdultoSeguimento(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      Location location,
      Date endDate) {
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ArrayList<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getAdultoSeguimentoEncounterType());
    Concept keyPop = hivMetadata.getKeyPopulationConcept();
    return eptsCalculationService.allObservations(
        keyPop, null, encounterTypes, location, cohort, context);
  }

  private CalculationResultMap getApssPrevencaoPositiva(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      Location location,
      Date endDate) {
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ArrayList<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getPrevencaoPositivaSeguimentoEncounterType());
    Concept keyPop = hivMetadata.getKeyPopulationConcept();
    return eptsCalculationService.allObservations(
        keyPop, null, encounterTypes, location, cohort, context);
  }

  private CalculationResultMap getPrepInicial(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      Location location,
      Date endDate) {
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ArrayList<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getPrepInicialEncounterType());
    Concept keyPop = hivMetadata.getKeyPopulationConcept();
    return eptsCalculationService.allObservations(
        keyPop, null, encounterTypes, location, cohort, context);
  }

  private CalculationResultMap getPrepSeguimento(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      Location location,
      Date endDate) {
    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    ArrayList<EncounterType> encounterTypes = new ArrayList<>();
    encounterTypes.add(hivMetadata.getPrepSeguimentoEncounterType());
    Concept keyPop = hivMetadata.getKeyPopulationConcept();
    return eptsCalculationService.allObservations(
        keyPop, null, encounterTypes, location, cohort, context);
  }

  private List<Obs> sortObsByObsDatetime(List<Obs> obs) {
    Collections.sort(
        obs,
        new Comparator<Obs>() {
          @Override
          public int compare(Obs a, Obs b) {
            return a.getObsDatetime().compareTo(b.getObsDatetime());
          }
        });
    return obs;
  }

  private Obs getRequiredObservation(CalculationResultMap map, Integer pId, Date endDate) {

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    List<Obs> obsWithinReportingPeriod = new ArrayList<>();
    List<Obs> keyPopForMen = new ArrayList<>();
    List<Obs> keyPopForWomen = new ArrayList<>();
    Patient patient = Context.getPatientService().getPatient(pId);

    Obs requiredObs = null;
    ListResult listResult = (ListResult) map.get(pId);
    List<Obs> obsList = EptsCalculationUtils.extractResultValues(listResult);
    for (Obs obs : obsList) {
      if (endDate != null && obs.getObsDatetime().compareTo(endDate) <= 0) {
        obsWithinReportingPeriod.add(obs);
      }
    }
    List<Obs> sortedObs;
    if (obsWithinReportingPeriod.size() > 0) {
      sortedObs = sortObsByObsDatetime(obsWithinReportingPeriod);
      for (Obs menObs : sortedObs) {
        if (menObs.getValueCoded() != null
            && (menObs.getValueCoded().equals(hivMetadata.getHomosexualConcept())
                || menObs.getValueCoded().equals(hivMetadata.getDrugUseConcept())
                || menObs.getValueCoded().equals(hivMetadata.getImprisonmentConcept())
                || menObs.getValueCoded().equals(hivMetadata.getTransGenderConcept())
                || menObs.getValueCoded().equals(hivMetadata.getOtherOrNonCodedConcept()))) {
          keyPopForMen.add(menObs);
        }
      }

      for (Obs femaleObs : sortedObs) {
        if (femaleObs.getValueCoded() != null
            && (femaleObs.getValueCoded().equals(hivMetadata.getSexWorkerConcept())
                || femaleObs.getValueCoded().equals(hivMetadata.getDrugUseConcept())
                || femaleObs.getValueCoded().equals(hivMetadata.getImprisonmentConcept())
                || femaleObs.getValueCoded().equals(hivMetadata.getTransGenderConcept())
                || femaleObs.getValueCoded().equals(hivMetadata.getOtherOrNonCodedConcept()))) {
          keyPopForWomen.add(femaleObs);
        }
      }
      // get the last obs in the list
      requiredObs = null;
      List<Obs> lasRequiredObsForMaleSorted = sortObsByObsDatetime(keyPopForMen);
      List<Obs> lasRequiredObsForFemaleSorted = sortObsByObsDatetime(keyPopForWomen);
      Obs lastObsForMenKP = null;
      if (lasRequiredObsForMaleSorted.size() > 0) {
        lastObsForMenKP = lasRequiredObsForMaleSorted.get(lasRequiredObsForMaleSorted.size() - 1);
      }
      Obs lastObsForWomenKP = null;
      if (lasRequiredObsForFemaleSorted.size() > 0) {
        lastObsForWomenKP =
            lasRequiredObsForFemaleSorted.get(lasRequiredObsForFemaleSorted.size() - 1);
      }

      // loop through all the obs collected for each patient per the encounter type and check if
      // there is any that occurred on same date
      if (patient.getGender().equals("M")) {
        for (Obs obs : lasRequiredObsForMaleSorted) {
          if (lastObsForMenKP != null
              && obs.getObsDatetime().compareTo(lastObsForMenKP.getObsDatetime()) >= 0) {
            if (obs.getValueCoded().equals(hivMetadata.getDrugUseConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getHomosexualConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getImprisonmentConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getTransGenderConcept())) {
              requiredObs = obs;
              
            } else if (obs.getValueCoded().equals(hivMetadata.getOtherOrNonCodedConcept())) {
              requiredObs = obs;
            }
          }
        }
      } else if (patient.getGender().equals("F")) {
        for (Obs obs : lasRequiredObsForFemaleSorted) {
          if (lastObsForWomenKP != null
              && obs.getObsDatetime().compareTo(lastObsForWomenKP.getObsDatetime()) >= 0) {
            if (obs.getValueCoded().equals(hivMetadata.getDrugUseConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getSexWorkerConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getImprisonmentConcept())) {
              requiredObs = obs;

            } else if (obs.getValueCoded().equals(hivMetadata.getTransGenderConcept())) {
              requiredObs = obs;
              
            } else if (obs.getValueCoded().equals(hivMetadata.getOtherOrNonCodedConcept())) {
              requiredObs = obs;
            }
          }
        }
      }
    }
    return requiredObs;
  }
}
