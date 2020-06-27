package FightPredictor.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class HelperMethods {
    private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    private static DecimalFormat twoDecFormat = new DecimalFormat("##0.00", otherSymbols);

    public static String formatNum(double num) {
        if(num != 9999f) {
            return (num>0?"#g+":"#r") + twoDecFormat.format(num);
        } else {
            return "#y----";
        }
    }
}
