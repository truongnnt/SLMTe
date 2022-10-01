package vn.truongnnt.atmpro.trafficlight.api.request;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class LoginRequest {
    private String Username;
    private String Password;
}
