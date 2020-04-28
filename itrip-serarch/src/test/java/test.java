import cn.itrip.search.service.Hotel;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class test {
@Test
    public void test() throws IOException, SolrServerException {
    String url="http://localhost:8088/solr/hotel";
    HttpSolrClient client=new HttpSolrClient.Builder(url).withConnectionTimeout(1000).withSocketTimeout(60000).build();
    SolrQuery query=new SolrQuery("*:*");
    query.addFilterQuery("cityId:"+2);

    query.setStart(0);
    query.setRows(3);
    QueryResponse response=client.query(query);
    List<Hotel> list=response.getBeans(Hotel.class);
    System.out.println(list);
}
}
