package vn.truongnnt.atmpro.trafficlight.api.request;

import lombok.Builder;

@Builder
public class Param {
    private float U1;
    private float U2;
    private float U3;

    private float I1;
    private float I2;
    private float I3;

    private float Bc1;
    private float Bc2;
    private float Bc3;

    private float Lc1;
    private float Lc2;
    private float Lc3;

    private float MainLc;
    private float Kwh;
}
