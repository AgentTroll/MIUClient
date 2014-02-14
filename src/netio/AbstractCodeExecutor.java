package netio;

public abstract class AbstractCodeExecutor<R, T> extends Lockable {
    abstract R run(T t);
}
