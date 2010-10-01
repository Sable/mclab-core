package natlab.toolkits.analysis.isscalar;

public class NonScalar extends IsScalarType {
	IsScalarType[][] array;
	int rows;
	int columns;
	
	public NonScalar(int rows, int columns) {
		this.array = new IsScalarType[rows][columns];
		this.rows = rows;
		this.columns = columns;
	}
	
	protected NonScalar getRow(int row) {
		NonScalar rowNonScalar = new NonScalar(1,columns);
		for (int i = 0; i < columns; i++) {
			rowNonScalar.array[0][i] = array[row][i];
		}
		return rowNonScalar;
	}
	
	protected synchronized void flatten() {
		// TODO only flatten when needed
		int flatRows = 0;
		for (int i = 0; i < rows; i++) {
			IsScalarType row = array[i][0];
			if (row.isScalar()) {
				flatRows += 1;
			}
			else if (row.isNonScalar()) {
				NonScalar nonScalarRow = ((NonScalar) row);
				nonScalarRow.flatten();
				flatRows += nonScalarRow.rows;
			}
			else {
				flatRows = -1;
				break;
			}
			for (int j = 1; j < columns; j++) {
				row = array[i][j];
				if (row.isNonScalar()) {
					NonScalar nonScalarRow = ((NonScalar) row);
					nonScalarRow.flatten();
				}
				else if (row.isBottom() || row.isTop()){
					flatRows = -1;
					break;
				}
			}
			if (flatRows == -1) {
				break;
			}
		}
		int flatColumns = 0;
		if (flatRows != -1) {
			for (int j = 0; j < columns; j++) {
				IsScalarType col = array[0][j];
				if (col.isScalar()) {
					flatColumns += 1;
				}
				else if (col.isNonScalar()) {
					NonScalar nonScalarColumn = ((NonScalar) col);
					nonScalarColumn.flatten();
					flatColumns += nonScalarColumn.columns;
				}
				else {
					flatColumns = -1;
					break;
				}
				for (int i = 1; i < rows; i++) {
					col = array[i][j];
					if (col.isNonScalar()) {
						NonScalar nonScalarColumn = ((NonScalar) col);
						nonScalarColumn.flatten();
					}
					else if (col.isBottom() || col.isTop()){
						flatColumns = -1;
						break;
					}
				}
				if (flatColumns == -1) {
					break;
				}
			}
		}
		if (flatRows == -1 || flatColumns == -1) {
			rows = 0;
			columns = 0;
			array = new IsScalarType[0][0];
		}
		else {
			IsScalarType[][] newArray = new IsScalarType[flatRows][flatColumns];
			int newRow = 0;
			for (int i = 0; i < rows; i++) {
				int newCol = 0;
				for (int j = 0; j < columns; j++) {
					IsScalarType element = array[i][j];
					// check whether newArray contains a splitted row from a already discovered nested element
					IsScalarType newElement = newArray[newRow][newCol];
					if (newElement != null) {
						if (newElement.isScalar()) {
							// leap over this splitted single-column row 
							newCol++;
						}
						else {
							// copy elements from splitted row
							NonScalar splittedRow = ((NonScalar) newElement);
							for (int l = 0; l < splittedRow.columns; l++) {
								newArray[newRow][newCol + l] = splittedRow.array[newRow][l];
							}
							// leap over this splitted row
							newCol += splittedRow.columns;
						}
					}
					else {
						if (element.isNonScalar()) {
							NonScalar nonScalarElement = (NonScalar) element;
							// flatten first row of nonScalarElement
							for (int l = 0; l < nonScalarElement.columns; l++) {
								newArray[newRow][newCol + l] = nonScalarElement.array[0][l];
							}
							// split remaining rows and already store them in newArray
							for (int k = 1; k < nonScalarElement.rows; k++) {
								newArray[newRow + k][newCol] = nonScalarElement.getRow(newRow + k);
							}
							// leap over those splitted rows
							newCol += nonScalarElement.columns;
						}
						else {
							newArray[newRow][newCol] = element;
							newCol++;
						}
					}
				}
				newRow++;
			}
			rows = flatRows;
			columns = flatColumns;
			array = newArray;
		}
	}
	
	protected boolean hasSameDimensions(NonScalar obj) {
		return (rows == obj.rows && columns == obj.columns);
	}
	
	@Override
	public String toString() {
		return "nonscalar[" + rows + "," + columns + "]";
	}
}
