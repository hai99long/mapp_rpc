package com.wing.mapp.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;

/**
 * Created by wanghl on 2017/4/17.
 */
public class ResponseSerializer extends Serializer<Response>
{
    @Override
    public void write(Kryo kryo, Output output, Response response) {
        output.writeLong(response.getId());
        output.writeByte(response.getStatus());
        output.writeBoolean(response.isEvent());
        output.writeBoolean(response.isHeartbeat());
        kryo.writeClassAndObject(output,response.getResult());
    }

    @Override
    public Response read(Kryo kryo, Input input, Class<Response> responseClass) {
        Response response = null;
        long id = input.readLong();
        byte status = input.readByte();
        boolean event = input.readBoolean();
        boolean heartBeat = input.readBoolean();
        Object result = kryo.readClassAndObject(input);
        response = new Response(id);
        response.setStatus(status);
        response.setHeartbeat(heartBeat);
        response.setResult(result);
        if(event)
            response.setEvent((String)result);
        return response;
    }
}
