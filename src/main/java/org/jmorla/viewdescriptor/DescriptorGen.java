package org.jmorla.viewdescriptor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Name;

public class DescriptorGen {


    public String generate(Map<Name, View> views) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int index = 0;
        for(var view : views.entrySet()) {
            sb.append("\"");
            sb.append(view.getKey());
            sb.append("\":");
            appendDescriptor(view.getValue(), sb);
            if (index < views.size() - 1) {
                sb.append(",");
            }
            index ++;
        }
        sb.append("}");

        return sb.toString();

    }

    private void appendDescriptor(View view, StringBuilder sb) {
        sb.append("{");
        sb.append("\"title\":").append("\"").append(view.title()).append("\",");
        sb.append("\"stylesheets\": ").append("[").append(mapArrayToString(view.stylesheets())).append("],");
        sb.append("\"scripts\": ").append("[").append(mapArrayToString(view.scripts())).append("],");
        sb.append("\"entrypoint\":").append("\"").append(view.entryPoint()).append("\"");
        sb.append("}");
    }

    private String mapArrayToString(String[] list) {
        return Stream.of(list).map(e -> "\"" + e + "\"").collect(Collectors.joining(","));
    }
}
