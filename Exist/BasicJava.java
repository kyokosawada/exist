// Scanner package
import java.util.Scanner;
import java.util.regex.Pattern;


public class BasicJava {
	public static void main(String [] args) {
		
		Scanner scanner = new Scanner(System.in);
        TableGenerator table = new TableGenerator();
      
        table.startTableGeneration();
        boolean exit = false;

        while(!exit){
        	System.out.println("\nMENU:");
        	System.out.println("[ 1 ] - Search");
        	System.out.println("[ 2 ] - Edit");
        	System.out.println("[ 3 ] - Print");
        	System.out.println("[ 4 ] - Reset");
        	System.out.println("[ x ] - Exit");
        	System.out.println("Choose an action: ");

        	String action = scanner.nextLine();

        	switch (action){
        		case "2" -> table.editValue();
        		case "3" -> table.printTable();
        		case "4" -> table.resetTable();
        		case "x" -> exit = true; 
        		default -> System.out.println("wla p");			

        	}


        }

   

	}
}

public class TableGenerator {
	// DECLARE string array to hold the locations of the ASCII strings
	private String [][] table;

	// DECLARE string to hold the randomly generated ascii
	private String generatedAscii;

	// DECLARE string to hold dimension of the Table
	private int row;
	private int column;

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
				for(int k = 0; k < 3; k++){
					// Ascii generator
					int randomAscii = (int)(Math.random() * 95) + 33;
					char randomChar = (char) randomAscii;
					generatedAscii += randomChar;
				}
				//Set string to the location of string array
				table[i][j] = generatedAscii;
			}
			
		} 

	}
		

	//DECLARE a method to print the strings array from the array
	public void printTable(){		
		for(int i = 0; i < row; i++){
			for(int j = 0; j < column; j++)
				System.out.print("{ " + table[i][j] + " }");	
			System.out.println("");
		}
	}

	public void resetTable(){
		startTableGeneration();

	}

	public void startTableGeneration(){
		Scanner scanner = new Scanner(System.in);

		System.out.println("Input table dimensions: ");
        String dimensions = scanner.nextLine();        
        System.out.println("You entered: " + dimensions);

        if(validateDimensions(dimensions)){
        	setTableDimension(dimensions);
        	generateTable();
        }
        else{
        	startTableGeneration();
        }
	}
	
	public boolean validateDimensions(String dimensions){
		return Pattern.matches("\\d+x\\d+", dimensions);
	}
	public boolean validateTableIndex(String tableIndex){
		if(Pattern.matches("\\d+,\\d+", tableIndex)){
			String[] index = tableIndex.split(",");
			System.out.println(Integer.parseInt(index[0]));
			if(Integer.parseInt(index[0]) < row )
				if(Integer.parseInt(index[1]) < column)
					return true;
		}
		return false;
	}

	public void editValue(){
		Scanner scanner = new Scanner(System.in);

		System.out.println("Input table index (row,column): ");
        String tableIndex = scanner.nextLine();        
  
        if(validateTableIndex(tableIndex)){
        	
        }
        else{
        	System.out.println("Invalid index format");
        }
 

	}

	
}	


