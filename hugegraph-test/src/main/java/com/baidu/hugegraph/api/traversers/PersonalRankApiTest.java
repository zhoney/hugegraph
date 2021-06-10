package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;

public class PersonalRankApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/personalrank";

    @Before
    public void prepareSchema() {
        BaseApiTest.initPropertyKey();
        BaseApiTest.initVertexLabel();
        BaseApiTest.initEdgeLabel();
        BaseApiTest.initVertex();
        BaseApiTest.initEdge();
    }

    @Test
    public void testPersonalRank() {
        Map<String, String> name2Ids = listAllVertexName2Ids();
        String markoId = name2Ids.get("marko");
        String reqBody = String.format("{"
                                       + "\"source\":\"%s\","
                                       + "\"max_depth\":\"%s\","
                                       + "\"label\":\"%s\","
                                       + "\"alpha\":\"%s\"}",
                                       markoId, 3, "created", 1);
        Response r = client().post(path, reqBody);
        String respBody = assertResponseStatus(200, r);
        Map<String, Object> entity = parseMap(respBody);
        assertNotNull(entity);
    }
}
