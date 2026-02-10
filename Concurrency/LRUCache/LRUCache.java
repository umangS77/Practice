package Concurrency.LRUCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache<K,V> {

    private class Node {
        K key;
        V value;
        Node prev, next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }

    private int capacity;
    private Map<K,Node> map;

    private Node head;
    private Node tail;

    private ReentrantLock lock = new ReentrantLock();

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    public V get(K key) {
        lock.lock();
        try {
            Node node = map.get(key);
            if (node == null) {
                return null;
            }
            moveToHead(node);
            return node.getValue();
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try{
            Node node = map.get(key);
            if (node == null) {
                node = new Node(key, value);
            } else {
                node.setValue(value);
            }
            addToHead(node);
            if (map.size() > capacity) {
                Node removedNode = trimTail();
                map.remove(removedNode.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    public void printCache() {
        Node current = head;
        while(current != null) {
            System.out.println("K = " + current.getKey() + " | V = " + current.getValue());
            current = current.getNext();
        }
    }

    private void addToHead(Node node) {
        node.setNext(head);
        node.setPrev(null);

        if (head != null) {
            head.setPrev(node);
        }
        head=node;

        if(tail == null) {
            tail = node;
        }
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }

    private void moveToHead(Node node) {
        if (node != head) {
            removeNode(node);
            addToHead(node);
        }
    }

    private Node trimTail() {
        if (tail == null) {
            return null;
        }
        Node trimmedNode = tail;
        removeNode(trimmedNode);
        return trimmedNode;
    }

}
