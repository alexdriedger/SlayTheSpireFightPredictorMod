package FightPredictor.patches.com.megacrit.cardcrawl.screens.CardRewardScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RenderValuePatches {
    private static DecimalFormat twoDecFormat = new DecimalFormat("##.00");

    @SpirePatch(clz = CardRewardScreen.class, method = "renderTwitchVotes")
    public static class RemoveTwitchVotes {
        @SpirePrefixPatch
        public static SpireReturn<Void> patch(CardRewardScreen __instance, SpriteBatch sb) {
            return SpireReturn.Return(null);
        }
    }

    //TODO: Add localization support
    public static String curActPredictionText = "This act: ";
    public static String nextActPredictionText = "Next act: ";

    private static float heightBuffer = 15f * Settings.scale;

    @SpirePatch(clz = CardRewardScreen.class, method = "renderCardReward")
    public static class RenderPrediction {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(CardRewardScreen __instance, SpriteBatch sb) {
            for(AbstractCard c : __instance.rewardGroup) {
                //TODO: Add check to see if prediction for card exists
                if (true) {
                    float curAct = 1.2525f;
                    float nextAct = -2.33734f;

                    FontHelper.renderSmartText(sb,
                            FontHelper.topPanelAmountFont,
                            curActPredictionText + " TAB " + formatNum(curAct)
                                    + " NL "
                                    + nextActPredictionText + " TAB " + formatNum(nextAct),
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

        private static String formatNum(double num) {
            return (num>0?"#g+":"#r") + twoDecFormat.format(num);
        }
    }
}