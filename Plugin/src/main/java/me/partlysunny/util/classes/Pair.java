package me.partlysunny.util.classes;

public class Pair<A, B> {

    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A a() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B b() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        return ((Pair<?, ?>) obj).a.equals(a) && ((Pair<?, ?>) obj).b.equals(b);
    }
}
