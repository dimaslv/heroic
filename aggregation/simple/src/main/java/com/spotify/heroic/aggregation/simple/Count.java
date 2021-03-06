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

package com.spotify.heroic.aggregation.simple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spotify.heroic.aggregation.AggregationContext;
import com.spotify.heroic.aggregation.SamplingQuery;
import com.spotify.heroic.common.Duration;
import com.spotify.heroic.common.Optionals;

import java.util.Optional;

public class Count extends SamplingAggregation {
    public static final String NAME = "count";

    @JsonCreator
    public Count(
        @JsonProperty("sampling") Optional<SamplingQuery> sampling,
        @JsonProperty("size") Optional<Duration> size,
        @JsonProperty("extent") Optional<Duration> extent
    ) {
        super(Optionals.firstPresent(size, sampling.flatMap(SamplingQuery::getSize)),
            Optionals.firstPresent(extent, sampling.flatMap(SamplingQuery::getExtent)));
    }

    @Override
    public CountInstance apply(
        final AggregationContext context, final long size, final long extent
    ) {
        return new CountInstance(size, extent);
    }

    @Override
    public String toDSL() {
        return samplingDSL(NAME);
    }
}
