# EPTS Reports Module

[![Build Status](https://travis-ci.org/esaude/openmrs-module-eptsreports.svg?branch=master)](https://travis-ci.org/esaude/openmrs-module-eptsreports) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/1889390f9f5246fbb3179fef3f1e2ac8)](https://app.codacy.com/app/esaude-ops/openmrs-module-eptsreports?utm_source=github.com&utm_medium=referral&utm_content=esaude/openmrs-module-eptsreports&utm_campaign=Badge_Grade_Dashboard) [![codecov](https://codecov.io/gh/jembi/openmrs-module-eptsreports/branch/develop/graph/badge.svg)](https://codecov.io/gh/jembi/openmrs-module-eptsreports)


## Description

PEPFAR reports for EPTS project in Mozambique

## Prerequisites

1.  [Install Java and Maven](https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation)

2.  Install git
    -   `sudo apt-get install git`

3.  To setup the OpenMRS SDK, you just need to open up a terminal/console and enter:
    -   `mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:setup-sdk`

## Building from Source

You will need to have Java 1.6+ and Maven 2.x+ installed.  Use the command 'mvn package' to
compile and package the module.  The .omod file will be in the omod/target folder.

Alternatively you can add the snippet provided in the [Creating Modules](https://wiki.openmrs.org/x/cAEr) page to your
omod/pom.xml and use the mvn command:

    mvn package -P deploy-web -D deploy.path="../../openmrs-1.8.x/webapp/src/main/webapp"

It will allow you to deploy any changes to your web
resources such as jsp or js files without re-installing the module. The deploy path says
where OpenMRS is deployed.

## Installation

1.  Build the module to produce the .omod file.
2.  Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder.  (Where ~/.OpenMRS is assumed to be the Application
Data Directory that the running openmrs is currently using.)  After putting the file in there
simply restart OpenMRS/tomcat and the module will be loaded and started.

## Reports Implemented

## MER Reports

| Report                        | Indicators Used          |
| ------------------------------| ------------------------ |
| _PEPFAR MER 2.3 Quarterly_    | Tx_Curr, Tx_New, Tx_Pvls |
| _PEPFAR MER 2.3 Semi-Annual_  | Tb_Prev, Tx_TB, tx_ML    |
| _TX_CURR Report 2.1_          | Tx_Curr2.1               |
| _IM-ER-Report_                | IM-ER2, IM-ER4           |

## MER Indicators

| Indicators   | Description                                                                                                                                                                                                   |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| _Tx_Pvls_    | Percentage of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (&lt;1000 copies/ml). |
| _Tx_Curr_    | Number of adults and children currently receiving antiretroviral therapy (ART). New spec using 30 day period                                                                                                  |
| _Tx_Curr2.1_ | Number of adults and children currently receiving antiretroviral therapy (ART). Using old spec using 60 day period                                                                                            |
| _Tx_New_     | Number of adults and children newly enrolled on antiretroviral therapy (ART).                                                                                                                                 |
| _IM-ER2_     | Implementation of PEPFAR Early Retention Indicators - 2 months                                                                                                                                                |
| _IM-ER4_     | Implementation of PEPFAR Early Retention Indicators - 4 months                                                                                                                                                |
| _TB_PREV_    | Indicator reports the proportion of ART patients who completed a standard course of TB preventive treatment within the semiannual reporting period.                                                           |
| _TX_TB_      | Indicator reports the proportion of ART patients screened for TB in the semiannual reporting period who start TB treatment.                                                                                   |
| _TX_ML_      | Indicator reports the Number of ART patients with no clinical contact since their last expected contact.                                                                                                      |

## How to perform a release

For more information on how to perform a release refer to this [link](https://wiki.openmrs.org/display/docs/Maven+Release+Process)

In summary execute the following:

    mvn release:prepare

If there is a problem, you can rollback using:

    mvn release:rollback
