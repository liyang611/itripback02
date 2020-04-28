package cn.itrip.search.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.List;

public class SolrUtil {
    public static void main(String[] args) throws IOException, SolrServerException {
        String url="http://localhst:8088/solr/hotel";
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
