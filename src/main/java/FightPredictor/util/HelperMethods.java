package FightPredictor.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
}
