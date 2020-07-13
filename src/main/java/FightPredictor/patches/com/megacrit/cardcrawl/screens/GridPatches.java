package FightPredictor.patches.com.megacrit.cardcrawl.screens;

import FightPredictor.FightPredictor;
import FightPredictor.CardEvaluationData;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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
            FightPredictor.upgradeEvaluations = CardEvaluationData.createByUpgrading(upgradeableCards, AbstractDungeon.actNum, Math.min(AbstractDungeon.actNum + 1, 4));
        } else if (forPurge) {
            List<AbstractCard> purgeableCards = AbstractDungeon.player.masterDeck.getPurgeableCards().group;
            FightPredictor.purgeEvaluations = CardEvaluationData.createByRemoving(purgeableCards, AbstractDungeon.actNum, Math.min(AbstractDungeon.actNum + 1, 4));
        }
    }
}
