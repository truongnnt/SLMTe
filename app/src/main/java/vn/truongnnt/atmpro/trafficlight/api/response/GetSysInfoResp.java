package vn.truongnnt.atmpro.trafficlight.api.response;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class GetSysInfoResp {

    @SerializedName("Normal")
    private int Normal;
    @SerializedName("Trouble")
    private int Trouble;
    @SerializedName("Maintenance")
    private int Maintenance;
}
