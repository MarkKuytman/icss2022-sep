package nl.han.ica.datastructures;

public class HANListNode<T> {
    T value;
    HANListNode<T> next;

    public HANListNode(T value) {
        this.value = value;
    }
    public HANListNode(T value, HANListNode<T> next) {
        this.value = value;
        this.next = next;
    }
}
