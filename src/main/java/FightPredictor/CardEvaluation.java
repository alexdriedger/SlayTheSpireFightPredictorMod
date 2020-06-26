package FightPredictor;

import FightPredictor.ml.ModelUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class CardEvaluation {

    private static final Map<Integer, List<String>> eliteIDs;
    private static final Map<Integer, List<String>> bossIDs;
    public static final String SKIP = "SKIP";

    static {
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

    private Map<String, Float> currentActElitesPredictions;
    private Map<String, Float> currentActBossesPredictions;
    private Map<String, Float> nextActElitesPredictions;
    private Map<String, Float> nextActBossesPredictions;

    // Base vector just needs encounters changed. card should already be in vector
    // cardid is just used for being able to reference this later
    public CardEvaluation(String cardID, float[] baseVector, int actNum) {
        this.cardID = cardID;
        this.hasNextActPredictions = actNum < 4;

        float[] vec = Arrays.copyOf(baseVector, baseVector.length);


        currentActElitesPredictions = makePredictions(eliteIDs.get(actNum), vec);
        currentActBossesPredictions = makePredictions(bossIDs.get(actNum), vec);
        if (this.hasNextActPredictions) {
            nextActElitesPredictions = makePredictions(eliteIDs.get(actNum + 1), vec);
            nextActBossesPredictions = makePredictions(bossIDs.get(actNum + 1), vec);
        }
    }

    public CardEvaluation() {
        this(SKIP, ModelUtils.getBaseInputVector(), AbstractDungeon.actNum);
    }

    public CardEvaluation(AbstractCard c) {
        this(c.cardID, ModelUtils.getInputVector(c), AbstractDungeon.actNum);
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

    public float getCurrentActAvg() {
        return getAvg(currentActElitesPredictions.values(), currentActBossesPredictions.values());
    }

    public float getNextActAvg() {
        return getAvg(nextActElitesPredictions.values(), nextActBossesPredictions.values());
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
}
