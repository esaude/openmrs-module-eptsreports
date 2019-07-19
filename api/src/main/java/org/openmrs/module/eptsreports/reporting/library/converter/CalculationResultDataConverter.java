package org.openmrs.module.eptsreports.reporting.library.converter;

import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.formatDate;
import static org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils.formatDateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.openmrs.Patient;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class CalculationResultDataConverter implements DataConverter {

  private String what;

  public String getWhat() {
    return what;
  }

  public void setWhat(String what) {
    this.what = what;
  }

  public CalculationResultDataConverter() {}

  public CalculationResultDataConverter(String what) {
    this.what = what;
  }

  @Override
  public Object convert(Object obj) {

    if (obj == null) {
      return "";
    }

    Object value = ((CalculationResult) obj).getValue();
    if (value instanceof Patient) {
      if (what.equals("F")) {
        return formatDateTime(((Patient) value).getDateCreated());
      } else if (what.equals("L")) {
        return formatDateTime(((Patient) value).getDateChanged());
      } else if (what.equals("deathDate") && ((Patient) value).getDeathDate() != null) {
        return formatDateTime(((Patient) value).getDeathDate());
      }
    } else if (value instanceof TreeMap) {
      Map.Entry<Date, String> lastEntry = ((TreeMap) value).lastEntry();
      String entry = "";
      if (lastEntry != null && what.equals("PC")) {
        entry = lastEntry.getValue();
      } else if (lastEntry != null && what.equals("PD")) {
        entry = formatDate(lastEntry.getKey());
      }
      Map<Date, String> hashedResults =
          new HashMap<Date, String>((Map<? extends Date, ? extends String>) value);
      if (hashedResults.size() > 0) {
        // loop through and get the PTV/ETV program enrollment
        for (Map.Entry<Date, String> outResults : hashedResults.entrySet()) {
          if (outResults.getValue().equals("PC4") && what.equals("PTVD")) {
            entry = formatDate(outResults.getKey());
            break;
          }
        }
      }
      return entry;
    }

    return null;
  }

  @Override
  public Class<?> getInputDataType() {
    return CalculationResult.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }
}
