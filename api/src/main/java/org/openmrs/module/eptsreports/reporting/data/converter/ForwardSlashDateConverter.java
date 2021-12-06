package org.openmrs.module.eptsreports.reporting.data.converter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openmrs.module.reporting.data.converter.DataConverter;

public class ForwardSlashDateConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {

    if (obj != null && obj instanceof Date) {
      return formatDate((Date) obj);
    }
    return "";
  }

  @Override
  public Class<?> getInputDataType() {
    return Date.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }

  public static String formatDate(Date date) {

    Format formatter = new SimpleDateFormat("dd/MM/yyyy");

    return formatter.format(date);
  }
}
