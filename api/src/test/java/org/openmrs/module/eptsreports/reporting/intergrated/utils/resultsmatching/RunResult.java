package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import java.util.List;
import java.util.Map;

/**
 * This contains a detailed reports result matching result
 */
public class RunResult {
  // milli seconds taken to run current report
  private long currentReportEvaluationTime;
  // milli seconds taken to run master report
  private long masterReportEvaluationTime;
  // name#uuid
  private String currentReport;
  // name#uuid
  private String masterReport;
  // each looking like, parameter: value
  private Map<String, Object> parameterValues;

  private List<Match> matches;

  public long getCurrentReportEvaluationTime() {
    return currentReportEvaluationTime;
  }

  public long getMasterReportEvaluationTime() {
    return masterReportEvaluationTime;
  }

  public String getCurrentReport() {
    return currentReport;
  }

  public String getMasterReport() {
    return masterReport;
  }

  public Map<String, Object> getParameterValues() {
    return parameterValues;
  }

  public List<Match> getMatches() {
    return matches;
  }

  public RunResult(long currentReportEvaluationTime, long masterReportEvaluationTime, String currentReport, String masterReport, Map<String, Object> parameterValues, List<Match> matches) {
    this.currentReportEvaluationTime = currentReportEvaluationTime;
    this.masterReportEvaluationTime = masterReportEvaluationTime;
    this.currentReport = currentReport;
    this.masterReport = masterReport;
    this.parameterValues = parameterValues;
    this.matches = matches;
  }
}
