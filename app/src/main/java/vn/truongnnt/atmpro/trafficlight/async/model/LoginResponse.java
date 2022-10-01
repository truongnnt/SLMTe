package vn.truongnnt.atmpro.trafficlight.async.model;

import java.util.List;
import java.util.Map;

import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;

public class LoginResponse extends ServiceResponse {
    private Map<String, String> userInfo;

    public Map<String, String> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, String> userInfo) {
        this.userInfo = userInfo;
    }
}
