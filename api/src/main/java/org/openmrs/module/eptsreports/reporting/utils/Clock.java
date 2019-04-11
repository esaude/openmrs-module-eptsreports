package org.openmrs.module.eptsreports.reporting.utils;

import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class Clock {

  public Date getCurrentDate() {
    return new Date();
  }
}
