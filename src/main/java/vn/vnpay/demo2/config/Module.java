package vn.vnpay.demo2.config;

import com.google.inject.AbstractModule;
import vn.vnpay.demo2.service.FeeCommandService;
import vn.vnpay.demo2.service.implement.FeeCommandServiceImpl;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(FeeCommandService.class).to(FeeCommandServiceImpl.class);
    }
}
