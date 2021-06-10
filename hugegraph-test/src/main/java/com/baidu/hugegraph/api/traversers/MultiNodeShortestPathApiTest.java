package com.baidu.hugegraph.api.traversers;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;

public class MultiNodeShortestPathApiTest extends BaseApiTest {

    final String path = "graphs/hugegraph/traversers/multinodeshortestpath";

    @Before
    public void prepareSchema() {
        BaseApiTest.initPropertyKey();
        BaseApiTest.initVertexLabel();
        BaseApiTest.initEdgeLabel();
        BaseApiTest.initVertex();
        BaseApiTest.initEdge();
    }

    @Test
    public void testPost() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String peterId = name2Ids.get("peter");
        String joshId = name2Ids.get("josh");
        String vadasId = name2Ids.get("vadas");
        String reqBody = String.format("{ "
                                       + "\"vertices\": { "
                                       + "  \"ids\": [\"%s\", \"%s\", \"%s\", "
                                       + "    \"%s\"]}, "
                                       + "\"step\": { "
                                       + "  \"direction\": \"BOTH\", "
                                       + "  \"properties\": {}}, "
                                       + "\"max_depth\": 10, "
                                       + "\"capacity\": 100000000, "
                                       + "\"with_vertex\": true}",
                                       markoId, peterId, joshId, vadasId);
        Response r = client().post(path, reqBody);
        String respJosn = assertResponseStatus(200, r);
        Map<String, Object> entity = parseMap(respJosn);
        assertMapContains(entity, "paths");
        assertMapContains(entity, "vertices");
    }
}
