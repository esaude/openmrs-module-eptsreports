package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.evaluator.PatientDataEvaluator;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoreThanOneEncounterInStatisticalYearCalculation extends AbstractPatientCalculation {

  private static final String ENCOUNTER_TYPE = "encounter_type";

  private static final String START_DATE = "onOrAfter";

  private static final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap calculationResultMap = new CalculationResultMap();

    Date startDate = (Date) context.getFromCache(START_DATE);

    Location location = (Location) context.getFromCache(LOCATION);

    EncounterType encounterType = (EncounterType) parameterValues.get(ENCOUNTER_TYPE);

    Date newStartDate = getStartDate(startDate);
    CalculationResultMap encounterMap =
        getEncounterMap(
            location, newStartDate, startDate, Arrays.asList(encounterType), cohort, context);

    for (Integer patientId : cohort) {

      List<Encounter> encounters = EptsCalculationUtils.resultForPatient(encounterMap, patientId);

      if (encounters != null && encounters.size() > 0) {
        calculationResultMap.put(patientId, new BooleanResult(true, this));
      }
    }

    return calculationResultMap;
  }

  private Date getStartDate(Date startDate) {

    Calendar startCalendar1 = Calendar.getInstance();
    startCalendar1.setTime(startDate);
    int month = startCalendar1.get(Calendar.MONTH);
    int day = startCalendar1.get(Calendar.DAY_OF_MONTH);

    if (day == 21 && month == Calendar.DECEMBER) {
      return startDate;
    }

    Calendar startCalendar2 = Calendar.getInstance();
    startCalendar2.setTime(startDate);
    startCalendar2.add(Calendar.YEAR, -1);
    startCalendar2.set(Calendar.MONTH, Calendar.DECEMBER);
    startCalendar2.set(Calendar.DAY_OF_MONTH, 21);
    return startCalendar2.getTime();
  }

  @Caching(strategy = ConfigurationPropertyCachingStrategy.class)
  @Localized("reporting.InnerEncounterstDataDefinition")
  private class InnerEncounterstDataDefinition extends BaseDataDefinition
      implements PatientDataDefinition {

    // ***** PROPERTIES *****

    @ConfigurationProperty private TimeQualifier which;

    @ConfigurationProperty(required = true)
    private List<EncounterType> types;

    @ConfigurationProperty private List<Location> locationList;

    @ConfigurationProperty private Date onOrAfter;

    @ConfigurationProperty private Date onOrBefore;

    @ConfigurationProperty private boolean onlyInActiveVisit;

    InnerEncounterstDataDefinition() {
      super();
    }

    public Class<?> getDataType() {
      if (which == TimeQualifier.LAST || which == TimeQualifier.FIRST) {
        return Encounter.class;
      }
      return List.class;
    }

    TimeQualifier getWhich() {
      return which;
    }

    void setWhich(TimeQualifier which) {
      this.which = which;
    }

    boolean getOnlyInActiveVisit() {
      return onlyInActiveVisit;
    }

    List<EncounterType> getTypes() {
      return types;
    }

    void setTypes(List<EncounterType> types) {
      this.types = types;
    }

    List<Location> getLocationList() {
      return locationList;
    }

    void setLocationList(List<Location> locationList) {
      this.locationList = locationList;
    }

    Date getOnOrAfter() {
      return onOrAfter;
    }

    void setOnOrAfter(Date onOrAfter) {
      this.onOrAfter = onOrAfter;
    }

    Date getOnOrBefore() {
      return onOrBefore;
    }

    void setOnOrBefore(Date onOrBefore) {
      this.onOrBefore = onOrBefore;
    }
  }

  @Handler(supports = InnerEncounterstDataDefinition.class, order = 50)
  class InnerEncountersDataEvaluator implements PatientDataEvaluator {

    @Autowired EvaluationService evaluationService;

    public EvaluatedPatientData evaluate(
        PatientDataDefinition definition, EvaluationContext context) throws EvaluationException {

      InnerEncounterstDataDefinition def = (InnerEncounterstDataDefinition) definition;
      EvaluatedPatientData c = new EvaluatedPatientData(def, context);

      if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
        return c;
      }

      HqlQueryBuilder q = new HqlQueryBuilder();
      q.select("e.patient.patientId", "e");
      q.from(Encounter.class, "e");
      q.wherePatientIn("e.patient.patientId", context);
      q.whereIn("e.encounterType", def.getTypes());
      q.whereIn("e.location", def.getLocationList());
      q.whereGreaterOrEqualTo("e.encounterDatetime", def.getOnOrAfter());
      q.whereLess("e.encounterDatetime", def.getOnOrBefore());

      if (def.getOnlyInActiveVisit()) {
        q.whereNull("e.visit.stopDatetime");
      }

      if (def.getWhich() == TimeQualifier.LAST) {
        q.orderDesc("e.encounterDatetime");
      } else {
        q.orderAsc("e.encounterDatetime");
      }

      List<Object[]> queryResult = evaluationService.evaluateToList(q, context);

      ListMap<Integer, Encounter> encountersForPatients = new ListMap<Integer, Encounter>();
      for (Object[] row : queryResult) {
        encountersForPatients.putInList((Integer) row[0], (Encounter) row[1]);
      }

      for (Integer pId : encountersForPatients.keySet()) {
        List<Encounter> l = encountersForPatients.get(pId);
        if (def.getWhich() == TimeQualifier.LAST || def.getWhich() == TimeQualifier.FIRST) {
          c.addData(pId, l.get(0));
        } else {
          c.addData(pId, l);
        }
      }

      return c;
    }
  }

  private CalculationResultMap getEncounterMap(
      Location location,
      Date onOrAfter,
      Date onOrBefore,
      List<EncounterType> encounterTypes,
      Collection<Integer> cohort,
      PatientCalculationContext context) {
    InnerEncounterstDataDefinition def = new InnerEncounterstDataDefinition();
    def.setWhich(TimeQualifier.ANY);
    def.setLocationList(Arrays.asList(location));
    if (onOrAfter != null) {
      def.setOnOrAfter(onOrAfter);
    }
    def.setOnOrBefore(onOrBefore);
    if (encounterTypes != null) {
      def.setName("all encounters ");
      def.setTypes(encounterTypes);
    } else {
      def.setName("all encounters of any type");
    }
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, null, null, context);
  }
}
