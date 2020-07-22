package FightPredictor.patches.com.megacrit.cardcrawl.screens.CardRewardScreen;

import FightPredictor.FightPredictor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static FightPredictor.util.HelperMethods.formatNum;

public class RenderValuePatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(FightPredictor.CARD_REWARD_VALUE_PREDICTION_ID);
    private static final String curActPredictionText = uiStrings.TEXT[0];
    private static final String nextActPredictionText = uiStrings.TEXT[1];
    private static final String percentileText = uiStrings.TEXT[2];

    @SpirePatch(clz = CardRewardScreen.class, method = "renderTwitchVotes")
    public static class RemoveTwitchVotes {
        @SpirePrefixPatch
        public static SpireReturn<Void> patch(CardRewardScreen __instance, SpriteBatch sb) {
            return SpireReturn.Return(null);
        }
    }

    private static float heightBuffer = 15f * Settings.scale;

    @SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
    public static class RenderPrediction {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(CardRewardScreen __instance, SpriteBatch sb) {
            if (FightPredictor.cardChoicesEvaluations == null) {
                return;
            }
            Map<AbstractCard, Map<Integer, Float>> scores = FightPredictor.cardChoicesEvaluations.getDiffs();
            for(AbstractCard c : __instance.rewardGroup) {

                if (scores.containsKey(c)) {
                    Map<Integer, Float> scoresByAct = scores.get(c);

                    float curActScore;
                    if (AbstractDungeon.floorNum == 16 || AbstractDungeon.floorNum == 33) {
                        curActScore = 9999f;
                    } else {
                        curActScore = scoresByAct.get(AbstractDungeon.actNum);
                    }

                    float nextAct;
                    nextAct = scoresByAct.getOrDefault(AbstractDungeon.actNum + 1, 9999f);

                    int percentile = FightPredictor.percentiles.getOrDefault(c.name, Integer.MAX_VALUE);

                    FontHelper.renderSmartText(sb,
                            FontHelper.topPanelAmountFont,
                            curActPredictionText + ": TAB " + formatNum(curActScore)
                                    + " NL "
                                    + nextActPredictionText + ": TAB " + formatNum(nextAct)
                                    + " NL "
                                    + percentileText + ":   " + formatPercentile(percentile),
                            c.hb.x,
                            c.hb.y - heightBuffer,
                            Color.WHITE);
                }
            }
        }

        private static String formatPercentile(int num) {
            if (Settings.language != Settings.GameLanguage.ENG) {
                return Integer.toString(num);
            }
            if (num == Integer.MAX_VALUE) {
                return "#y----";
            } else if (num == 11 || num == 12 || num == 13) {
                return num + "th";
            }
            if (Integer.toString(num).endsWith("1")) {
                return num + "st";
            } else if (Integer.toString(num).endsWith("2")) {
                return num + "nd";
            } else if (Integer.toString(num).endsWith("3")){
                return num + "rd";
            } else {
                return num + "th";
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }


    }
}