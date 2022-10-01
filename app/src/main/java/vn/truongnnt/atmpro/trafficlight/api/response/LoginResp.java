package vn.truongnnt.atmpro.trafficlight.api.response;

import lombok.Getter;

@Getter
public class LoginResp {
    private boolean Success;
    private String Error;
    private String Username;
    private String UserID;
    private String FullName;
    private String PhoneNumber;
    private String Email;
    private String Logintime;
    private String token;
    private String expiration;
}
