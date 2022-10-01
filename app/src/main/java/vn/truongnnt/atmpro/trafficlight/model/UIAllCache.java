package vn.truongnnt.atmpro.trafficlight.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UIAllCache extends GeneralInfo {

    private UIParameter uiParameter;
    private List<UISupplyInfo> uiEquipmentInfo;
    private UISavePrint uiSavePrint;

}
