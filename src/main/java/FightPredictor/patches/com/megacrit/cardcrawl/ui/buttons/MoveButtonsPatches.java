package FightPredictor.patches.com.megacrit.cardcrawl.ui.buttons;

import FightPredictor.FightPredictor;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;

@SpirePatch(clz = SkipCardButton.class, method = SpirePatch.STATICINITIALIZER)
public class MoveButtonsPatches {
    public static void Postfix() {
        float newValue = SkipCardButton.TAKE_Y - (25f * Settings.scale);
        ReflectionHacks.setPrivateStaticFinal(SkipCardButton.class, "TAKE_Y", newValue);
    }
}
