package vn.truongnnt.atmpro.trafficlight.api.response;

import java.util.Date;

import lombok.Getter;

@Getter
public class GetDeviceInfoResp extends MaintenanceResp{

    private String Iddg;
    private String Idqh;
    private String Idtp;
    private String NhaSx;
    private String NgaySx;

    private int Idtt;
    private int IsDisable;
}
