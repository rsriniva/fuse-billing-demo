package com.redhat.demo.billing;

import java.util.ArrayList;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class GenDataRoute extends RouteBuilder {
  private DataGenerator dataGenerator;
  private DataConverter dataConverter;
  
  @Override
  public void configure() throws Exception {
    // genData route
    from("timer:foo?period=5000")
      .setBody()
        .method(dataGenerator, "genRandomData")
      .marshal()
        .json(JsonLibrary.Jackson)
    .to("file:src/data")
    .routeId("genData");
    
    // processData route
    from("file:src/data?delete=true")
      .unmarshal()
        .json(JsonLibrary.Jackson, ArrayList.class)
      .bean(dataConverter, "processData")
      .split()
        .simple("${body}")
      .choice()
        .when()
          .simple("${body.sameAreaCode}")
            .bean(dataConverter, "calculateChargeInSameArea")
            .to("direct:persistDB")
        .otherwise()
          .bean(dataConverter, "calculateChargeNormal")
          .to("direct:persistDB")
    .routeId("processData");
    
    // persistDB route
    from("direct:persistDB")
      .setHeader("currentCall")
        .simple("${body}")
      .setBody()
        .simple("SELECT \"charge\" FROM \"PhoneCharge\" where \"PhoneNo\" = '${headers.currentCall.sender}'")
      .to("jdbc:dataSourcePS")
      .choice()
        .when()
          .simple("${body.size} == 0")
          .setBody()
            .simple("INSERT INTO \"PhoneCharge\"(\"PhoneNo\", charge)"
                + "VALUES('${headers.currentCall.sender}', ${headers.currentCall.charge}');")
            .endChoice()
        .otherwise()
          .bean(dataConverter, "sumup(${body[0][charge]}, ${headers.currentCall})")
          .setBody()
            .simple("UPDATE \"PhoneCharge\" SET \"charge\" = ${body.charge} WHERE \"PhoneNo\" = '${body.sender}';")
          .endChoice()
      .log("${body}")
      .to("jdbc:dataSourcePS")  
    .routeId("persistDB");
  }

  public DataGenerator getDataGenerator() {
    return dataGenerator;
  }
  public void setDataGenerator(DataGenerator dataGenerator) {
    this.dataGenerator = dataGenerator;
  }
  public DataConverter getDataConverter() {
    return dataConverter;
  }
  public void setDataConverter(DataConverter dataConverter) {
    this.dataConverter = dataConverter;
  }
}
