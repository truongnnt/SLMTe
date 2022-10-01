package vn.truongnnt.atmpro.trafficlight.async.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckExistResponse extends ServiceResponse{
    private boolean exist;
    private String id;
}
