package com.obtuse.ui;

/**
* Created by IntelliJ IDEA.
* User: danny
* Date: 2011/12/25
* Time: 10:34
* To change this template use File | Settings | File Templates.
*/

public enum MpsKnobSize {

    SIZE_5x5 {
        public int integerSize() {

            return 5;
        }
    },
    SIZE_7x7 {
        public int integerSize() {

            return 7;
        }
    },
    SIZE_9x9 {
        public int integerSize() {

            return 9;
        }
    },
    SIZE_11x11 {
        public int integerSize() {

            return 11;
        }
    },
    SIZE_13x13 {
        public int integerSize() {

            return 13;
        }
    },
    SIZE_15x15 {
        public int integerSize() {

            return 15;
        }
    },
    SIZE_17x17 {
        public int integerSize() {

            return 17;
        }
    };

    public abstract int integerSize();

}
