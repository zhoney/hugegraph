/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;

import com.baidu.hugegraph.config.CoreOptions;
import com.baidu.hugegraph.config.HugeConfig2;
import com.baidu.hugegraph.event.EventHub;
import com.baidu.hugegraph.task.TaskManager;
import com.baidu.hugegraph.traversal.algorithm.OltpTraverser;
import com.baidu.hugegraph.type.define.SerialEnum;
import com.baidu.hugegraph.util.E;
import com.baidu.hugegraph.util.Log;

public class HugeFactory {

    private static final Logger LOG = Log.logger(HugeGraph.class);

    static {
        SerialEnum.registerInternalEnums();
        HugeGraph.registerTraversalStrategies(StandardHugeGraph.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("HugeGraph is shutting down");
            HugeFactory.shutdown(30L);
        }, "hugegraph-shutdown"));
    }

    private static final String NAME_REGEX = "^[A-Za-z][A-Za-z0-9_]{0,47}$";

    private static final Map<String, HugeGraph> graphs = new HashMap<>();

    public static synchronized HugeGraph open(Configuration config) {
        HugeConfig2 conf = config instanceof HugeConfig2 ?
                           (HugeConfig2) config : new HugeConfig2(config);
        return open(conf);
    }

    public static synchronized HugeGraph open(HugeConfig2 config) {
        String name = config.get(CoreOptions.STORE);
        checkGraphName(name, "graph config(like hugegraph.properties)");
        name = name.toLowerCase();
        HugeGraph graph = graphs.get(name);
        if (graph == null || graph.closed()) {
            graph = new StandardHugeGraph(config);
            graphs.put(name, graph);
        } else {
            String backend = config.get(CoreOptions.BACKEND);
            E.checkState(backend.equalsIgnoreCase(graph.backend()),
                         "Graph name '%s' has been used by backend '%s'",
                         name, graph.backend());
        }
        return graph;
    }

    public static HugeGraph open(String path) {
        return open(getLocalConfig(path));
    }

    public static void checkGraphName(String name, String configFile) {
        E.checkArgument(name.matches(NAME_REGEX),
                        "Invalid graph name '%s' in %s, " +
                        "valid graph name is up to 48 alpha-numeric " +
                        "characters and underscores and only letters are " +
                        "supported as first letter. " +
                        "Note: letter is case insensitive", name, configFile);
    }

    public static PropertiesConfiguration getLocalConfig(String path) {
        File file = new File(path);
        E.checkArgument(file.exists() && file.isFile() && file.canRead(),
                        "Please specify a proper config file rather than: %s",
                        file.toString());
        PropertiesConfiguration configs = new PropertiesConfiguration();
        try {
            configs.read(new FileReader(path));
        } catch (ConfigurationException | IOException e) {
            throw new HugeException("Unable to load config file: %s", e, path);
        }
        return configs;
    }

    /**
     * Stop all the daemon threads
     * @param timeout seconds
     */
    public static void shutdown(long timeout) {
        try {
            if (!EventHub.destroy(timeout)) {
                throw new TimeoutException(timeout + "s");
            }
            TaskManager.instance().shutdown(timeout);
            OltpTraverser.destroy();
        } catch (Throwable e) {
            LOG.error("Error while shutdown", e);
            throw new HugeException("Failed to shutdown", e);
        }
    }

    public static void remove(HugeGraph graph) {
        HugeConfig2 config = (HugeConfig2) graph.configuration();
        String name = config.get(CoreOptions.STORE);
        graphs.remove(name);
    }
}
