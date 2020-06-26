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

import javax.smartcardio.Card;
import java.util.*;
import java.util.stream.Collectors;

@SpirePatch(clz = CombatRewardScreen.class, method = "setupItemReward")
public class CombatRewardScreenPatches {
    public static void Postfix(CombatRewardScreen __instance) {
        List<RewardItem> cardRewards = __instance.rewards.stream()
                                            .filter(r -> r.type == RewardItem.RewardType.CARD)
                                            .collect(Collectors.toList());

        List<CardEvaluation> betterEvals = cardRewards.stream()
                .flatMap(r -> r.cards.stream())
                .map(CardEvaluation::new)
                .collect(Collectors.toList());

        CardEvaluation betterSkip = new CardEvaluation();

        for (CardEvaluation ce : betterEvals) {
            float betterCurrentActDiff = (betterSkip.getCurrentActAvg() - ce.getCurrentActAvg()) * 100f;
            float betterNextActDiff = (betterSkip.getNextActAvg() - ce.getNextActAvg()) * 100f;
            FightPredictor.logger.info(ce.getCardID() + ". This Act => " + betterCurrentActDiff + ". Next Act => " + betterNextActDiff);
        }

    }
}
