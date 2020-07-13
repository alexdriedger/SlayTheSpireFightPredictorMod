package FightPredictor.patches.com.megacrit.cardcrawl.screens.ShopScreen;

import FightPredictor.FightPredictor;
import FightPredictor.util.HelperMethods;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.shop.ShopScreen;
import FightPredictor.CardEvaluation;

import java.util.ArrayList;

public class ShopScreenPatches {
    public static ArrayList<AbstractCard> allCards = new ArrayList<>();

    @SpirePatch(clz = ShopScreen.class, method = "init")
    public static class InitCardHook {
        @SpirePostfixPatch
        public static void patch(ShopScreen __instance, ArrayList<AbstractCard> coloredCards, ArrayList<AbstractCard> colorlessCards) {
            allCards.clear();
            allCards.addAll(coloredCards);
            allCards.addAll(colorlessCards);

            CardEvaluation skip = new CardEvaluation();
            FightPredictor.cardEvaluations.clear();
            for(AbstractCard c : allCards) {
                CardEvaluation ce = new CardEvaluation(c);
                ce.calculateAgainst(skip, AbstractDungeon.floorNum, AbstractDungeon.actNum);
                FightPredictor.cardEvaluations.put(c, ce);
            }
        }
    }

    @SpirePatch(clz = ShopScreen.class, method = "renderCardsAndPrices")
    public static class RenderShopCardEvaluations {
        @SpirePostfixPatch
        public static void patch(ShopScreen __instance, SpriteBatch sb) {
            for(AbstractCard c : allCards) {
                renderGridSelectPrediction(sb, c);
            }
        }

        private static void renderGridSelectPrediction(SpriteBatch sb, AbstractCard c) {
            String s = getPredictionString(c);
            sb.setColor(Color.WHITE);
            FontHelper.renderSmartText(sb,
                    FontHelper.cardDescFont_N,
                    s,
                    c.hb.cX - FontHelper.getSmartWidth(FontHelper.cardDescFont_N, s, Float.MAX_VALUE, FontHelper.cardDescFont_N.getSpaceWidth()) * 0.5f,
                    c.hb.y + (12f * Settings.scale),
                    Color.WHITE);
        }

        private static String getPredictionString(AbstractCard c) {
            float nextAct = 9999f;

            if (FightPredictor.cardEvaluations.get(c).hasNextActPredictions()) {
                nextAct = FightPredictor.cardEvaluations.get(c).getNextActScore();
            }
            return HelperMethods.formatNum(FightPredictor.cardEvaluations.get(c).getCurrentActScore()) + " | " + HelperMethods.formatNum(nextAct);
        }
    }
}
