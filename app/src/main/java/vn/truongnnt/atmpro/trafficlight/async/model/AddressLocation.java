package vn.truongnnt.atmpro.trafficlight.async.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressLocation {
    private String id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
