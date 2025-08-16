import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class HorseImpl implements HorseInterface {

	Random random = new Random();

    private static final String[] HORSE_NAMES = {
        "Thunder", "Lightning", "Storm", "Blaze", "Spirit",
        "Midnight", "Champion", "Starlight", "Apollo", "Phoenix",
        "Shadow", "Fire", "Wind", "Star", "Comet"
    };

	private static final String[] HORSE_WARCRIES = {
	    "Neigh neigh NEIGHHHH!",
	    "Whinny-wheee-snort!",
	    null,
	    "Pfffff! Stomp stomp!",
	    "Brrrr-neigh-neigh!",
	    "Whicker whicker wheee!",
	    "Snort-pff-NEIGHHHH!",
	    "Whinny whinny ha-ha!",
	    "Neigh! Pfffff! Wheee!",
	    null,
	    "Brrr-brrr-SNORT!",
	    "Whicker-neigh-stomp!",
	    "Pffffffffff-wheee!"
	};

	@Override
	public Horse[] createNumberOfHorses(int numOfHorses) {
		return IntStream.range(0, numOfHorses)
						.mapToObj(i -> new Horse())
						.toArray(Horse[]::new);
	}

	@Override
	public void loadNameToHorses(Horse[] horses) {
		Map<String, Integer> uniqueNames = new HashMap<>();
		Arrays.stream(horses)
				 .forEach(horse -> {
				 	int randomIndex = random.nextInt(HORSE_NAMES.length);
				 	String horseName = HORSE_NAMES[randomIndex];
				 	int counter = uniqueNames.getOrDefault(horseName, 0) + 1;

				 	uniqueNames.put(horseName, counter);
				 	String finalName = counter == 1 ? horseName : horseName + counter;
				 	horse.setName(finalName);
				 });
	}

	@Override
	public void loadWarcryToHorses(Horse[] horses) {
		Arrays.stream(horses)
				 .forEach(horse -> {
				 	int randomIndex = random.nextInt(HORSE_WARCRIES.length);
				 	String horseWarcry = HORSE_WARCRIES[randomIndex];

				 	Optional.ofNullable(horseWarcry)
				 			.ifPresentOrElse(
				 				warcry -> {
				 					horse.setWarcry(warcry);
				 				},
				 				() -> {
				 					horse.setWarcry("No warcry");
				 				}
				 			);
				 });
	}

	@Override
	public void loadAgeToHorses(Horse[] horses) {
		Arrays.stream(horses)
				 .forEach(horse -> {
				 	int randomAge = random.nextInt(8) + 5;
				 	horse.setAge(randomAge);
				 });
	}

	@Override
	public void loadConditionToHorses(Horse[] horses) {
		Arrays.stream(horses)
				 .forEach(horse -> {
				  	boolean isHealthy = random.nextBoolean();
                 	horse.setHealthy(isHealthy);
				 });
	}

	@Override
	public void printHorses(Horse[] horses) {
		IntStream.range(0, horses.length)
				 .forEach(i -> {
				 	System.out.println("Horse " + (i+1) + " information -----------------");
				 	System.out.println("Name: " + horses[i].getName());
				 	System.out.println("Warcry: " + horses[i].getWarcry());
				 	System.out.println("Age: " + horses[i].getAge());
				 	System.out.println("Condition: " + (horses[i].isHealthy() ? "Healthy" : "Not healthy"));
				 	System.out.println("");
				 });
	}

	@Override
	public Horse[] filterHealthy(Horse[] horses) {
		return Arrays.stream(horses)
					.filter(Horse::isHealthy)
                 	.toArray(Horse[]::new);   
	}

	@Override
	public void groupHorses(Horse[] horses) {
		Map<Integer, Long> horseAges = Arrays.stream(horses)
        	.collect(Collectors.groupingBy(
            	Horse::getAge,
            	Collectors.counting()
        	));

    	int mostFrequentAge = horseAges.entrySet().stream()
	        .max(Map.Entry.comparingByValue())
	        .map(Map.Entry::getKey)
	        .orElse(0);

		double averageAge = Arrays.stream(horses)
		    .mapToInt(Horse::getAge)
		    .average()
		    .orElse(0.0);

		Arrays.stream(horses)
			  .forEach(horse -> {
			  	horse.setGroup(
			  		horse.getAge() == mostFrequentAge ? "advanced" :
			  		horse.getAge() >= averageAge ? "intermediate" : "beginner"
			  	);
			  });
	}

}