package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.*;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNAACalculation;
import org.openmrs.module.eptsreports.reporting.calculation.cxcascrn.CXCASCRNBBCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.CXCASCRNQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNCohortQueries {

  private TxCurrCohortQueries txCurrCohortQueries;

  private GenderCohortQueries genderCohortQueries;

  private AgeCohortQueries ageCohortQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public CXCASCRNCohortQueries(
      TxCurrCohortQueries txCurrCohortQueries,
      GenderCohortQueries genderCohortQueries,
      AgeCohortQueries ageCohortQueries,
      HivMetadata hivMetadata) {
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.genderCohortQueries = genderCohortQueries;
    this.ageCohortQueries = ageCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  public enum CXCASCRNResult {
    POSITIVE,
    NEGATIVE,
    ANY,
    SUSPECTED,
    ALL
  }

  /**
   * A: Select all patients from Tx_curr by end of reporting period and who are female and Age >= 15
   * years
   */
  private CohortDefinition getA() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("A from  CXCA SCRN");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txcurr = this.txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true);

    CohortDefinition female = this.genderCohortQueries.femaleCohort();

    CohortDefinition adults = this.ageCohortQueries.createXtoYAgeCohort("adullts", 15, null);

    cd.addSearch(
        "txcurr", EptsReportUtils.map(txcurr, "onOrBefore=${endDate},location=${location}"));

    cd.addSearch("female", EptsReportUtils.map(female, ""));

    cd.addSearch("adults", EptsReportUtils.map(adults, ""));

    cd.setCompositionString("txcurr AND female AND adults");

    return cd;
  }

  /**
   *
   *
   * <ul>
   *   <li>AA:
   *       <ul>
   *         <li>( A_FichaCCU: ( Select all patients with the first Ficha de Registo Para Rastreio
   *             do Cancro do Colo Uterino (encounter type 28) with the following conditions:
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
   *                     <li>VIA RESULTS (concept id 2094) and value coded SUSPECTED CANCER,
   *                         NEGATIVE or POSITIVE (concept id IN [2093, 664, 703])
   *                     <li>And encounter datetime >= startdate and <= enddate
   *                   </ul>
   *               <li>A_FichaResumo: Select all patients on Ficha Resumo (encounter type 53) who
   *                   have the following conditions:
   *                   <ul>
   *                     <li>VIA RESULTS (concept id 2094) and value coded POSITIVE (concept id 703)
   *                     <li>And value datetime >= startdate and <= enddate )
   *                   </ul>
   *               <li>Note2: The system will consider the earliest VIA date during the reporting
   *                   period from the different sources listed above (from encounter type 6 and
   *                   53).
   *             </ul>
   *       </ul>
   * </ul>
   */
  private CohortDefinition getAA(CXCASCRNResult result) {
    CXCASCRNAACalculation cxcascrnCalculation =
        Context.getRegisteredComponents(CXCASCRNAACalculation.class).get(0);

    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setName("AA from CXCA SCRN");
    cd.setCalculation(cxcascrnCalculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("result", result);

    return cd;
  }

  public CohortDefinition getAA1OrAA2(CXCASCRNResult cxcascrnResult, boolean isAA1, boolean max) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    if (isAA1) {
      cd.setName("AA1 from CXCA SCRN");
    } else {
      cd.setName("AA2 from CXCA SCRN");
    }

    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.setQuery(
        CXCASCRNQueries.getAA1OrAA2Query(
            cxcascrnResult,
            isAA1,
            max,
            hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType().getEncounterTypeId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardEncounterType().getEncounterTypeId(),
            hivMetadata.getResultadoViaConcept().getConceptId(),
            hivMetadata.getNegative().getConceptId(),
            hivMetadata.getPositive().getConceptId(),
            hivMetadata.getSuspectedCancerConcept().getConceptId()));

    return cd;
  }

  private CohortDefinition getBB(CXCASCRNResult result) {
    CXCASCRNBBCalculation cxcascrnCalculation =
        Context.getRegisteredComponents(CXCASCRNBBCalculation.class).get(0);

    CalculationCohortDefinition cd = new CalculationCohortDefinition();
    cd.setName("BB from CXCA SCRN");
    cd.setCalculation(cxcascrnCalculation);
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    cd.addCalculationParameter("result", result);

    return cd;
  }

  public CohortDefinition get1stTimeScreened(CXCASCRNResult cxcascrnResult) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("1st Time Screened");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a = getA();
    CohortDefinition aa = getAA(cxcascrnResult);
    CohortDefinition aa1 = getAA1OrAA2(CXCASCRNResult.ANY, true, false);
    CohortDefinition aa2 = getAA1OrAA2(CXCASCRNResult.ANY, false, false);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("AA1", EptsReportUtils.map(aa1, "onOrAfter=${startDate},location=${location}"));
    cd.addSearch("AA2", EptsReportUtils.map(aa2, "onOrAfter=${startDate},location=${location}"));

    cd.setCompositionString("A AND AA AND NOT (AA1 OR AA2)");
    return cd;
  }

  public CohortDefinition getRescreenedAfterPreviousNegative(CXCASCRNResult cxcascrnResult) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Rescreened After Previous Negative");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a = getA();
    CohortDefinition aa = getAA(cxcascrnResult);
    CohortDefinition aa3 = getAA3OrAA4(CXCASCRNResult.NEGATIVE);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("AA3", EptsReportUtils.map(aa3, "onOrAfter=${startDate},location=${location}"));

    cd.setCompositionString("A AND AA AND AA3");
    return cd;
  }

  public CohortDefinition getPostTreatmentFollowUp(CXCASCRNResult cxcascrnResult) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Post Treatment FollowUp");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a = getA();
    CohortDefinition aa = getAA(cxcascrnResult);
    CohortDefinition aa4 = getAA3OrAA4(CXCASCRNResult.POSITIVE);
    CohortDefinition bb = getBB(cxcascrnResult);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch("AA4", EptsReportUtils.map(aa4, "onOrAfter=${startDate},location=${location}"));
    cd.addSearch(
        "BB",
        EptsReportUtils.map(
            bb, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND AA AND AA4 AND BB");

    return cd;
  }

  public CohortDefinition getRescreenedAfterPreviousPositive(CXCASCRNResult cxcascrnResult) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Rescreened After Previous Positive");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a = getA();
    CohortDefinition aa = getAA(cxcascrnResult);
    CohortDefinition fitstTimeScreened = get1stTimeScreened(cxcascrnResult);
    CohortDefinition rescreenedAfterPreviousNegative =
        getRescreenedAfterPreviousNegative(cxcascrnResult);
    CohortDefinition postTreatmentFollowUp = getPostTreatmentFollowUp(cxcascrnResult);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "postTreatmentFollowUp",
        EptsReportUtils.map(
            fitstTimeScreened, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "rescreenedAfterPreviousNegative",
        EptsReportUtils.map(
            rescreenedAfterPreviousNegative,
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "postTreatmentFollowUp",
        EptsReportUtils.map(
            postTreatmentFollowUp,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "A AND AA AND NOT postTreatmentFollowUp AND NOT rescreenedAfterPreviousNegative AND NOT postTreatmentFollowUp ");
    return cd;
  }

  public CohortDefinition getPositiveOrNegativeOrSuspetedOrAll(CXCASCRNResult cxcascrnResult) {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition a = getA();
    CohortDefinition aa = getAA(cxcascrnResult);

    cd.addSearch(
        "A",
        EptsReportUtils.map(a, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND AA");

    return cd;
  }

  public CohortDefinition getTotal(CXCASCRNResult cxcascrnResult) {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Total of CXCA SCRN");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition aa = getAA(cxcascrnResult);

    CohortDefinition a = this.getA();

    cd.addSearch("A", EptsReportUtils.map(a, "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "AA",
        EptsReportUtils.map(
            aa, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

    cd.setCompositionString("A AND AA");

    return cd;
  }

  public List<Concept> getAnswers(CXCASCRNResult cxcascrnResult) {
    Concept suspectedCancerConcept = hivMetadata.getSuspectedCancerConcept();
    Concept negative = hivMetadata.getNegative();
    Concept positive = hivMetadata.getPositive();

    List<Concept> answers = new ArrayList<>();

    if (cxcascrnResult == CXCASCRNResult.ALL) {
      answers = Arrays.asList(suspectedCancerConcept, negative, positive);

    } else if (cxcascrnResult == CXCASCRNResult.SUSPECTED) {
      answers = Arrays.asList(suspectedCancerConcept);

    } else if (cxcascrnResult == CXCASCRNResult.POSITIVE) {
      answers = Arrays.asList(positive);

    } else if (cxcascrnResult == CXCASCRNResult.NEGATIVE) {
      answers = Arrays.asList(negative);
    }
    return answers;
  }

  public CohortDefinition getAA3OrAA4(CXCASCRNResult cxcascrnResult) {
    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));
    if (cxcascrnResult == CXCASCRNResult.NEGATIVE) {
      cd.setName("AA3 from CXCA SCRN");
    }
    if (cxcascrnResult == CXCASCRNResult.POSITIVE) {
      cd.setName("AA4 from CXCA SCRN");
    }

    cd.setQuery(CXCASCRNQueries.getAA3OrAA4Query(cxcascrnResult, hivMetadata, false));

    return cd;
  }
}
