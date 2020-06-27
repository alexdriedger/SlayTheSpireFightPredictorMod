package FightPredictor.patches.com.megacrit.cardcrawl.rooms.CampfireUI;

import FightPredictor.FightPredictor;
import FightPredictor.CardEvaluation;
import FightPredictor.ml.ModelUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;

import java.util.List;

@SpirePatch(clz = CampfireUI.class, method = "initializeButtons")
public class CampfirePatches {
    public static void Postfix(CampfireUI __instance) {
        List<AbstractCard> upgradeableCards = AbstractDungeon.player.masterDeck.getUpgradableCards().group;
        FightPredictor.upgradeEvaluations.clear();

        CardEvaluation skip = new CardEvaluation();

        for (AbstractCard c : upgradeableCards) {
            float[] vector = ModelUtils.getInputVectorWithUpgrade(c);
            CardEvaluation ce = new CardEvaluation(c.cardID, vector, AbstractDungeon.actNum);
            ce.calculateAgainst(skip, AbstractDungeon.floorNum, AbstractDungeon.actNum);
            FightPredictor.upgradeEvaluations.put(c, ce);

            FightPredictor.logger.info(ce.getCardID() + " upgrade. This Act => " + ce.getCurrentActScore());
            if (ce.hasNextActPredictions()) {
                FightPredictor.logger.info(ce.getCardID() + " upgrade. Next Act => " + ce.getNextActScore());
            }
        }
    }
}
