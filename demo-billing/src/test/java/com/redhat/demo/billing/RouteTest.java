package com.redhat.demo.billing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

public class RouteTest extends CamelTestSupport {
  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    DataGenerator dataGen = new DataGenerator();
    DataConverter dataConv = new DataConverter();
    GenDataRoute rb = new GenDataRoute();
    rb.setDataGenerator(dataGen);
    rb.setDataConverter(dataConv);
    return rb;
  }
  
  @Override
  protected JndiRegistry createRegistry() throws Exception {
    JndiRegistry reg = super.createRegistry();
    BasicDataSource bds = new BasicDataSource();
    bds.setDriverClassName("org.h2.Driver");
    bds.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    reg.bind("dataSourcePS", bds);
    
    try {
      Connection conn = bds.getConnection();
      PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS \"PhoneCharge\"(\"PhoneNo\" character(12) NOT NULL, "
          + "\"charge\" numeric, "
          + "CONSTRAINT \"PhoneCharge_pkey\" PRIMARY KEY(\"PhoneNo\"));");
      stmt.execute();
    } catch(Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    return reg;
  }
  
  @Produce(uri = "file:src/data?delete=true")
  protected ProducerTemplate fileTemplate;

  @Override
  public String isMockEndpoints() {
    return "*";
  }

  @Test
  public void testGenDataRoute() throws Exception {
    MockEndpoint fileEndpoint = getMockEndpoint("mock:file:src/data");
    fileEndpoint.expectedMessageCount(1);
    assertNotNull(context.hasEndpoint("timer:foo?period=5000"));
    assertNotNull(context.hasEndpoint("file:src/data"));
    
    assertMockEndpointsSatisfied(6, TimeUnit.SECONDS);
  }

  @Test
  public void testProcessDataRoute() throws Exception {
    MockEndpoint fileEndpoint = getMockEndpoint("mock:direct:persistDB");
    fileEndpoint.expectedMessageCount(1);
    
    fileTemplate.sendBody("[\"123-123-1234\\t123-321-3214\\t50\"]");
    
    assertMockEndpointsSatisfied(2, TimeUnit.SECONDS);
    
  }
}
