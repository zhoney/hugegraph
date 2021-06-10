package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableMap;

public class RaysApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/rays";

    @Before
    public void prepareSchema() {
        BaseApiTest.initPropertyKey();
        BaseApiTest.initVertexLabel();
        BaseApiTest.initEdgeLabel();
        BaseApiTest.initVertex();
        BaseApiTest.initEdge();
    }

    @Test
    public void testGet() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String vadasId = name2Ids.get("vadas");
        Response r = client().get(path, ImmutableMap.of("source",
                                                        id2Json(markoId),
                                                        "max_depth", 10));
        String respBody = assertResponseStatus(200, r);
        List<Map<String, List<String>>> rays = assertJsonContains(respBody,
                                                                  "rays");
        assertNotNull(rays);
        assertEquals(2, rays.size());
        Object[] valuesArray = rays.get(0).values().toArray();
        assertNotEquals(0, valuesArray.length);
        List<String> values = (List<String>) valuesArray[0];
        assertTrue(values.contains(vadasId));
    }
}
