package flat.main;

import flat.elem.FlatField;
import flat.exep.FlatException;
import flat.util.DirectionCode;
import flat.util.TypeGetter;

import flat.elem.ops.*;

public class Main {
    private static int[] get(int... a) {
        return a;
    }
    public static void main(String[] args) {
        FlatField f = new FlatField(8,8);
        try {
            f.addArrowStream("black",get(-1,0),get(1,0),get(0,0),get(0,1),get(1,1),get(2,1),get(2,0));
            f.addArrowStream("black",get(-1,0),get(-1,0),get(4,1),get(4,0));
            f.addOperator(3,0,AddOperator.class);
            f.addArrowStream("black",get(0,-1),get(0,1),get(3,1));
            f.addOperator(3,2,SubtractOperator.class,DirectionCode.UP);
            f.addArrowStream("black",get(-1,0),get(1,0),get(0,2),get(1,2),get(2,2));
            f.addArrowStream("black",get(0,-1),get(1,0),get(3,3),get(3,4),get(4,4));
            f.addOperator(5,4,Copy.class);
            f.addArrowStream("black",get(0,1),get(0,-1),get(5,3));
            f.addOperator(5,2,Output.class);
            f.addArrowStream("black",get(-1,0),get(1,0),get(6,4));
            f.addOperator(7,4,Output.class);

            f.compile();

            f.update(0,0,0,9.0);
            f.update(4,1,0,3.0);
            f.update(0,2,0,7.0);

            for (int i=0; i<4; i++) {
                f.printState(i,6);
                System.out.println();
                System.out.println("----------------------------");
                System.out.println();
            }
        } catch (FlatException e) {
            System.err.println("MAIN: "+TypeGetter.getType(e,true)+"-"+e.getMessage());
            e.printStackTrace();
        }
    }
}