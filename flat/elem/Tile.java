package flat.elem;

import flat.exep.OutOfRangeException;
import flat.util.pair;

public interface Tile {
    static final int IDX_AUTO=48; // 사십팔
    static final int IDX_OPERATOR=49;
    Tile adjescent(int dx, int dy) throws OutOfRangeException;
    pair<Integer,Integer> getCord();
    String getType();
    String getType(int i) throws OutOfRangeException;
    Arrow getArrow(int i) throws OutOfRangeException;
    Operator getOperator();
}
