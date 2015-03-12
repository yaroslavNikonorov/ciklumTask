package server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yar on 11.03.15.
 */
public class Command {
    private String name;
    private Map<String, String> parameters = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
