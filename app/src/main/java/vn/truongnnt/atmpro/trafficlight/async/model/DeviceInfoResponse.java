package vn.truongnnt.atmpro.trafficlight.async.model;

import java.util.List;

import lombok.Builder;
import vn.truongnnt.atmpro.trafficlight.model.DeviceInfo;
import vn.truongnnt.atmpro.trafficlight.model.EquipmentInfo;

@Builder
public class DeviceInfoResponse extends ServiceResponse {

    private DeviceInfo deviceInfo;

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    //    private List<EquipmentInfo> equipmentInfos;
//
//    private String location;
//
//    private String name;
//
//    public List<EquipmentInfo> getEquipmentInfos() {
//        return equipmentInfos;
//    }
//
//    public void setEquipmentInfos(List<EquipmentInfo> equipmentInfos) {
//        this.equipmentInfos = equipmentInfos;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
}
