public class MenuService {

	public void startApplication() {
		HorseInterface horseServices = new HorseImpl();
        RaceService raceService = new RaceService();

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

            raceService.startRace(healthyHorses, raceDistance);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

	}
}