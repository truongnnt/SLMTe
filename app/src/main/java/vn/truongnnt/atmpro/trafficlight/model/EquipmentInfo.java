package vn.truongnnt.atmpro.trafficlight.model;

import java.util.Date;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class EquipmentInfo extends GeneralInfo {
    private Date warrantyDate;
    private String led;
//    public Date getWarrantyDate() {
//        return warrantyDate;
//    }
//    public void setWarrantyDate(Date warrantyDate) {
//        this.warrantyDate = warrantyDate;
//    }
//
//    public static class EquilmentInfoBuilder extends GeneralInfoBuilder {
//        private Date warrantyDate;
//
//        public EquilmentInfoBuilder warrantyDate(Date dt) {
//            this.warrantyDate = dt;
//            return this;
//        }
//
//        public EquilmentInfoBuilder name(String nm) {
//            super.name(nm);
//            return this;
//        }
//
//        public EquipmentInfo build() {
//            EquipmentInfo equipmentInfo = new EquipmentInfo();
//            super.buildSetup(equipmentInfo);
//            equipmentInfo.warrantyDate = this.warrantyDate;
//            return equipmentInfo;
//        }
//    }
}
