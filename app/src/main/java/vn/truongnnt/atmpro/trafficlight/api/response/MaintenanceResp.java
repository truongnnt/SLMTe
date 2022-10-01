package vn.truongnnt.atmpro.trafficlight.api.response;

import java.util.Date;

import lombok.Getter;

@Getter
public class MaintenanceResp extends Device{
    private Date TimeErr;
    private String Amtid;
    private String Pha;
    private String Gps;
}
