package FightPredictor.patches.com.megacrit.cardcrawl.combat;

import FightPredictor.patches.com.megacrit.cardcrawl.screens.CardRewardScreen.RenderValuePatches;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CombatPredictionPatches {
    public static int combatHPLossPrediction = 0;

    private static String predictionText = "Prediction: ";
    private static String realText = "Actual: ";
    private static String suffix = " damage";

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
    private static float START_Y = Settings.HEIGHT - (scl(176.0F * Settings.scale) + scl(AbstractBlight.RAW_W));
    private static final float BOX_H = scl(75);
    public static void renderPredictionDisplay(SpriteBatch sb) {
        sb.setColor(Settings.HALF_TRANSPARENT_BLACK_COLOR);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, START_X, START_Y, scl(450), BOX_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.INTENT_ATK_6, -START_X, START_Y - BOX_H*0.5f + 10f, scl(ImageMaster.INTENT_ATK_6.getWidth()), scl(ImageMaster.INTENT_ATK_6.getHeight()));

        FontHelper.renderSmartText(sb,
                FontHelper.largeDialogOptionFont,
                predictionText + " TAB " + formatNum(combatHPLossPrediction) + suffix
                        + " NL "
                        + realText + " TAB " + formatNum(GameActionManager.damageReceivedThisCombat) + suffix,
                -START_X + scl(ImageMaster.INTENT_ATK_6.getWidth()),
                START_Y + BOX_H - scl(10f),
                Color.WHITE);
    }

    private static float scl(float val) {
        return val * Settings.scale;
    }

    private static String formatNum(int num) {
        String prefix = "";
        if(num < 0) {
            prefix = "#g";
        } else if(num > 0) {
            prefix = "#r";
        }
        return prefix + num;
    }
}