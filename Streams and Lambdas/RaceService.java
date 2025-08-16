import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class RaceService {

    private final Random random = new Random();

    public void startRace(Horse[] healthyHorses, int raceDistance) {
        System.out.println("\n" + healthyHorses.length + " Qualified Horses (" +
                Arrays.stream(healthyHorses)
                        .map(Horse::getName)
                        .collect(Collectors.joining(", ")) + ")");

        Map<String, Integer> horseDistances = Arrays.stream(healthyHorses)
                .collect(Collectors.toMap(Horse::getName, horse -> 0));

        countdown();

        List<Horse> finishedHorses = new ArrayList<>();
        int raceRound = 0;

        while (finishedHorses.size() < healthyHorses.length) {
            raceRound++;
            final int currentRound = raceRound; 

            Arrays.stream(healthyHorses)
                    .parallel()
                    .filter(horse -> {
                        return finishedHorses.stream()
                                .noneMatch(finished -> finished.getName().equals(horse.getName()));
                    })
                    .forEach(horse -> {
                        String horseName = horse.getName();

                        int speed = generateSpeed(horse, currentRound);

                        int newDistance = horseDistances.get(horseName) + speed;
                        horseDistances.put(horseName, newDistance);

                        int remaining = Math.max(0, raceDistance - newDistance);

                        if (newDistance >= raceDistance) {
                            System.out.println(horseName + " ran " + speed + ", " + horseName + " finished the Race!");
                            System.out.println(horse.getWarcry());
                            finishedHorses.add(horse);
                        } else {
                            System.out.println(horseName + " ran " + speed + " remaining " + remaining);
                        }
                    });

            ThreadUtils.sleep(300);
        }

        System.out.println("\nRACE FINISHED!");
        displayResults(finishedHorses);
    }

    private void countdown() {
        Stream.of(3, 2, 1)
                .forEach(count -> {
                    System.out.println("\nRace Starting in " + count + "...");
                    ThreadUtils.sleep(1000);
                });
        System.out.println("GO!!!\n");
    }

    private int generateSpeed(Horse horse, int raceRound) {
        int baseSpeed = random.nextInt(10) + 1;

        return switch (horse.getGroup()) {
            case "advanced" -> raceRound >= 3 ? random.nextInt(6) + 5 : baseSpeed;
            case "intermediate" -> raceRound >= 5 ? (int) Math.ceil(baseSpeed * 1.1) : baseSpeed;
            case "beginner"-> baseSpeed;
            default -> baseSpeed;
        };
    }

    private void displayResults(List<Horse> finishedHorses) {
        System.out.println("\n=== FINAL RANKINGS ===");
        IntStream.range(0, finishedHorses.size())
                .forEach(i -> System.out.println((i + 1) + " - " + finishedHorses.get(i).getName()));
    }

}