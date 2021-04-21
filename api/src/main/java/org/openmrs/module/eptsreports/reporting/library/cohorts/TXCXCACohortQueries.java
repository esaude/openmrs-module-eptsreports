package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNBBCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCATreatmentHierarchyCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.TXCXCACalculation;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.TreatmentType;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TXCXCACohortQueries {

  private CXCASCRNCohortQueries cxcascrnCohortQueries;

  private HivMetadata hivMetadata;

  private final String MAPPINGS = "startDate=${startDate},endDate=${endDate},location=${location}";

  @Autowired
  public TXCXCACohortQueries(CXCASCRNCohortQueries cxcascrnCohortQueries, HivMetadata hivMetadata) {
    this.cxcascrnCohortQueries = cxcascrnCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  public CohortDefinition getf1srtTimeScreened() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setName("TX f1srt Time Screened");

    CohortDefinition b =
        this.cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb = this.getBB();
    CohortDefinition b1 =
        this.cxcascrnCohortQueries.getAA1OrAA2(
            CXCASCRNCohortQueries.CXCASCRNResult.ANY, true, false);
    CohortDefinition b2 =
        this.cxcascrnCohortQueries.getAA1OrAA2(
            CXCASCRNCohortQueries.CXCASCRNResult.ANY, false, false);
    ;

    cd.addSearch("B", EptsReportUtils.map(b, MAPPINGS));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("B1", EptsReportUtils.map(b1, "onOrAfter=${startDate},location=${location}"));
    cd.addSearch("B2", EptsReportUtils.map(b2, "onOrAfter=${startDate},location=${location}"));

    cd.setCompositionString("B AND BB AND NOT B1");
    return cd;
  }

  public CohortDefinition getRescreenedAfterPreviousNegative() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setName("TX Rescreened After Previous Negative");

    CohortDefinition b =
        this.cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb = this.getBB();
    CohortDefinition b3 =
        this.cxcascrnCohortQueries.getAA3OrAA4(CXCASCRNCohortQueries.CXCASCRNResult.NEGATIVE);

    cd.addSearch("B", EptsReportUtils.map(b, MAPPINGS));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("B3", EptsReportUtils.map(b3, "onOrAfter=${startDate},location=${location}"));

    cd.setCompositionString("B AND BB AND B3");

    return cd;
  }

  public CohortDefinition getPostTreatmentFollowUp() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setName("TX Post Treatment Follow Up");

    CohortDefinition b =
        this.cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb = this.getBB();
    CohortDefinition b4 =
        this.cxcascrnCohortQueries.getAA3OrAA4(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb1 = this.getBB1();

    cd.addSearch("B", EptsReportUtils.map(b, MAPPINGS));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("B4", EptsReportUtils.map(b4, "onOrAfter=${startDate},location=${location}"));
    cd.addSearch(
        "BB1",
        EptsReportUtils.map(
            bb1, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("B AND BB AND B4 AND BB1");

    return cd;
  }

  public CohortDefinition getRescreenedAfterPreviousPositive() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "startDate", Date.class));
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.setName("TX Rescreened after previous positive");

    CohortDefinition b =
        this.cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb = this.getBB();
    CohortDefinition f1srtTimeScreened = getf1srtTimeScreened();
    CohortDefinition rescreenedAfterPreviousNegative = getRescreenedAfterPreviousNegative();
    CohortDefinition postTreatmentFollowUp = getPostTreatmentFollowUp();

    cd.addSearch("B", EptsReportUtils.map(b, MAPPINGS));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("postTreatmentFollowUp", EptsReportUtils.map(f1srtTimeScreened, MAPPINGS));
    cd.addSearch(
        "rescreenedAfterPreviousNegative",
        EptsReportUtils.map(rescreenedAfterPreviousNegative, MAPPINGS));
    cd.addSearch("postTreatmentFollowUp", EptsReportUtils.map(postTreatmentFollowUp, MAPPINGS));

    cd.setCompositionString(
        "B AND BB AND NOT (postTreatmentFollowUp OR rescreenedAfterPreviousNegative OR postTreatmentFollowUp)");

    return cd;
  }

  private CohortDefinition getBB() {
    TXCXCACalculation cxcascrnCalculation =
        Context.getRegisteredComponents(TXCXCACalculation.class).get(0);

    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setName("BB from CXCA SCRN");
    cd.setCalculation(cxcascrnCalculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(
        "answers", this.cxcascrnCohortQueries.getAnswers(CXCASCRNCohortQueries.CXCASCRNResult.ANY));

    return cd;
  }

  private CohortDefinition getBB1() {
    CXCASCRNBBCalculation cxcascrnCalculation =
        Context.getRegisteredComponents(CXCASCRNBBCalculation.class).get(0);

    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setName("BB1 from CXCA SCRN");
    cd.setCalculation(cxcascrnCalculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter(
        "answers",
        this.cxcascrnCohortQueries.getAnswers(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE));

    return cd;
  }

  public CohortDefinition getB5OrB6OrB7(TreatmentType treatmentType) {
    CXCATreatmentHierarchyCalculation cxcascrnCalculation =
        Context.getRegisteredComponents(CXCATreatmentHierarchyCalculation.class).get(0);

    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    switch (treatmentType) {
      case B5:
        cd.setName("TX B5 Cryotherapy");
        break;
      case B6:
        cd.setName("TX B6 Thermocoagulation");
        break;
      case B7:
        cd.setName("TX B7 LEEP");
        break;
      default:
        throw new IllegalArgumentException("Unsupported value");
    }
    cd.setCalculation(cxcascrnCalculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("type", treatmentType);

    return cd;
  }

  public CohortDefinition getTotal() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total of  TX CXCA SCRN");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition b =
        this.cxcascrnCohortQueries.getTotal(CXCASCRNCohortQueries.CXCASCRNResult.POSITIVE);
    CohortDefinition bb = this.getBB();

    cd.addSearch("B", EptsReportUtils.map(b, MAPPINGS));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("B AND BB");

    return cd;
  }

  public CohortDefinition getB5() {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("TX B5 Cryotherapy");

    Map<String, Integer> map = new HashMap<>();
    map.put("28", hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType().getEncounterTypeId());
    map.put("2117", hivMetadata.getCryotherapyPerformedOnTheSameDayASViaConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("23967", hivMetadata.getCryotherapyDateConcept().getConceptId());
    map.put("2149", hivMetadata.getViaResultOnTheReferenceConcept().getConceptId());
    map.put("23874", hivMetadata.getPediatricNursingConcept().getConceptId());

    String sql =
        ""
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON e.encounter_id = o.encounter_id "
            + "WHERE  "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${28} "
            + "    AND ( "
            + "            (o.concept_id = ${2117} AND o.value_coded = ${1065}) "
            + "            OR "
            + "            (o.concept_id = ${23967} ) "
            + "            OR "
            + "            (o.concept_id = ${2149} AND o.value_coded = ${23874}) "
            + "        )    "
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(sql));

    return cd;
  }

  public SqlCohortDefinition getB6() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("TX B6 Thermocoagulation ");

    Map<String, Integer> map = new HashMap<>();
    map.put("28", hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType().getEncounterTypeId());
    map.put("2149", hivMetadata.getViaResultOnTheReferenceConcept().getConceptId());
    map.put("23972", hivMetadata.getThermocoagulationConcept().getConceptId());

    String sql =
        ""
            + " SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON e.encounter_id = o.encounter_id "
            + " WHERE  "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${28} "
            + "    AND o.concept_id = ${2149} AND o.value_coded = ${23972} "
            + "    AND e.location_id = :location ";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(sql));

    return cd;
  }

  public SqlCohortDefinition getB7() {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName("TX B7 LEEP");

    Map<String, Integer> map = new HashMap<>();
    map.put("28", hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType().getEncounterTypeId());
    map.put("2149", hivMetadata.getViaResultOnTheReferenceConcept().getConceptId());
    map.put("23970", hivMetadata.getLeepConcept().getConceptId());
    map.put("23973", hivMetadata.getconizationConcept().getConceptId());

    String sql =
        ""
            + " SELECT p.patient_id "
            + " FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON e.encounter_id = o.encounter_id "
            + " WHERE  "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${28} "
            + "    AND o.concept_id = ${2149} AND o.value_coded IN (${23970}, ${23973}) "
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);
    cd.setQuery(sb.replace(sql));

    return cd;
  }

  public CohortDefinition getFinalComposition(
      CohortDefinition ccd, CohortDefinition scd, String name) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.setName(name);

    cd.addSearch("CCD", EptsReportUtils.map(ccd, MAPPINGS));
    cd.addSearch(
        "SCD",
        EptsReportUtils.map(
            scd, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("CCD AND SCD");

    return cd;
  }
}
