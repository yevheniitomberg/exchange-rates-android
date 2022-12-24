package tech.tomberg.demo.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class InputNumberFilter implements InputFilter {
    private final double mDoubleMin , mDoubleMax;
    public InputNumberFilter ( double minValue , double maxValue) {
        this . mDoubleMin = minValue ;
        this . mDoubleMax = maxValue ;
    }
    public InputNumberFilter (String minValue , String maxValue) {
        this . mDoubleMin = Double. parseDouble (minValue) ;
        this . mDoubleMax = Double. parseDouble (maxValue) ;
    }
    @Override
    public CharSequence filter (CharSequence source , int start , int end , Spanned dest , int dstart , int dend) {
        try {
            int input = (int) Double.parseDouble (dest.toString() + source.toString()) ;
            if (isInRange((int) mDoubleMin, (int) mDoubleMax , input))
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }
    private boolean isInRange ( int a , int b , int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a ;
    }
}