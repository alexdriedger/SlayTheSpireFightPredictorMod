package FightPredictor.patches.com.megacrit.cardcrawl.combat;

import FightPredictor.FightPredictor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CombatPredictionPatches {
    public static int combatHPLossPrediction = 0;
    public static int combatStartingHP = 0;

    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(FightPredictor.COMBAT_PREDICTION_PANEL_ID);
    private static final String predictionText = uiStrings.TEXT[0];
    private static final String realText = uiStrings.TEXT[1];
    private static final String damage = uiStrings.TEXT[2];
    private static final String healed = uiStrings.TEXT[3];

    @SpirePatch(clz = AbstractRoom.class, method = "render")
    public static class RenderCall {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(AbstractRoom __instance, SpriteBatch sb) {
            renderPredictionDisplay(sb);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "renderPlayerBattleUi");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    private static float START_X = scl(8f);
    private static float START_Y = Settings.HEIGHT - (scl(176.0F) + scl(AbstractBlight.RAW_W));
    private static final float BOX_H = scl(75);
    public static void renderPredictionDisplay(SpriteBatch sb) {
        String text = getPredictionString(combatHPLossPrediction, combatStartingHP - AbstractDungeon.player.currentHealth);

        sb.setColor(Settings.HALF_TRANSPARENT_BLACK_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, START_X, START_Y, FontHelper.getSmartWidth(FontHelper.largeDialogOptionFont, text, Float.MAX_VALUE, FontHelper.largeDialogOptionFont.getSpaceWidth()), BOX_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.INTENT_ATK_6, -START_X, START_Y - BOX_H*0.5f + 10f, scl(ImageMaster.INTENT_ATK_6.getWidth()), scl(ImageMaster.INTENT_ATK_6.getHeight()));

        FontHelper.renderSmartText(sb,
                FontHelper.largeDialogOptionFont,
                text,
                -START_X + scl(ImageMaster.INTENT_ATK_6.getWidth()),
                START_Y + BOX_H - scl(10f),
                Color.WHITE);
    }

    private static String getPredictionString(int predictionVal, int realVal) {
        String combatNum = getCombatNum(predictionVal);
        String realNum = getRealNum(predictionVal, realVal);
        return predictionText + ": TAB " + combatNum
                + " NL "
                + realText + ": TAB " + realNum;
    }

    private static float scl(float val) {
        return val * Settings.scale;
    }

    private static String getCombatNum(int prediction) {
        if (prediction >= 0) {
            return "#r" + prediction + " " + damage;
        } else {
            return "#g" + (-prediction) + " " + healed;
        }
    }

    private static String getRealNum(int prediction, int real) {
        String color;
        if (real <= prediction) {
            color = "#g";
        } else {
            color = "#r";
        }

        if (real >= 0) {
            return color + real + " " + damage;
        } else {
            return color + (-real) + " " + healed;
        }
    }
}