package com.wing.mapp.common.codec;

import com.esotericsoftware.kryo.Serializer;
import com.wing.mapp.remoting.exchange.Request;
import com.wing.mapp.remoting.exchange.Response;
import de.javakaffee.kryoserializers.*;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wanghl on 2017/4/17.
 */
public class KryoReflectionFactory extends KryoReflectionFactorySupport
{
    public KryoReflectionFactory()
    {
        setRegistrationRequired(false);
        setReferences(true);
        register(Request.class, new RequestSerializer());
        register(Response.class, new ResponseSerializer());
        register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        register(Pattern.class, new RegexSerializer());
        register(BitSet.class, new BitSetSerializer());
        register(URI.class, new URISerializer());
        register(UUID.class, new UUIDSerializer());
        register(GregorianCalendar.class, new GregorianCalendarSerializer());
        register(InvocationHandler.class, new JdkProxySerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(this);
        SynchronizedCollectionsSerializer.registerSerializers(this);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Serializer<?> getDefaultSerializer(Class clazz)
    {
        if(EnumSet.class.isAssignableFrom(clazz))
            return new EnumSetSerializer();

        if(EnumMap.class.isAssignableFrom(clazz))
            return new EnumMapSerializer();

        if(Collection.class.isAssignableFrom(clazz))
            return new CopyForIterateCollectionSerializer();

        if(Map.class.isAssignableFrom(clazz))
            return new CopyForIterateMapSerializer();

        if(Date.class.isAssignableFrom(clazz))
            return new DateSerializer( clazz );

        if (SubListSerializers.ArrayListSubListSerializer.canSerialize(clazz)
                || SubListSerializers.JavaUtilSubListSerializer.canSerialize(clazz))
            return SubListSerializers.createFor(clazz);

        return super.getDefaultSerializer(clazz);
    }
}
