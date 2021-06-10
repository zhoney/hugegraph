package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableMap;

public class KneighborApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/kneighbor";

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
        Response r = client().get(path,
                                  ImmutableMap.of("source", id2Json(markoId),
                                                  "max_depth", 2));
        String respBody = assertResponseStatus(200, r);
        List<String> vertices = assertJsonContains(respBody, "vertices");
        assertEquals(3, vertices.size());
    }

    @Test
    public void testPost() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String reqBody = String.format("{ "
                                       + "\"source\": \"%s\", "
                                       + "\"step\": { "
                                       + "  \"direction\": \"BOTH\", "
                                       +
                                       "  \"labels\": [\"knows\", " +
                                       "\"created\"], "
                                       + "  \"properties\": { "
                                       + "    \"weight\": \"P.gt(0.1)\"}, "
                                       + "    \"degree\": 10000, "
                                       + "    \"skip_degree\": 100000}, "
                                       + "\"max_depth\": 3, "
                                       + "\"limit\": 10000, "
                                       + "\"with_vertex\": true, "
                                       + "\"with_path\": true}", markoId);
        Response r = client().post(path, reqBody);
        String content = assertResponseStatus(200, r);
        Map<String, Object> entity = parseMap(content);
        assertNotNull(entity);
        assertMapContains(entity, "kneighbor");
        assertMapContains(entity, "paths");
        assertMapContains(entity, "vertices");
    }
}
