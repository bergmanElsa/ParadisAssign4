package com.company;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable  {
    private static final long serialVersionUID = 8673264195747942595L;
    private volatile transient Object[] array;
    transient final ReentrantLock lock = new ReentrantLock();

    public CopyOnWriteArrayList(Collection<? extends E> c) {
        Object[] elements = c.toArray();
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elements.getClass() != Object[].class)
            elements = Arrays.copyOf(elements, elements.length, Object[].class);
        setArray(elements);
    }

    public CopyOnWriteArrayList(E[] toCopyIn) {
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }


    final Object[] getArray() {
        return array;
    }

    final void setArray(Object[] a) {
        array = a;
    }
    private static boolean eq(Object o1, Object o2) {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }

    @Override
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (len != 0) {
                // Copy while searching for element to remove
                // This wins in the normal case of element being present
                int newlen = len - 1;
                Object[] newElements = new Object[newlen];

                for (int i = 0; i < newlen; ++i) {
                    if (eq(o, elements[i])) {
                        // found one;  copy remaining and exit
                        for (int k = i + 1; k < len; ++k)
                            newElements[k-1] = elements[k];
                        setArray(newElements);
                        return true;
                    } else
                        newElements[i] = elements[i];
                }

                // special handling for last cell
                if (eq(o, elements[newlen])) {
                    setArray(newElements);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int i) {
        return null;
    }

    @Override
    public E set(int i, E e) {
        return null;
    }

    @Override
    public void add(int i, E e) {

    }

    @Override
    public E remove(int i) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return null;
    }

    @Override
    public List<E> subList(int i, int i1) {
        return null;
    }
}
