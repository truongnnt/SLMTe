package vn.truongnnt.atmpro.trafficlight.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import vn.truongnnt.atmpro.trafficlight.R;

/**
 * TODO: document your custom view class.
 */
public class DeviceComponent extends LinearLayout {

    private ImageView state;
    private TextView count;
    private int number;
    private String countText;
    private RelativeLayout parent;

    public DeviceComponent(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DeviceComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DeviceComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    private void init(Context context, AttributeSet attrs, int defStyle) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.device_layout, this, true);
        view.setClickable(true);
        view.setFocusable(true);
        setClickable(true);
        setFocusable(true);

        parent = view.findViewById(R.id.device_layout_parent);
//        rl.setOnClickListener(v -> {
//            Log.w("DeviceComponent", "onClick on RelativeLayout");
//        });
//        this.setOnClickListener(v -> {
//            Log.w("DeviceComponent", "onClick on Parent");
//        });
//
//        view.setOnClickListener((v) -> {
//            Log.w("DeviceComponent", "onClick on View");
//        });

        ImageView icon = view.findViewById(R.id.img_device_icon);
//        icon.setOnClickListener(v -> {
//            Log.w("DeviceComponent", "onClick on icon");
//        });

        state = view.findViewById(R.id.img_device_state);
        TextView name = view.findViewById(R.id.tv_device_name);
        count = view.findViewById(R.id.tv_device_count);

        if (attrs == null) {
            return;
        }
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DeviceComponent, defStyle, 0);

        icon.setImageDrawable(a.getDrawable(R.styleable.DeviceComponent_icon));

        boolean on = a.getBoolean(R.styleable.DeviceComponent_state, false);
        if (on) {
            state.setBackground(getResources().getDrawable(R.drawable.ic_ellipse_on));
        } else {
            state.setBackground(getResources().getDrawable(R.drawable.ic_ellipse_off));
        }

        name.setText(a.getString(R.styleable.DeviceComponent_name));

        countText = a.getString(R.styleable.DeviceComponent_count);
        number = a.getInt(R.styleable.DeviceComponent_number, 0);

        count.setText(String.format("%s: %d", countText, number));

        a.recycle();

        // Update TextPaint and text measurements from attributes
//        invalidateTextPaintAndMeasurements();
    }

//    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
//    }

    public void selected() {
        state.setBackground(getResources().getDrawable(R.drawable.ic_ellipse_on));
    }

    public void unSelect() {
        state.setBackground(getResources().getDrawable(R.drawable.ic_ellipse_off));
    }

    @SuppressLint("DefaultLocale")
    public void setNumber(int number) {
        this.number = number;
        count.setText(String.format("%s: %d", countText, number));
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        parent.setOnClickListener(l);
    }
}