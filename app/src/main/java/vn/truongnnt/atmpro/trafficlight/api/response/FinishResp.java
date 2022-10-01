package vn.truongnnt.atmpro.trafficlight.api.response;

import java.util.Date;

import lombok.Getter;

@Getter
public class FinishResp {
    private String UserId;
    private String Message;
    private Date Finishtime;
}
