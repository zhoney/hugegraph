package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class KoutApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/kout";

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
        String peterId = name2Ids.get("peter");
        String joshId = name2Ids.get("josh");
        Response r = client().get(path,
                                  ImmutableMap.of("source", id2Json(markoId),
                                                  "max_depth", 2));
        String respBody = assertResponseStatus(200, r);
        List<String> vertices = assertJsonContains(respBody, "vertices");
        assertEquals(1, vertices.size());
        assertTrue(vertices.containsAll(ImmutableList.of(peterId, joshId)));
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
                                       + "  \"degree\": 10000, "
                                       + "  \"skip_degree\": 100000}, "
                                       + "\"max_depth\": 1, "
                                       + "\"nearest\": true, "
                                       + "\"limit\": 10000, "
                                       + "\"with_vertex\": true, "
                                       + "\"with_path\": true}", markoId);
        Response resp = client().post(path, reqBody);
        String respBody = assertResponseStatus(200, resp);
        Map<String, Object> entity = parseMap(respBody);
        assertMapContains(entity, "size");
        assertMapContains(entity, "kout");
        assertMapContains(entity, "paths");
        assertMapContains(entity, "vertices");
        assertEquals(2, entity.get("size"));
    }
}
