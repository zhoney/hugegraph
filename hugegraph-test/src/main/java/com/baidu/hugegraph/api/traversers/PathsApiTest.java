package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableMap;

public class PathsApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/paths";

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
                                                        "target",
                                                        id2Json(vadasId),
                                                        "max_depth", 3));
        String respBody = assertResponseStatus(200, r);
        Map<String, Object> entity = parseMap(respBody);
        assertNotNull(entity);
        assertMapContains(entity, "paths");
        List<Map<String, Object>> paths = assertMapContains(entity, "paths");
        assertEquals(1, paths.size());
    }

    @Test
    public void testPost() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String joshId = name2Ids.get("josh");
        String reqBody = String.format("{ "
                                       + "\"sources\": { "
                                       + "  \"ids\": [\"%s\"] "
                                       + "}, "
                                       + "\"targets\": { "
                                       + "  \"ids\": [\"%s\"]}, "
                                       + "\"step\": { "
                                       + "  \"direction\": \"BOTH\", "
                                       + "  \"properties\": { "
                                       + "    \"weight\": \"P.gt(0.01)\"}}, "
                                       + "\"max_depth\": 10, "
                                       + "\"capacity\": 100000000, "
                                       + "\"limit\": 10000000, "
                                       + "\"with_vertex\": false}",
                                       markoId, joshId);
        Response r = client().post(path, reqBody);
        String respBody = assertResponseStatus(200, r);
        List<Map<String, Object>> paths = assertJsonContains(respBody, "paths");
        assertFalse(paths.isEmpty());
    }
}
