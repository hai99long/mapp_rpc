package com.wing.mapp.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wing.mapp.remoting.exchange.Request;

/**
 * Created by wanghl on 2017/4/17.
 */
public class RequestSerializer extends Serializer<Request>
{
    @Override
    public void write(Kryo kryo, Output output, Request request) {
        output.writeLong(request.getId());
        output.writeBoolean(request.isTwoWay());
        output.writeBoolean(request.isEvent());
        output.writeBoolean(request.isHeartbeat());
        kryo.writeClassAndObject(output,request.getData());
    }

    @Override
    public Request read(Kryo kryo, Input input, Class<Request> requestClass) {
        Request request = null;
        long id = input.readLong();
        boolean twoWay = input.readBoolean();
        boolean event = input.readBoolean();
        boolean heartBeat = input.readBoolean();
        Object data = kryo.readClassAndObject(input);
        request = new Request(id);
        request.setTwoWay(twoWay);
        request.setHeartbeat(heartBeat);
        request.setData(data);
        if(event)
            request.setEvent((String)data);
        return request;
    }
}
