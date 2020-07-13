package FightPredictor.patches.com.megacrit.cardcrawl.screens.CombatRewardScreen;

import FightPredictor.FightPredictor;
import FightPredictor.CardEvaluationData;
import FightPredictor.util.StatEvaluation;
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

            FightPredictor.cardChoicesEvaluations = CardEvaluationData.createByAdding(cards, AbstractDungeon.actNum, Math.max(AbstractDungeon.actNum + 1, 4));

        }
    }
}
