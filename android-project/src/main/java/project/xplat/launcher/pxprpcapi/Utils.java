package project.xplat.launcher.pxprpcapi;

import java.util.List;

public class Utils {
    public String joinStringList(Iterable<String> s){
        StringBuilder sb=new StringBuilder();
        for(String e : s){
            sb.append(e);
            sb.append(" ");
        }
        return sb.toString();
    }
    public Object elemAt(List<Object> l, int index) {
        return l.get(index);
    }
}
