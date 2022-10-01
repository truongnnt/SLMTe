package vn.truongnnt.atmpro.trafficlight.api.request;

import lombok.Builder;

@Builder
public class Register {
    private String UserName;
    private String Password;
    private String PhoneNumber;
    private String Email;
    private String FullName;
}
