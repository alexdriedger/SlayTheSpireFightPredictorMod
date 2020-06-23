package FightPredictor.patches.com.megacrit.cardcrawl.screens.CombatRewardScreen;

import FightPredictor.CardEvaluation;
import FightPredictor.FightPredictor;
import FightPredictor.ml.ModelUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.*;
import java.util.stream.Collectors;

@SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
public class CombatRewardScreenPatches {
    public static void Postfix(CombatRewardScreen __instance) {
        List<RewardItem> cardRewards = __instance.rewards.stream()
                                            .filter(r -> r.type == RewardItem.RewardType.CARD)
                                            .collect(Collectors.toList());

        List<AbstractCard> masterDeck = AbstractDungeon.player.masterDeck.group;
        List<AbstractRelic> masterRelics = AbstractDungeon.player.relics;
        String encounter = AbstractDungeon.lastCombatMetricKey;
        String character = AbstractDungeon.player.chosenClass.name();
        int maxHP = AbstractDungeon.player.maxHealth;
        int enteringHP = AbstractDungeon.player.currentHealth;
        int ascension = 20;
        int floor = AbstractDungeon.floorNum;
        boolean potionUsed = false;

        float[] baseVector = ModelUtils.getInputVector(masterDeck, masterRelics, encounter, character, maxHP, enteringHP, ascension, floor, potionUsed);

        CardEvaluation skip = new CardEvaluation(CardEvaluation.SKIP, baseVector, AbstractDungeon.actNum);

        List<CardEvaluation> cardEvaluations = cardRewards.stream()
                .flatMap(r -> r.cards.stream())
                .map(card -> {
                    List<AbstractCard> newDeck = new ArrayList<>(masterDeck);
                    newDeck.add(card);
                    float[] vector = ModelUtils.getInputVector(newDeck, masterRelics, encounter, character, maxHP, enteringHP, ascension, floor, potionUsed);
                    return new CardEvaluation(card.cardID, vector, AbstractDungeon.actNum);
                })
                .collect(Collectors.toList());

        FightPredictor.logger.info("Avg damage for elites and bosses this act: " + skip.getCurrentActAvg());
        FightPredictor.logger.info("Avg damage for elites and bosses next act: " + skip.getNextActAvg());

        for (CardEvaluation ce : cardEvaluations) {
            FightPredictor.logger.info(ce.getCardID() + ": This Act => " + ce.getCurrentActAvg());
            FightPredictor.logger.info(ce.getCardID() + ": Next Act => " + ce.getNextActAvg());
        }

        for (CardEvaluation ce : cardEvaluations) {
            float currentActDiff = (skip.getCurrentActAvg() - ce.getCurrentActAvg()) * 100f;
            float nextActDiff = (skip.getNextActAvg() - ce.getNextActAvg()) * 100f;
            FightPredictor.logger.info(ce.getCardID() + ". This Act => " + currentActDiff + ". Next Act => " + nextActDiff);
        }

    }
}
