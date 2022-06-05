package flat.elem;

import flat.exep.BadTimingException;
import flat.exep.IllFormatException;
import flat.exep.OperationFailedException;
import flat.exep.OutOfRangeException;
import flat.util.DirectionCode;
import flat.util.TypeGetter;
import flat.util.pair;

public final class FlatField {
    public final int height;
    public final int length;
    private boolean initing=true;

    public class Tile {
        private static final int IDX_AUTO=48; // 사십팔
        private static final int IDX_OPERATOR=511;
        private Arrow[] a=new Arrow[4];
        private Operator o;
        private boolean exists; // false - 화살표/연산자 없음
        private boolean isArrow; // true - 화살표, false - 연산자
        private String[] type={"none","none","none","none"};
        private final FlatField parent;
        private final pair<Integer, Integer> cord;

        Tile(FlatField f, pair<Integer, Integer> cord) {
            exists = false;
            parent = f;
            this.cord = cord;
        }

        private int available() {
            if (exists&&!isArrow) return -1;
            if (!(exists&&isArrow)) return 0;
            for (int i=0; i<3; i++) {
                if (type[i].equals("none")) return i;
            }
            return -1;
        }

        private static void _acheck(int idx) throws OutOfRangeException {
            if (!(0<=idx&&idx<=3)) {
                throw new OutOfRangeException("화살표 지시 숫자가 잘못되었습니다");
            }
        }
        private boolean addArrow(int idx,String tc, int[] from, int[] dir, int didx, boolean end) throws OutOfRangeException {
            if (end&&didx==IDX_OPERATOR) didx=0;
            if (idx==IDX_AUTO) idx=available();
            _acheck(idx);
            try {
                a[idx] = new Arrow(tc, from, dir, didx, end, this);
                type[idx]=tc;
            } catch (IllFormatException e) {
                return false;
            }
            exists = true;
            isArrow = true;
            return true;
        }

        private boolean removeArrow(int idx) throws OutOfRangeException {
            _acheck(idx);
            if (isArrow()) {
                exists = false;
                type[idx]="none";
                a[idx] = null;
                return true;
            }
            return false;
        }
        private boolean _makeop(Class<? extends Operator> c, Object[] param, Class<?>... type) throws IllFormatException {
            try {
                o = c.getConstructor(type).newInstance(param);
            } catch (NoSuchMethodException e) {
                throw new IllFormatException("마커 정보의 개수가 틀렸습니다.");
            } catch (InstantiationException | IllegalAccessException
                    | java.lang.reflect.InvocationTargetException e) {
                return false;
            }
            this.type[0] = TypeGetter.getType(o);
            for (int i=1; i<4; i++) {
                this.type[i]="none";
            }
            exists = true;
            isArrow = false;
            return true;
        }
        private boolean addOperator(Class<? extends Operator> c) throws IllFormatException {
            return _makeop(c,new Object[]{this},FlatField.Tile.class);
        }

        private boolean addOperator(Class<? extends Operator> c, int m1) throws IllFormatException {
            return _makeop(c,new Object[]{m1,this},int.class,FlatField.Tile.class);
        }

        private boolean addOperator(Class<? extends Operator> c, int m1, int m2) throws IllFormatException {
            return _makeop(c,new Object[]{m1,m2,this},int.class,int.class,FlatField.Tile.class);
        }

        private boolean removeOperator() {
            if (exists&&!isArrow) {
                exists = false;
                type[0] = "none";
                a = null;
                return true;
            }
            return false;
        }

        private boolean removeAll() {
            exists=false;
            for (int i=0; i<4; i++) {
                type[i]="none";
                a[i]=null;
            }
            o=null;
            return true;
        }

        private boolean isArrow() {
            return exists && isArrow;
        }

        private boolean isOperator() {
            return exists && !isArrow;
        }

        public Arrow getArrow(int idx) throws OutOfRangeException {
            _acheck(idx);
            if (exists&&isArrow)
                return a[idx];
            return null;
        }

        public Operator getOperator() {
            if (exists&&!isArrow)
                return o;
            return null;
        }
        
        public Tile adjescent(int dy, int dx) throws OutOfRangeException {
            return parent.index(cord.first+dy,cord.second+dx);
        }

        public pair<Integer,Integer> getCord() {
            return cord;
        }

        String getType(int idx) throws OutOfRangeException {
            _acheck(idx);
            if (exists&&isArrow) {
                return type[idx];
            } else {
                return "none";
            }
        }

        String getType() {
            if (exists&&!isArrow) {
                return type[0];
            } else {
                return "none";
            }
        }
    }

    private Tile[][] field;

    public FlatField(int height, int length) {
        this.height = height;
        this.length = length;
        field = new Tile[height][length];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < length; j++) {
                field[i][j] = new Tile(this, new pair<Integer, Integer>(i, j));
            }
        }
    }

    private Tile index(int y, int x) throws OutOfRangeException {
        if (!(0 <= y && y < height) || !(0 <= x && x < length)) {
            throw new OutOfRangeException(
                    "필드의 크기는 [" + height + "," + length + "]이나 인덱스 [" + y + "," + x + "]이 입력되었습니다");
        }
        return field[y][x];
    }

    public void addArrowStream(String tc, int[] initfrom, int[] enddest, int[]... cord) throws IllFormatException, OutOfRangeException, OperationFailedException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        if (cord.length==0) return;
        DirectionCode.check(initfrom);
        DirectionCode.check(enddest);
        int py=cord[0][0]+initfrom[0],px=cord[0][1]+initfrom[1];
        int y=0,x=0;
        for (int i=0; i<cord.length-1; i++) {
            if (cord[i].length!=2) {
                throw new IllFormatException("모든 좌표는 길이가 2인 배열이여야 합니다");
            }
            y=cord[i][0];
            x=cord[i][1];
            try {
                index(y,x).addArrow(Tile.IDX_AUTO,tc,new int[]{py-y,px-x},new int[]{cord[i+1][0]-y,cord[i+1][1]-x}, index(cord[i+1][0],cord[i+1][1]).available(),false);
            } catch (OutOfRangeException e) {
                if (e.getMessage().equals("화살표 지시 숫자가 잘못되었습니다")) {
                    throw new OperationFailedException("화살표가 들어갈 공간이 없습니다");
                } else throw e;
            }
            py=cord[i][0];px=cord[i][1];
        }
        index(cord[cord.length-1][0],cord[cord.length-1][1]).addArrow(Tile.IDX_AUTO,tc,new int[]{py-cord[cord.length-1][0],px-cord[cord.length-1][1]},enddest,Tile.IDX_OPERATOR,true);
    }

    public void addOperator(int y, int x, Class<? extends Operator> c) throws IllFormatException, OutOfRangeException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        index(y,x).addOperator(c);
    }
    public void addOperator(int y, int x, Class<? extends Operator> c,int m1) throws IllFormatException, OutOfRangeException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        index(y,x).addOperator(c,m1);
    }
    public void addOperator(int y, int x, Class<? extends Operator> c,int m1,int m2) throws IllFormatException, OutOfRangeException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        index(y,x).addOperator(c,m1,m2);
    }
    public void removeOperator(int[] cord) throws OutOfRangeException, OperationFailedException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        if (!index(cord[0],cord[1]).isOperator()) {
            throw new OperationFailedException("연산자가 이 자리에 없습니다");
        }
        index(cord[0],cord[1]).removeOperator();
    }
    public void removeArrowStream(int inity, int initx, int initidx) throws OutOfRangeException, OperationFailedException, BadTimingException {
        if (!initing) throw new BadTimingException("초기화가 이미 끝났습니다");
        if (!index(inity,initx).isArrow()) {
            throw new OperationFailedException("화살표가 이 자리에 없습니다");
        }
        int[] dir;
        int y=inity, x=initx;
        int temp=initidx;
        int idx=temp; // temp가 사용되지 않음 경고가 떠서 불편해서 이렇게 함
        while (true) {
            if (!index(inity,initx).isArrow()) break;
            dir=index(inity,initx).getArrow(idx).direction;
            temp=index(inity,initx).getArrow(idx).didx;
            index(y,x).removeArrow(idx);
            temp=idx;
            y+=dir[0];
            x+=dir[1];
        }
    }
    public boolean update(int y, int x, int idx, Object newValue) throws OutOfRangeException, BadTimingException {
        if (initing) throw new BadTimingException("아직 초기화 중입니다");
        if (!index(y,x).isArrow()) return false;
        return index(y,x).getArrow(idx).update(newValue);
    }
    public void compile() throws OperationFailedException {
        // TODO: compile the field
        initing=false;
    }
    public void clear() {
        for (int i=0; i<height; i++) {
            for (int j=0; j<length; j++) {
                field[i][j].removeAll();
            }
        }
        initing=true;
    }
    public void printState(int idx, int clen) throws OutOfRangeException {
        Tile._acheck(idx);
        if (clen<0||clen>25) return;
        String padding="                              ";
        for (int i=0; i<height; i++) {
            for (int j=0; j<length; j++) {
                if (field[i][j].isArrow()) {
                    if (field[i][j].getArrow(idx)==null) {
                        System.out.print(padding.substring(0,clen));
                        System.out.print(' ');
                    } else {
                        String prefix=String.valueOf(field[i][j].getArrow(idx).didx);
                        prefix+=DirectionCode.dirToString(field[i][j].getArrow(idx).direction, false);
                        if (field[i][j].getArrow(idx).getValue()==null) {
                            if (field[i][j].getArrow(idx).end) {
                                System.out.print((" "+prefix+"!"+padding).substring(0,clen)+" ");
                            } else {
                                System.out.print((" "+prefix+"x"+padding).substring(0,clen)+" ");
                            }
                        } else {
                            System.out.printf("%s ",(" "+prefix+field[i][j].getArrow(idx).getValue().toString()+padding).substring(0,clen));
                        }
                    }
                } else if (field[i][j].isOperator()) {
                    System.out.print(("  "+field[i][j].getOperator().getName().charAt(0)+padding).substring(0,clen));
                    System.out.print(' ');
                } else {
                    System.out.print(padding.substring(0,clen));
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }
}
