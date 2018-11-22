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
	 */
	protected void addRow(CohortIndicatorDataSetDefinition cohortDsd, String baseName, String baseLabel,
	        Mapped<CohortIndicator> indicator, List<ColumnParameters> columns) {
		
		for (ColumnParameters column : columns) {
			String name = baseName + "-" + column.getColumn();
			String label = baseLabel + " (" + column.getLabel() + ")";
			cohortDsd.addColumn(name, label, indicator, column.getDimensions());
		}
	}
	
	class ColumnParameters {
		
		private String name;
		
		private String label;
		
		private String dimensions;
		
		private String column;
		
		/**
		 * Default constructor
		 * 
		 * @param name the name
		 * @param label the label
		 * @param dimensions the dimension parameters
		 */
		public ColumnParameters(String name, String label, String dimensions, String column) {
			this.name = name;
			this.label = label;
			this.dimensions = dimensions;
			this.column = column;
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
		
		/**
		 * Gets the column
		 * 
		 * @return the column
		 */
		public String getColumn() {
			return column;
		}
	}
}
