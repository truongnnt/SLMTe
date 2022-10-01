package vn.truongnnt.atmpro.trafficlight.async.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SysInfoResponse extends ServiceResponse {

    private SysInfo normal;
    private SysInfo trouble;
    private SysInfo maintain;

    private int cabinetCount;
    private int lightCount;
    private int poleCount;


//    public int getCabinetCount() {
//        return cabinetCount;
//    }
//
//    public void setCabinetCount(int cabinetCount) {
//        this.cabinetCount = cabinetCount;
//    }
//
//    public int getLightCount() {
//        return lightCount;
//    }
//
//    public void setLightCount(int lightCount) {
//        this.lightCount = lightCount;
//    }
//
//    public int getPoleCount() {
//        return poleCount;
//    }
//
//    public void setPoleCount(int poleCount) {
//        this.poleCount = poleCount;
//    }
//
//    public SysInfo getNormal() {
//        return normal;
//    }
//
//    public void setNormal(SysInfo normal) {
//        this.normal = normal;
//    }
//
//    public SysInfo getTrouble() {
//        return trouble;
//    }
//
//    public void setTrouble(SysInfo trouble) {
//        this.trouble = trouble;
//    }
//
//    public SysInfo getMaintain() {
//        return maintain;
//    }
//
//    public void setMaintain(SysInfo maintain) {
//        this.maintain = maintain;
//    }
}
