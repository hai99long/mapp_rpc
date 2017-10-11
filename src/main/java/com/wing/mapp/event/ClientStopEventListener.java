/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wing.mapp.event;

import com.google.common.eventbus.Subscribe;
import com.wing.mapp.remoting.transport.netty.NettyClientExecutor;

/**
 * 销毁事件的监听器
 */
public class ClientStopEventListener {
    public int lastMessage = 0;

    @Subscribe
    public void listen(ClientStopEvent event) {
        lastMessage = event.getMessage();
        NettyClientExecutor.getInstance().stop();
    }

    public int getLastMessage() {
        return lastMessage;
    }
}

