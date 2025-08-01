// Scanner package
import java.util.Scanner;
import java.util.regex.Pattern;


public class BasicJava {
	public static void main(String [] args) {
		
		Scanner scanner = new Scanner(System.in);
        Table table = new Table();
      	

        table.startTableGeneration();
        boolean exit = false;

        while(!exit){
        	System.out.println("\nMENU:");
        	System.out.println("[ 1 ] - Search");
        	System.out.println("[ 2 ] - Edit");
        	System.out.println("[ 3 ] - Print");
        	System.out.println("[ 4 ] - Reset");
        	System.out.println("[ x ] - Exit");
        	System.out.print("Choose an action: ");

        	String action = scanner.nextLine();

        	switch (action){
        		case "1" -> table.searchValue();
        		case "2" -> table.editValue();
        		case "3" -> table.printTable();
        		case "4" -> table.resetTable();
        		case "x" -> exit = true; 
        		default -> System.out.println("Invalid action");			

        	}
        }  
	}
}

public class Table {
	// DECLARE string array to hold the locations of the ASCII strings
	private String [][] table;

	// DECLARE string to hold the randomly generated ascii
	private String generatedAscii;

	// DECLARE string to hold dimension of the Table
	private int row;
	private int column;

	// DECLARE the constants for the ascii
	private static final int ASCII_STRING_LENGTH = 3;
    private static final int ASCII_MIN = 33;
    private static final int ASCII_RANGE = 94;
    private static final int MAX_DIMENSION = 100;

	public boolean validateDimensions(String dimensions){

	    if (Pattern.matches("\\d+x\\d+", dimensions)) {
	        String[] parts = dimensions.split("x");

	        try{
		        int inputRow = Integer.parseInt(parts[0]);
		        int inputColumn = Integer.parseInt(parts[1]);
		        
		        return inputRow > 0 && inputColumn > 0 && inputRow <= MAX_DIMENSION && inputColumn <= MAX_DIMENSION;
	        }
	        catch(NumberFormatException e){
	        	return false;
	        }
	    }
	    return false;
	}

	public boolean validateTableIndex(String tableIndex){
	    if(Pattern.matches("\\d+,\\d+", tableIndex)){
	    	String[] parts = tableIndex.split(",");
	    	try {
		        Integer.parseInt(parts[0]); 
		        Integer.parseInt(parts[1]);  

		        return true;
		    }
	    	catch(NumberFormatException e) {
	        	return false;  
	    	}
	    }
	    else{
	    	return false;
	    }    
	}


	// DECLARE a method to set dimension of the table
	public void setTableDimension(String dimensions) {

		String[] dim = dimensions.split("x");

		row = Integer.parseInt(dim[0]);
		column = Integer.parseInt(dim[1]);

		table = new String[row][column];
	}

	// DECLARE nested for loop dimension array times
	public void generateTable (){
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++){
				generatedAscii = "";
				// DECLARE for loop 3 times to put random generated ascii to declared string
				for(int k = 0; k < ASCII_STRING_LENGTH; k++){
					// Ascii generator
					int randomAscii = (int)(Math.random() * ASCII_RANGE) + ASCII_MIN;
					char randomChar = (char) randomAscii;
					generatedAscii += randomChar;
				}
				//Set string to the location of string array
				table[i][j] = generatedAscii;
			}
		} 
	}
		
	public void startTableGeneration(){
   		Scanner scanner = new Scanner(System.in);

	    while(true) {
	        System.out.print("Input table dimensions (ROWxCOLUMN): ");
	        String dimensions = scanner.nextLine();
	        
	        if(validateDimensions(dimensions)) {
	            setTableDimension(dimensions);
	            generateTable();
	            break;
	        } else {
	            System.out.println("Enter proper format (ROWxCOLUMN)");
	        }
	    }
	}
 
	//DECLARE a method to print the strings array from the array
	public void searchValue(){
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter value to search: ");
		String valueSearched = scanner.nextLine();
	 
	    if(valueSearched.isEmpty()){
	        System.out.println("Search value can't be empty");
	        return;
	    }

	    boolean found = false;
    
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++){
				int occurrences = 0;
				int index = 0;

				while((index = table[i][j].indexOf(valueSearched, index)) != -1) {
                	occurrences++;
                	index++;
           	 	}

           	 	if(occurrences > 0){
           	 		found = true;
           	 		System.out.println(occurrences + " Occurence/s at " + "( " + i + "," + j + " )");
           	 	}
			}
		}
		if(!found){
			System.out.println("No occurence/s found in the table");
		}
	}	

	public void editValue(){
		int rowToEdit;
		int columnToEdit;

		Scanner scanner = new Scanner(System.in);
		System.out.print("Input table index (ROW,COLUMN): ");
		String tableIndex = scanner.nextLine();  
              
        if(validateTableIndex(tableIndex)){

        	String[] index = tableIndex.split(",");

        	
        	rowToEdit = Integer.parseInt(index[0]);
	        columnToEdit = Integer.parseInt(index[1]);

			if(rowToEdit < row){
				if(columnToEdit < column){
					System.out.print("Enter new value for { " + table[rowToEdit][columnToEdit] + " }: ");
					String newValue = scanner.nextLine();  
					table[rowToEdit][columnToEdit] = newValue;
				}
				else
					System.out.println("Enter proper column index");
			}
			else
				System.out.println("Enter proper row index");
        }
        else{
        	System.out.println("Enter proper format (ROW,COLUMN) or number too large");
        }
	}

	public void printTable(){		
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++)
				System.out.print("{ " + table[i][j] + " }");	
			System.out.println("");
		}
	}

	public void resetTable(){
		Scanner scanner = new Scanner(System.in);

		System.out.print("Input table dimensions (ROWxCOLUMN): ");
        String dimensions = scanner.nextLine(); 

		if(validateDimensions(dimensions)){
        	setTableDimension(dimensions);
        	generateTable();
        }
        else{
       	    System.out.println("Enter proper format (ROWxCOLUMN)");
        }
	}

}	


