package org.openmrs.module.eptsreports.reporting.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component("testsHelper")
public class TestsHelper {

  public SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.s");

  /** Parses date from string using {@link TestsHelper#DATE_FORMAT} */
  public Date getDate(String dateString) {
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * @param startDate
   * @param endDate
   * @param dateInQuestion
   * @return whether dateInQuestion is between startDate and endDate
   */
  public boolean dateBetween(Date startDate, Date endDate, Date dateInQuestion) {
    return startDate == null || endDate == null || dateInQuestion == null
        ? false
        : (dateInQuestion.equals(startDate)
            || dateInQuestion.equals(endDate)
            || (startDate.compareTo(dateInQuestion) * dateInQuestion.compareTo(endDate) >= 0));
  }
}
