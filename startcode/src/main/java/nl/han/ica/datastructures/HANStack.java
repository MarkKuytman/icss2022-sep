package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
    private List<T> stack_list;

    public HANStack() {
        stack_list = new ArrayList<>();
    }

    public HANStack(List<T> stack_list) {
        this.stack_list = stack_list;
    }

    @Override
    public void push(T value) {
        List<T> newStack = new ArrayList<T>();
        newStack.add(value);
        newStack.addAll(stack_list);
        stack_list = newStack;
    }

    @Override
    public T pop() {
        return stack_list.remove(0);
    }

    @Override
    public T peek() {
        if (stack_list.isEmpty()) return null;
        return stack_list.get(0);
    }
}
