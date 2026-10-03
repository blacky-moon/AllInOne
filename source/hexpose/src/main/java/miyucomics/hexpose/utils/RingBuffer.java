package miyucomics.hexpose.utils;

import java.util.ArrayDeque;
import java.util.Collection;

public class RingBuffer<T> {
    private final int capacity;
    private final ArrayDeque<T> buffer;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
    }

    public void add(T item) {
        if (buffer.size() == capacity) {
            buffer.removeFirst();
        }
        buffer.addLast(item);
    }

    public Collection<T> buffer() {
        return buffer;
    }

    public T last() {
        return buffer.peekLast();
    }

    public void clear() {
        buffer.clear();
    }

    public int size() {
        return buffer.size();
    }
}