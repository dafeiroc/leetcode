package arraystring;

import java.util.Stack;

public class StackQueue<E> {


    private Stack<E> s1 = new Stack<>();
    private Stack<E> s2 = new Stack<>();


    public void enQueue(E x) {
        s1.push(x);
    }

    public E deQueue() {

        E result;
        System.out.println(s1.size() + " " + s2.size() + " " + s1.capacity() + " "+ s2.capacity());
        int total = s1.size();
        for (int i = total; i > 1; i--) {
            E s = s1.pop();
            s2.push(s);
        }
        if (s1.size() == 1) {
            result = s1.pop();
        } else {
            result = s2.pop();
        }
        for (int i = s2.size(); i > 0; i --) {
            E temp = s2.pop();
            s1.push(temp);
        }
        return result;
    }

    public E BetterDeQueue() {
        System.out.println(s1.size() + " " + s2.size() + " " + s1.capacity() + " "+ s2.capacity());
        if (!s2.empty()) {
            return s2.pop();
        } else {
            int total = s1.size();
            for (int i = total; i > 1; i--) {
                E s = s1.pop();
                s2.push(s);
            }
        }
        return s1.pop();
    }

    public static void main(String args[]) {
        StackQueue<String> sq = new StackQueue<>();
        sq.enQueue("a");

        sq.enQueue("b");

        sq.enQueue("c");
        System.out.println(sq.BetterDeQueue());
        System.out.println(sq.BetterDeQueue());


        sq.enQueue("d");
        System.out.println(sq.BetterDeQueue());
        System.out.println(sq.BetterDeQueue());


    }

}
