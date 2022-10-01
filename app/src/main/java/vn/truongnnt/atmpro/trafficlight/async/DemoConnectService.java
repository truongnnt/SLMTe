package vn.truongnnt.atmpro.trafficlight.async;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import vn.truongnnt.atmpro.trafficlight.BasicActivity;
import vn.truongnnt.atmpro.trafficlight.api.response.AddCabinetResp;
import vn.truongnnt.atmpro.trafficlight.async.model.Action;
import vn.truongnnt.atmpro.trafficlight.async.model.AddressLocation;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceInfoResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceListResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.LoginResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.ResponseCode;
import vn.truongnnt.atmpro.trafficlight.async.model.SysInfoResponse;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;
import vn.truongnnt.atmpro.trafficlight.model.UserInfo;

@Singleton
public class DemoConnectService implements ConnectService {
    public static final String FULL_NAME_TAG = "full-name";
    public static final String LOCATION_TAG = "location";
    public static final String FULL_NAME = "Nguyễn Ngọc Thao Trường";
    public static final String LOCATION = "Tân Bình, TP. HCM";

    protected ExecutorService executor;
    protected Handler handler;

    protected UserInfo userInfo;

    @Inject
    protected BasicActivity context;

    @Inject
    public DemoConnectService() {
        Log.w(DemoConnectService.class.getCanonicalName(), "New instance ConnectService");
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Map<String, String> login(String username, String password) {
        context.showProgress();
        executor.execute(() -> {
            LoginResponse loginResponse = new LoginResponse();
            ResponseCode code = ResponseCode.SUCCESS;
            loginResponse.setCode(code);
            try {
                Map<String, String> res = new HashMap<>();
                res.put(FULL_NAME_TAG, FULL_NAME);
                res.put(LOCATION_TAG, LOCATION);
                TimeUnit.MILLISECONDS.sleep(100);

                this.userInfo = UserInfo.builder()
                        .fullName(username)
                        .name(username).build();
                loginResponse.setUserInfo(res);
            } catch (InterruptedException e) {
                context.setException(e);
            }
            //context.onReceiveResponse(Action.LOGIN, loginResponse);
            //context.dismissProgress();
            handler.post(this::getSysInfo);
        });
        return null;
    }

    @Override
    public void getSysInfo() {
        executor.execute(() -> {

            SysInfoResponse sysInfoResponse = SysInfoResponse.builder().build();

//            SysInfoResponse sysInfoResponse = new SysInfoResponse();
//            ResponseCode code = ResponseCode.SUCCESS;
//            sysInfoResponse.setCode(code);
            try {
//                sysInfoResponse.setCabinetCount(3251);
//                sysInfoResponse.setLightCount(2539);
//                sysInfoResponse.setPoleCount(6987);
//
//                SysInfo normal = new SysInfo(SysInfoType.NORMAL);
//                normal.setTime(new Date());
//                normal.setCount(368);
//                sysInfoResponse.setNormal(normal);
//
//                SysInfo trouble = new SysInfo(SysInfoType.TROUBLE);
//                trouble.setTime(new Date());
//                trouble.setCount(476);
//                sysInfoResponse.setTrouble(trouble);
//
//                SysInfo maintain = new SysInfo(SysInfoType.MAINTAIN);
//                maintain.setTime(new Date());
//                maintain.setCount(151);
//                sysInfoResponse.setMaintain(maintain);

                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                context.setException(e);
            }
            handler.post(() -> {
                context.onReceiveResponse(Action.GET_SYS_INFO, sysInfoResponse);
                context.dismissProgress();
            });
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public List<DeviceInfo> listDevice(DeviceType dv) {
        //context.showProgress();
        executor.execute(() -> {
            //DeviceListResponse listDeviceResponse = new DeviceListResponse();
            //listDeviceResponse.setCode(ResponseCode.SUCCESS);
            List<DeviceInfo> resp = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                resp.add(DeviceInfo.builder()
                        .date(new Date())
                        .name(String.format("BDU.168.%03d", i))
                        .build());
            }
            //listDeviceResponse.setDeviceInfos(resp);
            DeviceListResponse listDeviceResponse = DeviceListResponse.builder()
                    .deviceInfos(resp)
                    .build();

            handler.post(() -> {
                context.onReceiveResponse(Action.LIST_DEVICE, listDeviceResponse);
                //context.dismissProgress();
            });
        });
        return null;
    }

    @Override
    public boolean newAccount(String fullName, String username, String password, String email, String phone) {
        context.showProgress();

        executor.execute(() -> {
            LoginResponse loginResponse = new LoginResponse();
            ResponseCode code = ResponseCode.SUCCESS;
            loginResponse.setCode(code);
            try {
                Map<String, String> res = new HashMap<>();
                res.put(FULL_NAME_TAG, "Nguyễn Ngọc Thao Trường");
                res.put(LOCATION_TAG, LOCATION);
                TimeUnit.MILLISECONDS.sleep(100);

                loginResponse.setUserInfo(res);
            } catch (InterruptedException e) {
                context.setException(e);
            }
            handler.post(() -> {
                context.onReceiveResponse(Action.NEW_ACCOUNT, loginResponse);
                context.dismissProgress();
            });
        });
        return true;
    }

    @Override
    public void infoDevice(DeviceType dv, DeviceInfo dvInf) {
        context.showProgress();

        executor.execute(() -> {
//            DeviceInfoResponse listDeviceResponse = new DeviceInfoResponse();
//            listDeviceResponse.setCode(ResponseCode.SUCCESS);
            List<UISupplyInfo> equipmentInfos = new ArrayList<>();

            if (dv == DeviceType.CABINET) {
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCCB 80A").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MBA 500VA").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Chống sét lan truyền (0001)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 10A").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Contactor 50A (01)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Contactor 50A (02)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Contactor 50A (03)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Contactor 50A (04)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 1P 50A (01)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 1P 50A (02)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 1P 50A (03)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 1P 50A (04)").build());

                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("PLC S7-1200 (1211)").build());

                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 2P 50A (01)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 2P 50A (02)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 2P 50A (03)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("MCB 2P 50A (04)").build());
                equipmentInfos.add(UISupplyInfo.builder()
                        .warrantyDate(new Date())
                        .name("Router").build());
            } else if (dv == DeviceType.LIGHT) {
                for (int i = 0; i < 20; i++) {
                    equipmentInfos.add(UISupplyInfo.builder()
                            .warrantyDate(new Date())
                            .led(String.format("1%02d", i))
                            .name(String.format("LED 120W Philips %d", i)).build());
                }
            }

//            for(UISupplyInfo el : equipmentInfos){
//                el.setAction(UserAction.REPAIR);
//            }

            DeviceInfo deviceInfo = DeviceInfo.builder()
                    .uiSupplyInfoList(equipmentInfos)
                    .name(dvInf.getName())
                    .date(new Date())
                    //.location("TP Mới, Phú Chánh, Nguyễn Văn Linh")
                    .gps("10.8025818,106.6435837,16")
                    .build();

            //listDeviceResponse.setDeviceInfo(deviceInfo);
            DeviceInfoResponse deviceInfoResponse = DeviceInfoResponse.builder()
                    .deviceInfo(deviceInfo)
                    .build();


            handler.post(() -> {
                context.onReceiveResponse(Action.INFO_DEVICE, deviceInfoResponse);
                context.dismissProgress();
            });
        });
    }

    @Override
    public boolean sendFinish(DeviceType dv, DeviceInfo deviceInfo) {
        return false;
    }

    @Override
    public boolean checkExist(DeviceType dv, String id) {
        return false;
    }

    @Override
    public void getInfo(DeviceType dv, String id) {

    }

    @Override
    public AddCabinetResp addCabinet(DeviceType dv, String id, String qr, String gps, String Iddg, String Idqh, String Idtp) {
        return null;
    }

    @Override
    public List<AddressLocation> getCityProvince() {
        return null;
    }

    @Override
    public List<AddressLocation> getDistrict(String city) {
        return null;
    }

    @Override
    public List<AddressLocation> getStreet(String dist) {
        return null;
    }

    @Override
    public boolean resetPassword(String username) {
        return false;
    }

    @Override
    public UserInfo getUserInfo() {
        return userInfo;
    }
}
