package io.atlasmap.examples.camel.main;


import org.apache.camel.main.Main;

public class Application {

    public static void main(String args[]) throws Exception {
        Main camelMain = new Main();
        camelMain.configure().addRoutesBuilder(Route.class);
        camelMain.run(args);
    }
}
