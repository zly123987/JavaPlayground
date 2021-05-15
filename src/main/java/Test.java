public class Test {
    public static void main(String[] args){
            Parent a = new Parent();
            Child b = new Child();
            a.m1();
            b.m1();
    }
    public static String test(long i){
        String a = "1";
        if (a!= null)
            return a;
        return a;

    }

}

class Parent {
    // Static method in base class
    // which will be hidden in subclass
    public void m1()
    {
        System.out.println("From parent "
                + "static m1()");
    }

    // Non-static method which will
    // be overridden in derived class
    void m2()
    {
        System.out.println("From parent "
                + "non-static(instance) m2()");
    }
}

class Child extends Parent {
    // This method hides m1() in Parent
    public void m1()
    {
        System.out.println("From child static m1()");
    }

    // This method overrides m2() in Parent
    @Override
    public void m2()
    {
        System.out.println("From child "
                + "non-static(instance) m2()");
    }
}