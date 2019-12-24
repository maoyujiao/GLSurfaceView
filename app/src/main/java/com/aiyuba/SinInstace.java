package com.aiyuba;

/**
 * Created by maoyujiao on 2019/12/24.
 */

public class SinInstace {

    private SinInstace() {
    }

    public static SinInstace getSinInstace(){
        return SinInstaceHolder.instace;
    }

    static class SinInstaceHolder{
        public static final SinInstace instace = new SinInstace();
    }
}
