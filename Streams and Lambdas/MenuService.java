public class MenuService {

	public void startApplication() {
		HorseInterface horseServices = new HorseImpl();

		try {
			int numOfHorses = Integer.parseInt(ScanUtils.getUserInput("Insert number of horse: "));
			Horse[] horses = horseServices.createNumberOfHorses(numOfHorses);

			horseServices.loadNameToHorses(horses);
			horseServices.loadWarcryToHorses(horses);
			horseServices.loadAgeToHorses(horses);
			horseServices.loadConditionToHorses(horses);

			horseServices.printHorses(horses);

			int raceDistance = Integer.parseInt(ScanUtils.getUserInput("Race Distance: "));

			Horse[] healthyHorses = horseServices.filterHealthy(horses);
			horseServices.groupHorses(healthyHorses);
			horseServices.printHorses(healthyHorses);

		} catch (Exception e) {
			System.out.println("Exception caught: " + e.getMessage());
		}

	}
}