package FightPredictor;

import FightPredictor.ml.ModelUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class CardEvaluation {

    private static final Map<Integer, List<String>> hallwayIDs;
    private static final Map<Integer, List<String>> eliteIDs;
    private static final Map<Integer, List<String>> bossIDs;
    public static final String SKIP = "SKIP";

    static {
        hallwayIDs = new HashMap<>();
        hallwayIDs.put(1, Arrays.asList("Gremlin Gang", "Large Slime", "Looter", "Lots of Slimes", "Exordium Thugs", "Exordium Wildlife", "Red Slaver", "3 Louse", "2 Fungi Beasts"));
        hallwayIDs.put(2, Arrays.asList("Chosen and Byrds", "Sentry and Sphere", "Snake Plant", "Snecko", "Centurion and Healer", "Cultist and Chosen", "3 Cultists", "Shelled Parasite and Fungi"));
        hallwayIDs.put(3, Arrays.asList("Transient", "4 Shapes", "Maw", "Jaw Worm Horde", "Sphere and 2 Shapes", "Spire Growth", "Writhing Mass"));

        eliteIDs = new HashMap<>();
        eliteIDs.put(1, Arrays.asList("Gremlin Nob", "Lagavulin", "3 Sentries"));
        eliteIDs.put(2, Arrays.asList("Gremlin Leader", "Slavers", "Book of Stabbing"));
        eliteIDs.put(3, Arrays.asList("Giant Head", "Nemesis", "Reptomancer"));
        eliteIDs.put(4, Arrays.asList("Shield and Spear"));

        bossIDs = new HashMap<>();
        bossIDs.put(1, Arrays.asList("The Guardian", "Hexaghost", "Slime Boss"));
        bossIDs.put(2, Arrays.asList("Automaton", "Collector", "Champ"));
        bossIDs.put(3, Arrays.asList("Awakened One", "Time Eater", "Donu and Deca"));
        bossIDs.put(4, Arrays.asList("The Heart"));
    }

    private final String cardID;
    private final boolean hasNextActPredictions;

    private Map<String, Float> currentActHallwayPredictions;
    private Map<String, Float> currentActElitesPredictions;
    private Map<String, Float> currentActBossesPredictions;
    private Map<String, Float> nextActHallwayPredictions;
    private Map<String, Float> nextActElitesPredictions;
    private Map<String, Float> nextActBossesPredictions;

    private float currentActScore;
    private float nextActScore;

    // TODO : CHANGE THIS TO BE MORE GENERAL. STATE EVALUATION? DEFINITELY REMOVE cardID

    // Base vector just needs encounters changed. card should already be in vector
    // cardid is just used for being able to reference this later
    public CardEvaluation(String cardID, float[] baseVector, int actNum) {
        this.cardID = cardID;
        this.hasNextActPredictions = actNum < 4;

        float[] vec = Arrays.copyOf(baseVector, baseVector.length);


        if (actNum < 4) {
            currentActHallwayPredictions = makePredictions(hallwayIDs.get(actNum), vec);
        }
        currentActElitesPredictions = makePredictions(eliteIDs.get(actNum), vec);
        currentActBossesPredictions = makePredictions(bossIDs.get(actNum), vec);
        if (this.hasNextActPredictions) {
            if (actNum < 3) {
                nextActHallwayPredictions = makePredictions(hallwayIDs.get(actNum + 1), vec);
            }
            nextActElitesPredictions = makePredictions(eliteIDs.get(actNum + 1), vec);
            nextActBossesPredictions = makePredictions(bossIDs.get(actNum + 1), vec);
        }
    }

    /**
     * Get an evaluation of the current game state
     */
    public CardEvaluation() {
        this(SKIP, ModelUtils.getBaseInputVector(), AbstractDungeon.actNum);
    }

    /**
     * Get an evaluation of the current game state with the addition of a single card
     * @param c Card to add
     */
    public CardEvaluation(AbstractCard c) {
        this(c.cardID, ModelUtils.getInputVector(c), AbstractDungeon.actNum);
    }

    /**
     * Get an evaluation of the current game state with the addition of a single card
     * @param c Card to add
     */
    public CardEvaluation(AbstractCard c, int actNum) {
        this(c.cardID, ModelUtils.getInputVector(c), actNum);
    }

    private static Map<String, Float> makePredictions(List<String> encounters, float[] vector) {
        Map<String, Float> predictions = new HashMap<>();
        for (String enc : encounters) {
            float[] vec = ModelUtils.changeEncounter(vector, enc);
            float prediction = FightPredictor.model.predict(vec);
            predictions.put(enc, prediction);
        }
        return predictions;
    }

    /**
     * Calculate a score against another game state at a given floor. Use calculateAgainst(CardEvaluation other) for simple
     * average score calculation
     *
     * @param other game state to compare against
     * @param floorNum player is currently on
     * @param actNum player is in
     */
    public void calculateAgainst(CardEvaluation other, int floorNum, int actNum) {
        if (actNum == 4) {
            this.currentActScore = (other.getCurrentActEliteAndBossAvg() - this.getCurrentActEliteAndBossAvg()) * 100f;
            return;
        }

        this.currentActScore = (other.calculateCurrentActScore(floorNum, actNum) - this.calculateCurrentActScore(floorNum, actNum)) * 100f;
        if (this.hasNextActPredictions) {
            this.nextActScore = (other.calculateNextActScore(floorNum, actNum) - this.calculateNextActScore(floorNum, actNum)) * 100f;
        }
    }

    private float calculateCurrentActScore(int floorNum, int actNum) {
        float currentActHallwayScore = getAvg(currentActHallwayPredictions.values());
        float currentActEliteScore = getAvg(currentActElitesPredictions.values());
        float currentActBossScore = currentActBossesPredictions.get(AbstractDungeon.bossKey);

        int bossCount = 1;
        int eliteCount = isSecondHalfOfAct(floorNum) ? 2 : 3;
        int hallwayCount = isSecondHalfOfAct(floorNum) ? 2 : 5;

        float total = (currentActHallwayScore * hallwayCount) + (currentActEliteScore * eliteCount) + (currentActBossScore * bossCount);
        return (total / (hallwayCount + eliteCount + bossCount));
    }

    private float calculateNextActScore(int floorNum, int actNum) {
        if (hasNextActPredictions) {
            float nextActHallwayScore = actNum < 3 ? getAvg(nextActHallwayPredictions.values()) : 0f;
            float nextActEliteScore = getAvg(nextActElitesPredictions.values());
            float nextActBossScore = getAvg(nextActBossesPredictions.values());

            int bossCount = 3;
            int eliteCount = 3;
            int hallwayCount = 5;

            float total = (nextActHallwayScore * hallwayCount) + (nextActEliteScore * eliteCount) + (nextActBossScore * bossCount);
            return (total / (hallwayCount + eliteCount + bossCount));
        }
        return 0f;
    }

    private boolean isSecondHalfOfAct(int floorNum) {
        if (floorNum < 9) {
            return false;
        } else if (floorNum < 17) {
            return true;
        } else if (floorNum < 26) {
            return false;
        } else if (floorNum < 34) {
            return true;
        } else if (floorNum < 43) {
            return false;
        } else if (floorNum < 51) {
            return true;
        } else {
            return false;
        }
    }



    private float getCurrentActEliteAndBossAvg() {
        return getAvg(currentActElitesPredictions.values(), currentActBossesPredictions.values());
    }

    private float getAvg(Collection<Float>... floatCollections) {
        int count = 0;
        List<Float> all = new ArrayList<>();
        for (Collection<Float> c : floatCollections) {
            all.addAll(c);
            count += c.size();
        }
        float total = all.stream().reduce(Float::sum).get();
        return total / count;
    }

    public String getCardID() {
        return cardID;
    }

    public boolean hasNextActPredictions() {
        return hasNextActPredictions;
    }

    public Map<String, Float> getCurrentActElitesPredictions() {
        return currentActElitesPredictions;
    }

    public Map<String, Float> getCurrentActBossesPredictions() {
        return currentActBossesPredictions;
    }

    public Map<String, Float> getNextActElitesPredictions() {
        return nextActElitesPredictions;
    }

    public Map<String, Float> getNextActBossesPredictions() {
        return nextActBossesPredictions;
    }

    public float getCurrentActScore() {
        return currentActScore;
    }

    public float getNextActScore() {
        return nextActScore;
    }
}
