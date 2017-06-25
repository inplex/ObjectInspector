# ObjectInspector

## General
Inspect any kind of object and record the changes of its fields using reflection

## Example Usage
```java
Point p = new Point(1, 2);
ObjectInspector oi = new ObjectInspector(new AtomicReference<Object>(p));
oi.start();
Thread.sleep(40);
p.x = 1337;
p.y = 3100;
Thread.sleep(40);
System.out.println(Arrays.toString(oi.getChanges().toArray()));
oi.stop();
```
