package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import java.util.List;
import java.util.Map;

/** This contains a detailed reports result matching result */
public class RunResult {
  // time(hh:mm:ss) taken to run current report
  private String currentReportEvaluationTime;
  // time(hh:mm:ss) taken to run master report
  private String masterReportEvaluationTime;
  private String currentReportLabel;
  private String masterReportLabel;
  // name#uuid
  private String currentReport;
  // name#uuid
  private String masterReport;
  // each looking like, parameter: value
  private Map<String, Object> parameterValues;

  private List<Match> matches;

  public String getCurrentReportEvaluationTime() {
    return currentReportEvaluationTime;
  }

  public String getMasterReportEvaluationTime() {
    return masterReportEvaluationTime;
  }

  public String getCurrentReport() {
    return currentReport;
  }

  public String getMasterReport() {
    return masterReport;
  }

  public String getCurrentReportLabel() {
    return currentReportLabel;
  }

  public String getMasterReportLabel() {
    return masterReportLabel;
  }

  public Map<String, Object> getParameterValues() {
    return parameterValues;
  }

  public List<Match> getMatches() {
    return matches;
  }

  public RunResult(
      String currentReportEvaluationTime,
      String masterReportEvaluationTime,
      String currentReport,
      String masterReport,
      String currentReportLabel,
      String masterReportLabel,
      Map<String, Object> parameterValues,
      List<Match> matches) {
    this.currentReportEvaluationTime = currentReportEvaluationTime;
    this.masterReportEvaluationTime = masterReportEvaluationTime;
    this.currentReport = currentReport;
    this.masterReport = masterReport;
    this.currentReportLabel = currentReportLabel;
    this.masterReportLabel = masterReportLabel;
    this.parameterValues = parameterValues;
    this.matches = matches;
  }
}
