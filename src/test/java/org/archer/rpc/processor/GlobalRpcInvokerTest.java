package org.archer.rpc.processor;

//public class GlobalRpcInvokerTest {
//
//    @SneakyThrows
//    public void testRpcInvoke() {
//
////        Method method = ReflectionUtils.findMethod(Class.forName("org.example.HelloWordService"), "sayHello");
////        System.out.println(method == null);
//
//
//    }
//
//    public void testTestRpcInvoke() {
//
//        String a = "xiaoming";
//        Integer b = 20;
//        Object[] args = {a, b};
//        List<Pair<String, String>> polish = polishParams(args);
//        String json = JSON.toJSONString(polish);
//        System.out.println(json);
//        args = reSerializeParams(polish);
//        System.out.println(Arrays.toString(args));
//    }
//
//
//    @SneakyThrows
//    private Object[] reSerializeParams(List<Pair<String, String>> paramPairs) {
//        if (Objects.isNull(paramPairs)) {
//            return null;
//        }
//        Object[] args = new Object[paramPairs.size()];
//        for (int i = 0; i < paramPairs.size(); i++) {
//            args[i] = JSON.parseObject(paramPairs.get(i).getValue(), Class.forName(paramPairs.get(i).getKey()));
//        }
//        return args;
//    }
//
//    private List<Pair<String, String>> polishParams(Object[] params) {
//        if (Objects.isNull(params)) {
//            return null;
//        }
//        List<Pair<String, String>> paramPairs = Lists.newArrayList();
//        for (Object param : params) {
//            paramPairs.add(Pair.of(param.getClass().getName(), JSON.toJSONString(param)));
//        }
//        return paramPairs;
//    }
//
//
//    @Test
//    public void testMultiMap() {
//        ParserConfig.getGlobalInstance().addAccept("org.archer.rpc.utils.ArrayListMultiMap,org.archer.rpc.processor.GlobalRpcInvokerTest$Student,org.archer.rpc.utils.ArrayListMultiMap");
//        Multimap<String, Student> stringStudentMultimap = ArrayListMultiMap.create();
//        stringStudentMultimap.put("xiaoming", new Student("xiaoming", 20));
//        String json = JSON.toJSONString(stringStudentMultimap, SerializerFeature.WriteClassName);
//        System.out.println(json);
//        stringStudentMultimap = (Multimap<String, Student>) JSON.parseObject(json);
//        System.out.println(stringStudentMultimap.get("xiaoming"));
//
//    }
//
//    @Test
//    public void testNewMultiMap() {
//
//    }
//
//    @Data
//    static class Student {
//        private String name;
//        private int age;
//
//        public Student(String name, int age) {
//            this.name = name;
//            this.age = age;
//        }
//    }
//}