/**
 * 
 */
package com.sogou.map.kubbo.metrics;

import java.util.concurrent.atomic.AtomicLong;

import com.sogou.map.kubbo.common.AbstractAttributable;

/**
 * @author liufuliang
 *
 */
public class Counter extends AbstractAttributable implements Metric, Countable{
    private final AtomicLong adder;

    public Counter() {
        this.adder = new AtomicLong(0);
    }

    /**
     * Increment the counter by one.
     */
    public void inc() {
        inc(1);
    }

    /**
     * Increment the counter by {@code n}.
     *
     * @param n the amount by which the counter will be increased
     */
    public void inc(long n) {
        adder.addAndGet(n);
    }

    /**
     * Decrement the counter by one.
     */
    public void dec() {
        dec(1);
    }

    /**
     * Decrement the counter by {@code n}.
     *
     * @param n the amount by which the counter will be decreased
     */
    public void dec(long n) {
        adder.addAndGet(-n);
    }

    @Override
    public long count() {
        return adder.get();
    }

    @Override
    public long take() {
        long c = count();
        dec(c);
        return c;
    }

    @Override
    public void reset() {
        adder.set(0);
    }
}
