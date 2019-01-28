EPTS Reports Module
==========================

[![Build Status](https://travis-ci.org/esaude/openmrs-module-eptsreports.svg?branch=master)](https://travis-ci.org/esaude/openmrs-module-eptsreports)

[![Coverage Status](https://coveralls.io/repos/github/esaude/openmrs-module-eptsreports/badge.svg?branch=master)](https://coveralls.io/github/esaude/openmrs-module-eptsreports?branch=master)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1889390f9f5246fbb3179fef3f1e2ac8)](https://app.codacy.com/app/esaude-ops/openmrs-module-eptsreports?utm_source=github.com&utm_medium=referral&utm_content=esaude/openmrs-module-eptsreports&utm_campaign=Badge_Grade_Dashboard)

Description
-----------
PEPFAR reports for EPTS project in Mozambique

Prerequisites
-------------

1. [Install Java and Maven](https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation)
2. Install git
   - `sudo apt-get install git`
3. To setup the OpenMRS SDK, you just need to open up a terminal/console and enter:
   - `mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:setup-sdk`

Building from Source
--------------------
You will need to have Java 1.6+ and Maven 2.x+ installed.  Use the command 'mvn package' to
compile and package the module.  The .omod file will be in the omod/target folder.

Alternatively you can add the snippet provided in the [Creating Modules](https://wiki.openmrs.org/x/cAEr) page to your
omod/pom.xml and use the mvn command:

    mvn package -P deploy-web -D deploy.path="../../openmrs-1.8.x/webapp/src/main/webapp"

It will allow you to deploy any changes to your web
resources such as jsp or js files without re-installing the module. The deploy path says
where OpenMRS is deployed.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder.  (Where ~/.OpenMRS is assumed to be the Application
Data Directory that the running openmrs is currently using.)  After putting the file in there
simply restart OpenMRS/tomcat and the module will be loaded and started.

Reports Implemented
-------------------

## MER Reports

|Report                 |Indicators Used                                  |
|-----------------------|-------------------------------------------------|
|*PEPFAR MER 2.3 Quarterly*    |Tx_Curr, Tx_New, Tx_Pvls                         |
|*TX_CURR Report 2.1*   |Tx_Curr2.1                                          |

## MER Indicators

|Indicators |Description
|-----------|-----------
|*Tx_Pvls*  |Percentage of ART patients with a viral load result documented in the medical record and/or laboratory information systems (LIS) within the past 12 months with a suppressed viral load (<1000 copies/ml).
|*Tx_Curr*  |Number of adults and children currently receiving antiretroviral therapy (ART). New spec using 30 day period
|*Tx_Curr2.1*  |Number of adults and children currently receiving antiretroviral therapy (ART). Using old spec using 60 day period
|*Tx_New*   |Number of adults and children newly enrolled on antiretroviral therapy (ART).

How to perform a release
-------------------------

For more information on how to perform a release refer to this [link](https://wiki.openmrs.org/display/docs/Maven+Release+Process)

In summary execute the following:

```
mvn release:prepare
```

If there is a problem, you can rollback using:
```
mvn release:rollback
```