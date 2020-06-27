package FightPredictor.patches.com.megacrit.cardcrawl.screens;

import FightPredictor.FightPredictor;
import FightPredictor.CardEvaluation;
import FightPredictor.ml.ModelUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;

import java.util.List;

@SpirePatch(
        clz = GridCardSelectScreen.class,
        method = "open",
        paramtypez = {
                CardGroup.class,
                int.class,
                String.class,
                boolean.class,
                boolean.class,
                boolean.class,
                boolean.class
        }
)
public class GridPatches {
    public static void Postfix(GridCardSelectScreen __instance, CardGroup group, int numCards, String tipMsg, boolean forUpgrade, boolean forTransform, boolean canCancel, boolean forPurge) {
        if (forUpgrade) {
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
}
