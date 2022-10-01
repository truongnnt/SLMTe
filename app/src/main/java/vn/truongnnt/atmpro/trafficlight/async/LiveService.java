package vn.truongnnt.atmpro.trafficlight.async;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.truongnnt.atmpro.trafficlight.R;
import vn.truongnnt.atmpro.trafficlight.Utils;
import vn.truongnnt.atmpro.trafficlight.api.AMTApi;
import vn.truongnnt.atmpro.trafficlight.api.request.AddCabinet;
import vn.truongnnt.atmpro.trafficlight.api.request.Finish;
import vn.truongnnt.atmpro.trafficlight.api.request.LoginRequest;
import vn.truongnnt.atmpro.trafficlight.api.request.Param;
import vn.truongnnt.atmpro.trafficlight.api.request.Phieu;
import vn.truongnnt.atmpro.trafficlight.api.request.Register;
import vn.truongnnt.atmpro.trafficlight.api.response.AddCabinetResp;
import vn.truongnnt.atmpro.trafficlight.api.response.CheckExistResp;
import vn.truongnnt.atmpro.trafficlight.api.response.Duong;
import vn.truongnnt.atmpro.trafficlight.api.response.EquipmentInfo;
import vn.truongnnt.atmpro.trafficlight.api.response.FinishResp;
import vn.truongnnt.atmpro.trafficlight.api.response.GetDeviceInfoResp;
import vn.truongnnt.atmpro.trafficlight.api.response.GetSysInfoResp;
import vn.truongnnt.atmpro.trafficlight.api.response.LoginResp;
import vn.truongnnt.atmpro.trafficlight.api.response.MaintenanceResp;
import vn.truongnnt.atmpro.trafficlight.api.response.ManagerResp;
import vn.truongnnt.atmpro.trafficlight.api.response.QuanHuyen;
import vn.truongnnt.atmpro.trafficlight.api.response.RegisterResp;
import vn.truongnnt.atmpro.trafficlight.api.response.ResetPassResp;
import vn.truongnnt.atmpro.trafficlight.api.response.ThanhPho;
import vn.truongnnt.atmpro.trafficlight.async.model.Action;
import vn.truongnnt.atmpro.trafficlight.async.model.AddressLocation;
import vn.truongnnt.atmpro.trafficlight.async.model.CheckExistResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceInfoResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.DeviceListResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.FinishResponse;
import vn.truongnnt.atmpro.trafficlight.async.model.SysInfo;
import vn.truongnnt.atmpro.trafficlight.async.model.SysInfoResponse;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.SysInfoType;
import vn.truongnnt.atmpro.trafficlight.model.UISupplyInfo;
import vn.truongnnt.atmpro.trafficlight.model.UserAction;
import vn.truongnnt.atmpro.trafficlight.model.UserInfo;

@Singleton
public class LiveService extends DemoConnectService {

    private AMTApi api;
    //private OkHttpClient client;

    @Inject
    public LiveService() {
        super();
        reload();
    }

    private void reload() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder builder;
                    if (userInfo != null) {
                        builder = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + userInfo.getToken());
                    } else {
                        builder = chain.request().newBuilder();
                    }
                    Request request = builder.build();
                    return chain.proceed(request);
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AMTApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.api = retrofit.create(AMTApi.class);
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        super.setUserInfo(userInfo);
        reload();
    }

    @Override
    public Map<String, String> login(String username, String password) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return null;
        }
        context.showProgress();
        LoginRequest request = LoginRequest.builder()
                .Username(username)
                .Password(password)
                .build();
        Log.e(LiveService.class.getCanonicalName(), "LoginRequest [" + new Gson().toJson(request) + "]");
        Call<LoginResp> loginResponseCall = api.login(request);
        loginResponseCall.enqueue(new Callback<LoginResp>() {
            @Override
            public void onResponse(@NotNull Call<LoginResp> call, @NotNull Response<LoginResp> response) {
                LoginResp loginResponse = response.body();
                if (!response.isSuccessful()) {
                    String error = null;
                    if (loginResponse != null) {
                        error = loginResponse.getError();
                    }
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                if (loginResponse.isSuccess()) {
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    Date expiredAt = null;
                    try {
                        expiredAt = ft.parse(loginResponse.getExpiration());
                    } catch (ParseException ex) {
                        Log.w(LiveService.class.getCanonicalName(), ex);
                    }
                    LiveService.this.userInfo = UserInfo.builder()
                            .fullName(loginResponse.getFullName())
                            .name(loginResponse.getUsername())
                            .token(loginResponse.getToken())
                            .expiredAt(expiredAt)
                            .build();
                    //handler.post(LiveService.this::getSysInfo);
                    LiveService.this.getSysInfo();
                } else {
                    handler.post(() -> {
                        context.dismissProgress();
                        context.showMessage(context.getResources().getString(R.string.msg_login_fail));
                    });
                }
            }


            @Override
            public void onFailure(@NotNull Call<LoginResp> call, @NotNull Throwable t) {
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.dismissProgress();
                context.showMessage(t.getMessage());
            }
        });

        return null;
    }

    @Override
    public void getSysInfo() {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return;
        }
        context.showProgress();
        Call<GetSysInfoResp> sysInfoRespCall = api.getSysInfo();
        sysInfoRespCall.enqueue(new Callback<GetSysInfoResp>() {
            @Override
            public void onResponse(@NotNull Call<GetSysInfoResp> call, @NotNull Response<GetSysInfoResp> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                GetSysInfoResp apiResp = response.body();
                SysInfoResponse sysInfoResponse = SysInfoResponse.builder()
                        .maintain(SysInfo.builder()
                                .type(SysInfoType.MAINTAIN)
                                .count(apiResp.getMaintenance())
                                .build())
                        .normal(SysInfo.builder()
                                .type(SysInfoType.NORMAL)
                                .count(apiResp.getNormal())
                                .build())
                        .trouble(SysInfo.builder()
                                .type(SysInfoType.TROUBLE)
                                .count(apiResp.getTrouble())
                                .build())
                        .build();
                Call<ManagerResp> managerRespCall = api.manager();
                managerRespCall.enqueue(new Callback<ManagerResp>() {
                    @Override
                    public void onResponse(@NotNull Call<ManagerResp> call, @NotNull Response<ManagerResp> response) {
                        ManagerResp managerResp = response.body();
                        assert managerResp != null;
                        sysInfoResponse.setPoleCount(managerResp.getPole());
                        sysInfoResponse.setLightCount(managerResp.getLight());
                        sysInfoResponse.setCabinetCount(managerResp.getCabinet());
                        handler.post(() -> {
                            context.onReceiveResponse(Action.GET_SYS_INFO, sysInfoResponse);
                            context.dismissProgress();
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call<ManagerResp> call, @NotNull Throwable t) {
                        handler.post(() -> {
                            context.onReceiveResponse(Action.GET_SYS_INFO, sysInfoResponse);
                            context.dismissProgress();
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call<GetSysInfoResp> call, @NotNull Throwable t) {
                Log.w(LiveService.class.getCanonicalName(), "", t);
                handler.post(() -> {
                    context.onReceiveResponse(Action.GET_SYS_INFO, null);
                    Log.w(LiveService.class.getCanonicalName(), "", t);
                    context.dismissProgress();
                });
            }
        });
    }

    @Override
    public List<DeviceInfo> listDevice(DeviceType dv) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return null;
        }
        context.showProgress();
        Call<List<MaintenanceResp>> listMaintenanceRespCall = api.listMaintenance(dv.id);
        listMaintenanceRespCall.enqueue(new Callback<List<MaintenanceResp>>() {
            @Override
            public void onResponse(Call<List<MaintenanceResp>> call, @NotNull Response<List<MaintenanceResp>> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                List<MaintenanceResp> listMaintenanceResp = response.body();
                List<DeviceInfo> deviceInfoList = new ArrayList<>();
                assert listMaintenanceResp != null;
                for (MaintenanceResp el : listMaintenanceResp) {

                    DeviceInfo deviceInfo = DeviceInfo.builder()
                            .name(el.getAmtid())
                            //.id(dv == DeviceType.CABINET ? el.getIdtu() : el.getIdden())
                            .id(el.getIdtu())
                            .date(el.getTimeErr())
                            .phase(el.getPha())
                            .gps(el.getGps())
                            .build();
//                    executor.submit(() -> {
//                        String[] latlon = el.getGps().split(",");
//                        String address;
//                        try {
//                            address = Utils.getCompleteAddressString(context, Double.valueOf(latlon[0].trim()),
//                                    Double.valueOf(latlon[1].trim()));
//                        } catch (Exception ex) {
//                            Log.w(LiveService.class.getCanonicalName(), ex);
//                            address = "";
//                        }
//                        deviceInfo.setLocation(address);
//                        //deviceInfo.setId(dv == DeviceType.CABINET ? el.getIdtu() : el.getIdden());
//                        //deviceInfo.setId(el.getIdtu());
//                    });
                    deviceInfoList.add(deviceInfo);
                }

                DeviceListResponse listDeviceResponse = DeviceListResponse.builder()
                        .deviceInfos(deviceInfoList)
                        .build();
                handler.post(() -> {
                    context.onReceiveResponse(Action.LIST_DEVICE, listDeviceResponse);
                    context.dismissProgress();
                });
            }

            @Override
            public void onFailure(Call<List<MaintenanceResp>> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
        return null;
    }

    @Override
    public void infoDevice(DeviceType dv, DeviceInfo deviceInfo) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return;
        }
        context.showProgress();
        Call<List<EquipmentInfo>> deviceInfoRespCall = api.deviceInfo(dv.id, deviceInfo.getId());
        deviceInfoRespCall.enqueue(new Callback<List<EquipmentInfo>>() {
            @Override
            public void onResponse(Call<List<EquipmentInfo>> call, Response<List<EquipmentInfo>> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                List<EquipmentInfo> equipmentInfo = response.body();
                Log.e(LiveService.class.getCanonicalName(), "Type [" + dv.name() + "], id [" + deviceInfo.getId() + "], infoDevice-Response: [" + new Gson().toJson(equipmentInfo) + "]");

                List<UISupplyInfo> uiSupplyInfos = new ArrayList<>();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                for (EquipmentInfo eq : equipmentInfo) {
                    Date warranty;
                    try {
                        warranty = ft.parse(eq.getBaoHanh());
                    } catch (Exception ex) {
                        Log.w(LiveService.class.getCanonicalName(), ex);
                        warranty = new Date();
                    }
                    uiSupplyInfos.add(UISupplyInfo.builder()
                            //.name(eq.getChiTiet())
                            .name(eq.getTen())
                            .id(dv == DeviceType.CABINET ? eq.getIddv() : eq.getIdhw())
                            .led(eq.getIdhw())
                            .warrantyDate(warranty)
                            .build());
                }
                DeviceInfoResponse deviceInfoResponse = DeviceInfoResponse.builder()
                        .deviceInfo(DeviceInfo.builder()
                                //.location(deviceInfo.getLocation())
                                .gps(deviceInfo.getGps())
                                .date(deviceInfo.getDate())
                                .id(deviceInfo.getId())
                                .name(deviceInfo.getName())
                                .phase(deviceInfo.getPhase())
                                .uiSupplyInfoList(uiSupplyInfos)
                                .build())
                        .build();
                handler.post(() -> {
                    context.onReceiveResponse(Action.INFO_DEVICE, deviceInfoResponse);
                    context.dismissProgress();
                });
            }

            @Override
            public void onFailure(Call<List<EquipmentInfo>> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public boolean sendFinish(DeviceType dv, DeviceInfo deviceInfo) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return false;
        }
        context.showProgress();
        List<Phieu> phieus = new ArrayList<>();

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        for (UISupplyInfo sup : deviceInfo.getUiSupplyInfoList()) {
            if (sup.getAction() == null || sup.getAction() == UserAction.INSURANCE) {
                continue;
            }
            phieus.add(Phieu.builder()
                    .Iddv(sup.getId())
                    .LoaiSua(sup.getAction() == UserAction.REPLACE ? 0 : 1)
                    .NgayGio(ft.format(new Date()))
                    .build());
        }

        Finish.FinishBuilder finishBuilder = Finish.builder()
                //.(deviceInfo.getId())
                .Id(deviceInfo.getId())
                .GhiChu(deviceInfo.getUiSavePrint().getNote())
                .Phieu(phieus);
        if (dv == DeviceType.CABINET) {
            finishBuilder.Para(Param.builder()
                    .U1(Utils.floatFromString(deviceInfo.getUiParameter().getU1()))
                    .U2(Utils.floatFromString(deviceInfo.getUiParameter().getU2()))
                    .U3(Utils.floatFromString(deviceInfo.getUiParameter().getU3()))
                    .I1(Utils.floatFromString(deviceInfo.getUiParameter().getI1()))
                    .I2(Utils.floatFromString(deviceInfo.getUiParameter().getI2()))
                    .I3(Utils.floatFromString(deviceInfo.getUiParameter().getI3()))
                    .Bc1(Utils.floatFromString(deviceInfo.getUiParameter().getBrI1()))
                    .Bc2(Utils.floatFromString(deviceInfo.getUiParameter().getBrI2()))
                    .Bc3(Utils.floatFromString(deviceInfo.getUiParameter().getBrI3()))
                    .Lc1(Utils.floatFromString(deviceInfo.getUiParameter().getLeakI1()))
                    .Lc2(Utils.floatFromString(deviceInfo.getUiParameter().getLeakI2()))
                    .Lc3(Utils.floatFromString(deviceInfo.getUiParameter().getLeakI3()))
                    .Kwh(Utils.floatFromString(deviceInfo.getUiParameter().getPower()))
                    .MainLc(Utils.floatFromString(deviceInfo.getUiParameter().getLeakMainI()))
                    .build());
        }

        Finish request = finishBuilder.build();
        Log.e(LiveService.class.getCanonicalName(), "FinishRequest [" + new Gson().toJson(request) + "]");
        Call<FinishResp> finishRespCall = api.finish(dv.id, request);
        finishRespCall.enqueue(new Callback<FinishResp>() {
            @Override
            public void onResponse(Call<FinishResp> call, Response<FinishResp> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }
                FinishResp result = response.body();
                FinishResponse finishResponse = FinishResponse.builder()
                        .message(result.getMessage())
                        .date(result.getFinishtime())
                        .build();
                context.onReceiveResponse(Action.SEND_FINISH, finishResponse);
            }

            @Override
            public void onFailure(Call<FinishResp> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
        return false;
    }

    @Override
    public boolean checkExist(DeviceType dv, String id) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return false;
        }
        context.showProgress();
        Call<CheckExistResp> checkExistRespCall = api.checkExist(id);
        checkExistRespCall.enqueue(new Callback<CheckExistResp>() {
            @Override
            public void onResponse(Call<CheckExistResp> call, Response<CheckExistResp> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                CheckExistResp resp = response.body();
                Log.e(LiveService.class.getCanonicalName(), new Gson().toJson(resp));

                CheckExistResponse checkExistResponse = CheckExistResponse.builder()
                        .exist(resp.isExist())
                        .id(resp.getId())
                        .build();
                handler.post(() -> {
                    context.onReceiveResponse(Action.CHECK_EXIST, checkExistResponse);
                    context.dismissProgress();
                });
            }

            @Override
            public void onFailure(Call<CheckExistResp> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
        return false;
    }

    @Override
    public void getInfo(DeviceType dv, String id) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return;
        }
        context.showProgress();
        Call<GetDeviceInfoResp> checkExistRespCall = api.getInfo(dv == DeviceType.CABINET ? "Tu" : "Den", id);
        checkExistRespCall.enqueue(new Callback<GetDeviceInfoResp>() {
            @Override
            public void onResponse(Call<GetDeviceInfoResp> call, Response<GetDeviceInfoResp> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }

                GetDeviceInfoResp resp = response.body();
                Log.e(LiveService.class.getCanonicalName(), "Get-Info of Id [" + id + "] value [" + new Gson().toJson(resp) + "]");

                DeviceInfo deviceInfo = DeviceInfo.builder()
                        .name(resp.getAmtid())
                        //.id(dv == DeviceType.CABINET ? el.getIdtu() : el.getIdden())
                        .id(resp.getIdtu())
                        .date(resp.getTimeErr())
                        .phase(resp.getPha())
                        .gps(resp.getGps())
                        .build();
                DeviceInfoResponse checkExistResponse = DeviceInfoResponse.builder()
                        .deviceInfo(deviceInfo)
                        .build();
                handler.post(() -> {
                    //context.onReceiveResponse(Action.GET_INFO, checkExistResponse);
                    //context.dismissProgress();
                    infoDevice(dv, deviceInfo);
                });
            }

            @Override
            public void onFailure(Call<GetDeviceInfoResp> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public AddCabinetResp addCabinet(DeviceType dv, String id, String qr, String gps, String Iddg, String Idqh, String Idtp) {
        if (!Utils.checkTrial()) {
            context.showMessage(context.getString(R.string.trial_expired));
            return null;
        }
        context.showProgress();
        AddCabinet request = AddCabinet.builder()
                .Idtu(id).qr(qr).gps(gps)
                .Iddg(Iddg).Idqh(Idqh).Idtp(Idtp)
                .build();
        Log.e(LiveService.class.getCanonicalName(), "Add Cabinet id [" + id + "] body [" + new Gson().toJson(request) + "]");

        Call<AddCabinetResp> checkExistRespCall = api.addCabinet(request);
        checkExistRespCall.enqueue(new Callback<AddCabinetResp>() {
            @Override
            public void onResponse(Call<AddCabinetResp> call, Response<AddCabinetResp> response) {
                if (!response.isSuccessful()) {
                    onError(response.errorBody(), response.message(), response.code());
                    return;
                }
                AddCabinetResp resp = response.body();
                try {
                    Log.e(LiveService.class.getCanonicalName(), "Add-Cabinet-Response [" + response.message() + "]");
                } catch (Exception e) {
                    Log.e(LiveService.class.getCanonicalName(), "", e);
                }

                CheckExistResponse checkExistResponse = CheckExistResponse.builder()
                        .build();
                handler.post(() -> {
                    context.onReceiveResponse(Action.ADD_CABINET, checkExistResponse);
                    context.dismissProgress();
                });
            }

            @Override
            public void onFailure(Call<AddCabinetResp> call, Throwable t) {
                context.dismissProgress();
                Log.w(LiveService.class.getCanonicalName(), "", t);
                context.showMessage(t.getMessage());
            }
        });
        return null;
    }

    @Override
    public List<AddressLocation> getCityProvince() {
        if (!Utils.checkTrial()) {
            handler.post(() -> {
                context.showMessage(context.getString(R.string.trial_expired));
            });
            return Collections.emptyList();
        }
        //context.showProgress();
        List<AddressLocation> addrs = new ArrayList<>();
        try {
            Call<List<ThanhPho>> apiThanhPho = api.getThanhPho();
            List<ThanhPho> listTp = apiThanhPho.execute().body();
            Log.e(LiveService.class.getCanonicalName(), "List-ThanhPho [" + new Gson().toJson(listTp) + "]");
            listTp.forEach(thanhPho -> {
                addrs.add(AddressLocation.builder()
                        .id(thanhPho.getIdtp())
                        .name(thanhPho.getThPho())
                        .build());
            });
        } catch (Exception t) {
            Log.w(LiveService.class.getCanonicalName(), "", t);
            //context.showMessage(t.getMessage());
        } finally {
            //context.dismissProgress();
        }
        return addrs;

//        apiThanhPho.enqueue(new Callback<List<ThanhPho>>() {
//            @Override
//            public void onResponse(Call<List<ThanhPho>> call, Response<List<ThanhPho>> response) {
//                if (!response.isSuccessful()) {
//                    onError(response.errorBody(), response.message(), response.code());
//                    return;
//                }
//
//                List<ThanhPho> resp = response.body();
//                Log.e(LiveService.class.getCanonicalName(), "List-ThanhPho [" + new Gson().toJson(resp) + "]");
//
//                handler.post(() -> {
//                    //context.onReceiveResponse(Action.GET_INFO, checkExistResponse);
//                    //context.dismissProgress();
//                    infoDevice(dv, deviceInfo);
//                });
//            }
//
//            @Override
//            public void onFailure(Call<List<ThanhPho>> call, Throwable t) {
//                context.dismissProgress();
//                Log.w(LiveService.class.getCanonicalName(), "", t);
//                context.showMessage(t.getMessage());
//            }
//        });
//        return null;
    }

    @Override
    public List<AddressLocation> getDistrict(String city) {
        if (!Utils.checkTrial()) {
            handler.post(() -> {
                context.showMessage(context.getString(R.string.trial_expired));
            });
            return Collections.emptyList();
        }
        //context.showProgress();
        List<AddressLocation> addrs = new ArrayList<>();
        try {
            Call<List<QuanHuyen>> apiQuan = api.getQuan(city);
            List<QuanHuyen> listQuan = apiQuan.execute().body();
            Log.e(LiveService.class.getCanonicalName(), "List-QuanHuyen [" + new Gson().toJson(listQuan) + "]");
            listQuan.forEach(quanHuyen -> {
                addrs.add(AddressLocation.builder()
                        .id(quanHuyen.getIdqh())
                        .name(quanHuyen.getQuanHuyen())
                        .build());
            });
        } catch (Exception t) {
            Log.w(LiveService.class.getCanonicalName(), "", t);
            //context.showMessage(t.getMessage());
        } finally {
            //context.dismissProgress();
        }
        return addrs;
    }

    @Override
    public List<AddressLocation> getStreet(String dist) {
        if (!Utils.checkTrial()) {
            handler.post(() -> {
                context.showMessage(context.getString(R.string.trial_expired));
            });
            return Collections.emptyList();
        }
        //context.showProgress();
        List<AddressLocation> addrs = new ArrayList<>();
        try {
            Call<List<Duong>> apiDuong = api.getDuong(dist);
            List<Duong> listDuong = apiDuong.execute().body();
            Log.e(LiveService.class.getCanonicalName(), "List-Duong [" + new Gson().toJson(listDuong) + "]");
            listDuong.forEach(duong -> {
                addrs.add(AddressLocation.builder()
                        .id(duong.getIddg())
                        .name(duong.getTen())
                        .build());
            });
        } catch (Exception t) {
            Log.w(LiveService.class.getCanonicalName(), "", t);
            //context.showMessage(t.getMessage());
        } finally {
            //context.dismissProgress();
        }
        return addrs;
    }

    @Override
    public boolean resetPassword(String username) {
        if (!Utils.checkTrial()) {
            handler.post(() -> {
                context.showMessage(context.getString(R.string.trial_expired));
            });
            return false;
        }
        handler.post(() -> {
            context.showProgress();
        });
        try {
            Call<ResetPassResp> resetPass = api.resetPass(username);
            Response<ResetPassResp> response = resetPass.execute();
            if (!response.isSuccessful()) {
                handler.post(() -> {
                    //context.showMessage(response.message());
                    onError(response.errorBody(), response.message(), response.code());
                });
                return false;
            }

            ResetPassResp msg = response.body();
            Log.e(LiveService.class.getCanonicalName(), "ResetPass [" + new Gson().toJson(msg) + "]");

            return msg.isSuccess();
        } catch (Exception t) {
            Log.e(LiveService.class.getCanonicalName(), "", t);
            handler.post(() -> {
                context.showMessage(t.getMessage());
            });
            return false;
        } finally {
            handler.post(() -> {
                context.dismissProgress();
            });
        }
    }

    @Override
    public boolean newAccount(String fullName, String username, String password, String email, String phone) {
        if (!Utils.checkTrial()) {
            handler.post(() -> {
                context.showMessage(context.getString(R.string.trial_expired));
            });
            return false;
        }
        handler.post(() -> {
            context.showProgress();
        });
        try {
            Register register = Register.builder()
                    .Email(email).FullName(fullName).Password(password).PhoneNumber(phone).UserName(username)
                    .build();
            Log.e(LiveService.class.getCanonicalName(), "NewAccount-Request [" + new Gson().toJson(register) + "]");
            Call<RegisterResp> resetPass = api.newAccount(register);
            Response<RegisterResp> response = resetPass.execute();
            if (!response.isSuccessful()) {
                handler.post(() -> {
                    //context.showMessage(response.message());
                    onError(response.errorBody(), response.message(), response.code());
                });
                return false;
            }
            RegisterResp msg = response.body();
            Log.e(LiveService.class.getCanonicalName(), "NewAccount-Response [" + new Gson().toJson(msg) + "]");

            return msg.isSuccess();
        } catch (Exception t) {
            Log.e(LiveService.class.getCanonicalName(), "", t);
            handler.post(() -> {
                context.showMessage(t.getMessage());
            });
            return false;
        } finally {
            handler.post(() -> {
                context.dismissProgress();
            });
        }
    }

    private boolean onError(ResponseBody body, String mess, int code) {
        try {
            Log.e(LiveService.class.getCanonicalName(), "HttpError code: [" + code + "], mess [" + mess + "]");

            String msg = null;
            JSONObject jObjError;
            String bodyString = body.string();
            if (body == null || bodyString == null || bodyString.isEmpty()) {
                jObjError = null;
            } else {
                try {
                    jObjError = new JSONObject(bodyString);
                } catch (Exception exxxx) {
                    msg = bodyString;
                    jObjError = null;
                }
            }

            if (jObjError != null) {
                if (jObjError.has("Error")) {
                    msg = jObjError.getString("Error");
                }
//                if (msg == null) {
//                    if (jObjError.has("title")) {
//                        msg = jObjError.getString("title");
//                    }
//                }
            }
            if (msg == null) {
                switch (code) {
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        msg = context.getResources().getString(R.string.err_not_found);
                        break;
                    case HttpURLConnection.HTTP_CONFLICT:
                        msg = context.getResources().getString(R.string.err_conflict);
                        break;
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                        msg = context.getResources().getString(R.string.err_server);
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        msg = context.getResources().getString(R.string.err_bidden);
                        break;
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        msg = context.getResources().getString(R.string.err_unauthorize);
                        break;
                }
            }

            if (msg == null) {
                msg = mess;
            }
            if (msg == null) {
                msg = context.getResources().getString(R.string.msg_unkown_error);
            }
            context.dismissProgress();
            context.showMessage(msg);
            Log.e(LiveService.class.getCanonicalName(), msg);
            if (code == 401) {
                context.onEndSession();
            }
        } catch (Exception ex) {
            context.dismissProgress();
            context.showMessage(ex.getMessage());
        }

        return false;
    }
}
