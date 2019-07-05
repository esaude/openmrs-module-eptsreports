package org.openmrs.module.eptsreports.reporting.library.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientState;
import org.openmrs.PersonAttribute;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.SimpleResult;
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
    if (value instanceof Boolean) {
      return (Boolean) value ? "Yes" : "No";
    } else if (value instanceof Date) {
      return formatDate((Date) value);
    } else if (value instanceof Concept) {

      return ((Concept) value).getName();
    } else if (value instanceof String) {
      return value.toString();
    } else if (value instanceof Double) {
      return ((Double) value);
    } else if (value instanceof Integer) {
      return ((Integer) value);
    } else if (value instanceof Location) {
      return ((Location) value).getName();
    } else if (value instanceof SimpleResult) {
      return ((SimpleResult) value).getValue();
    } else if (value instanceof PersonAttribute) {
      return ((PersonAttribute) value).getValue();
    } else if (value instanceof Patient) {
      if (what.equals("F")) {
        return formatDateTime(((Patient) value).getDateCreated());
      } else if (what.equals("L")) {
        return formatDateTime(((Patient) value).getDateChanged());
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
    } else if (value instanceof PatientState) {
      if (what.equals("State")) {
        return ((PatientState) value).getState().getConcept();
      }
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

  private String formatDate(Date date) {
    DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    return date == null ? "" : dateFormatter.format(date);
  }

  private String formatDateTime(Date date) {
    DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return date == null ? "" : dateFormatter.format(date);
  }
}
