package org.openmrs.module.eptsreports.reporting.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.openmrs.util.OpenmrsClassLoader;

public class EptsQuerysUtils {

  public static String loadQuery(String filePathName) {

    InputStream inputStream = OpenmrsClassLoader.getInstance().getResourceAsStream(filePathName);

    StringBuilder sb = new StringBuilder();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;

      while ((line = br.readLine()) != null) {
        sb.append(line + " \n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sb.toString();
  }
}
