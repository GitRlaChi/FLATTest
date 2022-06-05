package flat.elem.ops;

import flat.elem.Operator;
import flat.elem.FlatField;
import flat.util.pair;

public final class Output extends Operator {
    public Output(FlatField.Tile t) {
        super(t);
    }

    public pair<Object,Integer> run(pair<Object,Integer>[] in) {
        System.out.print("Output: ");
        for (pair<Object,Integer> i : in) {
            System.out.print(i.first);
        }
        System.out.println();
        return null;
    }
}
