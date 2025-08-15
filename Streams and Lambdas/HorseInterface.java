interface HorseInterface {

	Horse[] createNumberOfHorses(int numOfHorses);

	void loadNameToHorses(Horse[] horses);

	void loadWarcryToHorses(Horse[] horses);

	void loadAgeToHorses(Horse[] horses);

	void loadConditionToHorses(Horse[] horses);

	void printHorses(Horse[] horses);

	Horse[] filterHealthy(Horse[] horses);

	void groupHorses(Horse[] horses);

}