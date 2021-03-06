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

package com.spotify.heroic.statistics.semantic;

import com.spotify.heroic.statistics.FutureReporter;
import com.spotify.heroic.statistics.FutureReporter.Context;
import com.spotify.heroic.statistics.MetricBackendReporter;
import com.spotify.heroic.statistics.ThreadPoolReporter;
import com.spotify.metrics.core.MetricId;
import com.spotify.metrics.core.SemanticMetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString(of = {"base"})
@RequiredArgsConstructor
public class SemanticMetricBackendReporter implements MetricBackendReporter {
    private static final String COMPONENT = "metric-backend";

    private final MetricId base;
    private final SemanticMetricRegistry registry;
    private final FutureReporter writeBatch;
    private final FutureReporter write;
    private final FutureReporter fetch;

    public SemanticMetricBackendReporter(SemanticMetricRegistry registry, String id) {
        this.registry = registry;

        this.base = MetricId.build().tagged("component", COMPONENT, "id", id);

        this.writeBatch = new SemanticFutureReporter(registry,
            base.tagged("what", "write-batch", "unit", Units.WRITE));
        this.write =
            new SemanticFutureReporter(registry, base.tagged("what", "write", "unit", Units.WRITE));
        this.fetch =
            new SemanticFutureReporter(registry, base.tagged("what", "fetch", "unit", Units.READ));
    }

    @Override
    public Context reportWrite() {
        return write.setup();
    }

    @Override
    public Context reportWriteBatch() {
        return writeBatch.setup();
    }

    @Override
    public Context reportFetch() {
        return fetch.setup();
    }

    @Override
    public ThreadPoolReporter newThreadPool() {
        return new SemanticThreadPoolReporter(registry, base);
    }
}
