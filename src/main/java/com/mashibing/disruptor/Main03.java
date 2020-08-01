package com.mashibing.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class Main03
{
    public static void main(String[] args) throws Exception
    {
        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        // Connect the handler
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event));

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();


        //ringBuffer.publishEvent((event, sequence) -> event.set(10000L));
        ringBuffer.publishEvent(new EventTranslatorOneArg<LongEvent, Long>() {
            @Override
            public void translateTo(LongEvent event, long sequence, Long l) {
                event.set(l);
            }
        }, 10000L);
        ringBuffer.publishEvent(new EventTranslatorTwoArg<LongEvent, Long, Long>() {
                                    @Override
                                    public void translateTo(LongEvent event, long sequence, Long l1, Long l2) {
                                        event.set(l1 + l2);
                                    }
                                },
                10000L, 10000L);

        System.in.read();
    }
}