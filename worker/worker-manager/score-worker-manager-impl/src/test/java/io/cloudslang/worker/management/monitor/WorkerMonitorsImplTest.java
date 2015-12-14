/*******************************************************************************
 * (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0 which accompany this distribution.
 *
 * The Apache License is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/

package io.cloudslang.worker.management.monitor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.cloudslang.engine.node.services.WorkerNodeService;
import io.cloudslang.engine.queue.entities.ExecutionMessageConverter;
import io.cloudslang.engine.queue.services.QueueDispatcherService;
import io.cloudslang.engine.queue.services.QueueStateIdGeneratorService;
import io.cloudslang.worker.execution.services.ExecutionService;
import io.cloudslang.worker.management.WorkerConfigurationService;
import io.cloudslang.worker.management.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WorkerMonitorsImplTest.MyTestConfig.class)
public class WorkerMonitorsImplTest {
    @Autowired
    private WorkerManager workerManager;

    @Autowired
    private OutboundBuffer outBuffer;

    @Autowired
    private InBuffer inBuffer;

    @Autowired
    private WorkerMonitors workerMonitors;

    @Before
    public void setUp() {
        reset(workerManager, outBuffer, inBuffer);
    }

    @Test
    public void testWorkerMonitors() {
        when(workerManager.getWorkerUuid()).thenReturn("1");
        when(workerManager.getExecutionThreadsCount()).thenReturn(5);
        when(inBuffer.getCapacity()).thenReturn(55);
        when(outBuffer.getCapacity()).thenReturn(66);


        Map<WorkerMonitorInfoEnum, Serializable> monitorInfo = workerMonitors.getMonitorInfo();
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.TOTAL_MEMORY));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.FREE_MOMORY));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.MAX_MOMORY));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.WROKER_ID));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.EXECUTION_THREADS_AMOUNT));
        assertEquals(5, monitorInfo.get(WorkerMonitorInfoEnum.EXECUTION_THREADS_AMOUNT));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.OUTBUFFER_CAPACITY));
        assertEquals(66, monitorInfo.get(WorkerMonitorInfoEnum.OUTBUFFER_CAPACITY));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.INBUFFER_CAPACITY));
        assertEquals(55, monitorInfo.get(WorkerMonitorInfoEnum.INBUFFER_CAPACITY));

        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_START_TIME));
        assertNotNull(monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_END_TIME));

        Long firstStart = (Long) monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_START_TIME);
        Long firstEnd = (Long) monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_END_TIME);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitorInfo = workerMonitors.getMonitorInfo();

        Long secondStart = (Long) monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_START_TIME);
        Long secondEnd = (Long) monitorInfo.get(WorkerMonitorInfoEnum.MONITOR_END_TIME);

        assertFalse(firstStart.equals(secondStart));
        assertFalse(firstEnd.equals(secondEnd));
    }

    @Configuration
    public static class MyTestConfig {
        @Bean public WorkerMonitors scheduledWorkerLoadMonitor() {return new WorkerMonitorsImpl();}

        @Bean public WorkerManager workerManager() {return mock(WorkerManager.class);}
        @Bean public OutboundBuffer outboundBuffer() {return mock(OutboundBuffer.class);}
        @Bean public WorkerNodeService workerNodeService() {return mock(WorkerNodeService.class);}
        @Bean public WorkerConfigurationService workerConfigurationService() {return mock(WorkerConfigurationService.class);}
        @Bean public WorkerRecoveryManager workerRecoveryManager() {return mock(WorkerRecoveryManager.class);}
        @Bean public Integer numberOfExecutionThreads() {return 1;}
        @Bean public Long initStartUpSleep() {return 1L;}
        @Bean public Long maxStartUpSleep() {return 2L;}
        @Bean public String workerUuid() {return "1";}

        @Bean public WorkerMonitor workerMonitor() {return mock(WorkerMonitor.class);}
        @Bean public InBuffer inBuffer() {return mock(InBuffer.class);}
        @Bean public QueueDispatcherService queueDispatcherService() {return mock(QueueDispatcherService.class);}
        @Bean public Integer inBufferCapacity() {return 1;}
        @Bean public Integer coolDownPollingMillis() {return 1;}

        @Bean public SimpleExecutionRunnableFactory simpleExecutionRunnableFactory() {return mock(SimpleExecutionRunnableFactory.class);}
        @Bean public SynchronizationManager synchronizationManager() {return mock(SynchronizationManager.class);}

        @Bean public ExecutionService executionService() {return mock(ExecutionService.class);}
        @Bean public ExecutionMessageConverter executionMessageConverter() {return mock(ExecutionMessageConverter.class);}
        @Bean public WorkerVersionService workerVersionService() {return mock(WorkerVersionService.class);}
        @Bean public EndExecutionCallback endExecutionCallback() {return mock(EndExecutionCallback.class);}
        @Bean public QueueStateIdGeneratorService queueStateIdGeneratorService() {return mock(QueueStateIdGeneratorService.class);}
    }
}
