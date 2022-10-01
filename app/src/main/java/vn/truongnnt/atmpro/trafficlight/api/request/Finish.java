package vn.truongnnt.atmpro.trafficlight.api.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Finish {
    protected String Id;
    protected String GhiChu;
    private Param Para;
    private List<Phieu> Phieu;
}
