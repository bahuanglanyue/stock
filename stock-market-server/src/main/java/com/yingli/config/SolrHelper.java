package com.yingli.config;

import com.yingli.stock.vo.StockSolr;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class SolrHelper {

    @Value("${solr.enable}")
    private boolean solrEnable;

    @Value("${solr.host}")
    private String solrHost;

    private SolrClient stockSolrClient;

    @PostConstruct
    public void init() {
        if (solrEnable) {
            stockSolrClient = new HttpSolrClient(solrHost + "/stock");
        }
    }

    public boolean isSolrEnable() {
        return solrEnable;
    }

    /**
     * 添加solr
     * @param list
     * @throws Exception
     */
    public void addStock(List<StockSolr> list) throws Exception {
        stockSolrClient.addBeans(list);
        stockSolrClient.commit();
    }
}
