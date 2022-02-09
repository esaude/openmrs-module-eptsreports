package org.openmrs.module.eptsreports.reporting.calculation.generic;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TargetGroupCalculation extends AbstractPatientCalculation {

  public static final String TIPO = "type";

  // Target Group source by precedence in ascending order
  enum TargetGroupSource {
    PERSON_ATTRIBUTE,
    REGISTO_PREP_FORM,
    SEGUIMENTO_PREP_FORM
  }

  public enum TargetGroup {
    ADOLESCENT_AND_YOUTH,
    PREGNANT,
    BREASTFEEDING,
    MILITARY,
    MINER,
    TRUCK_DRIVER,
    SERODISCORDANT;

    public static TargetGroup of(Concept concept) {
      HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

      CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

      if (concept.equals(hivMetadata.getAdolescentsAndYouthAtRiskConcept())) {
        return ADOLESCENT_AND_YOUTH;
      } else if (concept.equals(commonMetadata.getPregnantConcept())) {
        return PREGNANT;
      } else if (concept.equals(commonMetadata.getBreastfeeding())) {
        return BREASTFEEDING;
      } else if (concept.equals(hivMetadata.getMilitaryOrPoliceConcept())) {
        return MILITARY;
      } else if (concept.equals(hivMetadata.getMinerConcept())) {
        return MINER;
      } else if (concept.equals(hivMetadata.getDriverConcept())) {
        return TRUCK_DRIVER;
      } else if (concept.equals(hivMetadata.getCoupleResultsAreDifferentConcept())) {
        return SERODISCORDANT;
      }
      return null;
    }

    public static TargetGroup of(PersonAttribute personAttribute) {
      switch (personAttribute.getValue().toUpperCase()) {
        case "AYR":
        case "AJR":
          return ADOLESCENT_AND_YOUTH;
        case "PW":
        case "MG":
          return PREGNANT;
        case "BW":
        case "ML":
          return BREASTFEEDING;
        case "MIL":
          return MILITARY;
        case "MIN":
          return MINER;
        case "TD":
        case "CLC":
          return TRUCK_DRIVER;
        case "CS":
          return SERODISCORDANT;
        default:
      }
      return null;
    }
  }

  static class TargetGroupAndSource implements Comparable<TargetGroupAndSource> {

    private final TargetGroup targetGroup;
    private final TargetGroupSource source;

    TargetGroupAndSource(TargetGroup targetGroup, TargetGroupSource source) {
      this.targetGroup = targetGroup;
      this.source = source;
    }

    @Override
    public int compareTo(TargetGroupAndSource targetGroupAndSource) {
      return this.source.compareTo(targetGroupAndSource.source);
    }

    TargetGroup getTargetGroup() {
      return this.targetGroup;
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

    CalculationResultMap personAttribute = getPersonAttribute(cohort, context);

    CalculationResultMap getPrepInicial = getPrepInicial(cohort, context, location, onOrBefore);

    CalculationResultMap getPrepSeguimento =
        getPrepSeguimento(cohort, context, location, onOrBefore);

    TargetGroup type = (TargetGroup) parameterValues.get(TIPO);

    for (Integer pId : cohort) {
      boolean equals = false;

      TargetGroup patientTargetGroup =
          getAssignedTargetGroup(
              pId, personAttribute, getPrepInicial, getPrepSeguimento, onOrBefore);
      if (type != null && type.equals(patientTargetGroup)) {
        equals = true;
      }
      resultMap.put(pId, new BooleanResult(equals, this));
    }

    return resultMap;
  }

  private TargetGroup getAssignedTargetGroup(
      Integer pId,
      CalculationResultMap personAttribute,
      CalculationResultMap prepInicial,
      CalculationResultMap prepSeguimento,
      Date endDate) {

    ListMap<Date, TargetGroupAndSource> targetGroupByDate = new ListMap<>(true);

    if (!personAttribute.isEmpty(pId)) {
      PersonAttribute attr = personAttribute.get(pId).asType(PersonAttribute.class);
      Date date = attr.getDateCreated();
      try {
        TargetGroup targetGroup = TargetGroup.of(attr);
        targetGroupByDate.putInList(
            date, new TargetGroupAndSource(targetGroup, TargetGroupSource.PERSON_ATTRIBUTE));
      } catch (IllegalArgumentException e) {
        // Ignore unmapped target group string
      }
    }

    if (prepInicial != null && prepInicial.containsKey(pId)) {
      Obs obs = getRequiredObservation(prepInicial, pId, endDate);
      Date date;
      TargetGroup targetGroup;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        targetGroup = TargetGroup.of(obs.getValueCoded());
        targetGroupByDate.putInList(
            date, new TargetGroupAndSource(targetGroup, TargetGroupSource.REGISTO_PREP_FORM));
      }
    }

    if (prepSeguimento != null && prepSeguimento.containsKey(pId)) {
      Obs obs = getRequiredObservation(prepSeguimento, pId, endDate);
      Date date;
      TargetGroup targetGroup;
      if (obs != null
          && obs.getEncounter() != null
          && obs.getEncounter().getEncounterDatetime() != null
          && obs.getValueCoded() != null) {
        date = obs.getEncounter().getEncounterDatetime();
        targetGroup = TargetGroup.of(obs.getValueCoded());
        targetGroupByDate.putInList(
            date, new TargetGroupAndSource(targetGroup, TargetGroupSource.SEGUIMENTO_PREP_FORM));
      }
    }

    TargetGroup assignedTargetGroup = null;
    if (!targetGroupByDate.isEmpty()) {
      Date maxDate = Collections.max(targetGroupByDate.keySet());
      List<TargetGroupAndSource> targetGroups = targetGroupByDate.get(maxDate);
      assignedTargetGroup = Collections.max(targetGroups).getTargetGroup();
    }

    return assignedTargetGroup;
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
    Concept targetGroupConcept = hivMetadata.getPrepTargetGroupConcept();
    return eptsCalculationService.allObservations(
        targetGroupConcept, null, encounterTypes, location, cohort, context);
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
    Concept targetGroupConcept = hivMetadata.getPrepTargetGroupConcept();
    return eptsCalculationService.allObservations(
        targetGroupConcept, null, encounterTypes, location, cohort, context);
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
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);
    List<Obs> obsWithinReportingPeriod = new ArrayList<>();
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
      // get the last obs in the list
      requiredObs = sortedObs.get(sortedObs.size() - 1);
      // loop through all the obs collected for each patient per the encounter type and check if
      // there is any that occurred on same date
      for (Obs obs : sortedObs) {
        if (requiredObs != null
            && obs.getObsDatetime().compareTo(requiredObs.getObsDatetime()) >= 0) {
          if (obs.getValueCoded().equals(hivMetadata.getAdolescentsAndYouthAtRiskConcept())) {
            requiredObs = obs;

          } else if (obs.getValueCoded().equals(commonMetadata.getPregnantConcept())) {
            requiredObs = obs;

          } else if (obs.getValueCoded().equals(commonMetadata.getBreastfeeding())) {
            requiredObs = obs;

          } else if (obs.getValueCoded().equals(hivMetadata.getMilitaryOrPoliceConcept())) {
            requiredObs = obs;

          } else if (obs.getValueCoded().equals(hivMetadata.getMinerConcept())) {
            requiredObs = obs;
          } else if (obs.getValueCoded().equals(hivMetadata.getDriverConcept())) {
            requiredObs = obs;
          } else if (obs.getValueCoded()
              .equals(hivMetadata.getCoupleResultsAreDifferentConcept())) {
            requiredObs = obs;
          }
        }
      }
    }
    return requiredObs;
  }
}
