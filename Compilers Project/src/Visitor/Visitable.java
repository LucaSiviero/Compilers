package Visitor;

public interface Visitable {

    public <T> T accept(VisitorInterface<T> v);
}
