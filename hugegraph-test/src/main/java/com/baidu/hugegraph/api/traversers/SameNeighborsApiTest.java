package com.baidu.hugegraph.api.traversers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.baidu.hugegraph.api.BaseApiTest;
import com.google.common.collect.ImmutableMap;

public class SameNeighborsApiTest extends BaseApiTest {

    final static String path = "graphs/hugegraph/traversers/sameneighbors";

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
        String joshId = name2Ids.get("josh");
        String peterId = name2Ids.get("peter");
        Response r = client().get(path,
                                  ImmutableMap.of("vertex", id2Json(markoId),
                                                  "other", id2Json(joshId)));
        String respBody = assertResponseStatus(200, r);
        List<String> sameNeighbors = assertJsonContains(respBody,
                                                        "same_neighbors");
        assertFalse(sameNeighbors.isEmpty());
        assertTrue(sameNeighbors.contains(peterId));
    }
}
