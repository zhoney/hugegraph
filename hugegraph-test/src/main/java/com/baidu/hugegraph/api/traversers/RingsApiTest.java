package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableMap;

public class RingsApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/rings";

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
        Map<String, Object> params = ImmutableMap.of("source",
                                                     id2Json(markoId),
                                                     "max_depth", 10);
        Response r = client().get(path, params);
        String respJson = assertResponseStatus(200, r);
        List<Map<String, List<String>>> rings = assertJsonContains(respJson,
                                                                   "rings");
        assertFalse(rings.isEmpty());
    }
}
