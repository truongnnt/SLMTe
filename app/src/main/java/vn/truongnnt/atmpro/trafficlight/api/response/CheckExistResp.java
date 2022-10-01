package vn.truongnnt.atmpro.trafficlight.api.response;

import java.util.Date;

import lombok.Getter;

@Getter
public class CheckExistResp {
    private String Id;
    private boolean Exist;
    //private Date Checktime;
    private String Checktime;
}
