package vn.truongnnt.atmpro.trafficlight.model;

public enum DeviceType {
    CABINET("Cabinet"),
    LIGHT("Light"),
    POLE("Pole"),
    ;

    public final String id;

    DeviceType(String id) {
        this.id = id;
    }
}
