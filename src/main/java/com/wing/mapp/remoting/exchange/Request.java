package com.wing.mapp.remoting.exchange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wanghl on 2017/4/19.
 */
public class Request {
    public static final String HEARTBEAT_EVENT = null;
    private static final AtomicLong INVOKE_ID = new AtomicLong(0);
    private final long    mId;
    private boolean mTwoWay   = true;
    private boolean mEvent = false;
    private Object  mData;

    public Request() {
        mId = newId();
    }
    public Request(long id){
        mId = id;
    }

    public long getId() {
        return mId;
    }


    public boolean isTwoWay() {
        return mTwoWay;
    }

    public void setTwoWay(boolean twoWay) {
        mTwoWay = twoWay;
    }

    public boolean isEvent() {
        return mEvent;
    }
    public void setEvent(String event) {
        mEvent = true;
        mData = event;
    }
    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

    public boolean isHeartbeat() {
        return mEvent && HEARTBEAT_EVENT == mData;
    }

    public void setHeartbeat(boolean isHeartbeat) {
        if (isHeartbeat) {
            setEvent(HEARTBEAT_EVENT);
        }
    }

    private static long newId() {
        // getAndIncrement()增长到MAX_VALUE时，再增长会变为MIN_VALUE，负数也可以做为ID
        return INVOKE_ID.getAndIncrement();
    }

    @Override
    public String toString() {
        return "Request [id=" + mId + ", twoway=" + mTwoWay + ", , data=" + (mData == this ? "this" : mData) + "]";
    }
}
