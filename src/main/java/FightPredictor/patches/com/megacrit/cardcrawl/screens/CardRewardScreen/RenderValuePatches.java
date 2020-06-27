package FightPredictor.patches.com.megacrit.cardcrawl.screens.CardRewardScreen;

import FightPredictor.CardEvaluation;
import FightPredictor.FightPredictor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import static FightPredictor.util.HelperMethods.formatNum;

public class RenderValuePatches {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(FightPredictor.CARD_REWARD_VALUE_PREDICTION_ID);
    private static final String curActPredictionText = uiStrings.TEXT[0];
    private static final String nextActPredictionText = uiStrings.TEXT[1];

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
            for(AbstractCard c : __instance.rewardGroup) {
                CardEvaluation ce = FightPredictor.cardEvaluations.get(c);

                if (ce != null) {
                    float curAct = ce.getCurrentActScore();
                    float nextAct = 9999f;
                     if(ce.hasNextActPredictions()) {
                         nextAct = ce.getNextActScore();
                     }

                    FontHelper.renderSmartText(sb,
                            FontHelper.topPanelAmountFont,
                            curActPredictionText + ": TAB " + formatNum(curAct)
                                    + " NL "
                                    + nextActPredictionText + ": TAB " + formatNum(nextAct),
                            c.hb.x,
                            c.hb.y - heightBuffer,
                            Color.WHITE);
                }
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