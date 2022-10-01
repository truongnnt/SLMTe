package vn.truongnnt.atmpro.trafficlight.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UIParameter extends GeneralInfo {
    private String u1;
    private String u2;
    private String u3;

    private String i1;
    private String i2;
    private String i3;

    private String brI1;
    private String brI2;
    private String brI3;

    private String leakMainI;
    private String leakI1;
    private String leakI2;
    private String leakI3;

    private String power;
}
