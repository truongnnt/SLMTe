package vn.truongnnt.atmpro.trafficlight.async.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongnnt.atmpro.trafficlight.model.SysInfoType;

@Builder
@Getter
@Setter
public class SysInfo implements Serializable {


    private SysInfoType type;
    private Date time;
    private int count;

//    public SysInfo(SysInfoType type) {
//        this.type = type;
//    }
//
//    public SysInfoType getType() {
//        return type;
//    }
//
//    public void setType(SysInfoType type) {
//        this.type = type;
//    }
//
//    public Date getTime() {
//        return time;
//    }
//
//    public void setTime(Date time) {
//        this.time = time;
//    }
//
//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }

}
