package FightPredictor.util;

import FightPredictor.FightPredictor;
import FightPredictor.ml.ModelUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.*;

public class StatEvaluation {

    // Maps enemies to damage prediction
    private final Map<String, Float> predictions;

    private final List<AbstractCard> cards;
    private final List<AbstractRelic> relics;
    private final int maxHP;
    private final int enteringHP;
    private final int ascension;
    private final boolean potionUsed;

    /**
     * Create a StatEvaluation. Runs a prediction on every fight supplied in enemiesToPredictWith.
     *
     * @param cards in deck
     * @param relics obtained
     * @param maxHP of player
     * @param enteringHP of player
     * @param ascension of player
     * @param potionUsed in fight
     * @param enemiesToPredictWith Runs 1 prediction for every enemy group supplied.
     */
    public StatEvaluation(List<AbstractCard> cards, List<AbstractRelic> relics, int maxHP, int enteringHP, int ascension, boolean potionUsed, Set<String> enemiesToPredictWith) {
        this.predictions = new HashMap<>();

        this.cards = new ArrayList<>(cards);
        this.relics = new ArrayList<>(relics);
        this.maxHP = maxHP;
        this.enteringHP = enteringHP;
        this.ascension = ascension;
        this.potionUsed = potionUsed;

        addPredictions(enemiesToPredictWith);
    }

    /**
     * Additional enemies to run predictions with
     * @param enemiesToAdd enemies to run predictions with
     */
    public void addPredictions(Set<String> enemiesToAdd) {
        float[] vector = ModelUtils.getInputVectorNoEncounter(cards, relics, maxHP, enteringHP, ascension, potionUsed);
        for (String enemy : enemiesToAdd) {
            vector = ModelUtils.changeEncounter(vector, enemy);
            float prediction = FightPredictor.model.predict(vector) * 100f;
            predictions.put(enemy, prediction);
        }
    }

    /**
     * Average damage difference taken between two game loadouts, weighted towards the group of fights most likely to kill the player.
     * For comparable results, both StatEvaluations should have used the same max, entering hp
     * Throws an exception if either StatEvaluation does not have the needed fight data evaluated prior to calling.
     *
     * @param o1 Positive values indicate that this load out is predicted to be better
     * @param o2 Negative values indicate that this load out is predicted to be better
     * @param actNum Act to get average for
     * @return Weighted average of average damage taken per fight
     */
    public static float getWeightedAvg(StatEvaluation o1, StatEvaluation o2, int actNum) {
        float o1HallwayExpectedDmg;
        float o2HallwayExpectedDmg;
        if (actNum == 4) {
            o1HallwayExpectedDmg = 0f;
            o2HallwayExpectedDmg = 0f;
        } else {
            o1HallwayExpectedDmg = enemiesToAverage(BaseGameConstants.hallwayIDs, actNum, o1.predictions);
            o2HallwayExpectedDmg = enemiesToAverage(BaseGameConstants.hallwayIDs, actNum, o2.predictions);
        }

        float o1EliteExpectedDmg = enemiesToAverage(BaseGameConstants.eliteIDs, actNum, o1.predictions);
        float o2EliteExpectedDmg = enemiesToAverage(BaseGameConstants.eliteIDs, actNum, o2.predictions);
        float o1BossExpectedDmg = enemiesToAverage(BaseGameConstants.bossIDs, actNum, o1.predictions);
        float o2BossExpectedDmg = enemiesToAverage(BaseGameConstants.bossIDs, actNum, o2.predictions);

        float hallwayDiff = o1HallwayExpectedDmg - o2HallwayExpectedDmg;
        float eliteDiff = o1EliteExpectedDmg - o2EliteExpectedDmg;
        float bossDiff = o1BossExpectedDmg - o2BossExpectedDmg;

        float numerator = (bossDiff * o2BossExpectedDmg) + (eliteDiff * o2EliteExpectedDmg) + (hallwayDiff * o2HallwayExpectedDmg);
        float denominator = o2BossExpectedDmg + o2EliteExpectedDmg + o2HallwayExpectedDmg;

        return numerator / denominator;
    }

    private static float enemiesToAverage(Map<Integer, Set<String>> enemiesByAct, int act, Map<String, Float> predictions) {
        double total = enemiesByAct.get(act).stream()
                .mapToDouble(predictions::get)
                .average()
                .getAsDouble();
        return (float) total;
    }

    public Map<String, Float> getPredictions() {
        return predictions;
    }

    public Set<String> getEnemies() {
        return predictions.keySet();
    }
}
