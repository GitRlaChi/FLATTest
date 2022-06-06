package flat.elem.ops;

import flat.elem.Operator;
import flat.elem.Tile;
import flat.exep.IllFormatException;
import flat.exep.OperationFailedException;
import flat.util.TypeGetter;
import flat.util.pair;

public final class AddOperator extends Operator {
    public AddOperator(Tile t) {
        super(t);
    }

    public pair<Object,Integer> run(pair<Object,Integer>[] in) throws IllFormatException, OperationFailedException {
        double sum=0;
        boolean nullflag=false;
        if (in.length==0) {
            throw new IllFormatException("입력이 없습니다.");
        }
        for (pair<Object,Integer> i : in) {
            if (i.first==null) {
                nullflag=true;
                continue;
            }
            if (!TypeGetter.getType(i.first).equals("java.lang.Double")) {
                throw new IllFormatException("덧셈 연산자는 검정 화살표만을 입력받아야 합니다.");
            }
            if (i.second!=0) {
                throw new IllFormatException("덧셈 연산자는 마커를 사용하지 않습니다.");
            }
            sum+=(double)i.first;
        }
        if (nullflag) throw new OperationFailedException("입력값 중 '미정'이 있습니다");
        return new pair<Object,Integer>(sum,1);
    }
}
