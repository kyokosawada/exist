import java.util.Scanner;
import java.io.IOException;

public class MenuService {

    private TableService tableService = new TableService();
    private Scanner scanner = new Scanner(System.in);

    public void startApplication(String fileName) {
        try {
            tableService.loadTableFromFile(fileName);
            tableService.printTable();
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
            System.exit(1);
        }
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== MENU ===");
            System.out.println("[ search ] - Search");
            System.out.println("[ edit ] - Edit");
            System.out.println("[ print ] - Print");
            System.out.println("[ add_row ] - Add Row");
            System.out.println("[ sort ] - Sort");
            System.out.println("[ reset ] - Reset");
            System.out.println("[ x ] - Exit");
            String choice = getUserInput("Choose an action: ");

            switch (choice.toLowerCase()) {
                case "search" -> handleSearch();
                case "edit" -> handleEdit();
                case "print" -> handlePrint();
                case "add_row" -> handleAddRow();
                case "sort" -> handleSort();
                case "reset" -> handleReset();
                case "x" -> exit = true;
                default -> System.out.println("Invalid action. Please try again.");
            }
        }

        scanner.close();
    }
    
    private void handleSearch() {
        String searchTerm = getUserInput("Enter search term: ");

        if (searchTerm.trim().isEmpty()) {
            System.out.println("Search term cannot be empty. Please enter a valid search term.");
            return;
        }

        tableService.searchValue(searchTerm);
    }

    private void handleEdit() {
        try {
            String position = getUserInput("Enter cell position [row,column]: ");

            if (!position.matches("\\d+,\\d+")) {
                System.out.println("Invalid format.");
                return;
            }

            String[] parts = position.split(",");

            int rowIndex = Integer.parseInt(parts[0].trim());
            int columnIndex = Integer.parseInt(parts[1].trim());

            if (rowIndex < 0 || rowIndex >= tableService.getTable().size()) {
                System.out.println("Invalid row index");
                return;
            }

            if (columnIndex < 0 || columnIndex >= tableService.getTable().get(rowIndex).size()) {
                System.out.println("Invalid column index");
                return;
            }

            String editMode = getUserInput("Edit key, value or both? [key/value/both]: ");

            String newKey = "";
            String newValue = "";

            switch (editMode.toLowerCase()) {
                case "key":
                    newKey = getUserInput("Enter new key: ");
                    break;
                case "value":
                    newValue = getUserInput("Enter new value: ");
                    break;
                case "both":
                    newKey = getUserInput("Enter new key: ");
                    newValue = getUserInput("Enter new value: ");
                    break;
                default:
                    System.out.println("Invalid edit mode. Please use 'key', 'value', or 'both'");
                    return;
            }

            tableService.editCell(rowIndex, columnIndex, newKey, newValue, editMode);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid row and column numbers.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        } 
    }

    private void handlePrint() {
        tableService.printTable();
    }

    private void handleAddRow() {
        try {
            String input = getUserInput("Number of cells to add: ");
            int numberOfCells = Integer.parseInt(input);

            if (numberOfCells <= 0) {
                System.out.println("Number of cells must be positive. Please enter a number greater than 0.");
                return;
            }

            tableService.addRow(numberOfCells);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter a valid number.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private void handleSort() {
        try {
            String input = getUserInput("Enter row to sort: ");
            int rowIndex = Integer.parseInt(input);

            if (rowIndex < 0 || rowIndex >= tableService.getTable().size()) {
                System.out.println("Invalid row index.");
                return;
            }

            String order = getUserInput("Sort order [asc/desc]: ");

            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
                System.out.println("Invalid order.");
                return;
            }

            tableService.sortRow(rowIndex, order);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter a valid row number.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private void handleReset() {
        try {
            String dimensions = getUserInput("Enter table dimensions [ROWSxCOLUMNS]: ");

            if (!dimensions.matches("\\d+x\\d+")) {
                System.out.println("Invalid format.");
                return;
            }

            String[] parts = dimensions.split("x");

            int rows = Integer.parseInt(parts[0].trim());
            int columns = Integer.parseInt(parts[1].trim());

            if (rows <= 0 || columns <= 0) {
                System.out.println("Dimensions must be greater than 0.");
                return;
            }

            tableService.resetTable(rows, columns);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers for rows and columns.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private String getUserInput(String input) {
        System.out.print(input);
        return scanner.nextLine().trim();
    }
    
}