package vn.truongnnt.atmpro.trafficlight.async;

import javax.inject.Singleton;

import dagger.Component;
import vn.truongnnt.atmpro.trafficlight.BasicActivity;

@Singleton
@Component(modules = {BasicActivity.class})
public interface ConnectServiceFactory {
    DemoConnectService getDemoInstance();

    LiveService getLiveInstance();
}
