package vn.truongnnt.atmpro.trafficlight.async.model;

import java.io.Serializable;

public abstract class ServiceResponse implements Serializable {
    private ResponseCode code;


    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }
}
