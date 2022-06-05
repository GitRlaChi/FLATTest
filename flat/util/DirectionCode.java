package flat.util;

import flat.exep.IllFormatException;

/*
방향 표시자
0 - 위쪽(-y)
1 - 오른쪽(+x)
2 - 아래쪽(+y)
3 - 왼쪽(-x)
*/

public final class DirectionCode {
    private DirectionCode(){}
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;

    public static void check(int[] dir) throws IllFormatException {
        if (dir.length != 2) {
            throw new IllFormatException("방향 배열의 길이는 2여야 합니다");
        }
        if (!(-1 <= dir[0] && dir[0] <= 1) || !(-1 <= dir[1] && dir[1] <= 1)) {
            throw new IllFormatException("방향 배열의 모든 원소의 값은 -1과 1 사이여야 합니다");
        }
        if (dir[0] == 0 && dir[1] == 0) {
            throw new IllFormatException("방향 배열의 모든 원소가 0일 수는 없습니다");
        }
    }
    public static int getCodeByDirection(final int[] dir, boolean opposite) throws IllFormatException {
        DirectionCode.check(dir);
        int[] copydir = dir;
        if (opposite) {
            copydir[0] = -copydir[0];
            copydir[1] = -copydir[1];
        }
        switch (copydir[0]) {
            case -1:
                if (copydir[1] == 0)
                    return UP;
                break;
            case 0:
                switch (copydir[1]) {
                    case -1:
                        return LEFT;
                    case 1:
                        return RIGHT;
                }
                break;
            case 1:
                if (copydir[1] == 0)
                    return DOWN;
                break;
        }
        throw new IllFormatException("대각선 방향은 지원하지 않습니다");
    }
    public static String dirToString(final int[] dir, boolean opposite) {
        try {
            check(dir);
        } catch (IllFormatException e) {return "|";}
        int[] copydir = dir;
        if (opposite) {
            copydir[0] = -copydir[0];
            copydir[1] = -copydir[1];
        }
        switch (copydir[0]) {
            case -1:
            return "^";
            case 0:
            switch (copydir[1]) {
                case -1:
                    return "<";
                case 1:
                    return ">";
            }
            break;
            case 1:
                return "v";
        }
        return "|";
    }
}