package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;

public class NeighborRankApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/neighborrank";

    @Before
    public void prepareSchema() {
        BaseApiTest.initPropertyKey();
        BaseApiTest.initVertexLabel();
        BaseApiTest.initEdgeLabel();
        BaseApiTest.initVertex();
        BaseApiTest.initEdge();
    }

    @Test
    public void testNeighborRank() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String reqBody = String.format("{"
                                       + "\"source\":\"%s\","
                                       + "\"steps\": [{"
                                       + "  \"direction\": \"BOTH\"}],"
                                       + "\"alpha\":%s}", markoId, 1);
        Response r = client().post(path, reqBody);
        String respBody = assertResponseStatus(200, r);
        List<Double> ranks = assertJsonContains(respBody, "ranks");
        assertEquals(2, ranks.size());
    }
}
