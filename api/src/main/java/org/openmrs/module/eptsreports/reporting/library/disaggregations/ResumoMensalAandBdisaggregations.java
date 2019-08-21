/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.disaggregations;

import java.util.Arrays;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet.ColumnParameters;
import org.springframework.stereotype.Component;

@Component
public class ResumoMensalAandBdisaggregations {

  public List<ColumnParameters> getUnder14YearsColumns() {
    ColumnParameters under4years =
        new ColumnParameters("under4Years", "under 4 years patients", "age=0-4", "01");
    ColumnParameters fiveTo9years =
        new ColumnParameters("fiveTo9years", "5 to  9 years patients", "age=5-9", "02");
    ColumnParameters tenTo14yearsMale =
        new ColumnParameters(
            "tenTo14yearsMale", "10 to  14 years male patients", "gender=M|age=10-15", "03");
    ColumnParameters tenTo14yearsFemale =
        new ColumnParameters(
            "tenTo14yearsFemale", "10 to  14 years female patients", "gender=F|age=10-15", "04");
    ColumnParameters under54yearsTotal =
        new ColumnParameters("under54yearsTotal", "Under 15 years patients - Total", "0-15", "05");

    return Arrays.asList(
        under4years, fiveTo9years, tenTo14yearsMale, tenTo14yearsFemale, under54yearsTotal);
  }

  public List<ColumnParameters> getAdultPatients() {
    ColumnParameters fifteenTo19YearsM =
        new ColumnParameters(
            "fifteenTo19YearsM", "15 To 19 years male patients", "gender=M|age=15-19", "01");
    ColumnParameters fifteenTo19YearsF =
        new ColumnParameters(
            "fifteenTo19YearsF", "15 To 19 years female patients", "gender=F|age=15-19", "02");
    ColumnParameters above20YearsM =
        new ColumnParameters(
            "above20YearsM", "Above 20 years male patients", "gender=M|age=20+", "03");
    ColumnParameters above20YearsF =
        new ColumnParameters(
            "above20YearsF", "Above 20 years female patients", "gender=F|age=20+", "04");
    ColumnParameters adultsTotal =
        new ColumnParameters("adultsTotal", "Adults patients", "age=15+", "05");

    return Arrays.asList(
        fifteenTo19YearsM, fifteenTo19YearsF, above20YearsM, above20YearsF, adultsTotal);
  }

  public List<ColumnParameters> getAdolescentesColumns() {
    ColumnParameters tenTo14Male =
        new ColumnParameters("tenTo14Male", "10 to 14 years male", "gender=M|age=10-15", "01");
    ColumnParameters tenTo14Female =
        new ColumnParameters("tenTo14Female", "10 to 14 years female", "gender=F|age=10-15", "02");
    ColumnParameters fifteenTo19Female =
        new ColumnParameters(
            "fifteenTo19Female", "15 to 19 years female", "gender=F|age=15-19", "03");
    ColumnParameters fifteenTo19Male =
        new ColumnParameters("fifteenTo19Male", "15 to 19 years male", "gender=M|age=15-19", "04");
    ColumnParameters adolescentTotals =
        new ColumnParameters("adolescentTotals", "15 to 19 years totals", "age=15-19", "05");

    return Arrays.asList(
        tenTo14Male, tenTo14Female, fifteenTo19Female, fifteenTo19Male, adolescentTotals);
  }
}
