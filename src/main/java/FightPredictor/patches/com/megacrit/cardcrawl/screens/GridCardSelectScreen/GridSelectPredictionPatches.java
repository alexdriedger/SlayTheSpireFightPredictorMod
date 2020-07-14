package FightPredictor.patches.com.megacrit.cardcrawl.screens.GridCardSelectScreen;

import FightPredictor.FightPredictor;
import FightPredictor.CardEvaluationData;
import FightPredictor.util.HelperMethods;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

public class GridSelectPredictionPatches {
    @SpirePatch(clz = GridCardSelectScreen.class, method = "render")
    public static class RenderPrediction {
        //Render for all cards that aren't hovered card if a card is hovered
        @SpireInsertPatch(locator = Locator.class)
        public static void renderAllButHover(GridCardSelectScreen __instance, SpriteBatch sb, AbstractCard ___hoveredCard) {
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                CardEvaluationData evals;
                if(__instance.forUpgrade) {
                    evals = FightPredictor.upgradeEvaluations;
                } else if (__instance.forPurge) {
                    evals = FightPredictor.purgeEvaluations;
                } else if (AbstractDungeon.getCurrRoom().event instanceof TheLibrary) {
                    evals = FightPredictor.cardChoicesEvaluations;
                } else {
                    return;
                }
                for (AbstractCard c : __instance.targetGroup.group) {
                    if (c != ___hoveredCard) {
                        renderGridSelectPrediction(__instance, sb, c, evals);
                    }
                }
            }
        }

        //Render for hovered card and Render for all cards if no card is hovered
        @SpireInsertPatch(locator = Locator2.class)
        public static void renderHover(GridCardSelectScreen __instance, SpriteBatch sb, AbstractCard ___hoveredCard) {
            if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
                CardEvaluationData evals;
                if(__instance.forUpgrade) {
                    evals = FightPredictor.upgradeEvaluations;
                } else if (__instance.forPurge) {
                    evals = FightPredictor.purgeEvaluations;
                } else if (AbstractDungeon.getCurrRoom().event instanceof TheLibrary) {
                    evals = FightPredictor.cardChoicesEvaluations;
                } else {
                    return;
                }
                if (___hoveredCard != null) {
                    renderGridSelectPrediction(__instance, sb, ___hoveredCard, evals);
                } else {
                    for (AbstractCard c : __instance.targetGroup.group) {
                        renderGridSelectPrediction(__instance, sb, c, evals);
                    }
                }

            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractCard.class, "renderHoverShadow");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(GridCardSelectScreen.class, "confirmScreenUp");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }

        private static void renderGridSelectPrediction(GridCardSelectScreen __instance, SpriteBatch sb, AbstractCard c, CardEvaluationData evals) {
            String s = HelperMethods.getPredictionString(c, evals, __instance.forUpgrade);
            //sb.setColor(Settings.HALF_TRANSPARENT_BLACK_COLOR);
            //sb.draw(ImageMaster.WHITE_SQUARE_IMG, c.hb.x + (c.hb.x - FontHelper.getSmartWidth(FontHelper.cardDescFont_N, s, Float.MAX_VALUE, FontHelper.cardDescFont_N.getSpaceWidth())) * 0.5f, c.hb.y, FontHelper.getSmartWidth(FontHelper.cardDescFont_N, s, Float.MAX_VALUE, FontHelper.cardDescFont_N.getSpaceWidth()), FontHelper.cardDescFont_N.getLineHeight());
            sb.setColor(Color.WHITE);
            FontHelper.renderSmartText(sb,
                    FontHelper.cardDescFont_N,
                    s,
                    c.hb.cX - FontHelper.getSmartWidth(FontHelper.cardDescFont_N, s, Float.MAX_VALUE, FontHelper.cardDescFont_N.getSpaceWidth()) * 0.5f,
                    c.hb.y + (12f * Settings.scale),
                    Color.WHITE);
        }
    }

    @SpirePatch(clz = TheLibrary.class, method = "buttonEffect")
    public static class EvaluateLibraryCards {
        @SpireInsertPatch(locator = Locator.class, localvars = {"group"})
        public static void patch(TheLibrary __instance, int buttonEffect, CardGroup group) {
            FightPredictor.cardChoicesEvaluations = CardEvaluationData.createByAdding(group.group, AbstractDungeon.actNum, Math.min(AbstractDungeon.actNum + 1, 4));
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "open");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
            }
        }
    }
}
