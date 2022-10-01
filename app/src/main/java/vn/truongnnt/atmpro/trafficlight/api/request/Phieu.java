package vn.truongnnt.atmpro.trafficlight.api.request;

import lombok.Builder;

@Builder
public class Phieu {
    private String Iddv;
    private int LoaiSua;
    private String NgayGio;
}
