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
public class ResumoTrimestralAgeAndGenderDisaggregations {

  public List<ColumnParameters> getUnder14YearsColumns() {

    ColumnParameters under15YearMale =
        new ColumnParameters(
            "under15YearMale", "Under 15 years patients male patients", "gender=M|age=0-14", "01");
    ColumnParameters under15YearFemale =
        new ColumnParameters(
            "under15YearFemale",
            "Under 15 years patients female patients",
            "gender=F|age=0-14",
            "02");
    ColumnParameters under15YearTotal =
        new ColumnParameters("under15YearTotal", "Under 15 years Total", "age=0-14", "03");
    return Arrays.asList(under15YearMale, under15YearFemale, under15YearTotal);
  }

  public List<ColumnParameters> get8To14YearsColumns() {

    ColumnParameters to14YearMale =
        new ColumnParameters(
            "to14YearMale", "8 to 14 years patients male patients", "gender=M|age=8-14", "01");
    ColumnParameters to14YearFemale =
        new ColumnParameters(
            "to14YearFemale", "8 to 14 years female patients", "gender=F|age=8-14", "02");
    ColumnParameters to14YearTotal =
        new ColumnParameters("to14YearTotal", "8 to 14 years Total", "age=8-14", "03");
    return Arrays.asList(to14YearMale, to14YearFemale, to14YearTotal);
  }

  public List<ColumnParameters> getAdultPatients() {

    ColumnParameters over14YearsMale =
        new ColumnParameters(
            "over14YearsMale", "Over 14 years patients male patients", "gender=M|age=15+", "01");
    ColumnParameters over14YearsFemale =
        new ColumnParameters(
            "over14YearsFemale",
            "Over 14 years patients female patients",
            "gender=F|age=15+",
            "02");
    ColumnParameters over14YearsTotal =
        new ColumnParameters("over14YearsTotal", "Over 14 years Total", "age=15+", "03");
    return Arrays.asList(over14YearsMale, over14YearsFemale, over14YearsTotal);
  }
}
