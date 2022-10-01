package vn.truongnnt.atmpro.trafficlight.async;

import java.util.List;
import java.util.Map;

import vn.truongnnt.atmpro.trafficlight.api.response.AddCabinetResp;
import vn.truongnnt.atmpro.trafficlight.async.model.AddressLocation;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.DeviceType;
import vn.truongnnt.atmpro.trafficlight.model.UserInfo;

public interface ConnectService {

    void setUserInfo(UserInfo userInfo);

    Map<String, String> login(String username, String password);

    void getSysInfo();

    List<DeviceInfo> listDevice(DeviceType dv);

    boolean newAccount(String fullName, String username, String password, String email, String phone);

    void infoDevice(DeviceType dv, DeviceInfo deviceInfo);

    void getInfo(DeviceType dv, String id);

    UserInfo getUserInfo();

    boolean sendFinish(DeviceType dv, DeviceInfo deviceInfo);

    boolean checkExist(DeviceType dv, String id);

    AddCabinetResp addCabinet(DeviceType dv, String id, String qr, String gps, String Iddg, String Idqh, String Idtp);

    List<AddressLocation> getCityProvince();

    List<AddressLocation> getDistrict(String city);

    List<AddressLocation> getStreet(String dist);

    boolean resetPassword(String username);
}
