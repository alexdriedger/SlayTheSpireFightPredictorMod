package FightPredictor.patches.com.megacrit.cardcrawl.screens.CombatRewardScreen;

import FightPredictor.CardEvaluation;
import FightPredictor.FightPredictor;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;

import java.util.*;
import java.util.stream.Collectors;


public class CombatRewardScreenPatches {

    @SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class EvaluateCardRewards {
        public static void Postfix(CombatRewardScreen __instance) {
            List<RewardItem> cardRewards = __instance.rewards.stream()
                    .filter(r -> r.type == RewardItem.RewardType.CARD)
                    .collect(Collectors.toList());

            CardEvaluation skip = new CardEvaluation();

            List<AbstractCard> cards = cardRewards.stream()
                    .flatMap(r -> r.cards.stream())
                    .collect(Collectors.toList());

            FightPredictor.cardEvaluations.clear();
            for (AbstractCard c : cards) {
                CardEvaluation ce = new CardEvaluation(c);
                ce.calculateAgainst(skip);
                FightPredictor.cardEvaluations.put(c, ce);
                FightPredictor.logger.info(ce.getCardID() + ". This Act => " + ce.getCurrentActScore() + ". Next Act => " + ce.getNextActScore());
            }

        }
    }
}
