package flat.elem;

import flat.exep.IllFormatException;
import flat.exep.OutOfRangeException;
import flat.util.DirectionCode;
import flat.util.TypeGetter;

public final class Arrow {
    private class Value {
        public final String typecolor;
        private Object value;

        Value(String tc, Object v) {
            typecolor = tc;
            value = v;
        }

        public void setValue(Object newv) throws IllFormatException {
            if (newv == null) {
                value=null;
                return;
            }
            if (typecolor.equals("black")) {
                if (TypeGetter.getType(newv).equals("java.lang.Double")) {
                    value = newv;
                } else {
                    throw new IllFormatException("검정 화살표는 부동소수점 값을 전달합니다");
                }
            } else if (typecolor.equals("red")) {
                if (TypeGetter.getType(newv) == "java.lang.Boolean") {
                    value = newv;
                } else {
                    throw new IllFormatException("빨강 화살표는 논리값을 전달합니다");
                }
            } else if (typecolor.equals("green")) {
                value = newv;
            } else {
                throw new IllFormatException("이 색깔은 구현되지 않았습니다");
            }
        }
    }

    public final String typecolor;
    public final int[] direction;
    public final int[] from;
    public final int didx;
    public final boolean end;
    private Value v;
    private final FlatField.Tile parent;

    public Arrow(String tc, int[] from, int[] dir, int didx, boolean end, FlatField.Tile t)
            throws IllFormatException {
        typecolor = tc;
        v = new Value(tc, null);
        DirectionCode.check(from);
        DirectionCode.check(dir);
        this.from=from;
        direction = dir;
        this.end = end;
        this.didx=didx;
        parent=t;
    }

    private void setValue(Object newValue) throws IllFormatException {
        v.setValue(newValue);
    }

    public Object getValue() {
        return v.value;
    }

    public boolean checkDirection(int dy, int dx) {
        return direction[0] == dy && direction[1] == dx;
    }

    public boolean checkFrom(int dy, int dx) {
        return from[0] == dy && from[1] == dx;
    }
    
    public boolean update(Object newValue) {
        try {
            setValue(newValue);
        } catch (IllFormatException e) {
            System.err.printf("%s Arrow[%d,%d]: %s",typecolor,parent.getCord().first,parent.getCord().second,e.getMessage());
            return false;
        }
        if (end) {
            // update nearby operator
            System.out.println();
            Operator dest;
            try {
                dest = parent.adjescent(direction[0], direction[1]).getOperator();
                if (dest==null) return false;
                return dest.update();
            } catch (OutOfRangeException e) {
                return false;
            }
        }
        System.out.print(DirectionCode.dirToString(direction,false));
        Arrow dest;
        try {
            dest = parent.adjescent(direction[0], direction[1]).getArrow(didx);
        } catch (OutOfRangeException e) {
            return false;
        }
        if (dest == null)
            return false;
        if (!dest.checkFrom(-direction[0],-direction[1]))
            return false;
        return dest.update(newValue);
    }
}