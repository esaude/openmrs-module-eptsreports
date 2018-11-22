package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.indicator.CohortIndicator;

import java.util.List;

public abstract class BaseDataSet {
	
	/**
	 * Adds a row to a dataset based on an indicator and a list of column parameters
	 * 
	 * @param cohortDsd the dataset
	 * @param baseName the base columm name
	 * @param baseLabel the base column label
	 * @param indicator the indicator
	 * @param columns the column parameters
	 * @param columnNames the column names
	 */
	protected void addRow(CohortIndicatorDataSetDefinition cohortDsd, String baseName, String baseLabel,
	        Mapped<CohortIndicator> indicator, List<ColumnParameters> columns, List<String> columnNames) {
		int c = 0;
		for (ColumnParameters column : columns) {
			String name = baseName + "-" + columnNames.get(c++);
			String label = baseLabel + " (" + column.getLabel() + ")";
			cohortDsd.addColumn(name, label, indicator, column.getDimensions());
		}
	}
	
	class ColumnParameters {
		
		private String name;
		
		private String label;
		
		private String dimensions;
		
		/**
		 * Default constructor
		 * 
		 * @param name the name
		 * @param label the label
		 * @param dimensions the dimension parameters
		 */
		public ColumnParameters(String name, String label, String dimensions) {
			this.name = name;
			this.label = label;
			this.dimensions = dimensions;
		}
		
		/**
		 * Gets the name
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the label
		 * 
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		
		/**
		 * Gets the dimension parameters
		 * 
		 * @return the dimension parameters
		 */
		public String getDimensions() {
			return dimensions;
		}
	}
}
