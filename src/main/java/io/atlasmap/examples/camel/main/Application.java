package io.atlasmap.examples.camel.main;


import com.google.common.base.Splitter;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Application extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Exception : {}", String.valueOf(exceptionMessage()))
                .stop();

        /*rest("api").produces("APPLICATION_OCTET_STREAM").get().route()
                .routeId(getContext().getName() + ".REQ")

                .startupOrder(11)
                .convertBodyTo(String.class)
                .process(HEADERS_FROM_HTTP_QUERY_PARAMS)
                .removeHeaders("Camel*")
                .removeHeaders("Accept*")
                .removeHeaders("Content-Type*")
                .log(LoggingLevel.INFO, "start ${headers}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {

                        exchange.getMessage().setBody(readFile());
                    }
                })
                .convertBodyTo(byte[].class)
                .setHeader("Content-Disposition", simple("filename=atlasmap-mapping.adm"))
                //.setHeader("Content-Encoding", constant("gzip"))
                //.setHeader("Content-Type", constant("application/zip"))

                .log(LoggingLevel.INFO, "finish ")




        ;*/
        from("file-watch://C:\\Users\\79221\\test3\\"+
                "?events=MODIFY,DELETE&recursive=false&autocreate=false")
                .log("File event: ${header.CamelFileEventType} occurred on file ${header.CamelFileName} at ${header.CamelFileLastModified}")
                ;
        /*from("timer:foo?period=5000")
                .setBody(simple("resource:classpath:order.json"))
                .log("--&gt; Sending: [${body}]")
                .to("atlasmap:atlasmap-mapping.adm")
                .log("--&lt; Received: [${body}]");*/

    }

    public static void main(String args[]) throws Exception {
        Main camelMain = new Main();
        camelMain.configure().addRoutesBuilder(new Application());
        camelMain.run(args);
    }
    public FileInputStream readFile() throws IOException {
        File file = new File("src/main/resources/atlasmap-mapping.adm");
        return new FileInputStream(file);
    }
    private static final Processor HEADERS_FROM_HTTP_QUERY_PARAMS = x ->{
        Map<String, String> result = Splitter.on('&')
                .trimResults()
                .withKeyValueSeparator(
                        Splitter.on('=')
                                .limit(2)
                                .trimResults())
                .split(x.getMessage().getHeader(Exchange.HTTP_QUERY, String.class));
        for (String key : result.keySet()) {
            x.getMessage().setHeader(key, result.get(key));
        }
    };
}
