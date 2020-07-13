package FightPredictor.util;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class BaseGameConstants {

    public static final Map<Integer, Set<String>> hallwayIDs;
    public static final Map<Integer, Set<String>> eliteIDs;
    public static final Map<Integer, Set<String>> bossIDs;
    public static final Map<Integer, Set<String>> allFightsByAct;
    public static final Map<Integer, Set<String>> elitesAndBossesByAct;

    static {
        hallwayIDs = new HashMap<>();
        hallwayIDs.put(1, new HashSet<>(Arrays.asList("Gremlin Gang", "Large Slime", "Looter", "Lots of Slimes", "Exordium Thugs", "Exordium Wildlife", "Red Slaver", "3 Louse", "2 Fungi Beasts")));
        hallwayIDs.put(2, new HashSet<>(Arrays.asList("Chosen and Byrds", "Sentry and Sphere", "Snake Plant", "Snecko", "Centurion and Healer", "Cultist and Chosen", "3 Cultists", "Shelled Parasite and Fungi")));
        hallwayIDs.put(3, new HashSet<>(Arrays.asList("Transient", "4 Shapes", "Maw", "Jaw Worm Horde", "Sphere and 2 Shapes", "Spire Growth", "Writhing Mass")));

        eliteIDs = new HashMap<>();
        eliteIDs.put(1, new HashSet<>(Arrays.asList("Gremlin Nob", "Lagavulin", "3 Sentries")));
        eliteIDs.put(2, new HashSet<>(Arrays.asList("Gremlin Leader", "Slavers", "Book of Stabbing")));
        eliteIDs.put(3, new HashSet<>(Arrays.asList("Giant Head", "Nemesis", "Reptomancer")));
        eliteIDs.put(4, new HashSet<>(Arrays.asList("Shield and Spear")));

        bossIDs = new HashMap<>();
        bossIDs.put(1, new HashSet<>(Arrays.asList("The Guardian", "Hexaghost", "Slime Boss")));
        bossIDs.put(2, new HashSet<>(Arrays.asList("Automaton", "Collector", "Champ")));
        bossIDs.put(3, new HashSet<>(Arrays.asList("Awakened One", "Time Eater", "Donu and Deca")));
        bossIDs.put(4, new HashSet<>(Arrays.asList("The Heart")));

        allFightsByAct = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            Set<String> enemies = Stream.of(hallwayIDs.get(i), eliteIDs.get(i), bossIDs.get(i))
                    .flatMap(Set::stream)
                    .collect(toSet());
            allFightsByAct.put(i, enemies);
        }
        Set<String> enemies = Stream.of(eliteIDs.get(4), bossIDs.get(4))
                .flatMap(Set::stream)
                .collect(toSet());
        allFightsByAct.put(4, enemies);

        elitesAndBossesByAct = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            Set<String> bigEnemies = Stream.of(eliteIDs.get(i), bossIDs.get(i))
                    .flatMap(Set::stream)
                    .collect(toSet());
            elitesAndBossesByAct.put(i, bigEnemies);
        }
    }
}
