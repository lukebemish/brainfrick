package brainfrick.test;

import java.util.function.Function;

public class TestEverything {
    public static void main(String[] args) {
        TestAdd testAdd = new TestAdd();
        System.out.println(testAdd.add(3,5));
        System.out.println(testAdd.add2(3));
        Function testExtend = new TestExtend();
        System.out.println(testExtend.apply(5));
        Function testExtend2 = new TestExtend2(20);
        System.out.println(testExtend2.apply(5));
        TestMain.main(new String[]{});
        System.out.println(TestHelloWorld.helloWorld());
    }
}