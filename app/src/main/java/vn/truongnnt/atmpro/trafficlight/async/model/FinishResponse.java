package vn.truongnnt.atmpro.trafficlight.async.model;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinishResponse extends ServiceResponse{
    private Date date;
    private String message;
}
