package flat.util;

public final class TypeGetter {
    private TypeGetter(){}
    public static String getType(Object o) {
        if (o==null) return "null";
        return o.getClass().getName();
    }
    public static String getType(Object o, boolean lastonly) {
        if (!lastonly) {
            return getType(o);
        } else {
            String[] temp=getType(o).split("\\.");
            return temp[temp.length-1];
        }
    }
}
