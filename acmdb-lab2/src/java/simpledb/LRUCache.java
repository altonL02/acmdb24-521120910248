package simpledb;

import java.io.*;

import java.util.concurrent.ConcurrentHashMap;

import java.util.*;

public class LRUCache{
    public Node head, tail;
    private Map<PageId, Node> map;
    
    public LRUCache(int numPages){
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.prev = head;
        map = new ConcurrentHashMap<>(numPages);
    }

    private class Node{
        PageId pid;
        Node prev;
        Node next;
        
        public Node(PageId pid){
            this.pid = pid;
        }
    }

    public void modifyData(PageId pageId) {
        if (map.containsKey(pageId)) {
            Node node = map.get(pageId);
            moveToHead(node);
        } else {
            Node node = new Node(pageId);
            map.put(pageId, node);
            addToHead(node);
        }
    }

    public PageId getEvictPageId() {
        return removeTail().pid;
    }

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        map.remove(node.pid);
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTail() {
        Node res = tail.prev;
        removeNode(res);
        return res;
    }
}
