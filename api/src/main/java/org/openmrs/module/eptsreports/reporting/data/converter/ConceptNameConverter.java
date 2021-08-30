package org.openmrs.module.eptsreports.reporting.data.converter;

import java.io.*;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.util.OpenmrsClassLoader;

public class ConceptNameConverter implements DataConverter {

  @Override
  public Object convert(Object obj) {
    if (obj == null) {
      return "";
    }
    return getConceptNameFromCSV((Integer) obj);
  }

  @Override
  public Class<?> getInputDataType() {
    return String.class;
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }

  private String getConceptNameFromCSV(Integer concept_id) {
    InputStream conceptNames =
        OpenmrsClassLoader.getInstance()
            .getResourceAsStream("metadata/conceptMappingFiles/1087and1088Answers.csv");

    String line = "";
    String cvsSplitBy = ",";
    String headLine = "";
    String name = "";
    String concept = "";
    String valueReturned = "";

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(conceptNames, "UTF-8"));
      headLine = br.readLine();
      while ((line = br.readLine()) != null) {
        String[] records = line.split(cvsSplitBy);
        name = records[1];
        concept = records[0];
        if (Integer.parseInt(concept.trim()) == concept_id) {
          valueReturned = name;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return valueReturned;
  }
}
