/*
 * Copyright (c) 2015 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.heroic.statistics.noop;

import com.spotify.heroic.statistics.ConsumerReporter;
import com.spotify.heroic.statistics.ThreadPoolReporter;

public class NoopConsumerReporter implements ConsumerReporter {
    private NoopConsumerReporter() {
    }

    @Override
    public void reportMessageSize(int size) {
    }

    @Override
    public void reportMessageError() {
    }

    @Override
    public void reportConsumerSchemaError() {
    }

    @Override
    public void reportMessageDrift(final long ms) {
    }

    @Override
    public ThreadPoolReporter newThreadPool() {
        return NoopThreadPoolReporter.get();
    }

    private static final NoopConsumerReporter instance = new NoopConsumerReporter();

    public static NoopConsumerReporter get() {
        return instance;
    }
}
