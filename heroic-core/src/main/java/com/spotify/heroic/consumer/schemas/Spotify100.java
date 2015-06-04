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

package com.spotify.heroic.consumer.schemas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.heroic.consumer.Consumer;
import com.spotify.heroic.consumer.ConsumerSchema;
import com.spotify.heroic.exceptions.ConsumerSchemaException;
import com.spotify.heroic.exceptions.ConsumerSchemaValidationException;
import com.spotify.heroic.exceptions.FatalSchemaException;
import com.spotify.heroic.metric.model.WriteMetric;
import com.spotify.heroic.model.DataPoint;
import com.spotify.heroic.model.Series;

@ToString
public class Spotify100 implements ConsumerSchema {
    private static final String HOST = "host";
    private static final String KEY = "key";
    private static final String TIME = "time";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String SCHEMA_VERSION = "1.0.0";

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metric {
        private final String version;
        private final String key;
        private final String host;
        private final Long time;
        private final Map<String, String> attributes;
        private final Double value;

        @JsonCreator
        public static Metric create(@JsonProperty("version") String version, @JsonProperty("key") String key,
                @JsonProperty("host") String host, @JsonProperty("time") Long time,
                @JsonProperty("attributes") Map<String, String> attributes, @JsonProperty("value") Double value) {
            return new Metric(version, key, host, time, attributes, value);
        }
    }

    @Override
    public void consume(Consumer consumer, byte[] message) throws ConsumerSchemaException {
        final Metric metric;

        try {
            metric = mapper.readValue(message, Metric.class);
        } catch (final Exception e) {
            throw new ConsumerSchemaValidationException("Received invalid metric", e);
        }
        if (metric.getValue() == null) {
            throw new ConsumerSchemaValidationException("Metric must have a value but this metric has a null value: "
                    + metric);
        }

        if (metric.getVersion() == null || !SCHEMA_VERSION.equals(metric.getVersion()))
            throw new ConsumerSchemaValidationException(String.format("Invalid version {}, expected {}",
                    metric.getVersion(), SCHEMA_VERSION));

        if (metric.getTime() == null)
            throw new ConsumerSchemaValidationException("'" + TIME + "' field must be defined: " + message);

        if (metric.getKey() == null)
            throw new ConsumerSchemaValidationException("'" + KEY + "' field must be defined: " + message);

        final Map<String, String> tags = new HashMap<String, String>(metric.getAttributes());
        tags.put(HOST, metric.getHost());

        final Series series = new Series(metric.getKey(), tags);
        final DataPoint datapoint = new DataPoint(metric.getTime(), metric.getValue());
        final List<DataPoint> data = new ArrayList<DataPoint>();
        data.add(datapoint);

        try {
            consumer.write(new WriteMetric(series, data)).get();
        } catch (final Exception e) {
            throw new FatalSchemaException("Write failed", e);
        }
    }
}
