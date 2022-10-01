package vn.truongnnt.atmpro.trafficlight.model;

import java.util.Date;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class UserInfo extends GeneralInfo{
    private String fullName;
    private String token;
    private Date expiredAt;
}
