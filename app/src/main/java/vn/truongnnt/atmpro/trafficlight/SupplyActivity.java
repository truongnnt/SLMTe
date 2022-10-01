package vn.truongnnt.atmpro.trafficlight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;
import vn.truongnnt.atmpro.trafficlight.model.UserAction;

public class SupplyActivity extends BasicActivity {

    private TableLayout tblEquipment;
    //private final List<UISupplyInfo> equipmentSelected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply);

        DeviceInfo deviceInfo = (DeviceInfo) getIntent().getSerializableExtra(Utils.PARAM_DEVICE_INFO);
        this.tblEquipment = findViewById(R.id.tbl_equipment);
//        this.deviceSelected = new LinkedList<>();
//        this.rowEquipments = new HashMap<>();

        TextView tvEqTitle = findViewById(R.id.tv_equipment_title);
        TextView tvColIns = findViewById(R.id.tv_col_ins);
        TextView tvLedTitle = findViewById(R.id.tv_col_led);
        if (deviceType == DeviceType.CABINET) {
            tvEqTitle.setText(getString(R.string.cabinet_supply));
            tvColIns.setVisibility(View.VISIBLE);
            tvLedTitle.setVisibility(View.GONE);
        } else if (deviceType == DeviceType.LIGHT) {
            tvEqTitle.setText(getString(R.string.light_supply));
            tvColIns.setVisibility(View.GONE);
            tvLedTitle.setVisibility(View.VISIBLE);
        }

        TextView tvDeviceLocation = findViewById(R.id.tv_device_location);
        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceName.setSelected(true);
        if (deviceInfo != null) {
            //List<UISupplyInfo> allEquipments = deviceInfo.getUiSupplyInfoList();
            //showEquipment();
            if (deviceInfo.getName() != null) {
                tvDeviceName.setText(deviceInfo.getName());
            }
            if (deviceInfo.getLocation() != null) {
                tvDeviceLocation.setText(deviceInfo.getLocation());
            }
        }

        ImageView imBack = findViewById(R.id.img_back);
        imBack.setOnClickListener(v -> onBackPressed());

        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvTimeAp = findViewById(R.id.tv_time_ap);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat("HH:mm a");
        Timer lock = new Timer(false);
        lock.schedule(new TimerTask() {
            @Override
            public void run() {
                String[] sDate = ft.format(new Date()).split(" ");
                handler.post(() -> {
                    tvTime.setText(sDate[0]);
                    tvTimeAp.setText(sDate[1]);
                });
            }
        }, 0, 60000);

        Button accept = findViewById(R.id.bt_accept);
        accept.setOnClickListener(v -> {
            if (deviceInfo == null) {
                showMessage(getString(R.string.msg_not_found_device_info));
                return;
            }
            showProgress();
//            List<UISupplyInfo> equipmentInfos = new ArrayList<>();
//            for (int i = 0; i < tblEquipment.getChildCount(); i++) {
//                View child = tblEquipment.getChildAt(i);
//                if (child instanceof TableRow) {
//                    TableRow row = (TableRow) child;
//                    TextView name = row.findViewById(R.id.row_name);
//                    for (UISupplyInfo eq : allEquipments) {
//                        if (eq.getName().contentEquals(name.getText())) {
//                            try {
//                                UserAction userAction = eq.getAction();
//                                if (userAction == null || userAction == UserAction.INSURANCE) {
//                                    Log.w(SupplyActivity.class.getCanonicalName(), "Skip Device [" + eq.getName() + "]");
//                                } else {
//                                    equipmentInfos.add(eq);
//                                    Log.w(SupplyActivity.class.getCanonicalName(), "Action [" + userAction + "] at Device [" + eq.getName() + "]");
//                                }
//                            } catch (Exception ex) {
//                                Log.w(SupplyActivity.class.getCanonicalName(), "Fail to parser row [" + name.getText() + "]", ex);
//                            }
//                        }
//                    }
//                }
//            }

            doNextIntentForResult(SavePrintActivity.class);
//            Intent i = getNextIntent(SavePrintActivity.class);
//            i.putExtra(Utils.PARAM_EQUIPMENT_ACTION, (Serializable) equipmentInfos);
//            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(i);

            dismissProgress();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceInfo != null && deviceInfo.getUiSupplyInfoList() != null) {
            List<UISupplyInfo> allEquipments = deviceInfo.getUiSupplyInfoList();
            showEquipment(allEquipments);
        }
    }

    @Override
    void preNextActivity(Intent i) {
        super.preNextActivity(i);
        //i.putExtra(Utils.PARAM_EQUIPMENT_ACTION, (Serializable) equipmentSelected);
    }

    @Override
    public void onBackPressed() {
        if (deviceType == DeviceType.CABINET) {
            doBackIntent(ParameterActivity.class);
        } else if (deviceType == DeviceType.LIGHT) {
            doBackIntent(DeviceActivity.class);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    void showEquipment(List<UISupplyInfo> uiSupplyInfos) {
        tblEquipment.removeAllViews();

        int index = 0;
        for (UISupplyInfo eq : uiSupplyInfos) {
            index++;
            @SuppressLint("InflateParams") TableRow row = (TableRow) LayoutInflater.from(SupplyActivity.this).inflate(R.layout.row_supply_cabinet, null);

            TextView id = row.findViewById(R.id.row_id);
            id.setText(String.valueOf(index));
            TextView name = row.findViewById(R.id.row_name);
            name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_14));
            //name.setSelected(true);
            name.setText(eq.getName());

            ImageView imgIns = row.findViewById(R.id.row_insurance);
            ImageView imgRep = row.findViewById(R.id.row_replace);
            ImageView imgFix = row.findViewById(R.id.row_fix);

            if (eq.getAction() != null) {
                switch (eq.getAction()) {
                    case INSURANCE:
                        imgIns.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_selected, null));
                        break;
                    case REPLACE:
                        //equipmentSelected.add(eq);
                        imgRep.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_warning, null));
                        break;
                    case REPAIR:
                        //equipmentSelected.add(eq);
                        imgFix.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_error, null));
                        break;
                }
            }
            TextView led = row.findViewById(R.id.row_led);
            if (deviceType == DeviceType.CABINET) {
                led.setVisibility(View.GONE);
                imgIns.setVisibility(View.VISIBLE);
                imgIns.setOnClickListener(v -> {
                    //equipmentSelected.remove(eq); //-> INSURANCE not in list

                    TableRow rowSelected = (TableRow) v.getParent().getParent();
                    ImageView imgInsSel = rowSelected.findViewById(R.id.row_insurance);
                    if (eq.getAction() == UserAction.INSURANCE) {
                        eq.setAction(null);
                        imgInsSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                        return;
                    }
                    eq.setAction(UserAction.INSURANCE);
                    //Log.w(SupplyActivity.class.getCanonicalName(), "TransitionName [" + rowSelected.getTransitionName() + "]");

                    imgInsSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_selected, null));

                    ImageView imgRepSel = rowSelected.findViewById(R.id.row_replace);
                    imgRepSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));

                    ImageView imgFixSel = rowSelected.findViewById(R.id.row_fix);
                    imgFixSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                });
            } else if (deviceType == DeviceType.LIGHT) {
                led.setVisibility(View.VISIBLE);
                led.setText(eq.getLed());
                imgIns.setVisibility(View.GONE);
                row.findViewById(R.id.layout_ins).setVisibility(View.GONE);
            }

            imgRep.setOnClickListener(v -> {
                TableRow rowSelected = (TableRow) v.getParent().getParent();
                ImageView imgRepSel = rowSelected.findViewById(R.id.row_replace);
                if (eq.getAction() == UserAction.REPLACE) {
                    //equipmentSelected.remove(eq);
                    eq.setAction(null);
                    imgRepSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                    return;
                }
                //equipmentSelected.add(eq);
                eq.setAction(UserAction.REPLACE);
                //Log.w(SupplyActivity.class.getCanonicalName(), "TransitionName [" + rowSelected.getTransitionName() + "]");

                ImageView imgInsSel = rowSelected.findViewById(R.id.row_insurance);
                imgInsSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));

                imgRepSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_warning, null));

                ImageView imgFixSel = rowSelected.findViewById(R.id.row_fix);
                imgFixSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
            });

            imgFix.setOnClickListener(v -> {
                TableRow rowSelected = (TableRow) v.getParent().getParent();
                ImageView imgFixSel = rowSelected.findViewById(R.id.row_fix);
                if (eq.getAction() == UserAction.REPAIR) {
                    //equipmentSelected.remove(eq);
                    eq.setAction(null);
                    imgFixSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));
                    return;
                }
                //equipmentSelected.add(eq);
                eq.setAction(UserAction.REPAIR);
                //Log.w(SupplyActivity.class.getCanonicalName(), "TransitionName [" + rowSelected.getTransitionName() + "]");

                ImageView imgInsSel = rowSelected.findViewById(R.id.row_insurance);
                imgInsSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));

                ImageView imgRepSel = rowSelected.findViewById(R.id.row_replace);
                imgRepSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_unselected, null));

                imgFixSel.setBackground(getResources().getDrawable(R.drawable.ic_checkbox_error, null));
            });

            tblEquipment.addView(row);
        }
    }
}