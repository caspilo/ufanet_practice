package org.example.core.service.delay;

public class DelayProcedures implements DelayService{

    //    private final DelayRepository delayRepository;

    //    public DelayProcedures(DelayRepository delayRepository){
//        this.delayRepository = delayRepository;
//    }
    @Override
    public boolean getRetryStateForTask(Long id) {
        return false;
    }

    @Override
    public long getFixedDelayValue(Long id) {
        return 0;
    }

    @Override
    public boolean isRetryForTaskFixed(Long id) {
        return false;
    }

    @Override
    public int getMaxRetryCount(Long id) {
        return 0;
    }

    @Override
    public double getDelayBase(Long id) {
        return 0;
    }

    @Override
    public long getUpLimit(Long id) {
        return 0;
    }
}
