package components;

import javax.script.*;
import java.io.*;

public class Templator {

    private ScriptEngineManager factory;
    private ScriptEngine engine;
    private Invocable template;

    Templator(String structure) {
        factory = new ScriptEngineManager();
        engine = factory.getEngineByName("JavaScript");
        try {
            // Initializes the templating library itself
            engine.eval(new FileReader("../js/underscore.js"));
            engine.eval(new FileReader("../js/JSON2.js"));
            engine.eval(new FileReader("../js/template.js"));

            // Initializes the template with the structure
            engine.put("structure", structure);
            engine.eval("var tmpl = makeTemplate(structure)");
            template = (Invocable) engine;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @param content this is just a name
     */
    public String generate(String content)
        throws ScriptException, NoSuchMethodException {
        return (String) template.invokeFunction("tmpl", genJSON(content));
    }

    /**
     * @param name here takes the name and puts it in proper JSON format
     */
    public String genJSON(String name) {
        return "{\"name\":\"" + name + "\"}";
    }

    public static void main(String[] args) {
        Templator t = new Templator("Hello <%= name %>");
        try {
            System.out.println(t.generate("{\"name\":\"Michael\"}"));
            System.out.println(t.generate("{\"name\":\"Kenneth\"}"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
