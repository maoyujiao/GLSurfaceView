package com.aiyuba;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class ProxyFactory {

    private Object sinInstace;

    public ProxyFactory(Object sinInstace) {
        this.sinInstace = sinInstace;
    }

    private Object getProxy(){
        return Proxy.newProxyInstance(sinInstace.getClass().getClassLoader(),sinInstace.getClass().getInterfaces() , new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //执行方法
                method.invoke(sinInstace,args);
                //执行方法
                return null;
            }
        });
    }
}
