package org.openmrs.module.eptsreports.reporting.intergrated.utils.resultsmatching;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.util.StopWatch;

@Ignore
public class ResultsMatchingTest extends BaseModuleContextSensitiveTest {

  public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private static String INDICATOR_MAPPINGS_FILE_NAME = "eptsReportsResultsMatchingConfig.json";

  private static String XLSX_OUTPUT_NAME = "jembiAgainstFGHMatches.xlsx";

  private File eptsReportsResultsMatching;

  private JSONObject eptsReportsResultsMatchingConfig;

  private ReportDefinitionService reportDefinitionService;

  @Override
  public Boolean useInMemoryDatabase() {
    /*
     * ensure ~/.OpenMRS/openmrs-runtime.properties exists with your properties
     * such as; connection.username=openmrs
     * connection.url=jdbc:mysql://127.0.0.1:3316/openmrs
     * connection.password=wTV.Tpp0|Q&c
     */
    return false;
  }

  @Before
  public void setUp() throws Exception {
    eptsReportsResultsMatching =
        new File(System.getProperty("user.home") + File.separator + "eptsReportsResultsMatching");
    if (!eptsReportsResultsMatching.exists()) {
      eptsReportsResultsMatching.mkdirs();
    }
    File indicatorMappingFile;
    if (eptsReportsResultsMatching.list().length > 0
        && Arrays.asList(eptsReportsResultsMatching.list())
            .contains(INDICATOR_MAPPINGS_FILE_NAME)) {
      indicatorMappingFile =
          new File(
              eptsReportsResultsMatching.getAbsolutePath()
                  + File.separator
                  + INDICATOR_MAPPINGS_FILE_NAME);
    } else {
      indicatorMappingFile =
          new File(getClass().getClassLoader().getResource(INDICATOR_MAPPINGS_FILE_NAME).getPath());
    }
    eptsReportsResultsMatchingConfig =
        (JSONObject) new JSONParser().parse(new FileReader(indicatorMappingFile));
    Context.authenticate(
        (String) ((JSONObject) eptsReportsResultsMatchingConfig.get("user")).get("name"),
        (String) ((JSONObject) eptsReportsResultsMatchingConfig.get("user")).get("pass"));
    reportDefinitionService = Context.getService(ReportDefinitionService.class);
  }

  @Test
  public void matcher() throws Exception {
    // disabling running evaluations in batches
    ReportUtil.updateGlobalProperty(
        ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
    // defining a default reporting locale
    ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");

    boolean missMatch = false;
    List<RunResult> eptsReportTestResults = new ArrayList<RunResult>();
    Iterator mappingConfigIterator =
        ((JSONArray) eptsReportsResultsMatchingConfig.get("indicatorMappings")).iterator();
    // looping through each report matching with an entry in indicatorMappings
    while (mappingConfigIterator.hasNext()) {
      JSONObject reportConfig = (JSONObject) mappingConfigIterator.next();

      // the first and only key is uuid
      String reportUuid = reportConfig.keySet().iterator().next().toString();
      // current report indicator mapping
      JSONObject mappingsObject = (JSONObject) reportConfig.get(reportUuid);
      // getting parameter values for this indicator matching
      JSONObject parameterValues = (JSONObject) mappingsObject.get("parameterValues");
      // defining parameter values in report evaluation context
      EvaluationContext context = new EvaluationContext();
      context.addParameterValue(
          "startDate", DATE_FORMAT.parse((String) parameterValues.get("startDate")));
      context.addParameterValue(
          "endDate", DATE_FORMAT.parse((String) parameterValues.get("endDate")));
      context.addParameterValue(
          "location",
          Context.getLocationService()
              .getLocation(Integer.parseInt((String) parameterValues.get("locationId"))));

      // getting current report definition
      ReportDefinition currentReportDefinition =
          reportDefinitionService.getDefinitionByUuid(reportUuid);
      StopWatch stopWatch1 = new StopWatch();
      stopWatch1.start();
      // evaluating current report under stopWatch to evaluate execution time
      ReportData currentData = reportDefinitionService.evaluate(currentReportDefinition, context);
      stopWatch1.stop();

      // getting master report definition
      ReportDefinition masterReportDefinition =
          reportDefinitionService.getDefinitionByUuid((String) mappingsObject.get("masterReport"));
      StopWatch stopWatch2 = new StopWatch();
      stopWatch2.start();
      // evaluating current report under stopWatch to evaluate execution time
      ReportData masterData = reportDefinitionService.evaluate(masterReportDefinition, context);
      stopWatch2.stop();

      List<Match> matches = new ArrayList<Match>();
      // getting indicator mappings by keys for the datasets of this indicator matching
      Iterator mappingsIterator = ((JSONArray) mappingsObject.get("mappings")).iterator();
      // getting dataSetKeyAppender, it's set to "" if none
      String dataSetKeyAppender = (String) mappingsObject.get("dataSetKeyAppender");
      // looping through the mappings
      while (mappingsIterator.hasNext()) {
        JSONObject mapping = (JSONObject) mappingsIterator.next();
        // current indicator key
        String currentCode = (String) mapping.get("currentCode");
        // respective master indicator key
        String masterCode = (String) mapping.get("masterCode");
        // indicator mapping name
        String name = (String) mapping.get("name");
        // get current indicator result
        CohortIndicatorAndDimensionResult currentResult =
            getResultFromReportDataByKey(currentCode, currentData, dataSetKeyAppender);
        // get respective master indicator result
        CohortIndicatorAndDimensionResult masterResult =
            getResultFromReportDataByKey(masterCode, masterData, null);
        // set name to: current report definition name = master report definition name
        if (StringUtils.isBlank(name) && masterResult.getDefinition() != null) {
          name =
              currentResult.getDefinition().getName()
                  + "="
                  + masterResult.getDefinition().getName();
        }
        Match match =
            new Match(
                currentCode
                    + "="
                    + masterCode
                    + "["
                    + name
                    + "]", // current indicator key = master indicator key [name]
                currentResult.getValue().intValue(), // current indicator patient count
                masterResult.getValue().intValue()); // master indicator patient count
        // get any patient ids for patients in current indicator not in master
        Set<Integer> currentMemberIds =
            currentResult.getCohortIndicatorAndDimensionCohort().getMemberIds();
        // get any patient ids for patients in master indicator not in current
        Set<Integer> masterMemberIds =
            masterResult.getCohortIndicatorAndDimensionCohort().getMemberIds();
        if (!currentMemberIds.equals(masterMemberIds)) {
          missMatch = true;
          match.setCurrentOffSetPatientIds(symmetricDifference(currentMemberIds, masterMemberIds));
          match.setMasterOffSetPatientIds(symmetricDifference(masterMemberIds, currentMemberIds));
        }
        matches.add(match);
      }
      // generate and add reportResult (report indicator result matching entry) to
      // eptsReportTestResults
      eptsReportTestResults.add(
          new RunResult(
              convertMilliSecondsToHHmmss(stopWatch1.getTotalTimeMillis()),
              convertMilliSecondsToHHmmss(stopWatch2.getTotalTimeMillis()),
              currentReportDefinition.getName() + "#" + currentReportDefinition.getUuid(),
              masterReportDefinition.getName() + "#" + masterReportDefinition.getUuid(),
              (String) mappingsObject.get("currentReportLabel"),
              (String) mappingsObject.get("masterReportLabel"),
              context.getParameterValues(),
              matches));
    }

    // log results to Xlsx file and fail if there are any offSets
    XLSXGenerator.creatResultsMatchingResultsXlsx(
        eptsReportTestResults,
        eptsReportsResultsMatching.getAbsolutePath() + File.separator + XLSX_OUTPUT_NAME);
    Assert.assertFalse(missMatch);
  }

  // convert time in milliseconds to hh:mm:ss format
  private String convertMilliSecondsToHHmmss(long millis) {
    return String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis)
            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }

  /** Get a result for a given indicator from the reportData */
  public CohortIndicatorAndDimensionResult getResultFromReportDataByKey(
      String key, ReportData reportData, String dataSetKeyAppender) {
    for (Map.Entry<String, DataSet> entry : reportData.getDataSets().entrySet()) {
      MapDataSet dataSet = (MapDataSet) entry.getValue();

      if (StringUtils.isNotBlank(dataSetKeyAppender) && key.split(dataSetKeyAppender).length == 2) {
        String[] keys = key.split(dataSetKeyAppender);
        if (entry.getKey().equals(keys[0])) {
          return (CohortIndicatorAndDimensionResult) getKeyValueFromDataset(keys[1], dataSet);
        }
      } else {
        return (CohortIndicatorAndDimensionResult) getKeyValueFromDataset(key, dataSet);
      }
    }
    return null;
  }

  private Object getKeyValueFromDataset(String key, MapDataSet dataSet) {
    DataSetRow data = dataSet.getData();
    for (Iterator<DataSetColumn> i = dataSet.getData().getColumnValues().keySet().iterator();
        i.hasNext(); ) {
      DataSetColumn c = i.next();
      if (c.getName().equals(key)) {
        return data.getColumnValue(c);
      }
    }
    return null;
  }

  /** @return members in a not in b */
  private Set<Integer> symmetricDifference(Set<Integer> a, Set<Integer> b) {
    Set<Integer> aClone = new HashSet<>(a);
    Set<Integer> bClone = new HashSet<>(b);
    aClone.removeAll(bClone);
    return aClone;
  }
}
