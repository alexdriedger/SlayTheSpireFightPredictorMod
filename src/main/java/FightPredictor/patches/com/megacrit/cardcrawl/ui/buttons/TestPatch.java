package FightPredictor.patches.com.megacrit.cardcrawl.ui.buttons;

import FightPredictor.FightPredictor;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

@SpirePatch(clz = SkipCardButton.class, method = SpirePatch.CONSTRUCTOR)
public class TestPatch {
    public static void Postfix(SkipCardButton __instance) {
        FightPredictor.logger.info("Skip Y: " + SkipCardButton.TAKE_Y);
    }
}
