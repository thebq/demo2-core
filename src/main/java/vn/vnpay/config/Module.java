package vn.vnpay.config;

import com.google.inject.AbstractModule;
import vn.vnpay.service.FeeCommandService;
import vn.vnpay.service.implement.FeeCommandServiceImpl;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(FeeCommandService.class).to(FeeCommandServiceImpl.class);
    }
}
