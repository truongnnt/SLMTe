package vn.truongnnt.atmpro.trafficlight.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vn.truongnnt.atmpro.trafficlight.R;

/**
 * TODO: document your custom view class.
 */
public class SystemInfoComponent extends RelativeLayout {

    private TextView tvNumber;
    private TextView tvTime;

    public SystemInfoComponent(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SystemInfoComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SystemInfoComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.system_info_layout, this, true);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvNumber = view.findViewById(R.id.tv_number);
        tvTime = view.findViewById(R.id.tv_time);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SystemInfoComponent, defStyle, 0);
        if (attrs == null) {
            return;
        }
        //String desc = a.getString(R.styleable.DeviceComponent_count);
        tvDesc.setText(a.getString(R.styleable.SystemInfoComponent_desc));
        tvDesc.setTextColor(a.getColor(R.styleable.SystemInfoComponent_descColor, getResources().getColor(R.color.green)));

        tvNumber.setText(a.getString(R.styleable.SystemInfoComponent_numberSys));
        tvTime.setText(a.getString(R.styleable.SystemInfoComponent_time));
        a.recycle();
    }

    public void setTime(String time) {
        tvTime.setText(time);
    }

    public void setNumber(int num) {
        tvNumber.setText(String.valueOf(num));
    }
}