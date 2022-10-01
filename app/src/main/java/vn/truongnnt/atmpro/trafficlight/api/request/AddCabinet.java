package vn.truongnnt.atmpro.trafficlight.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongnnt.atmpro.trafficlight.api.response.MaintenanceResp;

@Getter
@Setter
@Builder
public class AddCabinet  {
    protected String Idtu;
    protected String qr;
    protected String gps;
    protected String Iddg;
    protected String Idqh;
    protected String Idtp;
    //protected String NhaSx;
}
