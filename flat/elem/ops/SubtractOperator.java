package flat.elem.ops;

import flat.elem.Operator;
import flat.elem.Tile;
import flat.exep.IllFormatException;
import flat.exep.OperationFailedException;
import flat.util.TypeGetter;
import flat.util.pair;

public final class SubtractOperator extends Operator {
    public SubtractOperator(int m1, Tile t) throws IllFormatException {
        super(m1,t);
    }

    public pair<Object,Integer> run(pair<Object,Integer>[] in) throws IllFormatException, OperationFailedException {
        double base=0;
        double sum=0;
        boolean markerflag=false;
        boolean nullflag=false;
        if (in.length<=1) {
            throw new IllFormatException("입력이 없거나 부족합니다.");
        }
        for (pair<Object,Integer> i : in) {
            if (i.first==null) {
                nullflag=true;
                continue;
            }
            if (!TypeGetter.getType(i.first).equals("java.lang.Double")) {
                throw new IllFormatException("뺄셈 연산자는 검정 화살표만을 입력받아야 합니다.");
            }
            if (i.second!=0) {
                if (markerflag) {
                    throw new IllFormatException("뺄셈 연산자는 마커를 첫째 마커 1개만 사용합니다.");
                }
                markerflag=true;
                base=(double)i.first;
                continue;
            }
            sum+=(double)i.first;
        }
        if (!markerflag) throw new IllFormatException("마커로 값이 입력되지 않았습니다.");
        if (nullflag) throw new OperationFailedException("입력값 중 '미정'이 있습니다");
        return new pair<Object,Integer>(base-sum,1);
    }
}
