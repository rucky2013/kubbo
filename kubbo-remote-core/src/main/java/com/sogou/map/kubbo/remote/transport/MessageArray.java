
package com.sogou.map.kubbo.remote.transport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author liufuliang
 */
public final class MessageArray implements Iterable<Object> {
    private final List<Object> messages = new ArrayList<Object>();

    private MessageArray() {
    }
    
    public void addMessage(Object msg) {
        messages.add(msg);
    }

    public void addMessages(Collection<Object> collection) {
        messages.addAll(collection);
    }
    public Collection<Object> removeMessages() {
        Collection<Object> result = Collections.unmodifiableCollection(messages);
        messages.clear();
        return result;
    }
    public Collection<Object> getMessages() {
        return Collections.unmodifiableCollection(messages);
    }

    public int size() {
        return messages.size();
    }

    public Object get(int index) {
        return messages.get(index);
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }
    
    public static MessageArray create(Collection<Object> collection) {
        MessageArray result = new MessageArray();
        result.addMessages(collection);
        return result;
    }

    public static MessageArray create(Object... args) {
        return create(Arrays.asList(args));
    }

    public static MessageArray create() {
        return new MessageArray();
    }

    @Override
    public Iterator<Object> iterator() {
        return messages.iterator();
    }

}
