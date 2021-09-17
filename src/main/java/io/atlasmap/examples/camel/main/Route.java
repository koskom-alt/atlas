package io.atlasmap.examples.camel.main;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.atlasmap.AtlasMapConstants;
import org.apache.camel.component.atlasmap.AtlasMapEndpoint;

import static org.apache.camel.support.builder.PredicateBuilder.or;

public class Route extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Exception : {}", String.valueOf(exceptionMessage()))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(exceptionMessage())
                .stop();

        rest("api").get().route()
                .routeId(getContext().getName() + ".REQ")
                .startupOrder(11)
                .choice()
                    .when(or(header("template_name").isNull(), header("template_name").isEqualTo("")))
                        .throwException(Exception.class, "Header template_name is required")
                .endChoice().end()
                .setHeader(AtlasMapConstants.ATLAS_RESOURCE_URI, simple("file:C:/test/commonAtlasMap/${header.template_name}"))
                .log(LoggingLevel.INFO, "sent to Endpoint: ${header.CamelAtlasResourceUri}")
                .to("atlasmap:dummy")
                .log(LoggingLevel.INFO, "finish ${body}\n${headers}")
        ;

        from("file-watch:///\\skb/Files/SKB-LAB/Интеграционные сервисы/atlas_temp?autocreate=false")
                .log("File event: ${header.CamelFileEventType} occurred on file ${header.CamelFileName} at ${header.CamelFileLastModified}")
                .process(exchange -> {
                    for (Endpoint e : getContext().getEndpoints()) {
                        if (e instanceof AtlasMapEndpoint) {
                            e.stop();
                            getContext().removeEndpoint(e);
                        }
                    }
                })
        ;
        /*from("file-watch:/C:\\test\\commonAtlasMap\\test3\\")
                .routeId("SRV.COMMON.ATLASMAP.FILE-WATCH")
                .log("File event: ${header.CamelFileEventType} occurred on file ${header.CamelFileName} at ${header.CamelFileLastModified}")
                .process(exchange -> {
                    for (Endpoint e : getContext().getEndpoints()) {
                        if (e instanceof AtlasMapEndpoint) {
                            log.info("Endpoint : {} changed", e.getEndpointUri());
                        }
                    }
                })
        ;*/


    }
}
