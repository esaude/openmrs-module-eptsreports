# EPTS Reports Results Matcher
> `ResultsMatchingTest.java` is an openmrs module sensitive context junit test that accesses an OpenMRS database and runs and matches results of a master report against its respective configured current report in `eptsReportsResultsMatchingConfig.json`

### Setup
  - Disable @Ignore off `ResultsMatchingTest.java`
  - Ensure your Mysql server with your openmrs database is running!
  - Setup Mysql connection details in `~/.OpenMRS/openmrs-runtime.properties` or its equivalent, see sample below;
  ```
  #Last updated by the OpenMRS Standalone application.
  #Wed Feb 06 15:12:50 EAT 2019
  connection.username=openmrs
  connection.url=jdbc:mysql://127.0.0.1:3318/openmrs
  connection.password=test
  ```
  - Configurations are stored in `eptsReportsResultsMatchingConfig.json`, to use different configurations than the default ones, add a file named the same way into `~/eptsReportsResultsMatching` folder
  
### Running
> Run `ResultsMatchingTest.java` as you would any junit test, we used Intellij IDEA; this will run all current reports and their FGH equivalents from the configuration, match results and generate an output

### Output
> Results for each report are stored as `RunResult.java`, a list of these are generated and logged onto `~/eptsReportsResultsMatching/jembiAgainstFGHMatches.xlsx`. Each result contains;
- Current report and and uuid
- Master report name and uuid
- Time in milliseconds taken to run either of the 2 reports above
- Matches (Each match contains number of patients per indicator, number differences between the 2 reports, different patient ids per indicator if any)

### Understanding the configuration
> Everything you need to have the utility perform is trigged from the single `eptsReportsResultsMatchingConfig.json`. let us explain its fields below;
```
{
  "user":{
    "name":"admin", // openmrs instance user name
    "pass":"eSaude123" // openmrs instance password
  },
  "indicatorMappings":[ // each report to support should have its own indicatorMapping entry in here
    {
      "6febad76-472b-11e9-a41e-db8c77c788cd":{ // e.g. the jembi report uuid
        "masterReport":"0ccad317-aa2c-4805-9935-ad05fdc1d029", // e.g. its equivalent FGH report uuid
        "currentReportLabel": "Jembi", // current report label/name
        "masterReportLabel": "FGH", // master report label or name
        "parameterValues":{
          "startDate":"2018-09-20", // startDate(yyyy-MM-dd) to run the 2 reports with
          "endDate":"2019-06-01", // endDate(yyyy-MM-dd) to run the 2 reports with
          "locationId":"215" // Location to run the 2 reports with
        },
        "dataSetKeyAppender":  "\\.", // Our Jembi report indicators have keys of names (<dataSetKey.indicatorKey>), this field helps us configure the separator incase we ever change it or set to "" for reports without this kind of indicator namings, '\\' are escape characters. This is not used for master/fgh indicators
        "mappings":[ // this is where the indicators from the Jembi report are matched against the FGH report
          {
            "masterCode":"TBSC_ALL", // master indicator code
            "currentCode":"T.TXB_DEN", // Respective current indicator code
            "name":"" // brief name of the indicator, if set to ""; the utility sets it to (<currentCode=masterCode[jembiIndicatorName=FGHIndicatorName]>)
          }
        ]
      }
    }
  ]
}
```
'//' have been used as comment blocks for explanations in the configuration above
