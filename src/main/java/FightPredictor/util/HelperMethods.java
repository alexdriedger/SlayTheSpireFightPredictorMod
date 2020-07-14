package FightPredictor.util;

import FightPredictor.CardEvaluationData;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.city.TheLibrary;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public class HelperMethods {
    private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    private static DecimalFormat twoDecFormat = new DecimalFormat("##0.00", otherSymbols);
//    private static DecimalFormat twoDecFormat = new DecimalFormat("+##0.00;-#", otherSymbols);

    public static String formatNum(double num) {
        if(num != 9999f) {
            String prefix;
            if (num < 0) {
                prefix = "#r";
            } else if (0 <= num && num <= 0.5) {
                prefix = "[#fce803]";
            } else {
                prefix = "#g";
            }
            return prefix + twoDecFormat.format(num);
        } else {
            return "#y----";
        }
    }

    public static String getPredictionString(AbstractCard c, CardEvaluationData eval, boolean forUpgrade) {
        Map<AbstractCard, Map<Integer, Float>> scores = eval.getDiffs();

        if (scores.containsKey(c)) {
            Map<Integer, Float> scoresByAct = scores.get(c);
            float currentAct = scoresByAct.get(AbstractDungeon.actNum);

            float nextAct;
            nextAct = scoresByAct.getOrDefault(AbstractDungeon.actNum + 1, 9999f);

            // Almost all upgrades are always good, so set negative values to low positive value
            if (forUpgrade) {
                if (currentAct < 0f) {
                    currentAct = 0.03f;
                }
                if (nextAct < 0f) {
                    nextAct = 0.04f;
                }
            }
            return HelperMethods.formatNum(currentAct) + " | " + HelperMethods.formatNum(nextAct);
        } else {
            return "";
        }
    }
}
