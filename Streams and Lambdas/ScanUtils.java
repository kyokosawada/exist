import java.util.Scanner;

public final class ScanUtils {
	private static final Scanner scanner = new Scanner(System.in);

	public static String getUserInput(String input) {
        System.out.print(input);
        return scanner.nextLine();
    }

}