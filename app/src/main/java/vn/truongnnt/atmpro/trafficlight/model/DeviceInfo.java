package vn.truongnnt.atmpro.trafficlight.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DeviceInfo extends GeneralInfo {

    private String location;
    private String gps;

    private String phase;

    private Date date;

    //private List<UISupplyInfo> equipmentActions;

    //private List<UISupplyInfo> equipmentChecks;

    private UIParameter uiParameter;
    private List<UISupplyInfo> uiSupplyInfoList;
    private UISavePrint uiSavePrint;

    private List<String> test;

//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }
//
//    public List<EquipmentInfo> getEquipmentInfoList() {
//        return equipmentInfoList;
//    }
//
//    public void setEquipmentInfoList(List<EquipmentInfo> equipmentInfoList) {
//        this.equipmentInfoList = equipmentInfoList;
//    }
//
//    public static class DeviceInfoBuilder {
//        private String name;
//        private String location;
//        private Date date;
//        private List<EquipmentInfo> equipmentInfoList;
//
//        public DeviceInfoBuilder date(Date dt) {
//            this.date = dt;
//            return this;
//        }
//
//        public DeviceInfoBuilder name(String nam) {
//            this.name = nam;
//            return this;
//        }
//        public DeviceInfoBuilder location(String location) {
//            this.location = location;
//            return this;
//        }
//
//        public DeviceInfoBuilder equipmentInfoList(List<EquipmentInfo> eqList) {
//            this.equipmentInfoList = eqList;
//            return this;
//        }
//
//
//        public DeviceInfo build() {
//            DeviceInfo deviceInfo = new DeviceInfo();
//            deviceInfo.name = this.name;
//            deviceInfo.location = this.location;
//            deviceInfo.date = this.date;
//            deviceInfo.equipmentInfoList = this.equipmentInfoList;
//            return deviceInfo;
//        }
//    }
}
