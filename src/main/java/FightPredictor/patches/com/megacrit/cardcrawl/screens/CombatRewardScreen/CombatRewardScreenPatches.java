package FightPredictor.patches.com.megacrit.cardcrawl.screens.CombatRewardScreen;

import FightPredictor.CardEvaluation;
import FightPredictor.FightPredictor;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.*;
import java.util.stream.Collectors;


public class CombatRewardScreenPatches {

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class EvaluateCardRewards {
        public static void Postfix(CombatRewardScreen __instance) {
            List<AbstractCard> cards = __instance.rewards.stream()
                    .filter(r -> r.type == RewardItem.RewardType.CARD)
                    .flatMap(r -> r.cards.stream()) // Flatten out all card rewards for prayer wheel
                    .collect(Collectors.toList());

            CardEvaluation skip = new CardEvaluation();

            FightPredictor.cardEvaluations.clear();

            // Evaluate cards
            for (AbstractCard c : cards) {
                CardEvaluation ce = new CardEvaluation(c);
                ce.calculateAgainst(skip, AbstractDungeon.floorNum, AbstractDungeon.actNum);
                FightPredictor.cardEvaluations.put(c, ce);

                FightPredictor.logger.info(ce.getCardID() + ". This Act => " + ce.getCurrentActScore());
                if (ce.hasNextActPredictions()) {
                    FightPredictor.logger.info(ce.getCardID() + ". Next Act => " + ce.getNextActScore());
                }
            }

        }
    }
}
