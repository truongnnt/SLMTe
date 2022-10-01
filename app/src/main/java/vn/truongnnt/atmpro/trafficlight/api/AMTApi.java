package vn.truongnnt.atmpro.trafficlight.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.truongnnt.atmpro.trafficlight.api.request.AddCabinet;
import vn.truongnnt.atmpro.trafficlight.api.request.Finish;
import vn.truongnnt.atmpro.trafficlight.api.request.LoginRequest;
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

public interface AMTApi {
    public static final String BASE_URL = "http://45.119.83.12:8899/";

    @POST("/api/Auth/Login")
    Call<LoginResp> login(@Body LoginRequest request);

    @GET("api/App/SysInfo")
    Call<GetSysInfoResp> getSysInfo();

    @GET("api/App/Manager")
    Call<ManagerResp> manager();

    @GET("api/App/Mainlist/{id}")
    Call<List<MaintenanceResp>> listMaintenance(@Path("id") String id);

    @GET("/api/App/Device/{dv}/{id}")
    Call<List<EquipmentInfo>> deviceInfo(@Path("dv") String dv, @Path("id") String id);

    @POST("/api/App/Finish/{dv}")
    Call<FinishResp> finish(@Path("dv") String dv, @Body Finish finish);

    @GET("/api/App/CheckTu/{id}")
    Call<CheckExistResp> checkExist(@Path("id") String id);

    @GET("/api/App/{type}/{id}")
    Call<GetDeviceInfoResp> getInfo(@Path("type") String type, @Path("id") String id);

    @POST("/api/App/CabinetAdd")
    Call<AddCabinetResp> addCabinet(@Body AddCabinet request);

    @GET("/api/App/ThanhPho")
    Call<List<ThanhPho>> getThanhPho();

    @GET("/api/App/Quan/{id}")
    Call<List<QuanHuyen>> getQuan(@Path("id") String tp);

    @GET("/api/App/Duong/{id}")
    Call<List<Duong>> getDuong(@Path("id") String qh);

    @POST("/api/Auth/ResetPass")
    Call<ResetPassResp> resetPass(@Query("usname") String username);

    @POST("/api/Auth/Register")
    Call<RegisterResp> newAccount(@Body Register request);
}
