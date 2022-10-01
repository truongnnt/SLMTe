package vn.truongnnt.atmpro.trafficlight.async.model;

import java.util.List;

import lombok.Builder;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;

@Builder
public class DeviceListResponse extends ServiceResponse {
    private List<DeviceInfo> deviceInfos;

    public List<DeviceInfo> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(List<DeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }
}
