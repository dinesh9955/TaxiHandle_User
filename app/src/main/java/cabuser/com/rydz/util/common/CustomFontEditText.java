package cabuser.com.rydz.util.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import cabuser.com.rydz.R;


public class CustomFontEditText extends androidx.appcompat.widget.AppCompatEditText {
    String customFont;

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context, attrs);

    }

    private void style(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        int cf = a.getInteger(R.styleable.CustomFontTextView_fontName, 1);
        int fontName = 0;
        switch (cf) {
            case 1:
                fontName=R.string.regular;
                break;
            case 2:
                fontName=R.string.bold;
                break;
            default:
                fontName = R.string.regular;
                break;
        }

        customFont = getResources().getString(fontName);

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                customFont );
        setTypeface(tf);
        a.recycle();
    }

    @Override
    public int getAutofillType() {
        return AUTOFILL_TYPE_NONE;
    }

}
