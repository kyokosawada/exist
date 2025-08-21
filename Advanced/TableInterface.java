import java.io.IOException;

interface TableInterface {

	void loadTableFromFile(String fileName) throws IOException;

	void searchValue(String searchTerm);

	int countOccurrences(String text, String searchTerm);

	void editCell(int rowIndex, int columnIndex, String newKey, String newValue, String editMode) throws IOException;

	void addRow(int numberOfCells) throws IOException;

	void sortRow(int rowIndex, String order) throws IOException;

	void resetTable(int rows, int columns) throws IOException;

	void printTable();

	Table getTable();
}