package vn.truongnnt.atmpro.trafficlight.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UISavePrint extends GeneralInfo{
    private String note;
}
