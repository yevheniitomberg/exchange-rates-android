package tech.tomberg.demo.filter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {
    private final Pattern pattern;
    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        pattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = pattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}
