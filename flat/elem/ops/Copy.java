package flat.elem.ops;

import flat.elem.Operator;
import flat.elem.Tile;
import flat.exep.IllFormatException;
import flat.exep.OperationFailedException;
import flat.util.pair;

public final class Copy extends Operator {
    public Copy(Tile t) {
        super(t);
    }

    public pair<Object,Integer> run(pair<Object,Integer>[] in) throws IllFormatException,OperationFailedException {
        if (in.length!=1) {
            throw new OperationFailedException("복사 연산자는 값 1개를 받습니다.");
        }
        if (in[0].second!=0) {
            throw new IllFormatException("복사 연산자는 마커를 사용하지 않습니다.");
        }
        return new pair<Object,Integer>(in[0].first,6);
    }
}
