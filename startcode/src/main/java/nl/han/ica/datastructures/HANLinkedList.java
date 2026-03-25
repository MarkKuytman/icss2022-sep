package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T>{

    private HANListNode<T> head;
    private int size;

    @Override
    public void addFirst(T value) {
        HANListNode<T> node = new HANListNode<>(value);
        node.next = head;
        head = node;
        size++;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) return;
        if (index == 0) {
            addFirst(value);
            return;
        }

        HANListNode<T> node = new HANListNode<>(value);
        HANListNode<T> prev = getNode(index - 1);
        node.next = prev.next;
        prev.next = node;
        size++;
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos > size) return;
        if (pos == 0) removeFirst();

        HANListNode<T> node = getNode(pos);
        HANListNode<T> prev = getNode(pos - 1);
        prev.next = node.next.next;
        size--;
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= size) throw new IndexOutOfBoundsException();
        return getNode(pos).value;
    }

    @Override
    public void removeFirst() {
        head = head.next;
    }

    @Override
    public T getFirst() {
        return head.value;
    }

    @Override
    public int getSize() {
        return size;
    }

    // TODO: look into improving lookup speed above O(n)
    private HANListNode<T> getNode(int index) {
        HANListNode<T> current = head;

        while (current.next != null && index > 0) {
            current = current.next;
            index--;
        }
        return current;
    }
}

