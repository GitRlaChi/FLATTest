package flat.elem;

import flat.exep.IllFormatException;
import flat.exep.OperationFailedException;
import flat.exep.OutOfRangeException;
import flat.util.TypeGetter;
import flat.util.pair;

public abstract class Operator {
    protected static int[] dx = { 0, 1, 0, -1 };
    protected static int[] dy = { -1, 0, 1, 0 };
    protected final int[] markers;
    protected final Tile parent;

    public Operator(Tile t) {
        parent=t;
        markers = new int[] { -1, -1 };
    }

    public Operator(int m1, Tile t) throws IllFormatException {
        parent=t;
        if (!(0 <= m1 && m1 <= 3)) {
            throw new IllFormatException("마커의 방향 표시자는 0~3의 값을 취해야 합니다");
        }
        markers = new int[] { m1, -1 };
    }

    public Operator(int m1, int m2, Tile t) throws IllFormatException {
        parent=t;
        if (!(0 <= m1 && m1 <= 3) || !(0 <= m2 && m2 <= 3)) {
            throw new IllFormatException("마커의 방향 표시자는 0~3의 값을 취해야 합니다");
        }
        if (m1 == m2) {
            throw new IllFormatException("서로 다른 마커가 같은 위치에 있을 수 없습니다");
        }
        markers = new int[] { m1, m2 };
    }
    public final String getName() {
        return TypeGetter.getType(this,true);
    }
    @SuppressWarnings("unchecked")
    public final boolean update() {
        pair<Object,Integer>[] temp=new pair[16];
        boolean[] markerflag={false,false};
        int arrowcnt=0;
        Arrow dest;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                try {
                    dest = parent.adjescent(dy[i], dx[i]).getArrow(j);
                    if (dest == null)
                        continue;
                    if (!dest.checkDirection(-dy[i], -dx[i]))
                        continue; // 출력&지나가는 화살표 거르기
                    temp[arrowcnt++]=new pair<Object,Integer>(dest.getValue(),(markers[0]==i)?1:((markers[1]==i)?2:0));
                    if (markers[0]==i) {
                        if (markerflag[0]) {
                            System.err.printf("%s: 마커가 표시된 면에서는 화살표 1개만 입력될 수 있습니다%n연산자가 연쇄반응을 종료했습니다%n",getName());
                            return false;
                        }
                        markerflag[0]=true;
                    } else if (markers[1]==i) {
                        if (markerflag[1]) {
                            System.err.printf("%s: 마커가 표시된 면에서는 화살표 1개만 입력될 수 있습니다%n연산자가 연쇄반응을 종료했습니다%n",getName());
                            return false;
                        }
                        markerflag[1]=true;
                    }
                } catch (OutOfRangeException e) {}
            }
        }
        pair<Object,Integer>[] inputlist=new pair[arrowcnt];
        for (int i=0; i<arrowcnt; i++) {
            inputlist[i]=temp[i];
        }
        pair<Object,Integer> output=null;
        try {
            output = run(inputlist);
        } catch (IllFormatException | OperationFailedException e) {
            System.err.printf("%s: %s%n연산자가 연쇄반응을 종료했습니다%n",getName(),e.getMessage());
            return false;
        }
        System.out.println(getName()+" 연산자가 값을 반환했습니다");
        System.out.print("\t입력: ");
        for (int i=0; i<inputlist.length; i++) {
            System.out.printf("%s[%d] ",inputlist[i].first,inputlist[i].second);
        }
        System.out.println();
        System.out.print("\t출력: ");
        if (output==null) {
            System.out.println("없음(연쇄반응 종료)");
            return false; // null을 반환하면 연쇄반응을 멈출 수 있다
        }
        System.out.printf("%s[허용 사용값: %d]%n",output.first,output.second);
        arrowcnt=0;
        boolean flags[][]=new boolean[4][4];
        boolean success=true;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                try {
                    dest = parent.adjescent(dy[i], dx[i]).getArrow(j);
                    if (dest == null)
                        continue;
                    if (!dest.checkFrom(-dy[i], -dx[i]))
                        continue; // 입력&지나가는 화살표 거르기
                    flags[i][j]=true;
                    arrowcnt++;
                } catch (OutOfRangeException e) {}
            }
        }
        if (arrowcnt>output.second) {
            System.err.printf("%s: 허용된 값보다 출력 화살표가 더 많습니다.%n",getName());
            return false;
        }
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                if (flags[i][j]) {
                    try {
                        dest = parent.adjescent(dy[i], dx[i]).getArrow(j);
                        if (!dest.update(output.first)) { // dest.update 실행됨
                            success=false;
                        }
                    } catch (OutOfRangeException e) {success=false;}
                }
            }
        }
        return success;
    }

    public abstract pair<Object,Integer> run(pair<Object,Integer>[] in) throws IllFormatException,OperationFailedException ;
    // run : execute the operation with input values
    //input pair<Object,Integer> : first-화살표의 값, second-마커(0:없음,1:첫째,2:둘째)
    //output pair<Object,Integer> : first-출력값, second-허용 사용값
}