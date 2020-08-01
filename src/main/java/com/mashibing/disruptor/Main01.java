package com.mashibing.disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.nio.ByteBuffer;

public class Main01
{


    /**
     * 通过事件来传递数据
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory,
                                                         bufferSize,//ringbuffer大小
                                                         Executors.defaultThreadFactory(),//线程工厂
                                                         ProducerType.SINGLE,//单个生成者
                                                         new YieldingWaitStrategy()//等待策略
                                                     );

        // Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //官方例程
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            LongEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequenceEventTranslator
            event.set(8888L);  // Fill with data
        }
        finally
        {
            ringBuffer.publish(sequence);
        }

    }
}