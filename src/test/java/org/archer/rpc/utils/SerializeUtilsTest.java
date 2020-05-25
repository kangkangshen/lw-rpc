package org.archer.rpc.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import junit.framework.TestCase;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.Serializable;

public class SerializeUtilsTest extends TestCase {

    @SneakyThrows
    public void testSerialize() {
        Multimap<String, Student> stringStudentMultimap = ArrayListMultimap.create();
        stringStudentMultimap.put("xiaoming", new Student("xiaoming", 20));
        stringStudentMultimap = (Multimap<String, Student>) SerializeUtils.toObject(SerializeUtils.serialize(stringStudentMultimap));
        System.out.println(stringStudentMultimap.get("xiaoming"));

        Object[] params = new Object[2];
        params[0] = new Student("xiaoming", 20);
        params[1] = null;
        params = SerializeUtils.toArray(SerializeUtils.serialize(params));
        System.out.println(params[0]);
    }

    public void testSerializeToObject() {
    }

    @Data
    static class Student implements Serializable {
        private String name;
        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }


    }
}